package ca.yorku.eecs.mack.demopong;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaScannerConnection;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import ca.yorku.eecs.mack.demopong.PongView.PongThread;

/**
 * <h1>Demo_Pong</h1>
 *
 * <h3>Summary</h3>
 *
 * <ul>
 * <li>Demonstration of a simple Pong-like game (including a setup dialog, data collection, etc.)
 * </ul>
 *
 * <h3>Related Reference</h3>
 *
 * The following publication presents research where a variation of this software was used.
 * <p>
 *
 * <ul>
 * <li>
 * <a href="http://www.yorku.ca/mack/ie2014.html">Comparing
 * order of control for tilt and touch games</a>, by Teather and MacKenzie  (<i>IE 2014</i>).
 * <p>
 * </ul>
 * <p>
 *
 * <h3>Background</h3>
 *
 * Pong was an arcade video game developed by Atari in 1972. The game-play simulates table tennis
 * where the player manipulates a paddle to bounce back an approaching ping-pong ball: </p>
 *
 * <center> <a href="./javadoc_images/DemoPong-1.jpg"><img src="./javadoc_images/DemoPong-1.jpg" width="250"></a> &nbsp; &nbsp; &nbsp;
 * &nbsp; &nbsp; <a href="./javadoc_images/DemoPong-2.jpg"><img src="./javadoc_images/DemoPong-2.jpg" width="400"></a> </center> </p>
 *
 * Additional historical details are found on Wikipedia (<a
 * href="http://en.wikipedia.org/wiki/Pong">click here</a>). </p>
 *
 * <h3>UI Summary</h3>
 *
 * When Demo_Pong is launched, the following UI appears: (click to enlarge)</p>
 *
 * <center> <a href="./javadoc_images/DemoPong-3.jpg"><img src="./javadoc_images/DemoPong-3.jpg" height="250"></a> </center> </p>
 *
 * The app runs in fullscreen, landscape mode. Game-play begins by tapping one of the Tilt or Touch
 * oval buttons. With this, the buttons disappear and a pong ball appears and moves diagonally on
 * the <i>table</i>, bouncing off the walls: (dashed lines added)</p>
 *
 * <center> <a href="./javadoc_images/DemoPong-4.jpg"><img src="./javadoc_images/DemoPong-4.jpg" height="250"></a> </center> </p>
 *
 *
 *
 * The initial ball velocity is 4 inches per second on a Nexus 4. The game geometry and ball
 * velocity are scaled according to the display size so that the game difficulty and experience are
 * approximately the same on any device. </p>
 *
 * The game <i>paddle</i> is positioned along the right side of the display (see above). </p>
 *
 * If the control mode is touch, a touch strip appears along the left side of the display: </p>
 *
 * <center> <a href="./javadoc_images/DemoPong-4.png"><img src="./javadoc_images/DemoPong-4.png" height="250"></a> </center> </p>
 *
 * As the ball moves, the user manoeuvres the paddle up and down anticipating the ball's arrival at
 * the right side of the table. The objective is to bounce the ball back with the paddle. This is a
 * <i>hit</i>. If the paddle misses the ball, the ball bounces off the right wall. This is a
 * <i>miss</i>. </p>
 *
 * If enabled, there is vibrotactile feedback when the ball hits the paddle and auditory feedback if
 * the ball misses the paddle and hits the right wall. </p>
 *
 *
 * The method of controlling the paddle varies according to which button was tapped to begin
 * game-play: </p>
 *
 * <center><blockquote>
 * <table border="1" cellspacing="0" cellpadding="6" width="85%">
 * <tr bgcolor="#cccccc">
 * <th>Button
 * <th>Control Mode
 * <th>Order of Control
 * <th>Description
 *
 * <tr>
 * <td><a href="./javadoc_images/DemoPong-5.png"><img src="./javadoc_images/DemoPong-5.png" width="100"></a>
 * <td align="center">Tilt
 * <td align="center">Position
 * <td>The tilt angle of the device controls the <i>position</i> of the paddle. If the device is
 * held horizontally, tilted 35&deg; toward the player, the paddle is vertically centered. Tilting
 * back by 25&deg; positions the paddle at the top of the table. Tilting forward by 25&deg positions
 * the paddle at the bottom of the table. Within this tilt range, there is a direct correspondence
 * between tilt angle and paddle position. Both the center angle and tilt range may be changed
 * through the app's settings (discussed below).
 *
 * <tr>
 * <td><a href="./javadoc_images/DemoPong-6.png"><img src="./javadoc_images/DemoPong-6.png" width="100"></a>
 * <td align="center">Tilt
 * <td align="center">Velocity
 * <td>The tilt angle of the device controls the <i>velocity</i> of the paddle. The center angle and
 * tilt range are the same as above. With velocity-control, however, it is the velocity of the
 * paddle that is controlled. At the center angle, the paddle velocity is 0. The velocity increases
 * gradually (paddle movement is up) as the paddle is tilted back from the center angle. The
 * velocity increases gradually and negatively (paddle movement is down) as the paddle is tilted
 * forward from the center angle.
 *
 * <tr>
 * <td><a href="./javadoc_images/DemoPong-7.png"><img src="./javadoc_images/DemoPong-7.png" width="100"></a>
 * <td align="center">Touch
 * <td align="center">Position
 * <td>The position of the finger on the touch strip controls the <i>position</i> of the paddle. The
 * user positions their left thumb on the touch strip and controls the paddle position by moving
 * their thumb up and down on the strip. There is a direct correspondence between the touch position
 * and the paddle position.
 *
 * <tr>
 * <td><a href="./javadoc_images/DemoPong-8.png"><img src="./javadoc_images/DemoPong-8.png" width="100"></a>
 * <td align="center">Touch
 * <td align="center">Velocity
 * <td>The position of the finger on the touch strip controls the <i>velocity</i> of the paddle. The
 * user positions their left thumb on the touch strip and controls the paddle <i>velocity</i> by
 * moving their thumb up and down on the strip. There is a direct correspondence between the touch
 * position and the paddle velocity, which is +ve when touching above the center line and -ve when
 * touching below the center line.
 *
 *
 * </table>
 * </center></blockquote>
 *
 * <h3>Game Progression</h3>
 *
 * The game includes 10 levels: level 1 is the easiest, level 10 is the hardest. Game-play begins at
 * level 1 and proceeds in sequences of trials. Each back-and-forth cycle of the ball is a trial,
 * ending in a hit or miss. By default, 5 trials are grouped in a sequence. At the end of a
 * sequence, the player's performance is assessed as follows: </p>
 *
 * <ul>
 * <li>0 misses &ndash; proceed to the next level
 * <li>1-2 misses &ndash; repeat level
 * <li>3-5 misses &ndash; go back one level
 * </ul>
 * <p>
 *
 * The following popups illustrate: </p>
 *
 * <center> <a href="./javadoc_images/DemoPong-6.jpg"><img src="./javadoc_images/DemoPong-6.jpg" height="200"></a> &nbsp; &nbsp; <a
 * href="./javadoc_images/DemoPong-7.jpg"><img src="./javadoc_images/DemoPong-7.jpg" height="200"></a> &nbsp; &nbsp; <a
 * href="./javadoc_images/DemoPong-8.jpg"><img src="./javadoc_images/DemoPong-8.jpg" height="200"></a> </center> </p>
 *
 * The game difficulty increases in three ways as the levels progress: </p>
 *
 * <ol>
 * <li>The ball velocity increases. The increase is linear by level. At level 10, the ball velocity
 * is 150% of the initial velocity.
 * <li>The paddle size (height) decreases. The decrease is linear by level. At level 10, the paddle
 * size 50% of the initial paddle size.
 * <li>A random offset is applied to the bounce angle at the wall opposite the paddle. The offset
 * increases linearly from 0.1 <i>x</i> at level 1 to <i>x</i> at level 10, where <i>x</i> = 45&deg;
 * &times; <code>nextRandom()</code>. The offset is applied relative to minimum and maximum bounce
 * angles of -45&deg; and +45&deg;, respectively.
 * </ol>
 * </p>
 *
 * Victory occurs when the user completes level 10 with zero misses: </p>
 *
 * <center> <a href="./javadoc_images/DemoPong-9.jpg"><img src="./javadoc_images/DemoPong-9.jpg" height="250"></a> </center> </p>
 *
 * The happy face above is animated. It rotates and bounces around within the popup. Auditory
 * feedback ("Tada!") accompanies victory.</p>
 *
 * <h3>Experimental Use</h3>
 *
 * Demo_Pong is not just a demo program and game. It is intended for experimental use to assess and
 * compare user performance with the four control modes (see above). As such, there is a setup
 * dialog to select codes for participants, operating parameters, etc. The setup dialog is shown
 * below: </p>
 *
 * <center> <a href="./javadoc_images/DemoPong-10.jpg"><img src="./javadoc_images/DemoPong-10.jpg" width="250"></a> </center> </p>
 *
 * For each launch of the program, game-play proceeds over the number of sequences specified in the
 * setup dialog. This is a maximum, however. If the player reaches level 10 and completes it with no
 * misses ("Victory"), the game ends. Either way, data are saved in a file and the program
 * terminates. </p>
 *
 * The following is an example of a data file collected as part of experimental testing: <a
 * href="./javadoc_images/DemoPong-P03-Tilt_Position-G01-B07.csv">DemoPong-P03-Tilt_Position-G01-B07.csv</a>. After
 * opening in Excel and adjusting the column widths and cell alignments, the data might look like
 * this: (click to enlarge)</p>
 *
 * <center><a href="./javadoc_images/DemoPong-11.jpg"><img src="./javadoc_images/DemoPong-11.jpg" width="800"></a></center> </p>
 *
 * The first four columns (A-D) identify the circumstances of the test, including codes for the
 * participant, the condition, the group, and the block. The condition code in the example is
 * "Tilt_Position", but this can be any code that identifies circumstances of the testing. The group
 * code typically identifies the group to which the participant was assigned in a within-subjects
 * design (i.e., for counterbalancing). The block code is generated automatically, to facilitate
 * testing and to ensure the file name is unique. </p>
 *
 * The next three columns (E-G) identify characteristics of the game-play: the sequence number, the
 * level, and the number of trials in the sequence. </p>
 *
 * The next nine columns (H-P) are performance measures. The performance measures in columns H
 * through L are obvious enough. The measures in columns M, N, O, and P merit explanation, however.
 * <i>Mov_min</i> is the minimum possible movement of the paddle to hit all balls, while
 * <i>Mov_actual</i> is the actual amount of movement. The units are pixels. <i>Mov_eff</i> is the
 * movement efficiency, which is <i>Mov_min</i> / <i>Mov_actual</i>, expressed as a percent. The
 * maximum is 100%, assuming 0 misses. This represents the most efficient paddle movement possible.
 * As seen above, the participant's movement of the paddle was far from ideal. <i>Reversals</i> is
 * the number of times the paddle direction changed. It might be expected that with experience
 * <i>Mov_eff</i> would increase and <i>Reversals</i> would decrease. However, this can only be
 * established through prolonged experimental testing. Finally, note that the measures in these
 * columns are for the number of trials specified in column G.
 *
 * </p>
 *
 * <h3>Code Summary</h3>
 *
 * Demo_Pong brings together many Android coding principles from our earlier demo programs. For
 * drawing in a secondary thread, see Demo_SurfaceView. For the use of tilt as an input control, see
 * Demo_TiltBall. For data collection, storage, and experimental testing, see Graffiti.
 * </p>
 *
 * @author (c) Scott MacKenzie, 2014-2018
 */

public class DemoPongActivity extends Activity implements View.OnTouchListener, SensorEventListener,
        ResultsDialog.OnResultsDialogClickListener, OvalButton.OnOvalButtonClickListener, PongView.OnBlockDoneListener,
        ThankYouDialog.OnThankYouDialogClickListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int TOP_GAME_LEVEL = 10;
    final static float RADIANS_TO_DEGREES = 57.2957795f;

    // int constants for paddle mode
    final static int TILT_POSITION = 100;
    final static int TILT_VELOCITY = 200;
    final static int TOUCH_POSITION = 300;
    final static int TOUCH_VELOCITY = 400;
    final static int SETTINGS = 1; // request code to launch settings activity

    // int constants to setup a sensor mode (see Demo TiltBall API for discussion)
    final static int ORIENTATION = 100;
    final static int ACCELEROMETER_ONLY = 200;
    final static int ACCELEROMETER_AND_MAGNETIC_FIELD = 300;
    final static int SENSOR_DELAY = SensorManager.SENSOR_DELAY_GAME;

    // alpha values for the low pass filter. See Demo TiltBall for discussion.
    final static float[] ALPHA_ARRAY = {0.5f, 0.3f, 0.15f, 0.06f};

    final String WORKING_DIRECTORY = "/DemoPongData/";
    final String DATA_HEADER = "Participant,Condition,Group,Block,Sequence,Level,Trials,Hits,Misses,Result,Time," +
            "Time_total,Mov_min,Mov_actual,Mov_eff,Reversals\n";

    // pointer to the thread that's actually running the animation
    PongThread pongThread;

    // pointer to the view in which the game is running
    PongView pongView;

    SensorManager sensorManager;
    Sensor sA, sO, sM;
    float alpha = ALPHA_ARRAY[2]; // for GAME sampling rate (may need to tweak)

    int defaultOrientation;
    int sensorMode;
    int paddleMode;
    String paddleModeString;

    // game play parameters (obtained from SetupActivity)
    String participantCode, conditionCode, groupCode;
    String blockCode;
    int gameLevel, trials, sequencesMax;
    int tiltCenter, tiltRange, tiltMin, tiltMax;
    float gainVelocityControl;
    boolean vibrotactileFeedback, auditoryFeedback, disableDataCollection;

    float timeTotal; // total time for all the trials in a block
    int sequenceNumber;

    BufferedWriter bw; // for writing output data
    File f;
    String base;

    SharedPreferences sp;
    boolean onceMoreBeforeExit;

    float[] accValues = new float[3]; // smoothed values from accelerometer
    float[] magValues = new float[3]; // smoothed values from magnetic field values

    float x, y, z, pitch, roll;

    LinearLayout buttonLayout;
    MessageView messageView;
    OvalButton setupButton;
    OvalButton tiltButtonPosition, tiltButtonVelocity;
    OvalButton touchButtonPosition, touchButtonVelocity;
    TouchStrip touchStrip;
    ResultsDialog resultsDialog;
    ThankYouDialog thankYouDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // get display metrics (in particular, the pixel density of the display)
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        Vibrator v = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        defaultOrientation = getDefaultDeviceOrientation();

        // get handles to the UI components from XML
        pongView = (PongView)findViewById(R.id.pong_view);
        buttonLayout = (LinearLayout)findViewById(R.id.layout_1);
        messageView = (MessageView)findViewById(R.id.message_view);
        setupButton = (OvalButton)findViewById(R.id.setup_button);
        tiltButtonPosition = (OvalButton)findViewById(R.id.tilt_button_position_control);
        tiltButtonVelocity = (OvalButton)findViewById(R.id.tilt_button_velocity_control);
        touchButtonPosition = (OvalButton)findViewById(R.id.touch_button_position_control);
        touchButtonVelocity = (OvalButton)findViewById(R.id.touch_button_velocity_control);
        touchStrip = (TouchStrip)findViewById(R.id.touch_strip);

        String setupText = getResources().getText(R.string.setup_title).toString();
        String tiltText = "   " + getResources().getText(R.string.tilt).toString() + "   ";
        String tiltTextPosition = getResources().getText(R.string.tilt_position).toString();
        String tiltTextVelocity = getResources().getText(R.string.tilt_velocity).toString();
        String touchText = getResources().getText(R.string.touch).toString();
        String touchTextPosition = getResources().getText(R.string.touch_position).toString();
        String touchTextVelocity = getResources().getText(R.string.touch_velocity).toString();

        setupButton.configure(dm.density, 0x888888, setupText, null);
        tiltButtonPosition.configure(dm.density, 0x5659c2, tiltText, tiltTextPosition);
        tiltButtonVelocity.configure(dm.density, 0xc14382, tiltText, tiltTextVelocity);
        touchButtonPosition.configure(dm.density, 0x44aa44, touchText, touchTextPosition);
        touchButtonVelocity.configure(dm.density, 0x44aaaa, touchText, touchTextVelocity);

        setupButton.setOnOvalButtonClickListener(this);
        tiltButtonPosition.setOnOvalButtonClickListener(this);
        tiltButtonVelocity.setOnOvalButtonClickListener(this);
        touchButtonPosition.setOnOvalButtonClickListener(this);
        touchButtonVelocity.setOnOvalButtonClickListener(this);

        // load settings from SharedPreferences object
        loadSettings();

        pongView.setMessageView(messageView);
        pongView.setVibrator(v);
        pongView.setParameters(gameLevel, trials, gainVelocityControl, vibrotactileFeedback, auditoryFeedback);
        pongView.setOnBlockDoneListener(this);

		/*
         * NOTE: We get a reference to pongThread in onWindowFocusChanged, rather than here. This is
		 * necessary because the user might access the setup dialog before starting the game. If
		 * this happens, the pongThread instance is destroyed, and the reference is null. When the
		 * user returns from the setup dialog a new instance of pongThread is created (in PongView's
		 * surfaceCreated method). Since onWindowFocusChanged executes after PongView's
		 * onSurfaceCreated method, the reference to pongThread obtained in onWindowFocusChanged is
		 * always valid.
		 */

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sO = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sA = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sM = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // setup the sensor mode (see Demo TiltMeter API for discussion)
        if (sO != null)
        {
            sensorMode = ORIENTATION;
            sA = null;
            sM = null;
            Log.i(MYDEBUG, "DemoPongActivity onCreate! Sensor mode: ORIENTATION");
        } else if (sA != null && sM != null)
        {
            sensorMode = ACCELEROMETER_AND_MAGNETIC_FIELD;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_AND_MAGNETIC_FIELD");
        } else if (sA != null)
        {
            sensorMode = ACCELEROMETER_ONLY;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_ONLY");
        } else
        {
            Log.i(MYDEBUG, "Can't run demo.  Requires Orientation sensor or Accelerometer");
            this.onDestroy();
            this.finish();
        }

        timeTotal = 0f;

    } // end onCreate

    /**
     * Executed from onCreate and from onActivityResult.
     */
    public void loadSettings()
    {
        // initialize SharedPreferences instance
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        Resources r = this.getResources();
        participantCode = sp.getString(r.getString(R.string.settings_participant_key), "P99");
        conditionCode = sp.getString(r.getString(R.string.settings_condition_key), "C99");
        groupCode = sp.getString(r.getString(R.string.settings_group_key), "G99");
        gameLevel = Integer.parseInt(sp.getString(r.getString(R.string.settings_level_key), "1"));
        trials = Integer.parseInt(sp.getString(r.getString(R.string.settings_trials_key), "5"));
        sequencesMax = Integer.parseInt(sp.getString(r.getString(R.string.settings_sequences_key), "100"));
        tiltRange = Integer.parseInt(sp.getString(r.getString(R.string.settings_tilt_range_key), "50"));
        tiltCenter = Integer.parseInt(sp.getString(r.getString(R.string.settings_tilt_center_key), "35"));

        gainVelocityControl = sp.getFloat(r.getString(R.string.settings_gain_velocity_control_key), 1f);
        vibrotactileFeedback = sp.getBoolean(r.getString(R.string.settings_vibrotactile_feedback_key), true);
        auditoryFeedback = sp.getBoolean(r.getString(R.string.settings_auditory_feedback_key), true);
        disableDataCollection = sp.getBoolean(r.getString(R.string.settings_disable_data_collection_key), true);

        tiltMin = (int)(tiltCenter - (float)tiltRange / 2f + 0.5f);
        tiltMax = (int)(tiltCenter + (float)tiltRange / 2f + 0.5f);
        sequenceNumber = 0;
    }

    /*
     * Wait until the activity window has focus to get the reference to pongThread. See NOTE on
     * onCreate.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // Log.i(MYDEBUG, "onWindowFocusChanged! hasFocus=" + hasFocus);
        if (hasFocus)
        {
            pongThread = pongView.getThread();
        }
    }

    // callback for a click on an oval button
    @Override
    public void onOvalButtonClick(OvalButton ovalButton)
    {
        if (ovalButton == setupButton)
        {
            // launch the SettingsActivity to allow the user to change the app's settings
            Intent i = new Intent(getApplicationContext(), SetupActivity.class);
            startActivityForResult(i, SETTINGS); // see comment for onActivityResult
            return;

        } else if (ovalButton == tiltButtonPosition) // begin game in tilt mode (position control)
        {
            if (!disableDataCollection)
                doFileInitialization();
            paddleMode = TILT_POSITION;
            paddleModeString = "Tilt_Position";
            sensorManager.registerListener(this, sO, SENSOR_DELAY); // might be null, that's OK
            sensorManager.registerListener(this, sA, SENSOR_DELAY); // might be null, that's OK
            sensorManager.registerListener(this, sM, SENSOR_DELAY); // might be null, that's OK
            touchStrip.setOnTouchListener(null);
            // Note: sensor listeners unregistered in onPause

        } else if (ovalButton == tiltButtonVelocity) // begin game in tilt mode (velocity control)
        {
            if (!disableDataCollection)
                doFileInitialization();
            paddleMode = TILT_VELOCITY;
            paddleModeString = "Tilt_Velocity";
            sensorManager.registerListener(this, sO, SENSOR_DELAY); // might be null, that's OK
            sensorManager.registerListener(this, sA, SENSOR_DELAY); // might be null, that's OK
            sensorManager.registerListener(this, sM, SENSOR_DELAY); // might be null, that's OK
            touchStrip.setOnTouchListener(null);
            // Note: sensor listeners unregistered in onPause

        } else if (ovalButton == touchButtonPosition) // begin game in touch mode (position control)
        {
            if (!disableDataCollection)
                doFileInitialization();
            paddleMode = TOUCH_POSITION;
            paddleModeString = "Touch_Position";
            touchStrip.setVisibility(View.VISIBLE);
            touchStrip.setOnTouchListener(this);
            sensorManager.unregisterListener(this); // see also, onPause

        } else if (ovalButton == touchButtonVelocity) // begin game in touch mode (velocity control)
        {
            if (!disableDataCollection)
                doFileInitialization();
            paddleMode = TOUCH_VELOCITY;
            paddleModeString = "Touch_Velocity";
            touchStrip.setVisibility(View.VISIBLE);
            touchStrip.setOnTouchListener(this);
            sensorManager.unregisterListener(this); // see also, onPause

        } else
        {
            Log.i(MYDEBUG, "Oops! Invalid button.");
            this.finish();
        }

        buttonLayout.setVisibility(View.INVISIBLE);
        onceMoreBeforeExit = true;
        pongThread.setPaddleMode(paddleMode);
        pongThread.doStartGame(); // let the thread work its magic
    }

    public void doFileInitialization()
    {
        if (!disableDataCollection)
        {
            // ===================
            // File initialization
            // ===================

            // make a working directory to store sd1 and sd2 data files
            File dataDirectory = new File(Environment.getExternalStorageDirectory() + WORKING_DIRECTORY);
            if (!dataDirectory.exists() && !dataDirectory.mkdirs())
            {
                Log.i("MYDEBUG", "Failed to create directory: " + WORKING_DIRECTORY);
                this.finish(); // terminate
            }

            // initialize output files
            int blockNumber = 1;
            blockCode = "B01";
            base = "DemoPong-" + participantCode + "-" + conditionCode + "-" + groupCode + "-" + blockCode;

            f = new File(dataDirectory, base + ".csv");

			/*
             * Make sure the block code is unique (if not, increment block code and try again). This
			 * is an important step to ensure data are not over-written.
			 */
            while (f.exists())
            {
                ++blockNumber; // try the next sequential block code
                blockCode = String.format(Locale.CANADA, "%02d", blockNumber);
                base = "DemoPong-" + participantCode + "-" + conditionCode + "-" + groupCode + "-" + blockCode;
                f = new File(dataDirectory, base + ".csv");
            }

            try
            {
                bw = new BufferedWriter(new FileWriter(f));

                // output header line to data file
                bw.write(DATA_HEADER, 0, DATA_HEADER.length());
                bw.flush();

            } catch (IOException e)
            {
                Log.i("MYDEBUG", "Exception opening data file! e=" + e.toString());
                this.finish();
            }
        }
    }

    /*
     * We used start-for-result when launching the setup activity. So, we handle changes to the
     * settings here.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SETTINGS) // only possibility, but we'll check anyway
        {
            /*
             * Access to the setup dialog followed by clicking "OK" reloads the settings and
			 * reconfigures pongView with the new parameters.
			 */
            if (resultCode == SetupActivity.OK)
            {
                loadSettings();
                pongView.setParameters(gameLevel, trials, gainVelocityControl, vibrotactileFeedback, auditoryFeedback);
            }
        }
    }

    /*
     * Intercept presses of "Back" in the device's Navigation Bar.
     */
    @Override
    public void onBackPressed()
    {
        sensorManager.unregisterListener(this); // just in case we're in tilt mode
        if (pongThread.gameState == PongThread.RUNNING)
        {
            pongThread.doPauseGame();
            buttonLayout.setVisibility(View.VISIBLE);
            touchStrip.setVisibility(View.INVISIBLE);

        } else if (pongThread.gameState == PongThread.PAUSED && onceMoreBeforeExit)
        {
            onceMoreBeforeExit = false;
            Toast.makeText(this, "Press Back once more to exit", Toast.LENGTH_LONG).show();
            buttonLayout.setVisibility(View.VISIBLE);
            touchStrip.setVisibility(View.INVISIBLE);

        } else
            super.onBackPressed();
    }

    // process touch events (used for touch control of paddle)
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        switch (me.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN: // move the paddle
            case MotionEvent.ACTION_MOVE: // move the paddle
                pongThread.setPaddleYRatio(getRatio(me.getY(), 0.1f * touchStrip.getHeight(), 0.9f * touchStrip
                        .getHeight()));
                break;

            case MotionEvent.ACTION_UP: // stop moving the paddle
            case MotionEvent.ACTION_CANCEL: // stop moving the paddle
                pongThread.setPaddleYRatio(-1f); // use negative value for finger-up flag

        }
        return false; // let the TouchStrip also process the event
    }

    // process sensor events (used for tilt control of paddle)
    @Override
    public void onSensorChanged(SensorEvent se)
    {
        // ===============================
        // DETERMINE DEVICE PITCH AND ROLL
        // ===============================

        switch (sensorMode)
        {
            case ORIENTATION: // ========================================================

				/*
                 * Use this mode if the device has an orientation sensor.
				 */

                if (se.sensor.getType() != Sensor.TYPE_ORIENTATION)
                {
                    Log.i(MYDEBUG, "Oops! Sensor event from " + se.sensor.getName());
                    return;
                }

                // This bit of fiddling is necessary so the app will work on different devices.
                switch (defaultOrientation)
                {
                    case Configuration.ORIENTATION_PORTRAIT:
                    {
                        // e.g., Nexus 4
                        pitch = se.values[1];
                        roll = se.values[2];
                        break;
                    }
                    case Configuration.ORIENTATION_LANDSCAPE:
                    {
                        // e.g., Samsung Galaxy Tab 10.1
                        pitch = se.values[2];
                        roll = -se.values[1];
                        break;
                    }
                }
                break;

            case ACCELEROMETER_AND_MAGNETIC_FIELD: // ===================================

				/*
                 * Use this mode if the device has both an accelerometer and a magnetic field sensor
				 * (but no orientation sensor). See...
				 * 
				 * http://blog.thomnichols.org/2012/06/smoothing-sensor-data-part-2
				 */

                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accValues = lowPass(se.values.clone(), accValues, alpha); // filtered
                if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    magValues = lowPass(se.values.clone(), magValues, alpha); // filtered

                if (accValues != null && magValues != null)
                {
                    // compute pitch and roll
                    float R[] = new float[9];
                    float I[] = new float[9];
                    boolean success = SensorManager.getRotationMatrix(R, I, accValues, magValues);
                    if (success) // see SensorManager API
                    {
                        float[] orientation = new float[3];
                        SensorManager.getOrientation(R, orientation); // see getOrientation API
                        pitch = orientation[1] * RADIANS_TO_DEGREES;
                        roll = -orientation[2] * RADIANS_TO_DEGREES;
                    }
                }
                break;

            case ACCELEROMETER_ONLY: // =================================================

				/*
                 * Use this mode if the device has an accelerometer but no magnetic field sensor and
				 * no orientation sensor (e.g., HTC Desire C, Asus MeMOPad). This algorithm doesn't
				 * work quite as well, unfortunately. See...
				 * 
				 * http://www.hobbytronics.co.uk/accelerometer-info
				 */

                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accValues = lowPass(se.values.clone(), accValues, alpha);

                x = accValues[0];
                y = accValues[1];
                z = accValues[2];
                pitch = -(float)Math.atan(y / Math.sqrt(x * x + z * z)) * RADIANS_TO_DEGREES;
                roll = (float)Math.atan(x / Math.sqrt(y * y + z * z)) * RADIANS_TO_DEGREES;
                break;
        }

        if (pongThread != null)
        {
            // move the paddle
            pongThread.setPaddleYRatio(getRatio(roll, tiltMin, tiltMax));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // not used but we need to provide an implementation anyway
    }

    /*
     * Convert a value to a ratio within a min/max range. If the value <= min, 0 is returned. If the
     * value >= max, 1 is returned. If the value is between min and max, the ratio is returned.
     */
    public float getRatio(float value, float min, float max)
    {
        return Math.min(Math.max(0, (value - min) / (max - min)), 1f);
    }

    /*
     * Get the default orientation of the device. This is needed to correctly map the sensor data
     * for pitch and roll (see onSensorChanged).
     */
    public int getDefaultDeviceOrientation()
    {
        WindowManager wm = (WindowManager)getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = wm.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation ==
                Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation ==
                Configuration.ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else
            return Configuration.ORIENTATION_PORTRAIT;
    }

    // invoked when the activity loses user focus
    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this); // just in case we're in tilt mode

        if (bw != null && !disableDataCollection)
        {
            // close output data file
            try
            {
                bw.close();

				/*
                 * Make the saved data files visible in Windows Explorer. There seems to be bug
				 * doing this with Android 4.4. I'm using the following code, instead of
				 * sendBroadcast. See...
				 * 
				 * http://code.google.com/p/android/issues/detail?id=38282
				 */
                MediaScannerConnection.scanFile(this, new String[] {f.getAbsolutePath()}, null, null);

            } catch (IOException e)
            {
                Log.i(MYDEBUG, "Exception closing file! e=" + e);
            }
        }
    }

    // see Demo Sensors for discussion
    @Override
    protected void onResume()
    {
        super.onResume();
        if (paddleMode == TILT_POSITION)
        {
            sensorManager.registerListener(this, sO, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sA, SensorManager.SENSOR_DELAY_GAME);
            sensorManager.registerListener(this, sM, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    // see Demo TiltMeter for discussion
    protected float[] lowPass(float[] input, float[] output, float alpha)
    {
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + alpha * (input[i] - output[i]);
        return output;
    }

    // Show the results dialog. See Demo Settings for comments.
    private void showResultsDialog()
    {
        // create an instance of the dialog fragment (passing in the data we want to show)
        resultsDialog = ResultsDialog.newInstance(pongThread.hits, pongThread.misses, pongView.gameLevel, trials,
                pongThread.gameState);

        // show the dialog
        resultsDialog.show(getFragmentManager(), "ResultsDialogFragment");
    }

    // Show the thank-you dialog. See Demo Settings for comments.
    private void showThankYouDialog()
    {
        // create an instance of the dialog fragment (passing in the data we want to show)
        thankYouDialog = ThankYouDialog.newInstance();

        // show the dialog
        thankYouDialog.show(getFragmentManager(), "ThankYouDialogFragment");
    }

    @Override
    public void onResultsDialogClick(int buttonId)
    {
        // only one button, but check anyway
        if (buttonId == DialogInterface.BUTTON_POSITIVE) // "continue"
        {
            // advance to the next game level if there were zero misses
            if (pongThread.misses == 0 && pongView.gameLevel < TOP_GAME_LEVEL)
                pongView.nextGameLevel();

                // go back to the previous game level if there were > 2 misses
            else if (pongThread.misses > 2 && pongView.gameLevel > 1)
                pongView.previousGameLevel();

            if (pongThread.isVictory() || sequenceNumber == sequencesMax)
                showThankYouDialog(); // we're done!

            else
            {
                pongThread.initGame();
                pongThread.doStartGame();
            }
        }
        resultsDialog.dismiss();
    }

    @Override
    public void onThankYouDialogClick()
    {
        thankYouDialog.dismiss();
        this.finish();
    }

    /*
     * The pongThread uses this callback to let us know that a block of trials is done.
     */
    public void onBlockDone()
    {
        ++sequenceNumber;
        showResultsDialog();

        if (bw != null && !disableDataCollection)
        {
            // compute total time (will increase with each block)
            timeTotal += pongThread.elapsedTime;
            String timeTotalString = String.format(Locale.CANADA, "%.1f", (timeTotal / 1000f));

            // compute the efficiency of paddle movement
            String efficiency;
            if (pongThread.paddleMovementActual == 0f)
                efficiency = "-1";
            else
                efficiency = String.format(Locale.CANADA, "%.1f", (float)pongThread.paddleMovementMin
                        / pongThread.paddleMovementActual * 100f);

            // build a string for the result status after a block of trials
            String result;
            if (pongThread.gameState == PongThread.WIN)
                result = "Win";
            else if (pongThread.gameState == PongThread.RETRY)
                result = "Retry";
            else if (pongThread.gameState == PongThread.BACK_ONE_LEVEL)
                result = "Back";
            else if (pongThread.gameState == PongThread.VICTORY)
                result = "Victory";
            else
                result = "" + pongThread.gameState;

            // build the results string (for saving in the data file)
            String results = participantCode + "," + conditionCode + "," + groupCode + "," + blockCode + ","
                    + sequenceNumber + "," + pongView.gameLevel + "," + trials + "," + pongThread.hits + ","
                    + pongThread.misses + "," + result + "," + (pongThread.elapsedTime / 1000f) + "," + timeTotalString
                    + "," + pongThread.paddleMovementMin + "," + pongThread.paddleMovementActual + "," + efficiency
                    + "," + pongThread.paddleReversals + "\n";
            try
            {
                bw.write(results, 0, results.length());
                bw.flush();
            } catch (IOException e)
            {
                Log.i(MYDEBUG, "Exception writing to data file! e=" + e);
            }
        }
    }
}
