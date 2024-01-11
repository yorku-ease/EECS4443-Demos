package ca.yorku.eecs.mack.demotiltball;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

/**
 * Demo_TiltBall - demonstrate moving a virtual ball on a display using the tilt of the device <p>
 *
 * The most obvious application of device tilt is games. However, tilt may also be used as a general purpose input
 * technique for selecting targets. The virtual ball is somewhat like a cursor. A target, such as an application icon or
 * a soft keyboard key, may be selected by manoeuvring the ball to the target and holding the ball inside the target for
 * a prescribed interval, such as 500 ms. An investigation of this is described in MacKenzie and Teather's <a
 * href="http://www.yorku.ca/mack/nordichi2012.html"><i>FittsTilt: The Application of Fitts' Law to Tilt-based
 * Interaction</i></a>. <p>
 *
 * This demo begins by presenting the user with a setup dialog containing four spinners: <p>
 *
 * <center> <a href="./javadoc_images/DemoTiltBall-1.jpg"><img src="./javadoc_images/DemoTiltBall-1.jpg" width = "600" alt="image"></a> </center> <p>
 *
 * Four interaction parameters may be selected: <p>
 *
 * <ul> <li><b>Order of control</b> - The options are <i>Velocity</i> or <i>Position</i>. With velocity control, the
 * ball's velocity is controlled by the tilt of the device. With position control, there is a direct mapping between
 * device tilt and the position of the ball. <p>
 *
 * <li><b>Gain</b> - The options are <i>Very low</i>, <i>Low</i>, <i>Medium</i>, <i>High</i>, or <i>Very high</i>. The
 * lower the gain setting, the greater the amount of tilt required to effect a change in the ball's velocity or
 * position. <p>
 *
 * <li><b>Path type </b> - The options are <i>Square</i>, <i>Circle</i>, or <i>Free</i>. For the Square or Circle
 * options, the user is presented with either a square or circular path. The idea is to control the ball's position on
 * the screen and move it around the path while staying within the path. For the Free option, no path is presented. The
 * user may freely move the ball about. <p>
 *
 * <li><b>Path width</b> - The options are <i>Narrow</i>, <i>Medium</i>, or <i>Wide</i>. For the square or circular
 * paths, this option sets the width of the path. The chosen option yields a path width that is 2&times;, 4&times;, or
 * 8&times;, the ball diameter, respectively. Obviously, it gets easier to move the ball around the path as the path
 * gets wider. </ul> <p>
 *
 * Once the interaction parameters are selected, the user taps "OK" to proceed to the main activity. The following
 * screen snaps show the square paths for each of the three widths. The virtual ball is seen within the path. <p>
 *
 * <center> <a href="./javadoc_images/DemoTiltBall-2.jpg"><img src="./javadoc_images/DemoTiltBall-2.jpg" width = "300"
 * alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="./javadoc_images/DemoTiltBall-3.jpg"><img src="./javadoc_images/DemoTiltBall-3.jpg" width =
 * "300" alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="./javadoc_images/DemoTiltBall-4.jpg"><img src="./javadoc_images/DemoTiltBall-4.jpg" width
 * = "300" alt="image"></a> </center> <p>
 *
 * Screen snaps of the free movement option and the circular path (medium width) are shown below. <p>
 *
 * <center> <a href="./javadoc_images/DemoTiltBall-5.jpg"><img src="./javadoc_images/DemoTiltBall-5.jpg" width = "300"
 * alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="./javadoc_images/DemoTiltBall-6.jpg"><img src="./javadoc_images/DemoTiltBall-6.jpg" width =
 * "300" alt="image"></a> </center> <p>
 *
 * <b>Human Performance</b><p> This is our first demo where issues of human performance arise. Each of the four
 * interaction parameters in the setup dialog is likely to impact a user's ability to manoeuvre the ball along the
 * path. Consider the following questions: <p>
 *
 * <ul> <li>Is the ball easier to control using velocity-control or position-control? <li>Which gain setting is better
 * for controlling the ball? <li>Is it harder to move the ball around the circular path or the square path? How much
 * harder? <li>Does the difference between the circular and square path depend on the order of control? <li>What is the
 * effect of path width on the user's ability to control and move the ball? </ul> <p>
 *
 * These are interesting questions &mdash; questions suitable for empirical enquiry. In fact, each interaction parameter
 * spoken of in these questions is a potential <i>independent variable</i> suitable for experimental testing with users
 * ("participants"). The counterpart to an independent variable is a <i>dependent variable</i> &mdash; the observed and
 * measured human response representing human performance. In fact, the screen snaps above include such a dependent
 * variable. For the square or circular path types, the demo records and outputs the number of "Wall hits" (see above).
 * Obviously, fewer wall hits is associated with better performance. Besides wall hits, we could measure the percentage
 * of time inside the path vs. outside the path, the spatial variability in ball movement, or the time for each lap
 * around the path. The measurements are dependent variables representing the speed and accuracy of the user performing
 * the task of moving the ball around the path. More complete details of experiment design, independent variables,
 * dependent variables, etc., are found in Chapter 5 in MacKenzie's
 * <a href="http://www.yorku.ca/mack/HCIbook/">Human-Computer Interaction: An Empirical Research Perspective</a>. <p>
 *
 * <b>Calculation of Wall Hits</b> <p>
 *
 * When the ball hits either the inner or outer path wall, a vibrotactile pulse is emitted and a wall-hit count is
 * incremented.  The calculation is a bit tricky, since we need to detect when the ball <i>first</i> touches the line.
 * Here's an explanation of how this is done.  We'll use the outer wall of the rectangular path as an example.  First,
 * the idea of a "shadow rectangle" is introduced.  The shadow rectangle is not visible in the UI.  The outer shadow
 * rectangle fits inside the outer rectangle and is separated from the outer rectangle by the diameter of the ball.  See
 * below (click to enlarge). <p>
 *
 * <center> <a href="./javadoc_images/DemoTiltBall-11.jpg"><img src="./javadoc_images/DemoTiltBall-11.jpg" width=400 alt="image"></a> </center> <p>
 *
 * A custom method called <code>ballTouchingWall</code> is defined in <code>RollingBallPanel.java</code>. The method
 * returns <code>true</code> if the ball is touching the line, <code>false</code> otherwise.  The
 * <code>ballTouchingLine</code> method uses the <code>intersects</code> method of the <code>RectF</code> class.
 * <code>intersects</code> is a static method that takes two rectangles as arguments. It returns <code>true</code> if
 * the two rectangles intersect, <code>false</code> otherwise. Here's the critical code snippet that detects if the ball
 * is touching the line:<p>
 *
 * <pre>
 *      if (RectF.intersects(ballNow, outerRectangle) && !RectF.intersects(ballNow, outerShadowRectangle))
 *           return true; // touching outside rectangular border
 *      ...
 *      return false;
 * </pre>
 * <p>
 *
 * The argument <code>ballNow</code> is simply a rectangle that encompasses the ball at its current position.  The
 * following four diagrams illustrate how this code detects if the ball is touching the line.  In (a), the ball is fully
 * within both the outer rectangle and the outer shadow rectangle.  However, the ball is <i>not</i> touching the line.
 * In the if-statement above, the first <code>intersects</code> returns <code>true</code> because the ball is
 * intersecting the outer rectangle.  The second <code>intersects</code> also returns <code>true</code> because the ball
 * is also intersecting the inner shadow rectangle.  However, the second <code>intersects</code> is prefaced with
 * <code>!</code> (NOT), so the overall result of the conditional expression is <code>false</code>: the ball is not
 * touching the line (see diagram).<p>
 *
 * <center> (a)<a href="./javadoc_images/DemoTiltBall-7.jpg"><img src="./javadoc_images/DemoTiltBall-7.jpg" width=200 alt="image"></a> (b)<a
 * href="./javadoc_images/DemoTiltBall-8.jpg"><img src="./javadoc_images/DemoTiltBall-8.jpg" width=200 alt="image"></a> (c)<a
 * href="./javadoc_images/DemoTiltBall-9.jpg"><img src="./javadoc_images/DemoTiltBall-9.jpg" width=200 alt="image"></a> (d)<a
 * href="./javadoc_images/DemoTiltBall-10.jpg"><img src="./javadoc_images/DemoTiltBall-10.jpg" width=200 alt="image"></a>
 *
 * </center> <p>
 *
 * In (b), the ball is moving toward the outer wall.  It is partially outside the inner shadow rectangle, but it is
 * still intersecting both rectangles.  The effect is the same.  In (c), the ball is touching the line of the outer
 * rectangle.  At this juncture, the ball <i>is</i> intersecting the outer rectangle but <i>is not</i> intersecting the
 * outer shadow rectangle.  The conditional expression in the if-statement is <code>true</code>, correctly indicating
 * that the ball is touching the line.  In (d), the ball has moved outside the outer rectangle and is intersecting
 * neither the outer rectangle nor the outer shadow rectangle.  The conditional expression is <code>false</code>,
 * indicating the the ball is no longer touching line.<p>
 *
 * The code in <code>ballTouchingLine</code> is a bit more involved, since it deals with both the inner and outer
 * rectangles and with inner and outer circles (if the app is operating with a circular path).  There is also the issue
 * of detecting and reacting to only the <i>first</i> wall hit.  To see how this is achieved, let's consider the code
 * that calls <code>ballTouchingLine</code>.  There a method in <code>RollingBallPanel.java</code> called
 * <code>updateBallPosition</code>.  This method is called from the main activity to pass along the sensor data so that
 * the ball position can be updated based on device tilt.  The method's primary focus is to calculate the ball's new
 * <i>x-y</i> coordinates based on <code>tiltMagnitude</code> and <code>tiltAngle</code> in combination with the order
 * of control and gain settings that were selected when the app was launched.  With the new <i>x-y</i> coordinates
 * available, <code>updateBallPosition</code> also determines if the ball's new position generates a wall hit.
 * Remember, we need to detect the <i>first</i> wall hit.  Just knowing that the ball is touching the inner or outer
 * path line is not sufficient. <p>
 *
 * To identify the first wall hit, a boolean flag called <code>touchFlag</code> is used.  The flag is initially
 * <code>false</code>.  It is set when <code>ballTouchingLine</code> returns <code>true</code> and cleared when
 * <code>ballTouchingLine</code> returns <code>false</code>.  Using the flag is simple. The vibrotactile pulse and the
 * increment of
 * <code>wallHits</code> only occur when <code>touchFlag</code> transitions from <code>false</code> to
 * <code>true</code>. Here's the relevant code (from <code>updateBallPosition</code>): <p>
 *
 * <pre>
 *      int wallHits = 0;
 *      boolean touchFlag = false;
 *      ...
 *      // if ball touches wall, vibrate and increment wallHits count
 *      if (ballTouchingLine() && !touchFlag)
 *      {
 *           touchFlag = true; // the ball has *just* touched the line: set the touchFlag
 *           vib.vibrate(20); // 20 ms vibrotactile pulse
 *           ++wallHits;
 *
 *      } else if (!ballTouchingLine() && touchFlag)
 *           touchFlag = false; // the ball is no longer touching the line: clear the touchFlag
 * </pre>
 * <p>
 *
 * And that's about it.  There is just one additional small detail to consider.  Generating the vibrotactile plus and
 * incrementing <code>wallHits</code> occurs both when the ball hits a path wall travelling from inside the path to
 * outside the path and when the ball hits a path wall travelling from outside the path to inside the path.  While
 * that's fine for the demo, in most cases it's more relevant to generate the vibrotactile pulse and increment
 * <code>wallHits</code> just once for this sort of ball movement.  In other words, only one wall hit is logged if the
 * ball transitions from inside the path to outside the path and then returns to a position inside the path. We'll let
 * you consider how to introduce this modification. <p>
 *
 * <b>Programming Notes</b><p> The use of the Android device's sensors for providing data on device tilt is the same as
 * in Demo_TiltMeter. Consult the API notes for Demo_TiltMeter (and Demo_Sensors) for further details. <p>
 *
 * The demo uses vibration.  Generating a vibrotactile pulse on an Android device is easy. A view may instantiate a <a
 * href="http://developer.android.com/reference/android/os/Vibrator.html"> <code>Vibrator</code></a> object as follows:
 * (<code>c</code> is the context for the view.)<p>
 *
 * <pre>
 *      Vibrator vib = (Vibrator)c.getSystemService(Context.VIBRATOR_SERVICE);
 * </pre>
 *
 * With this, a 20-ms pulse is output using <p>
 *
 * <pre>
 *      vib.vibrate(20);
 * </pre>
 * <p>
 *
 * Using the device's vibrator requires the following <code>uses-permission</code> element in the manifest file: <p>
 *
 * <pre>
 *     &lt;uses-permission android:name="android.permission.VIBRATE" &gt;
 *     &lt;/uses-permission&gt;
 * </pre>
 * <p>
 *
 * Consult this project's <code>AndroidManifest.xml</code> file for further details. <p>
 *
 * In Demo_TiltMeter and Demo_Sensors, the sampling rate for the device's sensors was selectable through a spinner in
 * the setup dialog. In this demo, the fastest setting is used. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2022
 */
public class DemoTiltBallActivity extends Activity implements SensorEventListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int REFRESH_INTERVAL = 20; // milliseconds (screen refreshes @ 50 Hz)

    // int constants to setup a mode (see DemoTiltMeter API for discussion)
    final static int ORIENTATION = 0;
    final static int ACCELEROMETER_ONLY = 1;
    final static int ACCELEROMETER_AND_MAGNETIC_FIELD = 2;
    final float RADIANS_TO_DEGREES = 57.2957795f;

    /*
     * Below are the alpha values for the low-pass filter. The four values are for the slowest
     * (NORMAL) to fastest (FASTEST) sampling rates, respectively. These values were determined by
     * trial and error. There is a trade-off. Generally, lower values produce smooth but sluggish
     * responses, while higher values produced jerky but fast responses.
     *
     * Furthermore, there is a difference by device, particularly for the FASTEST setting. For
     * example, the FASTEST sample rate is about 200 Hz on a Nexus 4 but only about 100 Hz on a
     * Samsung Galaxy Tab 10.1.
     *
     * Fiddle with these, as necessary.
     */
    final float[] ALPHA_VELOCITY = {0.99f, 0.80f, 0.40f, 0.15f};
    final float[] ALPHA_POSITION = {0.50f, 0.30f, 0.15f, 0.10f};
    float alpha;

    RollingBallPanel rb;
    int sensorMode;
    float[] accValues = new float[3];
    float[] magValues = new float[3];
    float x, y, z, pitch, roll;

    // parameters from the Setup dialog
    String orderOfControl, pathType, pathWidth;
    int gain;

    int defaultOrientation;
    ScreenRefreshTimer refreshScreen;
    private SensorManager sm;
    private Sensor sA, sM, sO;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        Log.i(MYDEBUG, "Got here! (DemoTiltBallActivity - onCreate)");
        
        setContentView(R.layout.main);

        // get parameters selected by user from setup dialog
        Bundle b = getIntent().getExtras();
        orderOfControl = b.getString("orderOfControl");
        gain = b.getInt("gain");
        pathType = b.getString("pathType");
        pathWidth = b.getString("pathWidth");

        // set alpha for low-pass filter (based on sampling rate and order of control)
        if (orderOfControl.equals("Velocity")) // velocity control
            alpha = ALPHA_VELOCITY[2]; // for GAME sampling rate
        else
            // position control
            alpha = ALPHA_POSITION[2]; // for GAME sampling rate

        // get this device's default orientation
        defaultOrientation = getDefaultDeviceOrientation();

        // force the UI to appear in the device's default orientation (and stay that way)
        if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // configure rolling ball panel, as per setup parameters
        rb = (RollingBallPanel)findViewById(R.id.rollingballpanel);
        rb.configure(pathType, pathWidth, gain, orderOfControl);

        // get sensors
        sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sO = sm.getDefaultSensor(Sensor.TYPE_ORIENTATION); // supported on many devices
        sA = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // supported on most devices
        sM = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // null on many devices

        // setup the sensor mode (see API for discussion)
        if (sO != null)
        {
            sensorMode = ORIENTATION;
            sA = null;
            sM = null;
            Log.i(MYDEBUG, "Sensor mode: ORIENTATION");
        } else if (sA != null && sM != null)
        {
            sensorMode = ACCELEROMETER_AND_MAGNETIC_FIELD;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_AND_MAGNETIC_FIELD");
        } else if (sA != null)
        {
            sensorMode = ACCELEROMETER_ONLY;
            sO = null;
            sM = null;
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_ONLY");
        } else
        {
            Log.i(MYDEBUG, "Can't run demo.  Requires Orientation sensor or Accelerometer");
            this.finish();
        }

        // on my umidigi device, orientation doesn't seem to work very well, switch mode
//        sensorMode = ACCELEROMETER_AND_MAGNETIC_FIELD;
//        sO = null;
//        sA = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // supported on most devices
//        sM = sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // null on many devices

        // NOTE: sensor listeners are registered in onResume

        // setup the screen refresh timer (updates every REFRESH_INTERVAL milliseconds)
        refreshScreen = new ScreenRefreshTimer(REFRESH_INTERVAL, REFRESH_INTERVAL);
        refreshScreen.start();
    }

    /*
     * Get the default orientation of the device. This is needed to correctly map the sensor data
     * for pitch and roll (see onSensorChanged). See...
     *
     * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on-
     * android-i-e-get-landscape
     */
    public int getDefaultDeviceOrientation()
    {
        WindowManager windowManager = (WindowManager)getSystemService(WINDOW_SERVICE);
        Configuration config = getResources().getConfiguration();
        int rotation = windowManager.getDefaultDisplay().getRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && config.orientation ==
                Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) && config.orientation ==
                Configuration.ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else
            return Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        sm.registerListener(this, sO, SensorManager.SENSOR_DELAY_GAME); // good enough!
        sm.registerListener(this, sA, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(this, sM, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sm.unregisterListener(this);
    }

    /*
     * Cancel the timer when the activity is stopped. If we don't do this, the timer continues after
     * the activity finishes. See...
     *
     * http://stackoverflow.com/questions/15144232/countdowntimer-continues-to-tick-in-background-how
     * -do-i-retrieve-that-count-in
     */
    @Override
    public void onStop()
    {
        refreshScreen.cancel();
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // not needed, but we need to provide an implementation anyway
    }

    @Override
    public void onSensorChanged(SensorEvent se)
    {
        // =======================================================
        // DETERMINE DEVICE PITCH AND ROLL (VARIES BY SENSOR MODE)
        // =======================================================

        switch (sensorMode)
        {
            // ---------------------------------------------------------------------------------------------
            case ORIENTATION:
                pitch = se.values[1];
                roll = se.values[2];
                break;

            // ---------------------------------------------------------------------------------------------
            case ACCELEROMETER_AND_MAGNETIC_FIELD:
                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                    accValues = lowPass(se.values.clone(), accValues, alpha); // filtered
                if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                    magValues = lowPass(se.values.clone(), magValues, alpha); // filtered

                if (accValues != null && magValues != null)
                {
                    // compute pitch and roll
                    float[] R = new float[9];
                    float[] I = new float[9];
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

            // ---------------------------------------------------------------------------------------------
            case ACCELEROMETER_ONLY:

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
    }

    /*
     * Low pass filter. The algorithm requires tracking only two numbers - the prior number and the
     * new number. There is a time constant "alpha" which determines the amount of smoothing. Alpha
     * is like a "weight" or "momentum". It determines the effect of the new value on the current
     * smoothed value. A lower alpha means more smoothing.
     *
     * NOTE: 0 <= alpha <= 1.
     *
     * See...
     *
     * http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter
     */
    protected float[] lowPass(float[] input, float[] output, float alpha)
    {
        for (int i = 0; i < input.length; i++)
            output[i] = output[i] + alpha * (input[i] - output[i]);
        return output;
    }

    /*
     * Screen updates are initiated in onFinish which executes every REFRESH_INTERVAL milliseconds
     */
    private class ScreenRefreshTimer extends CountDownTimer
    {
        ScreenRefreshTimer(long millisInFuture, long countDownInterval)
        {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished)
        {
        }

        @Override
        public void onFinish()
        {
            float tiltMagnitude = (float)Math.sqrt(pitch * pitch + roll * roll);
            float tiltAngle = tiltMagnitude == 0f ? 0f : (float)Math.asin(roll / tiltMagnitude) * RADIANS_TO_DEGREES;

            if (pitch > 0 && roll > 0)
                tiltAngle = 360f - tiltAngle;
            else if (pitch > 0 && roll < 0)
                tiltAngle = -tiltAngle;
            else if (pitch < 0 && roll > 0)
                tiltAngle = tiltAngle + 180f;
            else if (pitch < 0 && roll < 0)
                tiltAngle = tiltAngle + 180f;

            rb.updateBallPosition(pitch, roll, tiltAngle, tiltMagnitude); // will invalidate ball panel
            this.start();
        }
    }
}
