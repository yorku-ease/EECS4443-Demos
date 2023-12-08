package ca.yorku.eecs.mack.demogridview;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Locale;

public class ImageViewerActivity extends Activity implements OnTouchListener
{
	RelativeLayout container; // parent view (holds the image view)
	ImageView imageView; // holds the JPG file
	TextView textView; // status info
	ImageDownloader imageDownloader; // worker class/thread to get the images

	String directory; // directory containing the images
	String[] filenames; // array of all JPG files in the directory

	int index; // image currently displayed
	int displayWidth, displayHeight;
	float positionX;
	float positionY;
	float xRatio, yRatio;
	float scaleFactor, lastScaleFactor;

	// The �active pointer� is the one currently moving the image.
	private static final int INVALID_POINTER_ID = -1;
	private int activePointerId = INVALID_POINTER_ID;

	private float lastTouchX;
	private float lastTouchY;
	private ScaleGestureDetector scaleGestureDetector;
	private GestureDetector gestureDetector;
	private boolean zoomMode;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.imageviewer);
		container = (RelativeLayout)findViewById(R.id.imagecontainer);
		imageView = (ImageView)findViewById(R.id.imageView);
		textView = (TextView)findViewById(R.id.textView);

		// attach a touch listener so the image will respond to touch events
		// NOTE: listener attached to the parent container, not the view
		container.setOnTouchListener(this);

		// data passed from startActivity in DirectoryContentsActivity
		Bundle b = getIntent().getExtras();
		filenames = b.getStringArray("imageFilenames");
		directory = b.getString("directory");
		index = b.getInt("position");

		// set and apply the default for the scaling, transformation, etc.
		setDefaults();

		// Create the gesture detectors for scaling, flinging, double tapping
		scaleGestureDetector = new ScaleGestureDetector(this, new MyScaleGestureListener());
		gestureDetector = new GestureDetector(this, new MyGestureListener());

		// determine display width and height(will be used to scale images)
		// NOTE: width used to down-sample images to same memory
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		displayWidth = dm.widthPixels;
		displayHeight = displayWidth;

		imageView.setMaxWidth(displayWidth);
		imageView.setMaxHeight(displayHeight);

		// instantiate the image downloader (this is where the real work is done; see displayImage
		// method)
		imageDownloader = new ImageDownloader();
	}

	// call displayImage here (not in the constructor) to ensure the first image appears
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		displayImage(index);
	}

	// advance to the previous image
	private void previousImage()
	{
		--index;
		if (index < 0)
			index = filenames.length - 1;
		displayImage(index);
	}

	// advance to the next image
	private void nextImage()
	{
		++index;
		if (index >= filenames.length)
			index = 0;
		displayImage(index);
	}

	// display the image at the indicated position in the filenames array
	private void displayImage(int idx)
	{
		// form the path to the image in this Android device's storage
		String path = directory + File.separator + filenames[idx];

		// download the image
		imageDownloader.download(path, imageView, displayWidth);

		// get the file size (in kilobytes)
		File f = new File(path);
		long kiloBytes = f.length() / 1024;

		// get the image dimensions (without allocating memory; see API)
		BitmapFactory.Options o = new BitmapFactory.Options();
		o.inJustDecodeBounds = true;
		try
		{
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);
		} catch (FileNotFoundException e)
		{
			Log.i("MYDEBUG", "FileNotFoundException e=" + e.toString());
		}

		// output the image file position, count, size, and dimensions
		String s = String.format(Locale.CANADA, "%s (%d of %d, %d KB, %d x %d)", filenames[idx], (idx + 1),
		filenames.length, kiloBytes, o.outWidth, o.outHeight);
		textView.setText(s);

		setDefaults();
		imageView.animate().alpha(1f); // fade in
		imageView.invalidate();
	}

	// set and apply a variety of defaults to get things started off on the right foot
	private void setDefaults()
	{
		zoomMode = false;
		scaleFactor = 1f;
		lastScaleFactor = 1f;
		positionX = 0;
		positionY = 0;
		;
		xRatio = 0.5f;
		yRatio = 0.5f;
		imageView.setScaleX(scaleFactor);
		imageView.setScaleY(scaleFactor);
		imageView.setTranslationX(0);
		imageView.setTranslationY(0);
		imageView.setAlpha(0f); // start as invisible and then fade in (see displayImage)
	}

	// The next two methods ensure the same image is displayed when the device orientation changes.
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putInt("position", index);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		index = savedInstanceState.getInt("position");
		displayImage(index);
	}

	@Override
	public boolean onTouch(View v, MotionEvent me)
	{
		// Let the ScaleGestureDetector inspect all events for pinch/unpinch gestures
		scaleGestureDetector.onTouchEvent(me);

		// ... and check as well to see if it's a fling or double tap
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

				// Save the ID of this pointer
				activePointerId = me.getPointerId(0);

				lastTouchX = x;
				lastTouchY = y;

				break;
			}

			// --------------------------
			case MotionEvent.ACTION_MOVE:
			{
				if (activePointerId != INVALID_POINTER_ID)
				{
					// Find the index of the active pointer and fetch its position
					final int pointerIndex = me.findPointerIndex(activePointerId);
					final float x = me.getX(pointerIndex);
					final float y = me.getY(pointerIndex);

					// Only move if the ScaleGestureDetector isn't processing a gesture.
					if (!scaleGestureDetector.isInProgress())
					{
						// compute image position delta
						final float dx = x - lastTouchX;
						final float dy = y - lastTouchY;

						// apply the position horizontal delta (and maybe the vertical position
						// delta)
						positionX += dx;
						if (zoomMode)
							positionY += dy;

						// give the position deltas to the image instance
						imageView.setTranslationX(positionX);
						imageView.setTranslationY(positionY);

						// do it!
						imageView.invalidate();
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
				// invalidate the pointer ID
				activePointerId = INVALID_POINTER_ID;
				if (!zoomMode)
				{
					positionX = 0f;
					positionY = 0f;
					scaleFactor = 1f;
					lastScaleFactor = 1f;
					imageView.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationX(positionX).translationY(
							positionY);
				}
				break;
			}

			// -----------------------------
			case MotionEvent.ACTION_CANCEL:
			{
				// invalidate the pointer ID
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
					// This is the active pointer going up. Choose a new active pointer and adjust
					// accordingly.
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
	 * Use this instead of getHitRect. Returns correct coordinates!
	 * 
	 * The returned rectangle bounds the view AS IT IS RENDERED ON THE SCREEN. The coordinates are
	 * those of the parent view. The calculations accommodate the view's position (left, top, right,
	 * bottom), scale, translation, and pivot.
	 */
	public Rect getViewRectangle(ImageView view)
	{
		Rect r = new Rect();
		final float pivotX = view.getPivotX();
		final float pivotY = view.getPivotY();
		final float scaleX = view.getScaleX();
		final float scaleY = view.getScaleY();
		final float left = view.getLeft();
		final float top = view.getTop();
		final float right = view.getRight();
		final float bottom = view.getBottom();
		final float translationX = view.getTranslationX();
		final float translationY = view.getTranslationY();
		r.left = (int)(pivotX - (pivotX - left) * scaleX + translationX + 0.5f);
		r.top = (int)(pivotY - (pivotY - top) * scaleY + translationY + 0.5f);
		r.right = (int)(pivotX + (right - pivotX) * scaleX + translationX + 0.5f);
		r.bottom = (int)(pivotY + (bottom - pivotY) * scaleY + translationY + 0.5f);
		return r;
	}

	// ====================
	// Inner classes at end
	// ====================

	// ===========================================================================================================
	// detector for two-finger scale gestures (pinch, unpinch)
	private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener
	{
		float lastFocusX, lastFocusY;

		@Override
		public boolean onScale(ScaleGestureDetector detector)
		{
			zoomMode = true;
			scaleFactor *= detector.getScaleFactor();

			// Don't let the object get too small or too large.
			scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 40.0f));

			// prepare to scale the image (NOTE: scaling occurs about the center of the view -- the
			// pivot point)
			imageView.setScaleX(scaleFactor);
			imageView.setScaleY(scaleFactor);

			// determine the change in scale since the last execution of onScale
			float scaleFactorChange = scaleFactor - lastScaleFactor;
			lastScaleFactor = scaleFactor;

			// compute the pixel change in the size of the view, based on the amount of scaling
			float pixelChangeX = scaleFactorChange * imageView.getWidth();
			float pixelChangeY = scaleFactorChange * imageView.getHeight();

			// If the focus point moves, move the image too.
			float focusX = detector.getFocusX();
			float focusY = detector.getFocusY();
			float dx = focusX - lastFocusX;
			float dy = focusY - lastFocusY;
			lastFocusX = focusX;
			lastFocusY = focusY;
			positionX += dx;
			positionY += dy;

			/*
			 * This is the tricky part! Compute an additional position delta based on the difference
			 * between the focus point and the pivot point when the scale gesture began (see
			 * onScaleBegin). The goal is to ensure scaling appears centered on the focus point
			 * (event though it is really centered on the pivot point). NOTE: The view's pivot point
			 * is the middle of the view.
			 */
			float focusAdjustX = pixelChangeX * (0.5f - xRatio);
			float focusAdjustY = pixelChangeY * (0.5f - yRatio);
			positionX += focusAdjustX;
			positionY += focusAdjustY;

			// prepare to move the image
			imageView.setTranslationX(positionX);
			imageView.setTranslationY(positionY);

			// do it! (move and scale, that is)
			imageView.invalidate();
			return true;
		}

		/*
		 * Compute xRatio and yRatio. xRatio is the x offset of the focus point (relative to the
		 * left edge of the view) divided by the width of the view. yRatio is the y offset of the
		 * focus point (relative to the top of the view) divided by the height of the view. They are
		 * computed here, at the beginning of the gesture, and used later (see onScale) to ensure
		 * that the focus point remains between the fingers as the gesture proceeds.
		 */
		@Override
		public boolean onScaleBegin(ScaleGestureDetector detector)
		{
			float focusX = detector.getFocusX();
			float focusY = detector.getFocusY();
			lastFocusX = focusX;
			lastFocusY = focusY;

			// get a rectangle that bounds the view (as rendered!)
			Rect r = getViewRectangle(imageView);

			// compute x/y ratios (used in onScale)
			xRatio = (focusX - r.left) / (r.right - r.left);
			yRatio = (focusY - r.top) / (r.bottom - r.top);
			return true;
		}

		@Override
		public void onScaleEnd(ScaleGestureDetector detector)
		{
		}
	}

	// ===========================================================================================================
	// detector for fling or double tap gestures
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
		{
			Log.i("MYDEBUG", "Got here: onFling");
			if (velocityX > 3000) // flick to right
			{
				previousImage();
			} else if (velocityX < -3000) // flick to left
			{
				nextImage();
			}
			return true;
		}

		@Override
		public boolean onDoubleTap(MotionEvent me)
		{
			// toggle between normal (x1) and big (x3)
			if (zoomMode)
			{
				scaleFactor = 1f;
				lastScaleFactor = 1f;
			} else
			{
				scaleFactor = 3f;
				lastScaleFactor = 3f;
			}

			// return image to the home position (if necessary)
			positionX = 0f;
			positionY = 0f;

			// do it! (with feel-good animation)
			imageView.animate().scaleX(scaleFactor).scaleY(scaleFactor).translationX(positionX).translationY(positionY);

			// toggle zoom mode (so next double-tap has the opposite effect)
			zoomMode = !zoomMode;
			return true;
		}

		@Override
		public void onLongPress(MotionEvent me)
		{
			Log.i("MYDEBUG", "Got here: onLongPress");
			// do something interesting for a long press
		}
	}
}
