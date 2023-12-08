package ca.yorku.eecs.mack.demolistview3;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.concurrent.ConcurrentHashMap;

/*
 * This helper class downloads images from the Internet and binds those with the provided ImageView.
 * 
 * It requires the INTERNET permission, which should be added to your application's manifest file.
 * 
 * A local cache of downloaded images is maintained internally to improve performance.
 */
public class ImageDownloader
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	/*
	 * Download the specified image from the Internet and bind it to the provided ImageView. The
	 * binding is immediate if the image is found in the cache and will be done asynchronously
	 * otherwise. A null bitmap will be associated to the ImageView if an error occurs.
	 * 
	 * Pass the URL of the image to download and the ImageView to bind the downloaded image to.
	 */
	public void download(String url, ImageView imageView)
	{
		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);

		if (bitmap == null) // image is not in the cache (force download)
		{
			Log.i(MYDEBUG, "forceDownload!");
			forceDownload(url, imageView);
		} else
		{
			Log.i(MYDEBUG, "NOT forceDownload!");
			cancelPotentialDownload(url, imageView);
			imageView.setImageBitmap(bitmap);
		}
	}

	/*
	 * Same as download but the image is always downloaded and the cache is not used.
	 */
	private void forceDownload(String url, ImageView imageView)
	{
		// State sanity: URL is guaranteed to never be null in DownloadedDrawable and cache keys.
		if (url == null)
		{
			imageView.setImageDrawable(null);
			return;
		}

		if (cancelPotentialDownload(url, imageView))
		{
			BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
			DownloadedDrawable downloadedDrawable = new DownloadedDrawable(task);
			imageView.setImageDrawable(downloadedDrawable);
			imageView.setMinimumHeight(156);
			task.execute(url);
		}
	}

	/*
	 * Pass the URL and the ImageView.
	 * 
	 * Return true if the current download has been cancelled or if there was no download in
	 * progress on this image view. Return false if the download in progress deals with the same
	 * URL. The download is not stopped in that case.
	 */
	private static boolean cancelPotentialDownload(String url, ImageView imageView)
	{
		BitmapDownloaderTask bitmapDownloaderTask = getBitmapDownloaderTask(imageView);

		if (bitmapDownloaderTask != null)
		{
			String bitmapUrl = bitmapDownloaderTask.url;
			if ((bitmapUrl == null) || (!bitmapUrl.equals(url)))
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

	/*
	 * Pass the ImageView.
	 * 
	 * Return the currently active download task (if any) associated with this imageView. Return
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

	Bitmap downloadBitmap(String url)
	{
		// AndroidHttpClient is not allowed to be used from the main thread
		final HttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);

		try
		{
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK)
			{
				/*
				 * If you see this message, your device is probably *not* connected to the Internet.
				 * Connect and try again.
				 */
				Log.i(MYDEBUG, "Error " + statusCode + " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null)
			{
				InputStream inputStream = null;
				try
				{
					inputStream = entity.getContent();
					// Bug on slow connections, fixed in future release.
					return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));
				} finally
				{
					if (inputStream != null)
					{
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e)
		{
			getRequest.abort();
			Log.i(MYDEBUG, "I/O error while retrieving bitmap from " + url + ", e=" + e.toString());
		} catch (IllegalStateException e)
		{
			getRequest.abort();
			Log.i(MYDEBUG, "Incorrect URL: " + url + ", e=" + e.toString());
		} catch (Exception e)
		{
			getRequest.abort();
			Log.i(MYDEBUG, "Error while retrieving bitmap from " + url + ", e=" + e.toString());
		} finally
		{
			if ((client instanceof AndroidHttpClient))
			{
				((AndroidHttpClient)client).close();
			}
		}
		return null;
	}

	/*
	 * An InputStream that skips the exact number of bytes provided, unless it reaches EOF.
	 */
	static class FlushedInputStream extends FilterInputStream
	{
		public FlushedInputStream(InputStream inputStream)
		{
			super(inputStream);
		}

		@Override
		public long skip(long n) throws IOException
		{
			long totalBytesSkipped = 0L;
			while (totalBytesSkipped < n)
			{
				long bytesSkipped = in.skip(n - totalBytesSkipped);
				if (bytesSkipped == 0L)
				{
					int b = read();
					if (b < 0)
					{
						break; // we reached EOF
					} else
					{
						bytesSkipped = 1; // we read one byte
					}
				}
				totalBytesSkipped += bytesSkipped;
			}
			return totalBytesSkipped;
		}
	}

	/*
	 * The actual AsyncTask that will asynchronously download the image.
	 */
	class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
	{
		private String url;
		private final WeakReference<ImageView> imageViewReference;

		public BitmapDownloaderTask(ImageView imageView)
		{
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/*
		 * Actual download method.
		 */
		@Override
		protected Bitmap doInBackground(String... params)
		{
			Log.i(MYDEBUG, "doInBackground!");
			url = params[0];
			return downloadBitmap(url);
		}

		/*
		 * Once the image is downloaded, associate it to the imageView
		 */
		@Override
		protected void onPostExecute(Bitmap bitmap)
		{
			Log.i(MYDEBUG, "onPostExecute!");
			if (isCancelled())
			{
				bitmap = null;
			}

			addBitmapToCache(url, bitmap);

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

	/*
	 * A fake Drawable that will be attached to the imageView while the download is in progress.
	 * 
	 * Contains a reference to the actual download task, so that a download task can be stopped if a
	 * new binding is required, and makes sure that only the last started download process can bind
	 * its result, independently of the download finish order.
	 */
	static class DownloadedDrawable extends ColorDrawable
	{
		private final WeakReference<BitmapDownloaderTask> bitmapDownloaderTaskReference;

		public DownloadedDrawable(BitmapDownloaderTask bitmapDownloaderTask)
		{
			super(Color.BLACK);
			bitmapDownloaderTaskReference = new WeakReference<BitmapDownloaderTask>(
					bitmapDownloaderTask);
		}

		public BitmapDownloaderTask getBitmapDownloaderTask()
		{
			return bitmapDownloaderTaskReference.get();
		}
	}

	// ================================
	// Cache-related fields and methods
	// ================================

	/*
	 * We use a hard and a soft cache. A soft reference cache is too aggressively cleared by the
	 * Garbage Collector.
	 */

	private static final int HARD_CACHE_CAPACITY = 10;
	private static final int DELAY_BEFORE_PURGE = 10 * 1000; // in milliseconds

	// Hard cache, with a fixed maximum capacity and a life duration
	private final HashMap<String, Bitmap> sHardBitmapCache = new LinkedHashMap<String, Bitmap>(
			HARD_CACHE_CAPACITY / 2, 0.75f, true)
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

	// Soft cache for bitmaps kicked out of hard cache
	private final static ConcurrentHashMap<String, SoftReference<Bitmap>> sSoftBitmapCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			HARD_CACHE_CAPACITY / 2);

	private final Handler purgeHandler = new Handler();

	private final Runnable purger = new Runnable()
	{
		public void run()
		{
			clearCache();
		}
	};

	/*
	 * Adds this bitmap to the cache.
	 */
	private void addBitmapToCache(String url, Bitmap bitmap)
	{
		if (bitmap != null)
		{
			synchronized (sHardBitmapCache)
			{
				sHardBitmapCache.put(url, bitmap);
			}
		}
	}

	/*
	 * Pass the URL of the image that will be retrieved from the cache.
	 * 
	 * Return The cached bitmap or null if it was not found.
	 */
	private Bitmap getBitmapFromCache(String url)
	{
		// First try the hard reference cache
		synchronized (sHardBitmapCache)
		{
			final Bitmap bitmap = sHardBitmapCache.get(url);
			if (bitmap != null)
			{
				// Bitmap found in hard cache
				// Move element to first position, so that it is removed last
				sHardBitmapCache.remove(url);
				sHardBitmapCache.put(url, bitmap);
				return bitmap;
			}
		}

		// Then try the soft reference cache
		SoftReference<Bitmap> bitmapReference = sSoftBitmapCache.get(url);
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
				sSoftBitmapCache.remove(url);
			}
		}
		return null;
	}

	/*
	 * Clears the image cache used internally to improve performance. Note that for memory
	 * efficiency reasons, the cache will automatically be cleared after a certain inactivity delay.
	 */
	public void clearCache()
	{
		sHardBitmapCache.clear();
		sSoftBitmapCache.clear();
	}

	/*
	 * Allow a new delay before the automatic cache clear is done.
	 */
	private void resetPurgeTimer()
	{
		purgeHandler.removeCallbacks(purger);
		purgeHandler.postDelayed(purger, DELAY_BEFORE_PURGE);
	}
}
