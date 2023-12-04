package ca.yorku.eecs.mack.democustombutton;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.util.AttributeSet;
import android.view.View;

/**
 * MyButton
 * <p>
 *
 * Implementation of a custom hexagonal button as a subclass of <code>View</code>.
 *
 * @author (c) Scott MacKenzie, 2011-2017
 */
public class MyButton extends View
{
    private final int UP_FILL_COLOR = Color.GRAY;
    final int DOWN_FILL_COLOR = 0xff8080ff;
    final int BORDER_COLOR = Color.DKGRAY;
    final int DEFAULT_WIDTH = 100;
    final int DEFAULT_HEIGHT = 100;

    // Set the shape our our hexagonal button by defining arrays to scale the x and y points of a six-point path.
    // NOTE: The values are scaling factors, which are multiplied by the view width and view height.
    private final float[] BUTTON_POINTS_X_FACTOR = {0.00f, 0.50f, 1.00f, 1.00f, 0.50f, 0.00f};
    private final float[] BUTTON_POINTS_Y_FACTOR = {0.25f, 0.00f, 0.25f, 0.75f, 1.00f, 0.75f};

    // used for layout params (see DemoCustomButtonActivity)
    final static float LEFT_MARGIN_ADJ = 0.50f; // from above x-factor array
    final static float TOP_MARGIN_ADJ = 0.75f; // from above y-factor array

    public int width;
    public int height;
    float[] hexagonX, hexagonY;
    Paint linePaint, fillPaint, textPaint;
    Path buttonPath;
    String buttonText;
    int xText, yText;
    boolean isPressed;
    ToneGenerator tg;

    /**
     * Construct a MyButton object.
     */
    public MyButton(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize();
    }

    /**
     * Construct a MyButton object.
     */
    public MyButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    /**
     * Construct a MyButton object.
     */
    public MyButton(Context context)
    {
        super(context);
        initialize();
    }

    private void initialize()
    {
        setFocusableInTouchMode(true);
        setFocusable(true);
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(5);
        linePaint.setColor(BORDER_COLOR); // default is button-up

        fillPaint = new Paint();
        fillPaint.setColor(UP_FILL_COLOR);

        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.BLACK);
        textPaint.setTextAlign(Paint.Align.CENTER);
        isPressed = false;
        buttonText = "?"; // default
        tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 50);

        setButtonSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /*
     * onMeasure - since we are extending View, we must override onMeasure. For the MyButton custom
     * view, the width and height of the buttons are either DEFAULT_WIDTH and DEFAULT_HEIGHT (see
     * above) or as set from the Activity by calling setButtonSize. And so, we just use width and
     * height, and pass them to setMeasuredDimension, as required.
     *
     * For some custom views, it might be desirable to establish the view size from the XML layout
     * files. In this case, a more elaborate approach is needed for onMeasure. For an example of
     * this, see the onMeasure method in GraffitiPanel (the custom view used in DemoGraffiti).
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(width, height);
    }

    /**
     * Draw the MyButton object. Modify the code here to create the desired appearance.
     */
    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);
        canvas.drawPath(buttonPath, fillPaint);
        canvas.drawPath(buttonPath, linePaint);
        canvas.drawText(buttonText, xText, yText, textPaint);
    }

    @Override
    public boolean performClick()
    {
        super.performClick();
        return true;
    }

    /**
     * Set the size of this button. If this method is not invoked from the Activity, the button will
     * assume a default size of 100 x 100. In any event, the text displayed in the button is given
     * the size width / 3.
     *
     * The button path is initialized from the width and height parameter.
     *
     * @param widthArg  The width of the button in pixels.
     * @param heightArg The height of the button in pixels.
     */
    public void setButtonSize(int widthArg, int heightArg)
    {
        width = widthArg;
        height = heightArg;
        textPaint.setTextSize(width / 3);
        xText = width / 2;
        yText = height / 2 - (int)(textPaint.ascent() / 3);

        // Now do the path. Define hexagon (generic, in terms of width and height)

        hexagonX = new float[BUTTON_POINTS_X_FACTOR.length];
        hexagonY = new float[BUTTON_POINTS_Y_FACTOR.length];
        for (int i = 0; i < hexagonX.length; ++i)
        {
            hexagonX[i] = width * BUTTON_POINTS_X_FACTOR[i];
            hexagonY[i] = height * BUTTON_POINTS_Y_FACTOR[i];
        }

        buttonPath = new Path();
        buttonPath.moveTo(hexagonX[0], hexagonY[0]);
        for (int i = 1; i < hexagonX.length; ++i)
            buttonPath.lineTo(hexagonX[i], hexagonY[i]);
        buttonPath.close();
    }

    /**
     * Set the text for this button.
     *
     * @param buttonTextArg The text that appears in the button.
     */
    public void setButtonText(String buttonTextArg)
    {
        buttonText = buttonTextArg;
    }

    public void setIsPressed(boolean isPressedArg)
    {
        isPressed = isPressedArg;
        if (isPressed)
            fillPaint.setColor(DOWN_FILL_COLOR);
        else
            fillPaint.setColor(UP_FILL_COLOR);
        invalidate();
    }

    public void doClick()
    {
        tg.startTone(ToneGenerator.TONE_DTMF_C, 50);
    }

    public boolean contains(float x, float y)
    {
        /*
         * Since the x-y coordinate is the touch point within the button panel, we first subtract out the left and top
         * margins of the actual button position.
         */
        x -= this.getLeft();
        y -= this.getTop();

        // this is the "crossing number algorithm" (see https://en.wikipedia.org/wiki/Point_in_polygon for details)
        int polySides = hexagonX.length;
        boolean oddTransitions = false;
        for (int i = 0, j = polySides - 1; i < polySides; j = i++)
            if ((hexagonY[i] < y && hexagonY[j] >= y) || (hexagonY[j] < y && hexagonY[i] >= y))
                if (hexagonX[i] + (y - hexagonY[i]) / (hexagonY[j] - hexagonY[i]) * (hexagonX[j] - hexagonX[i]) < x)
                    oddTransitions = !oddTransitions;
        return oddTransitions;
    }
}
