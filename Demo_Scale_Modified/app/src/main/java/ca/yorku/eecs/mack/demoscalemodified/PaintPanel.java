package ca.yorku.eecs.mack.demoscalemodified;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.CountDownTimer;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

public class PaintPanel extends View
{
	final static String MYDEBUG = "MYDEBUG";
	final int TIMER_DELAY = 5; // ms
	final int ZOOM_DURATION = 300; // ms (the duration of the zoom in/out on double tap)
	final int STEPS = 20; // number of steps for the zoom in/out animation

	private Drawable targetImage;
	public float xPosition;
	public float yPosition;
	public float scaleFactor;
	public int imageIntrinsicWidth;
	public int imageIntrinsicHeight;

	/*
	 * The "active pointer" is the one currently moving the image. Even though the
	 * ScaleGestureDetector is used for 2-finger pinch and un-pinch gestures, we are still keeping
	 * track of multiple contact points in the onTouchEvent method. Here's why: If a 2-finger pinch
	 * ends with the first finger lifted off the display, the position of the image will move
	 * according to the movement of the second finger. If we don't keep track of multiple touch
	 * points, the image will "jump" to the position of the second finger.
	 */
	private static final int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;

	StatusPanel sp;
	CountDownTimer flingTimer;
	private float lastTouchX;
	private float lastTouchY;
	private boolean imageSelected;

	private ScaleGestureDetector scaleGestureDetector;
	private GestureDetector gestureDetector;
	private float xRatio, yRatio;
	private float flingVelocity;
	private float flingAngle;

	// added for double tap zoom in/out
	private boolean zoomIn;
	private CountDownTimer zoomTimer;
	int animateStepCount;
	float scaleStep, xStep, yStep;

	// Provide three constructors to correspond to each of the three in View.
	public PaintPanel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public PaintPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public PaintPanel(Context context)
	{
		super(context);
		initialize(context);
	}

	private void initialize(Context context)
	{
		this.setBackgroundColor(0xffffafb0); // AARRGGBB: opacity, red, green, blue

		// targetImage = context.getResources().getDrawable(R.drawable.varihall);

		// use the following image for debugging
		targetImage = context.getResources().getDrawable(R.drawable.lassonde);

		imageIntrinsicWidth = targetImage.getIntrinsicWidth();
		imageIntrinsicHeight = targetImage.getIntrinsicHeight();
		targetImage.setBounds(0, 0, imageIntrinsicWidth, imageIntrinsicHeight);

		xPosition = 10;
		yPosition = 10;
		imageSelected = false;
		scaleFactor = 1f;

		zoomIn = true; // first double-tap will zoom in

		// create the scale gesture detector and fling/double-tap gesture detector
		scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
		gestureDetector = new GestureDetector(context, new GestureListener());

		// use a animate the fling gesture (see onFling and doFling)
		flingTimer = new CountDownTimer(TIMER_DELAY, TIMER_DELAY)
		{
			public void onTick(long millisUntilFinished)
			{
			}

			public void onFinish()
			{
				doFling();
			}
		};

		// use a timer to animate the zooming in/out (see onDoubleTap and doAnimate)
		zoomTimer = new CountDownTimer(ZOOM_DURATION / STEPS, ZOOM_DURATION / STEPS)
		{
			public void onTick(long millisUntilFinished)
			{
			}

			public void onFinish()
			{
				doAnimate();
			}
		};
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.save();
		canvas.translate(xPosition, yPosition);
		canvas.scale(scaleFactor, scaleFactor);
		targetImage.draw(canvas);
		canvas.restore();

		// update the status panel
		final int x = (int)(xPosition + 0.5f); // image left position
		final int y = (int)(yPosition + 0.5f); // image top position
		final int w = (int)(imageIntrinsicWidth * scaleFactor + 0.5f); // image width
		final int h = (int)(imageIntrinsicHeight * scaleFactor + 0.5f); // image height
		sp.update(x, y, w, h, scaleFactor);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		/*
		 * For the modification, these method are moved outside the if-condition. gestureDetector
		 * will check for both fling gestures and double-tap gestures. If this method is left inside
		 * the if-condition, double-tap gestures are not recognized, because the gestureInProgress
		 * is cleared in ACTION_UP. Moving this method outside the if-condition ensures double-tap
		 * gestures are recognized irrespective of the state of the gestureInProgress flag.
		 */
		scaleGestureDetector.onTouchEvent(me);
		gestureDetector.onTouchEvent(me);

		final int action = me.getAction();
		switch (action & MotionEvent.ACTION_MASK)
		{
		// --------------------------
			case MotionEvent.ACTION_DOWN:
			{
				final float x = me.getX();
				final float y = me.getY();

				// determine if the touch point is inside the image.
				float left = xPosition;
				float top = yPosition;
				float right = left + imageIntrinsicWidth * scaleFactor;
				float bottom = top + imageIntrinsicHeight * scaleFactor;
				RectF r = new RectF(left, top, right, bottom);
				boolean inside = r.contains(x, y);

				// only begin the gesture if the touch point is inside the image.
				if (inside)
				{
					/*
					 * imageSelected is used to ensure the gestures only have their effect if the
					 * initial ACTION_DOWN was inside the images. See ACTION_MOVE, onScaleBegin,
					 * onScale, and onFling. This flag is cleared on ACTION_UP.
					 */
					imageSelected = true;
					lastTouchX = x;
					lastTouchY = y;

					// save the ID of this pointer
					activePointerId = me.getPointerId(0);
				}
				break;
			}

			// --------------------------
			case MotionEvent.ACTION_MOVE:
			{
				// ignore the move gesture is the initial ACTION_DOWN was outside the image
				if (!imageSelected)
					break;

				if (activePointerId != INVALID_POINTER_ID)
				{
					// find the index of the active pointer and fetch its position
					final int pointerIndex = me.findPointerIndex(activePointerId);
					final float x = me.getX(pointerIndex);
					final float y = me.getY(pointerIndex);

					// use this position to compute the 'delta' for the image
					final float dx = x - lastTouchX;
					final float dy = y - lastTouchY;
					xPosition += dx;
					yPosition += dy;
					invalidate();
					
					// the current pointer position becomes the last position
					lastTouchX = x;
					lastTouchY = y;
				}
				break;
			}

			// -------------------------
			case MotionEvent.ACTION_UP:
			{
				activePointerId = INVALID_POINTER_ID;
				imageSelected = false;
				break;
			}

			// -----------------------------
			case MotionEvent.ACTION_CANCEL:
			{
				activePointerId = INVALID_POINTER_ID;
				imageSelected = false;
				break;
			}

			// ---------------------------------
			case MotionEvent.ACTION_POINTER_UP:
			{
				// extract the index of the pointer that left the touch sensor
				final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				final int pointerId = me.getPointerId(pointerIndex);
				if (pointerId == activePointerId)
				{
					/*
					 * This was our active pointer going up. Choose a new active pointer and adjust
					 * accordingly. To understand why this code is necessary, read the comments
					 * above -- where activePointerId is declared.
					 */
					final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
					lastTouchX = me.getX(newPointerIndex);
					lastTouchY = me.getY(newPointerIndex);
					activePointerId = me.getPointerId(newPointerIndex);
				}
				break;
			}

			// ----------------------------------
			case MotionEvent.ACTION_POINTER_DOWN:
			{
				break;
			}
		}
		return true;
	}

	/*
	 * doFling - This method is called from the CountDownTimer upon timeout. The method adjusts the
	 * image's x/y position according to the velocity and direction (angle) of the finger upon lift.
	 * Then, we start the timer and invalidate the PaintPanel to force a re-paint of the image. On
	 * the next timeout, repeat. dx and dy decrease with each invocation by decreasing the velocity.
	 * When dz dips below a threshold, the timer is cancelled, thus terminating the fling.
	 * 
	 * The algorithm here is likely similar to that used by Android's animation framework. We can't
	 * use the animation services here because xPosition and yPosition are used to directly
	 * translate the canvas in onDraw.
	 */
	private void doFling()
	{
		final float FACTOR = 25f;
		final float DIVISOR = 1.5f;
		final float THRESHOLD = 1f;

		float dx = (float)Math.cos(flingAngle) * (flingVelocity / FACTOR);
		float dy = (float)Math.sin(flingAngle) * (flingVelocity / FACTOR);

		xPosition += dx;
		yPosition += dy;
		flingVelocity /= DIVISOR;

		float dz = (float)Math.sqrt(dx * dx + dy * dy);
		if (dz < THRESHOLD)
			flingTimer.cancel();
		else
			flingTimer.start();
		invalidate();
	}

	/*
	 * Recalculate the scaleFactor, xPosition, and yPosition, then invalidate to redraw. doAnimate
	 * will execute STEPS times with animateStepCount acting as the counter. scaleStep is added
	 * STEPS time to scaleFactor to achieve a zoom of x3 (zoom in) or /3 (zoom out). Note: If we are
	 * zooming out, scaleStep is negative. See onDoubleTap for the calculation of zoomStep (and the
	 * rationale and calculation for xStep and yStep).
	 */
	public void doAnimate()
	{
		--animateStepCount;
		if (animateStepCount >= 0)
		{
			scaleFactor += scaleStep;
			xPosition += xStep;
			yPosition += yStep;
			zoomTimer.start();
			invalidate();
		} else
			zoomTimer.cancel();
	}

	public void setStatusPanel(StatusPanel spArg)
	{
		sp = spArg;
	}

	private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		/*
		 * onScaleBegin - This method computes xRatio and yRatio. xRatio is the x offset of the
		 * focus point (relative to the left edge of the image) divided by the width of the image.
		 * yRatio is the y offset of the focus point (relative to the top of the image) divided by
		 * the height of the image. They are computed at the beginning of the gesture and are used
		 * later (see onScale) to ensure the new x/y position of the image, as used in onDraw, is
		 * such that the focus point in the image remains between the fingers as the gesture
		 * proceeds.
		 */
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			// ignore this scale gesture if the initial ACTION_DOWN was outside the image
			if (!imageSelected)
				return true;

			float xOffset = detector.getFocusX() - xPosition;
			float yOffset = detector.getFocusY() - yPosition;

			xRatio = xOffset / (imageIntrinsicWidth * scaleFactor);
			yRatio = yOffset / (imageIntrinsicHeight * scaleFactor);
			return true;
		}

		/*
		 * onScale - This method has two purposes. First, get the scale factor from the
		 * ScaleGestureDetector. This is a simple matter of calling getScaleFactor. The scale factor
		 * is the amount the image should zoom in (scaleFactor > 1) or zoom out (scaleFactor < 1).
		 * Second, compute the new x/y coordinate of the image. This is a bit tricky, since the
		 * ScaleGestureDetector knows nothing about the image's size or position. The
		 * ScaleGestureDetector only knows about the finger contact points. Two values are obtained
		 * from the ScaleGestureDetector: focusX and focusY. These represent the x/y coordinate
		 * mid-way between the two fingers. This coordinate is used in conjunction with xRatio and
		 * yRatio (see onScaleBegin) to compute the new x/y position of the image. Note that while
		 * scaleFactor, positionX, and positionY are determined here, it is in the onDraw method
		 * where they are actually used in re-painting the image.
		 */
		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			// ignore this scale gesture if the initial ACTION_DOWN was outside the image
			if (!imageSelected)
				return true;

			scaleFactor *= detector.getScaleFactor();

			// don't let the object get too small or too large
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));

			// ensure the scaling occurs about the focus point
			float focusX = detector.getFocusX();
			float focusY = detector.getFocusY();
			xPosition = focusX - xRatio * imageIntrinsicWidth * scaleFactor;
			yPosition = focusY - yRatio * imageIntrinsicHeight * scaleFactor;

			invalidate();
			return true;
		}
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent me)
		{
			// no implementation (handle in onTouchEvent)
			imageSelected = false;
			return false;
		}

		/*
		 * onFling - This method is executed when a fling or flick gesture is detected. The goal is
		 * to determine the fling velocity and the fling direction (angle). Then, the timer is
		 * started. The real work is done in doFling which is called each time the CountDownTimer
		 * times out.
		 */
		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
		{
			// ignore this fling gesture if the initial ACTION_DOWN was outside the image
			if (!imageSelected)
				return true;

			flingVelocity = (float)Math.sqrt(velocityX * velocityX + velocityY * velocityY);
			flingAngle = (float)Math.atan2(velocityY, velocityX);
			flingTimer.start();
			return true;
		}

		/*
		 * Zoom in/out on a double tap. The zoom is x3 (zoom in) or /3 (zoom out). scaleStep, xStep,
		 * and yStep are calculated to achieve this after STEPS iterations, as done in doAnimate.
		 * 
		 * Of course, we are using "focus-point zooming" -- the zooming is centred at the location
		 * in the image where the double-tap occurred.
		 */
		@Override
		public boolean onDoubleTap(MotionEvent me)
		{
			// determine if the double-tap is inside the image
			float left = xPosition;
			float top = yPosition;
			float right = left + imageIntrinsicWidth * scaleFactor;
			float bottom = top + imageIntrinsicHeight * scaleFactor;
			RectF r = new RectF(left, top, right, bottom);
			boolean inside = r.contains(me.getX(), me.getY());

			// ignore the double-tap if the location is outside the image
			if (!inside)
				return true;

			float xOffset = me.getX() - xPosition;
			float yOffset = me.getY() - yPosition;

			animateStepCount = STEPS;
			if (zoomIn)
			{
				scaleStep = (2f * scaleFactor) / STEPS;
				xStep = -(2f * xOffset) / STEPS;
				yStep = -(2f * yOffset) / STEPS;

			} else
			{
				scaleStep = -(2f * scaleFactor) / (3f * STEPS);
				xStep = (2f * xOffset) / (3f * STEPS);
				yStep = (2f * yOffset) / (3f * STEPS);
			}

			zoomIn = !zoomIn; // toggle so next double-tap has opposite effect
			doAnimate();
			return true;
		}

		@Override
		public void onLongPress(MotionEvent me)
		{
			// do something interesting for a long press
		}
	}
}
