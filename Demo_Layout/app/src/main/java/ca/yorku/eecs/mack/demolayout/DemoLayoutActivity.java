package ca.yorku.eecs.mack.demolayout;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

/**
 * Demo_Layout - demonstrate the basic mechanisms for UI layout and for changing layouts when the screen orientation
 * changes. <p>
 * <p>
 * Additional concepts demonstrated include (a) saving and restoring an activity's variables when the screen orientation
 * changes, (b) the sequence of lifecycle methods for Android activities, and (c) the means for editing and viewing the
 * XML code for the UI layout. <p>
 * <p>
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/ui/declaring-layout.html"> Layouts</a> <li> <a
 * href="http://developer.android.com/guide/topics/resources/overview.html"> Resources Overview</a> <li> <a href=
 * "http://developer.android.com/guide/topics/resources/runtime-changes.html" >Handling Configuration Changes</a>
 * <li> <a
 * href= "https://developer.android.com/guide/components/activities/index.html" >Activities<a/>
 *
 * </ul> <p>
 * <p>
 * API References: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/view/View.html"> <code>View</code></a> <li> <a
 * href="http://developer.android.com/reference/android/view/ViewGroup.html"> <code>ViewGroup </code></a> <li> <a href=
 * "http://developer.android.com/reference/android/widget/LinearLayout.html"> <code>LinearLayout</code></a> <li> <a
 * href= "http://developer.android.com/reference/android/widget/LinearLayout.LayoutParams.html" >
 * <code>LinearLayout.LayoutParams</code></a> <li> <a href= "http://developer.android.com/reference/android/widget/RelativeLayout.html">
 * <code>RelativeLayout</code></a> <li> <a href= "http://developer.android.com/reference/android/widget/RelativeLayout.LayoutParams.html"
 * > <code>RelativeLayout.LayoutParams</code></a> <li> <a href="http://developer.android.com/reference/android/app/Activity.html">
 * <code>Activity</code> </a> <li> <a href="http://developer.android.com/reference/android/os/Bundle.html">
 * <code>Bundle</code></a>
 *
 * <li> <a href="http://developer.android.com/reference/android/util/Log.html"> <code>Log</code></a> </ul> <p>
 *
 * </blockquote>
 * <p>
 * This demo concerns UI layouts and changing layouts when the screen orientation changes. The following screen snaps
 * show the UI with the device in portrait orientation (left) and landscape orientation (right): <p>
 *
 * <center><a href="DemoLayout-1.jpg"><img src="DemoLayout-1.jpg" width=300></a> <a href="DemoLayout-2.jpg"><img
 * src="DemoLayout-2.jpg" width=500></a> </center> <p>
 * <p>
 * <!-----------------------------------------------------------------------------------------> <b>Layout</b> <p>
 * <p>
 * The visual arrangement of UI components, such as views, text fields and buttons, is determined by a <i>layout</i>.
 * There are two ways to create a UI layout: (i) at runtime via code, or (ii) at design time using a text-based
 * extensible markup language (XML). Generally, the latter approach is preferred. As noted in the Android API Guide for
 * Layouts, <p>
 *
 * <blockquote><i> Declaring your UI in XML allows you to separate the presentation of
 * your app from the code that controls its behavior. Using XML files also makes
 * it easy to provide different layouts for different screen sizes and orientations.
 * </i></blockquote><p>
 * <p>
 * The layouts for this demo are specified in XML. The initial (blank) layout is contained in <code>main.xml</code>
 * within <code>res/layout/</code> when the Android project is first created. Then, <code>main.xml</code> is edited to
 * create the desired visual arrangement of components. Attaching the layout to the application's activity occurs in
 * <code>onCreate</code> with the following statement:
 *
 * <blockquote> <code>setContentView(R.layout.main);</code> </blockquote>
 * <p>
 * Within <code>main.xml</code>, components are included with their spatial arrangements set using a layout class
 * combined with layout parameters. The Android framework includes several layout classes, each of which is a subclass
 * of <code>ViewGroup</code>, which in turn is a subclass of <code>View</code>. The layout classes of interest here are
 * <code>LinearLayout</code> and <code>RelativeLayout</code>. The parameters establish the size and position of
 * components (called <i>child</i> elements) as well spacing around components ("margins"), spacing within components
 * ("padding"), background color, text size, etc. The parameters, or XML attributes, are specified in
 * <code>LinearLayout.LayoutParams</code> and <code>RelativeLayout.LayoutParams</code>. <p>
 * <p>
 * It is a good idea to provide a separate layout for each screen orientation, portrait and landscape. The
 * default layout for an Android activity is portrait and is provided in <code>res/layout/main.xml</code>. To have a
 * different layout when the screen orientation changes to landscape, create a new directory,
 * <code>res/layout-land</code>, and copy <code>main.xml</code> into this directory. Then, edit the new
 * <code>main.xml</code> to create the desired layout. <p>
 * <p>
 * There are two ways to edit or view the XML code. The default mode is to work directly with the XML code. This is seen
 * in the left image below (click to enlarge). Alternatively, you can edit or view the graphical layout. This is seen in
 * the right image below. Working with the graphical layout is obviously a higher-level process. The interface provides
 * a UI preview (centre), a palette of UI components (left), and panes for the component tree and component properties
 * (right). Edits made via the graphical layout are directly translated to the appropriate XML code. It is a matter of
 * choice whether you work directly with the XML code or with the graphical layout. Either way, some trial and error
 * will be required as you experiment with different ideas and techniques in creating an appealing UI. <p>
 *
 * <center><a href="DemoLayout-3.jpg"><img src="DemoLayout-3.jpg" width="500"></a> <a href="DemoLayout-4.jpg"><img
 * src="DemoLayout-4.jpg" width="500"></a> </center> <p>
 * <p>
 * Not only does this demo use a different layout for each screen orientation, we also use a different layout class to
 * create each layout. In portrait mode, the layout is created using <code>LinearLayout</code>. In landscape mode, the
 * layout is created using <code>RelativeLayout</code>. Although either layout class can be used in most situations,
 * there are advantages and disadvantages of each. <code>LinearLayout</code> is more intuitive, since the spatial
 * hierarchy of components is clearly evident in the nesting of layouts. However, the nesting of layouts comes at a
 * cost: performance. It takes longer to do the layout, because of the nesting. <code>RelativeLayout</code>, although
 * less intuitive, uses a flat structure and is generally better in terms of performance. <p>
 * <p>
 * The demo UI includes five buttons and a text field showing the progress of button clicks. The text field is an
 * instance of <code>EditText</code>. The buttons are arranged vertically when the screen is in portrait orientation and
 * horizontally when the screen is in landscape orientation. To see how this is done, examine
 * <code>res/layout/main.xml</code> (portrait layout) and <code>res/layout-land/main.xml</code> (landscape layout). The
 * transition from one layout to the other is handled automatically through the behind-the-scene lifecycle changes of
 * the <code>Activity</code> as the screen orientation changes. <p>
 * <p>
 * <!-----------------------------------------------------------------------------------------> <b>Activity
 * Lifecycle</b> <p>
 * <p>
 * An Android activity has a lifecycle. The lifecycle begins when the activity is created and ends when the activity is
 * destroyed. The following figure illustrates: <p>
 *
 * <center><a href="DemoLayout-5.jpg"><img src="DemoLayout-5.jpg" width="500"></a> </center> <p>
 * <p>
 * The figure above is from an early Android training document.  Note
 * the series of method calls and activity states as an activity is created, then destroyed. The normal "running" state
 * of an Android activity is called <i>Resumed</i> (see figure). <p>
 * <p>
 * This demo includes a simple implementation of each of the lifecycle methods. The implementation includes a
 * <code>Log.i</code> method call to output a message naming the method. With this, the following appears in the LogCat
 * window when the activity is launched: <p>
 *
 * <pre>
 *      MYDEBUG    onCreate! (saveInstanceState bundle is null)
 *      MYDEBUG    onStart!
 *      MYDEBUG    onResume!
 * </pre>
 *
 * <p>
 *
 * <i>Android Studio Tip</i>: If the logcat window is cluttered with annoying messages, set the Log Level to "Info" to
 * limit the output to <code>Log.i</code> messages.  You can also limit the output by filtering on "MYDEBUG" (see
 * below). <p>
 *
 * <center><a href="DemoLayout-7.jpg"><img src="DemoLayout-7.jpg" width=800></a></center> <p>
 * <p>
 * If the Back button in the Navigation Bar is pressed while the activity is running, the activity terminates. It is
 * <i>Destroyed</i> in Android terms. The following <code>Log.i</code> messages will appear in the LogCat window: <p>
 *
 * <pre>
 *      MYDEBUG    onPause!
 *      MYDEBUG    onStop!
 *      MYDEBUG    onDestroy!
 * </pre>
 *
 * <p>
 * <p>
 * This is all part of the usual lifecyle of an Android activity. <p>
 * <p>
 * Actually, the situation is a bit more complicated. When an Android device is rotated, the screen orientation changes
 * (e.g., from portrait to landscape). When this happens, the activity is destroyed and then re-created. <p>
 * <p>
 * As well as providing separate layouts for portrait and landscape orientations when the screen orientation changes, it
 * is also important to save and restore data. This is discussed next. <p>
 * <p>
 * <!-----------------------------------------------------------------------------------------> <b>State Variables</b>
 * <p>
 * <p>
 * Saving and restoring state variables as the device orientation changes is managed through two methods:
 * <code>onSaveInstanceState</code>, and <code>onRestoreInstanceState</code>. These are protected methods in the
 * <code>Activity</code> class. They should be implemented (i.e., overridden) in any activity that wishes to save and
 * restore state information when the device undergoes a configuration change, such as a change in screen orientation.
 * Variables are saved by putting them in a <code>Bundle</code> and restored by retrieving them from the
 * <code>Bundle</code>: <p>
 *
 * <pre>
 *      &#64;override
 *      public void onSaveInstanceState(Bundle savedInstanceState)
 *      {
 *           super.onSaveInstanceState(savedInstanceState);
 *           Log.i(MYDEBUG, "onSaveInstanceState!");
 *           savedInstanceState.putString(BUTTON_CLICK_COUNT_KEY, clicks);
 *      }
 *
 *      &#64;override
 *      public void onRestoreInstanceState(Bundle savedInstanceState)
 *      {
 *           super.onRestoreInstanceState(savedInstanceState);
 *           Log.i(MYDEBUG, "onRestoreInstanceState!");
 *           clicks = savedInstanceState.getString(BUTTON_CLICK_COUNT_KEY);
 *      }
 * </pre>
 * <p>
 * The <code>onSaveInstanceState</code> method is called between <code>onPause</code> and <code>onStop</code> as the
 * activity is being destroyed. The <code>onRestoreInstanceState</code> method is called between <code>onStart</code>
 * and <code>onResume</code> as the activity resumes. For example, while Demo_Layout is running (i.e., in the Resumed
 * state), if the device is rotated, the following <code>Log.i</code> messages appear in the LogCat window: <p>
 *
 * <pre>
 *      MYDEBUG    onPause!
 *      MYDEBUG    onSaveInstanceState!
 *      MYDEBUG    onStop!
 *      MYDEBUG    onDestroy!
 *      MYDEBUG    onCreate!
 *      MYDEBUG    onStart!
 *      MYDEBUG    onRestoreInstanceState!
 *      MYDEBUG    onResume!
 * </pre>
 *
 * <p>
 *
 * <b>Programming note:</b> In order to have the orientation layout change when the screen orientation changes, make
 * sure the manifest file does <i>not</i> contain <code>android:configChanges="orientation"</code> in the
 * <code>Activity</code>'s attributes. <p>
 *
 * @author (c) Scott MacKenzie, 2012-2022
 */
public class DemoLayoutActivity extends Activity implements OnClickListener {
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    private final static String BUTTON_CLICK_COUNT_KEY = "button_click_count"; // "key" for bundled click count

    private Button buttonOne, buttonTwo, buttonThree, buttonClear, buttonExit;
    private EditText clickUpdate;
    private String clicks;

    // Called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String s = " (savedInstanceState bundle is ";
        if (savedInstanceState == null)
            s += "null)";
        else
            s += "NOT null)";

        Log.i(MYDEBUG, "onCreate!" + s);
        setContentView(R.layout.main);
        init();

        // suppress soft keyboard
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void init() {
        buttonOne = findViewById(R.id.button1);
        buttonTwo = findViewById(R.id.button2);
        buttonThree = findViewById(R.id.button3);
        buttonClear = findViewById(R.id.buttonClear);
        buttonExit = findViewById(R.id.buttonExit);
        clickUpdate = findViewById(R.id.editText1);
        clickUpdate.setCursorVisible(false); // suppress the | beam

        buttonOne.setOnClickListener(this);
        buttonTwo.setOnClickListener(this);
        buttonThree.setOnClickListener(this);
        buttonClear.setOnClickListener(this);
        buttonExit.setOnClickListener(this);

        clicks = "";
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(MYDEBUG, "onStart!");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(MYDEBUG, "onResume!");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(MYDEBUG, "onPause!");
    }

    @Override
    public void onRestart() {
        Log.i(MYDEBUG, "onRestart!");
        super.onRestart();
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(MYDEBUG, "onStop!");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(MYDEBUG, "onDestroy!");
    }

    @Override
    public void onClick(View v) {
        if (v == buttonOne) {
            Log.i(MYDEBUG, "Button 1 clicked!");
            clicks += "1";
        } else if (v == buttonTwo) {
            Log.i(MYDEBUG, "Button 2 clicked!");
            clicks += "2";
        } else if (v == buttonThree) {
            Log.i(MYDEBUG, "Button 3 clicked!");
            clicks += "3";
        } else if (v == buttonClear) {
            clicks = "";
        } else if (v == buttonExit) {
            finish();
        }

        clickUpdate.setText(clicks);
    }

    /*
     * The next two methods are needed to maintain the button-clicks string when the device
     * orientation changes.
     *
     * NOTE: It is not necessary to refresh the displayed contents of clickUpdate in
     * onRestoreInstanceState. Here's why: clickUpdate is an instance of EditText. The text in an
     * EditText instance is part of its internal state and is automatically saved and restored as
     * part of the default implementations of onSavedInstanceState and onRestoreInstance state.
     * See...
     *
     * http://developer.android.com/guide/components/activities.html#SavingActivityState
     *
     * NOTE: The point above does NOT apply to instances of TextView.
     *
     * NOTE: For onSaveInstanceState, we are following the normal Java convention of calling the
     * super method first. This seems to work fine, even though the Android training reference shows
     * state variables being saved BEFORE calling the super method. See...
     *
     * http://developer.android.com/training/basics/activity-lifecycle/recreating.html
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(MYDEBUG, "onSaveInstanceState!");
        savedInstanceState.putString(BUTTON_CLICK_COUNT_KEY, clicks);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i(MYDEBUG, "onRestoreInstanceState!");
        clicks = savedInstanceState.getString(BUTTON_CLICK_COUNT_KEY);
    }
}