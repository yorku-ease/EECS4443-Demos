package ca.yorku.eecs.mack.demomultitouch;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Multitouch - demonstration of multitouch input on Android devices. </p>
 *
 * Related information: </p>
 *
 * API Guides:<p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/ui/ui-events.html">Input Events</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/view/MotionEvent.html">
 * <code>MotionEvent</code></a> </ul> <p>
 *
 * Android Developers Blog: <p>
 *
 * <ul> <li> <a href= "http://android-developers.blogspot.ca/2010/06/making-sense-of-multitouch.html" >Making Sense of
 * Multitouch</a> </ul><p>
 *
 * </blockquote>
 *
 * This demo examines the tracking of multiple touch points. For each touch point, a circle is painted on the display
 * below the finger. The circle follows the user's finger as the finger moves. The colour for each touch point
 * remains consistent, even if earlier touch points terminate. <p>
 *
 * Below is a screen snap showing four contact points (left) and UI photo showing three (right):
 *
 * <center> <a href="DemoMultitouch-1.jpg"><img src="DemoMultitouch-1.jpg" width=" 300"></a> <a
 * href="DemoMultitouch-2.jpg"><img src="DemoMultitouch-2.jpg" width=" 500"></a> </center> </p>
 *
 * If fingers always left the tablet in the reverse order from touching, multitouch would be simple. Of course, there is
 * no guarantee that finger events will be so orderly. To manage touch events for multiple fingers, each finger touch
 * point (also called a <i>pointer</i>) has both an <i>index</i> and an <i>identifier</i>. Understanding the distinction
 * between a pointer index and a pointer identifier is the first challenge in working with multitouch. We'll attempt a
 * brief explanation here, but the references above are the primary sources for unravelling the details of multitouch.
 * The blog reference is particularly helpful as it builds up the code bit by bit, with explanations along the way.
 * </p>
 *
 *
 * We noted in Demo_Touch that the primary actions for touch events are </p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Action <th>Description
 *
 * <tr> <td><code>ACTION_DOWN</code> <td>A finger has touched the display surface
 *
 * <tr> <td><code>ACTION_MOVE</code> <td>The finger has moved
 *
 * <tr> <td><code>ACTION_UP</code> <td>The finger has left the display surface </table> </blockquote> </p>
 *
 * Multitouch is a bit more complicated. The primary actions for multitouch are </p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th>Action <th>Description
 *
 * <tr> <td><code>ACTION_DOWN</code> <td>The first finger has touched the display surface
 *
 * <tr> <td><code>ACTION_POINTER_DOWN</code> <td>A subsequent finger has touched the display surface
 *
 * <tr> <td><code>ACTION_MOVE</code> <td>A finger has moved on the display surface
 *
 * <tr> <td><code>ACTION_POINTER_UP</code> <td>A finger (but not the last finger) has left the display surface
 *
 * <tr> <td><code>ACTION_UP</code> <td>The last finger has left the display surface </table> </blockquote> </p>
 *
 * Of course, there is no guarantee that the first finger to touch the display surface will be the last finger to leave
 * the display surface. When a finger, or pointer, first touches the display surface, it is assigned an identifier
 * (<code>id</code>). The <code>id</code> will not change for the duration of the movement associated with that finger.
 * However, the index of the finger/pointer might change from one touch event to the next. This is simply an artefact of
 * the possibility of multiple fingers touching and leaving the display surface, and in any order. </p>
 *
 * As noted in the <code>MotionEvent</code> API, <p>
 *
 * <blockquote> <i>The number of pointers only ever changes by one as individual pointers go up and down.</i>
 * </blockquote>
 *
 * The index of the changed pointer is retrieved from the <code>MotionEvent</code> object ( <code>me</code>) by </p>
 *
 * <pre>
 *      int pointerIndex = me.getActionIndex();
 * </pre>
 *
 * The <code>id</code> is retrieved by<p>
 *
 * <pre>
 *      int pointerId = me.getPointerId(pointerIndex);
 * </pre>
 *
 * In this demo, touch points are managed through two arrays, <code>touchPointId</code> and <code>touchPoints</code>.
 * The goal is to maintain consistency between an <code>id</code>, which corresponds to a particular finger or pointer,
 * and the <i>x/y</i> coordinates associated with that <code>id</code>. Consult the source code for further details.
 * </p>
 *
 * @author (c) Scott MacKenzie 2011-2018
 */

public class DemoMultitouchActivity extends Activity implements View.OnTouchListener
{
    //final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final static int MAX_TOUCH_POINTS = 10;
    final static int INVALID = -1;

    PaintPanel touchPanel;
    int touchCount = 0;
    int[] touchPointIds = new int[MAX_TOUCH_POINTS];
    PointF[] touchPoints = new PointF[MAX_TOUCH_POINTS];

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initialize()
    {
        // cast removed (not needed anymore, avoids warning message)
        touchPanel = findViewById(R.id.paintPanel);
        touchPanel.setOnTouchListener(this);

        // initialize touch point IDs with invalid markers
        for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
        {
            touchPointIds[i] = INVALID;
            touchPoints[i] = new PointF(-1f, -1f);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        if (v != touchPanel)
            return false;

        // get the index of the pointer (NOTE: will be 0 for ACTION_DOWN or ACTION_UP)
        final int pointerIndex = me.getActionIndex();

        // get the id of the pointer
        final int id = me.getPointerId(pointerIndex);

        switch (me.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN: // this is the first touch point in a gesture

                touchPointIds[touchCount] = id;
                touchPoints[touchCount].x = me.getX();
                touchPoints[touchCount].y = me.getY();
                ++touchCount;
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // this is a subsequent touch point

                // find an empty spot in the arrays for the new touch point
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                    if (touchPointIds[i] == INVALID)
                    {
                        touchPointIds[i] = id;
                        touchPoints[i].x = me.getX(pointerIndex);
                        touchPoints[i].y = me.getY(pointerIndex);
                        ++touchCount;
                        break;
                    }
                break;

            case MotionEvent.ACTION_MOVE: // finger movement (update x/y coordinates for all valid touch points)

                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                {
                    if (touchPointIds[i] != INVALID)
                    {
                        touchPoints[i].x = me.getX(me.findPointerIndex(touchPointIds[i]));
                        touchPoints[i].y = me.getY(me.findPointerIndex(touchPointIds[i]));
                    }
                }
                break;

            // a touch point is raised
            case MotionEvent.ACTION_POINTER_UP: // a finger goes up (but it's not the last finger)
            case MotionEvent.ACTION_UP: // last touch point (end of gesture)

                // find the touch point and make it invalid
                for (int i = 0; i < MAX_TOUCH_POINTS; ++i)
                    if (touchPointIds[i] == id)
                    {
                        touchPointIds[i] = INVALID;
                        touchPoints[i].x = -1f;
                        touchPoints[i].y = -1f;
                        --touchCount;
                        break;
                    }
                v.performClick(); // avoids accessibility warning
                break;

            case MotionEvent.ACTION_CANCEL:
                break;
        }
        touchPanel.drawCircles(touchPoints);
        return true;
    }
}