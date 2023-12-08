package ca.yorku.eecs.mack.demoedittext;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

/**
 * Demo_EditText - demonstrate the basic mechanism to enter text into a text field.
 * <p>
 *
 * Related information:
 * <p>
 *
 * <blockquote> API Guides:
 * <p>
 *
 * <ul>
 * <li>
 * <a href="http://developer.android.com/guide/topics/ui/controls.html">Input Controls</a>
 * </ul><p>
 *
 * API References:
 * <p>
 *
 * <ul>
 * <li>
 * <a href="http://developer.android.com/reference/android/widget/EditText.html">
 * <code>EditText</code> </a>
 * </ul><p>
 *
 * Training:
 * <p>
 *
 * <ul>
 * <li><a href="http://developer.android.com/training/keyboard-input/index.html">Handling Keyboard
 * Input</a> (and subsections)
 * </ul>
 * </blockquote>
 *
 * The input text field is an instance of <code>EditText</code>. The only other UI components in
 * this demo are a text field to received the text entered (an instance of <code>TextView</code>)
 * and an button to clear the text in both views.
 * <p>
 *
 * Below (left) is a screen snap of the UI upon launching &ndash; after touching the
 * <code>EditText</code> view. The default IME is set to the Google soft keyboard. The image on the
 * right shows the UI after entering "Hello world." and clicking the Send button on the keyboard.
 * <p>
 *
 * <center> <a href="DemoEditText-1.jpg"><img src="DemoEditText-1.jpg"
 * width="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoEditText-2.jpg"><img
 * src="DemoEditText-2.jpg" width="300"></a> </center>
 * <p>
 *
 * A touch-and-hold gesture on the text in the <code>EditText</code> instance selects a word for
 * editing (below left). Sliders appear and bracket the word. The sliders may be dragged to adjust
 * the selection range. Icons appear at the top for common actions. Tapping the icon invokes the
 * action. For users unfamiliar with the icons, a long-press launches a lightweight popup (
 * <code>Toast</code>) with additional information (below right).
 * <p>
 *
 * <center> <a href="DemoEditText-8.jpg"><img src="DemoEditText-8.jpg"
 * height="450"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoEditText-10.jpg"><img
 * src="DemoEditText-10.jpg" height="450"></a> </center>
 * <p>
 *
 * Additional features and behaviours for text entry are embedded in the IME (Input Method Editor),
 * rather than in the application per se. For example, the Google keyboard supports swipe gestures.
 * "Hello" can be entered by touching "h", swiping to "e", swiping to "l", then swiping to "o". The
 * world "hello" appears. A finger-lift selects the word, passing it on to the <code>EditText</code>
 * instance. See below, left. (Note: The ink trail in the image was added. During entry, an ink
 * trail follows the finger, but fades within in a second or two.) The Google keyboard also supports
 * word completion. After tapping "w", "o", "r", and "l", the word "world" appears along the top of
 * the keyboard. Tapping the word selects it, passing it on to the <code>EditText</code> instance.
 * See below, center. Voice typing is also supported. If the user taps on the microphone icon
 * (beside the SPACE bar), and and then speaks "hello world" the text is recognized (hopefully!) and
 * then delivered to the <code>EditText</code> instance. See below, right.
 * <p>
 *
 * <center> <a href="DemoEditText-3.jpg"><img src="DemoEditText-3.jpg" width="300">
 * </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoEditText-4.jpg"><img src="DemoEditText-4.jpg"
 * width="300"></a> </a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoEditText-9.jpg"><img
 * src="DemoEditText-9.jpg" width="300"></a> </center>
 * <p>
 *
 * The IME may be changed via the <code>Settings > Language and Input</code>. On the device used
 * while debugging this demo (a Nexus 4), the following options appear:
 * <p>
 *
 * <center> <a href="DemoEditText-5.jpg"><img src="DemoEditText-5.jpg" width="300"></a> </center>
 * <p>
 *
 * The two IMEs at the bottom are for <i>Graffiti</i> and <i>H4Touch</i>. If each is selected in
 * turn as the default IME, the UI appears as follows. (<i>Graffiti</i> is shown on the left,
 * <i>H4Touch</i> on the right.)
 * <p>
 *
 * <center> <a href="DemoEditText-6.jpg"><img src="DemoEditText-6.jpg"
 * width="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="DemoEditText-7.jpg"><img
 * src="DemoEditText-7.jpg" width="300"></a> </center>
 * <p>
 *
 * These methods will be discussed and demonstrated during classroom lectures.
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */

public class DemoEditTextActivity extends Activity implements OnEditorActionListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static String MESSAGE = "message";

    EditText editText;
    TextView textView;
    Button clearButton;
    String message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // cast removed (not needed any more, avoids warning message)
        editText = findViewById(R.id.edit_text);
        textView = findViewById(R.id.text_view);
        clearButton = findViewById(R.id.clear_button);

        editText.setOnEditorActionListener(this);

		/*
         * The following line is commented out, since the same effect is achieved in the manifest.
		 * It is included here to illustrate the programmatic method to control the visibility of
		 * the soft keyboard.
		 */
        // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    }

    /*
     * This callback executes when the Clear button is clicked (declared in main.xml).
     */
    public void buttonClick(View v)
    {
        editText.setText("");
        message = "";
        textView.setText(message);
    }

    /*
     * When the input method's "Send" action occurs, this callback executes. Copy the text from the
     * TextView generating this event (must be "editText") to the text field below ("textView").
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        Log.i(MYDEBUG, "onEditorAction!");
        message = v.getText().toString();
        textView.setText(message);
        return true;
    }

    /*
     * Save the message if the screen rotates.
     */
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        Log.i(MYDEBUG, "onSaveInstanceState!");
        savedInstanceState.putString(MESSAGE, message);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
     * Restore the message after the screen rotates.
     *
     * NOTE: We are only restoring the text in the TextView instance. It is not necessary to restore
     * the text in the EditText instance. This occurs automatically as part of the default
     * implementation (called via the super method). For more discussion see...
     *
     * http://developer.android.com/guide/components/activities.html#SavingActivityState
     */
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        Log.i(MYDEBUG, "onRestoreInstanceState!");
        super.onRestoreInstanceState(savedInstanceState);
        message = savedInstanceState.getString(MESSAGE);
        textView.setText(message);
    }
}
