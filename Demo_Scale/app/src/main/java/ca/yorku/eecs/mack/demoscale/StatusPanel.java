package ca.yorku.eecs.mack.demoscale;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Locale;

public class StatusPanel extends View
{
    final float TEXT_SIZE = 12f;
    final int OFFSET = 5;

    int x, y, w, h;
    float imageScale;
    float pixelDensity; // ...to control text size and margins
    Paint p;
    float textSize;
    float margin;

    // Should provide three constructors to correspond to each of the three in View.
    public StatusPanel(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        initialize(context);
    }

    public StatusPanel(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize(context);
    }

    public StatusPanel(Context context)
    {
        super(context);
        initialize(context);
    }

    public void initialize(Context c)
    {
        // get the pixel density for the device's display
        pixelDensity = c.getResources().getDisplayMetrics().density;

        textSize = TEXT_SIZE * pixelDensity;
        margin = OFFSET * pixelDensity;
        p = new Paint();
        p.setColor(Color.WHITE);
        p.setAntiAlias(true);
        p.setTextSize(textSize);
    }

    // update the values in the status panel
    public void update(int xArg, int yArg, int widthArg, int heightArg, float scaleArg)
    {
        x = xArg;
        y = yArg;
        w = widthArg;
        h = heightArg;
        imageScale = scaleArg;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas)
    {
        final float fieldWidth = (this.getWidth() / 2);
        canvas.drawText("x = " + x, margin + 0 * fieldWidth, 1 * (textSize + textSize / 4), p);
        canvas.drawText("w = " + w, margin + 1 * fieldWidth, 1 * (textSize + textSize / 4), p);
        canvas.drawText("y = " + y, margin + 0 * fieldWidth, 2 * (textSize + textSize / 4), p);
        canvas.drawText("h = " + h, margin + 1 * fieldWidth, 2 * (textSize + textSize / 4), p);
        canvas.drawText(String.format(Locale.CANADA, "scale = %.2f", imageScale), margin + 0 * fieldWidth, 3 *
                (textSize + textSize / 4), p);
    }
}
