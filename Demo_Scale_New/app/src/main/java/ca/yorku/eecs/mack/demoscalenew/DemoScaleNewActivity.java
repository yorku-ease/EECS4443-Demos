package ca.yorku.eecs.mack.demoscalenew;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Demo Scale New - demonstrate moving and scaling an image using touch and multitouch
 * <p>
 * 
 * This demo is similar to Demo Scale except the image and its container are specified in an XML
 * layout file. The image is stored in a <code>ImageView</code> with a <code>LinearLayout</code>
 * serving as the container for the <code>ImageView</code>. See <code>res/layout/main.xml</code> for
 * details.
 * </p>
 * 
 * The activity <code>implements View.OnTouchListener</code> with the touch listener attached to the
 * container. Touch events are processed in <code>onTouchEvent</code>. The image's size and location
 * are changed through calls to the <code>setTranslationX</code>, <code>setTranslationY</code>,
 * <code>setScaleX</code>, and <code>setScaleY</code> methods. </p>
 * 
 * @author (c) Scott MacKenzie, 2011-2017
 * 
 */

public class DemoScaleNewActivity extends Activity implements View.OnTouchListener
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
	//final static int X_START = 10;
	//final static int Y_START = 10;
	final static float SCALE_START = 1f;

	LinearLayout container; // a container to hold the view which holds the image
	ImageView imageView; // a view to hold the image
	StatusPanel statusPanel; // a status panel to display the image coordinates, size, and scale
	Drawable targetImage; // a drawable resource (which is placed in the view)

	private ScaleGestureDetector scaleGestureDetector; // for 2-finger gestures (pinch)
	private GestureDetector gestureDetector; // for 1-finger gestures (e.g., double tap, fling)

	float scaleFactor, xPosition, yPosition, xRatio, yRatio, lastTouchX, lastTouchY;
	float lastScaleFactor; // needed for double tap zoom in/out
	boolean zoomMode; // for double-tap gestures (x3, /3)
	Rect r; // used to get coordinates of the image rectangle
	float xStart, yStart; // for positioning the image in the view
	DisplayMetrics dm;
	int imageWidth, imageHeight;

	/*
	 * The "active pointer" is the one currently moving the image. Even though the
	 * ScaleGestureDetector is used for 2-finger pinch and un-pinch gestures, we are still need to
	 * keep track of multiple contact points in the onTouchEvent method. Here's why: If a 2-finger
	 * pinch ends with the first finger lifted off the display, the position of the image should
	 * move according to the movement of the second finger. If we don't keep track of multiple touch
	 * points, the image would "jump" to the position of the second finger.
	 */
	private static final int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
		setContentView(R.layout.main);
		init();
	}

	private void init()
	{
		container = (LinearLayout)findViewById(R.id.image_container);
		imageView = (ImageView)findViewById(R.id.image_view);
		statusPanel = (StatusPanel)findViewById(R.id.statuspanel);

		/*
		 * Determine the pixel density of the display (used to scale fonts so they are consistent in
		 * size on different displays).
		 */
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);

		// Create the gesture detectors for scaling, flinging, double tapping
		scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
		gestureDetector = new GestureDetector(this, new MyGestureListener());

		/*
		 * NOTE: We are listening for touch events on the image's container, rather than on the
		 * image itself.
		 */
		container.setOnTouchListener(this);

		// init the rectangle (used with getHitRect to get image coordinates on screen)
		r = new Rect();

		// get the image from a resource
		targetImage = this.getResources().getDrawable(R.drawable.varihall);
		// the image below was used during debugging
		// targetImage = this.getResources().getDrawable(R.drawable.square_400_by_400);

		// give the drawable to the view.
		imageView.setImageDrawable(targetImage);

		// we need to do this to get scaling to work properly (see onDoubleTap)
		imageView.setPivotX(0f);
		imageView.setPivotY(0f);

		//clickReset(null); // ...to start with the image in its reset state
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		imageWidth = imageView.getMeasuredWidth();
		imageHeight = imageView.getMeasuredHeight();
		clickReset(null);
		//imageView.invalidate();
	}

	// Called when the "Reset" button is pressed.
	public void clickReset(View view)
	{
		zoomMode = false;
		xPosition = dm.widthPixels / 2f - imageWidth / 2f;
		yPosition = dm.heightPixels / 2f - imageHeight / 2f - statusPanel.getHeight();
		scaleFactor = SCALE_START;
		lastScaleFactor = scaleFactor;

		imageView.setTranslationX(xPosition);
		imageView.setTranslationY(yPosition);
		imageView.setScaleX(scaleFactor);
		imageView.setScaleY(scaleFactor);

		updateStatusPanel();
	}

	private void updateStatusPanel()
	{
		imageView.getHitRect(r);
		statusPanel.update(r.left, r.top, (r.right - r.left), (r.bottom - r.top), scaleFactor);
	}

	@Override
	public boolean onTouch(View v, MotionEvent me)
	{
		// only process touch events that are situated on the image
		if (!onImage((int)me.getX(), (int)me.getY()))
			return true;

		// inspect all events for two-finger gestures (pinch in, pinch out)
		scaleGestureDetector.onTouchEvent(me);

		// inspect all events for single-finger gestures (double tap, fling, etc.)
		gestureDetector.onTouchEvent(me);

		final int action = me.getAction();
		switch (action & MotionEvent.ACTION_MASK)
		{
		// --------------------------
			case MotionEvent.ACTION_DOWN:
			{
				// get the touch coordinate
				final float x = me.getX();
				final float y = me.getY();

				// save as "last touch" (used in ACTION_MOVE to compute finger delta)
				lastTouchX = x;
				lastTouchY = y;

				// get x/y position of the image on first touch
				imageView.getHitRect(r);
				xPosition = r.left;
				yPosition = r.top;

				// save the ID of this pointer
				activePointerId = me.getPointerId(0);

				// don't do anything yet (image will be moved in ACTION_MOVE)
				break;
			}

			// --------------------------
			case MotionEvent.ACTION_MOVE:
			{
				if (activePointerId != INVALID_POINTER_ID)
				{
					// find the index of the active pointer and fetch its position
					final int pointerIndex = me.findPointerIndex(activePointerId);
					final float x = me.getX(pointerIndex);
					final float y = me.getY(pointerIndex);

					// only move if the ScaleGestureDetector isn't processing a gesture.
					if (!scaleGestureDetector.isInProgress())
					{
						// compute finger position delta
						final float dx = x - lastTouchX;
						final float dy = y - lastTouchY;

						// apply the x/y deltas to the image position
						xPosition += dx;
						yPosition += dy;

						// move the image
						imageView.setTranslationX(xPosition);
						imageView.setTranslationY(yPosition);
					}

					// this touch coordinate becomes the last touch coordinate
					lastTouchX = x;
					lastTouchY = y;
				}
				break;
			}

			// -------------------------
			case MotionEvent.ACTION_UP:
			{
				activePointerId = INVALID_POINTER_ID;
				break;
			}

			// -----------------------------
			case MotionEvent.ACTION_CANCEL:
			{
				activePointerId = INVALID_POINTER_ID;
				break;
			}

			// ---------------------------------
			case MotionEvent.ACTION_POINTER_UP:
			{
				// Extract the index of the pointer that left the touch sensor
				final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = me.getPointerId(pointerIndex);
				if (pointerId == activePointerId)
				{
					// This was our active pointer going up. Choose a new active pointer.
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					activePointerId = me.getPointerId(newPointerIndex);

					/*
					 * It is also important to switch the last-touch x/y values to those for the new
					 * active pointer. If this is *not* done, the image location will jump if the
					 * first finger is lifted during a pinch gesture. To understand this point, try
					 * commenting out the following two statements and then doing a few pinch
					 * gestures while lifting one finger, then the other.
					 */
					lastTouchX = me.getX(newPointerIndex);
					lastTouchY = me.getY(newPointerIndex);
				}
				break;
			}

			// ----------------------------------
			case MotionEvent.ACTION_POINTER_DOWN:
			{
				// not needed (multitouch gestures handled by ScaleGestureDetector)
				break;
			}
		}
		updateStatusPanel();
		return true;
	}

	// return true if the x/y finger coordinate is on the image
	private boolean onImage(int x, int y)
	{
		imageView.getHitRect(r);
		return r.contains(x, y);
	}

	private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		/*
		 * onScale - This method has two purposes. First, get the scale factor from the
		 * ScaleGestureDetector. This is a simple matter of calling getScaleFactor. The scale factor
		 * is the amount the image should zoom in (scaleFactor > 1) or zoom out (scaleFactor < 1).
		 * Second, compute the new x/y coordinate of the image. This is a bit tricky, since the
		 * ScaleGestureDetector knows nothing about the image's size or position. The
		 * ScaleGestureDetector only knows about the finger contact points. Two values are obtained
		 * from the ScaleGestureDetector: focusX and focusY. These represent the x/y coordinate
		 * mid-way between the two fingers. These values are used along with the previous values to
		 * compute the new x/y position of the image.
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			scaleFactor *= detector.getScaleFactor();

			// don't let the object get too small or too large
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

			// ensure the scaling occurs about the focus point
			float focusX = detector.getFocusX();
			float focusY = detector.getFocusY();
			xPosition = focusX - xRatio * (int)(targetImage.getIntrinsicWidth() * scaleFactor + 0.5f);
			yPosition = focusY - yRatio * (int)(targetImage.getIntrinsicHeight() * scaleFactor + 0.5f);

			imageView.setTranslationX(xPosition);
			imageView.setTranslationY(yPosition);
			imageView.setScaleX(scaleFactor);
			imageView.setScaleY(scaleFactor);
			return true;
		}

		// Record the x/y coordinate of the focus point at the beginning of a scale gesture
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			// the touch position relative to the left/top edge of the image
			float xOffset = detector.getFocusX() - xPosition;
			float yOffset = detector.getFocusY() - yPosition;

			// See API or discussion of x/y ratios
			xRatio = xOffset / (int)(targetImage.getIntrinsicWidth() * scaleFactor + 0.5f);
			yRatio = yOffset / (int)(targetImage.getIntrinsicHeight() * scaleFactor + 0.5f);

			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
			// this is needed if a zoom (double-tap) follows a scale (pinch)
			lastScaleFactor = scaleFactor;
		}
	}

	// look for fling or double tap gestures
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
		{
			if (onImage((int)me2.getX(), (int)me2.getY()))
			{
				xPosition += velocityX / 10f;
				yPosition += velocityY / 10f;

				imageView.animate().translationXBy(velocityX / 10f).translationYBy(velocityY / 10f).setInterpolator(
						new DecelerateInterpolator());
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent me)
		{
			if (!onImage((int)me.getX(), (int)me.getY()))
				return true;

			// toggle between zoom out (/3) and zoom in (x3)
			if (zoomMode)
				scaleFactor /= 3f; // zoom out
			else
				scaleFactor *= 3f; // zoom in

			/*
			 * The calculation below looks simple, but it was a challenge to get a good
			 * implementation for a double-tap zoom in/out. The double-tap zoom in/out has the
			 * desirable property that zooming occurs around the point in the image where the user's
			 * finger taps. This is obvious and intuitive from the user's perspective, but it was
			 * difficult to achieve. It makes most sense to do this by repositioning the
			 * view's pivot point to the location of the finger tap. But this does not seem to be
			 * possible due to a peculiar interaction between the pivot point and scaling. See...
			 * 
			 * http://stackoverflow.com/questions/14415035/setpivotx-works-strange-on-scaled-view/
			 * 14522916#14522916
			 * 
			 * The approach here is to set the view's pivot point to 0,0 during initialization. (The
			 * default pivot point is the middle of the image.) Since scaling occurs about the
			 * pivot, this approach requires the image position to be translated when scaling. The
			 * new x/y position for translation is calculated using the (i) the old scaling factor,
			 * (ii) the new scaling factor, and (iii) the location of the finger tap relative to the
			 * left/top edge of the image.
			 */
			imageView.getHitRect(r);
			xPosition = me.getX() - ((me.getX() - r.left) / lastScaleFactor) * scaleFactor;
			yPosition = me.getY() - ((me.getY() - r.top) / lastScaleFactor) * scaleFactor;
			lastScaleFactor = scaleFactor;

			// do it! (with feel-good animation)
			imageView.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationX(xPosition).translationY(yPosition);

			// toggle zoom mode (next double tap has opposite effect)
			zoomMode = !zoomMode;

			// doubleTapOnImage = false;
			return true;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
		{
			return true;
		}

		@Override
		public boolean onDown(MotionEvent me)
		{
			return true;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent me)
		{
			return true;
		}

		@Override
		public void onLongPress(MotionEvent me)
		{
			// do something interesting for a long press
		}
	}
}
