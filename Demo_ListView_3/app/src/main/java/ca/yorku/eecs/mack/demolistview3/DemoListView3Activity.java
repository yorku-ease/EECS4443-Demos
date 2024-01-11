package ca.yorku.eecs.mack.demolistview3;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;

/**
 * Demo_ListVew_3 - demonstrate downloading and presenting images in a <code>ListView</code>. The images are read from
 * an Internet web site. <p>
 *
 * Related links:
 *
 * <blockquote> API Guides: <p> <ul> <li><a href="http://developer.android.com/guide/topics/ui/layout/listview.html">List
 * View</a> <li><a href="http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews">Building
 * Layouts with an Adapter</a> </ul> <p>
 *
 * API References: <p> <ul> <li><a href="http://developer.android.com/reference/android/widget/ListView.html">
 * <code>ListView</code></a> <li><a href="http://developer.android.com/reference/android/widget/ListAdapter.html">
 * <code>ListAdapter</code></a> <li><a href="http://developer.android.com/reference/android/app/ListActivity.html">
 * <code>ListActivity</code></a> <li><a href="http://developer.android.com/reference/android/widget/Adapter.html">
 * <code>Adapter</code></a> <li><a href="http://developer.android.com/reference/android/widget/BaseAdapter.html">
 * <code>BaseAdapter</code></a> </ul> <p>
 *
 * Android Developer Blogs: <p> <ul> <li><a href="http://android-developers.blogspot.ca/2010/07/multithreading-for-performance.html">
 * Multithreading For Performance</a> </ul> </blockquote>
 *
 * This demo is the third is a series of three demos of <code>ListView</code>:<p>
 *
 * <blockquote> <table border="1" cellspacing="0" cellpadding="6"> <tr bgcolor="#cccccc" width="100"> <th align="center"
 * >Demo <th align="center" >Content (view class) <th align="center" >Source of Content
 *
 * <tr> <td>Demo_ListView_1 <td>Strings (<code>TextView</code>) <td>Resources Array
 *
 * <tr> <td>Demo_ListView_2 <td>Images (<code>ImageView</code>) <td>Device's internal memory card
 *
 * <tr> <td>Demo_ListView_3 <td>Images (<code>ImageView</code>) <td>Internet web site
 *
 * </table> </blockquote> <p>
 *
 * Study Demo_ListView_1 and Demo_ListVew_2 before studying this demo.  This demo is more complicated because we are
 * reading images from an Internet web site.  A simpler demo, Demo_InternetDownload, is a stripped-down version of this
 * demo.  Demo_InternetDownload accesses just one image.  As a suggestion, also study Demo_InternetDownload prior to
 * studying this demo.<p>
 *
 * Instead of loading a single image (from an Internet web page) and presenting it in an <code>ImageView</code>, we
 * download a series of images and present them in <code>ImageView</code> objects which populate a
 * <code>ListView</code>. There are many additional details dealt with here, such as collecting together the images
 * using an <code>ImageAdapter</code> (a subclass of <code>BaseAdapter</code> which implements the
 * <code>ListAdapter</code> interface) and the caching of images already downloaded to avoid unnecessary Internet
 * access. <p>
 *
 * Here's a screen snap of the app with a partial view into the scrollable list of images: <p>
 *
 * <center><a href="./javadoc_images/DemoListView3-1.jpg"><img src="./javadoc_images/DemoListView3-1.jpg" width="300" alt="image"></a></center> <p>
 *
 * As with Demo_ListView_1 and Demo_ListView_2, the <code>onCreate</code> method in the main activity is very simple.
 * The final step for all three demos is to bind the <code>ListView</code> to a <code>ListAdapter</code>: <p>
 *
 * <pre>
 *      setListAdapter(new ImageAdapter(URLS));
 * </pre>
 *
 * In this case, our <code>ListAdapter</code> and an <code>ImageAdapter</code> are configured to receive an
 * array of URLs as an argument.  The URLs reference images on an Internet web site.<p>
 *
 * Within <code>ImageAdapter</code>, an <code>ImageDownloader</code> object is declared an instantiated. Within the
 * <code>getView</code> method, the <code>download</code> method of <code>ImageDownloader</code> is called to do the
 * actual downloading of images. <p>
 *
 * An <code>ImageDownloader</code> class was also used in Demo_InternetDownload. The core purpose remains the same
 * &ndash; to host an inner class named <code>BitmapDownloader</code>, a subclass of <code>AsyncTask</code> (which
 * handles the downloading in a separate thread; see <code>DemoWebDownload</code>). Additional functionality is added
 * here, since we are downloading a series of images that appear in a scrollable <code>ListView</code> (rather than
 * downloading a single image and placing it in an <code>ImageView</code>). Additional cache-related fields and methods
 * are added to improve efficiency. <p>
 *
 * Since this program is accessing the Internet, the manifest must include the following permission (placed just before
 * the <code>application</code> element): <p>
 *
 * <pre>
 *      &lt;uses-permission android:name="android.permission.INTERNET"/&gt;
 * </pre>
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013-2018
 */

@SuppressWarnings("unused")
public class DemoListView3Activity extends ListActivity
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    private static final String[] URLS = {"http://www.yorku.ca/mack/nordichi2012-f1.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f2.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f3.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f4a.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f4b.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f5.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f6.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f7.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f8.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f9.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f10.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f11.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f12.jpg",
            "http://www.yorku.ca/mack/nordichi2012-f13.jpg"};


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Log.i(MYDEBUG, "onCreate!");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout);
        setListAdapter(new ImageAdapter(URLS));
    }
}
