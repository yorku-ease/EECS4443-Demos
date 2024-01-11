package ca.yorku.eecs.mack.demoscale;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

/**
 * Demo_Scale - demonstrate moving and scaling an image using touch and multitouch <p>
 *
 * Related information: <p>
 *
 * <blockquote> Developer Training: <p>
 *
 * <ul> <li> <a href="http://developer.android.com/training/gestures/index.html">Using Touch Gestures</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li> <a href= "http://developer.android.com/reference/android/view/GestureDetector.html">
 * <code>GestureDetector</code></a> <li> <a href= "http://developer.android.com/reference/android/view/GestureDetector.SimpleOnGestureListener.html"
 * > <code>GestureDetector.SimpleOnGestureListener</code></a> <li> <a href= "http://developer.android.com/reference/android/view/ScaleGestureDetector.html"
 * > <code>ScaleGestureDetector</code></a> <li> <a href= "http://developer.android.com/reference/android/view/ScaleGestureDetector.SimpleOnScaleGestureListener.html"
 * ><code>ScaleGestureDetector.SimpleOnScaleGestureListener</code></a> </ul> </blockquote>
 *
 * The references above are in addition to those cited in Demo_Multitouch. In particular, have another look at the
 * Android Developers Blog article by Adam Powell, "Making Sense of Multitouch" (link in Demo_Multitouch). Powell's
 * blog builds up
 * the code bit by bit, with explanations along the way. <p>
 *
 * The following are screen snaps of the application upon launch (left) and after the image has been moved and scaled
 * (right). <p>
 *
 * <center> <a href="./javadoc_images/DemoScale-1.jpg"><img src="./javadoc_images/DemoScale-1.jpg" width="300" alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="./javadoc_images/DemoScale-2.jpg"><img src="./javadoc_images/DemoScale-2.jpg" width="300" alt="image"></a> </center> <p>
 *
 * The image is a resource (<code>varihall.jpg</code>) that is loaded into a <code>Drawable</code> which is drawn into a
 * canvas. The pink region is a <code>PaintPanel</code> &ndash; a subclass of <code>View</code>. The drawing occurs
 * within the <code>onDraw</code> method in the <code>PaintPanel</code> class. Touch events are also handled in the
 * <code>PaintPanel</code> class, by implementing <code>onTouchEvent</code>. This is a slightly different setup from
 * Demo_Touch and Demo_Multitouch where <code>onTouch</code> was implemented in the Activity hosting the view. The
 * difference in organization is illustrated below. </p>
 *
 * <center> <a href="./javadoc_images/DemoScale-7.jpg"><img src="./javadoc_images/DemoScale-7.jpg" width="550" alt="image"></a> </center> <p>
 *
 *
 * Touch events in Android are more generally called <i>gestures</i>. A gesture begins when the first finger (or
 * <i>pointer</i>) touches the display surface and ends when the last finger leaves the display surface. During a
 * gesture, other fingers may come and go, creating a variety of UI possibilities. Bear in mind that the last
 * finger to leave the display surface is not necessarily the finger that initiated the gesture. <p>
 *
 * The most common example of a multitouch gesture is a two-finger "pinch". A pinch gesture has two forms: <i>pinch
 * open</i> to zoom in to content (below left) and <i>pinch close</i> to zoom out of content (below right). <p>
 *
 * <center> <a href="./javadoc_images/DemoScale-3.jpg"><img src="./javadoc_images/DemoScale-3.jpg" width="250" alt="image"></a>
 * &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a href="./javadoc_images/DemoScale-4.jpg"><img src="./javadoc_images/DemoScale-4.jpg"
 * width="250" alt="image"></a> </center> <p>
 *
 * Zooming in and zooming out are often called <i>scaling</i>. In this demo, we show how to move and scale an image
 * using single-touch and multitouch gestures. <p>
 *
 * Although we could write our own gesture-detection code to process low-level data in touch events, this amounts to
 * re-inventing the wheel. The Android framework provides two classes that do the work for us: <p>
 *
 * <ul> <li><code>GestureDetector</code> &mdash; for single-touch gestures, such as taps, double-taps, long presses, or
 * flings
 *
 * <li><code>ScaleGestureDetector</code> &mdash; for multitouch gestures, such as pinch open and pinch close. <p> </ul>
 *
 *
 * In both cases, there is a convenience class that provides empty implementations of the listener methods. These are
 * <p>
 *
 * <ul> <li> <code>GestureDetector.SimpleOnGestureListener</code> &mdash; listener methods for single-touch gestures
 * (e.g., <code>onFling</code>) <li> <code>ScaleGestureDetector.SimpleOnScaleGestureListener</code> &mdash; listener
 * methods for multitouch gestures (e.g., <code>onScale</code>) </ul> <p>
 *
 * By defining a custom class that extends the convenience class, we can provide implementations for methods of
 * interest, and ignore the others. (This is much the same as adapter classes in Java/Swing.) <p>
 *
 * Three features are included here that go beyond the implementation described in Powell's blog. These are described
 * next. <p>
 *
 * <b>Image Selection</b> </p>
 *
 * The gestures to manipulate the image only work if the initial touch point is inside the image. This is a simple
 * matter of creating a rectangle holding the current coordinates of the image and passing the rectangle to the
 * <code>contains</code> method of the <code>RectF</code> class, along with the <i>x-y</i> coordinate of the touch
 * point. The return value is <code>true</code> if the touch point is inside the image. This test is done in the
 * <code>onTouchEvent</code> method, coincident with <code>MotionEvent.ACTION_DOWN</code>. A boolean flag
 * (<code>imageSelected</code>) is used to signal that the initial ACTION_DOWN was inside the image. This flag is tested
 * in several places to ensure that the implemented gestures (move, fling, scale) are only performed if the initial
 * ACTION_DOWN was inside the image. <p>
 *
 * <b>Focus-point Zooming</b> </p>
 *
 * During the two-finger zoom gesture, the <i>focus point</i> remains midway between the two fingers as they move about,
 * pinching in or pinching out. This UI feature is simple, intuitive, and expected. However, it is a tricky to
 * implement because the touch events are on the view, not on the image. It's a two-step process. First, the
 * <code>onScaleBegin</code> method in the scale listener records the focus point at the beginning of the pinch gesture.
 * The focus point is the point midway between the two fingers when the second finger makes contact with the display
 * surface. The focus point is the point of interest to the user: It is the location within the image the user wishes to
 * zoom in to or zoom out of. However, the focus point reported by the <code>ScaleGestureDetector</code> is with respect
 * to the view, not the image. This is seen in the left image below for the <i>x</i>-coordinate of the focus point (as
 * reported by <code>getFocusX</code>). So, we compute <code>xOffset</code> &ndash; the offset of the focus point within
 * the image (below, left). From this, we compute <code>xRatio</code> &ndash; the focus point within the image divided
 * by the current width of the image (below, right). <p>
 *
 * <center> <a href="./javadoc_images/DemoScale-5.jpg"><img src="./javadoc_images/DemoScale-5.jpg" height="350" alt="image"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="./javadoc_images/DemoScale-6.jpg"><img src="./javadoc_images/DemoScale-6.jpg" height="250" alt="image"></a>
 *
 * </center> <p>
 *
 * <code>xRatio</code> and <code>yRatio</code> are then used in the <code>onScale</code> method during the gesture to
 * compute the new <i>x-y</i> position of image (which is used in <code>onDraw</code> when the image is painted). The
 * goal is to ensure that the focus point &mdash; the point in the image midway between the two fingers when the gesture
 * begins &mdash; is maintained throughout the gesture. <p>
 *
 * <b>Fling Gesture</b> </p>
 *
 * Fling or flick gestures are implemented. While moving the image, if the finger is still moving when it is raised at
 * the end of the gesture, the image will continue to move in the same direction as the finger just prior to lift. Such
 * motion is called <i>animation</i> in the Android documentation. The velocity of the image movement is proportional to
 * the finger velocity, but decreases to zero after a brief interval (a second or two, typically). Fling gestures are
 * implemented using <code>MyGestureListener</code>, a class that extends <code>GestureDetector.SimpleOnGestureListener</code>.
 * The only method implemented is <code>onFling</code>. The call to <code>onFling</code> occurs within the
 * <code>onTouchEvent</code> method of the <code>GestureDetector</code> when a fling gesture is detected. The
 * <code>GestureDetector</code>'s <code>onTouchEvent</code> method is called at the beginning of the
 * <code>PaintPanel</code>'s <code>onTouchEvent</code> method to allow the <code>GestureDetector</code> to inspect the
 * <code>MotionEvent</code>s (in same way as <code>onTouchEvent</code> for <code>ScaleGestureDetector</code> was called
 * to inspect <code>MotionEvent</code>s for a scale gesture). <p>
 *
 * The <code>onFling</code> method performs three tasks: compute the fling velocity, compute the fling direction
 * (angle), and start a timer. The bulk of the work thereafter is done in a method called <code>doFling</code> which is
 * called from the timer upon each timeout (nominally, every 5 ms). The <code>doFling</code> method works with the fling
 * velocity and fling angle to compute the new <i>x/y</i> position of the image. The fling velocity is decreased each
 * time <code>onFling</code> executes. The timer is either restarted, to prepare for the next execution of
 * <code>onFling</code>, or cancelled, if the movement distance is below a threshold. <code>onFling</code> ends by
 * calling <code>invalidate</code> to force a re-paint of the image in the new location. <p>
 *
 * Alternative methods for scaling and animation are demonstrated in a subsequent demo program, Demo_GridView. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2018
 */

public class DemoScaleActivity extends Activity
{
    PaintPanel imagePanel; // the panel in which to paint the image
    StatusPanel statusPanel; // a status panel the display the image coordinates, size, and scale

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
        setContentView(R.layout.main);

        // get references to UI components
        // cast removed (not needed anymore, avoids warning message)
        imagePanel = findViewById(R.id.paintpanel);
        statusPanel = findViewById(R.id.statuspanel);

        // give the image panel a reference to the status panel
        imagePanel.setStatusPanel(statusPanel);
    }

    // Called when the "Reset" button is pressed.
    public void clickReset(View view)
    {
        imagePanel.xPosition = 10;
        imagePanel.yPosition = 10;
        imagePanel.scaleFactor = 1f;
        imagePanel.invalidate();
    }
}
