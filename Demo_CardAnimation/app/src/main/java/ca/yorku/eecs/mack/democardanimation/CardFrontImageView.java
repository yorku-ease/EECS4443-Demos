package ca.yorku.eecs.mack.democardanimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.widget.ImageView;

public class CardFrontImageView extends ImageView
{
	//final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	String titleText;
	Paint textPaint;
	Bitmap background;
	Rect bounds; // to measure the text (needed to compute y-offset)
	int viewWidth, viewHeight;
	int backgroundWidth, backgroundHeight;
	float backgroundLeft, backgroundTop;
	float xText, yText, yOffset;
	int gradientSize;

	public CardFrontImageView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize();
	}

	public CardFrontImageView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize();
	}

	public CardFrontImageView(Context context)
	{
		super(context);
		initialize();
	}

	public void initialize()
	{
		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Paint.Align.CENTER);

		bounds = new Rect();
	}

	public void setTitleText(String titleTextArg)
	{
		titleText = titleTextArg;
	}

	/*
	 * The size of the view is now available. Calculate a few things that depend on the width and
	 * height of the view.
	 */
	public void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		viewWidth = w;
		viewHeight = h;

		// calculate the width & height of background (depends on orientation)
		if (viewWidth < viewHeight)
		{
			backgroundWidth = (int)(0.90f * viewWidth + 0.5f); // 90% of view width
			backgroundHeight = (int)(0.15f * viewHeight + 0.5f); // 15% of view height
		} else
		{
			backgroundWidth = (int)(0.90f * viewHeight + 0.5f); // 90% of view height
			backgroundHeight = (int)(0.15f * viewWidth + 0.5f); // 15% of view width
		}

		// calculate the x/y position for drawing the background
		backgroundLeft = (int)(viewWidth / 2f + -.5f) - (int)(backgroundWidth / 2f + 0.5f);
		backgroundTop = (int)(0.2f * viewHeight + 0.5f);

		gradientSize = (int)(0.05f * backgroundWidth + 0.5f);

		// get the background on which to paint the title text
		background = getGradientEdgedRectangle(backgroundWidth, backgroundHeight, gradientSize, 0xddff0000);

		// adjust text size to fit 70% of the background width
		final float textWidth = textPaint.measureText(titleText);
		final float availableWidth = 0.70f * backgroundWidth;
		final float textSize = textPaint.getTextSize();
		final float newTextSize = textSize * (availableWidth / textWidth);
		textPaint.setTextSize(newTextSize);

		xText = viewWidth / 2f;
		yText = (int)(backgroundTop + 0.5f * backgroundHeight + 0.5f); // middle (y-offset needed)

		// compute y-offset so text is vertically centered
		textPaint.getTextBounds("A", 0, 1, bounds);
		yOffset = bounds.height() / 2f;
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas); // draw image

		// draw the background for the title (re-size into background rectangle)
		canvas.drawBitmap(background, backgroundLeft, backgroundTop, null);

		// draw the text
		canvas.drawText(titleText, xText, yText + yOffset, textPaint); // draw title
	}

	/*
	 * Return a rectangular bitmap with a gradient edge. Four parameters are required: the width and
	 * height of the bitmap, the size of the gradient region, and the color.
	 * 
	 * Example: If width = 300, height = 100, and gradientSize = 10, the bitmap will have a central
	 * area that is 280 x 80, rendered in the specified color. A gradient region 10 pixels wide
	 * borders the central region. Within the gradient region the color transitions from the
	 * specified color adjacent to the central region to the same color with alpha = 0 at the edge,
	 * thus creating a fading-out feathered effect.
	 */
	public Bitmap getGradientEdgedRectangle(int width, int height, int gradientSize, int color)
	{
		Paint p = new Paint();

		// create the bitmap to draw into
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		// Middle section (top to bottom)
		int[] colorArray = { color & 0x00ffffff, color, color, color & 0x00ffffff };
		float[] positionArray = { 0f, (float)gradientSize / height, 1f - (float)gradientSize / height, 1f };
		LinearGradient lg = new LinearGradient(width / 2f, 0, width / 2f, height, colorArray, positionArray, Shader.TileMode.CLAMP);
		p.setShader(lg);
		p.setStrokeWidth(width - 2 * gradientSize);
		canvas.drawLine(width / 2f, 0, width / 2f, height, p); // top to bottom

		// Left edge
		int[] colorLeft = { color & 0x00ffffff, color }; // left side
		float[] positionLeft = { 0f, 1f };
		LinearGradient lgLeft = new LinearGradient(0, width / 2f, gradientSize, width / 2f, colorLeft, positionLeft,
				Shader.TileMode.CLAMP);
		p.setShader(lgLeft);
		p.setStrokeWidth(height - 2 * gradientSize);
		canvas.drawLine(0, height / 2f, gradientSize, height / 2f, p);

		// Right edge
		int[] colorRight = { color, color & 0x00ffffff }; // right side
		float[] positionRight = { 0f, 1f };
		LinearGradient lgRight = new LinearGradient(width - gradientSize, height / 2f, width, height / 2f, colorRight, positionRight,
				Shader.TileMode.CLAMP);
		p.setShader(lgRight);
		p.setStrokeWidth(height - 2 * gradientSize);
		canvas.drawLine(width - gradientSize, height / 2f, width, height / 2f, p);

		// Top left corner
		int[] colorCorner = { color, color & 0x00ffffff };
		float[] positionCorner = { 0f, 1f };
		RectF corner = new RectF(0, 0, gradientSize, gradientSize);
		RadialGradient rgTopLeft = new RadialGradient(gradientSize, gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgTopLeft);
		canvas.drawRect(corner, p);

		// Top right corner
		corner = new RectF(width - gradientSize, 0, width, gradientSize);
		RadialGradient rgTopRight = new RadialGradient(width - gradientSize, gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgTopRight);
		canvas.drawRect(corner, p);

		// Bottom left corner
		corner = new RectF(0, height - gradientSize, gradientSize, height);
		RadialGradient rgBottomLeft = new RadialGradient(gradientSize, height - gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgBottomLeft);
		canvas.drawRect(corner, p);

		// Bottom right corner
		corner = new RectF(width - gradientSize, height - gradientSize, width, height);
		RadialGradient rgBottomRight = new RadialGradient(width - gradientSize, height - gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgBottomRight);
		canvas.drawRect(corner, p);

		return bitmap;
	}
}
