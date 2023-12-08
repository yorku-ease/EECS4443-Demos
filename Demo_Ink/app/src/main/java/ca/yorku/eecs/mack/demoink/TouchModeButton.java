package ca.yorku.eecs.mack.demoink;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * TouchModeButton - implementation of a "touch mode" button. <p>
 *
 * <code>Button</code> objects have certain limitations in touch mode, such as not being able to receive focus. The
 * relevant discussion from the Android API Guides notes the following: <p>
 *
 * <blockquote> For a touch-capable device, once the user touches the screen, the device will enter touch mode. From
 * this point onward, only Views for which <code>isFocusableInTouchMode()</code> is true will be focusable, such as text
 * editing widgets. Other Views that are touchable, like buttons, will not take focus when touched; they will simply
 * fire their on-click listeners when pressed. (see
 * <a href="http://developer.android.com/guide/topics/ui/ui-events.html#TouchMode">Touch
 * Mode</a>) </blockquote>
 *
 * The class overcomes this limitation by implementing a touch mode button that extends <code>View</code>. <p>
 *
 * An activity that uses a <code>TouchModeButton</code> must be configured to sense and respond to touch events in the
 * usual way. There are three requirements: (i) include <code>implements View.OnTouchListener</code> in the
 * <code>Activity</code> signature, (ii) use <code>setOnTouchListener(this)</code> to attach the listener to the
 * <code>TouchModeButton</code>, and (iii) implement <code>onTouch</code>. IMPORTANT: Terminate the Activity's
 * <code>onTouch</code> method with <code> return false</code>. This allows <code>onTouchEvent</code> to execute here to
 * draw the button in its button-down state. <p>
 *
 * One of the potential uses of a <code>TouchModeButton</code> is for touch-and-hold. If the finger touches and remains
 * on the button, this can be sensed and responded to as appropriate. Such behaviour is not possible with a <code>Button
 * </code> object. Implementing touch-and-hold behaviour requires a few steps, as demonstrated in DemoInkActivity for
 * controlling the shading of the current inking color. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2017
 */

public class TouchModeButton extends View
{
    private final static int UP_FILL_COLOR = Color.GRAY;
    private final static int DOWN_FILL_COLOR = Color.DKGRAY;
    private final static int BORDER_COLOR = Color.DKGRAY;
    Paint textPaint, p;
    int fillColor;
    String text;
    int xText, yText;
    private int width;
    private int height;

    public TouchModeButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize();
    }

    void initialize()
    {
        p = new Paint();
        fillColor = UP_FILL_COLOR;
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
        this.setPadding(10, 10, 10, 10);
        text = "";
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
     * Set the size of this button. If this method is not invoked, the button will assume a default size based on the
     * text displayed in the button (with 5 pixels of padding all around).
     *
     * @param widthArg  The width of the button in pixels.
     * @param heightArg The height of the button in pixels.
     */
    public void setButtonSize(int widthArg, int heightArg)
    {
        width = widthArg;
        height = heightArg;
        textPaint.setTextSize(width / 4);
        xText = width / 2;
        yText = height / 2 - (int)(textPaint.ascent() / 3);
    }

    @Override
    protected void onDraw(Canvas c)
    {
        super.onDraw(c);
        p.setColor(fillColor);
        p.setStyle(Paint.Style.FILL);
        c.drawRect(0, 0, width, height, p);

        // draw border (color doesn't vary)
        p.setColor(BORDER_COLOR);
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(0);
        c.drawRect(0, 0, width, height, p);
        c.drawText(text, xText, yText, textPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        //Log.i("DEBUG", " Reached onTouchEvent : MotionEvent=" + me);
        if (me.getAction() == MotionEvent.ACTION_DOWN)
            fillColor = DOWN_FILL_COLOR;

        else if (me.getAction() == MotionEvent.ACTION_UP)
            fillColor = UP_FILL_COLOR;

        invalidate(); // repaint
        return true;
    }

    // for accessibility UIs
    @Override
    public boolean performClick()
    {
        return true;
    }
}
