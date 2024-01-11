package ca.yorku.eecs.mack.demograffiti;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Demo_Graffiti - demo of handwriting recognition using finger gestures on the surface of the tablet. Single-touch
 * only. <P>
 *
 * Here's is a screen snap upon launch (left) and after entering a few strokes (right): (Note: "Erase ink" is unchecked
 * in the right-side image.) <p>
 *
 * <center> <a href="./javadoc_images/DemoGraffiti-1.jpg"><img src="./javadoc_images/DemoGraffiti-1.jpg" width="300" alt="image"></a> <a
 * href="./javadoc_images/DemoGraffiti-2.jpg"><img src="./javadoc_images/DemoGraffiti-2.jpg" width="300" alt="image"></a> </center> <p>
 *
 * The core of this demo is a custom class called <code>GraffitiPanel</code>, which is sub-classed from
 * <code>View</code> (see pink panels in screen snaps above). A <code>GraffitiPanel</code> receives finger gestures and
 * displays them as digital ink on the panel.  Each gesture is passed on as a <code>Point</code> array to a
 * <code>Unistroke</code> object for recognition. The recognized result is written to a text field above the
 * <code>GraffitiPanel</code>. <p>
 *
 * The communication between the main activity and the <code>GraffitiPanel</code> occurs through
 * <code>GraffitiPanel.OnStrokeListener</code>. The main activity includes <code>implements
 * GraffitiPanel.OnStrokeListener</code> in its signature. As well, it attaches the listener to the
 * <code>GraffitiPanel</code> on initialization <p>
 *
 * <pre>
 *      graffitiPanel.setOnStrokeListener(this);
 * </pre>
 *
 * and includes an implementation of the <code>onStroke</code> method, as defined in the listener. The
 * <code>onStroke</code> methods begins <p>
 *
 * <pre>
 *      public void onStroke(GraffitiEvent ge)
 *      {
 *         int charCode = ge.charCode;
 *         int type = ge.type;
 *         ...
 * </pre>
 *
 * The <code>GraffitiEvent</code> object is prepared within the <code>GraffitiPanel</code> upon gesture recognition and
 * then passed to the main activity in <code>onStroke</code>. The <code>GraffitiEvent</code> object includes information
 * about the gesture, such as a character code, a type code, and so on. Full details are in the
 * <code>GraffitiEvent</code> API and source code. <p>
 *
 * The heaving lifting is done in the <code>Unistroke</code> class. Once a full gesture in inputted (on
 * <code>ACTION_UP</code> in the <code>GraffitiPanel</code>'s <code>onTouch</code> method), the gesture in the form of a
 * <code>Point</code> array is passed to the <code>recognize</code> method of the <code>Unistroke</code> class. There,
 * the gesture is recognized with the result returned as a string. Full details on gesture recognition are in the
 * <code>Unistroke</code> API and source code. <p>
 *
 * Back in <code>GraffitiPanel</code>, some additional processing occurs based on the type of gesture. Gestures
 * representing commands, such as shift, numeric shift, or backspace require special treatment. Usually, though, the
 * result of recognition is a character (e.g., "a") and in this case the character is simply added to the text appearing
 * in this demo's text view (see screen snap on right, above). <p>
 *
 * The demo includes a few features to expand the text input capability. Note in the screen snaps above that there are
 * watermarks (text in light gray) at the top of the <code>GraffitiPanel</code>. These are for modes. There are three
 * modes: SYM, SHIFT, and NUM. Each mode is invoked with a straight-line stroke in the direction of the watermark:
 * North-West for SYM, North for SHIFT, and North-East for NUM: <p>
 *
 * <center> <a href="./javadoc_images/DemoGraffiti-6.jpg"><img src="./javadoc_images/DemoGraffiti-6.jpg" width="600" alt="image"></a> </center> <p>
 *
 * The four screen snaps below show the sequence of gestures for entering uppercase A. (i) The user first makes a North
 * stroke. (ii) This puts the application into SHIFT mode, as indicated by SHIFT in red. (iii) The user then makes the
 * stroke for "a". (iv) On finger lift, uppercase A appears with the SHIFT mode cleared.
 *
 * <center> <a href="./javadoc_images/DemoGraffiti-3.jpg"><img src="./javadoc_images/DemoGraffiti-3.jpg" width="800" alt="image"></a> </center> <p>
 *
 * Note that the sequence above was made with "Erase ink" unchecked, so the ink would persist on the display. Under
 * normal operation, the ink is erased on each finger lift. Thus, in practice, strokes are typically made on top of one
 * another. <p>
 *
 * Two consecutive North strokes puts the application into SHIFT_LOCK mode, allowing the input of multiple uppercase
 * letters without requiring a separate North stroke before each letter. After some uppercase input, a single North
 * stroke resets SHIFT mode. <p>
 *
 * One capability of the <code>Unistroke</code> class is to swap dictionaries (see API). This feature is used in the
 * demo to swap between the Graffiti dictionary for letters and a digits dictionary. A North-East stroke invokes NUM
 * mode, swapping dictionaries. The strokes for digits are simple and intuitive: <p>
 *
 * <center> <a href="./javadoc_images/DemoGraffiti-4.jpg"><img src="./javadoc_images/DemoGraffiti-4.jpg" width="300" alt="image"></a> </center> <p>
 *
 * NUM and NUM_LOCK modes are available in a manner similar to SHIFT and SHIFT_LOCK modes. <p>
 *
 * A North-West stroke invokes SYM mode: <p>
 *
 * <center> <a href="./javadoc_images/DemoGraffiti-5.jpg"><img src="./javadoc_images/DemoGraffiti-5.jpg" width="600" alt="image"></a> </center> <p>
 *
 * Special symbols, such as punctuation, are entered in SYM mode by tapping on the symbol. SYM and SYM_LOCK modes are
 * available in a manner similar to SHIFT and SHIFT_LOCK.<p>
 *
 * Perhaps you are wondering why the symbols are rendered in light gray in SYM mode. The reason is that regular Graffiti
 * strokes can be made in SYM mode. So, in SYM mode both letters (Graffiti strokes) and symbols (taps) can be entered.
 * Rendering the symbols in a light gray is less intrusive if the user wishes to mix the input of symbols and letters.
 * Give it a try and see what you think. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2018
 */
public class DemoGraffitiActivity extends Activity implements View.OnClickListener, GraffitiPanel.OnStrokeListener
{
    private static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final int PULSE_DURATION = 15;

    GraffitiPanel graffitiPanel;
    ImageView gestureSetImage;
    EditText recognizedText;
    CheckBox eraseOnFingerLiftCheckBox;
    boolean eraseOnFingerLift;
    Vibrator vib;
    Button clearButton, exitButton;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        graffitiPanel = (GraffitiPanel)findViewById(R.id.paintPanel);
        graffitiPanel.setOnStrokeListener(this);

        recognizedText = (EditText)findViewById(R.id.recognizedtext);
        recognizedText.setBackgroundColor(Color.WHITE);
        recognizedText.setTextColor(Color.BLUE);
        recognizedText.setLines(2); // use two lines so we can test for RETURN

        // Hmm... we need this as well to get the text to wrap to the second line, see...
        // http://stackoverflow.com/questions/6158123/java-android-appending-a-newline-using-textview
        recognizedText.setInputType(recognizedText.getInputType() | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        eraseOnFingerLiftCheckBox = (CheckBox)findViewById(R.id.eraseonfingerlift);
        eraseOnFingerLiftCheckBox.setOnClickListener(this);
        eraseOnFingerLiftCheckBox.setChecked(true);
        eraseOnFingerLift = true;

        gestureSetImage = (ImageView)findViewById(R.id.imgGestureSet);
        gestureSetImage.setAdjustViewBounds(true);

        clearButton = (Button)findViewById(R.id.clearbutton);
        clearButton.setOnClickListener(this);
        exitButton = (Button)findViewById(R.id.exitbutton);
        exitButton.setOnClickListener(this);
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // prevent soft keyboard from popping up by default (stills pop up if text field is tapped)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /**
     * Called when the activity is terminated.
     */
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onClick(View v)
    {
        if (v == clearButton)
        {
            recognizedText.setText("");
            graffitiPanel.clear();

        } else if (v == exitButton)
        {
            super.onDestroy(); // cleanup
            this.finish(); // terminate

        } else if (v == eraseOnFingerLiftCheckBox)
        {
            eraseOnFingerLift = eraseOnFingerLiftCheckBox.isChecked();
            graffitiPanel.setEraseOnFingerLift(eraseOnFingerLift);
        }
    }

    @Override
    public void onStroke(GraffitiEvent ge)
    {
        int charCode = ge.charCode;
        int type = ge.type;

        switch (type)
        {
            case GraffitiEvent.TYPE_BACKSPACE:
                CharSequence cs = recognizedText.getText();
                if (cs.length() > 0)
                    recognizedText.setText(cs.subSequence(0, cs.length() - 1));
                recognizedText.setSelection(recognizedText.getText().length()); // move cursor to end
                break;

            case GraffitiEvent.TYPE_ENTER:
                recognizedText.append("\n");
                break;

            case GraffitiEvent.TYPE_ALPHA:
            case GraffitiEvent.TYPE_NUMERIC:
            case GraffitiEvent.TYPE_SYMBOL:
                recognizedText.append("" + (char)charCode);
                break;

            case GraffitiEvent.TYPE_SPACE:
                recognizedText.append(" ");
                break;

            case GraffitiEvent.TYPE_TAP:
                recognizedText.append(".");
                break;

            case GraffitiEvent.TYPE_NONE:
            case GraffitiEvent.TYPE_UNRECOGNIZED:
            case GraffitiEvent.TYPE_SOUTH_EAST:
                vib.vibrate(PULSE_DURATION);
                break;
        }
    }
}