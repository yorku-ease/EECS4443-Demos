package ca.yorku.eecs.mack.demoink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LineMeter extends View
{	
	int width, height;
	Paint lineFillPaint, lineEmptyPaint;
	RectF lengthRect, extraRect;
	float totalLength, lineLength;
	
	public LineMeter(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initialize();
	}
	
	private void initialize()
	{
		lineFillPaint = new Paint(); 
		lineFillPaint.setColor(Color.WHITE);
		lineFillPaint.setStyle(Paint.Style.FILL);
		
		lineEmptyPaint = new Paint(); 
		lineEmptyPaint.setColor(Color.WHITE);
		lineEmptyPaint.setStyle(Paint.Style.STROKE);
		lineEmptyPaint.setStrokeWidth(2);
		
		totalLength = 0f;
		lineLength = 0f;
		
		lengthRect = new RectF();
		extraRect = new RectF();
		setLineLength(10, 100);
	}

	/**
	 * Measure the view and its content to determine the measured width and the measured height.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(width, height);
	}
	
	public void setLineLength(float lengthArg, float maxArg)
	{
		lineLength = lengthArg < maxArg ? totalLength * lengthArg / maxArg : totalLength;
		lengthRect.top = 0.00f * height;
		lengthRect.left = 0.00f * width;
		lengthRect.right = lengthRect.left + lineLength;
		lengthRect.bottom = 1.00f * height;
		
		extraRect.top = lengthRect.top;
		extraRect.left = lengthRect.right;
		extraRect.right = 1.00f * width;
		extraRect.bottom = lengthRect.bottom;
		invalidate();
	}
	
	public void setSize(int widthArg, int heightArg)
	{
		width = widthArg;
		height = heightArg;
		totalLength = 1.00f * width;		
	}
	
	protected void onDraw(Canvas c)
	{
		super.onDraw(c);
		c.drawRect(lengthRect, lineFillPaint);
		c.drawRect(extraRect, lineEmptyPaint);
	}
}
