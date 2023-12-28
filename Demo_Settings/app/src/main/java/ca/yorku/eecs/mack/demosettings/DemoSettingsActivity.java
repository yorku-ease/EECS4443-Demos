package ca.yorku.eecs.mack.demosettings;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

/**
 * Demo_Settings - a simple application to demonstrate Settings. Use of a custom-designed alert dialog is also
 * demonstrated. <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/topics/ui/settings.html">Settings</a> <li><a
 * href="http://developer.android.com/guide/topics/ui/dialogs.html">Dialogs</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/preference/Preference.html">
 * <code>Preference</code></a> <li><a href="http://developer.android.com/reference/android/preference/PreferenceManager.html">
 * <code>PreferenceManager</code></a> <li><a href="http://developer.android.com/reference/android/content/SharedPreferences.html">
 * <code>SharedPreferences</code></a> <li><a href="http://developer.android.com/reference/android/preference/PreferenceFragment.html">
 * <code>PreferenceFragment</code></a> <li><a href="http://developer.android.com/reference/android/app/DialogFragment.html">
 * <code>DialogFragment</code></a> </ul>
 *
 *
 * </blockquote>
 *
 * A <i>setting</i> is a value set by the user. A setting is also called a <i>preference</i>. Settings are an important
 * part of most mobile applications. They allow users to modify application features and behaviours. Furthermore, users
 * expect settings to persist from one invocation of the application to the next. These features are demonstrated here.
 * <p>
 *
 * This demo is an extension of Demo_Android. The (deliberate) bug in Demo_Android is fixed and the modifications
 * requested for EECS4443 Lab #1 are included. Several other modifications are also included: <p>
 *
 * <ul> <li>The count is saved and restored when the screen rotates. <li>Separate layouts are used for portrait and
 * landscape display orientations. <li>Min and max settings are added to limit the count range. <li>Vibration is
 * (optionally) output if the count is about to exceed the min or max. <li>The <code>android:background</code>
 * attributes are removed in the XML layout files. <li>The Android style <code>style/Theme.Holo</code> is specified in
 * the manifest. <li>A confirmation dialog pops up if the user taps the Exit button. </ul> <p>
 *
 * As with Demo_Android, this application is rather silly in what it does. The main goal is to demonstrate how
 * to implement user selectable settings via Android's Preference APIs. We also demonstrate how to implement a
 * custom-designed alert dialog. <p>
 *
 * There are three settings supported: <p>
 *
 * <ol> <li>Vibration &ndash; a checkbox setting to enable/disable vibration if the count is about to exceed the min or
 * max limit<p>
 *
 * <li>Min &ndash; a list setting to set the minimum allowable value for the count<p>
 *
 * <li>Max &ndash; a list setting to set the maximum allowable value for the count </ol> <p>
 *
 * Let's see how these fit with the overall operation of Demo_Settings. Below are screen snaps of the application upon
 * launch in portrait and landscape modes: <p>
 *
 * <center> <a href="DemoSettings-1.jpg"><img src="DemoSettings-1.jpg" width=" 300" alt="image"></a>&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp; <a href="DemoSettings-2.jpg"><img src="DemoSettings-2.jpg" width=" 500" alt="image"></a> </center> <p>
 *
 * The count is incremented or decremented by tapping the Increment or Decrement buttons. The count is displayed. So too
 * are the values of a min field and a max field. The count is constrained to lie within min and max (inclusive). If the
 * count is min and the user taps Decrement, the counts stays the same. Furthermore, a 100-ms vibrotactile pulse is
 * output (if the vibration setting is checked). And that's about it! <p>
 *
 * There are three steps to consider in building a UI that provides and manages settings: <p>
 *
 * <ul> <li>Retrieving settings <li>Saving settings <li>Changing settings </ul> <p>
 *
 * Let's examine each. <p>
 *
 * <!------------------------------------------------------------------------------------------> <b>Retrieving
 * Settings</b> <p>
 *
 * Settings for Android applications are stored in the device's memory as key-value pairs. Importantly, the location of
 * the settings and the details for managing them are hidden from the user or developer. This is possible through
 * Android's Preference APIs, which manage the settings. Of particular interest is the <code>SharedPreferences</code>
 * interface, as it provides the ability to access and modify settings. <p>
 *
 * The settings (aka "preferences") are considered "shared" because they are accessible from any activity in the
 * application. Three settings are used in Demo_Settings (see above). They are retrieved when the application launches
 * as follows: <p>
 *
 * <pre>
 *      SharedPreferences sp;
 *      ...
 *      PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
 *      sp = PreferenceManager.getDefaultSharedPreferences(this);
 * </pre>
 *
 * The 1st line above is a declaration of a <code>SharedPreferences</code> instance variable, <code>sp</code>. The 2nd
 * and 3rd lines are executed from <code>onCreate</code>.
 *
 * The 2nd line initializes the settings based on default values in <code>/res/xml/preferences.xml</code>. This
 * statement is included for the demo, but is not needed since the <i>get</i>-methods (see below) specify a default
 * value. However, <code>setDefaultValues</code> could be used in a app to restore default (factory) values, if such a
 * feature is to be implemented. (Read the API for <code>setDefaultValues</code> for additional details.) The 3rd line
 * retrieves a reference to the application's shared preferences (settings). The values of the settings are retrieved as
 * follows: <p>
 *
 * <pre>
 *      vibrateOnLimit = sp.getBoolean(VIBRATE_KEY, false);
 *      minValue = Integer.parseInt(sp.getString(MIN_KEY, "-5"));
 *      maxValue = Integer.parseInt(sp.getString(MAX_KEY, "5"));
 * </pre>
 *
 * The settings are stored as key-value pairs. They are retrieved using get-methods of the
 * <code>SharedPreferences</code> interface. The get-methods above have two arguments. The first argument is the key (a
 * string). The corresponding value is retrieved and assigned as shown. The second argument is the default value which
 * is returned if there is no corresponding entry for the key. The actual use of the settings in Demo_Settings is
 * straight-forward. See the source code for further details. <p>
 *
 * <!------------------------------------------------------------------------------------------> <b>Saving Settings</b>
 * <p>
 *
 * Settings are easily saved using <i>put</i>-methods. For example, if the user changes <code>minValue</code> through
 * the app's UI, the new value is saved as follows: <p>
 *
 * <pre>
 *      SharedPreferences.Editor spe;
 *      ...
 *      spe = sp.getEditor();
 *      spe.putInt(MIN_KEY, minValue);
 * </pre>
 *
 * NOTE: The code above does not explicitly appear in this demo. In Demo_Settings, settings are managed through use of
 * Android's <code>Preference</code> APIs. The overall framework specifies how to build a UI that is consistent with the
 * user experience in other Android apps (e.g., system settings). The following is a high-level summary. A complete
 * description is found the API Guide for "Settings" (link above). <p>
 *
 * <!------------------------------------------------------------------------------------------> <b>Changing
 * Settings</b> <p>
 *
 * The vibration, min, and max values can be changed through settings. The settings are accessed through the Action
 * Overflow entry in the Action Bar (below left). If the user taps Action Overflow, the Options Menu appears. For
 * Demo_Settings, the Options Menu is very simple &ndash; just two entries: Settings and Help. There is currently no
 * implementation for Help (although popup Toast appears if this item is selected). If the user taps Settings, the
 * settings menu appears (below center). Tapping Minimum brings up the list of choices for the <code>minValue</code>
 * setting (below right) <p>
 *
 * <center> <a href="DemoSettings-3.jpg"><img src="DemoSettings-3.jpg" width=" 300" alt="image"></a>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;<a href="DemoSettings-4.jpg"><img src="DemoSettings-4.jpg" width=" 300"
 * alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoSettings-5.jpg"><img src="DemoSettings-5.jpg" width="
 * 300" alt="image"></a> </center><p>
 *
 * The code to create the settings UI is now described.<p>
 *
 * When the user taps the Settings entry in the Options menu, <code>SettingsActivity</code> is launched: <p>
 *
 * <pre>
 *       Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
 *       startActivityForResult(i, SETTINGS);
 * </pre>
 *
 * <p> <code>SettingsActivity</code> is an activity dedicated to getting and managing user settings. This includes
 * creating the UI the user interacts with. The initial UI is the Settings menu, which includes three items (above
 * center). The UI for each item depends on the type of item. The UI for the Vibrate entry is a instance of
 * <code>CheckBoxPreference</code> and appears directly in the Settings menu. The UIs for the Minimum and Maximum
 * entries are instances of <code>ListPreference</code>. The list pops up when the corresponding entry is selected in
 * the Settings menu (above right). The initial Settings menu as well as the UI for each entry are specified in
 * <code>res/xml/preferences.xml</code> (consult for details). Loading the UI specified in the XML file is a two-step
 * process. First, the <code>onCreate</code> method in <code>SettingsActivity</code> specifies a
 * <code>PreferenceFragment</code> as the UI for the activity: <p>
 *
 * <pre>
 *      getFragmentManager()
 *           .beginTransaction()
 *           .replace(android.R.id.content, new MyPreferenceFragment())
 *           .commit();
 * </pre>
 *
 * The actual fragment is an instance of <code>MyPreferenceFragment</code>, which extends
 * <code>PreferenceFragment</code>. The second step occurs through <code>onCreate</code> in
 * <code>MyPreferenceFragment</code>. It is here that the XML file is specified as the resource to load: <p>
 *
 * <pre>
 *     addPreferencesFromResource(R.xml.preferences);
 * </pre>
 *
 * Although the description above seems convoluted, bear in mind that these steps provide all the services necessary to
 * (a) define the look-and-feel of the settings menu, (b) pop-up the UIs associated with each setting, and (c) accept,
 * change, and store settings according to the user's selections. <p>
 *
 * As the user changes settings, the new values are stored in the device's memory, but the details are hidden. When
 * finished, the user closes the setting activity by tapping Back on the Navigation Bar. Execution returns to the main
 * activity where <code>onActivityResult</code> executes. The new settings are then loaded in the application. <p>
 *
 * The above is a high-level summary. For a more complete understanding, it is recommended that you read the Android API
 * Guide for Settings (link above) while studying the code and files herein. <p>
 *
 * <!------------------------------------------------------------------------------------------> <b>Confirmation
 * Dialog</b> <p>
 *
 * When the user taps Exit, a dialog pops up asking for confirmation that the user really wants to exit the application:
 * <p>
 *
 * <center> <a href="DemoSettings-6.jpg"><img src="DemoSettings-6.jpg" width=" 300" alt="image"></a> </center> <p>
 *
 * The dialog is an instance of <code>ExitAlertDialog</code>, a custom class that extends <code>DialogFragment</code>.
 * The dialog pops up from the <code>onCick</code> callback for the Exit button: <p>
 *
 * <pre>
 *      showExitDialog();
 * </pre>
 *
 * This method includes just two lines: <p>
 *
 * <pre>
 *      DialogFragment dialog = new ExitAlertDialog();
 *      dialog.show(getFragmentManager(), "ExitAlertDialogFragment");
 * </pre>
 *
 * The <code>ExitAlertDialog</code> class defines a listener interface called <code>ExitAlertListener</code> with two
 * methods, one for confirm ("OK") and one for cancel ("Cancel"). The methods are implemented in the main activity.
 * Organizing the dialog in this way means the user's response to the dialog is handled in the main activity. This is
 * described in the API Guide for Dialogs (link above). See, in particular, the subtopic "Passing Events Back to the
 * Dialog's Host". As always, consult the source code for complete details. <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */
public class DemoSettingsActivity extends Activity implements OnClickListener, ExitAlertDialog.ExitAlertListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int SETTINGS = 0;
    final static int HELP = 1;
    final static String CLICK_COUNT = "clickCount";

    Button incrementButton, decrementButton, exitButton, resetButton;
    TextView countValueView, minValueView, maxValueView;
    Vibrator vib;
    SharedPreferences sp;

    int clickCount;
    boolean vibrateOnLimit;
    int minValue, maxValue;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
        Log.i(MYDEBUG, "Initialization done. Application running.");
    }

    private void initialize()
    {
        // get the buttons and text view from the layout manager (rather than instantiate them)
        incrementButton = findViewById(R.id.incbutton);
        decrementButton = findViewById(R.id.decbutton);
        resetButton = findViewById(R.id.resetbutton);
        exitButton = findViewById(R.id.exitbutton);
        minValueView = findViewById(R.id.min_value_view);
        countValueView = findViewById(R.id.count_value_view);
        maxValueView = findViewById(R.id.max_value_view);

        incrementButton.setOnClickListener(this);
        decrementButton.setOnClickListener(this);
        resetButton.setOnClickListener(this);
        exitButton.setOnClickListener(this);

        // initialize click count
        clickCount = 0;
        countValueView.setText(String.format(Locale.CANADA, "%d", clickCount));

        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

		/*
         * The following statement is included for the demo, but is not needed since the get-methods
		 * specify a default value. The statement below could be used in a app to restore default
		 * (factory) values, if such a feature is to be implemented (but read the API for additional
		 * details).
		 */
        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);

        // initialize SharedPreferences instance
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        loadSettings();
    }

    /*
     * Put these statements in a method because the settings are loaded both from onCreate, when the
     * app is launched, and from onActivityResult, after the user changes the settings (via the
     * Options Menu).
     */
    private void loadSettings()
    {
        // build keys (makes the code more readable)
        final String VIBRATE_KEY = getBaseContext().getString(R.string.pref_vibrate);
        final String MIN_KEY = getBaseContext().getString(R.string.pref_min_title);
        final String MAX_KEY = getBaseContext().getString(R.string.pref_max_title);

        // retrieve values associated with keys
        vibrateOnLimit = sp.getBoolean(VIBRATE_KEY, false);
        minValue = Integer.parseInt(sp.getString(MIN_KEY, "-5"));
        maxValue = Integer.parseInt(sp.getString(MAX_KEY, "5"));

        // ensure the click count is >= min and <= max (adjust if necessary)
        if (clickCount < minValue)
            clickCount = minValue;
        if (clickCount > maxValue)
            clickCount = maxValue;

        minValueView.setText(String.format(Locale.CANADA, "%d", minValue));
        maxValueView.setText(String.format(Locale.CANADA, "%d", maxValue));
    }

    @Override
    public void onClick(View v)
    {
        if (v == incrementButton)
        {
            if (clickCount < maxValue)
                ++clickCount;
            else if (vibrateOnLimit)
                vib.vibrate(100);

        } else if (v == decrementButton)
        {
            if (clickCount > minValue)
                --clickCount;
            else if (vibrateOnLimit)
                vib.vibrate(100);

        } else if (v == resetButton)
        {
            clickCount = 0;

        } else if (v == exitButton)
        {
            /*
             * Show the exit alert dialog. The user's response is processed in this activity, rather
			 * than in the dialog. This occurs because we are implementing the dialog's listener
			 * methods. See the activity signature above and the implementation of
			 * onDialogPositiveClick and onDialogNegativeClick below.
			 */
            showExitAlertDialog();
        } else
            Log.i(MYDEBUG, "Oops: Invalid Click Event!");

        // update click count
        countValueView.setText(String.format(Locale.CANADA, "%d", clickCount));
    }

    // In-class exercise: Insert code here to handle the user pressing the devices "Back" button in the same manner
    // as pressing the Exit button.  Hint: Perhaps start with a Google search for "Android back button pressed".

    @Override
    public void onBackPressed()
    {
        showExitAlertDialog();
        //super.onBackPressed();
    }


    /*
     * The next three methods support the exit alert dialog. Before exiting, the user is prompted to
     * confirm. The approach here follows the description in the Android API Guide for Dialogs (in
     * particular, the subsection titled "Passing Events to Back to the Dialog's Host"; link below).
     * This allows us to handle the dialog's response (OK/Cancel) here, by implementing the listener
     * methods associated with the dialog. See...
     *
     * http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
     */
    public void showExitAlertDialog()
    {
        // create an instance of the dialog fragment and show it
        DialogFragment dialog = new ExitAlertDialog();
        dialog.show(getFragmentManager(), "ExitAlertDialogFragment");
    }

    /*
     * The dialog fragment receives a reference to this Activity through the Fragment.onAttach()
     * callback, which it uses to call the following methods defined by the
     * ExitAlertDialog.ExitAlertListener interface.
     */
    @Override
    public void onDialogPositiveClick(DialogFragment dialog)
    {
        // a tap on the dialog's positive button (OK)
        this.finish();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog)
    {
        // a tap on the dialog's negative button (Cancel)
    }

    // setup an Options menu (used for Settings)
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(SETTINGS, SETTINGS, SETTINGS, R.string.menu_settings);
        menu.add(HELP, HELP, HELP, R.string.menu_help);
        return true;
    }

    // handle a "Settings" selection in the options menu by launching SettingsActivity
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case SETTINGS:
                // launch the SettingsActivity to allow the user to change the app's settings
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(i, SETTINGS); // see comment for onActivityResult
                return true;

            case HELP:
                Toast.makeText(this, "Help! (not implemented)", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }

    /*
     * We're using the "for result" version of startActivity in order to get a call to
     * onActivityResult after the user returns from accessing the app's settings. We use this call
     * to update the app's behaviour based on the settings. This is done via loadSettings.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // SETTINGS is the only possibility, but we'll check anyway
        if (requestCode == SETTINGS)
        {
            loadSettings();
        }
    }

    /*
     * The next two methods are needed to maintain the click count when the screen rotates. We don't
     * need to save/restore the min/max settings, since this is done via the application's
     * SharedPreferences in onCreate (which also executes after a screen rotation).
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putInt(CLICK_COUNT, clickCount);
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        clickCount = savedInstanceState.getInt(CLICK_COUNT);

		/*
         * The next statement is needed because countValueView is also initialized from onCreate.
		 * After a screen rotation, onCreate executes first and initializes clickCount = 0. We
		 * restore the correct value here.
		 */
        countValueView.setText(String.format(Locale.CANADA, "%d", clickCount));
    }
}
