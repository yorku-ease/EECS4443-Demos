package ca.yorku.eecs.mack.demointernetdownload;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Locale;

/**
 * Demo_InternetDownload - demonstrate downloading an image from an Internet web site and displaying the image in a
 * view. <p>
 *
 * Related links: <p>
 *
 * <blockquote> API Guides: <p> <ul> <li><a href="http://developer.android.com/guide/components/processes-and-threads.html">Processes
 * and Threads</a> </ul> <p>
 *
 * API References: <p> <ul> <li><a href="http://developer.android.com/reference/android/net/http/AndroidHttpClient.html">
 * <code>AndroidHttpClient</code></a> <li><a href="http://developer.android.com/reference/org/apache/http/HttpResponse.html">
 * <code>HttpResponse</code></a> <li><a href="http://developer.android.com/reference/org/apache/http/HttpEntity.html">
 * <code>HttpEntity</code></a> <li><a href="http://developer.android.com/reference/org/apache/http/HttpStatus.html">
 * <code>HttpStatus</code></a> <li><a href="http://developer.android.com/reference/android/os/AsyncTask.html">
 * <code>AsyncTask</code></a> <li><a href="http://developer.android.com/reference/java/lang/ref/WeakReference.html">
 * <code>WeakReference</code></a> <li><a href="http://developer.android.com/reference/android/widget/ImageView.html">
 * <code>ImageView</code></a> </ul> <p>
 *
 * Android Developer's Blog: <p> <ul> <li><a href="http://android-developers.blogspot.ca/2009/05/painless-threading.html">Painless
 * Threading</a> (read first) <li><a href="http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html">
 * Multithreading For Performance</a> </ul> <p>
 *
 * Android Developer Training: <p> <ul>
 *     <li><a href="http://developer.android.com/training/displaying-bitmaps/process-bitmap.html">Handling
 * Bitmaps</a> <li><a href="http://developer.android.com/training/articles/perf-anr.html">Keeping Your
 * App Responsive</a> </ul> <p>
 *
 * </blockquote>
 *
 * The initial shell for this program is from the "Multithreading for Performance" Android Developer's Blog (link
 * above). The blog goes considerably further than this demo, however. It demonstrates downloading a series of web
 * images that appear in a scrollable <code>ListView</code>. This is the topic of a subsequent demo, Demo_ListView_3.
 * The goal here is simple: Demonstrate the basic mechanism for reading an image from a URL (an Internet web address).
 * Here, we download a single image and display it in an <code>ImageView</code>. <p>
 *
 * The <code>DemoInternetDownloadActivity</code> is very brief. It includes just one method, <code>onCreate</code>. Most
 * of the work is done in a separate class named <code>ImageDownloader</code>. The main purpose of
 * <code>ImageDownloader</code> is to host an inner class named <code>BitmapDownloader</code>, a subclass of
 * <code>AsyncTask</code>. As stated in the <code>AsyncTask</code> API, <p>
 *
 * <blockquote><i><code>AsyncTask</code> enables proper and easy use of the UI thread. This class allows [an
 * application] to perform background operations and publish results on the UI thread without having to manipulate
 * threads and/or handlers. </i></blockquote> <p>
 *
 * Indeed, <code>AsyncTask</code> hides many of the details. It is built around the <code>Thread</code> and
 * <code>Handler</code> classes, but the details are hidden. <p>
 *
 * The workhorse methods of <code>AsyncTask</code> are <code>doInBackground</code> and <code>onPostExecute</code>. The
 * background thread to download the image is initiated through <code>doInBackground</code>, which receives the URL
 * string as an argument and returns a "result" (defined as a <code>Bitmap</code> in the subclass). The bitmap result is
 * passed to <code>onPostExecute</code> which places it in the <code>ImageView</code> that was provided in the
 * constructor of <code>BitmapDownloader</code>. <p>
 *
 * The actual download occurs through the <code>downloadBitmap</code> method, called from <code>doInBackground</code>.
 * The <code>downloadBitmap</code> method takes care of the input streaming and hypertext transfer protocol (HTTP)
 * needed for communication with the Internet web site hosting the image. <p>
 *
 * The URL of the image is hard-coded in a final: <p>
 *
 * <pre>
 *      final String TEST_URL = "http://www.yorku.ca/mack/nordichi2012-f5.jpg";
 * </pre>
 *
 * The image is download from the Internet web site and presented in an <code>ImageView</code>: <p>
 *
 * <center><a href="DemoInternetDownload-1.jpg"><img src="DemoInternetDownload-1.jpg" width="300"
 * alt="image"></a></center> <p>
 *
 * Since this program is accessing the Internet, the manifest must include the following permission (placed just before
 * the <code>application</code> element): <p>
 *
 * <pre>
 *      &lt;uses-permission android:name="android.permission.INTERNET"/&gt;
 * </pre>
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */

public class DemoInternetDownloadActivity extends Activity
{
    final String TEST_URL = "http://www.yorku.ca/mack/nordichi2012-f5.jpg";

    TextView textView;
    ImageView imageView;
    ImageDownloader imageDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);

        imageDownloader = new ImageDownloader();
        imageDownloader.download(TEST_URL, imageView);

        textView.setText(String.format(Locale.CANADA, "Image from %s",TEST_URL));
    }
}
