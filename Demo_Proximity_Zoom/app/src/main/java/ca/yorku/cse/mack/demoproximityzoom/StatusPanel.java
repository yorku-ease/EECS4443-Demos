package ca.yorku.cse.mack.demoproximityzoom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StatusPanel extends View
{
	final float TEXT_SIZE = 12f;
	final int OFFSET = 5;
	final float FIELD_WIDTH = 100f;

	int x, y, w, h;
	float imageScale;
	float pixelDensity; // pixel density of the display (to control text size)
	Paint p;
	float textSize = TEXT_SIZE;
	float margin = OFFSET;

	// Should provide three constructors to correspond to each of the three in View.
	public StatusPanel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public StatusPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public StatusPanel(Context context)
	{
		super(context);
		initialize(context);
	}

	public void initialize(Context c)
	{
		// get the pixel density for the device's display
		pixelDensity = c.getResources().getDisplayMetrics().density;
		
		textSize = TEXT_SIZE * pixelDensity;
		margin = OFFSET * pixelDensity;
		p = new Paint();
		p.setColor(Color.WHITE);
		p.setAntiAlias(true);		
		p.setTextSize(textSize);
	}

	public void update(int xArg, int yArg, int widthArg, int heightArg, float scaleArg)
	{
		x = xArg;
		y = yArg;
		w = widthArg;
		h = heightArg;
		imageScale = scaleArg;
		invalidate();
	}

	protected void onDraw(Canvas canvas)
	{
		final float fieldWidth = (this.getWidth() / 2);
		canvas.drawText("x = " + x, margin + 0 * fieldWidth, 1 * (textSize + textSize / 4), p);
		canvas.drawText("w = " + w, margin + 1 * fieldWidth, 1 * (textSize + textSize / 4), p);
		canvas.drawText("y = " + y, margin + 0 * fieldWidth, 2 * (textSize + textSize / 4), p);
		canvas.drawText("h = " + h, margin + 1 * fieldWidth, 2 * (textSize + textSize / 4), p);
		canvas.drawText("scale = " + trim(imageScale, 2), margin + 0 * fieldWidth, 3 * (textSize + textSize / 4), p);
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int widthMeasureSpec)
	{
		return widthMeasureSpec;
	}

	private int measureHeight(int heightMeasureSpec)
	{
		return heightMeasureSpec;
	}

	// trim and round a float to the specified number of decimal places
	private float trim(float f, int decimalPlaces)
	{
		return (int)(f * 10 * decimalPlaces + 0.5f) / (float)(10 * decimalPlaces);
	}
}
