package ca.yorku.eecs.mack.democamera;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Locale;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Camera - demo application that uses an Android device's built-in camera </p>
 *
 * Related information: </p>
 *
 * <blockquote> API Guides: </p>
 *
 * <ul> <li> <a href ="http://developer.android.com/guide/topics/media/camera.html">Camera</a> <li> <a
 * href="http://developer.android.com/guide/components/intents-filters.html" >Intents and Intent Filters</a> <li><a
 * href="http://developer.android.com/guide/components/processes-and-threads.html">Processes and Threads</a> <li><a
 * href="http://developer.android.com/guide/topics/ui/layout/listview.html">List View</a> <li><a
 * href="http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews">Building Layouts with an
 * Adapter</a> </ul> <p>
 *
 * API References: </p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/app/Activity.html"> <code>Activity</code></a> <li><a
 * href="http://developer.android.com/reference/android/content/Intent.html"> <code>Intent</code></a> <li> <a
 * href="http://developer.android.com/reference/android/os/Environment.html"> <code>Environment</code></a> <li> <a
 * href="http://developer.android.com/reference/android/net/Uri.html"><code>Uri</code></a> <li> <a
 * href="http://developer.android.com/reference/android/graphics/Bitmap.html"><code>Bitmap</code> </a> <li> <a
 * href="http://developer.android.com/reference/android/widget/ImageView.html"> <code>ImageView</code></a> <li> <a
 * href="http://developer.android.com/reference/android/widget/VideoView.html"> <code>VideoView</code></a> <li><a
 * href="http://developer.android.com/reference/android/app/ListActivity.html"> <code>ListActivity</code></a> <li><a
 * href="http://developer.android.com/reference/android/widget/BaseAdapter.html"> <code>BaseAdapter</code></a> <li><a
 * href="http://developer.android.com/reference/android/os/AsyncTask.html"> <code>AsyncTask</code></a> <li><a
 * href="http://developer.android.com/reference/java/io/FilenameFilter.html"> <code>FilenameFilter</code></a> </ul>
 * </blockquote>
 *
 * Although a camera application can be developed directly using the camera API, there is a simpler approach: using an
 * Intent. As noted in the API Guide, </p>
 *
 * <blockquote><blockquote><i> A quick way to enable taking pictures or videos in your application without a lot of
 * extra code is to use an Intent to invoke an existing Android camera application. A camera intent makes a request to
 * capture a picture or video clip through an existing camera app and then returns control back to your application.
 * </i></blockquote></blockquote> </p>
 *
 * This is the approach demonstrated here. </p>
 *
 * Here is a screen snap of the demo application, showing a picture and video: </p>
 *
 * <center> <a href="DemoCamera-1.jpg"><img src="DemoCamera-1.jpg" width="300"></a> </center> </p>
 *
 * The picture (left) is displayed in an <code>ImageView</code>, a subclass of <code>View</code>. The video (right) is
 * displayed in a <code>VideoView</code>, as subclass of <code>SurfaceView</code> which is a subclass of
 * <code>View</code>. </p>
 *
 * To take a picture, the user taps the "Take Picture" button (see above). To record a video, the user taps the "Record
 * Video" button (see above). In either case, the device's built-in camera application is launched via an intent. This
 * occurs through code in the <code>onClick</code> method which executes in response to the button tap. Here's the code
 * for launching the camera intent to take a picture (with comments):<p>
 *
 * <pre>
 *      // create Intent to take a picture and return control to the calling application
 *      Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
 *
 *      // specify a file URI where the image will be saved
 *      fileUri = getOutputMediaFileUri(mediaStorageDirectory, MEDIA_TYPE_IMAGE);
 *
 *      // use putExtra to give the file URI to the intent
 *      intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
 *
 *      // start the image capture Intent (when the intent finishes, onActivityResult will execute)
 *      // Note: the 2nd argument is the Request Code (will be returned to onActivityResult)
 *      startActivityForResult(intent, IMAGE_MODE);
 * </pre>
 *
 * This code closely follows the example in the the API Guide for Camera (link above). Please review this and the code
 * herein for further details. </p>
 *
 * When the camera application launches, the user interface is defined by the device's camera application, not the demo
 * application. After taking a picture or recording a video, the user is prompted with some options (depending on the
 * application). In order to retrieve the image in the demo application, it must be "saved" via the camera application.
 * The following screen snap shows the camera application just after taking a picture. The Save button (see arrow) must
 * be tapped to save the picture and return control to the demo application. </p>
 *
 * <center> <a href="DemoCamera-2.jpg"><img src="DemoCamera-2.jpg" width="300"></a> </center> </p>
 *
 * Taking this option saves the picture/video and returns control to the activity that launched the intent
 * (<code>DemoCameraActivity</code>). At this juncture, the <code>onActivityResult</code> method in the calling activity
 * executes. This method is defined in the <code>Activity</code> class. Here's the signature: </p>
 *
 * <pre>
 *      protected void onActivityResult(int requestCode, int resultCode, Intent data)
 * </pre>
 *
 * The API for this method states, </p>
 *
 * <blockquote><i>Called when an activity you launched exits, giving you the <code>requestCode</code> you started it
 * with, the <code>resultCode</code> it returned, and any additional data from it. </i></blockquote> </p>
 *
 * The implementation of this method begins as follows: </p>
 *
 * <pre>
 *      &#064;Override
 *      protected void onActivityResult(int requestCode, int resultCode, Intent data)
 *      {
 *         super.onActivityResult(requestCode, resultCode, data);
 *         if (requestCode == IMAGE_MODE)
 *         {
 *            if (resultCode == Activity.RESULT_OK)
 *            {
 *               // popup a message to the user
 *               Toast.makeText(this, "Image saved to:\n" + fileUri.toString(), Toast.LENGTH_LONG).show();
 * </pre>
 *
 * Pictures/videos are saved in a directory called "CameraStuff" (created by this application) within the directory
 * returned by </p>
 *
 * <pre>
 *      Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
 * </pre>
 *
 * For a picture, the file is named <code>IMG_</code>, followed by a unique date+time designation, followed by
 * <code>.jpg</code>. An example with full path is </p>
 *
 * <pre>
 *      /mnt/shell/emulated/0/Pictures/CameraStuff/IMG_20150225_045939.jpg
 * </pre>
 *
 * For a video, the filename begins with <code>VID_</code> and ends with <code>.mp4</code>. </p>
 *
 * This demo application also keeps track of the files contained in the CameraStuff directory. Separate lists are
 * maintained for the pictures (filenames ending with <code>.jpg</code>) and videos (filenames ending with
 * <code>.mp4</code>). For this, a custom class that implements <code>FilenameFilter</code> is used. The user can cycle
 * forward/backward through the saved pictures/videos using the Previous/Next buttons (see above). </p>
 *
 * Tapping on the view of a picture starts a new activity that presents a scrollable list of all the images. The
 * activity is <code>ImageListViewerActivity</code>, a subclass of <code>ListActivity</code>. A
 * <code>ListActivity</code> displays a list of items bound by a data source. In this case, the items are images, with
 * each image presented in an <code>ImageView</code>. The <code>ListActivity</code> hosts a <code>ListView</code> in
 * which the <code>ImageView</code> instances reside. The data source is provided by a custom class called
 * <code>ImageAdapter</code>, a subclass of <code>BaseAdapter</code>. The actual data items (images in bitmaps) are
 * loaded by <code>ImageDownloader</code> a custom class that includes an inner class called
 * <code>BitmapDownloaderTask</code>, a subclass of <code>AsyncTask</code>. Additional details were presented in an
 * earlier demo program, Demo_ListView_2.</p>
 *
 * <code>AsyncTask</code> is a helper class built around <code>Thread</code> and <code>Handler</code>. An
 * <code>AsyncTask</code> instance provides computations that run on a background thread and provide results to the main
 * UI thread. The main advantage in using <code>AsyncTask</code> is that the details of <code>Thread</code> and
 * <code>Handler</code> are hidden. As noted in the <code>AsyncTask</code> API, </p>
 *
 * <blockquote><i> <code>AsyncTask</code> enables proper and easy use of the UI thread. This class allows [an app] to
 * perform background operations and publish results on the UI thread without having to manipulate threads and/or
 * handlers. </i></blockquote> </p>
 *
 * The following is a example of the scrollable list of images available through <code>ImageListViewerActivity</code>:
 * </p>
 *
 * <center> <a href="DemoCamera-3.jpg"><img src="DemoCamera-3.jpg" width="300"></a> </center> </p>
 *
 * From the main activity, tapping on the view of a video starts a new activity to allow video viewing in full-screen
 * mode. The activity is named <code>VideoViewerActivity</code>. Here's an example screen snap after
 * <code>VideoViewerActivity</code> is launched: </p>
 *
 * <center> <a href="DemoCamera-4.jpg"><img src="DemoCamera-4.jpg" width="300"></a> </center> </p>
 *
 * Full-screen video viewing mode supports flick gestures (called "flings" in the Android documentation). These are
 * provided through a custom class, <code>MyGestureListener</code>, which extends <code>GestureDetector
 * .SimpleOnGestureListener</code>.
 * </p>
 *
 * <code>GestureDetector.SimpleOnGestureListener</code> is a <i>convenience class</i> (known in Java/Swing as an
 * <i>adapter class</i>). It provides empty implementations of all the listener methods. There are nine. It is
 * "convenient" to use this class since we only need to implement the methods of interest. An implementation of
 * <code>onFling</code> is provided to advance to the next video (flick to the left) or the previous video (flick to the
 * right). We saw an implementation for <code>onFling</code> in an earlier demo program, Demo_Scale. Only a bare-bones
 * implementation is included here; there is room for improvement. </p>
 *
 * There is also an implementation of <code>onSingleTap</code>. Tapping on a video replays it. </p>
 *
 * Empty implementations are included for <code>onDoubleTap</code> and <code>onLongPress</code>. Perhaps you can think
 * of a reasonable UI response for these gestures. </p>
 *
 * To recap, this demo application includes four activities. Three are defined in the Demo_Camera package:
 * <code>DemoCameraActivity</code>, <code>ImageListViewerActivity</code>, and <code>VideoViewerActivity</code>. The
 * fourth is the activity associated with the camera intent. Activities are launched via intents. The intent to launch
 * this demo's initial activity, <code>DemoCameraActivity</code>, is specified through the <code>intent-filter</code>
 * element in <code>AndroidManifest.xml</code>. To launch a new activity from within an activity, an intent is defined
 * in code and passed to <code>startActivityForResult</code> along with a request code. When the launched activity
 * finishes, it executes <code>setResult</code> and passes control back to the calling activity. In the calling
 * activity, <code>onActivityResult</code> executes where the original request code, the result code, and the intent are
 * available for inspection. Here's a state diagram showing the flow of execution of activities within this demo
 * application: </p>
 *
 * <center> <a href="DemoCamera-5.jpg"><img src="DemoCamera-5.jpg" width="600"></a> </center> </p>
 *
 * The description above is a light on details, since there is the separate issue of passing data between activities.
 * </p>
 *
 * As always, consult the source code, the source-code comments, the API Guides, and API References for further details.
 * </p>
 *
 * NOTE: Sometimes new photos or videos taken with this app do not appear when viewing in Android's Gallery or Photos
 * app.  One fix for this (which worked on my device) is to delete the .thumbnails directory in the DCIM folder and
 * reboot the device.  For further discussion on this and other approaches to this problem, see
 * http://android.stackexchange .com/questions/7088/not-all-images-showing-up-in-gallery <p>
 *
 * @author (c) Scott MacKenzie 2011-2018
 */

public class DemoCameraActivity extends Activity implements OnClickListener, OnTouchListener
{
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final String WORKING_DIRECTORY = "CameraStuff";
    final static String VIDEO_INDEX_KEY = "video_index";
    final static String IMAGE_INDEX_KEY = "image_index";
    final static String DIRECTORY_KEY = "directory";
    final static String VIDEO_FILENAMES_KEY = "video_filenames";
    final static String IMAGE_FILENAMES_KEY = "image_filenames";
    private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private static final int IMAGE_CAMERA_MODE = 100;
    private static final int VIDEO_CAMERA_MODE = 200;
    private static final int IMAGE_VIEWER_MODE = 300;
    private static final int VIDEO_VIEWER_MODE = 400;

    Uri fileUri;
    Button imageCameraButton, videoCameraButton;
    Button imagePrevButton, imageNextButton, videoPrevButton, videoNextButton;
    ImageView imageView;
    VideoView videoView;
    File mediaStorageDirectory;
    String[] imageFilenames, videoFilenames;
    int imageIdx, videoIdx;
    TextView statusTextView;
    TextView imageCountView, videoCountView;

    // create a file Uri for saving an image or video
    private static Uri getOutputMediaFileUri(File directory, int type)
    {
        return Uri.fromFile(getOutputMediaFile(directory, type));
    }

    // create a File for saving an image or video
    private static File getOutputMediaFile(File directory, int type)
    {
        // create a media file name, encoded with the current date and time
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CANADA).format(System.currentTimeMillis());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE)
        {
            mediaFile = new File(directory.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO)
        {
            mediaFile = new File(directory.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else
        {
            return null;
        }
        return mediaFile;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    private void initialize()
    {
        // hide the action bar (gives more display space on small screens)
        if (getActionBar() != null)
            getActionBar().hide();

        // get references to UI widgets
        imageCameraButton = (Button)findViewById(R.id.button1);
        videoCameraButton = (Button)findViewById(R.id.button2);
        imageView = (ImageView)findViewById(R.id.imageView1);
        videoView = (VideoView)findViewById(R.id.videoView1);
        imagePrevButton = (Button)findViewById(R.id.button1a);
        imageNextButton = (Button)findViewById(R.id.button1b);
        videoPrevButton = (Button)findViewById(R.id.button2a);
        videoNextButton = (Button)findViewById(R.id.button2b);
        imageCountView = (TextView)findViewById(R.id.imageCount);
        videoCountView = (TextView)findViewById(R.id.videoCount);
        statusTextView = (TextView)findViewById(R.id.indexandcount);

        // attach listeners to UI widgets
        imageView.setOnTouchListener(this);
        videoView.setOnTouchListener(this);
        imageCameraButton.setOnClickListener(this);
        videoCameraButton.setOnClickListener(this);
        imagePrevButton.setOnClickListener(this);
        imageNextButton.setOnClickListener(this);
        videoPrevButton.setOnClickListener(this);
        videoNextButton.setOnClickListener(this);

        // make a working directory (if necessary) to store the images and videos
        mediaStorageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                WORKING_DIRECTORY);
        if (!mediaStorageDirectory.exists() && !mediaStorageDirectory.mkdirs())
        {
            Log.i(MYDEBUG, "Failed to create directory: " + WORKING_DIRECTORY);
            this.finish(); // terminate
        }
        Log.i(MYDEBUG, "Media directory: " + mediaStorageDirectory.toString());

        // fill arrays for image/video filenames currently in the working directory
        imageFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".jpg"));
        videoFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".mp4"));

		/*
         * Sort the arrays into chronological order. Note: This only works because the date and time
		 * of creation are embedded in the filenames.
		 */
        Arrays.sort(imageFilenames);
        Arrays.sort(videoFilenames);

        Log.i(MYDEBUG, "Number of image files: " + imageFilenames.length);
        Log.i(MYDEBUG, "Number of video files: " + videoFilenames.length);

        // index of last (most recent) image (or -1 if none)
        imageIdx = imageFilenames == null ? -1 : imageFilenames.length - 1;

        // index of last (most recent) video (or -1 in none)
        videoIdx = videoFilenames == null ? -1 : videoFilenames.length - 1;

        if (imageIdx >= 0) // there is at least one image in the directory (show it!)
            displayImage();

        if (videoIdx >= 0) // there is at least one video in the directory (show it!)
            displayVideo();
    }

    // touch callback for picture and video views
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // we're only interested in ACTION_UP events
        if (me.getAction() != MotionEvent.ACTION_UP)
            return true;

        if (v == imageView && imageFilenames.length > 0)
        {
            final Bundle b = new Bundle();
            b.putStringArray(IMAGE_FILENAMES_KEY, imageFilenames);
            b.putString(DIRECTORY_KEY, mediaStorageDirectory.toString());

            // start image viewer activity
            Intent i = new Intent(getApplicationContext(), ImageListViewerActivity.class);
            i.putExtras(b);
            startActivityForResult(i, IMAGE_VIEWER_MODE);

        } else if (v == videoView && videoFilenames.length > 0)
        {
            final Bundle b = new Bundle();
            b.putStringArray(VIDEO_FILENAMES_KEY, videoFilenames);
            b.putString(DIRECTORY_KEY, mediaStorageDirectory.toString());
            b.putInt(VIDEO_INDEX_KEY, videoIdx);

            // start video viewer activity
            Intent i = new Intent(getApplicationContext(), VideoViewerActivity.class);
            i.putExtras(b);
            startActivityForResult(i, VIDEO_VIEWER_MODE);
        }
        return true;
    }

    // callback for the UI buttons used to cycle through images/videos or to launch the camera intent
    @Override
    public void onClick(View v)
    {
        if (v == imageCameraButton) // launch camera intent (image mode)
        {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

            // specify a file URI where the image will be saved
            fileUri = getOutputMediaFileUri(mediaStorageDirectory, MEDIA_TYPE_IMAGE);

            // use putExtra to give the file URI to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the Intent (when the intent finishes, onActivityResult will execute)
            // Note: the 2nd argument is the Request Code (will be returned to onActivityResult)
            startActivityForResult(intent, IMAGE_CAMERA_MODE);

        } else if (v == videoCameraButton) // launch camera intent (video mode)
        {
            // create Intent to take a video and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

            // specify a file URI where the image will be saved
            fileUri = getOutputMediaFileUri(mediaStorageDirectory, MEDIA_TYPE_VIDEO); // not needed

            // use putExtra to give the file URI to the intent
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // specify video quality (1 = high)
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

            // start the Intent (when the intent finishes, onActivityResult will execute)
            // Note: the 2nd argument is the Request Code (will be returned to onActivityResult)
            startActivityForResult(intent, VIDEO_CAMERA_MODE);

        } else if (v == imagePrevButton && imageFilenames.length != 0)
            previousImage();

        else if (v == imageNextButton && imageFilenames.length != 0)
            nextImage();

        else if (v == videoPrevButton && videoFilenames.length != 0)
        {
            previousVideo();
            videoView.start();

        } else if (v == videoNextButton && videoFilenames.length != 0)
        {
            nextVideo();
            videoView.start();
        }
    }

    private void previousImage()
    {
        --imageIdx;
        if (imageIdx < 0)
            imageIdx = imageFilenames.length - 1;
        displayImage();
    }

    private void nextImage()
    {
        ++imageIdx;
        if (imageIdx >= imageFilenames.length)
            imageIdx = 0;
        displayImage();
    }

    private void displayImage()
    {
        if (imageFilenames != null && imageFilenames.length > 0)
        {
            String path = mediaStorageDirectory.toString() + File.separator + imageFilenames[imageIdx];
            Bitmap bmp = BitmapFactory.decodeFile(path);
            imageView.setImageBitmap(bmp);
            imageCountView.setText(String.format(Locale.CANADA, "%d of %d", (imageIdx + 1), imageFilenames.length));

        } else
            imageCountView.setText(String.format("%s", "(no pictures)"));
    }

    private void previousVideo()
    {
        --videoIdx;
        if (videoIdx < 0)
            videoIdx = videoFilenames.length - 1;
        displayVideo();
    }

    private void nextVideo()
    {
        ++videoIdx;
        if (videoIdx >= videoFilenames.length)
            videoIdx = 0;
        displayVideo();
    }

    private void displayVideo()
    {
        if (videoFilenames != null && videoFilenames.length > 0)
        {
            String path = mediaStorageDirectory.toString() + File.separator + videoFilenames[videoIdx];
            videoView.setVideoPath(path);
            videoView.seekTo(1); // ...so first frame appears in view (?)
            videoCountView.setText(String.format(Locale.CANADA, "%d of %d ", (videoIdx + 1), videoFilenames.length));

        } else
            videoCountView.setText(String.format(Locale.CANADA, "%s", "(no videos)"));
    }

    /*
     * The onActivityResult method is called upon exiting from either the camera application, the
     * image viewer, or the video viewer. The image viewer and the video viewer are the full-screen
     * activities defined herein to improve the viewing of images or videos. A series of if/else
     * statements are used to select the correct code to execute, which will depend on the
     * requestCode originally used to launch the activity and the resultCode returned from the
     * activity. Data can be returned from the activity via the Intent object passed to
     * onActivityResult.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_CAMERA_MODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                /*
                 * The following line of commented-out code is from the Android API Guide for
				 * "Camera". However, this line of code causes the app to crash with a null pointer
				 * exception. The problem is that the Intent passed to onActivityResult is null.
				 * After some digging, I came across the following explanation in StackOverflow:
				 *
				 * "The default Android camera application returns a non-null intent only when
				 * passing back a thumbnail in the returned Intent. If you pass EXTRA_OUTPUT with a
				 * URL to write to, it will return a null intent and the picture is in the URL that
				 * you passed in."  (Note: The author meant "URI", not "URL".) See...
				 *
				 * http://stackoverflow.com/questions/9890757/android-camera-data-intent-returns-null
				 */
                // Toast.makeText(this, "Image saved to:\n" + data.getData(),
                // Toast.LENGTH_LONG).show();

                // the fix...
                Toast.makeText(this, "Image saved to:\n" + fileUri.toString(), Toast.LENGTH_LONG).show();

                // update file list and index
                imageFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".jpg"));
                Arrays.sort(imageFilenames);

                imageIdx = imageFilenames.length - 1;
                displayImage();

            } else if (resultCode == Activity.RESULT_CANCELED)
            {
                // user cancelled the image capture
                Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_LONG).show();

            } else
            {
                // image capture failed, advise user
                Toast.makeText(this, "Image capture failed", Toast.LENGTH_LONG).show();
            }
        } else if (requestCode == VIDEO_CAMERA_MODE)
        {
            if (resultCode == RESULT_OK)
            {
                // see comment above for IMAGE_MODE
                Toast.makeText(this, "Video saved to:\n" + fileUri.toString(), Toast.LENGTH_LONG).show();

                // update file list and index (NOTE: current video is last file in list)
                videoFilenames = mediaStorageDirectory.list(new MyFilenameFilter(".mp4"));
                Arrays.sort(videoFilenames);

                videoIdx = videoFilenames.length - 1;
                displayVideo();

            } else if (resultCode == RESULT_CANCELED)
            {
                // user cancelled the video capture
                Toast.makeText(this, "Video capture cancelled", Toast.LENGTH_LONG).show();

            } else
            {
                // video capture failed, advise user
                Toast.makeText(this, "Video capture failed", Toast.LENGTH_LONG).show();
            }
        }

		// we're returning via the Back button in the Navigation Bar. Therefore, the return code is CANCEL
        else if (requestCode == IMAGE_VIEWER_MODE)
        {
            if (resultCode == Activity.RESULT_OK)
                displayImage();
            else
                Log.i(MYDEBUG, "UNKNOWN RESULT CODE (IMAGE)!");

        } else if (requestCode == VIDEO_VIEWER_MODE)
        {
            if (resultCode == Activity.RESULT_OK)
            {
                videoIdx = data.getIntExtra(VIDEO_INDEX_KEY, -1);
                displayVideo();
            } else
                Log.i(MYDEBUG, "UNKNOWN RESULT CODE (VIDEO)!");
        }
    }

    /*
     * We are handling the configuration change ourself, which is to say, the system will not shut
     * down and restart the activity when the configuration changes (such as occurs if the screen is
     * rotated).
     *
     * If the screen is rotated, the UI will still change (e.g., from portrait to landscape). This
     * occurs through the call to the super method. The new orientation is stored in the newConfig
     * object passed as an argument. The layout will remain the same, however (because onCreate does
     * not execute and no new resource is loaded).
     *
     * Note that this method is only called if the manifest contains
     * android:configChanges="orientation" as an attribute for the Activity element. See...
     *
     * http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange
     *
     * Note: Screen orientation changes for this demo occur much faster using this technique
     * compared to the usual approach of implementing onSaveInstanceState and
     * onRestoreInstanceState. This technique is faster because the activity is *not* being shut
     * down and restarted. Just look at the code executed from onCreate and you'll see why.
     *
     * One final note: We are implementing onConfigurationChanged simply to demonstrate this
     * alternative way to handle a configuration change. We aren't actually doing anything except
     * printing a message to the LogCat window. If this method is deleted or commented-out, the
     * app's behaviour is the same. For an example where there is actually some work to do in
     * onConfigurationChanged, see Demo_WebView.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        Log.i(MYDEBUG, "onConfigurationChanged! newConfig=" + newConfig);
        super.onConfigurationChanged(newConfig);
    }

    // A filter used with listFiles (see above) to return only files with a specified extension
    // (e.g., ".jpg" or ".mp4")
    class MyFilenameFilter implements FilenameFilter
    {
        String extension;

        MyFilenameFilter(String extensionArg)
        {
            this.extension = extensionArg;
        }

        public boolean accept(File f, String name)
        {
            return name.endsWith(extension);
        }
    }
}