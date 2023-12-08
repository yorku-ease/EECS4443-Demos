package ca.yorku.eecs.mack.demoflat;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;
import android.widget.ImageView;

import ca.yorku.eecs.mack.demolevel.R;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Flat - a demo that rotates a View, keeping it flat with respect to the ground. <p>
 *
 * Related information: <p>
 *
 * <blockquote> See <code>DemoSensors</code> for topic and links. </blockquote> <p>
 *
 * This demo is a stripped-down version of <code>DemoTiltMeter</code>. As implied in the short description above, the
 * demo uses data from an Android device's built-in sensors to rotate a view. As the device is held and moved about, the
 * View remains flat with respect to the ground. <p>
 *
 * The UI layout for the main activity (in <code>res/layout/main.xml</code>) uses a <code>LinearLayout</code> containing
 * a single <code>ImageView</code> element. In <code>onCreate</code>, an image resource is placed in the
 * <code>ImageView</code>: <p>
 *
 * <pre>
 *      imageView = (ImageView)findViewById(R.id.image_view);
 *      imageView.setImageResource(R.drawable.map_north);
 * </pre>
 *
 * The image resource is a <code>.jpg</code> file showing a map. Superimposed on the map is an arrow pointing NORTH
 * (below left). When the demo launches, the image is loaded and placed in the activity's UI (below right). <p>
 *
 * <center> <a href="DemoFlat-1.jpg"><img src="DemoFlat-1.jpg" height="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="DemoFlat-2.jpg"><img src="DemoFlat-2.jpg" height="400"></a> </center> <p>
 *
 * The UI image above was captured with the device sitting flat on a table, facing approximately NORTH (below left). If
 * the device is rotated, the arrow continues to point NORTH (below right).
 *
 * <center> <a href="DemoFlat-6a.jpg"><img src="DemoFlat-6a.jpg" height="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="DemoFlat-6b.jpg"><img src="DemoFlat-6b.jpg" height="300"></a> </center> <p>
 *
 *
 * Furthermore, if the device is picked up and moved about, the image remains flat with respect to the table or ground.
 * This occurs by rotating the view about the <i>x</i>-axis and <i>y</i>-axis. And the arrow continues to point
 * approximately NORTH. This is possible by rotating the view about the <i>z</i>-axis. A few examples of screen snaps
 * with the device held in different positions are shown below. <p>
 *
 * <center> <a href="DemoFlat-3.jpg"><img src="DemoFlat-3.jpg" width="200"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="DemoFlat-4.jpg"><img src="DemoFlat-4.jpg" width="200"></a></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="DemoFlat-5.jpg"><img src="DemoFlat-5.jpg" width="200"></a> </center> <p>
 *
 * The images above look strange. To appreciate this demo, it must be experienced first-hand: Run the app, hold the
 * device, and move the device around. Fun, isn't it! Can you think of other uses for this sort of interaction? <p>
 *
 * The common terms for angular position about the <i>x</i>-, <i>y</i>-, and <i>z</i>-axes are <i>pitch</i>,
 * <i>roll</i>, and <i>azimuth</i>, respectively. (Azimuth is sometimes called <i>yaw</i>.) The device and world axes
 * are shown below using images from the <code>SensorEvent</code> API:
 *
 * <center> <img src="DemoFlat-7.jpg"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <img src="DemoFlat-8.jpg"></a></center> <p>
 *
 * NOTE: For this demo to fully work, the Android device must have either (a) an orientation sensor
 * (<code>Sensor.TYPE_ORIENTATION</code>) or (b) an accelerometer ( <code>Sensor.TYPE_ACCELEROMETER</code>) <i>and</i> a
 * magnetic field sensor ( <code>Sensor.TYPE_MAGNETIC_FIELD</code>). Some Android devices, such as the Asus <i>MeMO
 * Pad</i>,
 * have an accelerometer but no orientation or magnetic field sensor. In this case the image will remain flat, by
 * rotating about the <i>x</i>-axis and <i>y</i>-axis, but it will not swivel about, which is to say, the arrow may not
 * point NORTH. The problem in this case is that reliable azimuth data are not available from an accelerometer.
 * (Google-search this topic for further details.) <p>
 *
 * The biggest challenge in this demo is getting the pitch, roll, and azimuth data from the device. This is done in
 * <code>onSensorChanged</code> &ndash; the callback for sensor events. There is some fiddling necessary, depending on
 * which sensors are available and whether the device's default orientation is portrait or landscape. For complete
 * details consult the source code, the source-code comments, and the previous demo programs, Demo_Sensors and
 * Demo_TiltMeter. <p>
 *
 * Once the pitch, roll, and azimuth data are obtained, rotating the image is relatively straight forward: <p>
 *
 * <pre>
 *      imageView.setRotationX(pitch);
 *      imageView.setRotationY(roll);
 *      imageView.setRotation(azimuth + azimuthAdjust);
 *      imageView.invalidate();
 * </pre>
 *
 * The code above appears in the <code>onFinish</code> method of the timer used for screen refreshes. The same technique
 * was used in Demo_Sensors and Demo_TiltMeter . The refresh interval is set programmatically at 50 ms, yielding a
 * screen update rate of 20 Hz. This rate can be increased or decreased independent of the sensor sampling rate. </p>
 *
 * If the sensor sample delay is set to <code>SensorManager.SENSOR_DELAY_FASTEST</code>, the delay is about 5 ms on a
 * Google <i>Nexus 4</i>. That's a sample rate of 200 Hz &ndash; very fast! The screen update rate could be increased
 * correspondingly. In general, a high update rate yields a smooth and responsive user experience. Lowering the update
 * rate will, at some point, result in a degraded user experience. Hmm... Is there a project topic here? At what update
 * rate, does the user begin to notice a degradation in responsiveness? If the user is doing a task using device
 * rotation, what is the relationship between the UI's update rate and user performance? <p>
 *
 * NOTE: This app doesn't work properly on Android 6 (M).  When the device pitch or roll exceeds 45 degrees, the display
 * shuts off.  This is likely a bug related to the use of the proximity sensor to turn off the display during a call. No
 * fix yet (Jan 2016).
 *
 * @author (c) Scott MacKenzie, 2014-2018
 */
public class DemoFlatActivity extends Activity implements SensorEventListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int REFRESH_INTERVAL = 50; // milliseconds (screen refreshes @ 20 Hz)
    final static float RADIANS_TO_DEGREES = 57.2957795f;

    // int constants to setup a mode (see API for discussion)
    final static int ORIENTATION = 0;
    final static int ACCELEROMETER_ONLY = 1;
    final static int ACCELEROMETER_AND_MAGNETIC_FIELD = 2;
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
    ImageView imageView;
    float alpha;

    SensorManager sensorManager;
    Sensor sA, sO, sM;

    int defaultOrientation;
    int sensorMode;

    float[] accValues = new float[3];
    float[] magValues = new float[3];
    //float[] R = new float[3];
    //float[] I = new float[3];
    float x, y, z, roll, pitch, azimuth, azimuthAdjust;
    int rate;

    ScreenRefreshTimer refreshScreen;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    private void initialize()
    {
        imageView = (ImageView)findViewById(R.id.image_view);
        imageView.setImageResource(R.drawable.map_north);

        // get this device's default orientation (e.g., portrait for phones, landscape for tablets)
        defaultOrientation = getDefaultDeviceOrientation();

        // force the view to appear in the device's default orientation (and stay that way)
        if (defaultOrientation == Configuration.ORIENTATION_LANDSCAPE)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        else
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        alpha = ALPHA_ARRAY[3]; // fastest
        rate = SensorManager.SENSOR_DELAY_FASTEST;

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        sO = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION); // supported on many devices
        sA = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // supported on most devices
        sM = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD); // null on many devices

        // setup the sensor mode (see DemoTiltMeter API for discussion)
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
            Log.i(MYDEBUG, "Sensor mode: ACCELEROMETER_ONLY");
        } else
        {
            Log.i(MYDEBUG, "Can't run demo.  Requires Orientation sensor or Accelerometer");
            this.onDestroy();
            this.finish();
        }

        // NOTE: sensor listeners are registered in onResume

        // setup the screen refresh timer (updates occur every REFRESH_INTERVAL milliseconds)
        refreshScreen = new ScreenRefreshTimer(REFRESH_INTERVAL, REFRESH_INTERVAL);
        refreshScreen.start();
    }

    /*
     * Re-size the image to fit the screen (without changing the image's aspect ratio). This is done
     * here to give the image resource time to load. If done in onCreate, the image width and height
     * are returned as zero.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        // determine screen width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeight = dm.heightPixels;

        // determine image width and height (Note: The image is now loaded)
        float imageWidth = imageView.getWidth();
        float imageHeight = imageView.getHeight();

        // compute scaling factor
        float widthRatio = imageWidth / screenWidth;
        float heightRatio = imageHeight / screenHeight;
        float scalingFactor = widthRatio > heightRatio ? screenWidth / imageWidth : imageHeight / screenHeight;

        // apply scaling factor to make the image fit the screen
        imageView.setScaleX(scalingFactor);
        imageView.setScaleY(scalingFactor);
    }

    /*
     * Get the default orientation of the device. This is needed in order to correctly map the
     * sensor data for pitch and roll (see onSensorChanged).
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
        sensorManager.registerListener(this, sO, rate); // sO might be null (that's OK)
        sensorManager.registerListener(this, sA, rate); // sA might be null (that's OK)
        sensorManager.registerListener(this, sM, rate); // sM might be null (that's OK)
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
        super.onStop();
        refreshScreen.cancel();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // not used but we need to provide an implementation anyway
    }

    @Override
    public void onSensorChanged(SensorEvent se)
    {
        // =======================================================
        // DETERMINE DEVICE PITCH AND ROLL (VARIES BY SENSOR MODE)
        // =======================================================

        switch (sensorMode)
        {
            // --------------------------------------------------------------------------
            case ORIENTATION:
                pitch = -se.values[1];
                roll = se.values[2];
                azimuth = -se.values[0];
                break;

            // --------------------------------------------------------------------------
            case ACCELEROMETER_AND_MAGNETIC_FIELD:

				/*
                 * Use this mode if the device has both an accelerometer and a magnetic field sensor
				 * (but no orientation sensor). See...
				 * 
				 * http://blog.thomnichols.org/2012/06/smoothing-sensor-data-part-2
				 */

                // smooth the sensor values using a low-pass filter
                if (se.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
                {
                    accValues = lowPass(se.values.clone(), accValues, alpha);
                    //Log.i(MYDEBUG, "onSensorChanged: accelerometer");
                }
                if (se.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
                {
                    magValues = lowPass(se.values.clone(), magValues, alpha);
                    //Log.i(MYDEBUG, "onSensorChanged: magnetic field");
                }

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
                        azimuth = orientation[0] * RADIANS_TO_DEGREES;
                    }
                }
                break;

            // --------------------------------------------------------------------------
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
                azimuth = 0f; // no azimuth data available (but see below)

                // Below is a failed attempt to get azimuth data, See...
                // http://diy.powet.eu/2011/03/19/tilt-compensation-azimuth-pitch-le-roll/?lang=en

                // double xh = x * Math.cos(pitch) + y * Math.sin(roll) - z * Math.cos(pitch) *
                // Math.sin(pitch);
                // double yh = y * Math.cos(roll) + z * Math.sin(roll);
                // azimuth = (float)Math.atan(-yh / xh) * RADIANS_TO_DEGREES;

                break;
        }
    }

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

    /*
     * Screen updates are done in onFinish which executes every REFRESH_INTERVAL milliseconds
     */
    public class ScreenRefreshTimer extends CountDownTimer
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
            //String s = String.format("%15.1f, %15.1f, %15.1f\n", pitch, roll, azimuth);
            //Log.i(MYDEBUG, s);
            imageView.setRotationX(pitch);
            imageView.setRotationY(roll);
            imageView.setRotation(azimuth + azimuthAdjust);
            imageView.invalidate();
            this.start();
        }
    }
}