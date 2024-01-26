package ca.yorku.eecs.mack.demogridview;

import java.io.File;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.widget.ImageView;

public class DirectoryImageView extends ImageView
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	int width, height;
	RectF rect;
	Paint textPaint, rectPaint;
	String text;

	public DirectoryImageView(Context c)
	{
		super(c);
		textPaint = new Paint();
		textPaint.setTextAlign(Paint.Align.CENTER);
		rectPaint = new Paint();
		rectPaint.setColor(0xdd888888);
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawRect(rect, rectPaint);
		canvas.drawText(text, (float) width / 2, 0.95f * height, textPaint);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec)
	{
		return width;
	}

	private int measureHeight(int measureSpec)
	{
		return height;
	}

	public void setSize(int wArg, int hArg)
	{
		width = wArg;
		height = hArg;
		rect = new RectF(0, 0.8f * height, width, height);
		textPaint.setTextSize(0.1f * height);
	}

	public void setText(String textArg)
	{
		String[] s = textArg.split(File.separator);
		text = s[s.length - 1];

		// adjust the Paint's text size so the text spans no more than 90% of the view
		final float textWidth = textPaint.measureText(text);
		final float availableWidth = 0.90f * width;
		final float textSize = textPaint.getTextSize();
		if (textWidth > availableWidth)
			textPaint.setTextSize(textSize * (availableWidth / textWidth));
	}
}
