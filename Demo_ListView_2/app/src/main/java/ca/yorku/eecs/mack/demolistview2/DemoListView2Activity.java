package ca.yorku.eecs.mack.demolistview2;

import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;

/**
 * Demo_ListVew_2 - demonstrates presenting an array of images in a <code>ListView</code>.  The images are read from
 * files stored in the device's internal memory card.<p>
 *
 * Related links:
 *
 * <blockquote>
 *
 * API Guides: <p> <ul> <li><a href="http://developer.android.com/guide/topics/ui/layout/listview.html">List View</a>
 * <li><a href="http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews">Building Layouts with
 * an Adapter</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul>
 *
 * <li><a href="http://developer.android.com/reference/android/widget/ListView.html"> <code>ListView</code></a>
 *
 * <li><a href="http://developer.android.com/reference/android/app/ListActivity.html"> <code>ListActivity</code></a>
 *
 * <li><a href="http://developer.android.com/reference/android/widget/ListAdapter.html"> <code>ListAdapter</code></a>
 * (interface)
 *
 * <li><a href="http://developer.android.com/reference/android/widget/Adapter.html"> <code>Adapter</code></a>
 * (interface)
 *
 * <li><a href="http://developer.android.com/reference/android/widget/BaseAdapter.html"> <code>BaseAdapter</code></a>
 * (implements ListAdapter)
 *
 * </ul>
 *
 * <p>
 *
 * Android Developer's Blog: <p> <ul> <li><a href="http://android-developers.blogspot.ca/2009/05/painless-threading.html">Painless
 * Threading</a> (read first) <li><a href="http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html">
 * Multithreading For Performance</a> </ul> <p>
 *
 * Android Developer Training: <p> <ul> <li><a href="http://developer.android.com/training/displaying-bitmaps/process-bitmap.html">Processing
 * Bitmaps Off the UI Thread</a> <li><a href="http://developer.android.com/training/articles/perf-anr.html">Keeping Your
 * App Responsive</a> </ul> <p>
 *
 * </ul> </blockquote>
 *
 * This demo is the second in a series of three demos using <code>ListView</code>: <p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc" width="100"> <th align="center"
 * >Demo <th align="center" >Content (view class) <th align="center" >Source of Content
 * <p>
 * <tr> <td>Demo_ListView_1 <td>Strings (<code>TextView</code>) <td>Resources Array
 * <p>
 * <tr> <td>Demo_ListView_2 <td>Images (<code>ImageView</code>) <td>Device's internal memory card
 * <p>
 * <tr> <td>Demo_ListView_3 <td>Images (<code>ImageView</code>) <td>Internet web site
 *
 * </table> </blockquote> <p>
 *
 * Study Demo_ListView_1 prior to studying this demo. This demo is more complicated, since each view in the
 * <code>ListView</code> contains an image, rather than a text string.<p>
 *
 * Here's a screen snap of the app with a partial view into the scrollable list of images: <p>
 *
 * <center><a href="./javadoc_images/DemoListView2-1.jpg"><img src="./javadoc_images/DemoListView2-1.jpg" width="300" alt="image"></a></center> <p>
 *
 * Instead of displaying text strings in the <code>ListView</code>, we display images.  As you can imagine, displaying
 * images brings special challenges, since the image files must be accessed and converted to bit maps for display in the
 * view objects that appear in the <code>ListView</code>.  This takes time!<p>
 *
 * As the user scrolls up and down in the <code>ListView</code>, it is important not to block the UI while waiting for
 * images to load.  So, the loading of images is delegated to a separate thread.  Furthermore, as the user scrolls up
 * and down, images may appear, disappear, then reappear.  It is more efficient to re-access an image if that image's
 * bitmap is stored in a cache, rather than re-reading the image from the original file.  All these details are handled
 * in this demo. <p>
 *
 * In the main activity we create a <code>String</code> array of image filenames (rather than a <code>String</code>
 * array of words, as in Demo_ListView_1).  The demo is setup to use the images that exist in the directory used by the
 * device's camera. <p>
 *
 * As with Demo_ListView_1, the final step in the app's <code>onCreate</code> method is to give the
 * <code>ListView</code> an adapter that is bound to the data: <p>
 *
 * <pre>
 *      setListAdapter(new ImageAdapter(imageFiles, path));
 * </pre>
 *
 * A <code>ListAdapter</code> is an extended <code>Adapter</code> that serves as a bridge between a
 * <code>ListView</code> and the data that backs the list. Our <code>ListAdapter</code> is an instance of
 * <code>ImageAdapter</code>, a custom class that extends <code>BaseAdapter</code> (which extends <code>Adapter</code>).
 * We override and implement four of the methods: <code>getCount</code>, <code>getItem</code>, <code>getItemId</code>,
 * and <code>getView</code>. <p>
 *
 * The description above is more or less the same as in Demo_ListView_1.  However, the <code>getView</code> method in
 * <code>ImageAdapter</code> instantiates <code>ImageView</code> objects, rather than <code>TextView</code> objects.
 * This is necessary since we are placing images, not text, in the views.  The <code>getView</code> method also uses a
 * different approach to retrieving the content (in this case, an image) that is placed in the the
 * <code>ImageView</code> object: <p>
 *
 * <pre>
 *      String file = path + File.separator + fileNames[position];
 *      imageDownloader.download(file, (ImageView)view, 300);
 * </pre>
 *
 * The task of retrieving the image from a file and placing it in the view is delegated to the <code>download</code>
 * method of the <code>ImageDownloader</code> class.  An <code>ImageDownloader</code> is a helper class that downloads
 * images and binds them with the provided <code>ImageView</code>. The core purpose of <code>ImageDownloader</code> is
 * to host an inner class named <code>BitmapDownloader</code>, a subclass of <code>AsyncTask</code>, which handles the
 * downloading in a separate thread. Additional cache-related fields and methods are added to improve efficiency. <p>
 *
 * To fully understand these difficult topics, your best bet is to study the links above under Android Developer's Blog
 * and Android Developer Training.  Study these while also reviewing the source code and comments in
 * <code>ImageDownloader</code>. <p>
 *
 * It is worth re-iterating a concluding point noted in Demo_ListView_1. Even though there are potentially many images
 * in the device's camera directory, <code>ImageView</code> objects are instantiated in <code>getView</code> only as
 * necessary to fill the device's display. As scrolling takes place, most of the images that appear are placed in
 * <code>ImageView</code> objects that already exist. Existing <code>ImageView</code> objects are simply repositioned
 * and updated to show the next image. <p>
 *
 * Finally, since this program is accessing the device's internal memory card, the manifest must include the following
 * permission (placed just before the application element):
 *
 * <pre>
 *      &lt;uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" /&gt;
 * </pre>
 * <p>
 *
 * @author (c) Scott MacKenzie, 2016-2018
 */

@SuppressWarnings("unused")
public class DemoListView2Activity extends ListActivity
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private static final int PERMISSIONS_REQUEST_IMAGES = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(MYDEBUG, "onCreate!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout);

        String[] permissions;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_IMAGES);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_IMAGES) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                loadImages();
            } else {
                // Permission was denied
                Toast.makeText(this, "Storage access denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void loadImages() {
        // get the directory of this device's camera images
        File pictures = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File images = new File(pictures.getPath());

        // get a string array of the names of all the JPG files in the Pictures directory
        String[] imageFiles = pictures.list(new MyFilenameFilter(".jpg"));
        Log.i("imageFiles", Arrays.toString(imageFiles));

        // we'll need the directory's path, so get that too
        String path = images.getAbsolutePath();

        // give the array of image filenames and their path to the image adapter and use that to fill our ListView
        setListAdapter(new ImageAdapter(imageFiles, path));
    }

    // A filter used with the list method (see above) to return only files with a specified extension (e.g., ".jpg")
    class MyFilenameFilter implements FilenameFilter
    {
        String extension;

        MyFilenameFilter(String extensionArg)
        {
            this.extension = extensionArg;
        }

        @Override
        public boolean accept(File f, String name)
        {
            return name.toLowerCase().endsWith(extension);
        }
    }
}
