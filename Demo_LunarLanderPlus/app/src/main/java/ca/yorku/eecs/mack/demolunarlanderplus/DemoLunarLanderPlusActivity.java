package ca.yorku.eecs.mack.demolunarlanderplus;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import ca.yorku.eecs.mack.demolunarlanderplus.LunarView.LunarThread;

/**
 * Demo_LunarLanderPlus - demo of the Lunar Lander arcade game.
 * <p>
 *
 * Related information:
 * <p>
 *
 * <blockquote> API Guides:
 * <p>
 *
 * <ul>
 * <li><a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html">Canvas and
 * Drawables</a> (and in particular <a
 * href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview">On a
 * SurfaceView<a/>)
 * <li><a href="http://developer.android.com/guide/components/processes-and-threads.html">Processes
 * and Threads</a>
 * </ul>
 * <p>
 *
 * API References:
 * <p>
 *
 * <ul>
 * <li><a href="http://developer.android.com/reference/android/view/SurfaceView.html">
 * <code>SurfaceView</code></a>
 * <li><a href="http://developer.android.com/reference/android/view/SurfaceHolder.html">
 * <code>SurfaceHolder</code></a>
 * <li><a href="http://developer.android.com/reference/android/view/SurfaceHolder.Callback.html">
 * <code>SurfaceHolder.Callback</code></a>
 * <li><a href="http://developer.android.com/reference/java/lang/Thread.html"><code>Thread</code>
 * </a>
 * <li><a href="http://developer.android.com/reference/android/app/Application.html">
 * <code>Application</code></a>
 * </ul>
 * <p>
 *
 * Java Tutorials:
 * <p>
 *
 * <ul>
 * <li><a
 * href="http://docs.oracle.com/javase/tutorial/essential/concurrency/index.html">Concurrency</a>
 * (and in particular <a
 * href="http://docs.oracle.com/javase/tutorial/essential/concurrency/sync.html">Synchronization</a>
 * and sub-topics)
 * </ul>
 * </blockquote>
 *
 * Lunar Lander was an arcade game released by Atari in 1979:
 * <p>
 *
 * <center> <a href="./javadoc_images/DemoLunarLanderPlus-1.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-1.jpg"
 * height="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="./javadoc_images/DemoLunarLanderPlus-2.jpg"><img
 * src="./javadoc_images/DemoLunarLanderPlus-2.jpg" height="300"></a> </center>
 * <p>
 *
 *
 * A brief history and description of the original game are found on Wikipedia (<a
 * href="http://en.wikipedia.org/wiki/Lunar_Lander_%28arcade_game%29">click here</a>).
 * <p>
 *
 * The current demo is a simple implementation of Lunar Lander for the Android platform. Lunar
 * Lander was chosen because an example implementation is cited in the Android API Guide for Canvas
 * and Drawables (see <a
 * href="http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview">On a
 * SurfaceView</a>). The code for the example is provided in the SDK samples folder. This demo
 * started as an import of the LunarLander project from the samples folder. Numerous changes were
 * introduced, hence the name change: Lunar Lander Plus. The most notable change is in user input.
 * In the sample code, user input occurred through key events from a D-pad. The current demo
 * replaces this with touch events from a semi-transparent button pad that overlays the game scene.
 * The button pad is seen in the following screen snap, showing the demo upon launching:
 * <p>
 *
 * <center> <a href="./javadoc_images/DemoLunarLanderPlus-3.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-3.jpg"
 * height="400"></a> </center>
 * <p>
 *
 * The background image in DemoLunarLanderPlus is one of the most famous photos of space exploration
 * by mankind. It shows the earth rising from the horizon of the moon. The photo was taken on the <a
 * href="http://en.wikipedia.org/wiki/Apollo_8">Apollo 8</a> mission on December 24, 1968.
 * <p>
 *
 * The buttons operate as follows:
 * <p>
 *
 * <ul>
 * <li>Up - start/pause the game
 * <li>Down - exit the game (ignored while playing)
 * <li>Left - rotate the lander counterclockwise
 * <li>Right - rotate the lander clockwise
 * <li>Center - fire the engine (thruster)
 * </ul>
 * <p>
 *
 * The button pad supports multitouch. During gameplay, the user is primarily acting with the Left,
 * Center, and Right buttons. The touches may overlap to achieve the game objective and to enhance
 * the user experience. For example, it is possible rotate the lander while at the same time firing
 * the engine. To toggle between clockwise and counterclockwise rotation, the user may press and
 * hold the Left button, while tapping quickly on the Right button (or vice versus).
 * <p>
 *
 * Among the other changes is the re-naming of variables and a re-organization of the code to make
 * the demo consistent with other demos in this series. A well, the current code corrects a
 * well-known bug that caused the application to crash when the user navigated away from the
 * application by, for example, pressing the Android Home key. (Just Google
 * "Android Lunar Lander Bug" to see the extent of the discussion around this bug and ideas for
 * correcting it.)
 * <p>
 *
 * Improvements to the UI include a new display of the fuel status and speed and the addition of
 * stats to show the number of wins, number of tries, and the elapsed time. Vibrotactile feedback
 * coincident with the engines firing is also included.
 * <p>
 *
 * The Lunar Lander game is presented in a <code>LunarView</code>, which extends
 * <code>SurfaceView</code>, a special sub-class of <code>View</code>.  The use of <code>SurfaceView</code> was
 * elaborated in an earlier demo program, Demo_SurfaceView.  Consult for details.
 * <p>
 *
 * The following screen snaps provide examples of the game in use. On the left is a run of the game
 * where no user input was provided. The lander crashed after 7.6 seconds. Note that the speed is
 * shown with two components. Any speed that is acceptable for landing is shown in green. Speed in
 * excess of this is shown in red. A crash is registered if (a) the landing speed is too great, (b)
 * the angle of the lander on touchdown is too severe, or (c) the lander misses the landing pad.
 * There is also the possibility of running out of fuel, in which case the lander is likely to fall
 * rapidly and crash. The next image shows a successful run of the game. The lander was navigated to
 * a soft landing on the landing pad. The run took 21.5 seconds, however. The next image shows a run
 * in progress. The user is touching the Center button to fire the engines and slow the rate of
 * decent.
 * <p>
 *
 * <center> <a href="./javadoc_images/DemoLunarLanderPlus-4.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-4.jpg"
 * height="400"></a> <a href="./javadoc_images/DemoLunarLanderPlus-5.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-5.jpg"
 * height="400"></a> <a href="./javadoc_images/DemoLunarLanderPlus-6.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-6.jpg"
 * height="400"></a> <a href="./javadoc_images/DemoLunarLanderPlus-7.jpg"><img src="./javadoc_images/DemoLunarLanderPlus-7.jpg"
 * height="400"></a> </center>
 * <p>
 *
 * The image above on the right shows the menu. A variety of options are selectable, including the
 * difficulty of the game (Easy, Medium, Difficult). The difficulty settings control the amount of
 * fuel available, the maximum allowable speed of the landing, the allowable angle of the lander at
 * touchdown, and the width of the landing pad. For example, setting the difficulty to Easy makes
 * the game easier in four ways:
 * <p>
 *
 * <ul>
 * <li>More fuel is available
 * <li>A faster landing speed is allowed
 * <li>A greater lander angle at touchdown is allowed
 * <li>The landing pad is wider
 * </ul>
 * <p>
 *
 * The game also includes the possibility of a "hyperspace win", accomplished by rotating the lander
 * 180 degrees and thrusting forward at high speed directly to the landing pad. Give it try!
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */

@SuppressWarnings("unused")
public class DemoLunarLanderPlusActivity extends Activity implements View.OnTouchListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private static final int MENU_EASY = 1;
    private static final int MENU_HARD = 2;
    private static final int MENU_MEDIUM = 3;
    private static final int MENU_PAUSE = 4;
    private static final int MENU_RESUME = 5;
    private static final int MENU_START = 6;
    private static final int MENU_STOP = 7;
    private static final int MENU_EXIT = 8;
    final int MAX_TOUCH_POINTS = 10;
    final int INVALID = -1;

    // variables to manage multitouch event pointers
    int index, id;
    int[] touchPointId = new int[MAX_TOUCH_POINTS];
    int[] buttonId = new int[MAX_TOUCH_POINTS];

    // a handle to the thread that's actually running the animation
    private LunarThread lunarThread;

    // a handle to the View in which the game is running
    private LunarView lunarView;

    // a button pad for user input
    private ButtonPad buttonPad;

    // invoked when the Activity is created
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Log.i(MYDEBUG, "onCreate! saveInstanceState=" + savedInstanceState);

        // use the layout defined in the XML file
        setContentView(R.layout.lunar_layout);

        // get the button pad from the XML (used for player input)
        buttonPad = (ButtonPad)findViewById(R.id.buttonSet);

        // attached a touch listener to the button pad
        buttonPad.setOnTouchListener(this);

        // get a handle to the LunarView from XML
        lunarView = (LunarView)findViewById(R.id.lunar);

		/*
         * The following line was deleted from the Android sample code as part of the very painful
		 * process of correcting a difficult bug in the Android sample code for Lunar Lander. The
		 * LunarThread object is now created in the surfaceCreated method of LunarView, rather than
		 * in the constructor of LunarView. This change necessitated several additional changes. In
		 * this activity we obtain a reference to the LunarThread in the onWindowFocusChanged method
		 * (see below).
		 */
        // lunarThread = lunarView.getThread();

        // get the TextView from the XML and pass a reference to the LunarView (used for messages)
        lunarView.setTextView((TextView)findViewById(R.id.text));

        // give the LunarThread a vibrator object (used when the engine is firing)
        Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
        lunarView.setVibrator(v);

        // invalidate all touch point ids and button ids
        for (int i = 0; i < touchPointId.length; ++i)
        {
            touchPointId[i] = INVALID;
            buttonId[i] = INVALID;
        }

        // The launcher icon has "LL+" as the title. Provide the full title in the action bar.
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setTitle("Lunar Lander Plus");
    }

    /*
     * Wait until the activity window has focus to get the lunar thread (because the lunar thread is
     * created in the lunar view's surfaceCreated method).
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (hasFocus)
        {
            // Log.i(MYDEBUG, "onWindowFocusChanged (has focus)!");
            lunarThread = lunarView.getThread();
        }
    }

    // invoked during init to give the Activity a chance to set up its Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);

        menu.add(0, MENU_START, 0, R.string.menu_start);
        menu.add(0, MENU_STOP, 0, R.string.menu_stop);
        menu.add(0, MENU_PAUSE, 0, R.string.menu_pause);
        menu.add(0, MENU_RESUME, 0, R.string.menu_resume);
        menu.add(0, MENU_EASY, 0, R.string.menu_easy);
        menu.add(0, MENU_MEDIUM, 0, R.string.menu_medium);
        menu.add(0, MENU_HARD, 0, R.string.menu_hard);
        menu.add(0, MENU_EXIT, 0, "Exit");
        return true;
    }

    // invoked when the user selects an item from the Menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case MENU_START:
                lunarThread.doStart();
                return true;
            case MENU_STOP:
                lunarThread.setState(LunarThread.STATE_LOSE, getText(R.string.message_stopped));
                return true;
            case MENU_PAUSE:
                lunarThread.doPause();
                return true;
            case MENU_RESUME:
                lunarThread.doUnpause();
                return true;
            case MENU_EASY:
                lunarThread.setDifficulty(LunarThread.DIFFICULTY_EASY);
                return true;
            case MENU_MEDIUM:
                lunarThread.setDifficulty(LunarThread.DIFFICULTY_MEDIUM);
                return true;
            case MENU_HARD:
                lunarThread.setDifficulty(LunarThread.DIFFICULTY_HARD);
                return true;
            case MENU_EXIT:
                // Exit the app. See, also, the run method in LunarThread.
                lunarThread.interrupt();
                onDestroy();
                finish();
                return true;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // Log.i(MYDEBUG, "Activity onTouch!");
        // get the index of the pointer associated with this event
        index = me.getActionIndex();

        // get the id of the pointer associated with this event
        id = me.getPointerId(index);

        // identify the ButtonPad button associated with this touch event
        final int tmpButton = buttonPad.getButton(me.getX(index), me.getY(index));

        // process the event
        switch (me.getAction() & MotionEvent.ACTION_MASK)
        {
            // touch down (first finger or subsequent finger)
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                // find an empty spot in the arrays for the new touch point
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                {
                    if (touchPointId[i] == INVALID)
                    {
                        touchPointId[i] = id;
                        buttonId[i] = tmpButton;
                        break;
                    }
                }

                // tell the button pad which key was pressed (updates L&F)
                buttonPad.setPressed(tmpButton);

                // let the thread work its magic (update game state and physics, draw the game)
                lunarThread.doKeyDown(tmpButton);

				/*
				 * If down-event is from the Down button, lunarThread is interrupted (except during
				 * game play). In this case, we're done.
				 */
                if (lunarThread.isInterrupted())
                {
                    onDestroy();
                    finish();
                }
                break;

            case MotionEvent.ACTION_UP: // last touch point
            case MotionEvent.ACTION_POINTER_UP:
                // find the released touch point, release the button, make it invalid, draw and update game via thread
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                {
                    if (touchPointId[i] == id)
                    {
                        touchPointId[i] = INVALID;
                        buttonPad.setReleased(buttonId[i]);
                        buttonId[i] = INVALID;
                        lunarThread.doKeyUp(tmpButton);
                        break;
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    // invoked when the Activity loses user focus.
    @Override
    protected void onPause()
    {
        // Log.i(MYDEBUG, "onPause!");
        super.onPause();
        lunarView.getThread().doPause(); // pause game when Activity pauses
    }

    @Override
    protected void onStart()
    {
        // Log.i(MYDEBUG, "onStart!");
        super.onStart();
    }

    @Override
    protected void onResume()
    {
        // Log.i(MYDEBUG, "onResume!");
        super.onResume();
    }

    @Override
    protected void onStop()
    {
        // Log.i(MYDEBUG, "onStop!");
        super.onStop();
        MyApplication ma = (MyApplication)getApplicationContext();
        Bundle b = lunarThread.saveState(new Bundle());
        ma.setBundle(b);
    }

    @Override
    protected void onDestroy()
    {
        // Log.i(MYDEBUG, "onDestroy!");
        super.onDestroy();
    }

    @Override
    protected void onRestart()
    {
        // Log.i(MYDEBUG, "onRestart!");
        super.onRestart();
        MyApplication ma = (MyApplication)getApplicationContext();
        Bundle b = ma.getBundle();
        lunarView.setRestoreBundle(b);
    }
}
