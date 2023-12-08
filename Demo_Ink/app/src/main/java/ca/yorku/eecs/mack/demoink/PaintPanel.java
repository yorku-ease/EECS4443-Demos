package ca.yorku.eecs.mack.demoink;

import java.io.Serializable;
import java.util.Iterator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

// =========================================================================================
/*
 * Inner class for a paint panel to receive finger touches to paint on a canvas.
 * 
 */
public class PaintPanel extends View implements Serializable
{
	private static final int CANVAS_COLOR = 0xffffafb0; // pink

	private Paint p;
	private int inkColor;
	private float strokeWidth = 5; // default

	Canvas offScreenCanvas;
	Bitmap bmp;
	Paint bmpPaint;
	Stroke currentStroke;
	public Sketch sketch;

	// Should provide three constructors to correspond to each of the three in View.
	public PaintPanel(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize();
	}

	public PaintPanel(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	public PaintPanel(Context context)
	{
		super(context);
		initialize();
	}

	@Override
	public boolean performClick()
	{
		super.performClick();
		return true;
	}

	private void initialize()
	{
		// turn off hardware acceleration (View-level)
		// See http://developer.android.com/guide/topics/graphics/hardware-accel.html
		this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

		this.setBackgroundColor(CANVAS_COLOR);
		p = new Paint();
		p.setAntiAlias(true);
		p.setDither(true);
		p.setStrokeJoin(Paint.Join.ROUND);
		p.setStrokeWidth(strokeWidth);
		p.setStyle(Paint.Style.STROKE);

		/*
		 * The following instruction doesn't work on a Google Nexus 4 running 4.0.1 unless
		 * hardware acceleration is turned off. See...
		 * 
		 * http://code.google.com/p/android/issues/detail?id=24873
		 */
		p.setStrokeCap(Paint.Cap.ROUND);

		bmpPaint = new Paint(Paint.DITHER_FLAG);
		currentStroke = new Stroke(p);
		sketch = new Sketch();
	}

	/*
	 * onSizeChanged - The main goal here is to create a bitmap that fills the entire panel. The
	 * bitmap is given to an off-screen canvas into which the sketch is drawn as strokes are
	 * made by the artist.
	 * 
	 * This method is called both when the activity is created and when the screen orientation
	 * changes. In the latter case, we also need to redraw the entire sketch.
	 */
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		bmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
		offScreenCanvas = new Canvas(bmp);
		drawEntireSketch(); // needed if there is a screen orientation change
	}

	@Override
	protected void onDraw(Canvas canvas)
	{
		canvas.drawBitmap(bmp, 0, 0, bmpPaint); // draw bitmap with existing strokes
		drawStroke(canvas, currentStroke); // add current in-progress stroke
	}

	/*
	 * drawStroke - Draw the given stroke into the given canvas. To do this, we iterate through
	 * the lines in the stroke and draw them into the canvas. The canvas is either the canvas
	 * provided through onDraw (into which the current in-progress stroke is drawn) or the
	 * off-screen canvas (into which completed strokes are drawn).
	 */
	private void drawStroke(Canvas canvas, Stroke stroke)
	{
		Iterator<Line> it = stroke.getStrokeSegments().iterator();
		Paint paint = stroke.getStrokePaint();
		while (it.hasNext())
		{
			Line tmp = it.next();
			paint.setStrokeWidth(tmp.strokeWidth);
			canvas.drawLine(tmp.x1, tmp.y1, tmp.x2, tmp.y2, paint);
		}
	}

	/*
	 * removeLastStroke - Remove the last stroke in the ArrayList of strokes. As well, the
	 * entire sketch (minus the stroke that is removed) must be recreated in a fresh bitmap
	 * which fills the off-screen canvas.
	 */
	public void removeLastStroke()
	{
		if (!sketch.strokeArray.isEmpty())
			sketch.strokeArray.remove(sketch.strokeArray.size() - 1);

		bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
		offScreenCanvas = new Canvas(bmp);
		drawEntireSketch();
	}

	/*
	 * drawEntireSketch - Draw the entire sketch. Iterate through the <code>ArrayList</code> of
	 * strokes and draw them into the off-screen canvas.
	 */
	public void drawEntireSketch()
	{
		for (Stroke s : sketch.strokeArray)
			drawStroke(offScreenCanvas, s);
		this.invalidate();
	}

	/*
	 * clear - Clear the entire sketch. Fresh instances of currentStroke and sketch are created.
	 * A new bitmap is created to fill the entire panel. A new off-screen canvas is created
	 * containing the bitmap. Finish by invalidating the panel, forcing a redraw to create an
	 * empty sketch.
	 */
	public void clear()
	{
		currentStroke = new Stroke(p);
		currentStroke.setInkColor(inkColor);
		sketch = new Sketch();
		bmp = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
		offScreenCanvas = new Canvas(bmp);
		this.invalidate(); // repaints
	}

	/*
	 * addLine - Add a stroke segment (i.e., a line) to the current stroke. Parameters are the
	 * beginning (x1,y1) and ending (x2,y2) of the line, the stroke width, and a boolean to
	 * indicate if this the last line in a stroke.
	 * 
	 * If this is the last line in a stroke, add the stroke to the sketch and draw the stroke to
	 * the off-screen canvas and create a new (empty) current stroke.
	 */
	public void addLine(float x1, float y1, float x2, float y2, float strokeWidth, boolean endOfGesture)
	{
		currentStroke.addSegment(new Line(x1, y1, x2, y2, strokeWidth));
		if (endOfGesture)
		{
			sketch.strokeArray.add(currentStroke);
			drawStroke(offScreenCanvas, currentStroke);
			currentStroke = new Stroke(p);
			currentStroke.setInkColor(inkColor);
		}
		this.invalidate();
	}

	/*
	 * setInkColor - will apply to all line segments in the current stroke.
	 */
	public void setInkColor(int inkColorArg)
	{
		inkColor = inkColorArg;
		currentStroke.setInkColor(inkColor);
	}

	/*
	 * setStrokeWidth - can change from one stroke segment to the next (based on finger
	 * pressure)
	 */
	public void setStrokeWidth(float strokeWidthArg)
	{
		strokeWidth = strokeWidthArg;
	}
}