package ca.yorku.eecs.mack.demograffiti;

/**
 * GraffitiPanel - sub-classed from <code>View</code>, a host for Graffiti gestures <p>
 *
 * See API for <code>DemoGraffitiActivity</code> for summary details.  Methods defined herein are described below.
 *
 * @author (c) Scott MacKenzie, 2013-2017
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Locale;

public class GraffitiPanel extends View
{
    final static int STATE_TEXT_SIZE = 30;
    final static int STATE_GAP = STATE_TEXT_SIZE / 6;
    final static int EDGE_GAP = 5;
    final static int SYMBOL_Y_OFFSET = 2 * (STATE_TEXT_SIZE + STATE_GAP);
    final static int GESTURE_BACKGROUND = 0xffffafb0; // pink
    final static int SYMBOL_BACKGROUND = Color.LTGRAY;

    // a stroke is deemed a tap is its duration in milliseconds is less than this value
    final static long TAP_DURATION_THRESHOLD = 80; // ms

    // a typical set of symbols supported on a desktop computer keyboard
    final static String[] SYMBOLS = {"~", "!", "@", "#", "$", "%", "^", "&", "*", "(", ")", "_", "+", "-", "=", "{",
            "}", "|", "[", "]", "\\", ":", "\"", ";", "'", "<", ">", "?", ",", ".", "/"}; // 31 symbols

    final int INK_COLOR = Color.BLUE;
    final float STROKE_WIDTH = 10f; // X pixel density on device (see below)
    final int SYMBOL_COLUMNS = 5; // 5x7 seems reasonable, adjust if necessary
    final int SYMBOL_ROWS = 7;

    int width, height;
    SymbolChip[] sc; // small rectangles holding the symbols for punctuation, etc.
    boolean capOn, capLockOn;
    boolean numOn, numLockOn;
    boolean symOn, symLockOn;
    Unistroke u;
    OnStrokeListener onStrokeListener; // for the call back to the activity implementing the
    private Paint paintInk, paintStateText, paintSymbolText, paintChipLine, paintStateTextWatermark;

    /*
     * The "gesture" array holds the points for the current gesture. On finger lift, it is provided
     * as a Point array to the Activity via the GraffitiEvent object.
     */
    private ArrayList<Point> gesture;

    /*
     * The "gestureSet" array holds the collection of gestures entered. It is only used if the
     * eraseOnFingerLift flag is not set. In this case, this array grows with each new gesture
     * entered, as will be apparent with the accumulation of ink on the GraffitiPanel. Leaving
     * eraseOnFingerLift cleared is intended only for demonstrations or for debugging, since drawing
     * performance degrades as the number of gestures to render increases.
     */
    //private ArrayList<Point[]> gestureSet;
    private ArrayList<ArrayList<Point>> gestureSet;

    private long timeStampFingerDown;
    private boolean eraseOnFingerLift = true;

    // Should provide three constructors to correspond to each of the three in View.
    public GraffitiPanel(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(context);
    }

    public GraffitiPanel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    public GraffitiPanel(Context context)
    {
        super(context);
        initialize(context);
    }

    private void initialize(Context c)
    {
        this.setBackgroundColor(GESTURE_BACKGROUND); // Pink

        u = new Unistroke();

        gesture = new ArrayList<Point>(); // an array of points representing a single gesture
        gestureSet = new ArrayList<ArrayList<Point>>();

        capOn = capLockOn = numOn = numLockOn = symOn = symLockOn = false;

        paintInk = new Paint();
        paintInk.setColor(INK_COLOR);
        float pixelDensity = c.getResources().getDisplayMetrics().density;
        paintInk.setStrokeWidth(STROKE_WIDTH * pixelDensity);
        paintInk.setStrokeCap(Paint.Cap.ROUND);

        paintStateText = new Paint();
        paintStateText.setColor(0xffff0000);
        paintStateText.setAntiAlias(true);
        paintStateText.setTypeface(Typeface.DEFAULT_BOLD);
        paintStateText.setTextSize(STATE_TEXT_SIZE);

        paintStateTextWatermark = new Paint();
        paintStateTextWatermark.setColor(0xffaaafb0);
        paintStateTextWatermark.setAntiAlias(true);
        paintStateTextWatermark.setTypeface(Typeface.DEFAULT_BOLD);
        paintStateTextWatermark.setTextSize(STATE_TEXT_SIZE);

        paintSymbolText = new Paint();
        paintSymbolText.setColor(0xffaaaaaa);
        paintSymbolText.setAntiAlias(true);
        paintSymbolText.setTextAlign(Paint.Align.CENTER);

        paintChipLine = new Paint();
        paintChipLine.setStyle(Paint.Style.STROKE);
        paintChipLine.setStrokeWidth(9f);
        paintChipLine.setAntiAlias(true);
        paintChipLine.setColor(0xffbbbbbb);
    }

    /*
     * The view is now visible so its width and height are available.
     *
     * Do nothing if hasFocus is false. Otherwise, initialize the width and height fields and things
     * that need width and height.
     */
    @Override
    public void onWindowFocusChanged(boolean hasFocus)
    {
        if (!hasFocus)
            return;
        width = this.getWidth();
        height = this.getHeight();
        initializeChips(); // need width and height to do this
    }

    /**
     * Attach the stroke listener to this <code>GraffitiPanel</code>. <p>
     *
     * This method is invoked on a <code>GraffitiPanel</code> instance from the activity. When the
     * <code>GraffitiPanel</code> deems that a stroke has occurred (upon <code>ACTION_UP</code>) <code>onStroke</code>
     * is called, sending a <code>GraffitiEvent</code> object back to the activity.
     *
     * @param onStrokeListenerArg the listener to attach to this <code>GraffitiPanel</code> (typically
     *                            <code>this</code>)
     */
    public void setOnStrokeListener(OnStrokeListener onStrokeListenerArg)
    {
        onStrokeListener = onStrokeListenerArg;
    }

    /**
     * Set the dictionary that this <code>GraffitiPanel</code> will use to recognize strokes. <p>
     *
     * Example values are <code>Unistroke.GRAFFITI</code> or <code>Unistroke.DIGITS</code>. See the
     * <code>Unistroke</code> API for further details. <p>
     *
     * @param dictionaryArg an int identifying the dictionary
     */
    public void setDictionary(int dictionaryArg)
    {
        u.setDictionary(dictionaryArg);
    }

    /**
     * Process touch events on this <code>GraffitiPanel</code> <p>
     *
     * When a gesture is completed and recognized, the <code>onStroke</code> callback method is called to send the
     * <code>GraffitiEvent</code> to the activity or IME service using this <code>GraffitiPanel</code>.
     */
    @Override
    public boolean onTouchEvent(MotionEvent me)
    {
        int x = Math.round(me.getX());
        int y = Math.round(me.getY());

        switch (me.getAction() & MotionEvent.ACTION_MASK)
        {
            // beginning of gesture
            case MotionEvent.ACTION_DOWN:
                timeStampFingerDown = System.currentTimeMillis();
                gesture.add(new Point(x, y));
                break;

            // gesture in progress
            case MotionEvent.ACTION_MOVE:
                gesture.add(new Point(x, y));
                break;

            // End of gesture
            // The critical work is done here (finish with a callback to the Activity using onStroke)
            case MotionEvent.ACTION_UP:
                int charCode;
                int type;
                long timeStampFingerUp = System.currentTimeMillis();
                long strokeDuration = timeStampFingerUp - timeStampFingerDown;

                gesture.add(new Point(x, y)); // last point in the gesture

                // =========================
                // Now do the Graffiti stuff
                // =========================

                String raw;
                if (strokeDuration < TAP_DURATION_THRESHOLD)
                    raw = "=TAP";
                else
                    raw = u.recognize(gesture); // the heavy lifting is done here!

                // Begin with straight-line strokes. Let's go around the compass dial, starting at
                // NORTH...
                // --------------------------------------
                if (raw.equals("=N")) // CAP or CAP_LOCK
                {
                    charCode = GraffitiEvent.CHAR_NULL;
                    type = GraffitiEvent.TYPE_NORTH;
                    if (!capOn)
                        capOn = true;
                    else if (!capLockOn)
                        capLockOn = true;
                    else
                    {
                        capOn = false;
                        capLockOn = false;
                    }
                }

                // --------------------------------------------
                else if (raw.equals("=NE")) // NUM and NUM_LOCK
                {
                    charCode = GraffitiEvent.CHAR_NULL;
                    type = GraffitiEvent.TYPE_NORTH_EAST;
                    if (numOn && !numLockOn)
                    {
                        numLockOn = true;
                        numOn = false;
                    } else if (numLockOn) // exit num lock and revert to Graffiti dictionary
                    {
                        numLockOn = false;
                        this.setDictionary(Unistroke.GRAFFITI);
                    } else
                    // switch to num mode; switch to digits dictionary
                    {
                        numOn = true;
                        this.setDictionary(Unistroke.DIGITS);
                    }
                }

                // ---------------------------------
                else if (raw.equals("=E")) // SPACE
                {
                    charCode = GraffitiEvent.CHAR_SPACE;
                    type = GraffitiEvent.TYPE_SPACE;
                }

                // -----------------------------------------
                else if (raw.equals("=SE")) // No assignment
                {
                    charCode = GraffitiEvent.CHAR_NULL;
                    type = GraffitiEvent.TYPE_SOUTH_EAST;
                }
                // SOUTH (will be i or 1, depending on mode)

                // ----------------------------------
                else if (raw.equals("=SW")) // RETURN
                {
                    charCode = GraffitiEvent.CHAR_ENTER;
                    type = GraffitiEvent.TYPE_ENTER;
                }

                // ------------------------------------
                else if (raw.equals("=W")) // BACKSPACE
                {
                    charCode = GraffitiEvent.CHAR_BACKSPACE;
                    type = GraffitiEvent.TYPE_BACKSPACE;
                }

                // --------------------------------------------
                else if (raw.equals("=NW")) // SYM and SYM_LOCK
                {
                    charCode = GraffitiEvent.CHAR_NULL;
                    type = GraffitiEvent.TYPE_NORTH_WEST;
                    if (!symOn)
                        symOn = true;
                    else if (!symLockOn)
                        symLockOn = true;
                    else
                    {
                        symOn = false;
                        symLockOn = false;
                    }
                }

                // ------------------------------------------------
                else if (raw.equals(Unistroke.UNRECOGNIZED_STROKE)) // unrecognized gesture
                {
                    charCode = GraffitiEvent.CHAR_UNRECOGNIZED;
                    type = GraffitiEvent.TYPE_UNRECOGNIZED;
                }

                // ------------------------------------------------
                else if (raw.equals("=TAP")) // a tap (. or symbol)
                {
                    if (symOn || symLockOn)
                    {
                        charCode = getSymbol(x, y).charAt(0); // retrieve charCode from symbol array
                        type = GraffitiEvent.TYPE_SYMBOL;
                        if (symOn && !symLockOn)
                            symOn = false;
                    } else
                    {
                        charCode = '.';
                        type = GraffitiEvent.TYPE_TAP;
                    }
                }

                // ------------------------------------------------------------------------------
                else
                // everything else (raw contains the character corresponding to the gesture)
                {
                    if (capOn) // convert to uppercase
                    {
                        raw = raw.toUpperCase(Locale.getDefault());
                        if (!capLockOn)
                        {
                            capOn = false;
                        }
                    }
                    charCode = raw.charAt(0); // most gesture characters are obtained here
                    type = numOn || numLockOn ? GraffitiEvent.TYPE_NUMERIC : GraffitiEvent.TYPE_ALPHA;

                    // if this was a one-time numeric entry, revert to graffiti dictionary
                    if (numOn && !numLockOn)
                    {
                        numOn = false;
                        this.setDictionary(Unistroke.GRAFFITI);
                    }
                }

                // update indicators on paint panel
                updateStateIndicators(capOn, capLockOn, numOn, numLockOn, symOn, symLockOn);

                if (symOn || symLockOn)
                    setBackgroundColor(GraffitiPanel.SYMBOL_BACKGROUND);
                else
                    setBackgroundColor(GraffitiPanel.GESTURE_BACKGROUND);

                // call back to the activity (or IME service) implementing OnStrokeListener
                onStrokeListener.onStroke(new GraffitiEvent(raw, charCode, type, x, y, timeStampFingerDown,
                        timeStampFingerUp, gesture));

                if (eraseOnFingerLift)
                    gestureSet.clear();
                else
                    gestureSet.add(new ArrayList<Point>(gesture));

                gesture.clear();
                break;
        }
        invalidate(); // refresh the graffiti panel (i.e., draw ink and state indicators)
        return true;
    } // end onTouchEvent

    @Override
    protected void onDraw(Canvas canvas)
    {
        super.onDraw(canvas);

        // =============================================================
        // paint state indicators (do this first so, ink appears on top)
        // =============================================================

        // begin with watermarks...
        canvas.drawText("SYM", STATE_GAP, STATE_TEXT_SIZE + STATE_GAP, paintStateTextWatermark);
        canvas.drawText("LOCK", STATE_GAP, 2 * (STATE_TEXT_SIZE + STATE_GAP), paintStateTextWatermark);
        canvas.drawText("SHIFT", width / 2 - paintStateText.measureText("SHIFT") / 2, STATE_TEXT_SIZE + STATE_GAP,
                paintStateTextWatermark);
        canvas.drawText("LOCK", width / 2 - paintStateText.measureText("LOCK") / 2, 2 * (STATE_TEXT_SIZE + STATE_GAP),
                paintStateTextWatermark);
        canvas.drawText("NUM", width - STATE_GAP - paintStateText.measureText("NUM"), STATE_TEXT_SIZE + STATE_GAP,
                paintStateTextWatermark);
        canvas.drawText("LOCK", width - STATE_GAP - paintStateText.measureText("LOCK"),
                2 * (STATE_TEXT_SIZE + STATE_GAP), paintStateTextWatermark);

        // on the left...
        if (symOn || symLockOn)
            canvas.drawText("SYM", STATE_GAP, STATE_TEXT_SIZE + STATE_GAP, paintStateText);
        if (symLockOn)
            canvas.drawText("LOCK", STATE_GAP, 2 * (STATE_TEXT_SIZE + STATE_GAP), paintStateText);

        // in the middle...
        if (capOn || capLockOn)
            canvas.drawText("SHIFT", width / 2 - paintStateText.measureText("SHIFT") / 2, STATE_TEXT_SIZE + STATE_GAP,
                    paintStateText);
        if (capLockOn)
            canvas.drawText("LOCK", width / 2 - paintStateText.measureText("LOCK") / 2,
                    2 * (STATE_TEXT_SIZE + STATE_GAP), paintStateText);

        // on the right...
        if (numOn || numLockOn)
            canvas.drawText("NUM", width - STATE_GAP - paintStateText.measureText("NUM"), STATE_TEXT_SIZE + STATE_GAP,
                    paintStateText);
        if (numLockOn)
            canvas.drawText("LOCK", width - STATE_GAP - paintStateText.measureText("LOCK"),
                    2 * (STATE_TEXT_SIZE + STATE_GAP), paintStateText);

        // =======================================================
        // Paint the symbols and their rectangles (if symbol mode)
        // =======================================================

        if (symOn || symLockOn)
        {
            for (int i = 0; i < SYMBOLS.length; ++i)
            {
                canvas.drawText(sc[i].text, sc[i].r.centerX(), sc[i].r.centerY() - paintSymbolText.ascent() * 0.4f,
                        paintSymbolText);
                canvas.drawRect(sc[i].r, paintChipLine);
            }
        }

        // ================================
        // Paint the ink for the gesture(s)
        // ================================

        // Current (unfinished) gesture
        paintGestureInk(canvas, gesture);

        // Saved gestures (NOTE: size > 0 only if eraseOnFingerLift is clear)
        for (int i = 0; i < gestureSet.size(); ++i)
            paintGestureInk(canvas, gestureSet.get(i));

    } // end onDraw

    private void paintGestureInk(Canvas c, ArrayList<Point> gesture)
    {
        for (int i = 1; i < gesture.size(); ++i)
            c.drawLine(gesture.get(i - 1).x, gesture.get(i - 1).y, gesture.get(i).x, gesture.get(i).y, paintInk);
        if (gesture.size() == 1)
            c.drawPoint(gesture.get(0).x, gesture.get(0).y, paintInk); // single-point gesture
    }

    // initialize the "chips" (rectangles) for symbols
    private void initializeChips()
    {
        sc = new SymbolChip[SYMBOLS.length];
        int x; // left side
        int y; // top
        int chipWidth = Math.round((float)(width - 2 * EDGE_GAP) / SYMBOL_COLUMNS);
        int chipHeight = Math.round((float)(height - SYMBOL_Y_OFFSET - 2 * EDGE_GAP) / SYMBOL_ROWS);

        for (int i = 0; i < SYMBOLS.length; ++i)
        {
            x = EDGE_GAP + (i % SYMBOL_COLUMNS) * chipWidth;
            y = SYMBOL_Y_OFFSET + EDGE_GAP + (i / SYMBOL_COLUMNS) * chipHeight;
            sc[i] = new SymbolChip(SYMBOLS[i], x, y, chipWidth, chipHeight);
        }

        paintSymbolText.setTextSize(7 * chipHeight / 10); // text size is 70% of chip height
    }

    /**
     * Clear all digital ink from this GraffitiPanel
     */
    public void clear()
    {
        gesture.clear();
        gestureSet.clear();
        invalidate(); // repaints
    }

    /**
     * Control whether the digital ink is erased at the end of a gesture. <p>
     *
     * @param eraseOnFingerLiftArg if true, digital ink is erased on finger lift
     */
    public void setEraseOnFingerLift(boolean eraseOnFingerLiftArg)
    {
        eraseOnFingerLift = eraseOnFingerLiftArg;
        clear();
    }

    // update the num, cap, and sym indicators
    private void updateStateIndicators(boolean uppercaseModeArg, boolean capsLockModeArg, boolean numOnArg,
                                       boolean numLockOnArg, boolean symOnArg, boolean symLockOnArg)
    {
        capOn = uppercaseModeArg;
        capLockOn = capsLockModeArg;
        numOn = numOnArg;
        numLockOn = numLockOnArg;
        symOn = symOnArg;
        symLockOn = symLockOnArg;
        this.invalidate(); // repaint
    }

    /*
     * onMeasure - Since this is a custom View, we must override onMeasure. The code here is a close
     * approximation to that found in LabelView.java (the Android sample API provided for custom
     * views; see http://developer.android.com/guide/topics/ui/custom-components.html#custom). The
     * approach here is to assume (hope!) that exact values are available via the XML layout files.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
    }

    private int measureWidth(int measureSpec)
    {
        int result = 100; // fall-back value (so something appears!)
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) // We were told how big to be (return that)
            result = specSize; // the value computed based on XML layout
        else if (specMode == MeasureSpec.AT_MOST)// respect AT_MOST if that's the spec
            result = Math.min(result, specSize);
        return result;
    }

    private int measureHeight(int measureSpec)
    {
        int result = 100; // fall-back value (so something appears!)
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY) // We were told how big to be (return that)
            result = specSize; // the value computed based on the XML layout
        else if (specMode == MeasureSpec.AT_MOST) // respect AT_MOST if that's the spec
            result = Math.min(result, specSize);
        return result;
    }

    // return a string for the symbol at the specified x/y location
    private String getSymbol(float xArg, float yArg)
    {
        String symbol = "";
        for (SymbolChip symbolChip : sc)
        {
            if (symbolChip.contains(xArg, yArg))
            {
                symbol = symbolChip.text;
                break;
            }
        }
        return symbol;
    }

    /**
     * Interface to pass stroke event data to an activity or IME service. <p>
     *
     * An Activity or service using a <code>GraffitiPanel</code> must<p>
     *
     * <ol>
     * <li>Include <code>implements GraffitiPanel.OnStrokeListener</code> in its signature
     * <li>Implement the <code>onStroke</code> call back method (see below)
     * <li>Include <code>setOnStrokeListener(this)</code> in its initialization of the <code>GraffitiPanel</code>
     * </ol>
     *
     * @author (c) Scott MacKenzie, 2013-2016
     */
    public interface OnStrokeListener
    {
        void onStroke(GraffitiEvent ge);
    }

    // =================================================================
    // SymbolChip - simple class to hold information for a "symbol chip"
    // =================================================================
    private static class SymbolChip
    {
        String text;
        RectF r;

        SymbolChip(String textArg, int xArg, int yArg, int widthArg, int heightArg)
        {
            text = textArg;
            r = new RectF(xArg, yArg, xArg + widthArg, yArg + heightArg);
        }

        boolean contains(float xArg, float yArg)
        {
            return r.contains(xArg, yArg);
        }
    }
}
