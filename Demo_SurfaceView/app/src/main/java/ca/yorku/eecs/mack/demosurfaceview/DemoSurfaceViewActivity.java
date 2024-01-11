package ca.yorku.eecs.mack.demosurfaceview;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.Locale;

import ca.yorku.eecs.mack.demosurfaceview.MySurfaceView.MyThread;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_SurfaceView - simple demo app that uses the <code>SurfaceView</code> class and a secondary thread for drawing.
 * </p>
 *
 * The purpose of the <code>SurfaceView</code> class is to provide a dedicated drawing surface embedded within a view
 * hierarchy. One of the main applications is for games. Many games demand constant, high-frequency updates to the
 * graphics, so maintaining the fidelity of the UI is important. This is best achieved if drawing is done from a
 * secondary thread (i.e., <i>not</i> from the main UI thread). This is demonstrated here. </p>
 *
 * <b>Related Information</b> </p>
 *
 * <blockquote> API Guides: </p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html">Canvas and Drawables</a> (and
 * in particular <a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview">On a
 * SurfaceView<a/>) <li><a href="http://developer.android.com/guide/components/processes-and-threads.html">Processes and
 * Threads</a> </ul> <p>
 *
 * API References: </p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/view/SurfaceView.html"> <code>SurfaceView</code></a>
 * <li><a href="http://developer.android.com/reference/android/view/SurfaceHolder.html"> <code>SurfaceHolder</code></a>
 * <li><a href="http://developer.android.com/reference/android/view/SurfaceHolder.Callback.html">
 * <code>SurfaceHolder.Callback</code></a> <li><a href="http://developer.android.com/reference/java/lang/Thread.html"><code>Thread</code>
 * <li><a href="http://developer.android.com/reference/android/os/SystemClock.html"> <code>SystemClock</code></a> </a>
 * <li><a href="http://developer.android.com/reference/android/widget/SeekBar.html"> <code>SeekBar</code></a> <li><a
 * href= "http://developer.android.com/reference/android/widget/SeekBar.OnSeekBarChangeListener.html">
 * <code>SeekBar.OnSeekBarChangeListener</code></a> </ul> <p>
 *
 * Java Tutorials: </p>
 *
 * <ul> <li><a href="http://docs.oracle.com/javase/tutorial/essential/concurrency/index.html">Concurrency</a> (and in
 * particular <a href="http://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html">Synchronization</a> and
 * sub-topics) </ul> </blockquote>
 *
 * <b>UI Summary</b> </p>
 *
 * Below is a screen snap of the UI a few seconds after the demo is launched: </p>
 *
 * <center><a href="./javadoc_images/DemoSurfaceView-1.jpg"><img src="./javadoc_images/DemoSurfaceView-1.jpg" width="300"></a> </center> </p>
 *
 * Although not apparent in the image, the happy-face ball is moving diagonally on the display, bouncing from wall to
 * wall. It's not much of a game, but this is good enough for the purpose of this demo. </p>
 *
 * Also not apparent is that ball is rotating while it moves. The display also presents the elapsed time since the app
 * was launched, the number of times the ball has hit the wall, and the ball velocity in inches per second. </p>
 *
 * The UI includes a slider along the bottom to control the ball velocity. The slider is an instance of
 * <code>SeekBar</code>. The current value appears beside the slider.</p>
 *
 * <b>SurfaceView and Thread</b> </p>
 *
 * The UI where the ball is drawn is seen within the dark red rectangle in the screen snap above. The view, or
 * <i>surface</i>, is an instance of <code>MySurfaceView</code>, which extends <code>SurfaceView</code>. The
 * <code>SurfaceView</code> class is a special sub-class of <code>View</code>. As stated in the <a
 * href="http://developer.android.com/reference/android/view/SurfaceView.html"><code>SurfaceView</code></a> API
 * Reference, <p>
 *
 * <blockquote> <i>One of the purposes of this class is to provide a surface in which a secondary thread can render into
 * the screen.</i> </blockquote>
 *
 * The <a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview">On a Surface
 * View</a> API Guide further notes, <p>
 *
 * <blockquote> <i>Inside your <code>SurfaceView</code> class is also a good place to define your secondary
 * <code>Thread</code> class, which will perform all the drawing procedures to your <code>Canvas</code></i>
 * </blockquote>
 *
 * This recommendation is followed here. Within the <code>MySurfaceView</code> class, we define an inner class called
 * <code>MyThread</code> which extends <code>Thread</code>. In fact, the code in this demo closely follows the
 * recommended organization as laid out in the various Android references (see links above). Consult these for
 * clarification as you work your way through the code. </p>
 *
 * Unfortunately, the various Android references and guides related to <code>SurfaceView</code> or <code>Thread</code>
 * do not provide any detailed examples. The closest thing to a full application is the LunarLander game which is
 * provided in the Android SDK's <code>sample</code> directory (as noted in <a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview">On
 * a SurfaceView</a>). Studying the LunarLander game is a challenge, however. The code is complex, out of date (input
 * uses key events, rather than touch events), and contains bugs (Google "Android Lunar Lander Bug" to see the extent of
 * the discussion around a difficult bug and ideas for correcting it). </p>
 *
 * This demo was reversed-engineered from the code for LunarLander. The majority of the code was removed and the UI was
 * revamped and simplified (see image above). We're showing the basics of using <code>SurfaceView</code> and drawing
 * from a secondary thread, but little more. </p>
 *
 * Let's proceed with a high-level description of the organization of code.</p>
 *
 * <b>Code Summary</b> </p>
 *
 * The <code>SurfaceHolder</code> class provides the link between the <code>SurfaceView</code> and the thread that draws
 * the view. This is established in the constructor of <code>MySurfaceView</code>: </p>
 *
 * <pre>
 *      SurfaceHolder surfaceHolder;
 *      MyThread myThread;
 *      ...
 *      public MySurfaceView(Context context, AttributeSet attrs)
 *      {
 *           super(context, attrs);
 *           surfaceHolder = this.getHolder();
 *           surfaceHolder.addCallback(this);
 *           myThread = new MyThread(surfaceHolder, this.getContext(), false);
 *      }
 * </pre>
 *
 * The code above retrieves a reference to the <code>SurfaceHolder</code> and instantiates the thread, giving it the
 * surface holder. The <code>addCallback</code> method adds a callback interface to the <code>SurfaceHolder</code>. This
 * is the usual arrangement for <code>SurfaceView</code>. (As expected, the class signature includes "<code>implements
 * SurfaceHolder.Callback</code>".) There are three callback methods: <code>surfaceCreated</code>,
 * <code>surfaceChanged</code>, and <code>surfaceDestroyed</code>. </p>
 *
 * Careful use of the callback methods is important to ensure that the drawing thread only accesses the surface when it
 * is valid &mdash; between <code>surfaceCreated</code> and <code>surfaceDestroyed</code>. Although the drawing thread
 * is instantiated in the constructor (see above), it is not started until <code>surfaceCreated</code> executes: </p>
 *
 * <pre>
 *      &#64;Override
 *      public void surfaceCreated(SurfaceHolder holder)
 *      {
 *           ...
 *           myThread.status = MyThread.STARTED;
 *           myThread.start();
 *      }
 * </pre>
 *
 * With this, the thread is started and drawing begins via the secondary thread. </p>
 *
 * The variable <code>status</code> (see above) is used to coordinate running and shutting down the thread. The thread
 * status is either running or shut-down, as represented using integer constants: </p>
 *
 * <pre>
 *      final static int STARTED = 100;
 *      final static int SHUT_DOWN = 200;
 * </pre>
 *
 * The workhorse method of the <code>Thread</code> class is <code>run</code>. Below is the implementation in our custom
 * thread class (<code>MyThread</code>): </p>
 *
 * <pre>
 *     &#64;Override
 *     public void run()
 *     {
 *          while (status == RUNNING)
 *          {
 *               Canvas c = null;
 *               try
 *               {
 *                    c = surfaceHolder.lockCanvas(); // *** 1. LOCK ***
 *                    synchronized (surfaceHolder)
 *                    {
 *                         updateGame(); // *** 2. COMPUTE ***
 *                         if (c != null)
 *                              drawGame(c); // *** 3. DRAW ***
 *                    }
 *               } finally
 *               {
 *                    if (c != null)
 *                    {
 *                         surfaceHolder.unlockCanvasAndPost(c); // *** 4. UNLOCK ***
 *                    }
 *               }
 *          }
 *     }
 * </pre>
 *
 * There are four main tasks performed in the <code>run</code> method: </p>
 *
 * <ol> <li>LOCK &mdash; Get a locked canvas for drawing into ("locking" prevents <code>SurfaceView</code> from
 * creating, destroying, or modifying the surface while it is being drawn). <p>
 *
 * <li>COMPUTE &mdash; Compute the new state of the game (update variables). <p>
 *
 * <li>DRAW &mdash; Draw the updated game into the canvas. <p>
 *
 * <li>UNLOCK &mdash; Unlock the canvas and display it in the UI. </ol> </p>
 *
 * The <code>updateGame</code> and <code>drawGame</code> methods (steps 2 and 3, above) are included in a
 * <code>synchronized</code> statement to ensure the main UI's thread does not alter the surface while the update is
 * taking place. </p>
 *
 * The thread executes as a continuous series of <i>compute-draw</i> operations on the UI. When the app terminates
 * (e.g., the user presses the Back button), the view's surface is destroyed. When this happens,
 * <code>surfaceDestroyed</code> is called: </p>
 *
 * <pre>
 *      &#64;Override
 *      public void surfaceDestroyed(SurfaceHolder holder)
 *      {
 *           myThread.status = MyThread.SHUT_DOWN;
 *           while (myThread != null)
 *           {
 *                try
 *                {
 *                     myThread.join(); // kill the thread (but be patient)
 *                     myThread = null;
 *                } catch (InterruptedException e) { }
 *           }
 *      }
 * </pre>
 *
 * The code above tells the thread to shut down and then waits for it to finish. </p>
 *
 * An additional detail in this demo is saving and restoring the UI's state variables when there is a configuration
 * change (e.g., the screen is rotated). For this, three variables are considered "state variables":</p>
 *
 * <ul>
 * <li><code>elapsedTime</code> &mdash; the elapsed time since the app was first launched<p>
 *
 * <li><code>wallHits</code>
 * &mdash; the number of times the ball hit the wall<p>
 *
 * <li><code>velocity</code> &mdash; the current velocity of the ball
 * </ul> <p>
 *
 * The variables are saved and restored as expected, using <code>onSaveInstanceState</code> and
 * <code>onRestoreInstanceState</code>. To facilitate saving and restoring, the state variables are defined in
 * <code>MySurfaceView</code> rather than in <code>MyThread</code>. </p>
 *
 * The values of these variables are presented on the UI's display while the app is running (see screen snap above).
 * </p>
 *
 * Consult the source code and comments for complete details. </p>
 *
 * @author (c) Scott MacKenzie, 2014-2018
 */
public class DemoSurfaceViewActivity extends Activity implements SeekBar.OnSeekBarChangeListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static String ELAPSED_TIME_KEY = "elapsed_time";
    final static String WALL_HITS_KEY = "wall_hits";
    final static String VELOCITY_KEY = "velocity";

    MySurfaceView mySurfaceView;
    SeekBar seekBar;
    TextView seekBarValue;
    CountDownTimer timer;
    float velocity;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        mySurfaceView = (MySurfaceView)findViewById(R.id.my_surface_view);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekBarValue = (TextView)findViewById(R.id.seekbar_value);

        velocity = MyThread.DEFAULT_VELOCITY; // for the ball

        seekBar.setMax(10 * MyThread.MAXIMUM_VELOCITY);
        seekBar.setProgress(10 * (int)velocity);
        seekBarValue.setText(String.format(Locale.CANADA, "%4.1f", velocity));
        seekBar.setOnSeekBarChangeListener(this);

		/*
         * NOTE: See MySurfaceView source code for details and comments on instantiating the
		 * secondary thread that does the drawing.
		 */

		/*
         * FYI Add-on: We use a timer to count and output the refresh rate for the UI. Output is
		 * written to the LogCat window once per second. The output is the value of refreshCount,
		 * which is simply the number of times the display was refreshed since the last output. On a
		 * Nexus 4, the value is about 60 refreshes per second.
		 * 
		 * The timer is started in onResume, rather than here. This ensures the timer is started
		 * both when the activity is launched and when the activity is resumed.
		 */
        timer = new CountDownTimer(1000, 1000)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                Log.i(MYDEBUG, "refreshCount=" + mySurfaceView.refreshCount);
                mySurfaceView.refreshCount = 0;
                start();
            }
        };
    }

    // ============================
    // Listener methods for seekbar
    // ============================

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean arg2)
    {
        // compute desired ball velocity, based on slider position
        velocity = (float)progress / MyThread.MAXIMUM_VELOCITY;

        // give the velocity to the SurfaceView (used in the drawing thread)
        mySurfaceView.setVelocity(velocity);

        // update the value displayed in the UI beside the slider
        seekBarValue.setText(String.format(Locale.CANADA, "%4.1f", velocity));
    }

    @Override
    public void onStartTrackingTouch(SeekBar arg0)
    {
    }

    @Override
    public void onStopTrackingTouch(SeekBar arg0)
    {
    }

    /*
     * Cancel the timer when the activity is stopped. If we don't do this, the timer continues after
     * the activity finishes. See Mort's answer in...
     *
     * http://stackoverflow.com/questions/15144232/countdowntimer-continues-to-tick-in-background-how
     * -do-i-retrieve-that-count-in
     */
    @Override
    public void onStop()
    {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        timer.start();
    }

    /*
     * If there is a configuration change (e.g., the screen is rotated), the three state variables
     * are saved and restored as part of the normal lifecycle transitions.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putLong(ELAPSED_TIME_KEY, mySurfaceView.elapsedTime);
        savedInstanceState.putInt(WALL_HITS_KEY, mySurfaceView.wallHits);
        savedInstanceState.putFloat(VELOCITY_KEY, mySurfaceView.velocity);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        mySurfaceView.elapsedTime = savedInstanceState.getLong(ELAPSED_TIME_KEY);
        mySurfaceView.wallHits = savedInstanceState.getInt(WALL_HITS_KEY);
        mySurfaceView.velocity = savedInstanceState.getFloat(VELOCITY_KEY);
    }
}
