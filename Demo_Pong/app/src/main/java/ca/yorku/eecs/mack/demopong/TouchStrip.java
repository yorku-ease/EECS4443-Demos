package ca.yorku.eecs.mack.demopong;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
 
public class TouchStrip extends View
{
	//final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static float DEFAULT_STROKE_WIDTH = 2;
	final static float CORNER_RADIUS = 16f;
	final static int TOUCH_POINT_WIDTH = 240; // 1.5 inches (mostly outside the view)

	Paint fillPaint, linePaint, centerLinePaint;
	RectF touchStrip, centerLine;
	Bitmap touchContactPoint;
	float pixelDensity, strokeWidth;
	float corderRadius;
	int touchPointWidth, touchPointHeight;
	float xTouch, yTouch;
	boolean fingerOnStrip;

	public TouchStrip(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public TouchStrip(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public TouchStrip(Context context)
	{
		super(context);
		initialize(context);
	}

	public void initialize(Context c)
	{
		// scale some variables, based on the display's pixel density
		pixelDensity = c.getResources().getDisplayMetrics().density;
		corderRadius = CORNER_RADIUS * pixelDensity;
		touchPointWidth = (int)(TOUCH_POINT_WIDTH * pixelDensity);
		touchPointHeight = touchPointWidth / 3;
		strokeWidth = DEFAULT_STROKE_WIDTH * pixelDensity;

		fillPaint = new Paint();
		fillPaint.setStyle(Paint.Style.FILL);

		linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setStrokeWidth(strokeWidth);

		centerLinePaint = new Paint();
		centerLinePaint.setStyle(Paint.Style.FILL);

		/*
		 * Initialize the touch strip rectangle and the center line rectangle. Note that the view's
		 * width and height are not yet available, so we cannot initialize the coordinates for the
		 * rectangle. This is done in onWindowFocusChanged.
		 */
		touchStrip = new RectF();
		centerLine = new RectF();

		// initialize the feathered oval that appears at the touch contact point.
		touchContactPoint = getFeatheredTouchPoint(touchPointWidth, touchPointHeight);

		fillPaint.setColor(0x44999999); // semi-transparent, grey
		linePaint.setColor(0x44000000); // semi-transparent, black
		centerLinePaint.setColor(0x44000000); // semi-transparent, black

		fingerOnStrip = false;
	}

	/*
	 * The view is now visible so its width and height are available.
	 * 
	 * Do nothing if hasFocus is false. Otherwise, initialize the coordinates for the tough strip
	 * rectangle.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		if (!hasFocus)
			return;

		touchStrip.left = 0f + strokeWidth;
		touchStrip.top = 0f + strokeWidth;
		touchStrip.right = this.getWidth() - strokeWidth;
		touchStrip.bottom = this.getHeight() - strokeWidth;

		centerLine.left = 0f + strokeWidth;
		centerLine.top = this.getHeight() / 2f - 4f * pixelDensity;
		centerLine.right = this.getWidth() - strokeWidth;
		centerLine.bottom = this.getHeight() / 2f + 4f * pixelDensity;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		// draw the touch strip fill
		canvas.drawRoundRect(touchStrip, corderRadius, corderRadius, fillPaint);

		// draw the finger touch point (if the finger is on the strip)
		if (fingerOnStrip)
			canvas.drawBitmap(touchContactPoint, xTouch - touchPointWidth / 2, yTouch - touchPointHeight / 2, null);

		// draw the touch strip line (draw last to create a clean edge to the strip)
		canvas.drawRoundRect(touchStrip, corderRadius, corderRadius, linePaint);
		canvas.drawRect(centerLine, centerLinePaint);
	}

	/*
	 * Create a feathered oval to display at the touch contact point. This is based on a
	 * StackOverflow posting. See...
	 * 
	 * http://stackoverflow.com/questions/9954395/how-to-achieve-feathering-effect-in-android
	 */
	private Bitmap getFeatheredTouchPoint(int widthArg, int heightArg)
	{
		// Determine largest dimension, use for rectangle.
		int size = Math.max(widthArg, heightArg);

		RadialGradient gradient = new RadialGradient(size / 2, size / 2, size / 2,
				new int[] { 0xFF999999, 0x00999999 }, new float[] { 0.0f, 1.0f },
				android.graphics.Shader.TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(gradient);

		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);
		return Bitmap.createScaledBitmap(bitmap, widthArg, heightArg, true);
	}

	/*
	 * Touch events are first processed in the main activity, where the location of the touch point
	 * relative to the top and bottom of the touch strip is determined. That information is passed
	 * on to the PongView for adjusting the position of the paddle. The onTouch method in the main
	 * activity returns "false", implying that the event is not fully consumed. Execution continues
	 * here, where we deal the visual presentation of the touch contact point on the touch strip.
	 */
	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		// get the location of the touch contact point
		xTouch = me.getX();
		yTouch = me.getY();

		// set or clear the finger-on-strip boolean
		switch (me.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				fingerOnStrip = true;
				break;
			case MotionEvent.ACTION_UP:
				fingerOnStrip = false;
				break;
			case MotionEvent.ACTION_CANCEL:
				fingerOnStrip = false;
		}
		this.invalidate();
		return true;
	}

	/*
	 * onMeasure - Since this is a custom View, we must override onMeasure.
	 * 
	 * The code here is a close approximation to that found in LabelView.java (the Android sample
	 * API provided for custom views). See also...
	 * 
	 * http://developer.android.com/guide/topics/ui/custom-components.html#custom
	 * 
	 * The approach here is to assume (hope!) that exact values are available via the XML layout
	 * files.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec)
	{
		int result = 100; // fall-back value (so something appears!)
		int specMode = View.MeasureSpec.getMode(measureSpec);
		int specSize = View.MeasureSpec.getSize(measureSpec);

		if (specMode == View.MeasureSpec.EXACTLY) // We were told how big to be (return that)
			result = specSize; // the value computed based on XML layout

		else if (specMode == View.MeasureSpec.AT_MOST)// respect AT_MOST if that's the spec
			result = Math.min(result, specSize);

		return result;
	}

	private int measureHeight(int measureSpec)
	{
		int result = 100; // fall-back value (so something appears!)
		int specMode = View.MeasureSpec.getMode(measureSpec);
		int specSize = View.MeasureSpec.getSize(measureSpec);

		if (specMode == View.MeasureSpec.EXACTLY) // We were told how big to be (return that)
			result = specSize; // the value computed based on the XML layout

		else if (specMode == View.MeasureSpec.AT_MOST) // respect AT_MOST if that's the spec
			result = Math.min(result, specSize);

		return result;
	}
}
