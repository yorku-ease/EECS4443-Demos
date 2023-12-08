package ca.yorku.cse.mack.demoh4touch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class StrokePreview extends View
{
	Paint p;
	
	public StrokePreview(Context context, AttributeSet attrs) 
	{
		super(context, attrs);
		init();
	}

	private void init() 
	{
		p = new Paint();
		p.setColor(Color.BLUE);
		p.setStrokeCap(Paint.Cap.ROUND);
	}
	
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		float height = this.getHeight();
		float width = this.getWidth();
		final float INSET = 10f;
		canvas.drawLine(INSET, height / 2f, width - INSET, height / 2f, p);		
	}
	
	public void setStrokePreviewColor(int colorArg)
	{
		p.setColor(colorArg);
		invalidate(); // repaint
	}	
	
	public void setStrokePreviewWidth(float strokeWidthArg)
	{
		p.setStrokeWidth(strokeWidthArg);
		invalidate(); // repaint
	}
}
