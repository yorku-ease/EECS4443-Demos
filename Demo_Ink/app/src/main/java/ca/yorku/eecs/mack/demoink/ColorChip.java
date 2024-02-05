package ca.yorku.eecs.mack.demoink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * ColorChip - a class, extended from View, to implement a color chip.
 * <p>
 * 
 * A color chip is used in paint stores to provide options for home owners and painters
 * planning to re-finish walls in a home.  Here, a color chip is simple a square or rectangle
 * that can be organized with other color chips to present a range of color options to a user.
 * <p>
 * 
 * @author Scott MacKenzie, 2011-2017
 *
 */
public class ColorChip extends View 
{
	int width, height;
	boolean border;
	Paint borderPaint;
	int chipColor;
	
	public ColorChip(Context context)
	{
		super(context);
		initialize();
	}
	
	public ColorChip(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		initialize();
	}
	
	public void initialize()
	{
		borderPaint = new Paint(); 
		borderPaint.setColor(Color.RED);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(6);
		border = false;
	}
	
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }    
    private int measureWidth(int measureSpec) { return width; }
    private int measureHeight(int measureSpec) { return height; }	
    
	public void setWidth(int wArg) { width = wArg; }
	public void setHeight(int hArg) { height = hArg; }
	
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);		
		if (border)
			canvas.drawRect(3f, 3f, (float)(width - 3), (float)(height - 3), borderPaint);		
	}

	public void touchEffectOn()
	{
		border = true;
		invalidate();
	}
	
	public void touchEffectOff() 
	{
		border = false;
		invalidate();
	}	

	public void setChipColor(int color) { setBackgroundColor(color); }	
	
	@Override
	public void setBackgroundColor(int color)
	{
		super.setBackgroundColor(color);
		chipColor = color;
	}
	
	public int getChipColor() { return getBackgroundColor(); }
	public int getBackgroundColor() { return chipColor; }
}
