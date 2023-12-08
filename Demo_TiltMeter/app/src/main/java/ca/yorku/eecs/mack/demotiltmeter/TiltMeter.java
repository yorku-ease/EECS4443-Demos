package ca.yorku.eecs.mack.demotiltmeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import java.util.Locale;
import android.util.Log;

/**
 * TiltMeter - implementation of a View to display the magnitude and direction of tilt.
 *
 * @author Scott MacKenzie
 */
public class TiltMeter extends View
{
    final String MYDEBUG = "MYDEBUG"; // for Log.i messages

    final float MAX_MAGNITUDE = 45f; // maximum tilt angle to demo
    final int DEFAULT_LABEL_TEXT_SIZE = 25; // fiddle as necessary
    final int DEFAULT_STATS_TEXT_SIZE = 15;
    final int DEFAULT_NEEDLE_STROKE_WIDTH = 10;
    final int DEFAULT_CIRCLE_STROKE_WIDTH = 3;
    final int DEFAULT_GAP = 7; // between lines
    final int DEFAULT_OFFSET = 10; // from bottom of display
    final int NEEDLE_COLOR = Color.RED;

    int width, height;
    float pixelDensity;
    int labelTextSize, statsTextSize, dialStrokeWidth, circleStrokeWidth, gap, offset;
    float tiltAngle, tiltMagnitude, pitch, roll;
    float xCircle, yCircle, xCenter, yCenter, xMagnitude, yMagnitude;
    float radius;
    Paint labelPaint, orientationLinePaint, zeroAnglePaint, needlePaint, circlePaint, circleFillPaint, statsPaint;
    String pitchString, rollString, angleString, magnitudeString;
    float[] updateY;

    public TiltMeter(Context context)
    {
        super(context);
        initialize(context);
    }

    public TiltMeter(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    public TiltMeter(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs);
        initialize(context);
    }

    private void initialize(Context c)
    {
        /*
         * Turn off hardware acceleration (View-level). See...
		 * 
		 * http://developer.android.com/guide/topics/graphics/hardware-accel.html
		 */
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        // get pixel density of this device's display
        pixelDensity = c.getResources().getDisplayMetrics().density;

        labelPaint = new Paint();
        labelPaint.setColor(Color.BLACK);
        labelPaint.setTextSize(DEFAULT_LABEL_TEXT_SIZE);
        labelPaint.setAntiAlias(true);

        statsPaint = new Paint();
        statsPaint.setAntiAlias(true);
        statsPaint.setTextSize(DEFAULT_STATS_TEXT_SIZE);

        needlePaint = new Paint();
        needlePaint.setAntiAlias(true);
        needlePaint.setColor(NEEDLE_COLOR);
        needlePaint.setStrokeWidth(DEFAULT_NEEDLE_STROKE_WIDTH);


        /*
         * Drawing the needle with a round cap provides a nice look. However, the following
		 * instruction does not work on a Google Nexus 4 running 4.0.1 unless hardware acceleration
		 * is turned off. See...
		 * 
		 * http://code.google.com/p/android/issues/detail?id=24873
		 */
        needlePaint.setStrokeCap(Paint.Cap.ROUND);

        orientationLinePaint = new Paint();
        orientationLinePaint.setAntiAlias(true);
        orientationLinePaint.setColor(Color.DKGRAY);
        orientationLinePaint.setStrokeWidth(0); // 1 pixel
        orientationLinePaint.setStyle(Paint.Style.STROKE);

        zeroAnglePaint = new Paint();
        zeroAnglePaint.setColor(Color.DKGRAY);
        zeroAnglePaint.setAntiAlias(true);
        zeroAnglePaint.setStrokeWidth(0); // 1 pixel
        zeroAnglePaint.setStyle(Paint.Style.STROKE);

        circlePaint = new Paint();
        circlePaint.setColor(Color.DKGRAY);
        circlePaint.setAntiAlias(true);
        circlePaint.setStrokeWidth(DEFAULT_CIRCLE_STROKE_WIDTH); // 1 pixel
        circlePaint.setStyle(Paint.Style.STROKE);

        circleFillPaint = new Paint();
        circleFillPaint.setColor(0xffaaaaaa); // between LTGRAY and DKGRAY
        circleFillPaint.setAntiAlias(true);
        circleFillPaint.setStyle(Paint.Style.FILL);

        pitchString = "";
        rollString = "";
        angleString = "";
        magnitudeString = "";

        this.setBackgroundColor(Color.LTGRAY);

        labelTextSize = (int)(DEFAULT_LABEL_TEXT_SIZE * pixelDensity + 0.5f);
        labelPaint.setTextSize(labelTextSize);
        labelPaint.setTextAlign(Paint.Align.CENTER);

        statsTextSize = (int)(DEFAULT_STATS_TEXT_SIZE * pixelDensity + 0.5f);
        statsPaint.setTextSize(statsTextSize);

        dialStrokeWidth = (int)(DEFAULT_NEEDLE_STROKE_WIDTH * pixelDensity + 0.5f);
        needlePaint.setStrokeWidth(dialStrokeWidth);

        circleStrokeWidth = (int)(DEFAULT_CIRCLE_STROKE_WIDTH * pixelDensity + 0.5f);
        circlePaint.setStrokeWidth(circleStrokeWidth);

        // spacing parameters for stats output on bottom-left of the display
        gap = (int)(DEFAULT_GAP * pixelDensity + 0.5f);
        offset = (int)(DEFAULT_OFFSET * pixelDensity + 0.5f);
    }

    // called when the window hosting this view gains or looses focus
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        Log.i(MYDEBUG, "onWindowFocusChanged 1!");

        if (!hasFocus)
            return;

        Log.i(MYDEBUG, "onWindowFocusChanged 2!");

        width = this.getWidth();
        height = this.getHeight();

        // compute radius of tilt meter and determine x-y coordinates of centre of view
        if (width < height)
            radius = 0.8f * (width / 2f);
        else
            radius = 0.8f * (height / 2f);
        xCenter = width / 2f;
        yCenter = height / 2f;

        Log.i(MYDEBUG, "onWindowFocusChanged 3!");

        // compute y offsets for painting stats (bottom-left of display)
        updateY = new float[4]; // 4 lines of update stats
        for (int i = 0; i < updateY.length; ++i)
            updateY[i] = height - offset - i * (statsTextSize + gap);
    }

    public void updateTilt(float angleArg, float magnitudeArg)
    {
        tiltAngle = angleArg;
        tiltMagnitude = magnitudeArg;
        xCircle = xCenter + radius * (float)Math.sin(Math.toRadians(tiltAngle));
        yCircle = yCenter - radius * (float)Math.cos(Math.toRadians(tiltAngle));

        tiltMagnitude = tiltMagnitude > MAX_MAGNITUDE ? MAX_MAGNITUDE : tiltMagnitude;
        xMagnitude = xCenter + (tiltMagnitude / MAX_MAGNITUDE) * radius * (float)Math.sin(Math.toRadians(tiltAngle));
        yMagnitude = yCenter - (tiltMagnitude / MAX_MAGNITUDE) * radius * (float)Math.cos(Math.toRadians(tiltAngle));

        angleString = String.format(Locale.CANADA, "Tilt angle (degrees from line) = %.2f", tiltAngle);
        magnitudeString = String.format(Locale.CANADA, "Tilt magnitude (0-45 degrees) = %.2f", tiltMagnitude);

        invalidate(); // force onDraw to execute
    }

    public void setPitch(float pitchArg)
    {
        pitchString = String.format(Locale.CANADA, "Device pitch (degrees) = %.2f", pitch);
        pitch = pitchArg;
    }

    public void setRoll(float rollArg)
    {
        rollString = String.format(Locale.CANADA, "Device roll (degrees) = %.2f", roll);
        roll = rollArg;
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        canvas.drawText("Demo Tilt Meter", xCenter, labelTextSize, labelPaint);

        // draw stats (pitch, roll, tilt angle, tilt magnitude)
        if (updateY != null) {
            canvas.drawText(pitchString, 10f, updateY[3], statsPaint);
            canvas.drawText(rollString, 10f, updateY[2], statsPaint);
            canvas.drawText(angleString, 10f, updateY[1], statsPaint);
            canvas.drawText(magnitudeString, 10f, updateY[0], statsPaint);
        }

        // draw circle fill
        canvas.drawCircle(xCenter, yCenter, radius, circleFillPaint);

        // draw circle
        canvas.drawCircle(xCenter, yCenter, radius, circlePaint);

        // draw zero degrees reference line
        canvas.drawLine(xCenter, yCenter, xCenter, yCenter - radius, zeroAnglePaint);

        // draw tilt orientation line
        canvas.drawLine(xCenter, yCenter, xCircle, yCircle, orientationLinePaint);

        // draw needle (on top of orientation line)
        canvas.drawLine(xCenter, yCenter, xMagnitude, yMagnitude, needlePaint);
    }
}
