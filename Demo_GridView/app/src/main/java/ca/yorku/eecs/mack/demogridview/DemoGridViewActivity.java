package ca.yorku.eecs.mack.demogridview;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Objects;

import android.util.Log;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_GridView - demonstrate presenting images in a GridView. Also demonstrates finger gestures
 * and animating view transitions. <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/topics/ui/layout/gridview.html">Grid
 * View</a>
 * <li><a href="http://developer.android.com/guide/topics/graphics/prop-animation.html">Property
 * Animation</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/widget/GridView.html">
 * <code>GridView</code></a>
 * <li><a href="http://developer.android.com/reference/android/widget/ImageView.html">
 * <code>ImageView</code></a> <li><a
 * href= "http://developer.android.com/reference/android/view/GestureDetector.html">
 * <code>GestureDetector</code> </a>
 * <li><a href= "http://developer.android.com/reference/android/view/ScaleGestureDetector.html" >
 * <code>ScaleGestureDetector</code></a>
 * <li><a href="http://developer.android.com/reference/android/view/ViewPropertyAnimator.html">
 * <code>ViewPropertyAnimator</code></a> </ul> <p>
 *
 * Android Developers Blog: <p>
 *
 * <ul>
 * <li><a href="http://android-developers.blogspot.ca/2011/02/animation-in-honeycomb.html">Animation
 * in Honeycomb</a>
 * <li><a href="http://android-developers.blogspot.ca/2011/05/introducing-viewpropertyanimator.html">
 * Introducing ViewPropertyAnimator</a> </ul>
 *
 * </blockquote>
 *
 * This demo replicates some of the functionality of the Gallery or Photos application provided with
 * most Android devices. The main class demonstrated is <code>GridView</code>. As well,
 * implementations of finger gestures are demonstrated, including dragging, flicking, tapping,
 * double tapping, and two-finger pinching. Animating view transitions is also demonstrated. <p>
 *
 * The application includes three activities: <p>
 *
 * <ol> <li><code>DemoGridViewActivity</code> &ndash; grid view of directories containing images
 * <li><code>DirectoryContentsActivity</code> &ndash; grid view of images within the selected
 * directory
 * <li><code>ImageViewerActivity</code> &ndash; detailed view of individual images </ol> <p>
 *
 * The following illustrates the flow of activities, beginning at the home screen: <p>
 *
 * <center><a href="./javadoc_images/DemoGridView-6.jpg"><img src="./javadoc_images/DemoGridView-6.jpg" width=" 300"></a></center>
 * <p>
 *
 *
 * The first activity to launch is <code>DemoGridViewActivity</code>, which presents a grid of
 * thumbnail images, each representing a directory that contains images. (Here, an "image" is any
 * file with a filename suffix of ".jpg" or ".JPG".) An example follows. <p>
 *
 * <center><a href="./javadoc_images/DemoGridView-1.jpg"><img src="./javadoc_images/DemoGridView-1.jpg" width=" 300"></a></center>
 * <p>
 *
 * The directories are determined at run time via <code>traverse</code>, a recursive method that
 * scans the device's file system looking for directories containing images. Since we are mostly
 * interested in the user's images, the top-level directory for the scan is the user's root
 * directory which is retrieved as an <code>Environment</code> variable: <p>
 *
 * <pre>
 *      File f = Environment.getExternalStorageDirectory();
 *      traverse(f);
 * </pre>
 * <p>
 *
 * The <code>getExternalStorageDirectory</code> method returns the primary external storage
 * directory. The directory is
 * <i>not</i> external to the device. As noted in the API, <p>
 *
 * <blockquote><blockquote><i> This directory can better be thought of as media/shared storage. It
 * is a file system that can hold a relatively large amount of data and that is shared across all
 * applications (does not enforce permissions). Traditionally this is an SD card, but it may also be
 * implemented as built-in storage in a device that is distinct from the protected internal storage
 * and can be mounted as a filesystem on a computer. </i></blockquote></blockquote>
 * <p>
 *
 * Notice in the screensnap above that the directory thumbnails include a semi-transparent grey band
 * along the bottom. The name of the directory and the number of images in the directory appear in
 * the band. Normally, the items in the grid would be instances of <code>ImageView</code>. To create
 * the desired effect, we instead use a custom class called
 * <code>DirectoryImageView</code> which extends <code>ImageView</code>. A
 * <code>DirectoryImageView</code> object
 * includes a text string member to hold directory information. An overridden <code>onDraw</code>
 * method takes care of drawing the semi-translucent grey band, the directory name, and the number
 * of files. Note that the text size is smaller on some of the thumbnails to ensure the text fits in
 * the available space. The adjustment occurs in the
 * <code>setText</code> method in <code>DirectoryImageView.java</code> ("<code>width</code>" is the
 * width of the thumbnail): <p>
 *
 * <pre>
 *      final float textWidth = textPaint.measureText(text);
 *      final float availableWidth = 0.90f * width;
 *      final float textSize = textPaint.getTextSize();
 *      if (textWidth > availableWidth)
 *           textPaint.setTextSize(textSize * (availableWidth / textWidth));
 * </pre>
 *
 * The details of reading the images from storage are largely the same as in Demo_ListView_2.
 * Consult for details. <p>
 *
 * The activity implements <code>AdapterView.OnItemClickListener</code> and attaches the listener to
 * the
 * <code>GridView</code> instance. When the user taps on a directory thumbnail,
 * <code>onItemClick</code> executes. The
 * name of the directory is bundled and sent to the next activity,
 * <code>DirectoryContentsActivity</code>:
 * <p>
 *
 * <pre>
 *      final Bundle b = new Bundle();
 *      b.putString("directory", directories[position].toString());
 *      Intent i = new Intent(getApplicationContext(), DirectoryContentsActivity.class);
 *      i.putExtras(b);
 *      startActivity(i);
 * </pre>
 *
 * The second activity, <code>DirectoryContentsActivity</code>, also uses a <code>GridView</code>.
 * Now, the grid shows images <i>within</i> the selected directory. Here's an example screen snap:
 * <p>
 *
 * <center><a href="./javadoc_images/DemoGridView-2.jpg"><img src="./javadoc_images/DemoGridView-2.jpg" width=" 300"></a></center>
 * <p>
 *
 * Once again, the images are read from storage in the same manner as in Demo_ListView_2. Consult
 * for details. <p>
 *
 * If there are more images than will fit on the screen, the grid is scrollable in a vertical
 * direction via finger flicks. Tapping on an image allows the image (and neighbouring images) to be
 * viewed in greater detail. This occurs by bundling the directory name, the filename array, and the
 * index of the image that was tapped, and launching the next activity. <p>
 *
 * The third activity is <code>ImageViewerActivity</code>, which allows individual images to be
 * viewed in greater detail. The image below, on the left, shows an initial UI when the activity
 * launches. <p>
 *
 * <center> <a href="./javadoc_images/DemoGridView-3.jpg"><img src="./javadoc_images/DemoGridView-3.jpg" width=" 300"></a> <a
 * href="./javadoc_images/DemoGridView-4.jpg"><img src="./javadoc_images/DemoGridView-4.jpg" width=" 300"></a> <a
 * href="./javadoc_images/DemoGridView-5.jpg"><img src="./javadoc_images/DemoGridView-5.jpg" width=" 300"></a> </center> <p>
 *
 * The layout for <code>ImageViewerActivity</code> is given in <code>imageviewer.xml</code>. It
 * includes a
 * <code>LinearLayout</code> holding a <code>TextView</code> and a <code>RelativeLayout</code>
 * container with a single
 * <code>ImageView</code>. The text view holds information about the image, including the filename,
 * the image dimensions, and the file size. The image view holds the image. The
 * <code>ImageView</code> instance is configured in
 * <code>imageviewer.xml</code> to fill its parent container: <p>
 *
 * <pre>
 *      android:layout_width="match_parent"
 *      android:layout_height="match_parent"
 * </pre>
 *
 * However, the image itself is scaled to fit within the bounds of the <code>ImageView</code>
 * instance, in a manner that centers the image while cropping it to retain its aspect ratio. This
 * occurs via <p>
 *
 * <pre>
 *      imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
 * </pre>
 *
 * This method is invoked in the <code>getView</code> method in <code>ImageAdapter.java</code>. <p>
 *
 * A variety of finger gestures are supported when viewing images. Touch events are handled by
 * implementing the
 * <code>OnTouchListener</code> listener method <code>onTouch</code>. As well, two inner classes
 * are used,
 * <code>ScaleListener</code>, which extends <code>ScaleGestureDetector</code>, to look for scaling
 * gestures, and
 * <code>GestureListener</code> , which extends <code>GestureDetector</code>, to look for flicks
 * (aka flings) and double taps. Instances of these classes appear at the beginning of
 * <code>onTouch</code> to inspect motion events and to look for various gestures. An example of
 * this was demonstrated earlier in Demo_Scale. Consult for details. <p>
 *
 * If the user double taps on the image, it zooms in to 3&times; its size (see the center image,
 * above). Some of the image is off-screen. The image may be touched and moved about by a dragging
 * action (see <code>ACTION_MOVE</code> in
 * <code>onTouch</code>). Another double tap returns the image to its normal size. When toggling
 * between normal (&times;1) and zoomed (&times;3), the transitions are animated using <p>
 *
 * <pre>
 *      imageView.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationX(positionX)
 *      .translationY(positionY);
 * </pre>
 *
 * The effect is to smooth the transition to the normal or zoomed state. This line of code is in
 * the
 * <code>onDoubleTap</code> method of <code>ImageViewerActivity.java</code>. The
 * <code>animate</code> method returns a
 * <code>ViewPropertyAnimator</code> object on which animation methods are chained. Each of the
 * methods is associated with a setter method. For example, <code>scaleX</code> (see above) becomes
 * <code>setScaleX</code>. The arguments are the values to which the animation proceeds. <p>
 *
 * Another option is to zoom in and examine a point of interest in an image using two-finger pinch
 * and un-pinch gestures (see the image on the right, above). This is handled in
 * <code>onScaleBegin</code> and <code>onScale</code> in
 * <code>ImageViewActivity.java</code>. Implementing this was tricky, since the scaling must be
 * centered on the <i>focus point</i> &mdash; the point mid-way between the two fingers when the
 * gesture begins. Furthermore, if the focus point moves during scaling, the image must also move so
 * that the point of interest remains between the two fingers. Thus, the <code>onScale</code> method
 * must take care of both scaling and moving the image. <p>
 *
 * Scaling and moving the image are performed differently than in Demo_Scale. In Demo_Scale, the
 * image was placed in a
 * <code>Drawable</code>. Moving and scaling were performed by invoking the <code>translate</code>
 * and
 * <code>scale</code> methods on the <code>Canvas</code> given to <code>onDraw</code> and then
 * drawing onto the canvas. Here, the image is retrieved as an instance of <code>Bitmap</code> which
 * is placed in an
 * <code>ImageView</code> instance in the grid: <p>
 *
 * <pre>
 *      imageView.setImageBitmap(bitmap);
 * </pre>
 *
 * (This line of code is in the <code>download</code> method in <code>ImageDownloader.java</code>.)
 * Moving and scaling are then performed directly on the <code>ImageView</code> instance. This
 * occurs via setter methods in
 * <code>onScale</code>: <p>
 *
 * <pre>
 *      imageView.setScaleX(scaleFactor);
 *      imageView.setScaleY(scaleFactor);
 *      imageView.setTranslationX(positionX);
 *      imageView.setTranslationY(positionY);
 * </pre>
 *
 * The variables <code>scaleFactor</code>, <code>positionX</code>, and <code>positionY</code> are
 * defined in
 * <code>ImageViewerActivity</code>. They are updated to respond to finger gestures based on the
 * <i>x</i> and <i>y</i>
 * data provided in <code>MotionEvent</code>. <p>
 *
 * Finally, flicking to the left or right advances to the next or previous image, respectively. To
 * avoid an abrupt transition, the new image is initially invisible: <p>
 *
 * <pre>
 *      imageView.setAlpha(0f);
 * </pre>
 *
 * It fades in via <p>
 *
 * <pre>
 *      imageView.animate().alpha(1f);
 * </pre>
 *
 * The line of code above is in <code>displayImage</code> in <code>ImageViewerActivity.java</code>.
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */
@SuppressWarnings("unused")
public class DemoGridViewActivity extends Activity implements AdapterView.OnItemClickListener
{
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    GridView directoryGridView; // the view in which to display the images
    DirectoryAdapter directoryAdapter; // interface between the view and images in the view
    ArrayList<DirectoryInfo> directoryInfo; // directory that contains at least one image/JPG file
    private static final int PERMISSIONS_REQUEST_STORAGE = 1;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        String[] permissions;
        // request READ_MEDIA_IMAGES permission when targeting SDK 33+, and READ_EXTERNAL_STORAGE otherwise (SDK 16-32)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions = new String[] {Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            permissions = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE};
        }

        // request permission
        ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_REQUEST_STORAGE);

        // get a reference to the DirectoryImageView
        directoryGridView = (GridView)findViewById(R.id.directorygridview);

        // initialize the ArrayList to hold information about directories containing image files
        directoryInfo = new ArrayList<>();

        // create the DirectoryAdapter and pass it the array of directory info data
        directoryAdapter = new DirectoryAdapter(this, directoryInfo);

        /*
         * Determine the display width and height. The column width is calculated so we have three
         * columns when the screen is in portrait mode. We'll keep the same column width in
         * landscape mode, but use as many columns as will fit. Including "-12" in the calculation
         * accommodates 3 pixels of space on the left and right and between each column.
         */
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int columnWidth = dm.widthPixels < dm.heightPixels ? dm.widthPixels / 3 - 12 : dm
                .heightPixels / 3 - 12;
        directoryAdapter.setColumnWidth(columnWidth);
        directoryGridView.setColumnWidth(columnWidth);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View v, int position, long id)
    {
        // put the selected directory name in a bundle and...
        final Bundle b = new Bundle();
        b.putString("directory", directoryInfo.get(position).toString());

        // start the grid viewer activity (passing it the bundle)
        Intent i = new Intent(getApplicationContext(), DirectoryContentsActivity.class);
        i.putExtras(b);
        startActivity(i);
    }

    /*
     * The traverse method recursively scans the file system, starting at the passed file (which
     * must be a directory). When a directory is found that contains at least one JPG file, the name
     * of the directory is added to the directoryList and the name of the JPG file is added to the
     * imageList. The imageList entry is the name of the first JPG found. It is used to present a
     * directory thumbnail in the startup GridView. The user taps the image to choose the directory
     * in which to view images.
     *
     * To improve performance, the traverse routine does not scan directories containing "Android",
     * as these are system directories.
     *
     * Adapted from... http://www.heimetli.ch/directory/RecursiveTraversal.html
     */
    @SuppressLint("DefaultLocale")
    public void traverse(File file)
    {
        // check if it is a directory (exclude Android system directories and hidden directories)
        if (file.isDirectory() && !(file.toString().indexOf("Android") > 0) && !(file.toString()
                .indexOf(".") > 0))
        {
            // get a list of all the entries in the directory
            String[] entryArray = file.list();

            // ensure that the list is not null
            if (entryArray != null)
            {
                /*
                 * Scan the list to determine if there is at least one JPG file. If so, add the
                 * directory and the filename to the arrays and terminate the scan.
                 */
                for (String entry : entryArray)
                {
                    if (entry.toLowerCase().indexOf(".jpg") > 0)
                    {
                        int n = Objects.requireNonNull(file.list(new MyFilenameFilter(".jpg"))).length;
                        directoryInfo.add(new DirectoryInfo(file, n, entry));
                        break;
                    }
                }

                // loop over all the entries
                for (String entry : entryArray)
                {
                    // recursive call to traverse if an entry is a directory
                    File f = new File(file, entry);
                    if (f.isDirectory())
                        traverse(f);
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_STORAGE) {
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
        /*
         * Starting at the top of the public storage space, find all directories that contain at
         * least one JPG file. Put the name of the directory, the number of image file and the
         * name of a sample image file file in the ArrayLists just created.
         */
        File f = Environment.getExternalStorageDirectory();
        traverse(f);

        Log.i(MYDEBUG, "Number of image directories: " + directoryInfo.size());

        // give the ImageAdapter to the GridView (and load the images)
        directoryGridView.setAdapter(directoryAdapter);

        // attach a click listener to the GridView (to respond to finger taps)
        directoryGridView.setOnItemClickListener(this);
    }

    // A filter used with the list method (see above) to return only files with a specified
    // extension (e.g., ".jpg")
    class MyFilenameFilter implements FilenameFilter
    {
        String extension;

        MyFilenameFilter(String extensionArg)
        {
            this.extension = extensionArg;
        }

        @SuppressLint("DefaultLocale")
        public boolean accept(File f, String name)
        {
            return name.toLowerCase().endsWith(extension);
        }
    }
}
