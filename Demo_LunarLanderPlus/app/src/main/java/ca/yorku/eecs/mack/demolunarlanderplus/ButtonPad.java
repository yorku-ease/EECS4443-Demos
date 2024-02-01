package ca.yorku.eecs.mack.demolunarlanderplus;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;

/**
 * ButtonPad - implementation of a "touch mode" set of button roughly equivalent to a D-pad on a
 * game controller. There are five buttons: Up, Down, Left, Right, Center. The touch events will be
 * processed by the activity.
 * <p>
 * 
 * @author (c) Scott MacKenzie, 2013
 * 
 */

public class ButtonPad extends View
{
	final static int CENTER = 0;
	final static int UP = 1;
	final static int DOWN = 2;
	final static int LEFT = 3;
	final static int RIGHT = 4;
	final static int NONE = -1;

	final boolean PRESSED = true;
	final boolean NORMAL = false;

	final int PAD_SPAN = 500;
	final int BACKGROUND_COLOR = 0x44123456; // "44" --> alpha (translucency)
	final int BORDER_COLOR = Color.DKGRAY;
	final int TEXT_SIZE = (int)(PAD_SPAN / 20f + 0.5f);

	// boolean to hold the state of the buttons (false = normal, true = pressed)
	boolean centerButtonPressed;
	boolean upButtonPressed;
	boolean downButtonPressed;
	boolean leftButtonPressed;
	boolean rightButtonPressed;

	private int width;
	private int height;
	private int centerX, centerY;
	private float offset;
	float cTextX, cTextY, uTextX, uTextY, dTextX, dTextY, lTextX, lTextY, rTextX, rTextY;

	Paint textPaint, pressedTextPaint, borderPaint;
	RectF centerCircle; // center button
	RectF cCircle, lRectangle, rRectangle, uRectangle, dRectangle; // circles or rectangles to show
																	// the pressed state
	Bitmap gradientSquare, gradientCircle;

	public ButtonPad(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		centerButtonPressed = false;
		upButtonPressed = false;
		downButtonPressed = false;
		leftButtonPressed = false;
		rightButtonPressed = false;

		borderPaint = new Paint();
		borderPaint.setColor(BORDER_COLOR);
		borderPaint.setStyle(Paint.Style.STROKE);
		borderPaint.setStrokeWidth(2);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setTextAlign(Paint.Align.CENTER);
		textPaint.setColor(0xbb444444);

		pressedTextPaint = new Paint();
		pressedTextPaint.setAntiAlias(true);
		pressedTextPaint.setTextAlign(Paint.Align.CENTER);
		pressedTextPaint.setColor(0xffff0000);
		pressedTextPaint.setTypeface(Typeface.create("Helvetica", Typeface.BOLD));

		this.setBackgroundColor(BACKGROUND_COLOR);

		setButtonSetSize(PAD_SPAN, PAD_SPAN);
	}

	/**
	 * Measure the view and its content to determine the measured width and the measured height.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(width, height);
	}

	/**
	 * Set the size of this button set (and a few other things). The goal is to minimize the
	 * calculations done in onDraw.
	 */
	public void setButtonSetSize(int widthArg, int heightArg)
	{
		width = widthArg;
		height = heightArg;
		textPaint.setTextSize((float) width / 8);
		pressedTextPaint.setTextSize((float) width / 8);
		centerX = (int)(width / 2f + 0.5f);
		centerY = (int)(height / 2f + 0.5f);

		centerCircle = new RectF(width * 0.25f, height * 0.25f, width * 0.75f, height * 0.75f);
		offset = 0.707f * ((float)Math.sqrt(2 * width * width) - (width / 2f)) / 2f;

		// compute text coordinates now (instead of each time onDraw executes)
		cTextX = width / 2f;
		cTextY = height / 2f + TEXT_SIZE / 1.5f;
		uTextX = width / 2f;
		uTextY = height / 8f + TEXT_SIZE / 1.5f;
		dTextX = width / 2f;
		dTextY = 7f * height / 8f + TEXT_SIZE / 1.5f;
		lTextX = width / 8f;
		lTextY = height / 2f + TEXT_SIZE / 1.5f;
		rTextX = 7f * width / 8f;
		rTextY = height / 2f + TEXT_SIZE / 1.5f;

		// initialize circles for pressed states of buttons
		cCircle = new RectF(width * 0.30f, height * 0.30f, width * 0.70f, height * 0.70f);
		uRectangle = new RectF(width * 0.20f, height * 0.0f, width * 0.80f, height * 0.28f);
		dRectangle = new RectF(width * 0.20f, height * 0.77f, width * 0.80f, height * 0.98f);
		lRectangle = new RectF(width * 0.02f, height * 0.20f, width * 0.23f, height * 0.80f);
		rRectangle = new RectF(width * 0.77f, height * 0.20f, width * 0.98f, height * 0.80f);
		gradientSquare = getGradientSquare(100, 30, 0x55ff0000);
		gradientCircle = getGradientCircle(100, 30, 0x55ff0000);
	}

	@Override
	protected void onDraw(Canvas c)
	{
		super.onDraw(c);

		// draw borders and lines
		c.drawRect(0, 0, width, height, borderPaint);
		c.drawLine(0, 0, offset, offset, borderPaint);
		c.drawLine(width, 0, width - offset, offset, borderPaint);
		c.drawLine(0, height, offset, height - offset, borderPaint);
		c.drawLine(width, height, width - offset, height - offset, borderPaint);
		c.drawOval(centerCircle, borderPaint);

		// draw text
		if (centerButtonPressed)
		{
			c.drawBitmap(gradientCircle, null, centerCircle, null);
			c.drawText("C", cTextX, cTextY, pressedTextPaint);
		} else
			c.drawText("C", cTextX, cTextY, textPaint);

		if (upButtonPressed)
		{
			c.drawBitmap(gradientSquare, null, uRectangle, null);
			c.drawText("U", uTextX, uTextY, pressedTextPaint);
		} else
			c.drawText("U", uTextX, uTextY, textPaint);

		if (downButtonPressed)
		{
			c.drawBitmap(gradientSquare, null, dRectangle, null);
			c.drawText("D", dTextX, dTextY, pressedTextPaint);
		} else
			c.drawText("D", dTextX, dTextY, textPaint);

		if (leftButtonPressed)
		{
			c.drawBitmap(gradientSquare, null, lRectangle, null);
			c.drawText("L", lTextX, lTextY, pressedTextPaint);
		} else
			c.drawText("L", lTextX, lTextY, textPaint);

		if (rightButtonPressed)
		{
			c.drawBitmap(gradientSquare, null, rRectangle, null);
			c.drawText("R", rTextX, rTextY, pressedTextPaint);
		} else
			c.drawText("R", rTextX, rTextY, textPaint);
	}

	/**
	 * Get the button at the indicated x/y coordinate.
	 * <p>
	 * Will return one of ButtonPad.CENTER, ButtonPad.UP, etc.
	 * <p>
	 * The algebra here is a bit tricky, but it works. First, identify if the touch point is in the
	 * center button. This is straight forward.
	 * <p>
	 * If the touch point is not in the center button, then compute the angle of the touch point
	 * relative to the top-left and top-right corners of the button pad. The combination of these
	 * two angles is used to determine which of the four remaining regions the touch point lies
	 * within. If you draw a diagram and put on your high-school algebra hat, this will makes sense!
	 */

	public int getButton(float x, float y)
	{
		float dx = x - centerX;
		float dy = y - centerY;
		int button = NONE;

		float distanceFromCenter = (float)Math.sqrt(dx * dx + dy * dy);
		float angle1 = (float)Math.atan(y / x);
		float angle2 = (float)Math.atan(y / (width - x));

		// determine which button is associated with this x/y coordinate
		if (distanceFromCenter <= width / 4f)
			button = CENTER;
		else if (angle1 <= Math.PI / 4f && angle2 <= Math.PI / 4f)
			button = UP;
		else if (angle1 <= Math.PI / 4f && angle2 > Math.PI / 4f)
			button = RIGHT;
		else if (angle1 > Math.PI / 4f && angle2 <= Math.PI / 4f)
			button = LEFT;
		else if (angle1 > Math.PI / 4f && angle2 > Math.PI / 4f)
			button = DOWN;
		return button;
	}

	/**
	 * Set the state of the button to pressed. Invalidate to update L&F.
	 * <p>
	 * If this seems overly complicated, it's because we are supporting multitouch interaction on
	 * the button pad. There is the possibility of >1 button being pressed simultaneously. So, we
	 * want to set to pressed the indicated button while not changing the state of the other
	 * buttons.
	 */
	public void setPressed(int buttonArg)
	{
		switch (buttonArg)
		{
			case CENTER:
				centerButtonPressed = PRESSED;
				break;
			case LEFT:
				leftButtonPressed = PRESSED;
				break;
			case RIGHT:
				rightButtonPressed = PRESSED;
				break;
			case UP:
				upButtonPressed = PRESSED;
				break;
			case DOWN:
				downButtonPressed = PRESSED;
				break;
		}
		invalidate();
	}

	/**
	 * Set the state of the button to released. Invalidate to update L&F.
	 */
	public void setReleased(int buttonArg)
	{
		switch (buttonArg)
		{
			case CENTER:
				centerButtonPressed = NORMAL;
				break;
			case LEFT:
				leftButtonPressed = NORMAL;
				break;
			case RIGHT:
				rightButtonPressed = NORMAL;
				break;
			case UP:
				upButtonPressed = NORMAL;
				break;
			case DOWN:
				downButtonPressed = NORMAL;
				break;
		}
		invalidate();
	}

	/*
	 * Return a square bitmap with a gradient edge. Three parameters are required: the width of the
	 * bitmap, the size of the gradient region, and the color.
	 * 
	 * Example: If width = 300 and gradientSize = 10, the bitmap will have a central area that is
	 * 280 x 280, rendered in the specified color. A gradient region 10 pixels wide borders the
	 * central region. Within the gradient region the color transitions from the specified color
	 * adjacent to the central region to the same color with alpha = 0 at the edge, thus creating a
	 * fading-out feathered effect.
	 */
	public Bitmap getGradientSquare(int width, int gradientSize, int color)
	{
		Paint p = new Paint();
		int height = width;

		// create the bitmap to draw into
		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		// Middle section (top to bottom)
		int[] colorArray = { color & 0x00ffffff, color, color, color & 0x00ffffff };
		float[] positionArray = { 0f, (float) gradientSize / width, 1f - (float) gradientSize / width, 1f };
		LinearGradient lg = new LinearGradient(width / 2f, 0, width / 2f, height, colorArray, positionArray, Shader
				.TileMode.CLAMP);
		p.setShader(lg);
		p.setStrokeWidth(width - 2 * gradientSize);
		canvas.drawLine(width / 2f, 0, width / 2f, height, p); // top to bottom

		// Left edge
		int[] colorLeft = { color & 0x00ffffff, color }; // left side
		float[] positionLeft = { 0f, 1f };
		LinearGradient lgLeft = new LinearGradient(0, width / 2f, gradientSize, width / 2f, colorLeft, positionLeft,
				Shader.TileMode.CLAMP);
		p.setShader(lgLeft);
		p.setStrokeWidth(width - 2 * gradientSize);
		canvas.drawLine(0, width / 2f, gradientSize, width / 2f, p);

		// Right edge
		int[] colorRight = { color, color & 0x00ffffff }; // right side
		float[] positionRight = { 0f, 1f };
		LinearGradient lgRight = new LinearGradient(width - gradientSize, width / 2f, width, width / 2f, colorRight, positionRight,
				Shader.TileMode.CLAMP);
		p.setShader(lgRight);
		p.setStrokeWidth(width - 2 * gradientSize);
		canvas.drawLine(width - gradientSize, width / 2f, width, width / 2f, p);

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
		corner = new RectF(0, width - gradientSize, gradientSize, height);
		RadialGradient rgBottomLeft = new RadialGradient(gradientSize, width - gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgBottomLeft);
		canvas.drawRect(corner, p);

		// Bottom right corner
		corner = new RectF(width - gradientSize, width - gradientSize, width, height);
		RadialGradient rgBottomRight = new RadialGradient(width - gradientSize, width - gradientSize, gradientSize, colorCorner, positionCorner,
				Shader.TileMode.CLAMP);
		p.setShader(rgBottomRight);
		canvas.drawRect(corner, p);

		return bitmap;
	}
	
	public Bitmap getGradientCircle(int diameter, int gradientSize, int color)
	{
		Paint p = new Paint();
	
		// create the bitmap to draw into and give it to a canvas instance
		Bitmap bitmap = Bitmap.createBitmap(diameter, diameter, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);

		// create the arrays for colors and the relative positions of colors
		int[] colorArray = { color, color, color & 0x00ffffff };
		float[] positionArray = { 0f, 1f - (float)gradientSize / diameter, 1f };

		// create the shader and give it to the paint instance
		RadialGradient rg = new RadialGradient((float) diameter / 2, (float) diameter / 2, (float) diameter / 2, colorArray, positionArray,
				Shader.TileMode.CLAMP);
		p.setShader(rg);
		
		// draw the gradient circle into the bitmap and return the bitmap
		canvas.drawCircle((float) diameter / 2, (float) diameter / 2, (float) diameter / 2, p);
		return bitmap;
	}
}
