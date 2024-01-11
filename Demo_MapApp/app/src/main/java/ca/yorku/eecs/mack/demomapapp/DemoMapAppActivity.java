package ca.yorku.eecs.mack.demomapapp;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;
import android.util.Log;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_MapApp - demonstrates a UI containing a Google Maps map. Also demonstrates using a Navigation Drawer. <p>
 *
 * Related Information: </p>
 *
 * <blockquote> Developer Guide: </p>
 *
 * <ul> <li><a href="https://developers.google.com/maps/documentation/android/">Google Maps Android API v2</a> (and, in
 * particular, <a href="https://developers.google.com/maps/documentation/android/start">Getting Started</a>) </ul> <p>
 *
 * Google Services:</p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/com/google/android/gms/maps/GoogleMap.html">
 * <code>GoogleMap</code></a> <li><a href= "http://developer.android.com/reference/com/google/android/gms/maps/SupportMapFragment.html">
 * <code>SupportMapFragment</code></a> <li><a href= "http://developer.android.com/reference/com/google/android/gms/maps/model/CameraPosition.html">
 * <code>CameraPosition</code></a> <li><a href= "http://developer.android.com/reference/com/google/android/gms/maps/model/CameraPosition.Builder.html"
 * ><code>CameraPosition.Builder</code></a> <li><a href= "http://developer.android.com/reference/com/google/android/gms/maps/CameraUpdateFactory.html">
 * <code>CameraUpdateFactory</code></a> </ul> <p>
 *
 * Reference: <p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/support/v4/app/FragmentActivity.html#">
 * <code>FragmentActivity</code></a> </ul> <p>
 *
 * Training: </p>
 *
 * <ul> <li><a href="http://developer.android.com/training/implementing-navigation/nav-drawer.html">Creating a
 * Navigation Drawer</a> </ul> <p>
 *
 * </ul> </blockquote> </p>
 *
 * The cornerstone of this demo is the Google Maps Android API v2 (link above) which is used to integrate maps into a
 * mobile UI. Developing an app using Google Maps requires the development environment to include the Google Play
 * services SDK. As well, each app &mdash; whether a simple demo or an app distributed on Google Play &mdash; requires
 * the manifest to include a Google Maps API key. The key enables access to the Google Maps servers. The details for
 * setting up the development environment and obtaining the API key are described in the Developer Guide (link above).
 * Follow the instructions beginning at <a href="https://developers.google.com/maps/documentation/android/start">Getting
 * Started</a>. </p>
 *
 * Once the development environment is setup and a project is created and configured with the API key, it is remarkably
 * simple to include a map in a UI. Many complex operations are handled automatically by the API. The app developer can
 * focus on the UI while avoiding the messy details of accessing Google Maps servers, downloading data, or caching map
 * tiles. </p>
 *
 * The <code>GoogleMap</code> class models the map object within the app. Within the UI, a map is represented either by
 * a <code>MapFragment</code> or a <code>MapView</code>. This demo uses a variant of <code>MapFragment</code> called
 * <code>SupportMapFragment</code>, a wrapper class that handles a map view and the necessary lifecycle needs. Since it
 * is a fragment, it is added directly to the activity's layout file, in this case, <code>main.xml</code>: <p>
 *
 * <pre>
 *      &lt;fragment
 *           android:id="@+id/map"
 *           android:layout_width="match_parent"
 *           android:layout_height="0dp"
 *           android:layout_weight="1"
 *           class="com.google.android.gms.maps.SupportMapFragment" /&gt;
 * </pre>
 *
 * The main activity extends <code>FragmentActivity</code>, which is the base class for activities that use the
 * support-based <code>Fragment</code> API. Note above that the Id for the <code>SupportMapFragment</code> is
 * <code>map</code>. The map itself is declared and then retrieved in activity's <code>onCreate</code> method: </p>
 *
 * <pre>
 *      GoogleMap myMap;
 *      ...
 *      myMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
 * </pre>
 *
 * Thereafter, it is a simple matter of listening for UI events and then manipulating the map through methods of the
 * <code>GoogleMap</code> class and related classes such as <code>CameraPosition</code>.</p>
 *
 * Upon launch, Demo_MapApp presents a map of Ontario (below left). The UI includes four buttons along the top to
 * directly change to a new map. Along the bottom there are UI elements for the camera control mode (discussed shortly).
 * Within the map, there is a marker in the center of Ontario, a my-location marker in the Toronto area, zoom buttons at
 * the bottom right, and a seek button at the top right. (Note: Location sensing must be enabled on the device: Settings
 * > Location > On.) Tapping the "York U" button changes the map to a close up of York University (below center). The
 * transition is smoothed using animation.</p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-1.jpg"><img src="./javadoc_images/DemoMapApp-1.jpg" width="250"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoMapApp-2.jpg"><img src="./javadoc_images/DemoMapApp-2.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoMapApp-10.jpg"><img src="./javadoc_images/DemoMapApp-10.jpg" width="250"></a></center></p>
 *
 * Did you notice the marker on the York U map in the vicinity of the Lassonde Building? This is a custom marker. Two
 * taps on the "+" button zooms in. A tap on the marker brings up an info window for the map location (above right).
 * We'll described how to set this up later.</p>
 *
 * <!---------------------------------------------------------------------------------------------> <b>Camera View</b>
 * </p>
 *
 * In the Google Map API, a map image is considered a <i>camera view</i>. The idea is that the map is the view through a
 * camera lens positioned somewhere in the sky, looking down at the Earth. For any map, there are four parameters of the
 * camera that set the view. These are stored in a <code>CameraPosition</code> object. The parameters are as follows:
 * </p>
 *
 * <center> <table border="1" cellspacing="0" cellpadding="6" width="80%"> <tr bgcolor="#cccccc"> <th>Camera View
 * Parameter <th>Data Type <th>Description
 *
 * <tr> <td align="center">Target <td align="center"><code>LatLng</code> <td>Sets the location the camera is pointing
 * at. The target location is specified through a <code>LatLng</code> object which holds the latitude and longitude of
 * the location.
 *
 * <tr> <td align="center">Zoom <td align="center"><code>float</code> <td>Sets the zoom level of the camera. Zoom ranges
 * from 0 to 21. At zoom = 0, the entire world is rendered with a width of 256 dp (density independent pixels). Adding 1
 * to zoom doubles the width.
 *
 * <tr> <td align="center">Bearing <td align="center"><code>float</code> <td>Sets the direction the camera is pointing,
 * in degrees clockwise from north. North is 0 degrees (= 360 degrees). South is 180 degrees.
 *
 * <tr> <td align="center">Tilt <td align="center"><code>float</code> <td>Sets the angle, in degrees, of the camera from
 * the nadir (directly facing the Earth). The minimum is 0 (looking directly down). The maximum ranges from 30 to 67.5,
 * depending on the current zoom. (<a href= "http://developer.android.com/reference/com/google/android/gms/maps/model/CameraPosition.Builder.html#tilt%28float%29"
 * >Click here</a> to view the algorithm for max tilt.)
 *
 * </table> </center> </p>
 *
 * More complete details are found in the APIs for <code>CameraPosition</code> and <code>CameraPosition.Builder</code>
 * and in the topic <a href="https://developers.google.com/maps/documentation/android/views">Changing the View</a> in
 * the Google Maps Android API v2 Developer's Guide.</p>
 *
 * As examples, the views for the Ontario and York University maps above are hard-coded in finals: </p>
 *
 * <pre>
 *      final static CameraPosition ONTARIO = new CameraPosition.Builder().target(new LatLng(50.007475, -85.954709))
 *           .zoom(4f).tilt(0f).bearing(0f).build();
 *
 *      final static CameraPosition YORK_UNIVERSITY = new CameraPosition.Builder().target(new LatLng(43.7731,
 * -79.5036))
 *           .zoom(15.5f).tilt(50f).bearing(300f).build();
 * </pre>
 *
 * So, the Ontario map has zoom = 4 and the York U map has zoom = 15.5. Note that the camera position for the York U map
 * has a tilt of 50 degrees from the nadir and a bearing = 300 degrees from North. These properties are evident in the
 * map. </p>
 *
 * The actual change in the map view is coded in the button callback with a call to <code>changeCamera</code>: </p>
 *
 * <pre>
 *      changeCamera(CameraUpdateFactory.newCameraPosition(buttons[idx]), true);
 *      ...
 *      private void changeCamera(CameraUpdate update, boolean animate)
 *      {
 *           if (animate)
 *                myMap.animateCamera(update);
 *           else
 *                myMap.moveCamera(update);
 *      }
 * </pre>
 *
 * Using <code>animateCamera</code> creates a smooth animated transition to the new map view. </p>
 *
 * <!---------------------------------------------------------------------------------------------> <b>Camera Control
 * Mode</b> </p>
 *
 * To further illustrate the zoom, tilt, and bearing parameters for a camera position, the demo includes a <i>camera
 * control mode</i>. This is implemented along the bottom of the UI using (i) a button to display and change the mode,
 * (ii) a seekbar to the adjust the value of a parameter, and (iii) a text field to display the current value (below
 * left). Tapping the button pops up a dialog to change the camera control mode (below right). </p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-3.jpg"><img src="./javadoc_images/DemoMapApp-3.jpg" width="500"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoMapApp-4.jpg"><img src="./javadoc_images/DemoMapApp-4.jpg" width="250"></a></center></p>
 *
 * Changes in the camera zoom, tilt, or bearing occur through adjustments in the seekbar slider. The work is done in the
 * seekbar's listener method, <code>onProgressChanged</code>: <p>
 *
 * <pre>
 *      &#64;Override
 *      public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
 *      {
 *           CameraPosition cp = myMap.getCameraPosition();
 *           float zoom = cp.zoom;
 *           float tilt = cp.tilt;
 *           float bearing = cp.bearing;
 *
 *           switch (cameraControlMode)
 *           {
 *                case ZOOM:
 *                     zoom = (float)progress / SCALE_FACTOR;
 *                     tilt = Math.min(tiltSave, getMaxTilt(zoom));
 *                     break;
 *
 *                case TILT:
 *                     tilt = (float)progress / SCALE_FACTOR;
 *                     tiltSave = tilt;
 *                     break;
 *
 *                case BEARING:
 *                     bearing = progress;
 *           }
 *           changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(cp.target, zoom, tilt, bearing)),
 *           false);
 *           updateSeekValue(progress, cameraControlMode);
 *      }
 * </pre>
 *
 * First, the current camera zoom, tilt, and bearing are retrieved. Depending on the current camera control mode, one of
 * these values is updated based on the position (aka progress) of the slider. (Since the maximum tilt depends on zoom,
 * tilt is also adjusted in zoom mode, if necessary.) Then, the new camera position is passed to
 * <code>changeCamera</code> as described earlier. Note that the boolean argument to <code>changeCamera</code> is
 * <code>false</code>. Thus, the camera change is direct, rather than animated. This creates a more responsive
 * experience as the user drags the seekbar slider. </p>
 *
 * <!---------------------------------------------------------------------------------------------> <b>Navigation
 * Drawer</b> </p>
 *
 * Besides four buttons at the top of the UI, the demo includes several additional pre-defined camera views, or
 * bookmarks. These are accessed using a Navigation Drawer &ndash; a sliding panel at the left edge of the UI. The panel
 * is revealed when the user swipes a finger from the left edge of the screen:</p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-5.jpg"><img src="./javadoc_images/DemoMapApp-5.jpg" width="600"></a> </center></p>
 *
 * Using a bookmark to change the camera view is much the same as using a button, as described earlier. Of course, the
 * <code>CameraPosition</code> objects for bookmarks are stored in a different array. As well, the code is responding to
 * a different callback. The navigation drawer's listener method is <code>onItemClick</code>: </p>
 *
 * <pre>
 *      &#64;Override
 *      public void onItemClick(AdapterView<?> parent, View view, int position, long id)
 *      {
 *           changeCamera(CameraUpdateFactory.newCameraPosition(bookmarks[position]), true);
 *           myMap.addMarker(new MarkerOptions().position(bookmarks[position].target).title(bookmarkNames[position]));
 *           tiltSave = bookmarks[position].tilt;
 *           myDrawerLayout.closeDrawer(myDrawerList);
 *      }
 * </pre>
 *
 * Using a navigation drawer in an Android UI is straight forward. The primary setup is in the XML layout file which
 * uses as <code>DrawerLayout</code> view group at the top level. The organization in the demo closely follows the
 * example in the Training document "Creating a Navigation Drawer" (link above). Consult for details.</p>
 *
 *
 * As an example of using the navigation drawer, note that the last item in the navigation drawer menu is Sochi &ndash;
 * the location of the 2014 Winter Olympics. Tapping Sochi advances the camera position to this Russian resort town on
 * the coast of the Black Sea (below left). Navigations here might include zooming in to the Sochi Olympic Park (below
 * center) and then switching the camera control mode to Tilt and adjusting the camera tilt (below right). </p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-6.jpg"><img src="./javadoc_images/DemoMapApp-6.jpg" width="250"></a> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoMapApp-7.jpg"><img src="./javadoc_images/DemoMapApp-7.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoMapApp87.jpg"><img src="./javadoc_images/DemoMapApp-8.jpg" width="250"></a></center></p>
 *
 * <!---------------------------------------------------------------------------------------------> <b>Satellites,
 * Settings, and Options </b></p>
 *
 * Did you notice anything unusual in the map views above? Sure! These are satellite views. Switching to a satellite
 * view is possible through the Settings entry in the Options Menu: </p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-9.jpg"><img src="./javadoc_images/DemoMapApp-9.jpg" width="600"></a> </center></p>
 *
 * The code to do the switch is a one-liner: </p>
 *
 * <pre>
 *      myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
 * </pre>
 *
 * Of course, the code executes in response to the Settings selection (from <code>onOptionsItemSelected</code>). The
 * implementation of this feature is a bit involved since map type (satellite vs. normal) is implemented as a
 * <i>setting</i> &ndash; a value set by the user that persists from one invocation of the app to another. So, selecting
 * Settings, launches a settings activity, etc. See Demo_Settings for further discussion. </p>
 *
 * Note that the Options Menu also includes entries for Bookmarks, Restore, and Help. Bookmarks provides an alternative
 * method to open the navigation drawer. Restore clears the map and brings up the original map of Ontario. Help is
 * unimplemented but selecting Help is acknowledged using popup Toast (below left). </p>
 *
 * <center> <a href="./javadoc_images/DemoMapApp-11.jpg"><img src="./javadoc_images/DemoMapApp-11.jpg" width="400"></a> &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;
 * <a href="./javadoc_images/DemoMapApp-12.jpg"><img src="./javadoc_images/DemoMapApp-12.jpg" width="400"></a> </center></p>
 *
 * Speaking of unimplemented, the app also senses a long-press finger gesture on the map surface. This is detected by
 * implementing <code>GoogleMap.OnMapLongClickListener</code>. If a long press is detected, a 50-ms vibrotactile pulse
 * is emitted along with popup Toast acknowledging the long-press (above right). Perhaps you can think of a useful UI
 * response for a long-press. </p>
 *
 * <!---------------------------------------------------------------------------------------------> <b>Custom
 * Markers</b></p>
 *
 * It is relatively easy to add custom markers to a map. An example is the marker for the Lassonde Building mentioned
 * earlier. The marker was added to the map during the initial setup: </p>
 *
 * <pre>
 *      lassonde = myMap.addMarker(new MarkerOptions().alpha(0f).position(LASSONDE_BUILDING).title("Lassonde Building")
 *           .snippet("Dept. of EECS").icon(BitmapDescriptorFactory.fromResource(R.drawable.lassonde_marker_small)));
 * </pre>
 *
 * Of course, a marker image must be created and saved as a resource. In this case, the marker image is stored in
 * <code>lassonde_marker_small.png</code>. Different resolutions are recommended to accommodate different display
 * densities. For example, an 18 &times; 18 version of the marker image is stored in <code>res/drawable-hdpi/</code> and
 * a 36 &times; 36 version is stored in <code>res/drawable-mdpi/</code>. The drawable directories with designations
 * <code><u>h</u>dpi</code> and <code><u>m</u>dpi</code> are for "high" and "medium" density displays, respectively. See
 * <a href="http://developer.android.com/guide/practices/screens_support.html">Supporting Multiple Screens</a> in the
 * API Guides for additional discussion. </p>
 *
 * Note in the code above that alpha is set to 0. Thus, the marker is invisible. The marker is only made visible if the
 * zoom level is 15 or greater. This is done in the <code>onCameraChange</code> method. Animation is included to create
 * a fade-in effect: </p>
 *
 * <pre>
 *      if (position.zoom >= 15)
 *           ObjectAnimator.ofFloat(lassonde, "alpha", 1f).setDuration(1000).start();
 *      else
 *           lassonde.setAlpha(0f);
 * </pre>
 *
 * There is likely a better way to manage markers if the app includes many custom markers. This is not explored
 * here.</p>
 *
 * @author (c) Scott MacKenzie 2014-2022
 */

public class DemoMapAppActivity extends FragmentActivity
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // approximate center of Ontario (Lake Wabigano)
    final static double ONTARIO_LAT = 50.007475;
    final static double ONTARIO_LNG = -85.954709;

    final static int ZOOM = 100;
    final static int TILT = 200;
    final static int BEARING = 300;

    final static int MAX_ZOOM = 21; // see CameraPosition.Builder API
    final static int MAX_BEARING = 360;
    final static int SCALE_FACTOR = 100; // for seekbar in zoom and tilt modes

    // define some map locations for the demo buttons
    final static CameraPosition ONTARIO = new CameraPosition.Builder().target(new LatLng(ONTARIO_LAT, ONTARIO_LNG))
            .zoom(4f).tilt(0f).bearing(0f).build();
    final static CameraPosition YORK_UNIVERSITY = new CameraPosition.Builder().target(new LatLng(43.7731, -79.5036))
            .zoom(15.5f).tilt(50f).bearing(300f).build();
    final static CameraPosition OTTAWA = new CameraPosition.Builder().target(new LatLng(45.424254, -75.698980)).zoom(
            15.5f).tilt(67.5f).bearing(0f).build();
    final static CameraPosition MUSKOKA = new CameraPosition.Builder().target(new LatLng(45.114443, -79.655752)).zoom(
            14f).tilt(25f).bearing(0f).build();

    // ... then initialize a button array
    final static CameraPosition[] buttons = {ONTARIO, YORK_UNIVERSITY, OTTAWA, MUSKOKA};
    final static LatLng LASSONDE_BUILDING = new LatLng(43.774096, -79.505351);

    // options menu items (used for groupId and itemId)
    private final static int BOOKMARKS = 0;
    private final static int RESTORE = 1;
    private final static int SETTINGS = 2;
    private final static int HELP = 3;

    // preset/bookmark buttons (initialized from resources in onCreate)
    CameraPosition[] bookmarks;
    String[] bookmarkNames; // for markers and navigation drawer menu
    Button ontario, york, ottawa, muskoka; // buttons to go directly to a location
    Button cameraControlModeButton; // button to change the camera control mode
    SeekBar seekBar; // to control either the camera zoom, tilt, or bearing
    TextView seekValue; // current value (aka progress) in the seekbar
    AlertDialog seekModeDialog; // dialog to change the camera control mode
    SharedPreferences sp; // currently, only for satellite vs. normal map type
    Vibrator vib; // for long-press
    int cameraControlMode;
    String[] cameraControlModeOptions;
    boolean satellite;
    Marker lassonde;

    /*
     * tiltSave is used when zooming. This is needed because the maximum allowable camera tilt angle
     * is higher when zoomed in than when zoomed out (see API for CameraPosition.Builder). So, when
     * zooming, we adjust both the zoom level and, if necessary, the camera tilt. The adjusted value
     * for tilt is min(tiltSave, maxTilt), where tiltSave is the tilt value when zooming began and
     * maxTilt is the maximum allowable tilt at the current zoom-level. This way, we can zoom out
     * and then zoom back in and retain the original camera tilt angle.
     */
    float tiltSave;

    // declare this listener because it gets added and removed as the app executes
    MyOnSeekBarChangeListener mySeekBarListener;

    // may be null if the Google Play services APK is not available
    private GoogleMap myMap;

    // for the Navigation Drawer
    private DrawerLayout myDrawerLayout;
    private ListView myDrawerList;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        //Log.i(MYDEBUG, "onCreate!");

        ontario = (Button)findViewById(R.id.ontario);
        york = (Button)findViewById(R.id.york_university);
        ottawa = (Button)findViewById(R.id.ottawa_parliament_bldgs);
        muskoka = (Button)findViewById(R.id.muskoka);
        cameraControlModeButton = (Button)findViewById(R.id.seek);
        seekBar = (SeekBar)findViewById(R.id.seek_bar);
        seekValue = (TextView)findViewById(R.id.seek_value);

        // initialize SharedPreferences instance
        sp = PreferenceManager.getDefaultSharedPreferences(this);
        loadSettings();

        cameraControlModeOptions = this.getResources().getStringArray(R.array.camera_control_mode_options);
        cameraControlModeButton.setText(cameraControlModeOptions[0]); // "Zoom" initially
        cameraControlMode = ZOOM; // default

        // init vibrator (used for long-press gesture)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        // load the preset locations (bookmarks)
        bookmarkNames = this.getResources().getStringArray(R.array.locations); // for markers
        String[] latLng = this.getResources().getStringArray(R.array.latlng);
        String[] zoom = this.getResources().getStringArray(R.array.zoom);
        String[] tilt = this.getResources().getStringArray(R.array.tilt);
        String[] bearing = this.getResources().getStringArray(R.array.bearing);
        bookmarks = new CameraPosition[latLng.length];
        for (int i = 0; i < bookmarks.length; ++i)
        {
            String[] temp = latLng[i].split(",");
            bookmarks[i] = new CameraPosition.Builder().target(
                    new LatLng(Double.parseDouble(temp[0]), Double.parseDouble(temp[1]))).zoom(
                    Float.parseFloat(zoom[i])).tilt(Float.parseFloat(tilt[i])).bearing(Float.parseFloat(bearing[i]))
                    .build();
        }

		/*
         * Setup the Navigation Drawer to hold the preset/bookmark locations. See...
		 * 
		 * http://developer.android.com/training/implementing-navigation/nav-drawer.html
		 */
        myDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        myDrawerList = (ListView)findViewById(R.id.left_drawer);
        myDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, bookmarkNames));
        myDrawerList.setOnItemClickListener(new MyDrawerItemClickListener());

        mySeekBarListener = new MyOnSeekBarChangeListener();

        // build a dialog to set the camera control mode for the seekbar
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.camera_control_mode_dialog_title);
        builder.setItems(cameraControlModeOptions, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                // update the label in the button to reflect the new camera control mode
                cameraControlModeButton.setText(cameraControlModeOptions[which]);

                // set the camera control mode for the seekbar
                if (cameraControlModeOptions[which].equals("Zoom"))
                    cameraControlMode = ZOOM;
                else if (cameraControlModeOptions[which].equals("Tilt"))
                    cameraControlMode = TILT;
                else
                    cameraControlMode = BEARING;
                updateSeekBar(myMap.getCameraPosition());
            }

        }).setOnCancelListener(new DialogInterface.OnCancelListener()
        {
            @Override
            public void onCancel(DialogInterface dialog)
            {
                // (re)attach the listener
                seekBar.setOnSeekBarChangeListener(mySeekBarListener);
            }
        });
        seekModeDialog = builder.create();

        // now, go do the work to set up the Google Maps map
        setUpMapIfNeeded();
    }

    /*
     * Only one setting, so this is overkill, but we might add new settings later.
     */
    private void loadSettings()
    {
        // build keys (makes the code more readable)
        final String SATELLITE_KEY = getBaseContext().getString(R.string.pref_satellite);

        // retrieve values associated with keys
        satellite = sp.getBoolean(SATELLITE_KEY, false);
        //Log.i(MYDEBUG, "satellite=" + satellite);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        //Log.i(MYDEBUG, "onResume!");
        //setUpMapIfNeeded();
    }

    /*
     * Confirm the availability of a GoogleMap. See...
     *
     * https://developers.google.com/maps/documentation/android/map#verify_map_availability
     */
    private void setUpMapIfNeeded()
    {
        // do a null check to confirm that we have not already instantiated the map
        if (myMap == null)
        {
            //Log.i(MYDEBUG, "Try to obtain map...");

            // try to obtain the map from the SupportMapFragment
            myMap = ((SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map)).getMap();

            //if (myMap == null)
            //    Log.i(MYDEBUG, "No map!");
            //else
            //    Log.i(MYDEBUG, "Success!");

            // if we were successful, initialize map properties, etc.
            if (myMap != null)
            {
                int mapType = satellite ? GoogleMap.MAP_TYPE_SATELLITE : GoogleMap.MAP_TYPE_NORMAL;
                myMap.setMapType(mapType);
                myMap.setMyLocationEnabled(true);
                myMap.setOnCameraChangeListener(new MyOnCameraChangeListener());
                myMap.setOnMapLongClickListener(new MyOnMapLongClickListener(this));

                addLassondeMarker(); // add an invisible marker for the Lassonde Building at York U

                //Log.i(MYDEBUG, "Go to Ontario...");
                goToOntario(); // the initial map when the app launches
            }
        }
    }

    // add a marker for the Lassonde Building at York U (initially invisible)
    private void addLassondeMarker()
    {
        /*
         * Add a marker for the Lassonde Building at York U, giving it a title and snippet (which
		 * appear when the user taps the marker). Alpha is set to 0, making the marker invisible.
		 * Alpha is adjusted to 1 (with animation) in onCameraChange if the zoom level is > 15.
		 */
        lassonde = myMap.addMarker(new MarkerOptions().alpha(0f).position(LASSONDE_BUILDING).title("Lassonde Building")
                .snippet("Dept. of EECS").icon(BitmapDescriptorFactory.fromResource(R.drawable.lassonde_marker_small)));
    }

    // present a map of Ontario with a marker in the middle
    private void goToOntario()
    {
        myMap.addMarker(new MarkerOptions().position(new LatLng(ONTARIO_LAT, ONTARIO_LNG)).title("Ontario"));
        changeCamera(CameraUpdateFactory.newCameraPosition(ONTARIO), true);
        tiltSave = ONTARIO.tilt;
    }

    // setup an Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, BOOKMARKS, BOOKMARKS, R.string.menu_bookmarks);
        menu.add(0, RESTORE, RESTORE, R.string.menu_restore);
        menu.add(0, SETTINGS, SETTINGS, R.string.menu_settings);
        menu.add(0, HELP, HELP, R.string.menu_help);
        return true;
    }

    // handle an Options menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case BOOKMARKS:
                myDrawerLayout.openDrawer(myDrawerList);
                return true;

            case RESTORE:
                myMap.clear(); // clear all markers except...
                addLassondeMarker(); // ... retain the Lassonde Maker
                Toast.makeText(this, "Map Restored!", Toast.LENGTH_SHORT).show();
                goToOntario();
                return true;

            case SETTINGS:
                // launch the SettingsActivity to allow the user to change the app's settings
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(i, SETTINGS); // see comment for onActivityResult
                return true;

            case HELP:
                Toast.makeText(this, "Help! (no implementation)", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    /*
     * We used the "for result" version when starting the settings activity. So, we handle changes
     * to the settings here.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // SETTINGS is the only possibility, but we'll check anyway
        if (requestCode == SETTINGS)
        {
            loadSettings();

            // do this here (perhaps re-organize later)
            if (satellite)
                myMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
            else
                myMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
    }

    // handle clicks on the demo's buttons
    public void onButtonClick(View v)
    {
        // only act if there is a map to act on
        if (!checkReady())
            return;

        // remove the seekbar listener (no need to fire a seekbar event on a button click)
        seekBar.setOnSeekBarChangeListener(null);

        if (v == cameraControlModeButton)
        {
            // change the camera control mode for the seekbar
            seekModeDialog.show();
        } else
        {
            // change the camera position according to the button that was clicked
            int idx = v.getId();
            //Log.i(MYDEBUG, "idx=" + idx);

            if (idx == york.getId())
                changeCamera(CameraUpdateFactory.newCameraPosition(YORK_UNIVERSITY), true);
            else if (idx == ontario.getId())
                changeCamera(CameraUpdateFactory.newCameraPosition(ONTARIO), true);
            else if (idx == muskoka.getId())
                changeCamera(CameraUpdateFactory.newCameraPosition(MUSKOKA), true);
            else if (idx == ottawa.getId())
                changeCamera(CameraUpdateFactory.newCameraPosition(OTTAWA), true);

            tiltSave = buttons[2].tilt;
        }
    }

    /*
     * Update the seekbar, setting its maximum value and its current value (aka progress) according
     * the current camera control mode.
     *
     * If the camera control mode is zoom or tilt, we also apply scale factors for zoom and tilt to
     * improve the granularity of the control. The scale factor is applied here in setting the
     * maximum and current value, and then removed later when the value is retrieved and applied to
     * update the camera position (see onProgressChanged).
     *
     * We also call updateSeekValue to update the seekValue TextView to display the current value.
     *
     * An additional detail is to attach a listener (since it was removed in the button click
     * callback). This is done to prevent the seekbar from firing change events when the camera
     * control mode is set via the popup alert dialog.
     */
    void updateSeekBar(CameraPosition cp)
    {
        int maxValue = -1;
        int currentValue = -1;

        if (cameraControlMode == ZOOM)
        {
            maxValue = MAX_ZOOM * SCALE_FACTOR;
            currentValue = (int)(cp.zoom * SCALE_FACTOR);

        } else if (cameraControlMode == TILT)
        {
            maxValue = (int)(getMaxTilt(cp.zoom) * SCALE_FACTOR);
            currentValue = (int)(cp.tilt * SCALE_FACTOR);

        } else if (cameraControlMode == BEARING)
        {
            maxValue = MAX_BEARING;
            currentValue = (int)cp.bearing;
        }

        seekBar.setMax(maxValue);
        seekBar.setProgress(currentValue);

        // (re)attach the listener
        seekBar.setOnSeekBarChangeListener(mySeekBarListener);

        // update the value displayed beside the seekbar
        updateSeekValue(currentValue, cameraControlMode);
    }

    /*
     * Compute the maximum tilt for the camera.
     *
     * The maximum allowable tilt depends on the zoom level of the map. The algorithm here is
     * described in the API for CameraPosition.Builder. See...
     *
     * http://developer.android.com/reference/com/google/android/gms/maps/model/CameraPosition.Builder
     * .html#tilt%28float%29
     */
    private float getMaxTilt(float zoom)
    {
        float maxTilt;
        if (zoom < 10f)
            maxTilt = 30f;
        else if (zoom <= 14f)
            maxTilt = 30f + (15f * (zoom - 10f) / 4f); // NOTE: 45 - 30 = 15
        else if (zoom <= 15.5f)
            maxTilt = 45f + (22.5f * (zoom - 14f) / 1.5f); // NOTE: 67.5 - 55 = 22.5
        else
            maxTilt = 67.5f;
        return maxTilt;
    }

    /*
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady()
    {
        if (myMap == null)
        {
            Toast.makeText(this, "Map not ready!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /*
     * Change the camera position, based on a new target, zoom, tilt, or bearing. A boolean is also
     * used to set whether the change is direct or animated.
     */
    private void changeCamera(CameraUpdate update, boolean animate)
    {
        if (animate)
            myMap.animateCamera(update);
        else
            myMap.moveCamera(update);
    }

    // Update the value in the text field beside the seekbar.
    private void updateSeekValue(int progress, int seekMode)
    {
        if (seekMode == ZOOM)
            seekValue.setText(String.format(Locale.CANADA, "%.1f", (float)progress / SCALE_FACTOR));
        else if (seekMode == TILT)
            seekValue.setText(String.format(Locale.CANADA, "%.1f", (float)progress / SCALE_FACTOR));
        else if (seekMode == BEARING)
            seekValue.setText(String.format(Locale.CANADA, "%d", progress));
    }

    // ============================
    // Inner classes defined at end
    // ============================

    // ==================================================================================================
    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener
    {
        /*
         * Process an adjustment to the seekbar. The adjustment will change either the zoom, tilt,
         * or bearing of the camera, depending on the camera control mode.
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
        {
            // get current camera zoom, tilt, and bearing
            CameraPosition cp = myMap.getCameraPosition();
            float zoom = cp.zoom;
            float tilt = cp.tilt;
            float bearing = cp.bearing;

			/*
             * Apply the new seekbar value (aka progress) to either the zoom, tilt, or bearing
			 * variable, depending on the camera control mode.
			 */
            switch (cameraControlMode)
            {
                case ZOOM:
                    zoom = (float)progress / SCALE_FACTOR;
                    tilt = Math.min(tiltSave, getMaxTilt(zoom));
                    break;

                case TILT:
                    tilt = (float)progress / SCALE_FACTOR;
                    tiltSave = tilt;
                    break;

                case BEARING:
                    bearing = progress;
                    break;
            }

			/*
             * Use the new data to create a new CameraPosition instance which is passed on to
			 * changeCamera to adjust the position of the camera.
			 * 
			 * To make the UI responsive to real-time adjustments in the seekbar, we call
			 * changeCamera with animate = false. This will invoke "moveCamera" rather than
			 * "animateCamera".
			 */
            changeCamera(CameraUpdateFactory.newCameraPosition(new CameraPosition(cp.target, zoom, tilt, bearing)),
                    false);

            // also update the text field beside the seekbar
            updateSeekValue(progress, cameraControlMode);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar)
        {
            // not needed (but must implement since part of SeekBar.OnSeekBarChangeListener)
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar)
        {
            // not needed (but must implement since part of SeekBar.OnSeekBarChangeListener)
        }
    }

    // ==================================================================================================
    private class MyDrawerItemClickListener implements ListView.OnItemClickListener
    {
        // process a selection in the navigation drawer (and close the drawer)
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            changeCamera(CameraUpdateFactory.newCameraPosition(bookmarks[position]), true);
            myMap.addMarker(new MarkerOptions().position(bookmarks[position].target).title(bookmarkNames[position]));
            tiltSave = bookmarks[position].tilt;
            myDrawerLayout.closeDrawer(myDrawerList);
        }
    }

    // ==================================================================================================
    private class MyOnCameraChangeListener implements GoogleMap.OnCameraChangeListener
    {
        /*
         * Process a change in the camper position.  For the moment, we're not doing much here.  Just check
         * the zoom level and adjust the visible of the Lassonde marker accordingly.  There is, no doubt, a
         * better way to manage the visibility of custom markers (ToDo!).
         */
        @Override
        public void onCameraChange(CameraPosition position)
        {
            updateSeekBar(position);
            if (position.zoom >= 15)
            {
                ObjectAnimator.ofFloat(lassonde, "alpha", 1f).setDuration(1000).start();
            } else
                lassonde.setAlpha(0f);
        }
    }

    // ==================================================================================================
    private class MyOnMapLongClickListener implements GoogleMap.OnMapLongClickListener
    {
        Context context;

        MyOnMapLongClickListener(Context contextArg)
        {
            context = contextArg;
        }

        // process a long-press on the map surface (for future consideration)
        @Override
        public void onMapLongClick(LatLng point)
        {
            Toast.makeText(context, "Long press! (no implementation)", Toast.LENGTH_SHORT).show();
            vib.vibrate(50);
        }
    }
}