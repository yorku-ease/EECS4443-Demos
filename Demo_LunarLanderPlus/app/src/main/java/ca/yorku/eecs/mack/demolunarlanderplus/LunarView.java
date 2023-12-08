package ca.yorku.eecs.mack.demolunarlanderplus;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * View that draws for a simple LunarLander game.
 * 
 * Has a mode which is RUNNING, PAUSED, etc. Has x, y, dx, dy, ... capturing the current ship
 * physics. All x/y, etc., are measured with (0,0) at the lower left. updatePhysics() advances the
 * physics based on realtime. draw() renders the ship, and does an invalidate() to prompt another
 * draw() as soon as possible by the system.
 */
class LunarView extends SurfaceView implements SurfaceHolder.Callback
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	// the application context (used to fetch Drawables, etc.)
	private Context context;

	// the text view to display messages, status info, etc.
	private TextView statusText;

	// pointer to the surface holder (to fetch the canvas to draw into)
	private SurfaceHolder surfaceHolder;

	// the secondary thread that actually draws the animation
	private LunarThread lunarThread;

	// the vibrator simulates the spaceship's "thruster"
	private Vibrator v;

	// bundle to restore game setup and state. May be null.
	private Bundle restoreBundle;

	public LunarView(Context contextArg, AttributeSet attrs)
	{
		super(contextArg, attrs);
		context = contextArg;

		/*
		 * Register our interest in hearing about changes to our surface. For more details, see...
		 * 
		 * see http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
		 */
		surfaceHolder = getHolder();
		surfaceHolder.addCallback(this);

		/*
		 * The lunarThread is instantiated, but not started! This is important because our secondary
		 * thread cannot start drawing until the surface is created. The thread is started in
		 * surfaceCreated (see below).
		 */
		lunarThread = new LunarThread(surfaceHolder, context, new Handler()
		{
			@Override
			public void handleMessage(Message m)
			{
				statusText.setVisibility(m.getData().getInt("viz"));
				statusText.setText(m.getData().getString("text"));
			}
		}, null);
	}

	/**
	 * Fetches the animation thread corresponding to this LunarView.
	 * 
	 * @return the animation thread
	 */
	public LunarThread getThread()
	{
		return lunarThread;
	}

	/**
	 * Standard window-focus override. Notice focus lost so we can pause on focus lost. e.g. user
	 * switches to take a call.
	 */
	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus)
	{
		Log.i(MYDEBUG, "onWindowFocusChanged! (LunarView)");
		if (!hasWindowFocus)
			lunarThread.doPause();
	}

	/**
	 * Installs a pointer to the text view used for messages.
	 */
	public void setTextView(TextView textView)
	{
		statusText = textView;
	}

	public void setVibrator(Vibrator vArg)
	{
		v = vArg;
	}
	
	// -------------------------------------------------------------------------
	// The next three methods are defined in the SurfaceCallback.Holder interface
	// -------------------------------------------------------------------------

	/* Callback invoked when the surface dimensions change. */
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		lunarThread.setSurfaceSize(width, height);
	}

	// the Surface has been created and is ready for drawing
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		Log.i("MYDEBUG", "surfaceCreated! lunarThread=" + lunarThread);

		if (lunarThread == null)
		{
			// recreate the lunarThread if we are resuming
			lunarThread = new LunarThread(surfaceHolder, context, new Handler()
			{
				@Override
				public void handleMessage(Message m)
				{
					statusText.setVisibility(m.getData().getInt("viz"));
					statusText.setText(m.getData().getString("text"));
				}
			}, restoreBundle);
		} else if (restoreBundle != null)
		{
			// this is a configuration change: restore state
			lunarThread.restoreState(restoreBundle);
		}

		lunarThread.setVibrator(v);
		lunarThread.setRunning(true);
		lunarThread.start();
	}

	/*
	 * Callback invoked when the Surface has been destroyed and must no longer be touched. WARNING:
	 * after this method returns, the Surface/Canvas must never be touched again!
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		Log.i(MYDEBUG, "surfaceDestroyed! (LunarView)");
		// tell thread to shut down, then wait for it to finish
		lunarThread.setRunning(false);
		while (lunarThread != null)
		{
			try
			{
				lunarThread.join();
				lunarThread = null;
			} catch (InterruptedException e)
			{
				Log.i(MYDEBUG, "surfaceDestroyed! (LunarView) e=" + e);
			}
		}
	}

	/*
	 * Set the bundle to restore the game setup and state. This method is called from the activity's
	 * onRestart method, which is called when restarting the activity from the stopped state. If the
	 * activity was previously destroyed, onRestart is not called. In this case, the present method
	 * is not called and the restore bundle passed to the LunarThread constructor (see above) will
	 * be null.
	 */
	public void setRestoreBundle(Bundle bArg)
	{
		restoreBundle = bArg;
	}

	/*
	 * As noted in...
	 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview, "Inside
	 * your SurfaceView class is also a good place to define your secondary Thread class, which will
	 * perform all the drawing procedures to your Canvas." Here goes...
	 */
	class LunarThread extends Thread
	{
		// Difficulty setting constants
		static final int DIFFICULTY_EASY = 0;
		static final int DIFFICULTY_HARD = 1;
		static final int DIFFICULTY_MEDIUM = 2;

		// Physics constants
		static final int PHYS_DOWN_ACCEL_SEC = 35;
		static final int PHYS_FIRE_ACCEL_SEC = 80;
		static final int PHYS_FUEL_MAX = 200; // was 100
		static final int PHYS_FUEL_EASY = (int)(0.9f * PHYS_FUEL_MAX);
		static final int PHYS_FUEL_MEDIUM = (int)(0.6f * PHYS_FUEL_MAX);
		static final int PHYS_FUEL_HARD = (int)(0.3f * PHYS_FUEL_MAX);

		static final int PHYS_FUEL_SEC = 10;
		static final int PHYS_SLEW_SEC = 120; // degrees/second rotate
		static final int PHYS_SPEED_HYPERSPACE = 180;
		static final int PHYS_SPEED_INIT = 30;
		static final int PHYS_SPEED_MAX = 120;

		// State-tracking constants
		static final int STATE_LOSE = 1;
		static final int STATE_PAUSE = 2;
		static final int STATE_READY = 3;
		static final int STATE_RUNNING = 4;
		static final int STATE_WIN = 5;

		// Goal condition constants
		static final int TARGET_ANGLE = 18; // > this angle means crash
		static final int TARGET_BOTTOM_PADDING = 17; // px below gear
		static final int TARGET_SPEED = 58; // > this speed means crash (was 28)
		static final double LANDING_PAD_WIDTH_FACTOR = 1.6; // width of target (relative to
																	// lander width)
		static final int LANDING_PAD_HEIGHT = 12; // how high above ground

		// UI constants (i.e., the speed & fuel bars)
		static final int BAR_HEIGHT = 10; // height of the bar(s)
		static final int TEXT_SIZE = 18;
		static final float BAR_WIDTH_FACTOR = 0.2f; // widths of bars (relative to display
															// width)
		static final float HORIZONTAL_GAP_FACTOR = 0.02f; // gap between bars (relative to
																	// display width)
		static final int LANDER_FUEL_GAP = 14; // dead space below plain lander image

		private Vibrator v; // used when engine is fired
		private Bitmap backgroundImage; // drawable to use as the background of the animation canvas
		private Drawable crashedImage; // what to draw for the lander when it crashes
		private Drawable firingImage; // what to draw for the Lander when the engine is firing
		private Drawable landerImage; // what to draw for the lander in its normal state
		private Handler handler; // message handler used by thread to interact with TextView
		private final SurfaceHolder surfaceHolder; // handle to the surface manager object we interact
												// with
		private Paint linePaint; // paint to draw the lines on the the screen
		private Paint fillPaint;
		private Paint fillPaintTooFast; // speed-too-high variant of the line color
		private Paint resultsPaint;
		private Paint landingPadPaint;
		private int canvasHeight = 1; // current height of the surface/canvas
		private int canvasWidth = 1; // current width of the surface/canvas
		private int difficulty; // current difficulty -- amount of fuel, allowed angle, etc. Default
								// is MEDIUM
		private double dx; // velocity dx
		private double dy; // velocity dy
		private boolean engineFiring; // is the engine burning?
		private double fuel; // fuel remaining
		private int goalAngle; // allowed angle
		private int goalSpeed; // allowed speed
		private int landingPadWidth; // width of the landing pad
		private int landingPadX; // x of the landing pad
		private double heading; // Lander heading in degrees, with 0 up, 90 right. Kept in the range
								// 0 to 360
		private int landerHeight; // pixel height of the lander image
		private int landerWidth; // pixel width of the lander image
		private long futureTime; // used to figure out elapsed time between frames
		private long lastTime;
		private int state; // the state of the game. One of READY, RUNNING, PAUSE, LOSE, or WIN.
		private int rotating; // currently rotating: -1 =left, 0 = now, 1 = right
		private boolean run = false; // indicate whether the surface has been created & is ready to
										// draw
		private RectF scratchRect; // scratch rectangle object
		private double x; // x of the lander center
		private double y; // y of the lander center

		//private float pixelDensity;
		private float barWidth, barHeight;
		private float hGap;
		private float landingPadHeight;
		private int totalWins, winsInARow;
		private int totalTries;
		private long elapsedTime;

		//private Bundle restoreBundle;

		LunarThread(SurfaceHolder surfaceHolderArg, Context contextArg, Handler handlerArg,
				Bundle restoreBundleArg)
		{
			float pixelDensity;
			Bundle restoreBundle;

			// Log.i("MYDEBUG", "LunarThread constructor!");
			// get handles to some important objects
			surfaceHolder = surfaceHolderArg;
			handler = handlerArg;
			context = contextArg;
			restoreBundle = restoreBundleArg;

			// get the resources instance for this package
			Resources r = contextArg.getResources();

			// get images
			landerImage = r.getDrawable(R.drawable.lander_plain);
			firingImage = r.getDrawable(R.drawable.lander_firing);
			crashedImage = r.getDrawable(R.drawable.lander_crashed);

			// load background image as a Bitmap instead of a Drawable b/c
			// we don't need to transform it and it's faster to draw this way
			backgroundImage = BitmapFactory.decodeResource(r, R.drawable.earthrise);

			// Initialize paints
			linePaint = new Paint();
			linePaint.setAntiAlias(true);
			linePaint.setStrokeWidth(0);
			linePaint.setStyle(Paint.Style.STROKE);
			linePaint.setARGB(255, 0, 255, 0);

			fillPaint = new Paint();
			fillPaint.setAntiAlias(true);
			fillPaint.setStyle(Paint.Style.FILL);
			fillPaint.setARGB(255, 0, 255, 0);

			fillPaintTooFast = new Paint();
			fillPaintTooFast.setAntiAlias(true);
			fillPaintTooFast.setARGB(255, 222, 0, 0);

			resultsPaint = new Paint();
			resultsPaint.setColor(0xffffffff);
			resultsPaint.setTextSize(TEXT_SIZE);

			landingPadPaint = new Paint();
			landingPadPaint.setAntiAlias(true);
			landingPadPaint.setStrokeWidth(LANDING_PAD_HEIGHT);
			landingPadPaint.setStyle(Paint.Style.STROKE);
			landingPadPaint.setARGB(255, 153, 51, 0);

			pixelDensity = r.getDisplayMetrics().density;
			barHeight = BAR_HEIGHT * pixelDensity;
			landingPadHeight = LANDING_PAD_HEIGHT * pixelDensity;
			landingPadPaint.setStrokeWidth(landingPadHeight);
			resultsPaint.setTextSize(barHeight);

			scratchRect = new RectF(0, 0, 0, 0);

			totalWins = 0;
			totalTries = 0;
			elapsedTime = 0;

			if (restoreBundle == null) // use default setup and state
			{
				setState(STATE_READY, null);
				difficulty = DIFFICULTY_MEDIUM;
				landerWidth = landerImage.getIntrinsicWidth();
				landerHeight = landerImage.getIntrinsicHeight();
				landingPadX = 10;
				landingPadWidth = (int)(landerWidth * LANDING_PAD_WIDTH_FACTOR);
				x = landingPadX + landingPadWidth / 2;
				y = LANDING_PAD_HEIGHT + landerHeight / 2 - LANDER_FUEL_GAP;
				dx = 0;
				dy = 0;
				heading = 0;
				winsInARow = 0;
				fuel = PHYS_FUEL_MEDIUM;
				totalWins = 0;
				totalTries = 0;
				elapsedTime = 0;
			} else
				// use saved setup and state info
				restoreState(restoreBundle);
		}

		/**
		 * Starts the game, setting parameters for the current difficulty.
		 */
		void doStart()
		{
			synchronized (surfaceHolder)
			{
				lastTime = SystemClock.elapsedRealtime();

				// First set the game for Medium difficulty
				fuel = PHYS_FUEL_MEDIUM;
				engineFiring = false;
				landingPadWidth = (int)(landerWidth * LANDING_PAD_WIDTH_FACTOR);
				goalSpeed = TARGET_SPEED;
				goalAngle = TARGET_ANGLE;
				int speedInit = PHYS_SPEED_INIT;
				elapsedTime = 0;

				// Adjust difficulty parameters for EASY/HARD
				if (difficulty == DIFFICULTY_EASY)
				{
					fuel = PHYS_FUEL_EASY;
					landingPadWidth = landingPadWidth * 4 / 3;
					goalSpeed = goalSpeed * 3 / 2;
					goalAngle = goalAngle * 4 / 3;
					speedInit = speedInit * 3 / 4;
				} else if (difficulty == DIFFICULTY_HARD)
				{
					fuel = PHYS_FUEL_HARD;
					landingPadWidth = landingPadWidth * 3 / 4;
					goalSpeed = goalSpeed * 7 / 8;
					speedInit = speedInit * 4 / 3;
				}

				// pick a convenient initial location for the lander sprite
				x = canvasWidth / 2;
				y = canvasHeight - landerHeight / 2;

				// start with a little random motion
				dy = Math.random() * -speedInit;
				dx = Math.random() * 2 * speedInit - speedInit;
				heading = 0;

				// Figure initial spot for landing, not too near center
				while (true)
				{
					landingPadX = (int)(Math.random() * (canvasWidth - landingPadWidth));
					if (Math.abs(landingPadX - (x - landerWidth / 2)) > canvasHeight / 6.0)
						break;
				}

				futureTime = SystemClock.elapsedRealtime() + 100;
				setState(STATE_RUNNING, null);
			}
		}

		/**
		 * Pauses the physics update & animation.
		 */
		void doPause()
		{
			synchronized (surfaceHolder)
			{
				if (state == STATE_RUNNING)
					setState(STATE_PAUSE, null);
			}
		}

		@Override
		public void run()
		{
			while (run)
			{
				// this will terminate the thread if the Exit option is selected in the menu
				if (isInterrupted())
					return;

				Canvas c = null;
				try
				{
					// See...
					// http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
					c = surfaceHolder.lockCanvas(null);
					synchronized (surfaceHolder)
					{
						if (state == STATE_RUNNING)
							updatePhysics();

						if (c != null)
							doDraw(c); // draw onto the SurfaceView's canvas
					}
				} finally
				{
					/*
					 * Do this in a finally so that if an exception is thrown during the above, we
					 * don't leave the Surface in an inconsistent state.
					 */
					if (c != null)
					{
						// give the canvas back to the SurfaceView (where it is presented on the
						// display)
						surfaceHolder.unlockCanvasAndPost(c);
					}
				}
			}
		}

		/**
		 * Restores game state from the indicated Bundle. Called when the Activity is being restored
		 * after having been previously stopped but not destroyed.
		 * 
		 * @param b
		 *            Bundle containing the game state
		 */
		synchronized void restoreState(Bundle b)
		{
			synchronized (surfaceHolder)
			{
				rotating = 0;
				engineFiring = false;
				int tempState = b.getInt("state");
				if (tempState == STATE_RUNNING)
					tempState = STATE_PAUSE;
				setState(tempState, null);
				difficulty = b.getInt("difficulty");
				x = b.getDouble("x");
				y = b.getDouble("y");
				dx = b.getDouble("dx");
				dy = b.getDouble("dy");
				heading = b.getDouble("heading");
				landerWidth = b.getInt("landerWidth");
				landerHeight = b.getInt("landerHeight");
				landingPadX = b.getInt("goalX");
				goalSpeed = b.getInt("goalSpeed");
				goalAngle = b.getInt("goalAngle");
				landingPadWidth = b.getInt("goalWidth");
				winsInARow = b.getInt("winsInARow");
				fuel = b.getDouble("fuel");
				totalWins = b.getInt("totalWins");
				totalTries = b.getInt("totalTries");
				elapsedTime = b.getLong("elapsedTime");
			}
		}

		/**
		 * Dump game state to the provided Bundle. Typically called when the Activity is being
		 * suspended.
		 * 
		 * @return Bundle with this view's state
		 */
		synchronized Bundle saveState(Bundle b)
		{
			synchronized (surfaceHolder)
			{
				if (b != null)
				{
					b.putInt("state", state);
					b.putInt("difficulty", difficulty);
					b.putDouble("x", x);
					b.putDouble("y", y);
					b.putDouble("dx", dx);
					b.putDouble("dy", dy);
					b.putDouble("heading", heading);
					b.putInt("landerWidth", landerWidth);
					b.putInt("landerHeight", landerHeight);
					b.putInt("goalX", landingPadX);
					b.putInt("goalSpeed", goalSpeed);
					b.putInt("goalAngle", goalAngle);
					b.putInt("goalWidth", landingPadWidth);
					b.putInt("winsInARow", winsInARow);
					b.putDouble("fuel", fuel);
					b.putInt("totalWins", totalWins);
					b.putInt("totalTries", totalTries);
					b.putLong("elapsedTime", elapsedTime);
				}
			}
			return b;
		}

		/**
		 * Sets the current difficulty.
		 * 
		 * @param difficultyArg -- the difficulty!
		 */
		void setDifficulty(int difficultyArg)
		{
			synchronized (surfaceHolder)
			{
				difficulty = difficultyArg;
			}
		}

		/**
		 * Sets if the engine is currently firing.
		 */
		void setFiring(boolean firing)
		{
			if (firing)
				doVibrate();
			else
				v.cancel();

			synchronized (surfaceHolder)
			{
				engineFiring = firing;
			}
		}

		private void doVibrate()
		{
			final long[] PATTERN = { 0, 10, 50 };
			v.vibrate(PATTERN, 0);
			//v.vibrate(50);
		}

		void setVibrator(Vibrator vArg)
		{
			v = vArg;
		}

		/**
		 * Used to signal the thread whether it should be running or not. Passing true allows the
		 * thread to run; passing false will shut it down if it's already running. Calling start()
		 * after this was most recently called with false will result in an immediate shutdown.
		 * 
		 * @param b
		 *            true to run, false to shut down
		 */
		void setRunning(boolean b)
		{
			run = b;
		}

		/**
		 * Sets the game mode. That is, whether we are running, paused, in the failure state, in the
		 * victory state, etc.
		 * 
		 * @param modeArg
		 *            one of the STATE_* constants
		 * @param message
		 *            string to add to screen or null
		 */
		void setState(int modeArg, CharSequence message)
		{
			/*
			 * This method optionally can cause a text message to be displayed to the user when the
			 * mode changes. Since the View that actually renders that text is part of the main View
			 * hierarchy and not owned by this thread, we can't touch the state of that View.
			 * Instead we use a Message + Handler to relay commands to the main thread, which
			 * updates the user-text View.
			 */
			synchronized (surfaceHolder)
			{
				state = modeArg;

				if (state == STATE_RUNNING)
				{
					Message msg = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", "");
					b.putInt("viz", View.INVISIBLE);
					msg.setData(b);
					handler.sendMessage(msg);
				} else
				{
					rotating = 0;
					engineFiring = false;
					Resources res = context.getResources();
					CharSequence str = "";
					if (state == STATE_READY)
					{
						str = res.getText(R.string.mode_ready);
					} else if (state == STATE_PAUSE)
					{
						str = res.getText(R.string.mode_pause);
					} else if (state == STATE_LOSE)
					{
						str = res.getText(R.string.mode_lose);
						winsInARow = 0;
					} else if (state == STATE_WIN)
					{
						str = res.getString(R.string.mode_win_prefix) + winsInARow + " "
								+ res.getString(R.string.mode_win_suffix);
					}

					if (message != null)
					{
						str = message + "\n" + str;
					}

					Message msg = handler.obtainMessage();
					Bundle b = new Bundle();
					b.putString("text", str.toString());
					b.putInt("viz", View.VISIBLE);
					msg.setData(b);
					handler.sendMessage(msg);
				}
			}
		}

		// Called when the surface dimensions change.
		void setSurfaceSize(int width, int height)
		{
			// synchronized to make sure these all change atomically
			synchronized (surfaceHolder)
			{
				canvasWidth = width;
				canvasHeight = height;
				barWidth = BAR_WIDTH_FACTOR * canvasWidth;
				hGap = HORIZONTAL_GAP_FACTOR * canvasWidth;

				// don't forget to resize the background image
				backgroundImage = Bitmap.createScaledBitmap(backgroundImage, width, height, true);
			}
		}

		// Resumes from a pause
		void doUnpause()
		{
			// Move the real time clock up to now
			synchronized (surfaceHolder)
			{
				futureTime = SystemClock.elapsedRealtime() + 100;
				lastTime = SystemClock.elapsedRealtime();
			}
			setState(STATE_RUNNING, null);
		}

		/**
		 * Handles a touch button event (triggered by ACTION_DOWN or ACTION_POINTER_DOWN).
		 * 
		 * @param buttonStatus
		 *            the button pad key that triggered the event
		 */
		@SuppressWarnings("UnusedReturnValue")
		boolean doKeyDown(int buttonStatus)
		{
			synchronized (surfaceHolder)
			{
				boolean okToStart = false;
				if (buttonStatus == ButtonPad.UP)
					okToStart = true;

				if (okToStart && (state == STATE_READY || state == STATE_LOSE || state == STATE_WIN))
				{
					doStart();
					return true;
				} else if (okToStart && state == STATE_PAUSE)
				{
					doUnpause();
					return true;
				} else if ((state == STATE_PAUSE || state == STATE_WIN || state == STATE_LOSE)
						&& buttonStatus == ButtonPad.DOWN)
				{
					this.interrupt();
					return true;
				} else if (state == STATE_RUNNING)
				{
					if (buttonStatus == ButtonPad.CENTER && fuel > 0) // center --> fire engines
					{
						setFiring(true);
						return true;
					} else if (buttonStatus == ButtonPad.LEFT) // left --> rotate counter clockwise
					{
						rotating = -1;
						return true;
					} else if (buttonStatus == ButtonPad.RIGHT) // right --> rotate clockwise
					{
						rotating = 1;
						return true;
					} else if (buttonStatus == ButtonPad.UP) // up --> pause
					{
						doPause();
						return true;
					}
				}
				return false;
			}
		}

		/**
		 * Handles a button pad event (triggered by ACTION_UP or ACTION_POINTER_UP).
		 * 
		 * @param buttonStatus
		 *            the button pad key that triggered the event
		 * @return true if the event was handled
		 */
		@SuppressWarnings("UnusedReturnValue")
		boolean doKeyUp(int buttonStatus)
		{
			boolean handled = false;
			synchronized (surfaceHolder)
			{
				if (buttonStatus == ButtonPad.CENTER)
				{
					setFiring(false);
					handled = true;
				} else if (buttonStatus == ButtonPad.LEFT || buttonStatus == ButtonPad.RIGHT)
				{
					rotating = 0;
					handled = true;
				}
			}
			return handled;
		}

		/**
		 * Draws the ship, fuel/speed bars, and background to the provided Canvas.
		 */
		private void doDraw(Canvas canvas)
		{
			// Draw the background image. Operations on the Canvas accumulate
			// so this is like clearing the screen.
			canvas.drawBitmap(backgroundImage, 0, 0, null);

			int yTop = canvasHeight - ((int)y + landerHeight / 2);
			int xLeft = (int)x - landerWidth / 2;

			// draw the fuel gauge
			scratchRect.set(hGap, 4, hGap + barWidth, 4 + barHeight);
			canvas.drawRect(scratchRect, linePaint);

			// draw the fuel level
			int fuelWidth = (int)(barWidth * fuel / PHYS_FUEL_MAX);
			scratchRect.set(hGap, 4, hGap + fuelWidth, 4 + barHeight);
			canvas.drawRect(scratchRect, fillPaint);

			// draw fuel gauge label
			canvas.drawText("Fuel", hGap, 4 + barHeight + TEXT_SIZE, resultsPaint);

			// draw the speed gauge, with a two-tone effect
			double speed = Math.sqrt(dx * dx + dy * dy);
			int speedWidth = (int)(barWidth * speed / PHYS_SPEED_MAX);
			if (speed <= goalSpeed)
			{
				scratchRect.set(hGap + barWidth + hGap, 4, hGap + barWidth + hGap + speedWidth, 4 + barHeight);
				canvas.drawRect(scratchRect, fillPaint);
			} else
			// too fast (draw two parts: desired + excess)
			{
				// Draw the excess speed color in red, with the desired speed in front of it
				scratchRect.set(hGap + barWidth + hGap, 4, hGap + barWidth + hGap + speedWidth, 4 + barHeight);
				canvas.drawRect(scratchRect, fillPaintTooFast);
				int goalWidth = (int)(barWidth * goalSpeed / PHYS_SPEED_MAX);
				scratchRect.set(hGap + barWidth + hGap, 4, hGap + barWidth + hGap + goalWidth, 4 + barHeight);
				canvas.drawRect(scratchRect, fillPaint);
			}
			canvas.drawText("Speed", hGap + barWidth + hGap, 4 + barHeight + TEXT_SIZE, resultsPaint);

			// wins
			canvas.drawText("" + totalWins, 0.70f * canvasWidth, 4 + TEXT_SIZE, resultsPaint);
			canvas.drawText("Wins", 0.70f * canvasWidth, 4 + barHeight + TEXT_SIZE, resultsPaint);

			// tries
			canvas.drawText("" + totalTries, 0.80f * canvasWidth, 4 + TEXT_SIZE, resultsPaint);
			canvas.drawText("Tries", 0.80f * canvasWidth, 4 + barHeight + TEXT_SIZE, resultsPaint);

			// time
			canvas.drawText(timeString(elapsedTime), 0.90f * canvasWidth, 4 + TEXT_SIZE, resultsPaint);
			canvas.drawText("Time", 0.90f * canvasWidth, 4 + barHeight + TEXT_SIZE, resultsPaint);

			// Draw the landing pad
			canvas.drawLine(landingPadX, canvasHeight - landingPadHeight / 2, landingPadX + landingPadWidth,
					canvasHeight - landingPadHeight / 2, landingPadPaint);

			// Draw the ship with its current rotation
			canvas.save();
			canvas.rotate((float)heading, (float)x, canvasHeight - (float)y);
			if (state == STATE_LOSE)
			{
				crashedImage.setBounds(xLeft, yTop, xLeft + landerWidth, yTop + landerHeight);
				crashedImage.draw(canvas);
			} else if (engineFiring)
			{
				firingImage.setBounds(xLeft, yTop, xLeft + landerWidth, yTop + landerHeight);
				firingImage.draw(canvas);
			} else
			{
				landerImage.setBounds(xLeft, yTop, xLeft + landerWidth, yTop + landerHeight);
				landerImage.draw(canvas);
			}
			canvas.restore();
		}

		/**
		 * Format the time as a string: m:ss.t (minutes, seconds, tenths of seconds)
		 * 
		 * @param elapsedTime - elapsed time
		 * @return the time as a string
		 */
		private String timeString(long elapsedTime)
		{
			StringBuilder time = new StringBuilder();
			int minutes = (int)(elapsedTime / 1000) / 60;
			int seconds = (int)(elapsedTime / 1000) - (minutes * 60);
			int tenths = (int)(elapsedTime / 10) % 10;
			time.append(minutes).append(":");
			if (seconds < 10)
				time.append("0").append(seconds);
			else
				time.append(seconds);
			time.append(".").append(tenths);
			return time.toString();
		}

		/**
		 * Configures the lander state (x, y, fuel, ...) based on the passage of realtime. Does not
		 * invalidate(). Called at the start of draw(). Detects the end-of-game and sets the UI to
		 * the next state.
		 */
		private void updatePhysics()
		{
			long now = SystemClock.elapsedRealtime();
			elapsedTime += now - lastTime;
			lastTime = now;

			/*
			 * Do nothing if futureTime is in the future. This allows the game-start to delay the
			 * start of the physics by 100ms or whatever.
			 */
			if (futureTime > now)
				return;

			double elapsed = (now - futureTime) / 1000.0;

			// rotating -- update heading
			if (rotating != 0)
			{
				heading += rotating * (PHYS_SLEW_SEC * elapsed);

				// Bring things back into the range 0..360
				if (heading < 0)
					heading += 360;
				else if (heading >= 360)
					heading -= 360;
			}

			// Base accelerations -- 0 for x, gravity for y
			double ddx = 0.0;
			double ddy = -PHYS_DOWN_ACCEL_SEC * elapsed;

			if (engineFiring)
			{
				// taking 0 as up, 90 as to the right
				// cos(deg) is ddy component, sin(deg) is ddx component
				double elapsedFiring = elapsed;
				double fuelUsed = elapsedFiring * PHYS_FUEL_SEC;

				// tricky case where we run out of fuel part way through the elapsed
				if (fuelUsed > fuel)
				{
					elapsedFiring = fuel / fuelUsed * elapsed;
					fuelUsed = fuel;

					// Oddball case where we adjust the "control" from here
					engineFiring = false;
				}

				fuel -= fuelUsed;

				if (fuel <= 0)
					v.cancel();

				// have this much acceleration from the engine
				double accel = PHYS_FIRE_ACCEL_SEC * elapsedFiring;

				double radians = 2 * Math.PI * heading / 360;
				ddx = Math.sin(radians) * accel;
				ddy += Math.cos(radians) * accel;
			}

			double dxOld = dx;
			double dyOld = dy;

			// figure speeds for the end of the period
			dx += ddx;
			dy += ddy;

			// figure position based on average speed during the period
			x += elapsed * (dx + dxOld) / 2;
			y += elapsed * (dy + dyOld) / 2;

			futureTime = now;

			// Evaluate if we have landed ... stop the game
			double yLowerBound = LANDING_PAD_HEIGHT + landerHeight / 2 - TARGET_BOTTOM_PADDING;
			CharSequence message = "";
			Resources res = context.getResources();
			if (y <= yLowerBound)
			{
				y = yLowerBound;

				int result = STATE_LOSE;
				double speed = Math.sqrt(dx * dx + dy * dy);
				boolean onGoal = (landingPadX <= x - landerWidth / 2 && x + landerWidth / 2 <= landingPadX
						+ landingPadWidth);

				// "Hyperspace" win -- upside down, going fast, puts you back at the top.
				if (onGoal && Math.abs(heading - 180) < goalAngle && speed > PHYS_SPEED_HYPERSPACE)
				{
					result = STATE_WIN;
					winsInARow++;
					++totalWins;
					++totalTries;
					doStart();

					return;
					// Oddball case: this case does a return, all other cases fall through to
					// setMode() below.
				} else if (!onGoal)
				{
					message = res.getText(R.string.message_off_pad);
				} else if (!(heading <= goalAngle || heading >= 360 - goalAngle))
				{
					message = res.getText(R.string.message_bad_angle);
				} else if (speed > goalSpeed)
				{
					message = res.getText(R.string.message_too_fast);
				} else
				{
					result = STATE_WIN;
					winsInARow++;
					++totalWins;
				}
				++totalTries;
				setState(result, message);
			}
			// added (29/3/2013) to check if the lander has ventured off the canvas
			else if (x + landerWidth / 2 < 0 || x - landerWidth / 2 > canvasWidth)
			{
				message = res.getText(R.string.message_off_screen);
				++totalTries;
				setState(STATE_LOSE, message);
			}
		} // end updatePhysics
	} // end LunarThread class definition
}