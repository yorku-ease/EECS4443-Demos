package ca.yorku.eecs.mack.demotiltmeter;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

public class DemoTiltMeterSetup extends Activity implements Button.OnClickListener
{
    final String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // IMPORTANT: keep these two array in sync (i.e., edit both or none)
    final String[] SAMPLING_RATE_STRING = {"Normal", "UI", "Game", "Fastest"};
    final static int[] SAMPLING_RATE = {
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_UI,
            SensorManager.SENSOR_DELAY_GAME,
            SensorManager.SENSOR_DELAY_FASTEST
    };

    // sensor mode constants (see API for discussion)
    final static int ORIENTATION = 0;
    final static int ACCELEROMETER_AND_MAGNETIC_FIELD = 1;
    final static int ACCELEROMETER_ONLY = 2;
    final static int NO_MODES_AVAILABLE = -1;

    private Spinner spinSamplingRate;

    private int sensorMode;
    private int samplingRate;

    private Button okButton, exitButton;
    private RadioButton rb1, rb2, rb3;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setup);

        spinSamplingRate = (Spinner)findViewById(R.id.paramSamplingRate);
        ArrayAdapter<CharSequence> adapter1 = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
                SAMPLING_RATE_STRING);
        spinSamplingRate.setAdapter(adapter1);

        rb1 = (RadioButton)findViewById(R.id.radiobutton1);
        rb2 = (RadioButton)findViewById(R.id.radiobutton2);
        rb3 = (RadioButton)findViewById(R.id.radiobutton3);
        okButton = (Button)findViewById(R.id.okButton);
        exitButton = (Button)findViewById(R.id.exitButton);

        rb1.setOnClickListener(this);
        rb2.setOnClickListener(this);
        rb3.setOnClickListener(this);
        okButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        /**
         * Check which sensor modes are available and enable/disable buttons accordingly.  We pick the first available
         * sensor mode as the default, and disable buttons for any sensor modes that are not available.
         */
        SensorManager sm = (SensorManager)getSystemService(SENSOR_SERVICE);
        sensorMode = NO_MODES_AVAILABLE; // assume the worst, but this will likely change as per available sensors

        // 1. Can we use ORIENTATION as the sensor mode?
        if (sm.getDefaultSensor(Sensor.TYPE_ORIENTATION) == null)
            rb1.setEnabled(false); // No!
        else
        {
            // Yes! Orientation sensor is available (use as default)
            rb1.toggle();
            sensorMode = ORIENTATION;
        }

        // 2. Can we use ACCELEROMETER_AND_MAGNETIC_FIELD as the sensor mode?
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null || sm.getDefaultSensor(Sensor.TYPE_GRAVITY) == null)
            rb2.setEnabled(false); // No!
        else if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && sm.getDefaultSensor(Sensor.TYPE_GRAVITY) !=
                null && !rb1.isEnabled())
        {
            // Yes! (but only set as default if ORIENTATION is not available)
            rb2.toggle();
            sensorMode = ACCELEROMETER_AND_MAGNETIC_FIELD;
        }

        // 3. Can we use ACCELEROMETER_ONLY as the sensor mode?
        if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null)
            rb3.setEnabled(false); // No!
        else if (sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) != null && !rb1.isEnabled() && !rb2.isEnabled())
        {
            // Yes! (but only set as default if the other two modes are not available)
            rb3.toggle();
            sensorMode = ACCELEROMETER_ONLY;
        }

        // if we haven't got a sensor mode yet, we're done!
        if (sensorMode == NO_MODES_AVAILABLE)
        {
            Log.i(MYDEBUG, "Can't run app (no sensors available)");
            this.finish();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == rb1)
        {
            sensorMode = DemoTiltMeterSetup.ORIENTATION;
        } else if (v == rb2)
        {
            sensorMode = DemoTiltMeterSetup.ACCELEROMETER_AND_MAGNETIC_FIELD;
        } else if (v == rb3)
        {
            sensorMode = DemoTiltMeterSetup.ACCELEROMETER_ONLY;
        } else if (v == okButton)
        {
            // put the user's choice in a bundle
            Bundle b = new Bundle();
            samplingRate = SAMPLING_RATE[spinSamplingRate.getSelectedItemPosition()];
            b.putInt("sensorMode", sensorMode);
            b.putInt("sensorSamplingRate", samplingRate);

            // start main activity (passing the bundle to it)
            Intent i = new Intent(getApplicationContext(), DemoTiltMeterActivity.class);
            i.putExtras(b);
            startActivity(i);

            // comment out -- so app returns to setup dialog when the user presses Back button
            //finish();
        } else if (v == exitButton)
        {
            super.onDestroy(); // cleanup
            this.finish(); // terminate
        }
    }
}
