package ca.yorku.eecs.mack.demoandroid;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import java.util.Locale;

/**
 * Demo_Android - demonstrate the basic structure of an Android application. <p>
 * <p>
 * This is the first in a series of demo programs for the course Mobile User Interfaces (EECS 4443), offered in
 * the Department of Electrical Engineering and Computer Science at York University, Toronto. It is assumed that
 * students have experience in Java programming, and have successfully taken the prerequisite course, User Interfaces
 * (EECS 3461), which teaches GUI programming in a desktop environment.<p>
 * <p>
 * In the lectures supporting this demo program, the basic structure and lifecycle of an Android application are
 * discussed. The lectures also present and discuss the operation of Android Studio, which is the integrated
 * development environment (IDE) used for developing and debugging Android applications.<p>
 * <p>
 * The core components of an Android application include the <code>Activity</code> class, the manifest file
 * (<code>AndroidManifest.xml</code>), and the GUI layout as specified in an XML layout file <code>(main.xml</code> in
 * <code>res/layout</code>). To learn about these and other topics, there are a variety of references and tutorials
 * available. Most are contained in the Android API Guides, API References, Training Guides, and even blogs. A good
 * place to start is with the first reference below, Application Fundamentals. <p>
 * <p>
 * Related information: <p>
 *
 * <blockquote> API Guides:<p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/components/fundamentals.html">Application Fundamentals</a>
 * <li><a href="http://developer.android.com/guide/components/activities.html">Activities</a> <li><a
 * href="http://developer.android.com/guide/topics/ui/controls/button.html">Buttons</a> <li><a
 * href="http://developer.android.com/guide/topics/manifest/uses-sdk-element.html">&lt;uses-sdk &gt;</a> </ul><p>
 * <p>
 * API References:<p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/app/Activity.html"> <code>Activity</code></a> <li><a
 * href="http://developer.android.com/reference/android/widget/Button.html"> <code>Button</code></a> <li><a
 * href="http://developer.android.com/reference/android/view/View.html"><code>View</code></a> <li><a
 * href="http://developer.android.com/reference/android/util/Log.html"><code>Log</code></a> </ul> <p>
 * <p>
 * Tools:<p>
 *
 * <ul> <li><a href="http://developer.android.com/tools/studio/index.html">Android Studio Overview</a> </ul><p>
 *
 * </blockquote>
 * <p>
 * This demo application is deliberately simple. Here is a screen snap: (click to enlarge)<p>
 *
 * <center><a href="DemoAndroid-1.jpg"><img src="DemoAndroid-1.jpg" width=200></a></center> <p>
 * <p>
 * The application includes a text field for output and three buttons for input. The buttons are Increment, Decrement,
 * and Exit. Button events are handled by including <code>implements OnClickListener</code> in the Activity's signature
 * and then providing an implementation of <code>onClick(View v)</code>, as defined in <code>OnClickListener</code>.
 * This is the usual method for handling events in Java/Swing. In Android parlance, the <code>onClick</code> method is
 * referred to as a <i>callback</i>, because the method is called from the code in the <code>Button</code> class, rather
 * than in the Activity per se. <p>
 * <p>
 * When a button is clicked (i.e., tapped by the user's finger), the <code>onClick</code> callback method executes. In
 * the method, a click count is incremented or decremented and then sent to the text field for display. The Exit button
 * terminates the application. <p>
 * <p>
 * The application includes a bug. We'll troubleshoot the bug by examining the source code and by observing the
 * program's behaviour using the debug monitor, also known as LogCat. A powerful technique for debugging is to print to
 * Android Studio's debug monitor (LogCat) while a program is running on a device attached to the host computer via a
 * USB cable.
 * Static methods of the <code>Log</code> class are used for this
 * purpose. For example, <p>
 *
 * <pre>
 *      private final static String MYDEBUG = "MYDEBUG";
 *      ...
 *      Log.i(MYDEBUG, "Initialization done. Application running.");
 * </pre>
 *
 * <p>
 * <p>
 * prints an information message to LogCat. With this, the following appears in the LogCat window when
 * <code>DemoAndroid</code> is launched: (click to enlarge)<p>
 *
 * <center><a href="DemoAndroid-2.jpg"><img src="DemoAndroid-2.jpg" width=800 > </a></center> <p>
 * <p>
 * To avoid clutter, other messages can be hidden by entering  the string "MYDEBUG" in the search field (red arrow in
 * image above). The string (<code>"MYDEBUG"</code>) is a tag to identify the source of the message. The second
 * string is the message text. A statement like this is useful to determine if execution reaches a particular
 * location in the code.
 * In fact the <code>Log.i</code> method above appears in the <code>onCreate</code> method of the Demo_Android
 * application.
 * Consult the source code for details. The <code>Log.i</code> method is also useful to print the value of variables.
 * Consult the API Reference for the
 * <code>Log</code> class for further details (link above). <p>
 * <p>
 * If the orientation of the device
 * changes (e.g., from portrait to landscape), the message will appear again.  In fact, the message is written to the
 * LogCat window each time the device orientation changes.  This is a simple demonstration of the <i>lifecycle</i> of an
 * Android application.  Android application lifecycles are examined in further detail in the next demo program,
 * Demo_Layout. <p>
 * <p>
 * The application includes a few deficiencies (even after the bug is fixed). One deficiency is the loss of the button
 * count when the screen is rotated. We'll learn how to remedy this in the next demo program. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2022
 */
public class DemoAndroidActivity extends Activity implements OnClickListener {
    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    private Button incrementButton, decrementButton, exitButton;
    private TextView textview;
    private int clickCount;

    // called when the activity is first created
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
        Log.i(MYDEBUG, "Initialization done. Application running.");
    }

    private void initialize() {
        // get references to buttons and text view from the layout manager (rather than instantiate them)
        incrementButton = (Button) findViewById(R.id.incbutton);
        decrementButton = (Button) findViewById(R.id.decbutton);
        exitButton = (Button) findViewById(R.id.exitbutton);
        textview = (TextView) findViewById(R.id.textview);

        // some code is missing here

        // initialize the click count
        clickCount = 0;

        // initialize the text field with the click count
        textview.setText(String.format(Locale.CANADA, "Count: %d", clickCount));
    }

    // this code executes when a button is clicked (i.e., tapped with user's finger)
    @Override
    public void onClick(View v) {
        if (v == incrementButton) {
            Log.i(MYDEBUG, "Increment button clicked!");
            ++clickCount;

        } else if (v == decrementButton) {
            Log.i(MYDEBUG, "Decrement button clicked!");
            --clickCount;

        } else if (v == exitButton) {
            Log.i(MYDEBUG, "Good bye!");
            this.finish();

        } else
            Log.i(MYDEBUG, "Oops: Invalid Click Event!");

        // update click count
        textview.setText(String.format(Locale.CANADA, "Count: %d", clickCount));
    }
}