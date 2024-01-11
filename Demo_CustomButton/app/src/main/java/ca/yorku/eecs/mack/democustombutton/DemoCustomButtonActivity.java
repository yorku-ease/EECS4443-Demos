package ca.yorku.eecs.mack.democustombutton;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Locale;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_CustomButton - demonstrate creating a custom button as a subclass of <code>View</code>. Allows for multitouch.
 * <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/ui/custom-components.html">Custom Components</a> </ul>
 * <p>
 *
 * API References: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/view/View.html"><code>View</code></a> <li> <a
 * href="http://developer.android.com/reference/android/widget/RelativeLayout.html"> <code>RelativeLayout</code></a>
 * <li> <a href="http://developer.android.com/reference/android/widget/RelativeLayout.LayoutParams.html">
 * <code>RelativeLayout.LayoutParams</code></a> </ul> </blockquote>
 *
 * In general, there are two approaches to creating a custom button. One is to create a subclass of <a
 * href="http://developer.android.com/reference/android/widget/Button.html"><code>Button</code> </a> and override
 * various methods to customize the button accordingly. Another approach is to create a new button from scratch as a
 * subclass of <a href="http://developer.android.com/reference/android/view/View.html"><code>View</code></a>. The
 * advantage of the first approach is that the default behaviour of <code>Button</code> objects is retained, except
 * where customization is used. The second approach is a bit more involved, since none of the default behaviour of
 * <code>Button</code> objects is available, except the behaviours inherited from the <code>View</code> class. The
 * second approach is demonstrated in this demo program. <p>
 *
 * One limitation of <code>Button</code> objects is that they cannot receive focus in touch mode. They operate only via
 * <code>onClick</code>, which fires when the finger is lifted from a button. To have greater control over a button,
 * such as controlling its shape or sensing when the finger first touches or slides off a button, some customization is
 * necessary. This
 * customization is best performed by defining a new class that extends <code>View</code>, rather than
 * <code>Button</code>. <P>
 *
 * Our custom button is an instance of <code>MyButton</code>, sub-classed from <code>View</code>. Objects of the
 * <code>MyButton</code> class are hexagonal in shape. The following screen snap shows the demo upon launching: <p>
 *
 * <center><a href="./javadoc_images/DemoCustomButton-1.jpg"><img src="./javadoc_images/DemoCustomButton-1.jpg" width=" 200"></a></center> <p>
 *
 * Now, if you're wondering why on earth anyone would want to use hexagonal buttons, have a look at the Metropolis
 * keyboard designed by Hunter, Zhai, and Smith (<a href="http://www.yorku.ca/mack/hci3.html">Click here</a>, see Figure
 * 26). <p>
 *
 * Although the space allocated for a <code>View</code> object is rectangular, a non-rectangular appearance is
 * possible.  This is achieved by overwriting the <code>onDraw</code> method and drawing the desired shape.  The
 * hexagonal shape for our
 * custom button is achieved by defining a <code>Path</code> with points forming a hexagon:<p>
 *
 * <center><a href="./javadoc_images/DemoCustomButton-5.jpg"><img src="./javadoc_images/DemoCustomButton-5.jpg" width="500"></a></center> <p>
 *
 * Clearly, the layout of hexagonal buttons poses special challenges. Layout is performed by positioning the buttons
 * within an instance of <code>RelativeLayout</code> called <code>buttonPanel</code>. The actual layout and positioning
 * are done programmatically. The buttons are added to <code>buttonPanel</code> using the <code>addView</code> method,
 * passing two arguments: (i) a reference to the view to add (i.e., the custom button), and (ii) an instance of
 * <code>RelativeLayout.LayoutParams</code> which specifies the location within <code>buttonPanel</code> to position the
 * button. The position is specified via the <code>leftMargin</code> and <code>topMargin</code> fields. <p>
 *
 * Since we are using code to layout and position the buttons, the work is done in the Activity's
 * <code>onWindowFocusChanged</code> method. This is necessary since the size of the <code>buttonPanel</code> is not
 * known earlier. To illustrate this, the demo includes a call to <code>Log.i</code> in each of <code>onCreate</code>,
 * <code>onStart</code>, <code>onResume</code>, and <code>onWindowFocusChanged</code>. The call identifies the method
 * and also outputs the current width and height of <code>buttonPanel</code>. The following appears in the LogCat window
 * when debugging on an LG <i>Nexus 5x</i> in portrait orientation: <p>
 *
 * <center> <img src="./javadoc_images/DemoCustomButton-4.jpg"> </center> <p>
 *
 * Since the buttons are subclasses of <code>View</code>, they inherently lie within rectangular bounds. Determining
 * when a button is tapped requires special care, since the rectangular bounds of the buttons overlap. The details for
 * this are found in the <code>contains</code> method of the <code>MyButton</code> class. <p>
 *
 * Of course, determining whether a touch point lies within a button begins by setting up the application to respond to
 * touch events. Rather than attach an <code>OnTouchListener</code> to each button, the <code>OnTouchListener</code> is
 * attached to <code>buttonPanel</code> &ndash; the instance of <code>RelativeLayout</code>. Within the
 * <code>onTouch</code> callback, the <i>x/y</i> coordinates of the touch event are passed to a method called
 * <code>findButton</code> to find the button, if any, within which the touch event occurred. Consult the source code
 * for further details. <p>
 *
 *
 * This demo also supports multitouch. If the finger touches a button, another finger may touch another bottom at the
 * same time. Buttons are "clicked" when each finger is raised. Either order of lifting is fine. An example is seen in
 * the following series of screen snaps: <p>
 *
 * <center><a href="./javadoc_images/DemoCustomButton-2.jpg"><img src="./javadoc_images/DemoCustomButton-2.jpg" width=" 700"></a></center> <p>
 *
 * One advantage of the scenario above is that fast taps may overlap without a loss of information. As
 * well, behaviours such as SHIFT-a to generate uppercase A are possible. Other more involved possibilities emerge, such
 * as using multiple fingers simultaneously on a series of "buttons" resembling piano keys: <p>
 *
 * <center><a href="./javadoc_images/DemoCustomButton-3.jpg"><img src="./javadoc_images/DemoCustomButton-3.jpg" width=" 400"></a></center> <p>
 *
 * We'll leave it to you to explore this possibility. <p>
 *
 * @author (c) Scott MacKenzie 2011-2018
 */

public class DemoCustomButtonActivity extends Activity implements View.OnTouchListener
{
    final String MYDEBUG = "MYDEBUG";

    final int MAX_TOUCH_POINTS = 10;
    final int INVALID = -1;

    RelativeLayout buttonPanel;
    MyButton buttonA, buttonB, buttonExit;
    TextView clickCountA, clickCountB, debug;

    int[] touchPointId = new int[MAX_TOUCH_POINTS];
    MyButton[] buttonID = new MyButton[MAX_TOUCH_POINTS];
    int buttonWidth, buttonHeight;
    int id, index;
    int countA, countB;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        buttonPanel = findViewById(R.id.buttonpanel);
        buttonPanel.setOnTouchListener(this);
        // defer further customization of the buttonPanel to onWindowFocusChanged

        Log.i(MYDEBUG, "onCreate! (w=" + buttonPanel.getWidth() + ", h=" + buttonPanel.getHeight() + ")");

        clickCountA = findViewById(R.id.clickcount1);
        clickCountB = findViewById(R.id.clickcount2);
        debug = findViewById(R.id.debug);

        // invalidate all touch point ids
        for (int i = 0; i < touchPointId.length; ++i)
            touchPointId[i] = INVALID;

        countA = 0;
        countB = 0;

        clickCountA.setText(String.format(Locale.CANADA, "%s%d", getResources().getString(R.string.clickCountAString)
                , countA));
        clickCountB.setText(String.format(Locale.CANADA, "%s%d", getResources().getString(R.string.clickCountBString)
                , countB));
        debug.setText(getResources().getString(R.string.debugString));
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!hasFocus)
            return;

        // now the button panel's width and height are available
        Log.i(MYDEBUG, "onWindowFocusChanged! (w=" + buttonPanel.getWidth() + ", h=" + buttonPanel.getHeight() + ")");

        // button view rectangles will be square and 1/3 the available width or height, whichever is less
        int availableWidth = buttonPanel.getWidth();
        int availableHeight = buttonPanel.getHeight();
        buttonWidth = Math.min((int)(availableWidth / 3f + 0.5f), (int)(availableHeight / 3f + 0.5f));
        buttonHeight = buttonWidth;

        buttonA = new MyButton(this);
        buttonA.setButtonSize(buttonWidth, buttonHeight);
        buttonA.setButtonText("A");

        buttonB = new MyButton(this);
        buttonB.setButtonSize(buttonWidth, buttonHeight);
        buttonB.setButtonText("B");

        buttonExit = new MyButton(this);
        buttonExit.setButtonSize(buttonWidth, buttonHeight);
        buttonExit.setButtonText("Exit");

        /*
         * Add buttons to buttonPanel and set margins. The margins set the left and top offsets of each
         * button within the buttonPanel.
         */
        RelativeLayout.LayoutParams AParams = new RelativeLayout.LayoutParams(availableWidth, availableHeight / 2);
        AParams.leftMargin = 0;
        AParams.topMargin = 0;
        buttonPanel.addView(buttonA, AParams);

        RelativeLayout.LayoutParams BParams = new RelativeLayout.LayoutParams(availableWidth, availableHeight / 2);
        BParams.leftMargin = buttonB.width;
        BParams.topMargin = 0;
        buttonPanel.addView(buttonB, BParams);

        RelativeLayout.LayoutParams ExitParams = new RelativeLayout.LayoutParams(availableWidth, availableHeight / 2);
        ExitParams.leftMargin = ExitParams.leftMargin + Math.round(MyButton.LEFT_MARGIN_ADJ * buttonExit.width);
        ExitParams.topMargin = ExitParams.topMargin + Math.round(MyButton.TOP_MARGIN_ADJ * buttonExit.height);
        buttonPanel.addView(buttonExit, ExitParams);
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(MYDEBUG, "onStart! (w=" + buttonPanel.getWidth() + ", h=" + buttonPanel.getHeight() + ")");
    }

    @Override
    public void onResume()
    {
        super.onResume();
        Log.i(MYDEBUG, "onResume! (w=" + buttonPanel.getWidth() + ", h=" + buttonPanel.getHeight() + ")");
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        int action = me.getAction();

        // get the index and the pointer associated with this event
        index = me.getActionIndex();

        // get the id of the pointer associated with this event
        id = me.getPointerId(index);

        // find the button, if any, associated with this touch event
        final MyButton tmp = findButton(me.getX(index), me.getY(index));

        // find the button associated with this pointer, if any
        for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
        {
            if (tmp == null || touchPointId[i] == id) // found it
            {
                // Did the finger shift to a blank area or to a different button?
                if (tmp != buttonID[i]) // Yes
                {
                    // Unpress the button.
                    touchPointId[i] = INVALID;
                    buttonID[i].setIsPressed(false);
                    buttonID[i] = null;
                    return true;
                }
            }
        }

        if (tmp == null)
            return true; // no button (touch is elsewhere on panel)

        switch (action & MotionEvent.ACTION_MASK)
        {
            // touch down (first or subsequent)
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_DOWN:
                // find an empty spot in the arrays for the new touch point
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                    if (touchPointId[i] == INVALID)
                    {
                        touchPointId[i] = id;
                        buttonID[i] = tmp;
                        break;
                    }
                tmp.setIsPressed(true);
                break;

            case MotionEvent.ACTION_UP: // last touch point
            case MotionEvent.ACTION_POINTER_UP:
                // find the touch point, click, then make it invalid
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                {
                    if (touchPointId[i] == id)
                    {
                        touchPointId[i] = INVALID;
                        buttonID[i].setIsPressed(false);
                        buttonID[i].doClick();
                        if (buttonID[i] == buttonA)
                        {
                            ++countA;
                            clickCountA.setText(String.format(Locale.CANADA, "%s%d", getResources().getString(R
                                    .string.clickCountAString), countA));

                        } else if (buttonID[i] == buttonB)
                        {
                            ++countB;
                            clickCountB.setText(String.format(Locale.CANADA, "%s%d", getResources().getString(R
                                    .string.clickCountBString), countB));

                        } else if (buttonID[i] == buttonExit)
                            doExit();

                        buttonID[i] = null;
                        break;
                    }
                }
                v.performClick(); // avoids accessibility warning
                break;

            case MotionEvent.ACTION_MOVE:
                break;
        }
        return true;
    }

    /*
     * Determine if the x/y coordinate is inside a button If so, return the button. If not, return null.
     */
    public MyButton findButton(float xArg, float yArg)
    {
        final MyButton[] buttonArray = {buttonA, buttonB, buttonExit};
        for (MyButton mb : buttonArray)
        {
            if (mb.contains(xArg, yArg))
                return mb;
        }
        return null;
    }

    // Exit button clicked (we're done)
    public void doExit()
    {
        this.onDestroy();
        this.finish();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }
}
