package ca.yorku.eecs.mack.demosurfaceview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import androidx.core.content.res.ResourcesCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.Locale;

public class MySurfaceView extends SurfaceView implements SurfaceHolder.Callback
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	SurfaceHolder surfaceHolder;
	MyThread myThread;
	int refreshCount;

	/*
	 * The following three variables are treated as "state variables". Their values are retained if
	 * the device undergoes a configuration change (e.g., the screen is rotated) or the application
	 * is paused or stopped (but not destroyed), then restarted.
	 */
	long elapsedTime;
	float velocity;
	int wallHits;

	public MySurfaceView(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		/*
		 * Register our interest in hearing about changes to our surface. See...
		 * 
		 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
		 */
		surfaceHolder = this.getHolder();
		surfaceHolder.addCallback(this);

		/*
		 * Instantiate the secondary thread. But, do *not* start the thread! This is important
		 * because the secondary thread cannot touch the surface until it is created. The thread is
		 * started in surfaceCreated (see below).
		 * 
		 * The 3rd argument is false, indicating this is *not* a restart of the application.
		 */
		myThread = new MyThread(surfaceHolder, this.getContext(), false);
	}

	/*
	 * The ball velocity is updated from the main activity if user adjusts the UI's slider. The main
	 * activity calls this method to pass on the updated velocity to MySurfaceView. With this, the
	 * updated velocity is available to the secondary thread that uses the game's variables to
	 * update the game.
	 */
	public void setVelocity(float velocityArg)
	{
		velocity = velocityArg;
	}

	// =======================================================================
	// The next three methods are the callbacks as per SurfaceHolder.Callback.
	// =======================================================================

	// callback when the surface dimensions change
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
	{
		// give the width and height of the SurfaceView to the thread
		myThread.setSurfaceSize(width, height);
	}

	/*
	 * Callback when the surface has been created and is ready to be used. This is the the point
	 * where the canvas is available and drawing can begin. Thus, it is here that the secondary
	 * thread is started.
	 */
	@Override
	public void surfaceCreated(SurfaceHolder holder)
	{
		/*
		 * If myThread is null, the constructor was not executed. This will occur if the application
		 * is restarting. In this case, we instantiate a new tread, passing true as the third
		 * argument.
		 */
		if (myThread == null)
		{
			myThread = new MyThread(surfaceHolder, this.getContext(), true);
		}

		myThread.status = MyThread.STARTED;
		myThread.start(); // begin drawing operations on the surface's canvas
	}

	/*
	 * Callback invoked when the surface has been destroyed and must no longer be touched. The
	 * imperative of shutting down the thread here is noted in the SurfaceView API Reference: "You
	 * must ensure that the drawing thread only touches the underlying Surface while it is valid --
	 * between SurfaceHolder.Callback.surfaceCreated() and
	 * SurfaceHolder.Callback.surfaceDestroyed()." See...
	 * 
	 * http://developer.android.com/reference/android/view/SurfaceView.html
	 */
	@Override
	public void surfaceDestroyed(SurfaceHolder holder)
	{
		// tell the thread to shut down, then wait for it to finish
		myThread.status = MyThread.SHUT_DOWN;
		while (myThread != null)
		{
			try
			{
				myThread.join(); // kill the thread (but be patient)
				myThread = null;
			} catch (InterruptedException e)
			{
				Log.i(MYDEBUG, "InterruptedException e=" + e.toString());
			}
		}
	}

	// ====================
	// Inner classes at end
	// ====================

	/*
	 * ============================================================================================
	 * As noted in the API Guide, "Inside your SurfaceView class is also a good place to define your
	 * secondary Thread class, which will perform all the drawing procedures to your Canvas." That's
	 * the approach used here. See...
	 * 
	 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
	 */
	class MyThread extends Thread
	{
		final static int DEFAULT_BALL_DIAMETER = 50; // 5/16 inch in dp units
		final static float PI = (float)Math.PI;
		final static float THREE_PI_OVER_FOUR = 3f * (float)Math.PI / 4f;
		final static float TWO_PI = 2f * (float)Math.PI;
		final static int DEFAULT_VELOCITY = 2; // inches per second
		final static int MAXIMUM_VELOCITY = 10; // inches per second
		final static int DEFAULT_ROTATION_VELOCITY = 360; // degrees per second
		final static int TEXT_SIZE = 12;

		// int constants for thread status
		final static int STARTED = 100;
		final static int SHUT_DOWN = 200;

		int width, height;
		long deltaTime, lastTime;
		float dz, rotationVelocity, oneInch, lastX, xBall, yBall, angle, ballRadius;
		float leftWall, rightWall, topWall, bottomWall, rotationAngle, deltaAngle, rotationSign, pixelDensity;

		/*
		 * This status variable is used to tell the thread whether or not it should be running. If
		 * status is RUNNING, the thread is allowed to run. If status is PAUSED, the thread will
		 * shut down.
		 * 
		 * The state variable is only changed in the SurfaceHolder callback methods. It is set to
		 * RUNNING in surfaceCreated and set to PAUSED in surfaceDestroyed.
		 * 
		 * The state variable is tested in the thread's run method as a pre-condition for each cycle
		 * of compute and draw.
		 */
		int status;

		Paint textPaint;
		Drawable happyFaceBall;
		final SurfaceHolder surfaceHolder; // handle to the surface manager object we interact with

		private MyThread(SurfaceHolder surfaceHolderArg, Context context, boolean restart)
		{
			surfaceHolder = surfaceHolderArg;

			// get ball image from an app resource
			//happyFaceBall = context.getResources().getDrawable(R.drawable.happy_face_ball); // deprecated in API 22
			happyFaceBall = ResourcesCompat.getDrawable(getResources(), R.drawable.happy_face_ball, null);

			pixelDensity = context.getResources().getDisplayMetrics().density;
			ballRadius = DEFAULT_BALL_DIAMETER / 2f * pixelDensity;

			oneInch = 160f * pixelDensity;
			rotationVelocity = DEFAULT_ROTATION_VELOCITY;
			rotationAngle = 0f;
			rotationSign = 1f; // always +1 or -1

			textPaint = new Paint();
			textPaint.setAntiAlias(true);
			textPaint.setColor(Color.WHITE);
			textPaint.setTextSize(TEXT_SIZE * pixelDensity);
			textPaint.setTextAlign(Paint.Align.LEFT);

			angle = -THREE_PI_OVER_FOUR;
			xBall = 100f;
			yBall = 100f;

			// initialize the state variables, provided we are *not* restarting the app
			if (!restart)
			{
				elapsedTime = 0;
				wallHits = 0;
				velocity = DEFAULT_VELOCITY;
			}
		}

		/*
		 * Called from surfaceChanged; i.e., just before drawing begins. Initialize variables for
		 * the display size, since these are needed to compute the ball's movement and position.
		 * 
		 * We also initialize lastTime, thus ensuring the 1st calculation of deltaTime is correct
		 * (see updateGame).
		 */
		private void setSurfaceSize(int widthArg, int heightArg)
		{
			width = widthArg;
			height = heightArg;

			leftWall = 0f;
			topWall = 0f;
			rightWall = width;
			bottomWall = height;

			lastTime = SystemClock.elapsedRealtime();
		}

		/*
		 * The thread updates the UI as fast as possible. This occurs through continuous cycles of
		 * compute-draw. It is possible to have the UI update at a set rate, such as every 100 ms.
		 * To do this, we need cycles of compute-draw-sleep. For an example of code that works in
		 * this manner, see...
		 * 
		 * http://blorb.tumblr.com/post/236799414/simple-java-android-game-loop
		 */

		@Override
		public void run()
		{
			while (status == STARTED)
			{
				/*
				 * The organization of code here roughly follows the recommendations in the Android
				 * API Guide topic "On a SurfaceView". See...
				 * 
				 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
				 */
				Canvas c = null;
				try
				{
					c = surfaceHolder.lockCanvas(); // *** 1. LOCK ***
					synchronized (surfaceHolder)
					{
						updateGame(); // *** 2. COMPUTE ***
						if (c != null)
							drawGame(c); // *** 3. DRAW ***
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
						surfaceHolder.unlockCanvasAndPost(c); // *** 4. UNLOCK ***
					}
				}
			}
		}

		// DRAW - onto the canvas provided by the surface holder (see run method)
		private void drawGame(Canvas canvas)
		{
			/*
			 * Begin by re-painting the entire canvas. Discussion on why this is necessary is given
			 * in the API Guide topic "On a SurfaceView". See...
			 * 
			 * http://developer.android.com/guide/topics/graphics/2d-graphics.html#on-surfaceview
			 */
			canvas.drawColor(0xff000000); // black

			// draw the text strings displaying the values of the apps state variables
			canvas.drawText(String.format(Locale.CANADA, "Ball velocity (inches/second) = %4.1f", velocity), 10,
					height - 10,
					textPaint);
			canvas.drawText(String.format(Locale.CANADA, "Wall hits = %d", wallHits), 10,
					height - 10 - textPaint.getTextSize() * 1.5f, textPaint);
			canvas.drawText(String.format(Locale.CANADA, "Elapsed time (seconds) = %.1f", (float)(elapsedTime / 1000f)
			), 10,
					height
					- 10 - 2f * textPaint.getTextSize() * 1.5f, textPaint);

			/*
			 * Ball rotation is achieved by rotating the canvas about the center of the ball and
			 * then drawing the ball.
			 */
			canvas.rotate(rotationAngle, xBall, yBall);
			happyFaceBall.draw(canvas);

			/*
			 * Increment a display refresh counter. The value is displayed in the LogCat window,
			 * thus giving an indication of the refresh interval (which is about 60 ms on a Nexus
			 * 4).
			 */
			++refreshCount;
		}

		// COMPUTE - new values of game variables, etc. (called from run; see above)
		private void updateGame()
		{
			/*
			 * Compute the delta time since we were last here. The delta time is needed to compute
			 * the amount of translation and rotation for the ball. These should depend only on
			 * their respective velocity parameters.
			 * 
			 * Note: The delta time is computed using SystemClock.elapsedRealtime, rather than the
			 * traditional Java method System.currentTimeMillis. This is recommended in the Android
			 * API Reference for SystemClock. See...
			 * 
			 * http://developer.android.com/reference/android/os/SystemClock.html
			 */

			long now = SystemClock.elapsedRealtime();
			deltaTime = now - lastTime;
			lastTime = now;

			// compute the elapsed time since the app was launched
			elapsedTime += deltaTime;

			// compute new position of ball according to the velocity and angle
			dz = velocity * (deltaTime / 1000f) * oneInch; // inches per second
			lastX = xBall;
			xBall += dz * (float)Math.sin(angle);
			yBall += dz * (float)Math.cos(angle);

			/*
			 * Check if the ball has reached one of the edges of the view. If so, change the angle
			 * of movement, adjust the position of the ball (so it is exactly at the edge),
			 * increment a wall-hits counter, and change the direction of ball rotation.
			 */

			// left wall check
			if (xBall - ballRadius < leftWall) // hit left wall
			{
				angle = TWO_PI - angle;
				xBall = leftWall + ballRadius;
				++wallHits;
				rotationSign *= -1f;

				// corner checks
				if (yBall - ballRadius < topWall)
					yBall = topWall + ballRadius;
				else if (yBall + ballRadius > bottomWall)
					yBall = bottomWall - ballRadius;
			}

			// right wall check
			else if (xBall + ballRadius > rightWall) // hit right wall
			{
				angle = TWO_PI - angle;
				xBall = rightWall - ballRadius;
				++wallHits;
				rotationSign *= -1f;

				// corner checks
				if (yBall - ballRadius < topWall)
					yBall = topWall + ballRadius;
				else if (yBall + ballRadius > bottomWall)
					yBall = bottomWall - ballRadius;
			}

			// bottom wall check
			else if (yBall + ballRadius > bottomWall) // hit bottom wall
			{
				angle = PI - angle;
				yBall = bottomWall - ballRadius;
				++wallHits;
				rotationSign *= -1f;

				// corner checks
				if (xBall - ballRadius < leftWall)
					xBall = leftWall + ballRadius;
				else if (xBall + ballRadius > rightWall)
					xBall = rightWall - ballRadius;
			}

			// top wall check
			else if (yBall - ballRadius < topWall) // hit top wall
			{
				angle = PI - angle;
				yBall = topWall + ballRadius;
				++wallHits;
				rotationSign *= -1f;

				// corner checks
				if (xBall - ballRadius < leftWall)
					xBall = leftWall + ballRadius;
				else if (xBall + ballRadius > rightWall)
					xBall = rightWall - ballRadius;
			}

			// compute the new rotation of the ball
			deltaAngle = rotationVelocity * (deltaTime / 1000f) * rotationSign;
			rotationAngle += deltaAngle;

			// update the ball's bounding rectangle
			happyFaceBall.setBounds((int)(xBall - ballRadius + 0.5f), (int)(yBall - ballRadius + 0.5f), (int)(xBall
					+ ballRadius + 0.5f), (int)(yBall + ballRadius + 0.5f));
		}
	} // end MyThread
}
