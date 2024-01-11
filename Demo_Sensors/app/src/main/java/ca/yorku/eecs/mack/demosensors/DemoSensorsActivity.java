package ca.yorku.eecs.mack.demosensors;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.Locale;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Sensors - demo of Android's <code>SensorManager</code>, <code>Sensor</code>, and <code>SensorEvent</code>
 * classes. Also demos the use of spinners in a setup dialog and passing user choices from one activity to another using
 * an intent. </p>
 *
 * Related information: </p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/sensors/sensors_overview.html">Sensors Overview</a> <li>
 * <a href="http://developer.android.com/guide/topics/sensors/sensors_motion.html">Motion Sensors</a> <li> <a
 * href="http://developer.android.com/guide/topics/sensors/sensors_position.html">Position Sensors</a> <li> <a
 * href="http://developer.android.com/guide/topics/sensors/sensors_environment.html">Environment Sensors</a> <li><a
 * href="http://developer.android.com/guide/topics/ui/controls/spinner.html">Spinners</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/hardware/SensorManager.html">
 * <code>SensorManager</code></a> <li> <a href="http://developer.android.com/reference/android/hardware/Sensor
 * .html"><code>Sensor</code> </a> <li> <a href="http://developer.android.com/reference/android/hardware/SensorEvent.html">
 *     <code>SensorEvent</code></a> <li> <a href="http://developer.android.com/reference/android/hardware/SensorEventListener.html">
 * <code>SensorEventListener </code></a> <li> <a href="http://developer.android.com/reference/android/os/Bundle
 * .html"><code>Bundle</code></a> <li> <a href="http://developer.android.com/reference/android/content/Intent
 * .html"><code>Intent</code> </a> <li><a href="http://developer.android.com/reference/android/widget/Spinner.html">
 * <code>Spinner</code></a> </ul> </blockquote>
 *
 * The Android platform supports many sensor types. These are divided into three broad categories: Motion, Position, and
 * Environment. <p>
 *
 * <blockquote> Motion sensors: <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc">
 * <th>Sensor <th>Description <th>Units of measurements
 *
 * <tr> <td><code>TYPE_ACCELEROMETER</code> <td>Acceleration force about the <i>x</i>-, <i>y</i>-, and <i>z</i>-axes <td
 * align="center">m/s<sup>2</sup>
 *
 * <tr> <td><code>TYPE_GRAVITY</code> <td>Force of gravity about the <i>x</i>-, <i>y</i>-, and <i>z</i>-axes <td
 * align="center">m/s<sup>2</sup>
 *
 * <tr> <td><code>TYPE_GYROSCOPE</code> <td>Rate of rotation about the <i>x</i>-, <i>y</i>-, and <i>z</i>-axes <td
 * align="center">rad/s
 *
 * <tr> <td><code>TYPE_LINEAR_ACCELERATION</code> <td>Acceleration force about the <i>x</i>-, <i>y</i>-, and
 * <i>z</i>-axes <td align="center">m/s<sup>2</sup>
 *
 * <tr> <td><code>TYPE_ROTATION_VECTOR</code> <td>Rotation vector component along the <i>x</i>-, <i>y</i>-, and
 * <i>z</i>-axes <td align="center">unitless </table> </blockquote>
 *
 *
 * Position sensors: <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Sensor
 * <th>Description <th>Units of measurements
 *
 * <tr> <td><code>TYPE_MAGNETIC_FIELD <td>Geomagnetic field strength along the <i>x</i>-, <i>y</i>-, and <i>z</i>-axes
 * <td align="center">&mu;T
 *
 * <tr> <td><code>TYPE_ORIENTATION</code> <td>Angles (roll, pitch, azimuth) about the <i>x</i>-, <i>y</i>-, and
 * <i>z</i>-axes <td align="center">degrees
 *
 * <tr> <td><code>TYPE_PROXIMITY</code> <td>Distance from object <td align="center">cm </table> </blockquote>
 *
 * Environment sensors:
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Sensor <th>Description
 * <th>Units of measurements
 *
 * <tr> <td><code>TYPE_AMBIENT_TEMPERATURE</code> <td>Ambient air temperature <td align="center">&deg;C
 *
 * <tr> <td><code>TYPE_LIGHT</code> <td>Luminance <td align="center">lx
 *
 * <tr> <td><code>TYPE_PRESSURE</code> <td>Ambient air pressure <td align="center">hPa or mbar
 *
 * <tr> <td><code>TYPE_RELATIVE_HUMIDITY</code> <td>Ambient relative humidity <td align="center">%
 *
 * <tr> <td><code>TYPE_TEMPERATURE <td>Device temperature <td align="center">&deg;C </table>
 *
 * </blockquote> </blockquote> </p>
 *
 * There are also some "trigger sensors" introduced with API level 18 (Jelly Bean), but these are not explored here.
 * <p>
 *
 * The complete list of sensors, including all variations, is found in the defined constants in the <code>Sensor</code>
 * class. Some of the sensors have been added in newer versions of the Android API. Some have been deprecated. The data
 * available from each sensor are defined in the <code>SensorEvent</code> class. </p>
 *
 * Let's begin with a few comments about the organization of this demo program. </p>
 *
 * This demo uses two activities in a single application. The first activity is a setup dialog. The class is
 * <code>DemoSensorsSetup</code> which <code>extends Activity</code>. The goal is to get input from the user. The user
 * selects a sensor and sampling rate from spinners and then clicks "OK" to launch the second activity to demonstrate
 * the sensor selected. The flow of execution is illustrated below. </p>
 *
 * <center><a href="./javadoc_images/DemoSensors-11.jpg"><img src="./javadoc_images/DemoSensors-11.jpg" width="600"></a></center> </p>
 *
 * Of course, a full examination of the flow of execution will consider the life cycle states and methods defined for
 * Android activities.  These were introduced in Demo_Layout.  The following diagram is a reminder that, since our app
 * has two activities, two sets of life cycle states and methods must be considered: <p>
 *
 * <center><a href="./javadoc_images/DemoSensors-12.jpg"><img src="./javadoc_images/DemoSensors-12.jpg" width="800"></a></center> </p>
 *
 * The demo includes an implementation of all the life cycle methods for both activities.  For each, a simple message is
 * sent to the logcat window along with an indication of which activity ("setup" or "main") is associated with the
 * method.  Can you guess the sequence of method calls when the setup activity is launched and then transitions to the
 * main activity?  Can you guess the sequence of method calls when the main activity is executing and then the user taps
 * the Back button on the navigation bar?  You are invited to explore these questions by running the app from Android
 * Studio with an Android device connected in USB debugging mode.  You'll need to observe the logcat output in the
 * Android Monitor. You should probably filter the output to show only the relevant messages.  See Demo_Layout as a
 * reminder of how to do this. <p>
 *
 * In the setup dialog, the user's choices are "put" into a bundle which is passed to the main activity. Here's the code
 * (see the <code>clickOK</code> method in <code>DemoSensorsSetup</code>): </p>
 *
 * <pre>
 *      // put the user's choices into a bundle
 *      Bundle b = new Bundle();
 *      b.putInt("sensorInfoIdx", selectedInfoIdx);
 *      b.putInt("samplingRateIdx", samplingRateIdx);
 *
 *      // start main demo activity (passing the bundle to it)
 *      Intent i = new Intent(getApplicationContext(), DemoSensorsActivity.class);
 *      i.putExtras(b);
 *      startActivity(i);
 * </pre>
 *
 * The second activity is <code>DemoSensorsActivity</code> which also <code>extends Activity</code>. When
 * <code>DemoSensorsActivity</code> launches, the user's choices are retrieved from the bundle using "get" methods.
 * Here's the code (see the <code>onCreate</code> method in <code>DemoSensorsActivity</code>): </p>
 *
 * <pre>
 *      Bundle b = getIntent().getExtras();
 *      sensorInfoIdx = b.getInt("sensorInfoIdx");
 *      samplingRateIdx = b.getInt("samplingRateIdx");
 *      </pre>
 *
 * Both activities are declared in the manifest file. The activity to execute first is identified in the manifest file
 * through an intent filter. Within the code for the first activity, control is passed to the second activity through an
 * <code>Intent</code> (see above). <p>
 *
 * One purpose of the setup dialog is to retrieve a list of all sensors available on the device. This is achieved as
 * follows: <p>
 *
 * <pre>
 *      SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
 *      List&lt;Sensor&gt; sensorListTemp = sensorManager.getSensorList(Sensor.TYPE_ALL);
 * </pre>
 *
 *
 * The list will vary from one device to another. The Samsung <i>Galaxy Tab 10.1</i>, for example, supports eight
 * sensors
 * (e.g., an accelerometer). The HTC <i>Desire C</i>, on the other hand, only supports three (e.g., a light sensor). <p>
 *
 * One useful feature in the demo is to sort the list by "sensor type".  The sensor type is a unique integer
 * constant that is assigned to each sensor.  Sorting the list is a two-step process.  First, the <code>List</code>
 * (which is immutable) is converted to an <code>ArrayList</code> (which is mutable).  Then, the <code>ArrayList</code>
 * is sorted using the <code>sort</code> method from Java's Collections framework.  The <code>sort</code> method
 * receives two arguments, the <code>ArrayList</code> to sort, and a <code>Comparator</code> to determine the order for
 * each comparison in the sort. A custom <code>Comparator</code> called <code>ByType</code> is used to force the sort to
 * proceed according to the type field in the <code>Sensor</code> objects: <p>
 *
 * <pre>
 *      List&lt;Sensor&gt; sensorList;
 *      ...
 *      sensorList = new ArrayList&lt;&gt;(sensorListTemp);
 *      Collections.sort(sensorList, new ByType());
 * </pre>
 *
 * The sorted list of sensors is output two ways.  First, the list is sent to the logcat window using <code>Log.i</code>
 * calls within an iterator.  The output is formatted to show the sensor type (an <code>int</code>) and the sensor name
 * (a <code>String</code>).  Here's the output for an LG <i>Nexus 5x</i>: <p>
 *
 * <pre>
 *     Sensors supported...
 *              1 : BMI160 accelerometer
 *              2 : BMM150 magnetometer
 *              3 : Orientation
 *              4 : BMI160 gyroscope
 *              5 : RPR0521 light
 *              6 : BMP280 pressure
 *              8 : RPR0521 proximity
 *              9 : Gravity
 *             10 : Linear Acceleration
 *             11 : Rotation Vector
 *             14 : BMM150 magnetometer (uncalibrated)
 *             15 : Game Rotation Vector
 *             16 : BMI160 gyroscope (uncalibrated)
 *             17 : Significant motion
 *             18 : BMI160 Step detector
 *             19 : BMI160 Step counter
 *             20 : Geomagnetic Rotation Vector
 *             22 : Tilt Detector
 *             25 : Pickup Gesture
 *          65536 : BMP280 temperature
 *          65537 : Sensors Sync
 *          65538 : Double Twist
 *          65539 : Double Tap
 *          65540 : Window Orientation
 * </pre>
 *
 * The sensor list is also presented in a spinner in the UI's setup dialog. </p>
 *
 * The following screen snaps show the application upon launching (left), the sensor list (center), and the sampling
 * rates (right): (The device is a Google <i>Nexus 5x</i>.) </p>
 *
 * <center><a href="./javadoc_images/DemoSensors-1.jpg"><img src="./javadoc_images/DemoSensors-1.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoSensors-2.jpg"><img src="./javadoc_images/DemoSensors-2.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
 * href="./javadoc_images/DemoSensors-8.jpg"><img src="./javadoc_images/DemoSensors-8.jpg" width="250"></a></center> </p>
 *
 * The demo will run on devices running API level 12 or higher.  All sensors defined up to Android API level 23 are
 * supported. If the device running the demo supports newer sensors, these will appear in the spinner. If the user
 * selects one of the newer sensors, a message pops up asking the user to select again. Consult the source code in
 * <code>DemoSensorsSetup</code> for details. </p>
 *
 * The sampling rate spinner allows the user to select the sampling rate among the four supported in the Android API.
 * These are as follows (with typical values): </p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Sampling Rate
 * <th><code>SensorManager</code> Constant <th>Delay<sup>a</sup> <th>Rate
 *
 * <tr> <td>Normal <td><code>SENSOR_DELAY_NORMAL</code> <td>200 ms <td>5 Hz
 *
 * <tr> <td>UI <td><code>SENSOR_DELAY_UI</code> <td>60 ms <td>16.7 Hz
 *
 * <tr> <td>Game <td><code>SENSOR_DELAY_GAME</code> <td>20 ms <td>50 Hz
 *
 * <tr> <td>Fastest <td><code>SENSOR_DELAY_FASTEST</code> <td>10 ms <td>100 Hz
 *
 * <tr> <td colspan="4"><sup>a</sup>Approximate (see comments below)
 *
 * </table> </blockquote> </p>
 *
 * The values above were observed on a Samsung <i>Galaxy Tab 10.1</i>. On a Google <i>Nexus 5x</i>, the fastest (i.e.,
 * lowest) time
 * was observed at 5 ms. Although faster is clearly better, power consumption increases with a higher sampling rate
 * (i.e., lower delay time). </p>
 *
 * BEWARE: The terminology is a bit loose in the Android documentation. In English usage, "rate" refers to speed and
 * "delay" refers to time. Time is the reciprocal of speed. These terms are mixed indiscriminately in the API. For
 * example, the <code>SensorManager</code> constants use the term "delay", but the same values are called "rate" in the
 * <code>registerListener</code> method. It is incorrect (or, at least, confusing) to express a rate as a delay. For
 * clarity, typical values for both the delay and the rate are given in the table above. </p>
 *
 * After choosing a sensor and sampling rate, the user clicks "OK" to terminate the setup dialog and pass control to the
 * main activity to demonstrate the selected sensor. </p>
 *
 * Each sensor event initiates a call to the <code>onSensorChanged</code> listener method.  Data for the event are
 * available through <code>SensorEvent</code> object passed in to <code>onSensorChanged</code>. Android sensors report 1
 * to 4 values, depending on the sensor. In the demo, sensor data are collected and presented graphically in
 * <code>ViewMeter</code> objects, which are custom-designed subclasses of <code>View</code>. Space for four meters is
 * provided on the display, although only those required are rendered. </p>
 *
 * The following screen snap shows the main activity with data from the <code>TYPE_ORIENTATION</code> sensor with the
 * sampling set to "Normal": (click to enlarge)</p>
 *
 * <center><a href="./javadoc_images/DemoSensors-3.jpg"><img src="./javadoc_images/DemoSensors-3.jpg" width="500"></a></center> </p>
 *
 * The labels for the meters are hard-coded using the labels in the <code>SensorEvent</code> API where each sensor is
 * described. For <code>TYPE_ORIENTATION</code> (see above), the labels are Azimuth, Pitch, and Roll. The labels
 * correspond to the angular position of the device with respect to the <i>z</i>, <i>x</i>, and <i>y</i> axes,
 * respectively. The values below each meter are obtained directly from the <code>values</code> array available through
 * the <code>SensorEvent</code> object. </p>
 *
 * For the image above, the device was sitting flat on a table, facing approximately NORTH.  The approximate sensor
 * values are 360&deg; for azimuth (device facing north), 0&deg; for pitch (device flat), 0&deg; for roll (device flat).
 * <p>
 *
 * An information panel on the left (see above) outputs information about the selected sensor. Apparently, the vendor
 * for this device's Orientation sensor is Google. The reported power consumption is 0.0 ma (as returned by the
 * <code>getPower</code> method in the <code>Sensor</code> class). </p>
 *
 * Below the vendor and power information are two lines giving the sensor's reporting mode and wake-up status. This
 * information is only available on devices running API level 21 or higher.  Below this is a continuous stream of
 * 2-tuple data in parentheses. A 2-tuple is outputted with each sensor event. The first number is the count of the
 * number of sensor samples since the app was launched.  The second number is the sensor delay for the most recent
 * sample. The sensor delay is the time from the last sensor event to the current sensor event. As seen above, the
 * sensor delay is about 80 ms.  This output is using "Normal" sampling rate on an LG <i>Nexus 5x</i>. </p>
 *
 * This demo is designed to input, process, and output sensor data all from within the sensor's
 * <code>onSensorChanged</code> method.  In fact, this does <i>not</i> conform to the best-practice advice given in the
 * Sensors Overview API Guide (link above).  We'll learn how to correctly organize the processing of sensor data in our
 * next demo program, Demo_TiltMeter.<p>
 *
 * Do you recall the earlier comment that the screen snap was taken with "the device is sitting flat on a table, facing
 * approximately NORTH"? To understand this, we must consider the Android coordinate system for orientation. The figures
 * below are from the <code>SensorEvent</code> API. The figure on the left illustrates the coordinate system for Android
 * devices. The figure on the right shows the same coordinates in a "world view" with the <i>y</i>-axis pointing NORTH,
 * the <i>x</i>-axis pointing EAST, and the <i>z</i>-axis pointing UP.  For the orientation sensor, rotations about the
 * axes are called pitch (about the <i>x</i>-axis), roll (about the <i>y</i>-axis), and azimuth (about the
 * <i>z</i>-axis). </p>
 *
 * <center> <a href="./javadoc_images/DemoSensors-4.jpg"><img src="./javadoc_images/DemoSensors-4.jpg" height="250"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
 * href="./javadoc_images/DemoSensors-5.jpg"><img src="./javadoc_images/DemoSensors-5.jpg" height="250"></a> </center> </p>
 *
 * Compare the figures above with the screen snap. Yes, the device is flat, facing approximately NORTH. </p>
 *
 * Using a fast sampling yields a smooth and responsive UI &ndash; a positive user experience (UX). But, there is a
 * downside: Device power consumption increases with higher sample rates. In general, the best sampling rate is the
 * slowest rate that gives an acceptable outcome. "Acceptable outcome" usually implies a positive user experience, but
 * other outcomes might be important depending on how the sensors are used. Of course, power consumption is always a
 * concern with mobile devices. On this issue, the following advice is offered in the <code>SensorManager</code> API:
 * <p>
 *
 * <blockquote><i> Always make sure to disable sensors you don't need, especially when your activity is paused. Failing
 * to do so can drain the battery in just a few hours. Note that the system will <i>not</i> disable sensors
 * automatically when the screen turns off. </i></blockquote>
 *
 * This advice is followed here by registering the listener in <code>onResume</code> (rather than <code>onCreate</code>)
 * and unregistering the listener in <code>onPause</code>. (Recall that <code>onPause</code> and <code>onResume</code>
 * are Activity lifecycle methods, as presented in Demo_Layout.) Consult the source code for complete details. <p>
 *
 * There are of course, other sensors for you to explore and use in creative ways in developing mobile user interfaces.
 * Most Android phones support <code>Sensor.TYPE_PROXIMITY</code>, which can be used to sense when the user positions
 * the device near their face to speak or listen during a phone call. The slowest sampling rate
 * (<code>SENSOR_DELAY_NORMAL</code>) is obviously sufficient for this purpose. The following screen snaps show
 * Demo_Sensors with the proximity sensor selected. </p>
 *
 * <center> <a href="./javadoc_images/DemoSensors-9.jpg"><img src="./javadoc_images/DemoSensors-9.jpg" height="250"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a
 * href="./javadoc_images/DemoSensors-10.jpg"><img src="./javadoc_images/DemoSensors-10.jpg" height="250"></a> </center> </p>
 *
 * According to information in the <code>SensorEvent</code> API, the proximity sensor data represent the "sensor
 * distance measured in centimeters". However, it is also noted that </p>
 *
 * <blockquote><i> Some proximity sensors only support a binary near or far measurement. In this case, the sensor should
 * report its maximum range value in the far state and a lesser value in the near state.</i> </blockquote> </p>
 *
 * That is the case for the device above. The data toggle between 5 (nothing nearby) and 0 (the hand in close
 * proximity). </p>
 *
 * @author (c) Scott MacKenzie, 2011-2018
 */
@SuppressWarnings("unused")
public class DemoSensorsActivity extends Activity implements SensorEventListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int REFRESH_INTERVAL = 50; // milliseconds (screen refreshes @ 20 Hz)
    final static String FORMAT_STRING = "Format: (samples, sensor_delay)";

    // sensor reporting modes are available as of API level 21 (we'll report it, if possible)
    // NOTE: I've reversed the position of "on change" and "one shot".  I suspect the API is wrong, since the
    // proximity sensor (likely on-change) reports "1" as its reporting mode
    final static String[] REPORTING_MODE = {"Continuous", "On change", "One shot", "Special trigger"};

    TextView infoPanel, sensorNamePanel;
    ViewMeter xMeter, yMeter, zMeter, meter4;
    int sensorInfoIdx, samplingRateIdx, samplingRate, smoothingRange;
    long now, nowSave, delta;
    StringBuilder infoData;
    StringBuilder headerData;

    float x, y, z, m4; // sensor values are stored in these variable
    int sampleCount;

    private SensorManager sensorManager;
    private Sensor sensor;

    public void onCreate(Bundle savedInstanceState)
    {
        Log.i(MYDEBUG, "onCreate! (main)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Bundle b = getIntent().getExtras();
        assert b != null;
        sensorInfoIdx = b.getInt("sensorInfoIdx");
        samplingRate = b.getInt("sensorSamplingRate");

        // get a SensorInfo object (contains all the information we'll need for the selected sensor)
        SensorInfo sensorInfo = DemoSensorsSetup.SENSOR_INFO[sensorInfoIdx];

        // use a SensorManage instance to get a Sensor object for the selected sensor
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        sensor = sensorManager.getDefaultSensor(sensorInfo.type);

        // NOTE: sensor listener is registered in onResume

        // initialize some interesting information to appear as the header in the info panel
        headerData = new StringBuilder(String.format("Vendor: %s\n", sensor.getVendor()));
        headerData.append(String.format("Power usage: %s ma\n", sensor.getPower()));

        /*
         * Append the sensor "reporting mode" and "wake-up" status, if possible.  The reporting mode and wake-up
         * status were introduced in API 21 and this demo's minSDK is 12 (see build.gradle).  Because of this, we add
         * an if-statement preventing the problematic code from executing on devices below API 21, thus averting a
         * runtime crash.
         *
         * Note: The compiler (actually Lint) is smart enough to *not* generate compile errors for the code below.
         *
         */
        Log.i(MYDEBUG, "SDK version = " + Build.VERSION.SDK_INT);
        if (Build.VERSION.SDK_INT >= 21)
        {
            String reportingMode = REPORTING_MODE[sensor.getReportingMode()]; // introduced in API level 21
            headerData.append(String.format("Reporting mode: %s\n", reportingMode));

            String wakeUpSensor = sensor.isWakeUpSensor() ? "Yes" : "No"; // introduced in API level 21
            headerData.append(String.format("Wake-up sensor: %s\n", wakeUpSensor));
        }

        // append one more string identifying the format of the data to come
        headerData.append(String.format("%s\n", FORMAT_STRING));

        // put the header data in the info panel (more data will be appended as sensor events fire)
        infoPanel = (TextView)findViewById(R.id.info_text_view);
        infoPanel.setText(headerData);

        // get a reference to the view that appears above the four meters
        sensorNamePanel = (TextView)findViewById(R.id.sensorname);

        // put the name of the selected sensor in the view
        sensorNamePanel.setText(sensorInfo.name);

        // initialize the four view meters with labels that are appropriate for the selected sensor
        xMeter = (ViewMeter)findViewById(R.id.xmeter);
        xMeter.setLabel(sensorInfo.value0Label);
        xMeter.setMinMax(sensorInfo.value0Min, sensorInfo.value0Max);
        xMeter.value = sensorInfo.value0Min;

        yMeter = (ViewMeter)findViewById(R.id.ymeter);
        yMeter.setLabel(sensorInfo.value1Label);
        yMeter.setMinMax(sensorInfo.value1Min, sensorInfo.value1Max);
        yMeter.value = sensorInfo.value1Min;

        zMeter = (ViewMeter)findViewById(R.id.zmeter);
        zMeter.setLabel(sensorInfo.value2Label);
        zMeter.setMinMax(sensorInfo.value2Min, sensorInfo.value2Max);
        zMeter.value = sensorInfo.value2Min;

        meter4 = (ViewMeter)findViewById(R.id.meter4);
        meter4.setLabel(sensorInfo.value3Label);
        meter4.setMinMax(sensorInfo.value3Min, sensorInfo.value3Max);
        meter4.value = sensorInfo.value3Min;

        sampleCount = 0; // increments with each sensor event
    }

    @Override
    public void onStart()
    {
        Log.i(MYDEBUG, "onStart! (main)");
        super.onStart();
    }

    // implement onResume and onPause as per guidelines (to conserve power)
    @Override
    public void onResume()
    {
        Log.i(MYDEBUG, "onResume! (main) --> sensor listener registered");
        super.onResume();
        sensorManager.registerListener(this, sensor, samplingRate);
    }

    @Override
    public void onPause()
    {
        Log.i(MYDEBUG, "onPause! (main) --> sensor listener unregistered");
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onRestart()
    {
        Log.i(MYDEBUG, "onRestart! (main)");
        super.onRestart();
    }

    @Override
    public void onDestroy()
    {
        Log.i(MYDEBUG, "onDestroy! (main)");
        super.onDestroy();
    }

    @Override
    public void onStop()
    {
        Log.i(MYDEBUG, "onStop! (main)");
        super.onStop();
    }

    // callback for sensor events (not used)
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy)
    {
        // not used (but we need to provide an implementation anyway)
    }

    // callback for sensor events (this is the main one!)
    @Override
    public void onSensorChanged(SensorEvent se)
    {
        ++sampleCount; // keep a running tally of the number of sensor events

        // compute the time delta since the last sensor event
        nowSave = now;
        now = se.timestamp;
        delta = nowSave > 0 ? (now - nowSave) / 1000000 : 0; // milliseconds since last event

        // get values from sensors (up to four)
        x = se.values.length > 0 ? se.values[0] : xMeter.min;
        y = se.values.length > 1 ? se.values[1] : yMeter.min;
        z = se.values.length > 2 ? se.values[2] : zMeter.min;
        m4 = se.values.length > 3 ? se.values[3] : meter4.min;

        // update meters with sensor values
        xMeter.updateMeter(x);
        yMeter.updateMeter(y);
        zMeter.updateMeter(z);
        meter4.updateMeter(m4);

        // if infoPanel full, clear it (but refresh the header data)
        if (infoPanel.getLineCount() * infoPanel.getLineHeight() > infoPanel.getHeight())
            infoPanel.setText(headerData);

        // update info panel
        String newInfo = String.format(Locale.CANADA, "(%d, %d) ", sampleCount, delta);
        infoPanel.append(newInfo);
    }
}
