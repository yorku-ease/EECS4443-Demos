
package ca.yorku.eecs.mack.demosensors;

import java.util.Locale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ViewMeter - implementation of a View to display a value on a linear indicator
 * 
 * @author (c) Scott MacKenzie 2011-2014
 * 
 */
@SuppressWarnings("unused")
public class ViewMeter extends View
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
	
	final float DEFAULT_WIDTH = 80f;
	final float DEFAULT_HEIGHT = 250f;
	final float DEFAULT_MIN = 0f;
	final float DEFAULT_MAX = 10f;
	final int DEFAULT_NUMBER_OF_DIVISIONS = 10;

	final int BAR_BACKGROUND_COLOR = 0xffa0a0ff;
	final int BAR_COLOR = 0xff000080;
	final int LABEL_COLOR = Color.RED;
	final int BORDER_COLOR = Color.BLACK;

	float width;
	float height;
	float insetHorizontal, insetVertical;
	float barWidth;
	float barHeight;

	public String label, minText, maxText;
	float min, max, value;
	int numberOfDivisions;
	boolean valueOK;
	float outOfRangeValue;
	Paint labelPaint, fillPaint, blankPaint, borderPaint, markerPaint, barBorderPaint,
			labelMinPaint, labelMaxPaint, valuePaint;

	public ViewMeter(Context context)
	{
		super(context);
		initialize();
	}

	public ViewMeter(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	public ViewMeter(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs);
		initialize();
	}

	private void initialize()
	{
		labelPaint = new Paint();
		labelPaint.setColor(Color.BLACK);
		labelPaint.setAntiAlias(true);
		labelPaint.setTextAlign(Paint.Align.CENTER);

		labelMinPaint = new Paint();
		labelMinPaint.setColor(Color.BLACK);
		labelMinPaint.setAntiAlias(true);
		labelMinPaint.setTextAlign(Paint.Align.RIGHT);

		labelMaxPaint = new Paint();
		labelMaxPaint.setColor(Color.BLACK);
		labelMaxPaint.setAntiAlias(true);
		labelMaxPaint.setTextAlign(Paint.Align.RIGHT);

		valuePaint = new Paint();
		valuePaint.setAntiAlias(true);
		valuePaint.setTextAlign(Paint.Align.CENTER);

		markerPaint = new Paint();
		markerPaint.setColor(BAR_COLOR);
		markerPaint.setAntiAlias(true);

		fillPaint = new Paint();
		fillPaint.setColor(BAR_COLOR);
		fillPaint.setStyle(Paint.Style.FILL);

		blankPaint = new Paint();
		blankPaint.setColor(BAR_BACKGROUND_COLOR);
		blankPaint.setStyle(Paint.Style.FILL);

		borderPaint = new Paint();
		borderPaint.setColor(Color.BLACK);
		borderPaint.setStyle(Paint.Style.STROKE);

		barBorderPaint = new Paint();
		barBorderPaint.setColor(BAR_COLOR);
		barBorderPaint.setStyle(Paint.Style.STROKE);

		setMinMax(DEFAULT_MIN, DEFAULT_MAX);
		numberOfDivisions = DEFAULT_NUMBER_OF_DIVISIONS;

		this.setBackgroundColor(Color.LTGRAY);
	}

	/*
	 * Update the value for this view meter. Returns a boolean which is true if the value is in
	 * range for this view meter, false otherwise.
	 */
	public boolean updateMeter(float valueArg)
	{
		if (valueArg < min)
		{
			value = min;
			valueOK = false;
			outOfRangeValue = valueArg;
		} else if (valueArg > max)
		{
			value = max;
			valueOK = false;
			outOfRangeValue = valueArg;
		} else
		{
			value = valueArg;
			valueOK = true;
		}
		invalidate(); // repaint
		return valueOK;
	}

	// set the label for this view meter
	public void setLabel(String labelArg)
	{
		label = labelArg;
		float textWidth = labelPaint.measureText(label);
		if (textWidth > 0.8f * width)
			labelPaint.setTextScaleX(0.8f * width / textWidth);
	}

	// set the min and max values for this view meter
	public void setMinMax(float minArg, float maxArg)
	{
		min = minArg;
		max = maxArg;
		minText = "" + min;
		maxText = "" + max;

		String sampleText = "0000";
		if (minText.length() > sampleText.length())
			sampleText = minText;
		if (maxText.length() > sampleText.length())
			sampleText = maxText;

		float scale = (0.8f * insetHorizontal) / labelMinPaint.measureText(sampleText);
		labelMinPaint.setTextScaleX(scale);
		labelMaxPaint.setTextScaleX(scale);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);

		// draw border
		canvas.drawRect(0, 0, width - 1, height - 1, borderPaint);

		if (label == null || label.equals(" "))
			return; // meter not in use

		float valueHeight = (value - min) / (max - min) * barHeight;

		// draw blank segment of bar
		canvas.drawRect(insetHorizontal, insetVertical, insetHorizontal + barWidth, insetVertical
				+ (barHeight - valueHeight), blankPaint);

		// draw fill segment of bar
		canvas.drawRect(insetHorizontal, insetVertical + (barHeight - valueHeight), insetHorizontal
				+ barWidth, insetVertical + barHeight, fillPaint);

		// draw bar border
		canvas.drawRect(insetHorizontal, insetVertical, insetHorizontal + barWidth, insetVertical
				+ barHeight, barBorderPaint);

		// draw markers
		final float markerLength = insetVertical / 10f;
		for (int i = 0; i < numberOfDivisions + 1; ++i)
		{
			float dy = i * (barHeight / numberOfDivisions);
			canvas.drawLine(insetHorizontal - markerLength, insetVertical + dy, insetHorizontal,
					insetVertical + dy, markerPaint);
		}

		// draw text for min/max values by top/bottom markers
		canvas.drawText(minText, 0.85f * insetHorizontal, insetVertical + barHeight
				- labelMinPaint.ascent() / 3f, labelMinPaint);
		canvas.drawText(maxText, 0.85f * insetHorizontal, insetVertical - labelMaxPaint.ascent()
				/ 3f, labelMaxPaint);

		// draw text for meter label (above bar)
		canvas.drawText(label, width / 2, insetVertical / 2 - labelPaint.ascent() / 3f, labelPaint);

		// draw text for value (below bar)
		String valueString;
		if (valueOK)
		{
			valueString = String.format(Locale.CANADA, "%.2f", value);
			valuePaint.setColor(Color.BLACK);
		} else
		{
			valueString = String.format(Locale.CANADA, "%.2f", outOfRangeValue);
			valuePaint.setColor(Color.RED);
		}

		// draw text for the sensor value (below bar)
		canvas.drawText(valueString, width / 2f, insetVertical + barHeight + insetVertical
				/ 2f - valuePaint.ascent() / 2f, valuePaint);
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

		width = this.getWidth();
		height = this.getHeight();
		insetHorizontal = 0.4f * width;
		barWidth = 0.2f * width;
		insetVertical = 0.15f * height;
		barHeight = 0.7f * height;
		labelPaint.setTextSize(0.5f * insetHorizontal);

		// compute a scale factor so that eight characters fit in 80% of the width
		float scale = (0.8f * width) / labelMinPaint.measureText("00000000");

		// adjust the paint object's text scale property accordingly
		valuePaint.setTextScaleX(scale);
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
