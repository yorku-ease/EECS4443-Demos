package ca.yorku.eecs.mack.demointernetdownload;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/*
 * The main purpose of the ImageDownloader class is to host the inner class
 * BitmapDownloader, a subclass of AsyncTask. It is this class's
 * doInBackground method that actually takes care of the download operation. And, of
 * course, it is executing in a separate thread, so the UI thread is not blocked waiting for the
 * download.
 * 	
 * Most of the code here is from the Android Developer's Blog
 * "Multithreading For Performance", which provides additional discussion. See...
 * 
 * http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html
 */

class ImageDownloader
{
	private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	void download(String url, ImageView imageView)
	{
		BitmapDownloaderTask task = new BitmapDownloaderTask(imageView);
		task.execute(url);
	}

	private class BitmapDownloaderTask extends AsyncTask<String, Void, Bitmap>
	{
		private final WeakReference<ImageView> imageViewReference;

		BitmapDownloaderTask(ImageView imageView)
		{
			/*
			 * The ImageView is stored as a WeakReference so that a download in progress does not
			 * prevent a killed activity's ImageView from being garbage collected.
			 */
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		/*
		 * The ellipsis (...) below means that the method can receive a variable number of
		 * arguments. See...
		 * 
		 * http://stackoverflow.com/questions/2367398/what-is-the-ellipsis-for-in-this-method-signature
		 */

		@Override
		// Actual download method, run in a worker thread
		protected Bitmap doInBackground(String... params)
		{
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associate it to the imageView
		protected void onPostExecute(Bitmap bitmap)
		{
			if (isCancelled())
			{
				bitmap = null;
			}

			ImageView imageView = imageViewReference.get();
			if (imageView != null)
			{
				imageView.setImageBitmap(bitmap);
			}
		}
	}

	/*
	 * Download the image referenced by the URL string and return it as a bitmap.
	 */
	private static Bitmap downloadBitmap(String url) {
		OkHttpClient client = new OkHttpClient();
		Request request = new Request.Builder()
				.url(url)
				.build();

		// Note: the execute() method is synchronous and blocks the current thread until the HTTP response is received
		try (Response response = client.newCall(request).execute()) {
			if (!response.isSuccessful()) {
				Log.i(MYDEBUG, "Oops! statusCode=" + response.code() + " while retrieving bitmap from " + url);
				return null;
			}

			assert response.body() != null;
			InputStream inputStream = response.body().byteStream();
			return BitmapFactory.decodeStream(new FlushedInputStream(inputStream));

		} catch (Exception e) {
			Log.i(MYDEBUG, "Oops! Abort while retrieving bitmap from " + url + ", e=" + e);
		}

		return null;
	}

	/*
	 * As noted in the Android Developer's Blog (link below), this class fixes a bug in the
	 * BitmapFactory.decodeStream method (called in downloadBitmap method) when the connection is
	 * slow. The fix is to pass an instance of this class to downloadBitmap. See...
	 * 
	 * http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html
	 */
	private static class FlushedInputStream extends FilterInputStream
	{
		FlushedInputStream(InputStream inputStream)
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
}
