package ca.yorku.eecs.mack.demoink;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Demo_Ink - demo for digital inking using finger gestures on the surface of a tablet. Only single-touch gestures are
 * supported. <P>
 *
 * Related information: </p>
 *
 * <blockquote> API Guides: </p>
 *
 * <ul> <li> <a href="http://developer.android.com/guide/topics/graphics/2d-graphics.html"> Canvas and Drawables</a>
 * <li> <a href="http://developer.android.com/guide/topics/graphics/index.html"> Animation and Graphics</a> (including
 * <a href= "http://developer.android.com/guide/topics/graphics/hardware-accel.html" >Hardware Acceleration</a>) <li><a
 * href="http://developer.android.com/guide/topics/resources/runtime-changes.html">Handling Runtime Changes</a>
 * </ul><p>
 *
 * API References: </p>
 *
 * <ul> <li> <a href="http://developer.android.com/reference/android/graphics/Paint.html"> <code>Paint</code> </a> <li>
 * <a href="http://developer.android.com/reference/android/graphics/Canvas.html"> <code>Canvas</code></a> <li> <a href=
 * "http://developer.android.com/reference/android/os/CountDownTimer.html"> <code>CountDownTimer</code></a> <li> <a
 * href="http://developer.android.com/reference/android/widget/Toast.html"> <code>Toast</code> </a> <li> <a
 * href="http://developer.android.com/reference/android/os/Environment.html"> <code>Environment</code></a> <li> <a
 * href="http://developer.android.com/reference/android/view/View.html"> <code>View</code></a> <li> <a
 * href="http://developer.android.com/reference/android/view/MotionEvent.html"> <code>MotionEvent</code></a> <li> <a
 * href= "http://developer.android.com/reference/android/media/ToneGenerator.html"> <code>ToneGenerator</code></a> <li>
 * <a href="http://developer.android.com/reference/java/util/ArrayList.html"> <code>ArrayList</code> </a> <li> <a
 * href="http://developer.android.com/reference/java/util/Iterator.html"> <code>Iterator</code> </a> </ul> </blockquote>
 * </p>
 *
 * Do you have a talent for sketching? With this demo, you can find out. The user can paint using digital ink and a
 * finger. It's a simple app, with a just few features. These include choosing between ten colors and adjusting the
 * color to make it darker (to black) or lighter (to white). Inked strokes can be undone. The thickness of the stroke is
 * controlled by finger pressure. When your masterpiece is ready, you can save it as a JPG file or even send it to a
 * friend as an email attachment. </p>
 *
 * Here's an example screen snap showing a quick sketch: </p>
 *
 * <center><a href="./javadoc_images/DemoInk-1.jpg"><img src="./javadoc_images/DemoInk-1.jpg" width="300"></a></center> </p>
 *
 * <b>Digital Ink, Drawing, and Canvas</b> </p>
 *
 * Digital ink is just a fancy term for lines drawn on a canvas. The drawing surface, which has a pink background (see
 * screen snap above), is an inner class named <code>PaintPanel</code>, a subclass of <code>View</code>. As the finger
 * touches and moves about, touch events are generated. Each stroke or inking gesture begins on
 * <code>ACTION_DOWN</code>, continues on <code>ACTION_MOVE</code>, and ends on <code>ACTION_UP</code>. The stroke is
 * divided into stroke segments, or lines. Each line is saved as an instance of <code>Line</code>, a custom class that
 * holds the <i>x</i> and <i>y</i> coordinates of the beginning and ending of a line and the thickness of the line. Each
 * stroke is saved as an instance of <code>Stroke</code>, a custom class that holds the lines for a single stroke and
 * the color of the stroke. The collection of strokes forming a complete sketch is saved in an instance of
 * <code>Sketch</code>, also a custom class. The <code>Sketch</code> class is simply an <code>ArrayList</code> of
 * <code>Stroke</code> objects. </p>
 *
 * The sketch is drawn using the panel's <code>onDraw</code> method. To improve performance, <code>onDraw</code>
 * includes just two statements: </p>
 *
 * <pre>
 *      protected void onDraw(Canvas canvas)
 *      {
 *           canvas.drawBitmap(bmp, 0, 0, bmpPaint);
 *           drawStroke(canvas, currentStroke);
 *      }
 * </pre>
 *
 * </p>
 *
 * The first statement draws a bitmap that fills the entire panel. The bitmap contains the background and all the
 * existing strokes in the sketch. The second statement adds the current in-progress stroke. </p>
 *
 * The <code>drawStroke</code> method is called from <code>onDraw</code> (see above) and also from <code>addLine</code>,
 * which is called from <code>onTouch</code> as the stroke is being made by the user (i.e., artist). In the latter case,
 * <code>drawStroke</code> draws into an off-screen canvas holding the same bitmap (<code>bmp</code>). Thus, the sketch
 * is gradually built up in the bitmap with each stroke drawn only once. </p>
 *
 * The Undo operation simply removes the last element in the <code>ArrayList</code> of <code>Stroke</code> objects. This
 * also requires recreating the sketch in the off-screen bitmap (minus the stroke that was removed). </p>
 *
 * The thickness of the line is determined by the pressure values provided by <code>MotionEvent</code>. Unfortunately,
 * the pressure values are somewhat erratic and inconsistent from one Android device to another. The implementation here
 * works reasonably well on a Google Nexus 4. </p>
 *
 * One unusual Android bug is that </p>
 *
 * <pre>
 *      p.setStrokeCap(Paint.Cap.ROUND);
 * </pre>
 *
 * </p>
 *
 * does not work on a Google Nexus 4 unless hardware acceleration is turned off. Using a round stroke cap is important,
 * since it helps consecutive line segments blend together. So, hardware acceleration is turned off. Consult the source
 * code for further details. </p>
 *
 * <b>Touch Mode Buttons</b> </p>
 *
 * An interesting feature in this application is the use of a <code>TouchModeButton</code> &mdash; a custom button that
 * extends <code>View</code>. A TouchModeButton is used to adjust the shading of the ink. Shading for the currently
 * selected ink can be darkened (to black) or lightened (to white). The buttons labeled "-" (darken) and "+" (lighten)
 * are TouchModeButtons. They can be tapped, as expected, to effect a step change in the shading. </p>
 *
 * TouchModeButtons also support touch-and-hold. If the finger touches and continues to touch the button, the shading is
 * continuously adjusted, darker or lighter: </p>
 *
 * <center><a href="./javadoc_images/DemoInk-5.jpg"><img src="./javadoc_images/DemoInk-5.jpg" width="400"></a> </center> </p>
 *
 * Implementing this feature involves several steps. The following is a high-level summary. </p>
 *
 * Rather than responding to events using <code>onClick</code>, as with a regular <code>Button</code>, a
 * <code>TouchModeButton</code> fires touch events. The <code>onTouch</code> method includes a series of if/else
 * statements to determine the source of the touch event. If the event is caused by a <code>TouchModeButton</code> and
 * the event is <code>ACTION_UP</code>, a method called <code>adjustShading</code> is called to change the shading. This
 * is the expected behaviour for a regular button. </p>
 *
 * The event handlers for a <code>TouchModeButton</code> also deal with touch-and-hold. For this, boolean flags are
 * used: <code>darkenButtonDown</code> for the "-" (darken) button and <code>lightenButtonDown</code> for the "+"
 * (lighten) button. These flags are set on <code>ACTION_DOWN</code> and cleared on <code>ACTION_UP</code>. Furthermore,
 * on <code>ACTION_DOWN</code> a timer is started. If the <code>ACTION_UP</code> code is delayed because the finger
 * remains on the button, then a timeout will occur, causing the timer's <code>onFinish</code> method to execute. This
 * method calls <code>doAdjustShading</code> which in turn calls <code>adjustShading</code> to adjust the shading.
 * However, <code>doAdjustShading</code> does a bit more. Before calling <code>adjustShading</code> the state of the
 * boolean flags is checked. The method <code>adjustShading</code> is only called if one of the flags is set, indicating
 * that the finger is still touching the button. </p>
 *
 * The <code>doAdjustShading</code> method also restarts the timer; so, the method will execute again on the next
 * timeout. This process repeats as long as the finger is touching either the "-" (darken) or "+" (lighten) button. When
 * the finger is lifted from the button, the corresponding flag is cleared (on <code>ACTION_UP</code>). This is detected
 * in <code>doAdjustShading</code> , and at such time the <code>adjustShading</code> method is not called and the timer
 * is cancelled, thus terminating the adjustment. </p>
 *
 * One additional detail point is that two timers are used: <code>timerLong</code> (250 ms) and <code>timerShort</code>
 * (10 ms). On <code>ACTION_DOWN</code>, <code>timerLong</code> is used. In <code>doAdjustShading</code>,
 * <code>timerShort</code> is used. This arrangement creates the desired effect, often known as "typematic". For a quick
 * tap on the button, the shading is immediately adjusted (on <code>ACTION_UP</code>). For touch-and-hold, there is a
 * slight delay (250 ms) and then adjustments occur continuously (every 10 ms). </p>
 *
 * <b>Screen Rotations</b> </p>
 *
 * If the device undergoes a configuration change, such as a screen rotation, it is desirable to save and restore the
 * current sketch: </p>
 *
 * <center><a href="./javadoc_images/DemoInk-3.jpg"><img src="./javadoc_images/DemoInk-3.jpg" width="250"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoInk-4.jpg"><img src="./javadoc_images/DemoInk-4.jpg" width="425"></a> </center> </p>
 *
 *
 * This is done is the usual manner &mdash; by saving and restoring variables through <code>onSaveInstanceState</code>
 * and <code>onRestoreInstanceState</code>. (See Demo_Layout for an example.) The sketch itself is saved in
 * <code>onSaveInstanceState</code> using </p>
 *
 * <pre>
 *      savedInstanceState.putSerializable(SKETCH_KEY, touchPanel.sketch);
 * </pre>
 *
 * and restored in <code>onRestoreInstanceState</code> using </p>
 *
 * <pre>
 *      touchPanel.sketch = (PaintPanel.Sketch)savedInstanceState.getSerializable(SKETCH_KEY);
 * </pre>
 *
 * This is possible because the sketch is an instanced of the inner class <code>Sketch</code> which "<code>implements
 * Serializable</code>". </p>
 *
 * <b>Save and Send</b> </p>
 *
 * The "regular buttons" in this application operate as expected. Two buttons of particular interest are Save and Send.
 * Tapping the Save button saves the current sketch to a JPG file. The file will be saved in a sub-directory called
 * "DemoInkData" within the device's default external storage directory, as returned by </p>
 *
 * <pre>
 *      Environment.getExternalStorageDirectory();
 * </pre>
 *
 * The directory is <i>not</i> external to the device. As noted in the API, </p>
 *
 * <blockquote><i> This directory can better be thought of as media/shared storage. It is a filesystem that can hold a
 * relatively large amount of data and that is shared across all applications (does not enforce permissions).
 * Traditionally this is an SD card, but it may also be implemented as built-in storage in a device that is distinct
 * from the protected internal storage and can be mounted as a filesystem on a computer. </i></blockquote> </p>
 *
 * The directory will vary by device. On a Google Nexus 4, the directory is </p>
 *
 * <pre>
 *      /mnt/shell/emulated/0
 * </pre>
 *
 * The sub-directory is automatically created, if necessary. The filename will be "<code>SKETCH_</code>", followed by a
 * unique date+time designation, followed by "<code>.jpg</code> ". An example with full path is </p>
 *
 * <pre>
 *      /mnt/shell/emulated/0/DemoInkData/SKETCH_20130226_072558.jpg
 * </pre>
 *
 * </p>
 *
 * Upon saving, it is useful to "scan" the file to make it visible in the file systems (e.g., Windows Explorer): </p>
 *
 * <pre>
 *      MediaScannerConnection.scanFile(this, new String[] { sketchFile.getAbsolutePath() }, null, null);
 * </pre>
 *
 * </p>
 *
 * Then, a brief message pops up to inform the user. A facility in Android to do this is <code>Toast</code>. Here's the
 * code: </p>
 *
 * <pre>
 *      Toast.makeText(this, &quot;Ink sketch saved to:\n&quot; + sketchFile.toString(), Toast.LENGTH_LONG).show();
 * </pre>
 *
 * </p>
 *
 * For example, </p>
 *
 * <center><a href="./javadoc_images/DemoInk-2.jpg"><img src="./javadoc_images/DemoInk-2.jpg" Height="450"></a></center> </p>
 *
 * Programming note: Implementing the file save feature requires the manifest file to include the
 * <code>uses-permission</code> element for writing to external storage. See <code>AndroidManifest.xml</code> for
 * further details. </p>
 *
 * Tapping the Send button launches an email intent to send the current ink sketch as an email attachment. Details are
 * discussed in Demo_VoiceEmail and Demo_Camera. <p>
 *
 * NOTE: Jan 20, 2015 -- app appears to crash when selecting "Send". This is new since updating to Lollipop. But, after
 * clicking OK, the app continues. Seems to relate to Serializable. Tried moving PaintPanel, Sketch, Stroke, and Line
 * into their own files and making Sketch, Stroke, and Line Serializable. Some chatter on StackOverflow, but no solution
 * as yet.<p>
 *
 * NOTE: Jan 6, 2016.  Solved!  The crash noted above is now fixed.  It was fixed by declaring the Paint object in the
 * Stroke class to be "transient".  Indeed, the Paint object was causing the crash, as indicated in the stack trace:
 * "Caused by: java.io.NotSerializableException: android.graphics.Paint".  StackOverflow lead to the fix. However, I
 * don't fully understand the issue because selecting "Send" does not send the sketch object per se.  It sends a bitmap
 * of the view holding the sketch.  As best I can tell, generating the bitmap of the view is independent of how the
 * view was originally painted. In any event, pre-appending "transient" to the declaration of the Paint object in the
 * Stroke class averts the crash. <p>
 *
 * @author (c) Scott MacKenzie, 2011-2019
 */

public class DemoInkActivity extends Activity implements View.OnTouchListener, View.OnClickListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static String CURRENT_COLOR_STRING_KEY = "current_color";
    final static String SKETCH_KEY = "sketch";

    final String WORKING_DIRECTORY = "/DemoInkData/";
    final int[] COLOR = {Color.BLACK, Color.BLUE, Color.CYAN, 0xff7f7f00, Color.GRAY, Color.GREEN, 0xff007f7f,
            Color.MAGENTA, Color.RED, Color.WHITE, Color.YELLOW, 0xff996633};
    final float DEFAULT_STROKE_WIDTH = 10f;
    final int DELAY_LONG = 250; // ms
    final int DELAY_SHORT = 10; // ms
    final int COLOR_MAX = 255;
    final int LIGHTER = 1;
    final int DARKER = 2;
    final boolean END_OF_LINE = true;
    final boolean NOT_END_OF_LINE = false;

    PaintPanel touchPanel;
    View currentColor;
    RelativeLayout colorChipTable;
    Button undoButton, clearButton, saveButton, sendButton, exitButton;
    ColorChip[] colorChip = new ColorChip[COLOR.length];
    ColorChip colorChipLast; // last color chip to have focus
    LineMeter lineMeter;
    TouchModeButton darkenButton, lightenButton;
    File dataDirectory;
    File sketchFile;

    boolean darkenButtonDown, lightenButtonDown;
    ToneGenerator tg;
    CountDownTimer timerLong, timerShort;
    int chipWidth, chipHeight;
    int inkColor = COLOR[1]; // default to blue (see above)
    float strokeWidth = DEFAULT_STROKE_WIDTH;
    float adjustedStrokeWidth;
    float x1, y1, x2, y2; // points for a line segment
    int sampleCount;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
        setContentView(R.layout.main);
        init();
    }

    private void init()
    {
        // determine display width and height
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        touchPanel = (PaintPanel)findViewById(R.id.paintPanel);
        touchPanel.setInkColor(inkColor);
        touchPanel.setStrokeWidth(strokeWidth);
        touchPanel.setOnTouchListener(this);

        // set the width and height of a color chip
        chipWidth = Math.min(dm.widthPixels, dm.heightPixels) / 10;
        chipHeight = chipWidth;

		/*
         * Iterate over findViewById to get references to the colorChip instances. NOTE: For this
		 * for-loop to work, the resource Ids for the colorChips must be consecutive starting at
		 * R.id.color0. As currently implemented in layout/main.xml and layout-land/main.xml, this
		 * is the case since the colorChips are declared consecutively.
		 */
        for (int i = 0, j = R.id.color0; i < colorChip.length; ++i, ++j)
        {
            colorChip[i] = (ColorChip)findViewById(j);
            colorChip[i].setChipColor(COLOR[i]);
            colorChip[i].setWidth(chipWidth);
            colorChip[i].setHeight(chipHeight);
        }

        colorChipTable = (RelativeLayout)findViewById(R.id.colorchiptable);
        colorChipTable.setOnTouchListener(this);

        lineMeter = (LineMeter)findViewById(R.id.linemeter);
        lineMeter.setBackgroundColor(0xff666666);
        lineMeter.setSize(4 * chipWidth, chipHeight / 4);
        lineMeter.setLineLength(Color.red(COLOR[1]) + Color.green(COLOR[1]) + Color.blue(COLOR[1]), 3 * COLOR_MAX);

        darkenButton = (TouchModeButton)findViewById(R.id.darkerbutton);
        darkenButton.setButtonSize(2 * chipWidth, chipHeight);
        darkenButton.text = "-";
        darkenButton.setOnTouchListener(this);
        darkenButtonDown = false;

        lightenButton = (TouchModeButton)findViewById(R.id.lighterbutton);
        lightenButton.setButtonSize(2 * chipWidth, chipHeight);
        lightenButton.text = "+";
        lightenButton.setOnTouchListener(this);
        lightenButtonDown = false;

        undoButton = (Button)findViewById(R.id.undobutton);
        undoButton.setOnClickListener(this);
        clearButton = (Button)findViewById(R.id.clearbutton);
        clearButton.setOnClickListener(this);

        saveButton = (Button)findViewById(R.id.savebutton);
        saveButton.setOnClickListener(this);

        sendButton = (Button)findViewById(R.id.sendbutton);
        sendButton.setOnClickListener(this);

        exitButton = (Button)findViewById(R.id.exitbutton);
        exitButton.setOnClickListener(this);

        currentColor = findViewById(R.id.currentcolor);
        currentColor.setBackgroundColor(COLOR[1]);
        sampleCount = 0;

        timerLong = new CountDownTimer(DELAY_LONG, DELAY_LONG)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                doAdjustShading();
            }
        };

        timerShort = new CountDownTimer(DELAY_SHORT, DELAY_SHORT)
        {
            public void onTick(long millisUntilFinished)
            {
            }

            public void onFinish()
            {
                doAdjustShading();
            }
        };

        tg = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);

        /* 2017-09-28: Hmmm, the following code segment caused my code to fail and terminate.  Seems the problem was
         * that I was targeting SDK 23, where permission to write storage must be requested from the user
         * rather than simply stating such in the manifest.  The fix: target SDK 22.
         */

        // if it does not yet exist, create the directory for the voice recordings to be saved into
        dataDirectory = new File(Environment.getExternalStorageDirectory() + WORKING_DIRECTORY);
        if (!dataDirectory.exists() && !dataDirectory.mkdir())
        {
            Log.i(MYDEBUG, "Failed to create directory: " + dataDirectory.toString());
            super.onDestroy();
            this.finish();
        }
    }

    @Override
    public void onClick(View v)
    {
        if (v == undoButton) // undo the last inking gesture
            touchPanel.removeLastStroke();

        else if (v == clearButton) // clear all the ink from the canvas
            touchPanel.clear();

        else if (v == saveButton) // save the current ink sketch as a JPG file
        {
            // enable the view to create a cache of the canvas
            touchPanel.setDrawingCacheEnabled(true);

            // retrieve the canvas as a bitmap
            Bitmap bm = touchPanel.getDrawingCache();

            // get date and time info for the file name, making it unique
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis());

            // create the file into which the sketch will be saved
            sketchFile = new File(dataDirectory.getPath() + File.separator + "SKETCH_" + timeStamp + ".jpg");

            // save it!
            saveBitmapAsCompressedJPGFile(bm, sketchFile);

			/*
             * Make the saved data file visible in Windows Explorer. There seems to be bug doing
			 * this with Android 4.4. I'm using the following code, instead of sendBroadcast. See...
			 * 
			 * http://code.google.com/p/android/issues/detail?id=38282
			 */
            MediaScannerConnection.scanFile(this, new String[] {sketchFile.getAbsolutePath()}, null, null);

            // output a brief pop-up informing the user of the result
            Toast.makeText(this, "Ink sketch saved to:\n" + sketchFile.toString(), Toast.LENGTH_LONG).show();

        } else if (v == sendButton) // send the canvas bitmap by email using an intent
        {
            // enable the view to create a cache of the canvas
            touchPanel.setDrawingCacheEnabled(true);

            // retrieve the canvas as a bitmap
            //Bitmap bm = touchPanel.getDrawingCache();

            //String path = Images.Media.insertImage(getContentResolver(), bm, "bitmap", null);
            //Uri screenshotUri = Uri.parse(path);
            Uri screenshotUri = Uri.parse("file://" + sketchFile); // bug fix: Jan 31, 2018 (uses correct filename)
            Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
            emailIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Inked sketch");
            emailIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
            emailIntent.setType("image/png");
            startActivity(Intent.createChooser(emailIntent, "Send email using..."));

        } else if (v == exitButton) // exit DemoInk
        {
            this.finish();
        }
    }

    // Save a bitmap as a compressed JPEG file
    private void saveBitmapAsCompressedJPGFile(Bitmap b, File f)
    {
        try
        {
            FileOutputStream fos = new FileOutputStream(f);
            b.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (Exception e)
        {
            // output a brief pop-up informing the user
            Toast.makeText(this, "Exception writing image file!", Toast.LENGTH_SHORT).show();
            // ... and also send a message to the LogCat window
            Log.i(MYDEBUG, "Exception writing image file: " + e);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        final float x = me.getX();
        final float y = me.getY();

        if (v == touchPanel)
        {
            switch (me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    ++sampleCount;
                    x2 = x;
                    y2 = y;
                    break;

                case MotionEvent.ACTION_MOVE:

					/*
                     * Instead of processing just the current x/y touch point, we'll process all the
					 * touch points associated with this ACTION_MOVE -- to make the ink path as
					 * smooth as possible. As noted in the API for MotionEvent, "For efficiency,
					 * motion events with ACTION_MOVE may batch together multiple movement samples
					 * within a single object." Getting all the samples involves calling
					 * getHistoricalX and getHistoricalY, rather than getX and getY. The value of
					 * historySize on a Nexus 4 is generally small (typically 1 or 2), so the
					 * improvement is likely minor. The effect may be more pronounced on other
					 * devices.
					 */

                    // if there are historical points, process them (this does not include the current point)
                    final int historySize = me.getHistorySize();
                    for (int i = 0; i < historySize; ++i)
                    {
                        ++sampleCount;
                        x1 = x2; // last point becomes first in new line segment
                        y1 = y2;
                        x2 = me.getHistoricalX(i); // new end of line segment
                        y2 = me.getHistoricalY(i);
                        adjustedStrokeWidth = getPressurizedStrokeWidth(me.getHistoricalPressure(i));
                        touchPanel.addLine(x1, y1, x2, y2, adjustedStrokeWidth, NOT_END_OF_LINE);
                    }

                    // now process the current point
                    x1 = x2; // last point becomes fist in new line segment
                    y1 = y2;
                    x2 = x; // new end of line segment
                    y2 = y;
                    adjustedStrokeWidth = getPressurizedStrokeWidth(me.getPressure());
                    touchPanel.addLine(x1, y1, x2, y2, adjustedStrokeWidth, NOT_END_OF_LINE);
                    break;

                case MotionEvent.ACTION_UP:
                    x1 = x2; // last point becomes fist in new line segment
                    y1 = y2;
                    x2 = x; // new end of line segment
                    y2 = y;
                    adjustedStrokeWidth = getPressurizedStrokeWidth(me.getPressure());
                    touchPanel.addLine(x1, y1, x2, y2, adjustedStrokeWidth, END_OF_LINE); // true = end of stroke
                    sampleCount = 0;
                    v.performClick(); // to avoid accessibility warning
                    break;
            }
        } else if (v == colorChipTable) // finger on color chip table
        {
            ColorChip colorChipNew = findColorChip(me.getX(), me.getY());

            // check if contact point shifted outside color chip table (ignore)
            if (colorChipNew == null)
            {
                colorChipLast.touchEffectOff();
                return true; // contact point shifted outside color chip table (ignore)
            }

            switch (me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    colorChipNew.touchEffectOn();
                    break;

                case MotionEvent.ACTION_MOVE:
                    if (colorChipLast != null && colorChipLast != colorChipNew)
                    {
                        colorChipLast.touchEffectOff();
                        colorChipNew.touchEffectOn();
                    }
                    break;

                case MotionEvent.ACTION_UP: // new color
                    colorChipNew.touchEffectOff();
                    colorChipLast = null;
                    inkColor = colorChipNew.getChipColor();
                    updateColor(inkColor);
                    tg.startTone(ToneGenerator.TONE_DTMF_C, 50);
                    break;
            }
            colorChipLast = colorChipNew;
            return true;
        } else if (v == darkenButton)
        {
            switch (me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    darkenButtonDown = true;
                    timerLong.start();
                    break;

                case MotionEvent.ACTION_UP:
                    darkenButtonDown = false;
                    adjustShading(DARKER);
            }
            return false; // let the view's onTouchEvent method execute as well
        } else if (v == lightenButton)
        {
            switch (me.getAction() & MotionEvent.ACTION_MASK)
            {
                case MotionEvent.ACTION_DOWN:
                    lightenButtonDown = true;
                    timerLong.start();
                    break;

                case MotionEvent.ACTION_UP:
                    lightenButtonDown = false;
                    adjustShading(LIGHTER);
            }
            return false; // let the view's onTouchEvent method execute as well
        }
        return true;
    }

    private void updateColor(int color)
    {
        touchPanel.setInkColor(color);
        currentColor.setBackgroundColor(color);
        lineMeter.setLineLength(Color.red(color) + Color.green(color) + Color.blue(color), 3 * COLOR_MAX);
    }

    /*
     * This routine adjusts the stroke width by scaling it according to the raw pressure values. The
     * scaling constants were determined by trial and error, and are likely to vary by device.
     */
    private float getPressurizedStrokeWidth(float pressure)
    {
        // Reasonable values for Google Nexus 4
        final float MIN = 0.565f; // min value for pressure
        final float MAX = 0.800f; // max (up to about 1.6 for a "fat touch")
        final float SCALE = 10f; // scaling factor for pressure difference between min and max

        // Reasonable values for Samsung Galaxy Tab 10.1
        // final float MIN = 0.100f; // min value for pressure
        // final float MAX = 0.390f; // max
        // final float SCALE = 10f; // scaling factor for pressure difference between min and max

        return strokeWidth * SCALE * ((pressure - MIN) / MAX);
    }

    private void adjustShading(int direction)
    {
        // extract the RBG components from inkColor
        int red = Color.red(inkColor);
        int green = Color.green(inkColor);
        int blue = Color.blue(inkColor);
        if (direction == DARKER)
        {
            // attempt a 1% decrease on each color
            red = red - (int)(0.01f * COLOR_MAX) < 0 ? 0 : red - (int)(0.01f * COLOR_MAX);
            green = green - (int)(0.01f * COLOR_MAX) < 0 ? 0 : green - (int)(0.01f * COLOR_MAX);
            blue = blue - (int)(0.01f * COLOR_MAX) < 0 ? 0 : blue - (int)(0.01f * COLOR_MAX);
        } else if (direction == LIGHTER)
        {
            // attempt a 1% increase on each color
            red = red + (int)(0.01f * COLOR_MAX) > COLOR_MAX ? COLOR_MAX : red + (int)(0.01f * COLOR_MAX);
            green = green + (int)(0.01f * COLOR_MAX) > COLOR_MAX ? COLOR_MAX : green + (int)(0.01f * COLOR_MAX);
            blue = blue + (int)(0.01f * COLOR_MAX) > COLOR_MAX ? COLOR_MAX : blue + (int)(0.01f * COLOR_MAX);
        }

        // put the adjusted RGB components back together in inkColor
        inkColor = 0xff000000 + (red << 16) + (green << 8) + blue;
        touchPanel.setInkColor(inkColor);
        currentColor.setBackgroundColor(inkColor);

        // adjust the line meter according to the combined magnitude of the RGB values
        lineMeter.setLineLength(red + green + blue, 3 * COLOR_MAX);
    }

    /*
     * doAdjustShading - called from the timer upon each timeout. Provided the darker or lighter
     * boolean flag is set, the corresponding method is called to darken or lighten the color in the
     * ink. After this, the timer is started again. However, if neither flag is set, the timer is
     * cancelled, thus terminating the touch-and-hold behaviour. Note that the darker and lighter
     * boolean flags are set or cleared in response to finger down or finger up events on the darker
     * or lighter buttons (see above).
     */
    private void doAdjustShading()
    {
        if (darkenButtonDown)
        {
            adjustShading(DARKER);
            timerShort.start();
        } else if (lightenButtonDown)
        {
            adjustShading(LIGHTER);
            timerShort.start();
        } else
        {
            timerLong.cancel();
            timerShort.cancel();
        }
    }

    // findColorChip - return the color chip at the specified x/y touch location
    public ColorChip findColorChip(float xArg, float yArg)
    {
        for (ColorChip cc : colorChip)
        {
            if (xArg >= cc.getLeft() && xArg <= cc.getRight() && yArg >= cc.getTop()
                    && yArg <= cc.getBottom())
                return cc;
        }
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putInt(CURRENT_COLOR_STRING_KEY, inkColor);
        savedInstanceState.putSerializable(SKETCH_KEY, touchPanel.sketch);
        super.onSaveInstanceState(savedInstanceState);
    }

    /*
     * onRestoreInstanceState - This method and the next are used to save/restore the sketch and the
     * current ink color. For this, we're following the recommendations described in the Android API
     * Guides. See...
     *
     * http://developer.android.com/guide/topics/resources/runtime-changes.html
     */
    @SuppressWarnings("unchecked")
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        touchPanel.sketch = (Sketch)savedInstanceState.getSerializable(SKETCH_KEY);
        inkColor = savedInstanceState.getInt(CURRENT_COLOR_STRING_KEY);
        updateColor(inkColor);
    }
}
