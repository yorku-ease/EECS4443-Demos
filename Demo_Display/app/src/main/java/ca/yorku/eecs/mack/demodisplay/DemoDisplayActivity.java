package ca.yorku.eecs.mack.demodisplay;

import android.os.Bundle;
import android.app.Activity;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Surface;
import android.widget.TextView;

import java.util.Locale;

/**
 * Demo_Display - demo to determine, report, and use display properties.</p>
 *
 * Related information: <p>
 *
 * <blockquote> API References: <p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/util/DisplayMetrics.html">
 * <code>DisplayMetrics</code></a> <li><a href="http://developer.android.com/reference/android/content/res/Configuration.html">
 * <code>Configuration</code></a> <li><a href="http://developer.android.com/reference/android/content/res/Resources.html">
 * <code>Resources</code></a> <li><a href="http://developer.android.com/reference/android/view/Display.html">
 * <code>Display</code></a> <li><a href="http://developer.android.com/reference/android/view/WindowManager.html">
 * <code>WindowManager</code></a> <li><a href="http://developer.android.com/reference/android/view/View.html"><code>View</code></a>
 * </ul> <p>
 *
 * API Guides: <p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/topics/ui/custom-components.html#custom">Fully Customized
 * Components</a> </ul> </blockquote>
 *
 * This program demonstrates the means to retrieve and use information about the display on a device running an Android
 * app. The information includes both native properties (properties that never change) and current properties
 * (properties that change if the device is rotated). </p>
 *
 * The following are native properties that do not change for a particular device: </p>
 *
 * <ul> <li>The pixel density of the display <li>The default orientation of the display </ul><p>
 *
 * The following properties change if the device is rotated: </p>
 *
 * <ul> <li>The width and height of the display <li>The orientation of the display <li>The rotation of the display
 * </ul><p>
 *
 * The best way to appreciate this demo is to run it and observe its behaviour as the device is rotated. Doing so with
 * an LG Nexus 4 yields the following displays for portrait (left) or landscape (right) orientation:</p>
 *
 * <center> <a href="./javadoc_images/DemoDisplay-1.jpg"><img src="./javadoc_images/DemoDisplay-1.jpg" width="240"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoDisplay-2.jpg"><img src="./javadoc_images/DemoDisplay-2.jpg" width="400"></a> </center> </p>
 *
 * The following shows the output to the LogCat window when the app launches with the device upright and after a few
 * screen rotations: </p>
 *
 * <center> <a href="./javadoc_images/DemoDisplay-3.jpg"><img src="./javadoc_images/DemoDisplay-3.jpg" width="700"></a></center> </p>
 *
 * Five lines of display information are sent to the LogCat window when the app launches and  each time the screen is
 * rotated. The first two lines give native properties that do not change. The next three lines give properties that
 * change as the screen rotates. Let's examine each property.</p>
 *
 * <!----------------------------------------------------------------------------------------> <b>Density</b></p>
 *
 * The pixel density of the display is a property that is established when the device is manufactured. It does not
 * change. Note that the term "density", as used in the Android API, is not really a density. It is a <i>scaling
 * factor</i> relative to the Android baseline density of 160 dpi (dots per inch), which is common for many Android
 * phones and tablets. Thus, density = 1 means the device's display density is 160 dots per inch. As seen above, density
 * = 2 on an LG Nexus 4, which means there are 320 pixels per inch on the display. </p>
 *
 * The display density is obtained by first getting a <code>DisplayMetrics</code> instance, then using the window
 * manager to initialize the display metrics according to the display on which the application is running: </p>
 *
 * <pre>
 *      DisplayMetrics dm = new DisplayMetrics();
 *      this.getWindowManager().getDefaultDisplay().getMetrics(dm);
 * </pre>
 *
 * The display density is retrieved from the <code>density</code> field in the <code>DisplayMetrics</code> instance:
 * </p>
 *
 * <pre>
 *      float density = dm.density;
 * </pre>
 *
 * <!----------------------------------------------------------------------------------------> <b>Orientation
 * (Current)</b> </p>
 *
 * Android phones are normally operated upright &ndash; in portrait orientation. Android tablets, on the other hand, are
 * generally operated on their side &ndash; in landscape orientation. Of course, either type of device may operate in
 * either orientation. So, the device has both a natural or <i>default orientation</i> and a <i>current orientation</i>.
 * </p>
 *
 * The current orientation is stored in the <code>orientation</code> field of the <code>Configuration</code> object
 * returned by <code>getConfiguration</code> which is invoked on the <code>Resources</code> object returned by the
 * Activity's <code>getResources</code> method. Whew! That's a mouthful! As typical with Android programming, you can
 * proceed in stages to take-on complex code requirements: </p>
 *
 * <pre>
 *      Resources r = this.getResources();
 *      Configuration c = r.getConfiguration();
 *      int orientation = c.orientation;
 *
 *      if (orientation == Configuration.ORIENTATION_LANDSCAPE)
 *           Log.i(MYDEBUG, "The device orientation is LANDSCAPE");
 *      else
 *           Log.i(MYDEBUG, "The device orientation is PORTRAIT");
 * </pre>
 *
 * </p>
 *
 * The resulting code is readable, but includes needless declarations. Alternatively, you can chain methods
 * together:</p>
 *
 * <pre>
 *      if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
 *           Log.i(MYDEBUG, "The device orientation is LANDSCAPE");
 *      else
 *           Log.i(MYDEBUG, "The device orientation is PORTRAIT");
 * </pre>
 *
 * </p>
 *
 * In this case, the code is tight and efficient, but cryptic. The approach in the demo lies somewhere in the middle.
 * Consult for details. </p>
 *
 * <!----------------------------------------------------------------------------------------> <b>Rotation</b> </p>
 *
 * As well as having an orientation property, Android devices have a <i>rotation</i> property. Rotation is referenced
 * with respect to the device's natural or default orientation. Thus, an Android phone operating in its normal or
 * upright position has a rotation of 0 degrees. An Android tablet operating in its normal or landscape position also
 * has a rotation of 0 degrees. When either of these devices is turned on its side, the resulting rotation is either
 * <code>Surface.ROTATION_90</code> or <code>Surface.ROTATION_270</code> depending on the direction it was turned. The
 * angle is the rotation of the rendered graphics, which is opposite the rotation of the device. So, if the device is
 * rotated 90 degrees <i>counter-clockwise</i>, the display contents are rotated 90 degrees <i>clockwise</i> to
 * compensate. Thus, the rotation value is <code>Surface.ROTATION_90</code>. </p>
 *
 * Code that executes according to the display rotation may be setup as follows: </p>
 *
 * <pre>
 *     int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
 *     switch (rotation)
 *     {
 *          case Surface.ROTATION_0:
 *               ...
 *               break;
 *
 *          case Surface.ROTATION_90:
 *               ...
 *               break;
 *
 *          case Surface.ROTATION_270:
 *               ...
 *               break;
 *
 *          case Surface.ROTATION_180:
 *               ...
 *               break;
 *     }
 * </pre>
 *
 * </p>
 *
 * <!----------------------------------------------------------------------------------------> <b>Orientation
 * (Native)</b> </p>
 *
 * Unfortunately, there is no simple way to determine whether an Android device's natural or default orientation is
 * landscape or portrait. The default orientation is a function of the current orientation and the current rotation. It
 * must be computed. Here's a method that returns the <code>Configuration</code> constant for the device's natural or
 * default orientation: </p>
 *
 * <pre>
 *      public int getDefaultOrientation()
 *      {
 *           int orientation = this.getResources().getConfiguration().orientation;
 *           int rotation = this.getWindowManager().getDefaultDisplay().getRotation();
 *
 *           if (((rotation == Surface.ROTATION_0  || rotation == Surface.ROTATION_180) &&
 *                   orientation == Configuration.ORIENTATION_LANDSCAPE)
 *                   || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
 *                   orientation == Configuration.ORIENTATION_PORTRAIT))
 *                return Configuration.ORIENTATION_LANDSCAPE;
 *           else
 *                return Configuration.ORIENTATION_PORTRAIT;
 *      }
 * </pre>
 *
 * </p>
 *
 * The value returned is consistent for any device. This is apparent in the <code>logcat</code> output above which
 * consistently indicates "Natural orientation = PORTRAIT", which is the default orientation for the LG Nexus 4. </p>
 *
 * The code above for <code>getDefaultOrientation</code> was derived from a StackOverflow posting. <a href=
 * "http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation-on
 * -android-i-e-get-landscape" >Click here</a> to view. </p>
 *
 * <!----------------------------------------------------------------------------------------> <b>Display Size</b></p>
 *
 * The <code>DisplayMetrics</code> instance also includes fields for the width and height of the display &ndash; the
 * <i>display size</i>. These are accessed as follows: </p>
 *
 * <pre>
 *      DisplayMetrics dm = new DisplayMetrics();
 *      this.getWindowManager().getDefaultDisplay().getMetrics(dm);
 *
 *      int width = dm.widthPixels;
 *      int height = dm.heightPixels;
 * </pre>
 *
 * The width and height change as the screen is rotated. One might expect that the display height in portrait mode
 * equals the display width in landscape mode. This is not necessarily the case. Notice in the screen snaps and
 * <code>logcat</code> output above that the LG Nexus 4's display height is 1184 pixels in portrait orientation, while
 * the display width is 1196 pixels in landscape orientation. Why are these values different? Here's an explanation.
 * </p>
 *
 * The values stored in the <code>widthPixels</code> and <code>heightPixels</code> fields of the
 * <code>DisplayMetrics</code> instance are not necessarily the width and height of the physical display. In fact, the
 * LG Nexus 4's physical screen resolution is 768 &times; 1280 pixels. The values reported exclude "system decor
 * elements" &ndash; screen elements that are always present. One such element since Android 4.0 (Ice Cream Sandwich) is
 * the Navigation Bar. The Navigation Bar is rendered on the display and it is always present, but it is unavailable for
 * use in an application's UI window. So, the space for the Navigation Bar is excluded in the width and height
 * properties. The difference between height-portrait (1184) and width-landscape (1196) occurs because the rendering of
 * the Navigation Bar is slightly different in each orientation. The Navigation Bar is 1280 &minus; 1184 = 96 pixels
 * high in portrait orientation and 1280 &minus; 1196 = 84 pixels wide in landscape orientation.</p>
 *
 * <!----------------------------------------------------------------------------------------> <b>Relative vs. Absolute
 * Metrics</b> </p>
 *
 * Laying out and sizing components is one of the most important and challenging tasks in creating a mobile UI. When
 * using XML to create a layout, the size of components is usually specified in relative terms, using attributes such as
 * <code>android:layout_width</code> and <code>android:layout_height</code> with values such as
 * <code>"wrap_content"</code> or <code>"match_parent"</code>. The exact size is not determined until the layout is
 * created (or <i>inflated</i> in Android terms). </p>
 *
 * However, in some cases you might want to control the size of components in absolute terms. For example, you might
 * want a button that is 1-inch wide or a View that contains a &frac12;-inch wide circle: </p>
 *
 * <center> <a href="./javadoc_images/DemoDisplay-4.jpg"><img src="./javadoc_images/DemoDisplay-4.jpg" width="400"></a></center> </p>
 *
 * Furthermore, you might want the components to be the same size on any device running the app. Let's examine some of
 * the details in controlling the absolute size of components. </p>
 *
 * The technique for setting the absolute size a component depends on how the component is coded in the UI. Specifying
 * the exact size for a component in XML is simple. For a button (or any View), the width may be specified as
 * follows:</p>
 *
 * <pre>
 *      android:layout_width="160dp"
 * </pre>
 *
 * The "dp" above is for <i>density independent pixel</i>. Before the number is used in sizing the component, it is
 * scaled according the display density for the device on which the app is running. The result is a button that is the
 * same width on every device. Scaling is performed relative to the baseline density for Android devices: 160 dpi. If
 * the device actually has a 160-dpi display, then "160dp" produces a button that is 160 pixels wide. If the device has
 * a 320-dpi display, then "160dp" produces a button that is 320 pixels wide. Either way, the button is 1-inch wide. The
 * programmer needn't worry about the details; they are handled by the window manager when the layout is inflated. </p>
 *
 * The situation for the circle above is quite different. The circle is created within the <code>onDraw</code> method in
 * a custom class, sub-classed from View. The <code>onDraw</code> method operates at a much lower level than the
 * attributes in an XML file or the methods invoked on View objects. Everything drawn within a View using
 * <code>onDraw</code> is specified in absolute pixel coordinates. So, if we want a circle that is, say, &frac12;-inch
 * wide, there is a bit of work to do. Actually, not much work. It's still quite simple. An important variable for the
 * custom view is the density of the display. As noted earlier, this information, which is actually a scaling factor, is
 * held in the <code>density</code> field in a <code>DisplayMetrics</code> instance.  Since we are working within a
 * View, not an Activity, the programming details are slightly different.  This is explained next. </p>
 *
 * In the custom view, the pixel density of the device's display is retrieved through the Context object passed in to
 * the constructor. On the Context object, the <code>getResources</code> method is called to retrieve a reference to the
 * application's resources. On the Resources object, the <code>getDisplayMetrics</code> method is called to retrieve a
 * <code>DisplayMetrics</code> object which holds information about the device's display. The <code>density</code> field
 * in the <code>DisplayMetrics</code> object field holds the pixel density of the display: <p>
 *
 * <pre>
 *      pixelDensity = c.getResources().getDisplayMetrics().density;
 * </pre>
 *
 * In the custom view, we also define a constant representing an absolute size in density independent pixels: </p>
 *
 * <pre>
 *      final static int HALF_INCH = 80;
 * </pre>
 *
 * In the <code>onDraw</code> method, the <code>drawCircle</code> method is called to draw the circle. Four arguments
 * are required: (i) <i>x</i>-coordinate of the center of the circle, (ii) <i>y</i>-coordinate of the center of the
 * circle, (iii) the radius of the circle, and (iv) a reference to a Paint object to paint the circle. The radius is
 * adjusted to ensure the circle has the desired absolute size on any device: </p>
 *
 * <pre>
 *      &#64;Override
 *      protected void onDraw(Canvas canvas)
 *      {
 *           // draw a half-inch circle in the middle of our half-inch view
 *           float x = this.getWidth() / 2f;
 *           float y = this.getHeight() / 2f;
 *           float radius = (HALF_INCH * pixelDensity) / 2f;
 *           canvas.drawCircle(x, y, radius, paint);
 *      }
 * </pre>
 *
 * The code above draws a &frac12;-inch circle in the center of the view. The view itself is a &frac12; &times; &frac12;
 * inch square. This is ensured by implementing the protected method <code>onMeasure</code> and including a call to
 * <code>setMeasuredDimension</code>, passing the desired absolute width and height of the view: </p>
 *
 * <pre>
 *      &#64;Override
 *      protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
 *      {
 *           // Ensure the view is a half-inch wide and a half-inch high on any device.
 *           setMeasuredDimension((int)(HALF_INCH * pixelDensity), (int)(HALF_INCH * pixelDensity));
 *      }
 * </pre>
 *
 * For further details, consult the API Reference for the <code>View</code> class and the API Guide "Fully Customized
 * Components" (links above). </p>
 *
 * One additional feature is that the text drawn in the circle is centered and spans 90% of the diameter. Consult the
 * source code for details. </p>
 *
 * @author (c) Scott MacKenzie 2014-2019
 */

public class DemoDisplayActivity extends Activity
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages
    final static String CIRCLE_TEXT = "Circle";

    TextView pixelDensityValue, defaultOrientationValue, currentDisplaySizeValue,
            currentOrientationValue,
            currentRotationValue;
    HalfInchView halfInchView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        // Note: Casts removed (automatically done by compiler -- see API)
        pixelDensityValue = findViewById(R.id.pixel_density_value);
        defaultOrientationValue = findViewById(R.id.default_orientation_value);
        currentDisplaySizeValue = findViewById(R.id.current_display_size_value);
        currentOrientationValue = findViewById(R.id.current_orientation_value);
        currentRotationValue = findViewById(R.id.current_rotation_value);
        halfInchView = findViewById(R.id.half_inch_view);

        if (savedInstanceState == null)
            Log.i(MYDEBUG, "Launching...");

        reportDisplayDensity();
        reportDefaultOrientation();
        reportCurrentDisplaySize();
        reportCurrentOrientation();
        reportCurrentRotation();

        // give the text to the view drawing the half-inch circle
        halfInchView.setCircleText(CIRCLE_TEXT);
    }

    // determine and report the display density
    public void reportDisplayDensity()
    {
        float pixelDensity = getPixelDensity();

        // report density
        Log.i(MYDEBUG, "Pixel density = " + pixelDensity + " (relative to 160 dpi)");
        pixelDensityValue.setText(String.format(Locale.CANADA, "%f (relative to 160 dpi)", pixelDensity));
    }

    // determine and report the device's default or natural orientation
    public void reportDefaultOrientation()
    {
        switch (getDefaultOrientation())
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                Log.i(MYDEBUG, "Natural orientation = LANDSCAPE");
                defaultOrientationValue.setText(R.string.landscape);
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                Log.i(MYDEBUG, "Natural orientation = PORTRAIT");
                defaultOrientationValue.setText(R.string.portrait);
                break;
        }
    }

    // determine and report the current display size
    public void reportCurrentDisplaySize()
    {
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        Log.i(MYDEBUG, "Current display size: width = " + width + ", height = " + height);
        currentDisplaySizeValue.setText(String.format(Locale.CANADA, "w = %d, h = %d", width, height));
    }

    // determine and report the current display orientation
    public void reportCurrentOrientation()
    {
        Configuration config = this.getResources().getConfiguration();
        String s = "";

        switch (config.orientation)
        {
            case Configuration.ORIENTATION_LANDSCAPE:
                s = "LANDSCAPE";
                break;

            case Configuration.ORIENTATION_PORTRAIT:
                s = "PORTRAIT";
                break;
        }
        Log.i(MYDEBUG, "Current orientation = " + s);
        currentOrientationValue.setText(s);
    }

    // determine and report the current display rotation
    public void reportCurrentRotation()
    {
        int value = -1;
        switch (getCurrentRotation())
        {
            case Surface.ROTATION_0:
                value = 0;
                break;

            case Surface.ROTATION_180:
                value = 180;
                break;

            case Surface.ROTATION_270:
                value = 270;
                break;

            case Surface.ROTATION_90:
                value = 90;
                break;
        }
        Log.i(MYDEBUG, "Current rotation = " + value + " degrees");
        currentRotationValue.setText(String.format(Locale.CANADA, "%d degrees", value));
    }

    /*
     * Get the device's default/natural orientation. The default orientation is a function of the
     * current orientation combined with the current rotation. See...
     *
     * http://stackoverflow.com/questions/4553650/how-to-check-device-natural-default-orientation
     * -on-
     * android-i-e-get-landscape
     */
    public int getDefaultOrientation()
    {
        int orientation = this.getResources().getConfiguration().orientation;
        int rotation = getCurrentRotation();

        if (((rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) && orientation
                == Configuration.ORIENTATION_LANDSCAPE)
                || ((rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270) &&
                orientation == Configuration.ORIENTATION_PORTRAIT))
            return Configuration.ORIENTATION_LANDSCAPE;
        else
            return Configuration.ORIENTATION_PORTRAIT;
    }

    // return the current rotation of the display
    public int getCurrentRotation()
    {
        return this.getWindowManager().getDefaultDisplay().getRotation();
    }

    // return the pixel density of the device's display
    public float getPixelDensity()
    {
        // determine the pixel density of the device's display
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    @Override
    public void onPause()
    {
        Log.i(MYDEBUG, "================================================");
        Log.i(MYDEBUG, "Configuration Changing...");
        super.onPause();
    }
}
