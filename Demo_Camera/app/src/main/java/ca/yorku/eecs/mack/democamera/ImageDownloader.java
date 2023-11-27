package ca.yorku.eecs.mack.democamera;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This helper class download images from the Internet and binds those with the provided ImageView.
 *
 * <p> It requires the INTERNET permission, which should be added to your application's manifest file. </p>
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
class ImageDownloader
{
    private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new
            ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY /
            2, 0.75f, true)
    {
        private static final long serialVersionUID = 1L;

        @Override
        protected boolean removeEldestEntry(LinkedHashMap.Entry<String, Bitmap> eldest)
        {
            if (size() > HARD_CACHE_CAPACITY)
            {
                // Entries push-out of hard reference cache are transferred to soft reference cache
                sSoftBitmapCache.put(eldest.getKey(), new SoftReference<Bitmap>(eldest.getValue()));
                return true;
            } else
                return false;
        }
    };
    private final Handler purgeHandler = new Handler();
    private final Runnable purger = new Runnable()
    {
        public void run()
        {
            clearCache();
        }
    };
    private int displayWidth;

	/*
     * Cache-related fields and methods.
	 * 
	 * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the Garbage Collector.
	 */

    /**
     * Returns true if the current download has been canceled or if there was no download in progress on this image
     * view. Returns false if the download in progress deals with the same url. The download is not stopped in that
     * case.
     */
    private static boolean cancelPotentialDownload(String pathArg, ImageView imageView)
    {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null)
        {
            String bitmapPath = bitmapDownloaderTask.url;
            if ((bitmapPath == null) || (!bitmapPath.equals(pathArg)))
            {
                bitmapDownloaderTask.cancel(true);
            } else
            {
                // The same URL is already being downloaded.
                return false;
            }
        }
        return true;
    }

    /**
     * @param imageView Any imageView
     * @return Retrieve the currently active download task (if any) associated with this imageView. null if there is no
     * such task.
     */
    private static BitmapDownloaderTask getBitmapDownloaderTask(ImageView imageView)
    {
        if (imageView != null)
        {
            Drawable drawable = imageView.getDrawable();
            if (drawable instanceof DownloadedDrawable)
            {
                DownloadedDrawable downloadedDrawable = (DownloadedDrawable)drawable;
                return downloadedDrawable.getBitmapDownloaderTask();
            }
        }
        return null;
    }

    /**
     * Download the specified image from the path (directory + filename) and bind it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously otherwise. A null bitmap
     * will be associated to the ImageView if an error occurs.
     *
     * @param path      The path of the image to download.
     * @param imageView The ImageView to bind the downloaded image to.
     */
    void download(String path, ImageView imageView, int displayWidthArg)
    {
        displayWidth = displayWidthArg;

        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(path);

        if (bitmap == null)
        {
            forceDownload(path, imageView);
        } else
        {
            cancelPotentialDownload(path, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Same as download but the image is always downloaded and the cache is not used. Kept private at the moment as its
     * interest is not clear.
     */
    private void forceDownload(String path, ImageView imageView)
    {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (path == null)
        {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(path, imageView))
        {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, displayWidth);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            imageView.setMinimumHeight(156);
            task.execute(path);
        }
    }

    /**
     * Adds this bitmap to the cache.
     *
     * @param bitmap The newly downloaded bitmap.
     */
    private void addBitmapToCache(String path, Bitmap bitmap)
    {
        if (bitmap != null)
        {
            synchronized (sHardBitmapCache)
            {
                sHardBitmapCache.put(path, bitmap);
            }
        }
    }

    /**
     * @param path The URL of the image that will be retrieved from the cache.
     * @return The cached bitmap or null if it was not found.
     */
    private Bitmap getBitmapFromCache(String path)
    {
        // First try the hard reference cache
        synchronized (sHardBitmapCache)
        {
            final Bitmap bitmap = sHardBitmapCache.get(path);
            if (bitmap != null)
            {
                // Bitmap found in hard cache
                // Move element to first position, so that it is removed last
                sHardBitmapCache.remove(path);
                sHardBitmapCache.put(path, bitmap);
                return bitmap;
            }
        }

        // Then try the soft reference cache
        SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(path);
        if (bitmapReference != null)
        {
            final Bitmap bitmap = bitmapReference.get();
            if (bitmap != null)
            {
                // Bitmap found in soft cache
                return bitmap;
            } else
            {
                // Soft reference has been Garbage Collected
                sSoftBitmapCache.remove(path);
            }
        }
        return null;
    }

    /**
     * Clears the image cache used internally to improve performance. Note that for memory efficiency reasons, the cache
     * will automatically be cleared after a certain inactivity delay.
     */
    private void clearCache()
    {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }

    // allow a new delay before the automatic cache clear is done
    private void resetPurgeTimer()
    {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }

    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p> Contains a reference to the actual download task, so that a download task can be stopped if a new binding is
     * required, and makes sure that only the last started download process can bind its result, independently of the
     * download finish order. </p>
     */
    private static class DownloadedDrawable extends ColorDrawable
    {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask)
        {
            super(Color.LTGRAY);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        BitmapDownloaderTask getBitmapDownloaderTask()
        {
            return bitmapDownloaderTaskReference.get();
        }
    }

    // the actual AsyncTask that will asynchronously download the image
    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
    {
        private final WeakReference<ImageView> imageViewReference;
        private String url;
        private int displayWidth;

        BitmapDownloaderTask(ImageView imageView, int displayWidthArg)
        {
            imageViewReference = new WeakReference<ImageView>(imageView);
            displayWidth = displayWidthArg;
        }

        // actual download method. Image is scaled to fit available display width
        @Override
        protected Bitmap doInBackground(String... params)
        {
            url = params[0];
            return decodeFile(new File(url));
        }

        private Bitmap decodeFile(File f)
        {
            // scaling method adapted from...
            // http://stackoverflow.com/questions/8132296/handle-resolution-of-different-size-images-in-listview
            //
            // See as well...
            // http://developer.android.com/training/displaying-bitmaps/load-bitmap.html#read-bitmap
            try
            {
                // decode image size (without allocating memory to store the bitmap)
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(new FileInputStream(f), null, o);

                // determine scale value so image will fit in available display width
                int scale = 1;
                int width_tmp = o.outWidth;
                while (true)
                {
                    if (width_tmp < displayWidth)
                        break;
                    width_tmp /= 2;
                    scale *= 2;
                }

                // decode image, scaled to fit display width (allocates memory to store the bitmap)
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                o2.inSampleSize = scale;
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (FileNotFoundException e)
            {
                Log.i(MYDEBUG, "FileNotFoundException: e=" + e.toString());
            }
            return null;
        }

        // once the image is downloaded, associate it to the imageView
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            // Log.i(MYDEBUG, "onPostExecute!");
            if (isCancelled())
            {
                bitmap = null;
            }

            addBitmapToCache(url, bitmap);

            ImageView imageView = imageViewReference.get();
            BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);
            // Change bitmap only if this process is still associated with it
            if ((this == bitmapDownloaderTask))
            {
                imageView.setImageBitmap(bitmap);
            }
        }
    }
}
