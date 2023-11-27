package ca.yorku.eecs.mack.democamera;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.File;
import java.util.Locale;

public class VideoViewerActivity extends Activity implements OnTouchListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static String VIDEO_INDEX_KEY = DemoCameraActivity.VIDEO_INDEX_KEY;
    final static String DIRECTORY_KEY = DemoCameraActivity.DIRECTORY_KEY;
    final static String VIDEO_FILENAMES_KEY = DemoCameraActivity.VIDEO_FILENAMES_KEY;

    VideoView videoView;
    TextView statusTextView;
    int videoIdx;
    String[] videoFilenames;
    String directory;
    GestureDetector gestureDetector;
    Bundle bundle;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.videofullscreen);
        videoView = (VideoView)findViewById(R.id.videoFullScreenView);
        videoView.setOnTouchListener(this);

        videoView.setZOrderOnTop(true); // This seems to be necessary (not sure why)

        statusTextView = (TextView)findViewById(R.id.indexandcount);

        // support for gesture detections (e.g., fling)
        gestureDetector = new GestureDetector(this, new MyGestureListener());

        // hide the action bar (provided there is one)
        if (getActionBar() != null)
            getActionBar().hide();

        // data passed from the calling activity in startActivityForResult (see DemoCameraIntentActivity)
        bundle = this.getIntent().getExtras();
        videoFilenames = bundle.getStringArray(VIDEO_FILENAMES_KEY);
        directory = bundle.getString(DIRECTORY_KEY);
        videoIdx = bundle.getInt(VIDEO_INDEX_KEY);

        displayVideo();
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
        final String path = directory + File.separator + videoFilenames[videoIdx];
        videoView.setVideoPath(path);
        videoView.start();

        // update status as well
        statusTextView.setText(String.format(Locale.CANADA, "Video %d of %d", (videoIdx + 1), videoFilenames.length));
    }

    /*
     * Check for fling, double-touch, or single tap gestures (see below for callback methods)
     */
    public boolean onTouch(View v, MotionEvent me)
    {
        gestureDetector.onTouchEvent(me);
        return true;
    }

    // maintain the video index if the screen is rotated
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        videoView.stopPlayback();
        savedInstanceState.putInt(VIDEO_INDEX_KEY, videoIdx);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        videoIdx = savedInstanceState.getInt(VIDEO_INDEX_KEY);

        if (videoIdx >= 0) // there is at least one video in the directory (show it!)
            displayVideo();
    }

    @Override
    public void onBackPressed()
    {
        bundle.putInt(VIDEO_INDEX_KEY, videoIdx);
        this.setResult(Activity.RESULT_OK, getIntent().putExtras(bundle));
        super.onBackPressed();
    }

    /*
     * SimpleOnGestureListener is a convenience class (aka an adapter class). Here, we're only
     * implementing four of the methods.
     */
    class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        // advance to the previous/next video, depending on the direction of the flick
        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float xVelocity, float yVelocity)
        {
            if (xVelocity > 0) // flick to right
                previousVideo();
            else
                // flick to left
                nextVideo();
            return true;
        }

        // for a single tap, play the video (again)
        @Override
        public boolean onSingleTapUp(MotionEvent me)
        {
            videoView.start();
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent me)
        {
            // do something interesting for a double touch/tap
            return true;
        }

        @Override
        public void onLongPress(MotionEvent me)
        {
            // do something interesting for a long press
        }
    }
}
