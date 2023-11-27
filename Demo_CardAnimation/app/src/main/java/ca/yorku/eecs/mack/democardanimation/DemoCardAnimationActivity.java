package ca.yorku.eecs.mack.democardanimation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Demo_CardAnimation - demo of "card" animations. Also demonstrates use of Android's <code>Shader</code> subclasses to
 * create gradient shading. <p>
 *
 * Related information: <p>
 *
 * <blockquote> API Guides:<p>
 *
 * <ul> <li><a href="http://developer.android.com/guide/topics/graphics/prop-animation.html">Property Animation</a> (and
 * subsection <a href="http://developer.android.com/guide/topics/graphics/prop-animation.html#declaring-xml" >Declaring
 * Animations in XML</a>) <li><a href="http://developer.android.com/guide/components/fragments.html">Fragments</a>
 * <li><a href="http://developer.android.com/guide/topics/resources/animation-resource.html">Animation Resources</a>
 * </ul><p>
 *
 * API References:
 *
 * <ul> <li><a href="http://developer.android.com/reference/android/animation/ValueAnimator.html">
 * <code>ValueAnimator</code></a> <li><a href="http://developer.android.com/reference/android/animation/ObjectAnimator.html">
 * <code>ObjectAnimator</code></a> <li><a href="http://developer.android.com/reference/android/animation/AnimatorSet.html">
 * <code>AnimatorSet</code></a> <li><a href="http://developer.android.com/reference/android/app/Fragment.html">
 * <code>Fragment</code></a> <li><a href="http://developer.android.com/reference/android/app/FragmentManager.html">
 * <code>FragmentManager</code></a> <li><a href="http://developer.android.com/reference/android/app/FragmentTransaction.html">
 * <code>FragmentTransaction</code></a> </ul><p>
 *
 * Android Training:
 *
 * <ul> <li><a href="http://developer.android.com/training/animation/index.html">Adding Animations</a> (and the
 * subsection <a href="http://developer.android.com/training/animation/cardflip.html">Displaying Card Flip
 * Animations</a>) <li><a href="http://developer.android.com/training/custom-views/making-interactive.html">Making the
 * View Interactive</a> </ul><p>
 *
 * Android Developers Blog:
 *
 * <ul> <li><a href="http://android-developers.blogspot.ca/2011/02/animation-in-honeycomb.html">Animation in
 * Honeycomb</a> <li><a href="http://android-developers.blogspot.ca/2011/05/introducing-viewpropertyanimator.html">
 * Introducing ViewPropertyAnimator</a> </ul>
 *
 * </blockquote>
 *
 * This demo implements ten "cards" representing top news stories of 2018. The news source is <a
 * href="https://www.ctvnews.ca/ctv-national-news/ctv-national-news-top-10-news-stories-of-2018-1.4227834"><code>ctvnews.ca</code></a>.
 * Each card has two sides. The front is an image collage depicting a story. The back presents a text summary of the
 * story. Upon launch, the application presents the front of the first card depicting the #1 new story of the year
 * (below left). If the user taps the cards, the card flips over to show a summary of the story (below right). The
 * front-to-back transition is animated. Obviously, the animation is not apparent in the screen snaps. <p>
 *
 * <center> <a href="DemoCardAnimation-2.jpg"><img src="DemoCardAnimation-2.jpg" width="300"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="DemoCardAnimation-3.jpg"><img src="DemoCardAnimation-3.jpg" width="300"></a> </center> <p>
 *
 * All card transitions in this demo are initiated using finger gestures. The animations are implemented using Android's
 * Property Animation framework which was introduced in Honeycomb (Android 3.0). The animations are demo'd using
 * gestures: <p>
 *
 * <center> <table border="1" cellspacing="0" cellpadding="6" width="80%"> <tr bgcolor="#cccccc"> <th
 * rowspan="2">Finger<br> Gesture <th colspan="2">Transition <th rowspan="2">Animation
 *
 * <tr bgcolor="#cccccc"> <th>From <th>To
 *
 * <tr> <td>Tap <td>Card font (or back) <td>Card back (or front) <td>Flip
 *
 * <tr> <td>Swipe left <td>Current card <td>Next card <td>Slide left
 *
 * <tr> <td>Swipe right <td>Current card <td>Previous card <td>Slide right
 *
 * <tr> <td>Long-press <td>Current card <td>New card (selected from menu) <td>Fade-in/scale </table> </center> <p>
 *
 * One of our earlier demo programs, Demo_GridView, also uses Android's Property Animation framework. There is a
 * difference, however. In Demo_GridView, the animations are implemented in code. Here, the animations are declared in
 * XML resource files. There are two advantages in declaring animations in XML resource files: (i) easy reuse of
 * animations in multiple activities, and (ii) easy editing of the animation sequence. <p>
 *
 * Declaring an animation in an XML resource file is one thing. Getting the animation to actually run is a bit tricky,
 * however. Let's continue with a high-level description of the demo's organization and how the animations are run. <p>
 *
 * The layout for the main activity is defined in <code>main.xml</code> which includes a single <code>FrameLayout</code>
 * element: <p>
 *
 * <pre>
 *      &lt;FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
 *           android:id="@+id/container"
 *           android:layout_width="match_parent"
 *           android:layout_height="match_parent" /&gt;
 * </pre>
 * <p>
 *
 * The <code>FrameLayout</code> has the Id <code>container</code> (see above). It is a "container" into which
 * <code>View</code> instances are added and removed. At any point in time, the <code>View</code> instance in the
 * <code>FrameLayout</code> is either an <code>ImageView</code> (the image collage, card front) or a
 * <code>LinearLayout</code> (the summary text, card back). These views are the layouts for two fragments, which are
 * added to or removed from the main activity as the UI processes finger gestures. <p>
 *
 * A fragment is like a "sub-activity" which can be added to or removed from a main activity. Android fragments are
 * relatively easy to work with. (Consult the API Guides or API References for full details; links above). Here, we
 * define two fragment classes. Each is an inner-class that <code>extends Fragment</code>. One is for a card front
 * (<code>CardFrontFragment</code>), the other is for a card back (<code>CardBackFragment</code>). As an example,
 * <code>CardFrontFragment</code> is defined as follows: <p>
 *
 * <pre>
 *      public static class CardFrontFragment extends Fragment
 *      {
 *           CardFrontImageView frontView; // subclass of ImageView
 *
 *           public CardFrontFragment() { }
 *
 *           &#64;Override
 *           public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
 *           {
 *                frontView = (CardFrontImageView)inflater.inflate(R.layout.fragment_card_front, container, false);
 *                frontView.setImageResource(imageId);
 *                frontView.setTitleText(title);
 *                return frontView;
 *           }
 *      }
 * </pre>
 *
 * The layout for <code>CardFrontFragment</code> is specified in <code>fragment_card_front.xml</code>, which simply
 * holds an <code>ImageView</code> into which an image is put. The layout itself is put into the main activity's
 * <code>FrameLayout</code>, which bears the Id <code>container</code> (see above). <p>
 *
 * The <code>ImageView</code> for the card front is actually an instance of <code>CardFrontImageView</code>, which
 * extends <code>ImageView</code>. A custom class is used since a title is superimposed on the image (details below).
 * </p>
 *
 * So, the main task in this demo is to add new fragments to the main activity while removing old fragments. We now
 * describe how fragments representing cards are added and removed. <p>
 *
 * All card transitions are initiated using touch gestures, which are handled by implementing
 * <code>onSingleTapUp</code>, <code>onLongPress</code>, and <code>onFling</code>. These are listener methods defined in
 * <code>GestureDetector.OnGestureListener</code> (see <code>DemoScale</code> for further discussion). As an example,
 * the card "flip" animation is initiated in <code>onSingleTapUp</code>. The method includes a single call to
 * <code>flipCard</code>. Here's the code for <code>flipCard</code>: <p>
 *
 * <pre>
 *      private void flipCard()
 *      {
 *           showingBack = !showingBack; // toggle showing back/front
 *
 *           if (showingBack)
 *           {
 *                getFragmentManager().beginTransaction()
 *                     .setCustomAnimations(R.animator.card_flip_enter, R.animator.card_flip_exit)
 *                     .replace(R.id.container, new CardBackFragment())
 *                     .commit();
 *           } else
 *           {
 *                getFragmentManager().beginTransaction()
 *                     .setCustomAnimations(R.animator.card_flip_enter, R.animator.card_flip_exit)
 *                     .replace(R.id.container, new CardFrontFragment())
 *                     .commit();
 *           }
 *      }
 * </pre>
 *
 * Flipping cards is a matter of swapping fragments. This is done by initiating a transaction
 * (<code>beginTransaction</code>) on the main activity's <code>FragmentManager</code>. Swapping is done via the
 * <code>replace</code> method, which removes the current fragment and replaces it with a new fragment. <p>
 *
 * The code to swap fragments includes a series of chained method calls on the main activity's
 * <code>FragmentManager</code>. As well as <code>beginTransaction</code> and <code>replace</code> (just noted), there
 * is a call to <code>setCustomAnimations</code> (see above). This method takes two arguments. Both arguments are Ids
 * for XML animation resources. The first argument is the animation to run for the entering fragment. The second
 * argument is the animation to run for the exiting fragment. Bear in mind that the animations are run on the view of
 * the fragments, not on the fragments per se. The animation resources hold the XML elements that specify the animation.
 * The following shows the contents of <code>res/animator/card_flip_enter.xml</code> &ndash; the animation for bringing
 * a card into view: (This appears below as an image, since the XML is well-commented. Please review in detail.) <p>
 *
 * <center> <a href="DemoCardAnimation-1.jpg"><img src="DemoCardAnimation-1.jpg" width="600"></a> </center> <p>
 *
 * And that's about it. <p>
 *
 * As well as the "flip" animation, this demo includes a "slide" animation and a "fade-in/scale" animation. The slide
 * animation occurs in response to a swipe left or swipe right gesture (in <code>onFling</code>). The effect is to slide
 * the current card out of view and the next or previous card into view. The process is much the same as for the flip
 * animation. Of course, different animation resource files are specified in the <code>setCustomAnimations</code>
 * method. <p>
 *
 * The fade-in/scale animation is used when a new card is selected from a popup menu. The menu appears in response to a
 * long-press gesture (in <code>onLongPress</code>): <p>
 *
 * <center> <a href="DemoCardAnimation-4.jpg"><img src="DemoCardAnimation-4.jpg" width="300"></a> </center> <p>
 *
 * Another feature demo'd is the use of Android's <code>Shader</code> class (actually, its subclasses) to create a
 * background for the card title. The background has gradient shading around the edges, creating a fading-out feathered
 * effect. This is seen in the 1st image above. Creating the gradient is delegated to a method called
 * <code>getGradientEdgedRectangle</code> which is called from the <code>onSizeChanged</code> method in the
 * <code>CardFrontImageView</code> class: </p>
 *
 * <pre>
 *      background = getGradientSquare(backgroundWidth, backgroundHeight, gradientSize, 0xddff0000);
 * </pre>
 *
 * The <code>getGradientEdgedRectangle</code> method returns a bitmap. In the <code>onDraw</code> method, the bitmap is
 * drawn onto the canvas (after which the text for the title is drawn): </p>
 *
 * <pre>
 *      &#64;Override
 *      public void onDraw(Canvas canvas)
 *      {
 *           // draw image
 *           super.onDraw(canvas);
 *
 *           // draw the background for the title
 *           canvas.drawBitmap(background, backgroundLeft, backgroundTop, null);
 *
 *           // draw the text for the title
 *           canvas.drawText(titleText, xText, yText + yOffset, textPaint);
 *      }
 * </pre>
 *
 * The gradient bitmap is built-up as a series of seven regions which are drawn into the bitmap to create the desired
 * effect for the background and the edges: <p>
 *
 * <center> <a href="DemoCardAnimation-5.jpg"><img src="DemoCardAnimation-5.jpg" width="600"></a> </center> <p>
 *
 * The central, left, and right regions are painted using a <code>LinearGradient</code> shader while the corner regions
 * are painted using a <code>RadialGradient</code> shader. Consult the source code and comments for further details.
 * <p>
 *
 * @author Scott MacKenzie, 2014-2018
 */
public class DemoCardAnimationActivity extends Activity implements View.OnTouchListener
{
    //final String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // keys for get/put methods
    final String IMAGE_KEY = "image_id_key";
    final String TITLE_KEY = "title_key";
    final String TEXT_KEY = "text_key";
    final String BACKGROUND_COLOR_KEY = "background_color_key";
    final String SHOWING_BACK_KEY = "showing_back_key";

    final String LAST_CARD_MESSAGE = "Last card";
    final String FIRST_CARD_MESSAGE = "First card";

    final int MIN_CARD_NUMBER = 1;
    final int MAX_CARD_NUMBER = 10;

    final int[] IMAGE_ID = {
            R.drawable.news01_2018, R.drawable.news02_2018, R.drawable.news03_2018, R.drawable.news04_2018,
            R.drawable.news05_2018, R.drawable.news06_2018, R.drawable.news07_2018, R.drawable.news08_2018,
            R.drawable.news09_2018, R.drawable.news10_2018
    };

    // resources for card front and back fragments, etc.
    static int imageId;
    static int cardNumber; // keep track of the card currently being viewed
    static String title, text;
    static int backgroundColor;

    String[] cardTitle;
    String[] cardText;
    int[] cardBackgroundColor;

    FrameLayout cardView; // we need this to attach the touch listener
    GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
    AlertDialog dialog; // to change cards (pops up on long-press)
    Vibrator vib; // vibrate when the dialog pops up
    boolean showingBack; // true = showing back of card

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        cardTitle = getResources().getStringArray(R.array.card_title);
        cardText = getResources().getStringArray(R.array.card_text);
        cardBackgroundColor = getResources().getIntArray(R.array.card_background_color);

        if (savedInstanceState == null)
        {
            // put the front of the card in the content view (as a fragment)
            getFragmentManager().beginTransaction().add(R.id.container, new CardFrontFragment()).commit();

            // default to Top Story card
            cardNumber = 1;
            setCard(cardNumber);
        }

        // UI responds to touch gestures
        cardView = (FrameLayout)findViewById(R.id.container);
        cardView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());

        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); // for long-press gesture

        // build a dialog to change cards (pops up on long-press)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.menu_title);
        builder.setItems(R.array.card_title, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                cardNumber = which + 1;
                setCard(cardNumber);
                getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_appear_enter,
                        R.animator.card_appear_exit).replace(R.id.container, new CardFrontFragment()).commit();
            }
        });
        dialog = builder.create();
    }

    /*
     * Set the resources for the specified card, then set showingBack to false to show the front of
     * the card.
     */
    private void setCard(int n)
    {
        imageId = IMAGE_ID[n - 1];
        title = cardTitle[n - 1];
        text = cardText[n - 1];
        backgroundColor = cardBackgroundColor[n - 1];
        showingBack = false; // next flip will show back
    }

    /*
     * Advance to the next card. The transition includes a "card slide" animation. If we are already
     * viewing the last card, do nothing (but use a Toast dialog to inform the user).
     */
    private void nextCard()
    {
        ++cardNumber;
        if (cardNumber <= MAX_CARD_NUMBER)
        {
            setCard(cardNumber);
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_slide_left_enter,
                    R.animator.card_slide_right_exit).replace(R.id.container, new CardFrontFragment()).commit();
        } else
        {
            cardNumber = MAX_CARD_NUMBER;
            Toast.makeText(this, LAST_CARD_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Advance to the previous card. The transition includes a "card slide" animation. If we are
     * already viewing the first card, do nothing (but use a Toast dialog to inform the user).
     */
    private void previousCard()
    {
        --cardNumber;
        if (cardNumber >= MIN_CARD_NUMBER)
        {
            setCard(cardNumber);
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_slide_right_enter,
                    R.animator.card_slide_left_exit).replace(R.id.container, new CardFrontFragment()).commit();
        } else
        {
            cardNumber = MIN_CARD_NUMBER;
            Toast.makeText(this, FIRST_CARD_MESSAGE, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * The UI responds to finger gestures. No need to get down-and-dirty with onTouch. Let Android's
     * GestureDetector determine the type of gesture and then do the work in the GestureDetector's
     * listener methods.
     */
    @Override
    public boolean onTouch(View v, MotionEvent me)
    {
        // let the gesture detector process the touch event (see MyGestureListener below)
        gestureDetector.onTouchEvent(me);
        return true;
    }

    private void flipCard()
    {
        // toggle showing back/front
        showingBack = !showingBack;

		/*
         * Create and commit a new fragment transaction to replace the current fragment. The
		 * transition includes a "card flip" animation.
		 */

        if (showingBack)
        {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_flip_enter,
                    R.animator.card_flip_exit).replace(R.id.container, new CardBackFragment()).commit();
        } else
        {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.card_flip_enter,
                    R.animator.card_flip_exit).replace(R.id.container, new CardFrontFragment()).commit();
        }
    }

    // save state variables in the event of a screen rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        String imageIdKey = IMAGE_KEY;
        savedInstanceState.putInt(imageIdKey, imageId);
        savedInstanceState.putString(TITLE_KEY, title);
        savedInstanceState.putString(TEXT_KEY, text);
        savedInstanceState.putInt(BACKGROUND_COLOR_KEY, backgroundColor);
        savedInstanceState.putBoolean(SHOWING_BACK_KEY, showingBack);
        super.onSaveInstanceState(savedInstanceState);
    }

    // restore state variables after a screen rotation
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        imageId = savedInstanceState.getInt(IMAGE_KEY);
        title = savedInstanceState.getString(TITLE_KEY);
        text = savedInstanceState.getString(TEXT_KEY);
        backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR_KEY);
        showingBack = savedInstanceState.getBoolean(SHOWING_BACK_KEY);
    }

    // ====================
    // Inner classes at end
    // ====================

    // ==================================================================================================
    // A fragment representing the front of the card (select image accordingly)
    public static class CardFrontFragment extends Fragment
    {
        CardFrontImageView frontView;

        public CardFrontFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            frontView = (CardFrontImageView)inflater.inflate(R.layout.fragment_card_front, container, false);
            frontView.setImageResource(imageId);
            frontView.setTitleText(title);
            return frontView;
        }
    }

    // ==================================================================================================
    // A fragment representing the back of the card (select text and background color accordingly)
    public static class CardBackFragment extends Fragment
    {
        View backView;
        TextView titleView, textView;

        public CardBackFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            backView = inflater.inflate(R.layout.fragment_card_back, container, false);
            titleView = (TextView)backView.findViewById(R.id.text1);
            textView = (TextView)backView.findViewById(R.id.text2);
            titleView.setText(title);
            textView.setText(text);
            backView.setBackgroundColor(backgroundColor);
            return backView;
        }
    }

    // ==================================================================================================
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapUp(MotionEvent me)
        {
            flipCard();
            return true;
        }

        @Override
        public void onLongPress(MotionEvent me)
        {
            vib.vibrate(50);
            dialog.show(); // show dialog to change card
        }

        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
        {
            if (velocityX < 0) // swipe left
                nextCard();
            else // swipe right
                previousCard();
            return true;
        }
    }
}
