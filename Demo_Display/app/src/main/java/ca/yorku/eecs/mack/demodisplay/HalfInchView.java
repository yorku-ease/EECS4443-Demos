package ca.yorku.eecs.mack.demodisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/* HalfInchView - A simple view that will be 0.5 x 0.5 inches on any Android device.  
 * 
 * Centered in the view is a dark gray circle with a diameter of 0.5 inches.
 * 
 * Text is drawn in the circle in red.  The text is centered vertically and horizontally and fills 
 * 90% of the width of the circle.
 *
 * @author (c) Scott MacKenzie 2014-2019
 */
public class HalfInchView extends View
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	/*
	 * The number here is in "density independent pixels". Setting this final to 80 and giving it to
	 * setMeasuredDimension (called in onMeasure) will make the View half an inch wide and half an
	 * inch high -- provided the device has display density = 1. This is the default for devices
	 * with a display pixel density of 160 dpi (dots per inch). To get a half-inch View on devices
	 * with a different display pixel density, we multiple by a scaling factor (see below).
	 */
	final static int HALF_INCH = 80; // density independent pixels

	// A scaling factor to adjust the measurements in this view.
	float pixelDensity;

	String circleText; // ... to draw in the circle
	Paint circlePaint, textPaint;
	float yTextOffset;

	// Provide three constructors to correspond to each of the three in View.
	public HalfInchView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public HalfInchView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public HalfInchView(Context context)
	{
		super(context);
		initialize(context);
	}

	private void initialize(Context c)
	{
		pixelDensity = c.getResources().getDisplayMetrics().density;
		circlePaint = new Paint();
		circlePaint.setColor(Color.DKGRAY);
		textPaint = new Paint();
		textPaint.setColor(Color.RED);
		textPaint.setTextAlign(Paint.Align.CENTER);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		// draw a half-inch circle in the middle of our half-inch view
		float x = this.getWidth() / 2f;
		float y = this.getHeight() / 2f;
		float radius = (HALF_INCH * pixelDensity) / 2f;
		canvas.drawCircle(x, y, radius, circlePaint);
		canvas.drawText(circleText, x, y + yTextOffset, textPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		// Ensure the view is a half-inch wide and a half-inch high on any device.
		setMeasuredDimension((int)(HALF_INCH * pixelDensity), (int)(HALF_INCH * pixelDensity));
	}

	/*
	 * Give the view some text and adjust the Paint's text size so the text will span 90% of the
	 * width of the view. Also, compute a y-offset (used in onDraw) to center the text vertically
	 * within the view.
	 */
	public void setCircleText(String circleTextArg)
	{
		circleText = circleTextArg;

		// adjust the Paint's text size so the text spans 90% of the circle
		float textWidth = textPaint.measureText(circleText);
		float availableWidth = 0.90f * HALF_INCH * pixelDensity;
		float textSize = textPaint.getTextSize();
		float newTextSize = textSize * (availableWidth / textWidth);
		textPaint.setTextSize(newTextSize);

		/*
		 * Compute a y-offset so the vertical position is centered in the circle.
		 * 
		 * Note: Although the computation of y-offset is intuitively simple (see below), there are a
		 * few idiosyncrasies with getTextBounds that cause the y-offset to be out by a few pixels.
		 * For additional discussion, see StackOverflow at...
		 * 
		 * http://stackoverflow.com/questions/5714600/gettextbounds-in-android
		 */
		Rect bounds = new Rect();
		textPaint.getTextBounds(circleText, 0, circleText.length(), bounds);
		yTextOffset = bounds.height() / 2f;
	}
}