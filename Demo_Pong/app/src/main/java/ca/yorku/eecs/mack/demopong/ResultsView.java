package ca.yorku.eecs.mack.demopong;

import ca.yorku.eecs.mack.demopong.PongView.PongThread;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

public class ResultsView extends View
{
	//final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static int DEFAULT_BALL_DIAMETER = 50; // 1/8 inch in dp units
	final static float PI = (float)Math.PI;
	final static float THREE_PI_OVER_FOUR = 3f * (float)Math.PI / 4f;
	final static float TWO_PI = 2f * (float)Math.PI;
	final static int DEFAULT_BALL_VELOCITY = 1; // inches per second
	final static float DEFAULT_ROTATION_INCREMENT = 10f;

	int width, height;
	int level, hits, misses;
	int numberOfTrials, gameState;
	boolean victory;	

	float gap1, gap2, radius, y1, y2, yOffset, x, pixelDensity, ballRadius, xBall, yBall;

	Paint textPaint, linePaint, fillPaintRed, fillPaintGreen;

	Drawable happyFace, sadFace, neutralFace, victoryMessage;
	Drawable ballVictory;
	Bitmap resultsBitmap;
	Rect r;

	long deltaTime, lastTime;
	float dz, velocity, lastX, angle, oneInch;
	float leftWall, topWall, rightWall, bottomWall, rotation, rotationIncrement;

	public ResultsView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		textPaint = new Paint();
		textPaint.setAntiAlias(true);
		textPaint.setColor(Color.WHITE);
		textPaint.setTextAlign(Paint.Align.RIGHT);

		linePaint = new Paint();
		linePaint.setStyle(Paint.Style.STROKE);
		linePaint.setColor(Color.DKGRAY);

		fillPaintRed = new Paint();
		fillPaintRed.setStyle(Paint.Style.FILL);
		fillPaintRed.setColor(Color.RED);

		fillPaintGreen = new Paint();
		fillPaintGreen.setStyle(Paint.Style.FILL);
		fillPaintGreen.setColor(Color.GREEN);

		happyFace = context.getResources().getDrawable(R.drawable.happy_face);
		neutralFace = context.getResources().getDrawable(R.drawable.neutral_face);
		sadFace = context.getResources().getDrawable(R.drawable.sad_face);
		victoryMessage = context.getResources().getDrawable(R.drawable.victory_message);

		// get ball image
		ballVictory = context.getResources().getDrawable(R.drawable.happy_face_ball);

		pixelDensity = context.getResources().getDisplayMetrics().density;
		ballRadius = DEFAULT_BALL_DIAMETER / 2f * pixelDensity;
		xBall = 100;
		yBall = 100;

		oneInch = 160 * pixelDensity;
		velocity = DEFAULT_BALL_VELOCITY;
		angle = -THREE_PI_OVER_FOUR;
		rotation = 0f;
		rotationIncrement = DEFAULT_ROTATION_INCREMENT;
	}

	/*
	 * The view is now visible so its width and height are available.
	 * 
	 * Do nothing if hasFocus is false. Otherwise, initialize width and height and other things that
	 * depend on these.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		if (!hasFocus)
			return;

		width = this.getWidth();
		height = this.getHeight();

		/*
		 * Compute the coordinates for positioning the images (happy_face, neutral_face, sad_face,
		 * and victory_message). The images, which include some text, are all the same size with
		 * image_width = 6 x image_height. The goal is to position the images horizontally in the
		 * center of the view and vertically near the bottom of the view.
		 */
		final int rWidth = (int)Math.min(0.8f * width, 0.3f * height * 6f); // compute image width
		final int rHeight = (int)(rWidth / 6f); // images width:height ratio is 6:1
		final int rLeft = (int)(width / 2f - rWidth / 2f); // x-position to draw images
		final int rTop = (int)(0.90f * height - rHeight); // y-position to draw images

		// compute the bounding rectangle and give it to the images
		r = new Rect();
		r.left = rLeft;
		r.top = rTop;
		r.right = rLeft + rWidth;
		r.bottom = rTop + rHeight;
		happyFace.setBounds(r);
		neutralFace.setBounds(r);
		sadFace.setBounds(r);
		victoryMessage.setBounds(r);

		/*
		 * Compute the paint's text size based on a formula that considers both the width and height
		 * of the resulting text. We want the width of "Misses:" to be no more than 25% of the view
		 * width. Furthermore, we want the height of "Misses:" to be no more than 10% of the view
		 * height. Below, we calculate the new text size based on each of these requirements and
		 * then choose the lesser of the two.
		 */

		// compute text size based on width constraint (see above)
		final float currentTextWidth = textPaint.measureText("Misses:");
		final float availableWidth = 0.25f * width;
		final float currentTextSize = textPaint.getTextSize();
		final float textSizeWidthConstraint = currentTextSize * (availableWidth / currentTextWidth);

		// compute text size based on height constraint (see above)
		Rect r2 = new Rect();
		textPaint.getTextBounds("A", 0, 1, r2);
		final float currentTextHeight = r2.height();
		final float availableHeight = 0.1f * height;
		final float textSizeHeightConstraint = currentTextSize * (availableHeight / currentTextHeight);

		// select the lesser of the two sizes as the new text size
		final float newTextSize = Math.min(textSizeWidthConstraint, textSizeHeightConstraint);
		textPaint.setTextSize(newTextSize);

		/*
		 * Compute the height of the text, as measured using a nominal character (i.e., an uppercase
		 * character with no descender).
		 */
		textPaint.getTextBounds("A", 0, 1, r2);
		final float textHeight = r2.height();

		// compute parameters for sizing and positioning text and circles
		gap1 = 0.08f * width; // gap between text as 1st circle
		gap2 = 0.02f * width; // gap between circles
		radius = 1.2f * (textHeight / 2f);
		x = 0.35f * width; // text to the left, circles to the right
		y1 = 0.20f * height;
		y2 = 0.45f * height;

		// make adjustments to ensure circles fit in available space
		while (gap1 + numberOfTrials * (2f * radius + gap2) > (0.65f * width))
		{
			radius *= 0.90f;
			gap1 *= 0.90f;
			gap2 *= 0.70f;
		}

		// create the results bitmap (which is drawn in onDraw)
		createResultsBitmap();

		// compute a y-offset so the circles are vertically aligned with the text
		yOffset = -textHeight / 2f;

		leftWall = 0f;
		topWall = 0f;
		rightWall = width;
		bottomWall = height;
	}

	public void setResults(int levelArg, int numberOfTrialsArg, int hitsArg, int missesArg, int gameStateArg)
	{
		level = levelArg;
		numberOfTrials = numberOfTrialsArg;
		hits = hitsArg;
		misses = missesArg;
		gameState = gameStateArg;

		// for quick testing of the victory result dialog. (comment out for final version)
		//gameState = PongThread.STATE_VICTORY;

		victory = gameState == PongThread.VICTORY;
	}

	/*
	 * The onDraw method will be busy if the game state is "victory". To minimize the computations
	 * in onDraw, we create a results bitmap here, rather than recreating it each time onDraw runs.
	 */
	public void createResultsBitmap()
	{
		resultsBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(resultsBitmap);

		c.drawText("Hits:", x, y1, textPaint);
		c.drawText("Misses:", x, y2, textPaint);

		// "hits" circles
		for (int i = 0; i < numberOfTrials; ++i)
			if (i < hits)
				c.drawCircle(x + gap1 + (gap2 + 2f * radius) * i, y1 + yOffset, radius, fillPaintGreen);
			else
				c.drawCircle(x + gap1 + (gap2 + 2f * radius) * i, y1 + yOffset, radius, linePaint);

		// "misses" circles
		for (int i = 0; i < numberOfTrials; ++i)
			if (i < misses)
				c.drawCircle(x + gap1 + (gap2 + 2f * radius) * i, y2 + yOffset, radius, fillPaintRed);
			else
				c.drawCircle(x + gap1 + (gap2 + 2f * radius) * i, y2 + yOffset, radius, linePaint);

		// draw the appropriate results image based on the game state
		if (gameState == PongThread.VICTORY)
			victoryMessage.draw(c);
		else if (gameState == PongThread.WIN)
			happyFace.draw(c);
		else if (gameState == PongThread.RETRY)
			neutralFace.draw(c);
		else if (gameState == PongThread.BACK_ONE_LEVEL)
			sadFace.draw(c);
	}

	@Override
	public void onDraw(Canvas canvas)
	{
		super.onDraw(canvas);
		canvas.drawBitmap(resultsBitmap, 0, 0, null);

		if (victory)
		{
			updateGame();
			canvas.rotate(rotation, xBall, yBall);
			ballVictory.draw(canvas);
		}
	}

	/*
	 * Modelled after the method of the same name in PongView.
	 */
	public void updateGame()
	{
		// compute delta time
		long now = SystemClock.elapsedRealtime();
		deltaTime = now - lastTime;
		lastTime = now;

		// compute new position of ball according to the velocity and angle
		dz = velocity * ((float)deltaTime / 1000) * oneInch; // inches per second
		lastX = xBall;
		xBall += dz * (float)Math.sin(angle);
		yBall += dz * (float)Math.cos(angle);

		// left wall check
		if (xBall - ballRadius < leftWall) // hit left wall
		{
			angle = TWO_PI - angle;
			xBall = leftWall + ballRadius;
			rotationIncrement *= -1f;
		}

		// right wall check
		else if (xBall + ballRadius > rightWall) // hit right wall
		{
			angle = TWO_PI - angle;
			xBall = rightWall - ballRadius;
			rotationIncrement *= -1f;
		}

		// bottom wall check
		else if (yBall + ballRadius > bottomWall) // hit bottom wall
		{
			angle = PI - angle;
			yBall = bottomWall - ballRadius;
			rotationIncrement *= -1f;
		}

		// top wall check
		else if (yBall - ballRadius < topWall) // hit top wall
		{
			angle = PI - angle;
			yBall = topWall + ballRadius;
			rotationIncrement *= -1f;
		}

		rotation += rotationIncrement;
		
		ballVictory.setBounds((int)(xBall - ballRadius + 0.5f), (int)(yBall - ballRadius + 0.5f), (int)(xBall
				+ ballRadius + 0.5f), (int)(yBall + ballRadius + 0.5f));

		invalidate();
	}

	/*
	 * onMeasure - Since this is a custom View, we should override onMeasure.
	 * 
	 * The code here is a close approximation to that found in LabelView.java (the Android sample
	 * API provided for custom views). See also...
	 * 
	 * http://developer.android.com/guide/topics/ui/custom-components.html#custom
	 * 
	 * The approach here is to assume (hope!) that exact values are available via the XML layout
	 * files.
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int measureSpec)
	{
		int result = 100; // fall-back value (so something appears!)
		int specMode = View.MeasureSpec.getMode(measureSpec);
		int specSize = View.MeasureSpec.getSize(measureSpec);

		if (specMode == View.MeasureSpec.EXACTLY) // We were told how big to be (return that)
			result = specSize; // the value computed based on XML layout

		else if (specMode == View.MeasureSpec.AT_MOST)// respect AT_MOST if that's the spec
			result = Math.min(result, specSize);

		return result;
	}

	private int measureHeight(int measureSpec)
	{
		int result = 100; // fall-back value (so something appears!)
		int specMode = View.MeasureSpec.getMode(measureSpec);
		int specSize = View.MeasureSpec.getSize(measureSpec);

		if (specMode == View.MeasureSpec.EXACTLY) // We were told how big to be (return that)
			result = specSize; // the value computed based on the XML layout

		else if (specMode == View.MeasureSpec.AT_MOST) // respect AT_MOST if that's the spec
			result = Math.min(result, specSize);

		return result;
	}
}
