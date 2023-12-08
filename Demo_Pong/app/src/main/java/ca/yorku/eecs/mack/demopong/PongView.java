package ca.yorku.eecs.mack.demopong;

import java.util.Random;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * View that draws a simple Pong game.
 */
class PongView extends SurfaceView implements SurfaceHolder.Callback
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	// final static int GAME_LEVELS = 10;
	final static float SCREEN_WIDTH_BASELINE = 1196 / (2f * 160f); // 3.74 inches (Nexus 4)

	// pointer to the application context (used to fetch Drawables, etc.)
	Context context;

	// pointer to the text view that displays the Demo Pong logo image
	MessageView messageView;

	// pointer to the surface holder (the link between the view and the secondary thread)
	SurfaceHolder surfaceHolder;

	// the thread that actually draws the game
	PongThread pongThread;

	float pixelDensity, screenWidthInches;
	float screenWidthScalingFactor;
	int gameLevel, trials;
	float gainVelocityControl;
	boolean vibrotactileFeedback, auditoryFeedback;
	Vibrator v;

	/*
	 * The following interface provides a means to communicate back to the host activity that a
	 * block of trials is done.
	 */
	public interface OnBlockDoneListener
	{
		void onBlockDone();
	}

	OnBlockDoneListener onBlockDoneListener;

	public void setOnBlockDoneListener(OnBlockDoneListener obdl)
	{
		onBlockDoneListener = obdl;
	}

	public PongView(Context contextArg, AttributeSet attrs)
	{
		super(contextArg, attrs);

		/*
		 * Register our interest in hearing about changes to our surface. See...
		 * 
		 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
		 */
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);
		context = contextArg;

		pixelDensity = contextArg.getResources().getDisplayMetrics().density;
		screenWidthInches = contextArg.getResources().getDisplayMetrics().widthPixels / (pixelDensity * 160f);
		screenWidthScalingFactor = screenWidthInches / SCREEN_WIDTH_BASELINE;

		/*
		 * Instantiate the secondary thread. But, do *not* start the thread! This is important
		 * because the secondary thread cannot touch the surface until it is created. The thread is
		 * started in surfaceCreated (see below).
		 */
		pongThread = new PongThread(surfaceHolder, this.getContext());

	}

	// fetch the animation thread corresponding to this PongView
	public PongThread getThread()
	{
		return pongThread;
	}

	// pause on focus lost (e.g. user switches to take a call)
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		if (!hasWindowFocus)
			pongThread.doPauseGame();
	}

	// installs a pointer to the text view used for messages
	public void setMessageView(MessageView messageViewArg)
	{
		messageView = messageViewArg;
	}

	public void setVibrator(Vibrator vArg)
	{
		v = vArg;
	}

	public void setParameters(int gameLevelArg, int trialsArg, float gainVelocityControlArg,
			boolean vibrotactileFeedbackArg, boolean auditoryFeedbackArg)
	{
		gameLevel = gameLevelArg;
		trials = trialsArg;
		gainVelocityControl = gainVelocityControlArg;
		vibrotactileFeedback = vibrotactileFeedbackArg;
		auditoryFeedback = auditoryFeedbackArg;
	}

	public void nextGameLevel()
	{
		++gameLevel;
		if (gameLevel > DemoPongActivity.TOP_GAME_LEVEL)
			--gameLevel;
	}

	public void previousGameLevel()
	{
		--gameLevel;
		if (gameLevel < 1)
			++gameLevel;
	}

	// ======================================================================
	// The next three methods are the callbacks as per SurfaceHolder.Callback.
	// ======================================================================

	// callback when the Surface dimensions change
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// Log.i(MYDEBUG, "PongView: surfaceChanged! width=" + width + ", height=" + height);
		pongThread.setSurfaceSize(width, height);
	}

	// callback when the Surface has been created and is ready to be used
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		// Log.i(MYDEBUG, "PongView: surfaceCreated! pongThread=" + pongThread);
		if (pongThread == null)
		{
			// this is executed if the app is restarting
			pongThread = new PongThread(surfaceHolder, this.getContext());
		}

		pongThread.setVibrator(v);
		pongThread.setOnBlockDoneListener(onBlockDoneListener);
		pongThread.threadStatus = PongThread.STARTED;
		pongThread.start(); // begin drawing operations on the surface's canvas
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer be touched. WARNING:
	 * after this method returns, the Surface/Canvas must never be touched again!
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		/*
		 * Tell thread to shut down and wait for it to finish (or else it might touch the Surface
		 * after we return and explode!).
		 */
		pongThread.threadStatus = PongThread.SHUT_DOWN;
		while (pongThread != null)
		{
			try
			{
				pongThread.join();
				pongThread = null;
			} catch (InterruptedException e)
			{
				Log.i(MYDEBUG, "surfaceDestroyed exception: " + e);
			}
		}
	}

	/*
	 * ============================================================================================
	 * As noted in the API guide for SurfaceView, "Inside your SurfaceView class is also a good
	 * place to define your secondary Thread class, which will perform all the drawing procedures to
	 * your Canvas." That's the approach used here. See...
	 * 
	 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
	 */
	class PongThread extends Thread
	{
		final static float PI = (float)Math.PI;
		final static float TWO_PI = 2f * (float)Math.PI;
		final static float PI_OVER_FOUR = (float)Math.PI / 4f;
		final static float THREE_PI_OVER_FOUR = 3f * (float)Math.PI / 4f;

		// int constants for game state
		final static int PAUSED = 100;
		final static int READY = 200;
		final static int RUNNING = 300;
		final static int WIN = 400;
		final static int VICTORY = 500;
		final static int RETRY = 600;
		final static int BACK_ONE_LEVEL = 700;

		// int constants for thread status
		final static int STARTED = 100;
		final static int SHUT_DOWN = 200;

		final static float PADDLE_LENGTH_FACTOR = 6f; // x ball diameter
		final static int WALL_THICKNESS = 8;
		final static int PADDLE_WIDTH = 20; // 1/8 inch (dp units)
		final static int CORNER_RADIUS = 6;
		final static int DEFAULT_BALL_DIAMETER = 20; // 1/8 inch in dp units
		final static int DEFAULT_BALL_VELOCITY = 4; // inches per second
		final static int MAXIMUM_PADDLE_VELOCITY = 10; // inches per second

		final static int RESULTS_BACKGROUND_COLOR = 0xfff7bda1;
		final static int WALL_COLOR = 0xffff9b9b;
		final static int TABLE_COLOR = 0xfff7bda1;
		final static int PADDLE_LINE_COLOR = 0xff9e0000;
		final static int PADDLE_FILL_COLOR = 0xffc04444;
		final static int STATS_COLOR = 0xff888888;
		final static int STATS_HIT_COLOR = 0xff008800;
		final static int STATS_MISS_COLOR = 0xffaa0000;

		// int constants for trial status
		final static int HIT = 100;
		final static int MISS = 200;
		final static int PENDING = 300;

		final String[] LEVEL_TEXT = { "1", "2", "3", "4", "5", "6", "7", "8", "9", "10" };

		Vibrator v; // used when ball hits paddle (if setting enabled)
		MediaPlayer miss, victorySound;
		Drawable ball;
		Random random;
		final SurfaceHolder surfaceHolder; // handle to the surface manager object we interact with
		OnBlockDoneListener onBlockDoneListener;

		Paint wallPaint; // paint to draw the lines on the the screen
		Paint resultsBackgroundPaint;
		Paint tablePaint;
		Paint paddleLinePaint, paddleFillPaint, resultsPaint, resultsHitPaint, resultsMissPaint, timePaint;
		Paint levelCurrentTextPaint, levelTextPaint, levelCurrentFillPaint, levelFillPaint;

		RectF resultsPanel, table, wall, paddle;
		RectF[] results, level;
		int[] resultsLog;
		float paddleThickness; // thickness of paddle
		float angle;
		float[] angleOffset;
		float oneInch;

		float paddleHeight; // height of the paddle (adjusted for game level)
		float paddleHeightLevel1; // height of paddle for level 1
		float velocity; // velocity of ball
		float wallThickness; // wall thickness (affects size of table)

		float ballRadius;
		long lastTime;
		int gameState; // the state of the game
		int threadStatus; // the status of the thread (STARTED or SHUT_DOWN)
		float x; // x of the ball center
		float y; // y of the ball center
		int idx;
		float lastX; // to determine the direction of ball movement (see updatePhysics)
		float paddleLastTop, paddleLastTopOptimal;
		boolean paddleHasMoved, paddleMovingUp;
		float dz; // the distance the ball should move
		long deltaTime, elapsedTime, startTime;
		int hits, misses, paddleReversals;
		float paddleReversalTop;
		boolean crossedPaddleThreshold;
		float xTime, yTime, yLevelCurrent, yLevel;
		float[] xLevel;
		float paddleYRatio, cornerRadius;
		int paddleMovementMin, paddleMovementActual;
		float paddleMovementEfficiency;
		int mode;

		public PongThread(SurfaceHolder surfaceHolderArg, Context contextArg)
		{
			/*
			 * This is the handle to the SurfaceView's surface holder (needed to synchronize
			 * operations in the secondary thread).
			 */
			surfaceHolder = surfaceHolderArg;

			// init game used (only used if auditory feedback setting enabled)
			miss = MediaPlayer.create(contextArg, R.raw.miss);
			victorySound = MediaPlayer.create(contextArg, R.raw.tada);

			// get ball image
			ball = contextArg.getResources().getDrawable(R.drawable.ball);

			random = new Random();
			resultsPanel = new RectF();
			table = new RectF();
			wall = new RectF();
			paddle = new RectF();

			// initialize paints
			wallPaint = new Paint();
			wallPaint.setStyle(Paint.Style.STROKE);
			wallPaint.setColor(WALL_COLOR);

			resultsBackgroundPaint = new Paint();
			resultsBackgroundPaint.setStyle(Paint.Style.FILL);
			resultsBackgroundPaint.setColor(RESULTS_BACKGROUND_COLOR);

			tablePaint = new Paint();
			tablePaint.setStyle(Paint.Style.FILL);
			tablePaint.setColor(TABLE_COLOR);

			paddleLinePaint = new Paint();
			paddleLinePaint.setStyle(Paint.Style.STROKE);
			paddleLinePaint.setColor(PADDLE_LINE_COLOR);

			paddleFillPaint = new Paint();
			paddleFillPaint.setStyle(Paint.Style.FILL);
			paddleFillPaint.setColor(PADDLE_FILL_COLOR);

			levelCurrentFillPaint = new Paint();
			levelCurrentFillPaint.setStyle(Paint.Style.FILL);
			levelCurrentFillPaint.setColor(0xffffff44);

			levelCurrentTextPaint = new Paint();
			levelCurrentTextPaint.setAntiAlias(true);
			levelCurrentTextPaint.setColor(Color.BLUE);
			levelCurrentTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
			levelCurrentTextPaint.setTextAlign(Paint.Align.CENTER);

			levelFillPaint = new Paint();
			levelFillPaint.setStyle(Paint.Style.FILL);
			levelFillPaint.setColor(0xff888888);

			levelTextPaint = new Paint();
			levelTextPaint.setAntiAlias(true);
			levelTextPaint.setColor(0xffaaaaaa);
			levelTextPaint.setTextAlign(Paint.Align.CENTER);

			resultsPaint = new Paint();
			resultsPaint.setStyle(Paint.Style.STROKE);
			resultsPaint.setColor(STATS_COLOR);

			resultsHitPaint = new Paint();
			resultsHitPaint.setStyle(Paint.Style.FILL);
			resultsHitPaint.setColor(STATS_HIT_COLOR);

			resultsMissPaint = new Paint();
			resultsMissPaint.setStyle(Paint.Style.FILL);
			resultsMissPaint.setColor(STATS_MISS_COLOR);

			timePaint = new Paint();
			timePaint.setAntiAlias(true);
			timePaint.setColor(Color.DKGRAY);
			timePaint.setTextAlign(Paint.Align.RIGHT);

			// init/adjust things that depend on the pixel density or display width
			oneInch = 160 * pixelDensity;
			ballRadius = DEFAULT_BALL_DIAMETER / 2f * pixelDensity * screenWidthScalingFactor;
			wallThickness = WALL_THICKNESS * pixelDensity;
			wallPaint.setStrokeWidth(wallThickness);
			paddleLinePaint.setStrokeWidth(2f * pixelDensity);
			paddleHeightLevel1 = (int)(2F * ballRadius * PADDLE_LENGTH_FACTOR + 0.5f);
			paddleThickness = PADDLE_WIDTH * pixelDensity;
			cornerRadius = CORNER_RADIUS * pixelDensity;

			idx = 0;
			paddleYRatio = 0.5f; // middle position

			setGameState(READY);

		} // end PongThread constructor

		/*
		 * This method is called from the main activity (and through the surface view) to give the
		 * thread the interface ("this"). The onBlockEnd method is called on the passed argument at
		 * the end of a block.
		 */
		public void setOnBlockDoneListener(OnBlockDoneListener onBlockDoneListenerArg)
		{
			onBlockDoneListener = onBlockDoneListenerArg;
		}

		// starts the game
		public void doStartGame()
		{
			synchronized (surfaceHolder)
			{
				lastTime = SystemClock.elapsedRealtime();
				startTime = lastTime;
				deltaTime = 0;
				setGameState(RUNNING);
			}
		}

		// pauses the physics update & animation
		public void doPauseGame()
		{
			synchronized (surfaceHolder)
			{
				if (gameState == RUNNING)
					setGameState(PAUSED);
			}
		}

		// return true if the user succeeds (completes level 10 with no misses)
		public boolean isVictory()
		{
			return gameState == VICTORY;
		}

		@Override
		public void run()
		{
			while (threadStatus == STARTED)
			{
				Canvas c = null;
				try
				{
					/*
					 * The organization of code here roughly follows the recommendations in the
					 * Android API Guides for SurfaceView. See...
					 * 
					 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-
					 * surfaceview
					 */
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder)
					{
						if (gameState == RUNNING)
							updateGame();

						if (c != null)
							drawGame(c); // draw onto the SurfaceView's canvas
					}
				} finally
				{
					/*
					 * Do this in a "finally" block to guarantee that this code executes (even if an
					 * exception is thrown during the try-block). This ensures we don't leave the
					 * Surface in an inconsistent state. See...
					 * 
					 * http://docs.oracle.com/javase/tutorial/essential/exceptions/finally.html
					 */
					if (c != null)
					{
						/*
						 * Give the canvas back to the SurfaceView (where it is presented on the
						 * display).
						 */
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		/*
		 * Sets the game state -- that is, whether we are ready, running, paused, win, lose, back
		 * one level, or victory.
		 */
		public void setGameState(int stateArg)
		{
			synchronized (surfaceHolder)
			{
				gameState = stateArg;
			}
		}

		public void setVibrator(Vibrator vArg)
		{
			v = vArg;
		}

		/*
		 * Perform the basic initialization of the game, according to the game level.
		 */
		public void initGame()
		{
			// adjust the size of the paddle (up to 50% shrinkage, depending on game level)
			paddleHeight = paddleHeightLevel1 - (paddleHeightLevel1 / 2f) * gameLevel / 10f;

			// adjust the position of the paddle (starts in middle position)
			paddle.left = table.right - paddleThickness;
			paddle.top = (table.bottom + table.top) / 2f - paddleHeight / 2f;
			paddle.right = table.right;
			paddle.bottom = (table.bottom + table.top) / 2f + paddleHeight / 2f;
			paddleLastTop = paddle.top;
			paddleHasMoved = false;
			paddleMovingUp = false;

			// initialize the position of the ball
			x = paddle.left - ballRadius;
			y = (table.top + table.bottom) / 2f;
			lastX = x;

			/*
			 * Initialize the starting direction and angle for the movement of the ball.
			 * 
			 * NOTE: The graphics coordinate system places the point 0,0 at the top-left of the
			 * display with x increasing to the right and y increasing down. Therefore, movement
			 * down (South) is 0 degrees, movement to the right (East) is 90 degrees (PI/2),
			 * movement up (North) is 180 degrees (PI), and so on.
			 * 
			 * With the initialization below, the initial ball movement is North-West (i.e., to the
			 * left at an angle 45 degrees above horizontal).
			 */
			angle = random.nextBoolean() ? -THREE_PI_OVER_FOUR : -PI_OVER_FOUR;

			/*
			 * Scale the velocity, as per the screen width. This is done to ensure the game creates
			 * a similar UI experience on large or small displays. Using the scaled velocity, the
			 * time to traverse the display is the same, regardless of the size of the display.
			 */
			velocity = DEFAULT_BALL_VELOCITY * screenWidthScalingFactor;

			/*
			 * Increase the velocity, as per game level. The initial velocity is incremented by 0%
			 * at game-level = 0. The increment increases linearly, reaching 50% of the initial
			 * velocity at game-level = 10.
			 */
			velocity += (velocity / 2f) * getRatio(gameLevel, 1f, 10f); // inches per second

			idx = 0;
			hits = 0;
			misses = 0;
			paddleReversals = 0;
			paddleMovementMin = 0;
			paddleMovementActual = 0;
			paddleMovementEfficiency = 0f;

			angleOffset = new float[trials];
			resultsLog = new int[trials];
			for (int i = 0; i < trials; ++i)
			{
				// all results pending (until trials begin)
				resultsLog[i] = PENDING;

				/*
				 * Initialize an array of random angle offsets. The offset is added to the default
				 * bounce angle when the ball hits the left wall. The offset is greater for higher
				 * game levels. The maximum random offset is 45 degrees at the top game level.
				 * 
				 * We initialize the array here to minimize the processing in updateGame.
				 */
				angleOffset[i] = random.nextFloat() * PI / 4f
						* getRatio(gameLevel, 1f, DemoPongActivity.TOP_GAME_LEVEL);
			}
		}

		/*
		 * Convert a value to a ratio within a min/max range. If the value <= min, 0 is returned. If
		 * the value >= max, 1 is returned. If the value is between min and max, the ratio is
		 * returned.
		 */
		public float getRatio(float value, float min, float max)
		{
			return Math.min(Math.max(0f, (value - min) / (max - min)), 1f);
		}

		// called from PongView when the surface dimensions change
		public void setSurfaceSize(int width, int height)
		{
			// synchronized to make sure these all change atomically
			synchronized (surfaceHolder)
			{
				// define a small division for partitioning the results panel
				float div = 16 * pixelDensity; // 1/10th of an inch

				timePaint.setTextSize(div);
				levelCurrentTextPaint.setTextSize(div - 2f * pixelDensity);
				levelTextPaint.setTextSize(div - 5f * pixelDensity);

				// initialize the rectangles that define the game screen
				resultsPanel.left = 0;
				resultsPanel.top = height - div;
				resultsPanel.right = width;
				resultsPanel.bottom = height;

				table.left = wallThickness;
				table.top = wallThickness;
				table.right = width - wallThickness;
				table.bottom = resultsPanel.top - wallThickness;

				wall.left = wallThickness / 2f;
				wall.top = wallThickness / 2f;
				wall.right = width - wallThickness / 2f;
				wall.bottom = resultsPanel.top - wallThickness / 2f;

				// init the rectangles (i.e., circles) that display the result of each trial
				results = new RectF[trials];
				for (int i = 0; i < results.length; ++i)
				{
					float xResults = resultsPanel.right - (div / 2f) - (i * div);
					float yResults = resultsPanel.height() / 2f;
					results[i] = new RectF();
					results[i].left = xResults - div / 2f;
					results[i].top = resultsPanel.bottom - yResults - div / 2f;
					results[i].right = xResults + div / 2f;
					results[i].bottom = resultsPanel.bottom - yResults + div / 2f;
				}

				// init the rectangles (i.e., circles) that display the game levels
				level = new RectF[DemoPongActivity.TOP_GAME_LEVEL];
				for (int i = 0; i < level.length; ++i)
				{
					float xLevel = resultsPanel.left + div / 2f + i * div;
					float yLevel = resultsPanel.height() / 2f;
					level[i] = new RectF();
					level[i].left = xLevel - div / 2f;
					level[i].top = resultsPanel.bottom - yLevel - div / 2f;
					level[i].right = xLevel + div / 2f;
					level[i].bottom = resultsPanel.bottom - yLevel + div / 2f;
				}

				// coordinates for painting the time (faster to *not* compute in doDraw)
				xTime = width - div / 2f - trials * div;
				yTime = height - (2f * pixelDensity);

				// coordinates for painting the level info (faster to *not* compute in doDraw)
				yLevelCurrent = height - (3f * pixelDensity);
				yLevel = height - (5f * pixelDensity);
				xLevel = new float[DemoPongActivity.TOP_GAME_LEVEL];
				for (int i = 0; i < xLevel.length; ++i)
					xLevel[i] = (level[i].right + level[i].left) / 2f;

				// initialize the game (including ball velocity, paddle size, etc.)
				initGame();
			}
		}

		/*
		 * Initialize the paddle y-ratio. The passed argument holds the critical value that controls
		 * the position of the paddle. The value is a float between 0 and 1. For 0, the paddle is at
		 * the bottom of the table. For 1, the paddle is at the top of the table. The paddleYRatio
		 * value is used in updateGame in repositioning the paddle. This method is called from the
		 * main activity in response to either a touch event (Touch mode) or sensor event (Tilt
		 * mode).
		 */
		public void setPaddleYRatio(float yArg)
		{
			paddleYRatio = yArg;
		}

		// set the paddle control mode (either MODE_TOUCH or MODE_TILT)
		public void setPaddleMode(int modeArg)
		{
			mode = modeArg;
		}

		// draw the game scene (update ball position, etc.)
		private void drawGame(Canvas canvas)
		{
			// draw the results panel
			canvas.drawRect(resultsPanel, resultsBackgroundPaint);

			// draw the table
			canvas.drawRect(table, tablePaint);

			// draw the table wall
			canvas.drawRect(wall, wallPaint);

			// draw the paddle (fill, then line)
			canvas.drawRoundRect(paddle, cornerRadius, cornerRadius, paddleFillPaint);
			canvas.drawRoundRect(paddle, cornerRadius, cornerRadius, paddleLinePaint);

			// draw the game-level circles and text
			for (int i = 0; i < level.length; ++i)
			{
				if ((i + 1) == gameLevel)
				{
					canvas.drawOval(level[i], levelCurrentFillPaint);
					canvas.drawText(LEVEL_TEXT[i], xLevel[i], yLevelCurrent, levelCurrentTextPaint);
				} else
				{
					canvas.drawOval(level[i], levelFillPaint);
					canvas.drawText(LEVEL_TEXT[i], xLevel[i], yLevel, levelTextPaint);
				}
			}

			// draw the results circles
			for (int i = 0; i < results.length; ++i)
			{
				if (resultsLog[i] == HIT)
					canvas.drawOval(results[i], resultsHitPaint);
				else if (resultsLog[i] == MISS)
					canvas.drawOval(results[i], resultsMissPaint);
				else
					canvas.drawOval(results[i], resultsPaint);
			}

			// draw the time
			canvas.drawText(String.format("%.1f s", (elapsedTime / 1000f)), xTime, yTime, timePaint);

			// draw the ball
			ball.draw(canvas);
		}

		/*
		 * Do the heavy lifting here. Compute the new position of the ball, check if the ball hits a
		 * wall or the paddle, change the ball angle (perhaps), add to the results log, etc.
		 */
		public void updateGame()
		{
			// compute elapsed time and delta time
			final long now = SystemClock.elapsedRealtime();
			deltaTime = now - lastTime;
			lastTime = now;
			elapsedTime = now - startTime;

			// compute new position of ball according to the velocity and angle
			dz = velocity * ((float)deltaTime / 1000) * oneInch; // units: inches
			lastX = x;
			x += dz * (float)Math.sin(angle);
			y += dz * (float)Math.cos(angle);

			/*
			 * Compute the new position of paddle according to the paddle movement mode and the
			 * paddleYRatio. The paddleYRatio is provided by main activity in response to user
			 * tilting the device (tilt mode) or touching and gesturing on the touch strip (touch
			 * mode).
			 */
			if (paddleYRatio >= 0)
			{
				// first, adjust coordinate of paddle bottom
				switch (mode)
				{
				// position control
					case DemoPongActivity.TILT_POSITION:
					case DemoPongActivity.TOUCH_POSITION:
						paddle.bottom = table.bottom - (1f - paddleYRatio)
								* (table.bottom - (table.top + paddleHeight));
						break;

					// velocity control
					case DemoPongActivity.TILT_VELOCITY:
					case DemoPongActivity.TOUCH_VELOCITY:
						float v = (paddleYRatio - 0.5f) * (MAXIMUM_PADDLE_VELOCITY * 2f) * gainVelocityControl
								* screenWidthScalingFactor;
						float dy = v * ((float)deltaTime / 1000) * oneInch;
						paddle.bottom = Math.max(Math.min(table.bottom, paddle.bottom + dy), table.top + paddleHeight);
						break;
				}

				// now, adjust coordinate of paddle top
				paddle.top = paddle.bottom - paddleHeight;
			}

			/*
			 * Increment *actual* paddle movement and check for a reversal in the paddle's movement
			 * direction.
			 * 
			 * The initial if-test is added so as to ignore the movement of the paddle from its
			 * location at the beginning of a block to its first position under user control.
			 */
			if (paddleHasMoved)
			{
				paddleMovementActual += (int)Math.abs(paddle.top - paddleLastTop);

				// check if there was a reversal in the paddle's direction (threshold = 2 pixels)
				if (paddle.top - paddleReversalTop > 2f && paddleMovingUp)
				{
					++paddleReversals;
					paddleReversalTop = paddle.top;
					paddleMovingUp = false;

				} else if (paddle.top - paddleReversalTop < -2f && !paddleMovingUp)
				{
					++paddleReversals;
					paddleReversalTop = paddle.top;
					paddleMovingUp = true;
				}
			}

			/*
			 * Determine when the paddle is first moved after the beginning of a block. When it is
			 * first moved, set the paddleHasMoved flag and store the position of the paddle. The
			 * stored position is used as the starting point in detecting reversals in the direction
			 * of paddle movement (see above).
			 */
			if (Math.abs(paddleLastTop - paddle.top) > 1f)
			{
				paddleHasMoved = true;
				paddleReversalTop = paddle.top;
			}

			// the paddle's current position becomes the last position.
			paddleLastTop = paddle.top;

			/*
			 * NOTE: The labels "left", "right", "top", and "bottom" assume (i) the device is
			 * operated in landscape mode, (ii) ball moves left and right at an angle, and (iii) the
			 * paddle moves up and down along the right edge of the canvas.
			 * 
			 * NOTE: The graphics coordinate system places the point 0,0 at the top-left of the
			 * display with x increasing to the right and y increasing down. Therefore, an angle of
			 * 0 degrees is down (South), an angle of 90 degrees is to the right (East), and so on.
			 * 
			 * There are several checks to do:
			 * 
			 * First, check if the ball hits the left wall. If so, change the direction and angle of
			 * movement.
			 * 
			 * We also inject a bit of randomness by adding an offset to the bounce angle. The range
			 * of the offset increases from 0 degrees at game-level = 0 to 90 degrees at game-level
			 * = 10. The offset is applied in a manner that ensures the return angle is between +45
			 * and -45 degrees from horizontal.
			 * 
			 * We begin by checking if a block of trials is finished. If so, respond accordingly
			 * (see below).
			 */
			if (x - ballRadius < table.left) // hit left wall
			{
				if (idx == trials) // finished a block of trials
				{
					if (gameLevel == DemoPongActivity.TOP_GAME_LEVEL && misses == 0) // VICTORY!
					{
						victorySound.start(); // play the victory TADAAAAAAAA sound clip
						setGameState(VICTORY);
					}

					else if (misses == 0)
						setGameState(WIN);

					else if (misses <= 2)
						setGameState(RETRY);

					else
						setGameState(BACK_ONE_LEVEL);

					// let the host activity know we're done (a results dialog will popup)
					onBlockDoneListener.onBlockDone();

				} else
				{
					if (Math.cos(angle) < 0)
						angle = THREE_PI_OVER_FOUR - angleOffset[idx];
					else
						angle = PI_OVER_FOUR + angleOffset[idx];
				}
				x = table.left + ballRadius;
				crossedPaddleThreshold = false; // to prepare for next approach
			}

			/*
			 * Check if the ball arrives at the x position of the left edge of the paddle (while
			 * moving left to right). This is the place where a "hit" should occur, so it is the
			 * place to add to the variable holding the paddle's minimum movement distance. Of
			 * course, we do an additional check to see of the paddle actually hit the paddle. If
			 * so, log a hit, vibrate, and change the direction and angle of movement. If not, we
			 * just carry on, letting the ball advance toward the right wall.
			 */
			else if (!crossedPaddleThreshold && (x > lastX) && (x + ballRadius) > paddle.left)
			{
				crossedPaddleThreshold = true; // only check once per approach

				// accumulate *minimum* paddle movement
				if (y < paddleLastTopOptimal)
				{
					paddleMovementMin += (int)Math.abs(y - paddleLastTopOptimal);
					paddleLastTopOptimal = y;
				} else if (y > paddleLastTopOptimal + paddleHeight)
				{
					paddleMovementMin += (int)Math.abs(y - (paddleLastTopOptimal + paddleHeight));
					paddleLastTopOptimal = y - paddleHeight;
				}

				// check if the paddle hit the ball
				if ((y + ballRadius) > paddle.top && (y - ballRadius) < paddle.bottom) // "hit"
				{
					resultsLog[idx++] = HIT;
					++hits;
					if (vibrotactileFeedback)
						v.vibrate(10);
					angle = TWO_PI - angle;
					x = paddle.left - ballRadius;
				}
			}

			/*
			 * Check if the ball hits the right wall. If so, log a miss, sound an audio alert
			 * ("miss"), and change the direction and angle of movement.
			 */
			else if (x + ballRadius > table.right) // hit right wall ("miss")
			{
				resultsLog[idx++] = MISS;
				++misses;
				if (auditoryFeedback)
					miss.start();
				angle = TWO_PI - angle;
				x = table.right - ballRadius;
			}

			/*
			 * Check if the ball hits the bottom wall. If so, change the angle of movement.
			 */
			else if (y + ballRadius > table.bottom) // hit bottom wall
			{
				angle = PI - angle;
				y = table.bottom - ballRadius;
			}

			/*
			 * Check if the ball hits the top wall. If so, change the angle of movement.
			 */
			else if (y - ballRadius < table.top) // hit top wall
			{
				angle = PI - angle;
				y = table.top + ballRadius;
			}

			// update the bounding rectangle for the ball
			ball.setBounds((int)(x - ballRadius + 0.5f), (int)(y - ballRadius + 0.5f), (int)(x + ballRadius + 0.5f),
					(int)(y + ballRadius + 0.5f));
		}
	} // end PongThread class definition
}
