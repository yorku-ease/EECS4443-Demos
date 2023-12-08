package ca.yorku.eecs.mack.demopong;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

public class MessageView extends View
{
	//final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static float TEXT_SIZE = 24f;

	int width, height;
	Paint paint;
	float pixelDensity;
	Drawable demopong;

	public MessageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public MessageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public MessageView(Context context)
	{
		super(context);
		initialize(context);
	}

	public void initialize(Context context)
	{
		pixelDensity = 1f;
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextAlign(Paint.Align.CENTER);
		paint.setTextSize(TEXT_SIZE);

		// get ball image
		demopong = context.getResources().getDrawable(R.drawable.demopong);

	}

	/*
	 * The view is now visible so its width and height are available.
	 * 
	 * Do nothing if hasFocus is false. Otherwise, initialize width and height and other things that
	 * depend on these. rectangle.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		if (!hasFocus)
			return;

		width = this.getWidth();
		height = this.getHeight();
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		demopong.setBounds(0, 0, width, height);
		demopong.draw(canvas);
	}
}
