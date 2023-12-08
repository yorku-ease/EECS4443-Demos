package ca.yorku.eecs.mack.demotouch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Touch - demonstration of touch input on Android devices. This demo supports single touch only. </p>
 *
 * Related information: </p>
 *
 * <blockquote>
 *
 * API Guides:<p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/ui/ui-events.html">Input Events</a> </ul><p>
 *
 * API References: </p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/view/View.html"> <code>View</code></a> <li> <a
 * href= "http://developer.android.com/reference/android/view/View.OnTouchListener.html" >
 * <code>OnTouchListener</code></a> <li> <a href="http://developer.android.com/reference/android/view/MotionEvent.html">
 * <code>MotionEvent</code></a> <li> <a href="http://developer.android.com/reference/android/view/VelocityTracker.html">
 * <code>VelocityTracker</code></a> </ul><p>
 *
 * Training: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/training/gestures/movement.html">Tracking Movement</a> </ul>
 * </blockquote>
 *
 * This demo is a simple examination of touch input on an Android device. We are are <i>not</i> considering multitouch
 * or gesture detection (yet!). Single-finger touch input in an Android application is similar to mouse input in a
 * Java/Swing application. Instead of responding to <i>mouse events</i>, an Android application responds to <i>touch
 * events</i>. Similarly, instead of attaching a <i>mouse listener</i> to a component (such as a <code>JPanel</code>), a
 * <i>touch listener</i> is attached to a view. The view is an instance of <code>View</code> (or a sub-class of
 * <code>View</code>). The listener is <code>OnTouchListener</code>, which defines one method, <code>onTouch</code>.
 * </p>
 *
 * In Android parlance, the <code>onTouch</code> method is referred to as a <i>callback</i>, since it is called from the
 * object on which the event occurred. The <code>onTouch</code> callback provides two arguments: the <code>View</code>
 * object on which the event occurred and a <code>MotionEvent</code> object describing the event. The
 * <code>MotionEvent</code> object includes all the information we might expect for a mouse event in Java/Swing, such as
 * the <i>x</i> and <i>y</i> coordinates of the event, and whether the finger has touched down on the display surface,
 * moved on the display surface, or lifted up off the display surface. In the Android API reference, a finger is
 * referred to as a <i>pointer</i>. </p>
 *
 * The <code>MotionEvent</code> object includes considerably more information, however. This is partly due to the
 * requirements for multitouch, which is the focus of our next demo. </p>
 *
 * The following screen snaps show the demo application with a light touch (left) and a heavy touch (right): </p>
 *
 * <center> <a href="DemoTouch-1.jpg"><img src="DemoTouch-1.jpg" width="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="DemoTouch-2.jpg"><img src="DemoTouch-2.jpg" width="300"></a> </center> </p>
 *
 * The pink region is a <code>PaintPanel</code>, a subclass of <code>View</code>. A circle is painted on the paint panel
 * where the finger is touching. Pressure sensing is straight forward on Android devices, so this is included in the
 * demo as well. The diameter of the circle is proportional to the contact pressure. In fact, pressure is determined by
 * the finger contact surface area, not the pressure. So, pressing hard with the tip of the finger may yield less
 * "pressure" than pressing soft with a finger sitting flat on the display surface. </p>
 *
 * Note that this demo does not include an Exit button (unlike earlier demos). Generally, Android applications do not
 * need an explicit button to exit an application. The Back button in the Navigation Bar serves this purpose: </p>
 *
 * <center> <a href="DemoTouch-3.jpg"><img src="DemoTouch-3.jpg" width="600"></a> </center> </p>
 *
 * The Back button is used for <i>back navigation</i>. As used here, pressing Back terminates ("destroys") the current
 * activity. Furthermore, since Demo Touch includes just a single activity, the effect is to exit the application. </p>
 *
 * To assist in debugging, status information is output in two ways. First, data are written to the LogCat debug monitor
 * using </p>
 *
 * <pre>
 *      String s = String.format("Touch event: %11s {x=%4.1f, y=%4.1f, p=%1.3f}", eventType, t, x, y, p);
 *      Log.i(MYDEBUG, s);
 * </pre>
 *
 *
 * The formatted string includes four arguments, all derived from the <code>MotionEvent</code> object received in the
 * <code>onTouch</code> callback: </p>
 *
 * <ul> <li><code>eventType</code> &ndash; the kind of action associated with this event (e.g.,
 * <code>ACTION_DOWN</code>) <li><code>x</code> &ndash; the <i>x</i> coordinate in the view where the event occurred
 * <li><code>y</code> &ndash; the <i>y</i> coordinate in the view where the event occurred <li><code>p</code> &ndash;
 * the pressure of the touch associated with this event </ul> </p>
 *
 * An example stream of messages written to the LogCat window is shown below. </p>
 *
 * <center><img src="DemoTouch-4.jpg"></center> </p>
 *
 * The messages above are for a single finger gesture or stroke. Thus, the events consist of a single
 * <code>ACTION_DOWN</code> event, followed by a series of <code>ACTION_MOVE</code> events, followed by a single
 * <code>ACTION_UP</code> event. The gesture was in a south-east direction, from { 133.5, 167.0 } to { 453.8, 520.4 }.
 * (The Fling velocities are discussed below.)</p>
 *
 * The same data are also displayed using <code>setText</code> invoked on <code>TextView</code> UI elements which appear
 * below the paint panel (see screen snaps above). The touch status data are for <i>x</i>, <i>y</i>, and <i>p</i>
 * (pressure) are shown as integers. (Note: pressure appears as the raw value &times; 100.)</p>
 *
 * NOTE: The best way to appreciate this demo is to run it on an Android device (e.g., in USB debugging mode) and to
 * observe the status data on the display as a finger touches and moves about on the display. <p>
 *
 * <b>Touch Events</b> <p>
 *
 * Handling touch events occurs through the <code>onTouch</code> callback method defined in
 * <code>View.OnTouchListener</code>. When a touch event occurs, <code>onTouch</code> is called.  Two arguments are
 * passed in, the <code>View</code> object responsible for the touch event and a <code>MotionEvent</code> object which
 * provides information about the event. The type of event is retrieved by calling the <code>getAction</code> method on
 * the <code>MotionEvent</code> object. For single-touch, the primary actions are </p>
 *
 * <ul> <li><code>ACTION_DOWN</code> - a finger has touched the display surface <li><code>ACTION_MOVE</code> - the
 * finger has moved <li><code>ACTION_UP</code> - the finger has left the display surface </ul> <p>
 *
 * Additional <i>get</i>-methods are available to retrieve position and other information about the event. The following
 * shows the implementation of <code>onTouch</code> herein (some details are omitted). Consult the source code for
 * additional details. </p>
 *
 * <pre>
 *      &#64;Override
 *      public boolean onTouch(View v, MotionEvent me)
 *      {
 *           float x = me.getX();
 *           float y = me.getY();
 *           float p = me.getPressure();
 *
 *           switch (me.getAction() & MotionEvent.ACTION_MASK)
 *           {
 *                case MotionEvent.ACTION_DOWN:
 *                     updateStatus(ACTION_DOWN_STRING, x, y, p);
 *                     touchPanel.setCircle(x, y, p); // draw circle on panel
 *                     break;
 *
 *                case MotionEvent.ACTION_MOVE:
 *                     updateStatus(ACTION_MOVE_STRING, x, y, p);
 *                     touchPanel.setCircle(x, y, p); // draw circle on panel
 *                     break;
 *
 *                case MotionEvent.ACTION_UP:
 *                     updateStatus(ACTION_UP_STRING, x, y, p);
 *                     touchPanel.clear(); // clear circle on finger lift
 *                     break;
 *           }
 *           return true;
 *      }
 * </pre>
 *
 *
 * One distinction between the mouse listener methods of Java/Swing (e.g., <code>mousePressed</code>) and the touch
 * listener method in the Android platform (<code>onTouch</code>) is the return type. Mouse listener methods return
 * <code>void</code>. Note above that <code>onTouch</code> returns a <code>boolean</code>. Returning <code>true</code>
 * indicates that the event has been "consumed", which is to say, no further processing is needed. If <code>false</code>
 * is returned, then further processing is needed, for example, in a parent view (if any) or in the Activity hosting the
 * view. For further discussion, see the description of <code>onTouchEvent</code> in the <code>Activity</code> API.</p>
 *
 * <b>VelocityTracker</b> <p>
 *
 * It is sometimes useful to know the velocity of finger movement on the display during a touch gesture. The
 * <code>VelocityTracker</code> class is a helper class for this purpose. The second line of status output on the
 * display shows the velocity of finger movement along the <i>x</i>-axis and <i>y</i>-axis. Positive velocities are to
 * the right for the <i>x</i>-axis and down for the <i>y</i>-axis. The fling velocities are also shown. These are the
 * <i>x</i>-axis and <i>y</i>-axis velocities of the finger at the end of a touch gesture (upon <code>ACTION_UP</code>).
 * <p>
 *
 * Use of <code>VelocityTracker</code> is simple. The approach used here was borrowed from the Tracking Velocity
 * training module (link above). <p>
 *
 * If detecting gestures such as flings is desired, it is typically done at a higher level. The usual approach is to
 * extend <code>GestureDetector.SimpleOnGestureListener</code> and implement <code>onFling</code>. This is demonstrated
 * in Demo_Scale. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2018
 */

public class DemoTouchActivity extends Activity implements View.OnTouchListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    PaintPanel touchPanel;
    TextView statusText, xText, yText, pText;

    VelocityTracker vt;
    float xVelocity, yVelocity, xFling, yFling;
    TextView velocityText, vxText, vyText, flingxText, flingyText;

    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init()
    {
        // cast removed (not needed anymore, avoids warning message)
        touchPanel = findViewById(R.id.paintPanel);
        statusText = findViewById(R.id.info_text_view);
        xText = findViewById(R.id.x_text_view);
        yText = findViewById(R.id.y_text_view);
        pText = findViewById(R.id.p_text_view);

        velocityText = findViewById(R.id.velocity_text_view);
        vxText = findViewById(R.id.vx_text_view);
        vyText = findViewById(R.id.vy_text_view);
        flingxText = findViewById(R.id.flingx_text_view);
        flingyText = findViewById(R.id.flingy_text_view);

        // attach touch listener to panel
        touchPanel.setOnTouchListener(this);

        statusText.setText(R.string.touchStatus);
        xText.setText(R.string.xEquals);
        yText.setText(R.string.yEquals);
        pText.setText(R.string.pEquals);

        velocityText.setText(R.string.velocities);
        vxText.setText(R.string.xEquals);
        vyText.setText(R.string.yEquals);
        flingxText.setText(R.string.flingXEquals);
        flingyText.setText(R.string.flingYEquals);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        float x = me.getX();
        float y = me.getY();
        float p = me.getPressure();

        switch (me.getAction() & MotionEvent.ACTION_MASK)
        {
            case MotionEvent.ACTION_DOWN:

                if (vt == null)
                    vt = VelocityTracker.obtain();
                else
                    vt.clear();
                vt.addMovement(me);
                xFling = 0f;
                yFling = 0f;

                updateStatus(getResources().getString(R.string.actionDown), x, y, p);
                touchPanel.setCircle(x, y, p); // draw circle on panel
                break;

            case MotionEvent.ACTION_MOVE:

                vt.addMovement(me);
                vt.computeCurrentVelocity(1000); // velocity in pixels per second
                xVelocity = vt.getXVelocity();
                yVelocity = vt.getYVelocity();

                updateStatus(getResources().getString(R.string.actionMove), x, y, p);
                touchPanel.setCircle(x, y, p); // draw circle on panel
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:

                xFling = xVelocity;
                yFling = yVelocity;
                xVelocity = 0f;
                yVelocity = 0f;
                vt.recycle();

                // The following is not used in the Android training code for VelocityTracker.
                // However, it seems to be necessary to avoid a crash when recycling. See...
                // http://stackoverflow.com/questions/26074907/velocitytracker-causes-crash-on-android-4-4
                vt = null;

                updateStatus(getResources().getString(R.string.actionMove), x, y, p);
                Log.i(MYDEBUG, "Fling velocities: Vx=" + xFling + ", Vy=" + yFling);

                touchPanel.clear(); // clear circle on finger lift
                break;
        }
        return true;
    }

    private void updateStatus(String eventType, float x, float y, float p)
    {
        String s = String.format(Locale.CANADA, "Touch event: %11s {x=%4.1f, y=%4.1f, p=%1.3f}", eventType, x, y, p);
        Log.i(MYDEBUG, s);

        s = x == -1f ? "" : "" + (int)(x + 0.5f);
        xText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.xEquals), s));

        s = y == -1f ? "" : "" + (int)(y + 0.5f);
        yText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.yEquals), s));

        s = p == -1f ? "" : "" + (int)(100f * p + 0.5f); // x 100
        pText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.pEquals), s));

        s = (int)xVelocity == 0 ? "" : "" + (int)(xVelocity + 0.5f);
        vxText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.xEquals), s));

        s = (int)yVelocity == 0 ? "" : "" + (int)(yVelocity + 0.5f);
        vyText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.yEquals), s));

        s = (int)xFling == 0 ? "" : "" + (int)(xFling + 0.5f);
        flingxText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.flingXEquals), s));

        s = (int)yFling == 0 ? "" : "" + (int)(yFling + 0.5f);
        flingyText.setText(String.format(Locale.CANADA, "%s%s", getResources().getString(R.string.flingYEquals), s));
    }

    // Inner class to hold paint panel to receive touches
    private static class PaintPanel extends View
    {
        final static float INVALID = -1.0f;
        final static int PINK = 0xffffafb0;

        float x, y, pressure, radius;
        Paint p;

        // Should provide three constructors to correspond to each of the three in View.
        public PaintPanel(Context context, AttributeSet attrs, int defStyle)
        {
            super(context, attrs, defStyle);
            initialize();
        }

        public PaintPanel(Context context, AttributeSet attrs)
        {
            super(context, attrs);
            initialize();
        }

        public PaintPanel(Context context)
        {
            super(context);
            initialize();
        }

        private void initialize()
        {
            this.setBackgroundColor(PINK);
            p = new Paint();
            p.setColor(Color.DKGRAY);
            x = INVALID;
        }

        // This method is similar to paintComponent in Java/Swing.
        @Override
        protected void onDraw(Canvas canvas)
        {
            super.onDraw(canvas);
            if (x == INVALID)
                return; // nothing to draw (yet)

            // draw the circle on the canvas
            canvas.drawCircle(x, y, radius, p);
        }

        public void setCircle(float xArg, float yArg, float pressureArg)
        {
            x = xArg;
            y = yArg;
            pressure = pressureArg;
            radius = 100f * pressure;
            this.invalidate(); // forces a repaint (see onDraw)
        }

        public void clear()
        {
            x = INVALID;
            this.invalidate(); // forces a repaint
        }
    }
}