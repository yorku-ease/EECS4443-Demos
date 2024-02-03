package ca.yorku.eecs.mack.demotiltmeter;

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
 * <style> pre {font-size:110%} </style>
 * <p>
 * Demo_TiltMeter - demo application that displays a tilt meter. </p>
 *
 * Related information: </p>
 *
 * <blockquote> See Demo_Sensors for topics and links.</blockquote> </p>
 *
 * This demo uses the Android sensors to determine the angle and magnitude of device tilt relative to a reference line
 * on a flat surface. The tilt angle and tilt magnitude are displayed as a needle in a meter. </p>
 *
 * Upon launch, the user is presented with a setup dialog.  See below.  Radio buttons are used to set the sensor mode
 * (described below).  A spinner is used to set the sensor sampling rate (as in Demo_Sensors).<p>
 *
 * <center><a href="./javadoc_images/DemoTiltMeter-1.jpg"><img src="./javadoc_images/DemoTiltMeter-1.jpg" width="250"></a></center> </p>
 *
 * After choosing a sampling rate and sensor mode, the main activity begins when the user taps "OK". The tilt meter
 * appears with the needle indicating the position of the device relative to a flat surface (below left). </p>
 *
 * <center><a href="./javadoc_images/DemoTiltMeter-3.jpg"><img src="./javadoc_images/DemoTiltMeter-3.jpg" width="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
 * href="./javadoc_images/DemoTiltMeter-7.jpg"><img src="./javadoc_images/DemoTiltMeter-7.jpg" width="300"></a></center> </p>
 *
 * The meter is an instance of <code>TiltMeter</code>, a custom-designed class, sub-classed from <code>View</code>. The
 * UI also outputs the device pitch and roll (as reported by the sensors) and the overall tilt angle and tilt magnitude
 * (discussed below). Compare the measures reported above (image on right) with the magnitude and position of the needle
 * in the tilt meter. From this information, we can infer the position of the device relative to a flat surface. </p>
 *
 * As the user moves the device about, the magnitude and direction of the meter's needle are adjusted to correspond to
 * the device's tilt angle and tilt magnitude. Some examples follow. </p>
 *
 * <center> <a href="./javadoc_images/DemoTiltMeter-4.jpg"><img src="./javadoc_images/DemoTiltMeter-4.jpg" height="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="./javadoc_images/DemoTiltMeter-5.jpg"><img src="./javadoc_images/DemoTiltMeter-5.jpg" height="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoTiltMeter-6.jpg"><img src="./javadoc_images/DemoTiltMeter-6.jpg" height="250"></a> </center> </p>
 *
 * The device's rotation about the <i>x</i>-axis (pitch) and the <i>y</i>-axis (roll) are available from the device
 * sensors. For this demo, we need to aggregate these measures into the device's overall tilt angle and tilt magnitude.
 * Tilt angle (<code>tiltAngle</code>) is the overall angle of device relative to an arbitrary reference line on the
 * device, such as "straight ahead". Tilt magnitude (<code>tiltMagnitude</code>) is the angle of device tilt relative to
 * a flat surface, measured at the reference line. These are computed as follows: </p>
 *
 * <pre>
 *      float tiltAngle = tiltMagnitude == 0f ? 0f : (float)Math.asin(roll / tiltMagnitude) * RADIANS_TO_DEGREES;
 *      float tiltMagnitude = (float)Math.sqrt(pitch * pitch + roll * roll);
 * </pre>
 *
 * The following additional explanation may help in understanding the math above.  First, think of tilting a
 * surface and then rolling a ball down the surface.  Consider the line formed by the path of the ball.  If you LOOK
 * FROM ABOVE and have a reference line for 0 degrees (e.g., straight ahead), then <code>tiltAngle</code> is the angle
 * between the reference line and the line corresponding to the path of the ball.  On the other hand, if you LOOK
 * FROM THE SIDE, <code>tiltMagnitude</code> is the angle between the flat surface and the line corresponding to the
 * path of the ball.
 * <p>
 *
 * This demo was originally developed using Android's <code>TYPE_ORIENTATION</code> sensor. This sensor provides exactly
 * the data needed: the pitch and roll of the device relative to a flat surface.  However, you will notice in Android
 * Studio that this sensor is flagged as deprecated: </p>
 *
 *
 * <center><img src="./javadoc_images/DemoTiltMeter-9.jpg" alt="image"></center> </p>
 *
 * Yes, the <code>TYPE_ORIENTATION</code> sensor was deprecated with Android API level 8. In the
 * <code>SensorEvent</code> API under <code>TYPE_ORIENTATION</code>, we are told,
 *
 * <blockquote><i> This sensor type exists for legacy reasons, please use <code>getRotationMatrix()</code> in
 * conjunction with <code>remapCoordinateSystem()</code> and <code>getOrientation()</code> to compute these values
 * instead. </i></blockquote> </p>
 *
 * That's fine, but this advice can only be followed if the device has a <code>TYPE_ACCELEROMETER</code> sensor and
 * possibly a <code>TYPE_MAGNETIC_FIELD</code> sensor. However, these sensors are not available on all Android devices.
 * To accommodate this, the demo includes three <i>sensor modes</i> (see table below).  Experience suggests that
 * <code>TYPE_ORIENTATION</code> works pretty good as the data are stable and do not require filtering or smoothing.  As
 * a suggestion, try each mode (if possible) and see if the movement of the meter's needle is smooth as the device is
 * moved about. The following summarizes the sensor modes used in the demo. </p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Sensor Mode <th>Sensor's
 * Used <th>This Mode Is Used If... <th>Example Devices
 * <p>
 * <tr> <td><font size="-1">ORIENTATION</font> <td><code>TYPE_ORIENTATION</code> <td>the device has an orientation
 * sensor <td>LG <i>Nexus 4</i>, Samsung <i>Galaxy Tab 10.1</i>
 * <p>
 * <tr> <td><font size="-1">ACCELEROMETER_AND_MAGNETIC_FIELD</font> <td><code>TYPE_ACCELEROMETER</code> and<br>
 * <code>TYPE_MAGNETIC_FIELD</code> <td>the device does not have an orientation sensor but has both an accelerometer and
 * a magnetic field sensor <td align="center">?
 * <p>
 * <tr> <td><font size="-1">ACCELEROMETER_ONLY</font> <td><code>TYPE_ACCELEROMETER</code> <td>the device has an
 * accelerometer but no orientation sensor and no magnetic field sensor <td>HTC <i>Desire C</i>, Asus <i>MeMO Pad</i>
 * </table>
 * </blockquote> </p>
 *
 * Data smoothing is required when using the <code>TYPE_ACCELEROMETER</code> sensor. Smoothing is performed by passing
 * the accelerometer data through a low-pass filter.  The filtering code is contained in a simple method called
 * <code>lowPass</code>, adopted from a blog post by Thom Nichols
 * (<a href="http://blog.thomnichols.org/2011/08/smoothing-sensor-data-with-a-low-pass-filter">click here</a>). <p>
 *
 * This demo was designed to conform to the following advice given in the Sensors Overview API Guide:<p>
 *
 * <center><img src="./javadoc_images/DemoTiltMeter-8.jpg" alt="image"></center> </p>
 *
 * In view of this, we separate the input of sensor data (in <code>onSensorChanged</code>) from the processing of sensor
 * data. In <code>onSensorChanged</code>, as little data processing as possible occurs. The processing of sensor data
 * and updating the UI's view meters is handled separately: using a <code>ScreenRefreshTimer</code>, an inner class
 * which is sub-classed from <code>CountDownTimer</code>. The timer is setup to timeout every 50 ms (corresponding to a
 * refresh rate of 20 Hz). In the timer's <code>onFinish</code> method, the pitch and roll data from the sensors are
 * used to compute the device's tilt magnitude and tilt angle (see code above).  These data are then passed to
 * TiltMeter to update the angle and length of the meter's needle. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2022
 */
public class DemoTiltMeterActivity extends Activity implements SensorEventListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int REFRESH_INTERVAL = 50; // milliseconds (screen refreshes @ 20 Hz)
    final static float RADIANS_TO_DEGREES = 57.2957795f;

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
    final float[] ALPHA_ARRAY = {0.5f, 0.3f, 0.15f, 0.06f};
    TiltMeter tiltMeter;
    float alpha;
    int defaultOrientation;
    int sensorMode;

    SensorManager sensorManager;
    Sensor sA, sO, sM;

    float[] accValues = new float[3]; // smoothed values from accelerometer
    float[] magValues = new float[3]; // smoothed values from magnetic field sensor

    float x, y, z, pitch, roll;
    int samplingRate;

    ScreenRefreshTimer refreshScreen;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    private void initialize()
    {
        Bundle b = getIntent().getExtras();
        assert b != null;
        samplingRate = b.getInt("sensorSamplingRate");
        sensorMode = b.getInt("sensorMode");

        // set the time constant for the low-pass filter
        if (samplingRate == SensorManager.SENSOR_DELAY_NORMAL)
            alpha = ALPHA_ARRAY[0];
        else if (samplingRate == SensorManager.SENSOR_DELAY_UI)
            alpha = ALPHA_ARRAY[1];
        else if (samplingRate == SensorManager.SENSOR_DELAY_GAME)
            alpha = ALPHA_ARRAY[2];
        else if (samplingRate == SensorManager.SENSOR_DELAY_FASTEST)
            alpha = ALPHA_ARRAY[3];

        // get this device's default orientation
        defaultOrientation = getDefaultDeviceOrientation();

        // force the meter to appear in the device's default orientation (and stay that way)
        if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // get a reference to our tilt meter
        tiltMeter = (TiltMeter)findViewById(R.id.titlmeter);

        // attempt to initialize the three possible sensors that might be used (some of these might be null)
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sO = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // supported on most Android devices
        sA = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // null on HTC Desire C, Asus MeMO Pad
        sM = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // null on HTC Desire C, Asus MeMO Pad

        // output a logcat message indicating the sensor mode and set un-needed sensors to null
        if (sensorMode == DemoTiltMeterSetup.ORIENTATION)
        {
            Log.i(MYDEBUG, "Sensor mode: Orientation");
            sA = null;
            sM = null;
        } else if (sensorMode == DemoTiltMeterSetup.ACCELEROMETER_AND_MAGNETIC_FIELD)
        {
            Log.i(MYDEBUG, "Sensor mode: Accelerometer + Magnetic field");
            sO = null;
        } else if (sensorMode == DemoTiltMeterSetup.ACCELEROMETER_ONLY)
        {
            Log.i(MYDEBUG, "Sensor mode: Accelerometer only");
            sO = null;
            sM = null;
        } else
        {
            Log.i(MYDEBUG, "Unknown sensor mode!"); // we should never reach this code
            finish();
        }

        // NOTE: sensor listeners are registered in onResume

        // setup the screen refresh timer (updates to the tilt meter occur every REFRESH_INTERVAL milliseconds)
        refreshScreen = new ScreenRefreshTimer(REFRESH_INTERVAL, REFRESH_INTERVAL);
        refreshScreen.start();
    }

    // get the default orientation of the device (affects how the tilt meter is rendered)
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
        sensorManager.registerListener(this, sO, samplingRate); // sO might be null (that's OK)
        sensorManager.registerListener(this, sA, samplingRate); // sA might be null (that's OK)
        sensorManager.registerListener(this, sM, samplingRate); // sM might be null (that's OK)
        Log.i(MYDEBUG, "onResume 2!");
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        sensorManager.unregisterListener(this);
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
        refreshScreen.cancel();
        super.onStop();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // not used but we need to provide an implementation anyway
    }

    @Override
    public void onSensorChanged(SensorEvent se)
    {
        Log.i(MYDEBUG, "onSensorChanged!");
        // =======================================================
        // DETERMINE DEVICE PITCH AND ROLL (VARIES BY SENSOR MODE)
        // =======================================================

        switch (sensorMode)
        {
            // ---------------------------------------------------------------------------------------------
            case DemoTiltMeterSetup.ORIENTATION:
                pitch = se.values[1];
                roll = se.values[2];
                break;

            // ---------------------------------------------------------------------------------------------
            case DemoTiltMeterSetup.ACCELEROMETER_AND_MAGNETIC_FIELD:
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
            case DemoTiltMeterSetup.ACCELEROMETER_ONLY:

				/*
                 * The accelerometer-only sensor mode doesn't work quite as well, unfortunately, See...
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
    } // end onSensorChanged

    /*
     * Low pass filter. The algorithm requires tracking only two numbers - the prior number and the
     * new number. There is a time constant "alpha" which determines the amount of smoothing. Alpha
     * is like a "weight" or "momentum". It determines the effect of the new value on the current
     * smoothed value.
     *
     * A lower alpha means more smoothing. NOTE: 0 <= alpha <= 1.
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

    // Screen updates are done in onFinish which executes every REFRESH_INTERVAL milliseconds
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
            /*
             * Do the main number crunching here, rather than in onSensorChanged.  The first task is to compute the
             * device's tilt magnitude and tilt angle.  The inputs are the pitch and roll values obtained in
             * onSensorChanged.  This method executes every REFRESH_INTERVAL milliseconds.  We are always using the
             * most recent pitch and roll values.
             */
            float tiltMagnitude = (float)Math.sqrt(pitch * pitch + roll * roll);
            float tiltAngle = tiltMagnitude == 0f ? 0f : (float)Math.asin(roll / tiltMagnitude) *
                    RADIANS_TO_DEGREES;

            // some funny remapping seems to be necessary (it might be possible to skip this, but I'm not sure how!)
            if (pitch > 0 && roll > 0)
                tiltAngle = 360f - tiltAngle;
            else if (pitch > 0 && roll < 0)
                tiltAngle = -tiltAngle;
            else if (pitch < 0 && roll > 0)
                tiltAngle = tiltAngle + 180.0f;
            else if (pitch < 0 && roll < 0)
                tiltAngle = tiltAngle + 180f;

            // give the pitch and roll values to the tilt meter (values will appear in the bottom-left of the view)
            tiltMeter.setPitch(pitch);
            tiltMeter.setRoll(roll);

            // give the tilt angle and tilt magnitude to the tilt meter.  These control the angle and length of the
            // needle
            tiltMeter.updateTilt(tiltAngle, tiltMagnitude);

            // re-start the timer (next update will occur in REFRESH_INTERVAL milliseconds)
            this.start();
        }
    }
}
