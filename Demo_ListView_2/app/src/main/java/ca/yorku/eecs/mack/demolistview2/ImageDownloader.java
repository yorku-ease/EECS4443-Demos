package ca.yorku.eecs.mack.demolistview2;

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
 * This helper class downloads images and binds them with the provided ImageView.
 *
 * A local cache of downloaded images is maintained internally to improve performance.
 */
class ImageDownloader
{		
    /**
     * Download the specified image from the path (directory + filename) and binds it to the provided ImageView. The
     * binding is immediate if the image is found in the cache and will be done asynchronously
     * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
     *
     * @param pathArg pointer to the image to download.
     * @param imageView the ImageView to bind the downloaded image to
     * @param widthArg the width of the ImageView (image is down sampled to save memory)
     */
    void download(String pathArg, ImageView imageView, int widthArg)
    {
    	//String path = pathArg;
    	//int width = widthArg;
    	
        resetPurgeTimer();
        Bitmap bitmap = getBitmapFromCache(pathArg);

        if (bitmap == null) 
        {
            forceDownload(pathArg, imageView, widthArg);
        } else 
        {
            cancelPotentialDownload(pathArg, imageView);
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * Same as download but the image is always downloaded and the cache is not used.
     */
    private void forceDownload(String path, ImageView imageView, int width)
    {
        // State sanity: url is guaranteed to never be null in DownloadedDrawable and cache keys.
        if (path == null) 
        {
            imageView.setImageDrawable(null);
            return;
        }

        if (cancelPotentialDownload(path, imageView)) 
        {
            BitmapDownloaderTask task = new BitmapDownloaderTask(imageView, width);
            DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
            imageView.setImageDrawable(downloadedDrawable);
            task.execute(path);
        }
    }

    /**
     * Returns true if the current download has been canceled or if there was no download in
     * progress on this image view.
     * Returns false if the download in progress deals with the same url. The download is not
     * stopped in that case.
     */
    private static boolean cancelPotentialDownload(String pathArg, ImageView imageView)
    {
        BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

        if (bitmapDownloaderTask != null) 
        {
            String bitmapPath = bitmapDownloaderTask.path;
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
     * @return Retrieve the currently active download task (if any) associated with this imageView.
     * null if there is no such task.
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
     * The actual AsyncTask that will asynchronously download the image.
     */
    private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
    {
        private String path;
        private final WeakReference<ImageView> imageViewReference;
        
        private int viewWidth;

        BitmapDownloaderTask(ImageView imageView, int displayWidthArg)
        {
            imageViewReference = new WeakReference<ImageView>(imageView);
            viewWidth = displayWidthArg;
        }

        /**
         * Actual download method.  Image is scaled to fit available view width.
         */
        @Override
        protected Bitmap doInBackground(String... params)
        {
            path = params[0];           
            return decodeFile(new File(path));
        }
        
        private Bitmap decodeFile(File f)
        {
        	// scaling method adapted from...
        	// http://stackoverflow.com/questions/8132296/handle-resolution-of-different-size-images-in-listview
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
                    if(width_tmp < viewWidth)
                        break;
                    width_tmp /= 2;
                    scale *= 2;
                }                
               
                // decode image, scaled to fit display width (allocates memory to store the bitmap)
                BitmapFactory.Options o2 = new BitmapFactory.Options();
                //o2.inSampleSize = scale;
                o2.inSampleSize = scale / 2; // a bit better quality (for zooming in)
                return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
            } catch (FileNotFoundException e)
            {
            	Log.i("MYDEBUG", "FileNotFoundException: e=" + e.toString());
            }
            return null;
        }

        /**
         * Once the image is downloaded, associate it to the imageView
         */
        @Override
        protected void onPostExecute(Bitmap bitmap)
        {
            if (isCancelled()) 
            {
                bitmap = null;
            }

            addBitmapToCache(path, bitmap);

            if (imageViewReference != null) 
            {
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

    /**
     * A fake Drawable that will be attached to the imageView while the download is in progress.
     *
     * <p>Contains a reference to the actual download task, so that a download task can be stopped
     * if a new binding is required, and makes sure that only the last started download process can
     * bind its result, independently of the download finish order.</p>
     */
    static class DownloadedDrawable extends ColorDrawable
    {
        private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

        public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask) 
        {
            super(Color.LTGRAY);
            bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(bitmapDownloaderTask);
        }

        public BitmapDownloaderTask getBitmapDownloaderTask() 
        {
            return bitmapDownloaderTaskReference.get();
        }
    } 
   
    /*
     * Cache-related fields and methods.
     *
     * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
     * Garbage Collector.
     */
   
    private static final int HARD_CACHE_CAPACITY = 10;
    private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

    // Hard cache, with a fixed maximum capacity and a life duration
    private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(HARD_CACHE_CAPACITY / 2, 0.75f, true)
    {        
		private static final long serialVersionUID = 1L; // fix compiler warning

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

    // Soft cache for bitmaps kicked out of hard cache
    private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache =
        new ConcurrentHashMap<String, SoftReference<Bitmap>>(HARD_CACHE_CAPACITY / 2);

    private final Handler purgeHandler = new Handler();

    private final Runnable purger = new Runnable()
    {
        public void run() 
        {
            clearCache();
        }
    };

    //Add the passed bitmap to the cache.
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

    // return the bitmap from the cache specified by the passed path
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
 
    /*
     Clear the image cache that is used internally to improve performance. Note that for memory
     * efficiency reasons, the cache is automatically cleared after a certain inactivity delay.
     */
    public void clearCache() 
    {
        sHardBitmapCache.clear();
        sSoftBitmapCache.clear();
    }


    // Allow a new delay before the automatic cache clear is done.
    private void resetPurgeTimer() 
    {
        purgeHandler.removeCallbacks(purger);
        purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
    }
}
