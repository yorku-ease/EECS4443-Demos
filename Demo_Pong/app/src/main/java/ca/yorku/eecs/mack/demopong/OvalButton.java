package ca.yorku.eecs.mack.demopong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class OvalButton extends View
{
	//final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static float DEFAULT_STROKE_WIDTH = 3;

	final static int STATE_UP = 100;
	final static int STATE_DOWN = 200;

	Paint fillUpPaint, fillDownPaint, linePaint, textPaint, secondaryTextPaint;
	int width, height;
	int buttonState;
	float yOffset, yOffsetSecondary; // for positioning text in button
	float pixelDensity, strokeWidth;
	int color;
	String text, secondaryText;
	RectF r;

	/*
	 * The reference to the listener is initialized in the host activity when it executes
	 * "setOnOvalButtonClickListener", passing "this" (a reference to the host activity that
	 * implements the listener).
	 */
	OnOvalButtonClickListener onOvalButtonClickListener;

	public OvalButton(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize();
	}

	public OvalButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	public OvalButton(Context context)
	{
		super(context);
		initialize();
	}

	public void initialize()
	{
		pixelDensity = 1f; // default
		strokeWidth = DEFAULT_STROKE_WIDTH;

		fillUpPaint = new Paint();
		fillUpPaint.setStyle(Paint.Style.FILL);

		fillDownPaint = new Paint();
		fillDownPaint.setStyle(Paint.Style.FILL);

		linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.CENTER);

		secondaryTextPaint = new Paint();
		secondaryTextPaint.setAntiAlias(true);
		secondaryTextPaint.setTextAlign(Paint.Align.CENTER);

		buttonState = STATE_UP;

		r = new RectF();
	}

	// called from the main activity to configure some button properties
	public void configure(float pixelDensityArg, int colorArg, String textArg, String secondaryTextArg)
	{
		pixelDensity = pixelDensityArg;
		color = colorArg;
		text = textArg;
		secondaryText = secondaryTextArg;

		strokeWidth *= pixelDensity;
		linePaint.setStrokeWidth(strokeWidth);

		fillUpPaint.setColor(0x44000000 | color); // semi-transparent
		linePaint.setColor(0xff000000 | color); // solid
		textPaint.setColor(0xff000000 | color); // solid
		secondaryTextPaint.setColor(0xff000000 | color); // solid

		// create a darker version of the color for button-down
		final int redDark = (int)((color & 0xff0000) / 2f + 0.5f);
		final int greenDark = (int)((color & 0x00ff00) / 2f + 0.5f);
		final int blueDark = (int)((color & 0x0000ff) / 2f + 0.5f);
		fillDownPaint.setColor(0x44000000 + redDark + greenDark + blueDark);
	}

	/*
	 * This is the definition of the click listener. It contains a single abstract method that the
	 * host activity must implement. Of course, the host activity will include
	 * "implements OvalButton.OnOvalButtonClickListener" in its signature. The method defined below
	 * executes in the host activity as a "callback". It is a callback because it is called from
	 * here (see onTouchEvent) where we send a reference to the button "back" to the calling
	 * activity.
	 */
	public interface OnOvalButtonClickListener
	{
		 void onOvalButtonClick(OvalButton rb);
	}

	/*
	 * This method is called from an activity that uses the OvalButton. The effect is to attach a
	 * listener to the button. When we detect that the button is clicked (see onTouchEvent, below),
	 * we'll send a reference to the button ("this") back to the calling activity. If the calling
	 * activity uses >1 OvalButton, the reference is used to determine which OvalButton was clicked.
	 */
	public void setOnOvalButtonClickListener(OnOvalButtonClickListener onOvalButtonClickListenerArg)
	{
		onOvalButtonClickListener = onOvalButtonClickListenerArg;
	}

	/*
	 * The view is now visible so its width and height are available.
	 * 
	 * Do nothing if hasFocus is false. Otherwise, initialize the width and height fields and a few
	 * other things that need width and height.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		if (!hasFocus)
			return;

		width = this.getWidth() - (int)(2f * strokeWidth + 0.5f);
		height = this.getHeight() - (int)(2f * strokeWidth + 0.5f);

		/*
		 * Initialize the rectangle that bounds the view. The first two arguments (left, right) are
		 * adjusted to ensure the stroke is not cropped at the edges. See also Demo_Display.
		 */
		r.left = strokeWidth;
		r.top = r.left;
		r.right = width;
		r.bottom = height;

		// adjust the text width to span 80% of the width of the oval
		final float availableWidth = 0.80f * width;
		final float textWidth = textPaint.measureText(text);
		final float currentFontSize = textPaint.getTextSize();
		final float newFontSize = currentFontSize * (availableWidth / textWidth);
		textPaint.setTextSize(newFontSize);

		// further adjustments are needed if there is secondary text
		if (secondaryText != null)
		{
			textPaint.setTextSize(newFontSize * 0.666f);
			secondaryTextPaint.setTextSize(newFontSize * 0.3333f);
		}

		// compute y-offsets
		final Rect bounds = new Rect();
		textPaint.getTextBounds("A", 0, 1, bounds);
		if (secondaryText == null)
			yOffset = bounds.height() * 0.50f;
		else
			yOffset = bounds.height() * 0.20f;

		secondaryTextPaint.getTextBounds("A", 0, 1, bounds);
		yOffsetSecondary = yOffset + 1.5f * bounds.height();
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		if (buttonState == STATE_UP)
			canvas.drawOval(r, fillUpPaint);
		else
			canvas.drawOval(r, fillDownPaint);
		canvas.drawOval(r, linePaint);
		canvas.drawText(text, width / 2f, height / 2f + yOffset, textPaint);
		if (secondaryText != null)
			canvas.drawText(secondaryText, width / 2f, height / 2f + yOffsetSecondary, secondaryTextPaint);
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

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		switch (me.getAction() & MotionEvent.ACTION_MASK)
		{
			case MotionEvent.ACTION_DOWN:
				if (ovalContains(width / 2f, height / 2f, width / 2f, height / 2f, me.getX(), me.getY()))
					buttonState = STATE_DOWN;
				break;

			case MotionEvent.ACTION_UP:
				buttonState = STATE_UP;
				if (ovalContains(width / 2f, height / 2f, width / 2f, height / 2f, me.getX(), me.getY()))
				{
					/*
					 * The button is clicked! Invoke the callback in the activity that is using the
					 * button -- and send it a reference to "this" button.
					 */
					onOvalButtonClickListener.onOvalButtonClick(this);
				}
		}
		this.invalidate();
		return true;
	}

	/*
	 * Since the button is oval, we cannot use the contains method of the RectF class to determine
	 * if the touch point is inside the button. Instead, use the standard calculation to determine
	 * if a point is inside an ellipse. See...
	 * 
	 * http://math.stackexchange.com/questions/76457/check-if-a-point-is-within-an-ellipse
	 * 
	 * The first four parameters define the oval. The last two are the point to test.
	 */
	public boolean ovalContains(float xCenter, float yCenter, float xRadius, float yRadius, float x, float y)
	{
		return (((x - xCenter) * (x - xCenter)) / (xRadius * xRadius) + ((y - yCenter) * (y - yCenter))
				/ (yRadius * yRadius) <= 1f);
	}
}
