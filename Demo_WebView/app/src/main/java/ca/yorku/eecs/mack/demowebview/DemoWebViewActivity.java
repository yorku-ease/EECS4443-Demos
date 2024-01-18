package ca.yorku.eecs.mack.demowebview;

import android.annotation.SuppressLint;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

/**
 * Demo_WebView - demonstrate loading and displaying web content in a mobile UI. <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides: <p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/webapps/webview.html">Building Web Apps in WebView</a> <li><a
 * href= "http://developer.android.com/guide/topics/resources/runtime-changes.html#HandlingTheChange" >Handling Runtime
 * Changes</a> (and subsection "Handling the Configuration Change Yourself") <li><a
 * href="http://developer.android.com/guide/topics/manifest/activity-element.html">&lt;activity &gt;</a> </ul> <p>
 *
 * API References: <p>
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/webkit/WebView.html"> <code>WebView</code></a>
 * <li><a href="http://developer.android.com/reference/android/webkit/WebViewClient.html">
 * <code>WebViewClient</code></a> <li><a href="http://developer.android.com/reference/android/webkit/WebSettings.html">
 * <code>WebSettings</code></a> <li><a href="http://developer.android.com/reference/android/webkit/WebChromeClient
 * .html"> <code>WebChromeClient</code></a> </ul> <p>
 *
 * Training: <p>
 *
 * <ul> <li><a href="http://developer.android.com/training/implementing-navigation/temporal.html">Providing Proper Back
 * Navigation</a> </ul> <p>
 *
 * <i>devahead</i> blog: <p>
 *
 * <ul> <li><a href= "http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-orientation-change/"
 * >Preserving the state of an Android WebView on screen orientation change</a> </ul> </blockquote>
 *
 * The cornerstone for loading and displaying web content in an Android UI is the <code>WebView</code> class. The API
 * Reference begins with this brief summary for <code>WebView</code>: <p>
 *
 * <blockquote><i>A view that displays web pages. This class is the basis upon which you can roll your own web browser
 * or simply display some online content within your Activity. It uses the WebKit rendering engine to display web pages
 * and includes methods to navigate forward and backward through a history, zoom in and out, perform text searches and
 * more.</i> </blockquote>
 *
 * This demo uses <code>WebView</code> to implement a bare-bones web browser. We are using some of the built-in features
 * of <code>WebView</code> and adding a few of our own. Obviously, additional work is needed to turn this demo into a
 * full-fledged mobile web browser. Before delving into the technical details, here are examples of the UI in landscape
 * and portrait orientations: (Note that the layouts are slightly different.)<p>
 *
 * <center><a href="./javadoc_images/DemoWebView-1.jpg"><img src="./javadoc_images/DemoWebView-1.jpg" width="250" alt="image"></a>&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;
 * <a href="./javadoc_images/DemoWebView-3.jpg"><img src="./javadoc_images/DemoWebView-3.jpg" width="400" alt="image"></a> </center><p>
 *
 * <p> Getting started with <code>WebView</code> is simple. In the layout file (e.g., <code>main.xml</code>), a
 * <code>FrameLayout</code> is positioned at a location in the UI where the <code>WebView</code> is to appear: <p>
 *
 * <pre>
 *      &lt;LinearLayout&gt;
 *           ...
 *           &lt;FrameLayout
 *                android:id="@+id/web_view_placeholder"
 *                android:layout_width="match_parent"
 *                android:layout_height="match_parent" /&gt;
 *           ...
 *      &lt;/LinearLayout&gt;
 * </pre>
 *
 * <p>
 *
 * The <code>FrameLayout</code> is a <i>place-holder</i> for the <code>WebView</code>. In the main activity, a
 * <code>FrameLayout</code> object is declared with a reference obtained in <code>onCreate</code>: <p>
 *
 * <pre>
 *      FrameLayout webViewPlaceHolder;
 *      ...
 *      webViewPlaceHolder = (FrameLayout)findViewById(R.id.web_view_placeholder);
 * </pre>
 *
 * In the main activity, we also declare our <code>WebView</code> object. It is instantiated in
 * <code>onCreate</code>:<p>
 *
 * <pre>
 *      WebView webView;
 *      ...
 *      webView = new WebView(this);
 * </pre>
 *
 * <p>
 *
 * The <code>WebView</code> is then attached to the <code>FrameLayout</code> (also in <code>onCreate</code>):<p>
 *
 * <pre>
 *      webViewPlaceholder.addView(webView);
 * </pre>
 *
 * <p>
 *
 * Providing content to the <code>WebView</code> is simple:<p>
 *
 * <pre>
 *      webView.loadURL("http://www.yorku.ca/");
 * </pre>
 *
 * <p>
 *
 * Since we are accessing the Internet, permission is requested in <code>AndroidManifest.xml</code> :<p>
 *
 * <pre>
 *      &lt;uses-permission android:name="android.permission.INTERNET" /&gt;
 * </pre>
 *
 * <p>
 *
 * And that's about it! Of course, there is much more. One difficult issue with <code>WebView</code> is handling a
 * configuration change when the screen is rotated. This is discussed below (see "Configuration Changes").<p>
 *
 * The basic <code>WebView</code> object only displays the content of a web page. It does not provide the means to
 * manage or control content. For this, three additional classes are used in conjunction with <code>WebView</code>:<p>
 *
 * <blockquote> <table summary="table" border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc"> <th
 * align="center">Class <th align="center">Summary <th align="center">Example code
 * <p>
 * <tr> <td><code>WebSettings</code> <td>Manages settings for a WebView. When the WebView is first created, a
 * WebSettings object is initialized with default settings. The settings are retrieved using get-methods and changed
 * using set-methods. <td><code> WebSettings&nbsp;ws&nbsp;=&nbsp;webView.getSettings();<br> ...<br>
 * ws.setJavaScriptEnabled(true);<br> ws.setBuiltInZoomControls(true); </code>
 * <p>
 * <tr> <td><code>WebViewClient</code> <td>A class that is called when things happen that impact the rendering of the
 * content, such as errors or form submissions. Provides the ability to intercept URL loading. <td><code>
 * webView.setWebViewClient(new MyWebViewClient());<br> ...<br> public&nbsp;void&nbsp;onPageFinished(WebView&nbsp;
 * view,&nbsp;String&nbsp;url)<br> {<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// do something<br> }<br> </code>
 * <p>
 * <tr> <td><code>WebChromeClient</code> <td>A class that is called when something happens that might impact a browser
 * UI. Progress updates and JavaScript alerts are sent to WebChromeClient. <td><code> webView.setWebChromeClient(new
 * MyWebChromeClient());<br> ...<br> public&nbsp;void&nbsp;onReceivedIcon(WebView&nbsp;view,&nbsp;Bitmap&nbsp;
 * favicon)<br> {<br> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;// do something<br> } </code> </table> </blockquote>
 *
 * The classes <code>MyWebViewClient</code> and <code>MyWebChromeClient</code> are custom-designed inner classes that
 * extend the corresponding class. Methods are overriden and implemented to achieve the desired effect for this demo.
 * Consult the source code for further details.<p>
 *
 * <b>Back/Forward Navigation</b><p>
 *
 * WebView has built-in support for back/forward history. However, there is no means to use the history. The demo UI
 * includes back (&larr;) and forward (&rarr;) buttons for that purpose. Conveniently, WebView provides four methods to
 * facilitate back/forward navigation: <code>canGoBack</code>, <code>canGoForward</code>, <code>goBack</code>, and
 * <code>goForward</code>. The <code>canGoBack</code> and <code>canGoForward</code> methods return Booleans that are
 * easily used with <code>goBack</code> and <code>goForward</code> for back and forward navigation. The code is in the
 * button callback (<code>onClick</code>). We also include code to disable the back or forward button if there is no
 * available history: <p>
 *
 * <center> <a href="./javadoc_images/DemoWebView-2.jpg"><img src="./javadoc_images/DemoWebView-2.jpg" width="600" alt="image"></a> </center><p>
 *
 *
 * In fact, the demo's back button (&larr;) isn't really needed because <code>WebView</code> supports back navigation
 * using the Back button in the Navigation Bar. Within our <code>WebView</code>, we implement the
 * <code>onBackPressed</code> callback:<p>
 *
 * <pre>
 *      &#64;Override
 *      public void onBackPressed()
 *      {
 *           if (webView.canGoBack())
 *           {
 *                webView.goBack();
 *                return;
 *
 *           } else if (toastBeforeExit)
 *           {
 *                toastBeforeExit = false;
 *                Toast.makeText(this, "Press Back once more to exit", Toast.LENGTH_LONG).show();
 *                return;
 *           }
 *           super.onBackPressed();
 *      }
 * </pre>
 *
 * If the back history is empty, we defer to the <code>super</code> method (to exit the activity). <p>
 *
 * A minor UI feature is to not immediately defer to the <code>super</code> method if there is no back history. A
 * Boolean variable (<code>toastBeforeExit</code>) is used along with popup Toast to give the user one warning before
 * exiting (below left). Press Back again and the app exits. This is similar to the warning given in Astro before
 * exiting (below right). <p>
 *
 * <center><a href="./javadoc_images/DemoWebView-4.jpg"><img src="./javadoc_images/DemoWebView-4.jpg" width="250" alt="image"></a>&nbsp;&nbsp;&nbsp;
 * &nbsp;&nbsp;
 * <a href="./javadoc_images/DemoWebView-5.jpg"><img src="./javadoc_images/DemoWebView-5.jpg" width="250" alt="image"></a></center> <p>
 *
 * <b>URL Display and Edit</b> <p>
 *
 * The demo includes an <code>EditText</code> field that holds and displays the current URL (below, 1). The field is
 * editable, allowing the URL to be edited or a fresh URL to be entered. For example, while viewing the CBC online news,
 * Toronto news is retrieved by editing the URL. The user taps in the field at the end of the URL (below, 2). A soft
 * keyoard pops up (not shown). The user presses backspace a few times (below, 3), enters <code>toronto</code> (below,
 * 4), and taps the Go button (or taps Next on the soft keyboard).<p>
 *
 * <center><a href="./javadoc_images/DemoWebView-6.jpg"><img src="./javadoc_images/DemoWebView-6.jpg" width="350" alt="image"></a></center> <p>
 *
 * Of course, editing is an inherent capability of <code>EditText</code> objects. See Demo_EditText for further
 * discussion. Processing the URL when Go is tapped is handled in the <code>onClick</code> callback: <p>
 *
 * <pre>
 *      if (view == go)
 *      {
 *           urlString = urlField.getText().toString(); //get URL
 *           urlField.setSelection(urlField.getText().length()); // move caret to end
 *           webView.loadUrl(urlString); // load web page in WebView
 *           getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN); // hide soft keyboard
 *      }
 * </pre>
 *
 * <b>Bookmarks</b><p>
 *
 * The demo includes a hard-coded set of bookmarks accessible through the Options Menu: <p>
 *
 * <center><a href="./javadoc_images/DemoWebView-7.jpg"><img src="./javadoc_images/DemoWebView-7.jpg" width="500" alt="image"></a></center> <p>
 *
 * Of course, a better way to support bookmarks is to allow the user to add and delete personalised bookmarks. We'll
 * leave this for you to explore. <p>
 *
 * <b>Web Page Favicon and Title</b> <p>
 *
 * Notice in the UI images above that the web page's title and shortcut icon (favicon) appear in the application's
 * Action Bar: <p>
 *
 * <center><a href="./javadoc_images/DemoWebView-8.jpg"><img src="./javadoc_images/DemoWebView-8.jpg" width="500" alt="image"></a></center> <p>
 *
 * Placing the title of the web page in the Action Bar is done in the <code>onPageFinished</code> method, which is
 * defined in <code>WebViewClient</code> (see table above). This method executes when the web page is finished loading
 * in the WebView. A few administrative details are handled in <code>onPageFinished</code>, such as placing the URL in
 * the <code>EditText</code> field. Another detail is to retrieve the title of the web page and place it in the Action
 * Bar:<p>
 *
 * <pre>
 *      getActionBar().setTitle(webView.getTitle());
 * </pre>
 *
 * Retrieving the favicon is done in the <code>onReceivedIcon</code> method, which is defined in
 * <code>WebChromeClient</code> (see table above). This method executes to notify the host that a favicon has been
 * received from the web page: <p>
 *
 * <pre>
 *       &#64;Override
 *       public void onReceivedIcon(WebView view, Bitmap favicon)
 *       {
 *            Drawable drawable = new BitmapDrawable(getResources(), favicon);
 *            getActionBar().setIcon(drawable); // added in API level 14
 *       }
 * </pre>
 *
 * <p>
 *
 * <b>Configuration Changes</b> <p>
 *
 * One difficult issue in this demo is handling a screen orientation change. A screen orientation change is a type of
 * <i>configuration change</i>, which is normally handled by shutting down and restarting the activity. See Demo_Layout
 * for further discussion. There is a particular challenge with <code>WebView</code>, since the full state of a
 * <code>WebView</code> instance is not preserved on a configuration change, even when we choose to <i>handle the change
 * ourself</i>, as we are doing here. The solution is to use a <code>FrameLayout</code> as a place-holder for the
 * <code>WebView</code>. Developing this approach was not easy! As it happens, there is considerable chatter on
 * StackOverflow on how to handle a device configuration change with <code>WebView</code> . Most suggestions don't work
 * &ndash; which is to say, they work but only in limited scenarios. The idea for using a <code>FrameLayout</code>
 * place-holder originated in a devahead blog (link above). Fortunately, advice on StackOverflow lead to the devahead
 * blog. The general approach is described next. <p>
 *
 * This demo does <i>not</i> implement <code>onSavedInstanceState</code> and <code>onRestoreInstanceState</code>.
 * Although normally the activity is shut down and restarted on a device orientation change, this is prevented here by
 * adding <p>
 *
 * <pre>
 *      android:configChanges="keyboard|keyboardHidden|orientation|screenSize"
 * </pre>
 *
 * in the <code>activity</code> element of <code>AndroidManifest.xml</code>. This element identifies configuration
 * changes that the activity will handle itself. The OR'd strings identify events that trigger a configuration change.
 * For those included, we are committing to handling the configuration change ourself, rather than have the activity
 * shutdown and restarted. When the event occurs (e.g., a screen orientation change), the activity remains running and
 * <code>onConfigurationChanged</code> is called. <p>
 *
 * This approach requires us to handle the configuration change ourself &ndash; in the
 * <code>onConfigurationChanged</code> callback. We are using a different layout for portrait and landscape orientations
 * (see the images at the top of this API), but the layout is changed in <code>onConfigurationChanged</code>, not in
 * <code>onCreate</code>. (For more information on configuration changes, review the Android API Guides on "Handling
 * Runtime Changes", and, in particular, the subsection titled "Handling the Configuration Change Yourself". The strings
 * used with the <code>Android:configChanges</code> element are defined in the Android API Guide for the
 * <code>&lt;activity&gt;</code> element. See above for links.) <p>
 *
 * The code in the demo is organized to allow the <code>WebView</code> object to remain intact throughout the life of
 * the application. Here's a high-level description of what happens to our <code>WebView</code> instance when the screen
 * orientation changes: <p>
 *
 * <ol> <li>The <code>WebView</code> is removed from the <code>FrameLayout</code> place-holder. <li>The activity's
 * <code>setContentView</code> is called to load a new layout (either portrait or landscape). <li>A reference to the new
 * <code>FrameLayout</code> place-holder is retrieved. <li>The <code>WebView</code> is placed in the new place-holder.
 * </ol> <p>
 *
 * The <code>FrameLayout</code> object is discarded and re-created, because the activity is given a new layout when the
 * orientation changes. However, the <code>WebView</code> object is not destroyed: it is simply moved from the old
 * <code>FrameLayout</code> place-holder to the new <code>FrameLayout</code> place-holder. <p>
 *
 * Note that steps 3 and 4 are required both in <code>onCreate</code> and in <code>onConfigurationChanged</code>. Thus,
 * they are placed in a method (<code>initUI</code>) that is called both from <code>onCreate</code> and from
 * <code>onConfigurationChanged</code>. There are of course other details that are required both for creating the
 * initial UI and for handling a configuration change. Consult the source code for complete details. <p>
 *
 * Note: The statement corresponding to each of the fours steps above is identified in the source code with the comment
 * "API Note <i>n</i>". <p>
 *
 * @author (c) Scott MacKenzie, 2013-2020
 */

@SuppressWarnings("unused")
public class DemoWebViewActivity extends Activity implements View.OnClickListener, OnEditorActionListener
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // options menu items (used for groupId and itemId)
    private final static int BOOKMARKS = 0;
    private final static int SETTINGS = 1;
    private final static int HELP = 2;

    // a kludgey way to setup up bookmarks (but demonstrates the general idea)
    final Bookmark[] BOOKMARK = {new Bookmark("CBC", "https://www.cbc.ca/news"),
            new Bookmark("York University", "https://www.yorku.ca/"),
            new Bookmark("Dept of EECS", "https://lassonde.yorku.ca/eecs/"),
            new Bookmark("BBC", "https://www.bbc.com/news"), new Bookmark("NHL", "https://www.nhl.com/"),
            new Bookmark("Weather", "https://www.theweathernetwork.com/ca/36-hour-weather-forecast/ontario/toronto"),
            new Bookmark("Scott", "http://www.yorku.ca/mack/"),
            new Bookmark("CSE4443", "https://www.eecs.yorku.ca/course_archive/2015-16/W/4443/")};

    /*
     * A FrameLayout is used as a place-holder for the WebView to solve a particular problem
     * relating to screen orientation changes. The standard Android behaviour on a screen
     * orientation change is not satisfactory because the full state of the WebView is not
     * preserved. Of the many gallant attempts to solve this problem (most on StackOverflow), the
     * best solution is seems to be one described in a devahead blog. It is the solution used here.
     * For more details, see...
     *
     * http://www.devahead.com/blog/2012/01/preserving-the-state-of-an-android-webview-on-screen-
     * orientation-change/
     */
    protected FrameLayout webViewPlaceholder;
    protected WebView webView;
    EditText urlField; // a field to enter/display the URL
    Button go, back, forward;
    String urlString;
    boolean toastBeforeExit;
    String[] bookmarkTitles;
    float pixelDensity = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initUI();
    }

    @SuppressLint("SetJavaScriptEnabled")
    protected void initUI()
    {
        // retrieve the FrameLayout place-holder (we'll put the WebView there -- see below)
        webViewPlaceholder = (FrameLayout)findViewById(R.id.web_view_placeholder); // API Note 3

        // get pixel density of display (needed to scale favicon)
        pixelDensity = getPixelDensity();

		/*
         * initUI is called both from onCreate and from onConfigurationChanged. The WebView is
		 * initialized only once, from onCreate. This if-statement effectively bypasses the
		 * initialization during a configuration change. See, as well, comments in the
		 * onConfigurationChanged method.
		 */
        if (webView == null)
        {
            // create the WebView
            webView = new WebView(this);
            webView.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

            // WebSettings is used to control the settings for a WebView
            WebSettings ws = webView.getSettings();

			/*
             * Enable Javascript. NOTE: Generates a security warning. Some web sites, such as CBC.ca
			 * do not load unless Javascript is enabled, so we are enabling Javascript. Concerning
			 * the security issue, see...
			 * 
			 * http://developer.android.com/training/articles/security-tips.html#WebView
			 */
            ws.setJavaScriptEnabled(true);

            ws.setBuiltInZoomControls(true); // enable zoom controls

            // use MyWebViewClient to manage content for our WebView
            webView.setWebViewClient(new MyWebViewClient());

            // use MyWebChromeClient to receive favicon icons
            webView.setWebChromeClient(new MyWebChromeClient());

            // the default (home) page will be the 2nd entry in the BOOKMARKS array
            urlString = BOOKMARK[1].url;
            webView.loadUrl(urlString);
        }

        // attach the WebView to its place-holder
        webViewPlaceholder.addView(webView); // API Note 4

        urlField = (EditText)findViewById(R.id.url_field);
        go = (Button)findViewById(R.id.go);
        back = (Button)findViewById(R.id.left);
        forward = (Button)findViewById(R.id.right);

        updateButtons();
        urlField.setText(urlString);
        urlField.setSelection(urlField.getText().length()); // move caret to end

        toastBeforeExit = true; // to give a toast warning before exiting

        go.setOnClickListener(this);
        back.setOnClickListener(this);
        forward.setOnClickListener(this);
        urlField.setOnEditorActionListener(this);

        bookmarkTitles = new String[BOOKMARK.length];
        for (int i = 0; i < bookmarkTitles.length; ++i)
            bookmarkTitles[i] = BOOKMARK[i].name;

        // hide the soft keyboard (will appear when the user taps the EditText field)
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
    } // end initUI

    // return the pixel density of the device's display
    public float getPixelDensity()
    {
        // determine the pixel density of the device's display
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.density;
    }

    /*
     * We've got three buttons: Go, Back, and Forward. Handle the button clicks here.
     */
    @Override
    public void onClick(View view)
    {
        if (view == go)
        {
            // get the new URL (as edited in the URL text field)
            urlString = urlField.getText().toString();

            // move caret to end
            urlField.setSelection(urlField.getText().length());

            // cleartext traffic is disabled by default starting in Android 9 (SDK 28)
            // alternatively, add android:usesCleartextTraffic="true" to the AndroidManifest <application/>
            if (urlString.startsWith("http://")) {
                urlString = urlString.replaceFirst("http://", "https://");
            }
            else if(urlString.startsWith("www.")) {
                urlString = "https://" + urlString;
            }
            else if (!urlString.startsWith("https://")) {
                urlString = "https://www." + urlString;
            }

            // load it in the WebView
            webView.loadUrl(urlString);

            // hide the soft keyboard
            // getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

            // hide the soft keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(webView.getWindowToken(), 0);

        } else if (view == back && webView.canGoBack())
        {
            webView.goBack();
            updateButtons();

        } else if (view == forward && webView.canGoForward())
        {
            webView.goForward();
            updateButtons();
        }
    }

    private void updateButtons()
    {
        if (webView.canGoBack())
            back.setEnabled(true);
        else
            back.setEnabled(false);

        if (webView.canGoForward())
            forward.setEnabled(true);
        else
            forward.setEnabled(false);
    }

    /*
     * Intercept presses of "Back" in the device's Navigation Bar. If our WebView has a back
     * history, go back. Otherwise, defer to the super method (likely terminate the app). See...
     *
     * http://developer.android.com/training/implementing-navigation/temporal.html#back-webviews
     */
    @Override
    public void onBackPressed()
    {
        if (webView.canGoBack())
        {
            webView.goBack();
            return;

        } else if (toastBeforeExit)
        {
            toastBeforeExit = false;
            Toast.makeText(this, "Press Back once more to exit", Toast.LENGTH_LONG).show();
            return;
        }
        super.onBackPressed();
    }

    /*
     * When the input method's "Send" action occurs, this callback executes. Fake a tap on the "Go"
     * button to load the edited URL in the WebView (and hide the soft keyboard).
     */
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
    {
        // process the new URL in the Go button's onClick callback
        onClick(go);

        // hide the soft keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(webView.getWindowToken(), 0);
        return true;
    }

    // setup an Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, BOOKMARKS, BOOKMARKS, R.string.menu_bookmarks);
        menu.add(0, SETTINGS, SETTINGS, R.string.menu_settings);
        menu.add(0, HELP, HELP, R.string.menu_help);
        return true;
    }

    // Handle an Options menu selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case BOOKMARKS:
                showBookmarksDialog();
                break;

            case SETTINGS:
                Toast.makeText(this, "Settings! (no implementation)", Toast.LENGTH_SHORT).show();
                break;

            case HELP:
                Toast.makeText(this, "Help! (no implementation)", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    /*
     * We are handling configuration changes ourself, as per the "Android:configChanges" element in
     * the manifest. When a screen orientation change occurs, the activity is *not* shutdown and
     * restarted. Instead, this method is called.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        if (webView != null)
        {
            /*
             * Remove the WebView from the old place-holder. Once the new layout is setup, the
			 * WebView will be placed in the new place-older (an instance of FrameLayout).
			 */
            webViewPlaceholder.removeView(webView); // API Note 1
        }

        super.onConfigurationChanged(newConfig);

		/*
         * Load the layout resource for the new configuration (portrait or landscape). The new
		 * configuration has a new place-holder (an instance of FrameLayout).
		 */
        setContentView(R.layout.main); // API Note 2

		/*
         * Re-initialize the UI. Concerning the WebView, two important things will happen in the
		 * initUI method: (i) a reference to the new place-holder will be retrieved from the new
		 * layout, and (ii) the WebView is put in the place-holder. Note that through all of this,
		 * the WebView instance has remained intact.
		 */
        initUI();
    }

    private void showBookmarksDialog()
    {
        // Initialize the dialog
        AlertDialog.Builder parameters = new AlertDialog.Builder(this);
        parameters.setCancelable(false).setTitle(R.string.bookmarks_title).setItems(bookmarkTitles,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        webView.loadUrl(BOOKMARK[which].url);
                    }
                }).show();
    }

    // ============================
    // Inner classes defined at end
    // ============================

    // ==============================================================================================
    // content loaded in the WebView is managed by a WebViewClient
    private class MyWebViewClient extends WebViewClient
    {
        @Override
        public void onPageFinished(WebView view, String url)
        {
            // update the EditText field containing the URL (when the page is finished loading)
            urlString = url;
            urlField.setText(urlString);
            urlField.setSelection(urlField.getText().length()); // move caret to end

            // update the buttons' back/forward status
            updateButtons();

            // display the page's title in the Action Bar
            String s = webView.getTitle();
            if (s != null)
                getActionBar().setTitle(s);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon)
        {
            // display a loading message (replaced with URL in onPageFinished)
            getActionBar().setTitle("loading...");

            /*
             * Display a blank icon (will be replaced if onReceivedIcon executes)
			 *
			 * Note: For some reason, the Bitmap received is the favicon for the existing page, not
			 * the favicon for the page that is loading.
			 */
            if (Build.VERSION.SDK_INT >= 14)
                getActionBar().setIcon(R.drawable.blank_favicon); // API level 14
        }

        /*
         * Added to allow YouTube videos to show in a dedicated video view (via an intent). See...
         *
         * http://stackoverflow.com/questions/2292086/play-youtube-video-in-webview
         */
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url)
        {
            if (url.startsWith("vnd.youtube:") || url.indexOf("youtube.com") > 0)
            {
                int n = url.indexOf("?");
                if (n > 0)
                {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
                }
                return true; // we're done
            }
            return false; // bubble-up to generic implementation
        }
    }

    // ==============================================================================================
    /*
	 * As stated in the WebView API, the WebChromeClient class is called when something that might
	 * impact a browser UI happens. For instance, progress updates and JavaScript alerts are sent
	 * here.
	 * 
	 * Here, we're only using WebChromeClient to get notifications of favicons.
	 */
    private class MyWebChromeClient extends WebChromeClient
    {
        /*
         * This method only executes if the loaded web page has a favicon
         */
        @Override
        public void onReceivedIcon(WebView view, Bitmap favicon)
        {
            Drawable drawable = new BitmapDrawable(getResources(), favicon);

            // https://stackoverflow.com/questions/15299508/i-want-to-change-actionbar-icon-size
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
            Drawable d = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, (int)(50 * pixelDensity), (int)(50 * pixelDensity), true));

            if (Build.VERSION.SDK_INT >= 14)
                getActionBar().setIcon(d); // added in API level 14
        }
    }

    // ==============================================================================================
    // simple class to hold a bookmark as a name and a URL (both strings)
    private static class Bookmark
    {
        String name; // appears in the Options Menu
        String url; // used to load the page via WebView's loadUrl method

        Bookmark(String nameArg, String urlArg)
        {
            name = nameArg;
            url = urlArg;
        }
    }
}
