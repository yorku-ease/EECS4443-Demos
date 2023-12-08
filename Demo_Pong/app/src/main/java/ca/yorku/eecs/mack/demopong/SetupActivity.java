package ca.yorku.eecs.mack.demopong;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class SetupActivity extends Activity
{
	// final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static int OK = 100;

	// gain values for velocity control
	final static float LOW = 0.33f;
	final static float MEDIUM_LOW = 0.5f;
	final static float MEDIUM = 1f;
	final static float MEDIUM_HIGH = 1.5f;
	final static float HIGH = 2.0f;

	Spinner spinParticipant, spinCondition, spinGroup, spinLevel, spinTrials, spinSequences, spinTiltRange,
			spinTiltCenter, spinGainVelocityControl;
	CheckBox checkVibrotactileFeedback, checkAuditoryFeedback, checkDisableDataCollection;

	String[] participantArray = { "P01", "P01", "P02", "P03", "P04", "P05", "P06", "P07", "P08", "P09", "P10", "P11",
			"P12", "P13", "P14", "P15", "P16", "P17", "P18", "P19", "P20", "P21", "P22", "P23", "P24", "P25" };
	String[] conditionArray = { "C01", "Tilt_Position", "Tilt_Velocity", "Touch_Position", "Touch_Velocity", "C01",
			"C02", "C03", "C04", "C05", "C06", "C07", "C08", "C09", "C10" };
	String[] groupArray = { "G01", "G01", "G02", "G03", "G04", "G05", "G06", "G07", "G08", "G09", "G10", "G11", "G12",
			"G13", "G14", "G15", "G16", "G17", "G18", "G19", "G20", "G21", "G22", "G23", "G24", "G25" };
	String[] levelArray = { "1", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };
	String[] trialsArray = { "5", "1", "2", "5", "10", "15", "20", "25", };
	String[] sequencesArray = { "15", "1", "5", "10", "11", "12", "13", "14", "15", "20", "25", "100" };
	String[] tiltRangeArray = { "50", "20", "30", "40", "50", "60", "70", "80" };
	String[] tiltCenterArray = { "35", "0", "15", "25", "35", "45", "55" };
	String[] gainVelocityControlArray = { "Medium", "Low", "Medium low", "Medium", "Medium high", "High" };
	float[] gainVelocityControlValues = { MEDIUM, LOW, MEDIUM_LOW, MEDIUM, MEDIUM_HIGH, HIGH };

	String participantCode, conditionCode, groupCode;
	int level, trials, sequences;
	int tiltRange, tiltCenter;
	float gainVelocityControl;
	boolean vibrotactileFeedback;
	boolean auditoryFeedback;
	boolean disableDataCollection;

	SharedPreferences sp;
	SharedPreferences.Editor spe;

	// Called when the activity is first created
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.setup);

		loadSettings();

		// for the spinners the setting loaded replaces the 1st element in the array
		participantArray[0] = participantCode;
		conditionArray[0] = conditionCode;
		groupArray[0] = groupCode;
		levelArray[0] = "" + level;
		trialsArray[0] = "" + trials;
		sequencesArray[0] = "" + sequences;
		tiltRangeArray[0] = "" + tiltRange;
		tiltCenterArray[0] = "" + tiltCenter;
		gainVelocityControlArray[0] = getGainVelocityControlString(gainVelocityControl);

		// get references to widget elements
		spinParticipant = (Spinner)findViewById(R.id.param_participant);
		spinCondition = (Spinner)findViewById(R.id.param_condition);
		spinGroup = (Spinner)findViewById(R.id.param_group);
		spinLevel = (Spinner)findViewById(R.id.param_level);
		spinTrials = (Spinner)findViewById(R.id.param_trials);
		spinSequences = (Spinner)findViewById(R.id.param_sequences);
		spinTiltRange = (Spinner)findViewById(R.id.param_tilt_range);
		spinTiltCenter = (Spinner)findViewById(R.id.param_tilt_center);
		spinGainVelocityControl = (Spinner)findViewById(R.id.param_gain_velocity_control);
		checkVibrotactileFeedback = (CheckBox)findViewById(R.id.param_vibrotactile_feedback);
		checkAuditoryFeedback = (CheckBox)findViewById(R.id.param_auditory_feedback);
		checkDisableDataCollection = (CheckBox)findViewById(R.id.param_disable_data_collection);

		// initialise spinner adapters
		ArrayAdapter<CharSequence> adapterPC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				participantArray);
		spinParticipant.setAdapter(adapterPC);

		ArrayAdapter<CharSequence> adapterSS = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				conditionArray);
		spinCondition.setAdapter(adapterSS);

		ArrayAdapter<CharSequence> adapterG = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, groupArray);
		spinGroup.setAdapter(adapterG);

		ArrayAdapter<CharSequence> adapterC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, levelArray);
		spinLevel.setAdapter(adapterC);

		ArrayAdapter<CharSequence> adapterSPB = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle, trialsArray);
		spinTrials.setAdapter(adapterSPB);

		ArrayAdapter<CharSequence> adapterS = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				sequencesArray);
		spinSequences.setAdapter(adapterS);

		ArrayAdapter<CharSequence> adapterTR = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				tiltRangeArray);
		spinTiltRange.setAdapter(adapterTR);

		ArrayAdapter<CharSequence> adapterTC = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				tiltCenterArray);
		spinTiltCenter.setAdapter(adapterTC);

		ArrayAdapter<CharSequence> adapterVCA = new ArrayAdapter<CharSequence>(this, R.layout.spinnerstyle,
				gainVelocityControlArray);
		spinGainVelocityControl.setAdapter(adapterVCA);

		checkVibrotactileFeedback.setChecked(vibrotactileFeedback);
		checkAuditoryFeedback.setChecked(auditoryFeedback);
		checkDisableDataCollection.setChecked(disableDataCollection);
	}

	// given the gain float, return the corresponding string
	public String getGainVelocityControlString(float gain)
	{
		if (gain == LOW)
			return "Low";
		else if (gain == MEDIUM_LOW)
			return "Medium low";
		else if (gain == MEDIUM)
			return "Medium";
		else if (gain == MEDIUM_HIGH)
			return "Medium high";
		else if (gain == HIGH)
			return "High";
		else
			return "?";
	}

	public void loadSettings()
	{
		// initialize SharedPreferences instance
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		Resources r = this.getResources();
		participantCode = sp.getString(r.getString(R.string.settings_participant_key), "P99");
		conditionCode = sp.getString(r.getString(R.string.settings_condition_key), "C99");
		groupCode = sp.getString(r.getString(R.string.settings_group_key), "G99");
		level = Integer.parseInt(sp.getString(r.getString(R.string.settings_level_key), "1"));
		trials = Integer.parseInt(sp.getString(r.getString(R.string.settings_trials_key), "5"));
		sequences = Integer.parseInt(sp.getString(r.getString(R.string.settings_sequences_key), "100"));
		tiltRange = Integer.parseInt(sp.getString(r.getString(R.string.settings_tilt_range_key), "50"));
		tiltCenter = Integer.parseInt(sp.getString(r.getString(R.string.settings_tilt_center_key), "35"));
		gainVelocityControl = sp.getFloat(r.getString(R.string.settings_gain_velocity_control_key), 1f);
		vibrotactileFeedback = sp.getBoolean(r.getString(R.string.settings_vibrotactile_feedback_key), true);
		auditoryFeedback = sp.getBoolean(r.getString(R.string.settings_auditory_feedback_key), true);
		disableDataCollection = sp.getBoolean(r.getString(R.string.settings_disable_data_collection_key), true);
	}

	// called when the "Save" button is pressed
	public void clickSave(View view)
	{
		spe = sp.edit();
		Resources r = this.getResources();
		spe.putString(r.getString(R.string.settings_participant_key), participantArray[spinParticipant
				.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_condition_key), conditionArray[spinCondition
				.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_group_key), groupArray[spinGroup.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_level_key), levelArray[spinLevel.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_trials_key), trialsArray[spinTrials.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_sequences_key), sequencesArray[spinSequences
				.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_tilt_range_key), tiltRangeArray[spinTiltRange
				.getSelectedItemPosition()]);
		spe.putString(r.getString(R.string.settings_tilt_center_key), tiltCenterArray[spinTiltCenter
				.getSelectedItemPosition()]);
		spe.putFloat(r.getString(R.string.settings_gain_velocity_control_key),
				gainVelocityControlValues[spinGainVelocityControl.getSelectedItemPosition()]);
		spe.putBoolean(r.getString(R.string.settings_vibrotactile_feedback_key), checkVibrotactileFeedback.isChecked());
		spe.putBoolean(r.getString(R.string.settings_auditory_feedback_key), checkAuditoryFeedback.isChecked());
		spe.putBoolean(r.getString(R.string.settings_disable_data_collection_key), checkDisableDataCollection
				.isChecked());
		spe.apply();
		Toast.makeText(this, "Preferences saved!", Toast.LENGTH_SHORT).show();
	}

	// called with the "OK" button is clicked
	public void clickOK(View v)
	{
		// no need to send the parameters in a bundle as they are loaded explicitly in the main activity
		setResult(SetupActivity.OK);
		this.finish();
	}

	// called when the "Exit" button is pressed
	public void clickExit(View view)
	{
		this.finish(); // terminate
	}
}
