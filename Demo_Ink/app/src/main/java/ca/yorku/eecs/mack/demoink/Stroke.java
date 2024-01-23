package ca.yorku.eecs.mack.demoink;

import android.graphics.Paint;

import java.io.Serializable;
import java.util.ArrayList;

// =========================================================================================
/*
 * A simple class to hold the line segments forming a stroke. Also holds the paint/color for
 * drawing the stroke.
 */
class Stroke implements Serializable
{
    private static final long serialVersionUID = 1L;

    private final transient Paint strokePaint;
    private final ArrayList<Line> strokeSegments;

    Stroke(Paint strokePaintArg)
    {
        strokePaint = new Paint(strokePaintArg);
        strokeSegments = new ArrayList<Line>();
    }

    void addSegment(Line strokeSegment)
    {
        strokeSegments.add(strokeSegment);
    }

    ArrayList<Line> getStrokeSegments()
    {
        return strokeSegments;
    }

    Paint getStrokePaint()
    {
        return strokePaint;
    }

    void setInkColor(int colorArg)
    {
        strokePaint.setColor(colorArg);
    }

}
