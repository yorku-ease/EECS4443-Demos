package ca.yorku.eecs.mack.demomultitouch;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

public class PaintPanel extends View
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static float DEFAULT_RADIUS = 50f; 

	/*
	 * Use ten colors, sequenced according to the colour codes for electronic resistors. See...
	 * 
	 * http://en.wikipedia.org/wiki/Electronic_color_code
	 */
	private final int[] inkColor = { Color.BLACK, // black (0)
			0xff663300, // brown (1)
			Color.RED, // red (2)
			0xffff9966, // orange (3)
			Color.YELLOW, // yellow (4)
			Color.GREEN, // green (5)
			Color.BLUE, // blue (6)
			0xff7f007f, // violet (7)
			Color.GRAY, // gray (8)
			Color.WHITE, // white (9)
	};
	private Paint p;
	private PointF[] touchPoints;
	float radius, pixelDensity;
	StringBuilder sb;

	// Should provide three constructors to correspond to each of the three in View.
	public PaintPanel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public PaintPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public PaintPanel(Context context)
	{
		super(context);
		initialize(context);
	}

	private void initialize(Context c)
	{
		this.setBackgroundColor(0xffffafb0); // AARRGGBB: opacity, red, green, blue
		p = new Paint();
		
		pixelDensity = c.getResources().getDisplayMetrics().density;
		radius = DEFAULT_RADIUS * pixelDensity;
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		if (touchPoints == null)
			return; // no arrays until first touch

		for (int i = 0; i < touchPoints.length; ++i)
		{
			if (touchPoints[i].x >= 0.0f)
			{
				p.setColor(inkColor[i]);
				canvas.drawCircle(touchPoints[i].x, touchPoints[i].y, radius, p);
			}
		}
		Log.i(MYDEBUG, sb.toString()); // output a string indicating the number of touch points
	}

	public void drawCircles(PointF[] pointsArg)
	{
		touchPoints = pointsArg;

		/*
		 * Build a string to output to the LogCat window. The string indicates the number of touch
		 * points (e.g., "123" means three fingers/pointers are touching the display surface).
		 */
		sb = new StringBuilder();
		for (int i = 0, j = 1; i < touchPoints.length; ++i)
		{
			if (touchPoints[i].x >= 0.0f)
			{
				sb.append(j);
				++j;
			}
		}
		this.invalidate();
	}

	public void clear()
	{
		touchPoints = null;
		this.invalidate(); // repaints
	}	
}
