package ca.yorku.eecs.mack.demolistview1;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ListView;


/**
 * Demo_ListView_1 - demonstrates presenting an array of words in a <code>ListView</code> <p>
 *
 * Related links:
 *
 * <blockquote> API Guides: <p>
 *
 * <ul>
 *
 * <li><a href="http://developer.android.com/guide/topics/ui/layout/listview.html">List View</a>
 *
 * <li><a href="http://developer.android.com/guide/topics/ui/declaring-layout.html#AdapterViews">Building Layouts with
 * an Adapter</a>
 *
 * </ul> <p>
 *
 * API References: <p>
 *
 * <ul>
 *
 * <li><a href="http://developer.android.com/reference/android/widget/ListView.html"> <code>ListView</code></a>
 *
 * <li><a href="http://developer.android.com/reference/android/app/ListActivity.html">
 * <code>ListActivity</code></a>
 *
 * <li><a href="http://developer.android.com/reference/android/widget/ListAdapter.html"> <code>ListAdapter</code></a>
 * (interface)
 *
 * <li><a href="http://developer.android.com/reference/android/widget/Adapter.html"> <code>Adapter</code></a>
 * (interface)
 *
 * <li><a href="http://developer.android.com/reference/android/widget/BaseAdapter.html"> <code>BaseAdapter</code></a>
 * (implements ListAdapter)
 *
 * </ul> <p>
 *
 * </blockquote>
 *
 * This is the first in a series of three demos using <code>ListView</code>:
 * <p>
 *
 * <blockquote>
 * <table border="1" cellspacing="0" cellpadding="6">
 * <tr bgcolor="#cccccc" width="100">
 * <th align="center" >Demo
 * <th align="center" >Content (view class)
 * <th align="center" >Source of Content
 *
 * <tr>
 * <td>Demo_ListView_1
 * <td>Strings (<code>TextView</code>)
 * <td>Resources Array
 *
 * <tr>
 * <td>Demo_ListView_2
 * <td>Images (<code>ImageView</code>)
 * <td>Device's internal memory card
 *
 * <tr>
 * <td>Demo_ListView_3
 * <td>Images (<code>ImageView</code>)
 * <td>Internet web site
 *
 * </table>
 * </blockquote>
 * <p>
 *
 * The first demo is extremely simple: Each
 * item in the list is a <code>String</code> &ndash; a word taken from an array of 9022 words. <p>
 *
 * Here's a screen snap of the app with a partial view into the scrollable list of words: <p>
 *
 * <center><a href="DemoListView1-1.jpg"><img src="DemoListView1-1.jpg" width="300" alt="image"></a></center> <p>
 *
 * In the code, note that the main activity extends <code>ListActivity</code> (not <code>Activity</code>). As noted in
 * the API
 * Reference, a <code>ListActivity</code> is <p>
 *
 * <blockquote><i> an activity that displays a list of items by binding to a data source such as an array or
 * <code>Cursor</code>, and exposes event handlers when the user selects an item. <code>ListActivity</code> hosts a
 * <code>ListView</code> object that can be bound to different data sources, typically either an array or a
 * <code>Cursor</code> holding query results. </i> </blockquote> <p>
 *
 * The data source for this demo is an array. <p>
 *
 * The main activity includes just one method, <code>onCreate</code>. Four primary tasks are handled in
 * <code>onCreate</code>. First, we set the content view (UI) for the app to the XML layout that contains the
 * <code>ListView</code>: <p>
 *
 * <pre>
 *      setContentView(R.layout.list_view_layout);
 * </pre>
 *
 * In <code>list_view_layout.xml</code>, the <code>ListView</code> is declared: <p>
 *
 * <pre>
 *      &lt;ListView
 *         android:id="@android:id/list"
 *         android:layout_width="match_parent"
 *         android:layout_height="0dp"
 *         android:layout_weight="1"
 *         android:background="#888888"
 *         android:fastScrollEnabled="true" /&gt;
 * </pre>
 *
 * Note that providing a screen layout with a <code>ListView</code> object with the id "<code>@android:id/list</code>"
 * is required when using <code>ListActivity</code>. <p>
 *
 * The second task in <code>onCreate</code> is to get a reference to the array of words (which is included in the demo
 * as a resource):<p>
 *
 * <pre>
 *      String[] words = getResources().getStringArray(R.array.words);
 * </pre>
 *
 * Third, an adapter is created that is bound to the array of words: <p>
 *
 * <pre>
 *      WordAdapter wa = new WordAdapter(words);
 * </pre>
 *
 * The adapter class is <code>WordAdapter</code> which extends <code>BaseAdapter</code> (see below). <p>
 *
 * Finally, a reference to the ListView is obtained and the <code>ListView</code> is then given the new adapter which
 * provides the content for the <code>ListView</code>: <p>
 *
 * <pre>
 *      ListView lv = (ListView)findViewById(android.R.id.list);
 *      lv.setAdapter(wa);
 * </pre>
 *
 * With this, the app is created and the UI appears with a <code>ListView</code> object populated with <code>List</code>
 * objects.  Each <code>List</code> object is an instance of <code>TextView</code> containing a string of text &ndash;
 * a word taken from the array of words.  As many words appear as will fit on the device's display.  The user many
 * scroll
 * up and down through the list in the usual way, using touch gestures. Scrolling is also possible by moving the
 * scrollbar's handle.  One added feature is "indexed scrolling", discussed below.<p>
 *
 * Our adapter is an instance of <code>WordAdapter</code>, a custom class that extends <code>BaseAdapter</code> (which
 * extends <code>Adapter</code>). We override and implement four of the methods: <code>getCount</code>,
 * <code>getItem</code>, <code>getItemId</code>, and <code>getView</code>. <p>
 *
 * The work of actually getting the words and placing them in the list occurs in <code>getView</code>.  As the list is
 * scrolled up and down, words are retrieved as necessary, based on the "position" in the array of new words scrolling
 * into view.  The words are placed in the <code>TextView</code> objects which form the views populating the
 * <code>ListView</code>.
 * <p>
 *
 * Bear in mind that even though there are 9022 words in the array, <code>View</code> objects &ndash; actually
 * <code>TextView</code> objects &ndash; are instantiated in <code>getView</code> only as necessary to fill the device's
 * display.  As scrolling takes place, most of the words that appear are placed in <code>TextView</code> objects that
 * already exist.  Existing <code>TextView</code> objects are simply repositioned and updated to show the next word to
 * be displayed. <p>
 *
 * <b>Indexed Scrolling</b><p>
 *
 * Scrolling is improved using indexed scrolling:
 * <p>
 *
 * <center><a href="DemoListView1-2.jpg"><img src="DemoListView1-2.jpg" width="300" alt="image"></a></center> <p>
 *
 * The user may touch and drag the scrolling elevator to move quickly over large distances in the underlying
 * data.  The pop-out letter beside the scrollbar handle provides a visual cue of the current
 * location
 * of scrolling.
 * <p>
 *
 * To enable indexed scrolling, the ScrollView must be configured to perform fast indexing. This can be done
 * two ways: (i) programmatically, by invoking the <code>setFastScrollEnabled</code> method on the ListView, or (ii) by
 * adding the
 * <code>fastScrollEnabled</code> attribute to the <code>ScrollView</code> element in the XML layout file.  The
 * latter approach is used here, as seen in the XML code above.
 * <p>
 *
 * As well, the adapter must be configured to implement the <code>SectionIndexer</code> interface.  This entails
 * implementing three interface methods and dividing the ScrollView contents into sections.  Full details are in
 * <code>WordAdapter.java</code>.
 * <p>
 *
 * @author (c) Scott MacKenzie, 2016-2018
 */

@SuppressWarnings("unused")
public class DemoListView1Activity extends ListActivity
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // make the ListVew the UI for the application
        setContentView(R.layout.list_view_layout);

        // get the array of words to present in the ListView
        String[] words = getResources().getStringArray(R.array.words);

        // create a word adapter bound to the array of words
        WordAdapter wa = new WordAdapter(words);

        // get a reference to the ListView
        ListView lv = (ListView)findViewById(android.R.id.list);

        // give the adapter to the ListView
        lv.setAdapter(wa);
    }
}
