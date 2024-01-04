package ca.yorku.eecs.mack.demosensors;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

@TargetApi(22)
public class DemoSensorsSetup extends Activity implements OnItemSelectedListener {
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // IMPORTANT: keep these two arrays in sync (i.e., edit both or none)
    final static String[] SAMPLING_RATE_STRING = {"Normal", "UI", "Game", "Fastest"};
    final static int[] SAMPLING_RATE = {
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_UI,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_FASTEST};

    final static String TRY_AGAIN_MESSAGE = "Not supported in this demo. Please select again.";

    // Define an array of the most common SENSOR_INFO objects. Fiddle with min/max values depending on sensor.
    final static SensorInfo[] SENSOR_INFO = {
            new SensorInfo(Sensor.TYPE_ACCELEROMETER, "Accelerometer", "X Value", -12f, 12f, "Y Value",
                    -12f, 12f, "Z Value", -12f, 12f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_AMBIENT_TEMPERATURE, "Ambient Temperature", "Degrees C", 0f, 40f, "" +
                    " ", 0f, 0f, " ", 0f, 0f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_TEMPERATURE, "Temperature", "Degrees C", 0f, 40f, " ", 0f, 0f, " ",
                    0f, 0f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_GRAVITY, "Gravity", "m/s^2 (x)", -12f, 12f, "m/s^2 (y)", -12f, 12f,
                    "m/s^2 (z)", -12f, 12f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_GYROSCOPE, "Gyroscope", "Speed (Tx)", -3f, 3f, "Speed (Ty)", -3f, 3f,
                    "Speed (Tz)", -3f, 3f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_GYROSCOPE_UNCALIBRATED, "Gyroscope (uncalibrated)", "Speed (Tx)",
                    -3f, 3f, "Speed (Ty)", -3f, 3f, "Speed (Tz)", -3f, 3f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_LIGHT, "Light Sensor", "Lux", 0f, 3000f, " ", 0f, 0f, " ", 0f, 0f, " ", 0f,
                    0f),
            new SensorInfo(Sensor.TYPE_STEP_COUNTER, "Step Cnt", "Step Cnt", 0f, 10000f, " ", 0f, 0f, " ",
                    0f, 0f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_STEP_DETECTOR, "Step Det", "Step Det", 0f, 10000f, " ", 0f, 0f, " ",
                    0f, 0f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_LINEAR_ACCELERATION, "Linear Acceleration", "X Value", -12f, 12f,
                    "Y Value", -12f, 12f, "Z Value", -12f, 12f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_SIGNIFICANT_MOTION, "Significant Motion", "X Value", -12f, 12f, "Y " +
                    "Value", -12f, 12f, "Z Value", -12f, 12f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_MAGNETIC_FIELD, "Magnetic Field", "uTesla-x", -50f, 50f,
                    "uTesla-y", -50f, 50f, "uTesla-z", -50f, 50f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED, "Magnetic Field (uncalibrated)",
                    "uTesla-x", -50f, 50f, "uTesla-y", -50f, 50f, "uTesla-z", -50f, 50f, " ", 0f, 0f),
            new SensorInfo(Sensor.TYPE_PRESSURE, "Pressure", "hPa", 0f, 10f, " ", 0f, 0f, " ", 0f, 0f, " " +
                    "", 0f, 0f),
            new SensorInfo(Sensor.TYPE_PROXIMITY, "Proximity", "cm", 0f, 10f, " ", 0f, 0f, " ", 0f, 0f, " " +
                    "", 0f, 0f),
            new SensorInfo(Sensor.TYPE_ROTATION_VECTOR, "Rotation Vector", "x*sin(T/2)", -1f, 1f, "y*sin" +
                    "(T/2)", -1f,
                    1f, "z*sin(T/2)", -1f, 1f, "cos(T/2)", -1f, 1f),
            new SensorInfo(Sensor.TYPE_GAME_ROTATION_VECTOR, "Game Rotation Vector", "x*sin(T/2)", -1f,
                    1f, "y*sin" + "(T/2)", -1f, 1f, "z*sin(T/2)", -1f, 1f, "cos(T/2)", -1f, 1f),
            new SensorInfo(Sensor.TYPE_GEOMAGNETIC_ROTATION_VECTOR, "Geomagnetic Rotation Vector", "x*sin" +
                    "(T/2)", -1f, 1f, "y*sin" + "(T/2)", -1f, 1f, "z*sin(T/2)", -1f, 1f, "cos(T/2)", -1f, 1f),
            new SensorInfo(Sensor.TYPE_ORIENTATION, "Orientation", "Azimuth", 0f, 360f, "Pitch", -180f, 180f,
                    "Roll", -90f, 90f, " ", 0f, 0f)};
    List<Sensor> sensorListTemp; // immutable
    List<Sensor> sensorList; // mutable (so we can sort the list by sensor type)
    int previousValidIdx;
    private Spinner spinSensor, spinSamplingRate;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(MYDEBUG, "onCreate! (setup)");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        spinSensor = (Spinner) findViewById(R.id.paramSensor);
        spinSamplingRate = (Spinner) findViewById(R.id.paramSamplingRate);

        // get a list of "all" sensors available on this device
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        if (sm != null)
            sensorListTemp = sm.getSensorList(Sensor.TYPE_ALL);
        else {
            Log.i(MYDEBUG, "Oops! No SensorManager. Bye!");
            return;
        }

        // sort by type
        sensorList = new ArrayList<>(sensorListTemp); // first, we need a modifiable List
        Collections.sort(sensorList, new ByType()); // now sort the List (using "by type" Comparator)

        // Output a list of sensors to logcat window (sorted by type)
        Iterator<Sensor> it = sensorList.iterator();
        Log.i(MYDEBUG, "Sensors supported...");
        while (it.hasNext()) {
            Sensor s = it.next();
            String str = String.format(Locale.CANADA, "   %5d : %s\n", s.getType(), s.getName());
            Log.i(MYDEBUG, str);
        }

        // build a string array of the sensor names (will appear in a spinner in the setup dialog)
        String[] sensorNameArray = new String[sensorList.size()];
        for (int i = 0; i < sensorNameArray.length; ++i)
            sensorNameArray[i] = sensorList.get(i).getName();

        // configure Sensor spinner to display the sensor names
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<>(this, R.layout.spinnerstyle,
                sensorNameArray);
        spinSensor.setAdapter(adapter1);

        // configure the Sampling rate spinner to display the available sampling rates
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<>(this, R.layout.spinnerstyle,
                SAMPLING_RATE_STRING);
        spinSamplingRate.setAdapter(adapter2);

        // listen for selections on the Sensor spinner (and check for a valid/supported selection)
        spinSensor.setOnItemSelectedListener(this);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // an OnItemSelectedListener method (must implement, but not needed)
    }

    /*
     * For the Sensor spinner, we need to check the user's selection because some sensors might appear that are not
     * supported by this demo (e.g., the "double twist" sensor). If the user selects one of these, use Toast to ask
     * the user to select again.
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (isValid(sensorList.get(pos).getType()))
            previousValidIdx = pos;
        else {
            Toast.makeText(this, TRY_AGAIN_MESSAGE, Toast.LENGTH_SHORT).show();
            spinSensor.setSelection(previousValidIdx);
        }
    }

    // Return true if the "type" of sensor is supported by this demo, false otherwise.
    private boolean isValid(int type) {
        for (SensorInfo si : SENSOR_INFO)
            if (type == si.type)
                return true;
        return false;
    }

    // Called when the "OK" button is pressed.
    public void clickOK(View view) {
        // get user's choices --> the sensor and the sampling rate

        // locate the sensor selected by the user in the SENSOR_INFO array
        int selectedSensor = sensorList.get(spinSensor.getSelectedItemPosition()).getType();
        int selectedInfoIdx;
        for (selectedInfoIdx = 0; selectedInfoIdx < SENSOR_INFO.length; ++selectedInfoIdx)
            if (selectedSensor == SENSOR_INFO[selectedInfoIdx].type)
                break;
        int samplingRateIdx = spinSamplingRate.getSelectedItemPosition();
        int samplingRate = SAMPLING_RATE[samplingRateIdx];

        // put the user's choices into a bundle
        Bundle b = new Bundle();
        b.putInt("sensorInfoIdx", selectedInfoIdx);
        b.putInt("sensorSamplingRate", samplingRate);

        // start main demo activity (passing the bundle to it)
        Intent i = new Intent(getApplicationContext(), DemoSensorsActivity.class);
        i.putExtras(b);
        startActivity(i);

        /*
         * Comment-out the call to finish. If commented out, we return to the setup dialog
         * when the user presses Back from DemoSensorsActivity. Thus, we get to pick another sensor
         * and try again.
         */
        // finish();
    }

    // Called when the "Exit" button is pressed.
    public void clickExit(View view) {
        this.finish(); // terminate
    }

    /*
     * The following lifecycle methods are included for an in-class demonstration of
     * activity-to-activity transitions. This demo app includes two activities, a setup activity and
     * a main activity. The app launches into the setup activity. The setup activity transitions to
     * the main activity and the main activity transitions back to the setup activity. The
     * setup-to-main transition occurs when the "OK" button is pressed in the setup activity. The
     * main-to-setup transition occurs when the "Back" button is pressed in the main activity.
     *
     * What lifecycle methods execute when the "OK" button is pressed (in the setup activity)? What
     * methods execute when the Back button is pressed (in the main activity)? To find out, run this
     * demo with the the LogCat window open (and with a device connected to the host in USB
     * debugging mode).
     */

    @Override
    public void onStart() {
        Log.i(MYDEBUG, "onStart! (setup)");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(MYDEBUG, "onResume! (setup)");
        super.onResume();
    }

    @Override
    public void onPause() {
        Log.i(MYDEBUG, "onPause! (setup)");
        super.onPause();
    }

    @Override
    public void onRestart() {
        Log.i(MYDEBUG, "onRestart! (setup)");
        super.onRestart();
    }

    @Override
    public void onStop() {
        Log.i(MYDEBUG, "onStop! (setup)");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.i(MYDEBUG, "onDestroy! (setup)");
        super.onDestroy();
    }

    // simple Comparator to sort the sensor list by sensor type
    private static class ByType implements Comparator<Sensor> {
        public int compare(Sensor a, Sensor b) {
            return a.getType() - b.getType();
        }
    }
}
