package ca.yorku.eecs.mack.demoquotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

/**
 * <style> pre {font-size:110%} </style>
 *
 * Demo_Quotation - demo program to display quotations by famous people. Also includes a quiz to test the user's
 * knowledge of quotations. </p>
 *
 * This program brings together many concepts demonstrated in earlier demos, in particular Demo_Settings and
 * Demo_CardAnimation. </p>
 *
 * Quotations are embedded in the application as resources. Each Quotation has two parts, text and an image. The
 * quotation text is stored in a large array in <code>res/values/quotation.xml</code>. The image for the person quoted
 * is stored in <code>res/drawable-nodpi/quotation_<i>xxx_yyy</i>.png</code>, where <code><i>xxx_yyy</i></code> is the
 * person's name. </p>
 *
 * When the program launches, an array of <code>Quotation</code> objects is created, then a quotation is selected at
 * random and presented for viewing: </p>
 *
 * <center> <a href="./javadoc_images/DemoQuotation-1.jpg"><img src="./javadoc_images/DemoQuotation-1.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="./javadoc_images/DemoQuotation-2.jpg"><img src="./javadoc_images/DemoQuotation-2.jpg" width="375"></a> </center> </p>
 *
 * Separate fragments are used for the image UI and the quote UI. As seen above, we see both the person and the wisdom
 * imparted. Apparently, Phyllis Diller was not too fond of housework. The app responds to an orientation change with a
 * new layout, while retaining the quotation (above right). </p>
 *
 * The information in the quotations array resource is divided into four parts, with "#" delimiting the parts. The parts
 * are (i) the person's name, (ii) the life dates, (iii) the quotation, and (iv) a brief biography (bio). The entry for
 * Phyllis Diller follows: <p>
 *
 * <pre>
 *        &lt;item&gt;
 *        Phyllis Diller
 *        #(1917-2012)
 *        #Housework can\'t kill you. But why take the chance?
 *        #An American comedian (and actress and voice artist) best known for her eccentric stage persona
 *        and her wild hair and clothes. Diller\'s voice acting roles included the Queen in A Bug\'s Life.
 *        &lt;\item&gt;
 * </pre>
 * </p>
 *
 * The first three words in the bio are chosen with particular care, as they are used for a hint in the quiz (describe
 * shortly). If the image is swiped, it flips over to show the bio (below left). A tap selects a new quotation at random
 * (below center). To search for a quotation by a specific person, a long-tap is used. In this case, a scrollable list
 * of all names pops up (below right). Scroll and tap to select a new quotation. All transitions are animated. See
 * Demo_CardAnimation for details.</p>
 *
 * <center> <a href="./javadoc_images/DemoQuotation-3.jpg"><img src="./javadoc_images/DemoQuotation-3.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
 * <a href="./javadoc_images/DemoQuotation-4.jpg"><img src="./javadoc_images/DemoQuotation-4.jpg" width="250"></a>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; <a
 * href="./javadoc_images/DemoQuotation-5.jpg"><img src="./javadoc_images/DemoQuotation-5.jpg" width="250"></a> </center> </p>
 *
 * There is an Options menu which is activated via the Action Overflow in the Action Bar. There are three options: </p>
 *
 * <center> <a href="./javadoc_images/DemoQuotation-6.jpg"><img src="./javadoc_images/DemoQuotation-6.jpg" width="300"></a></center> </p>
 *
 * The Help entry is located at the bottom of the Options Menu, as recommended in the Android Design documentation (<a
 * href="http://developer.android.com/design/patterns/help.html">click here</a>). There is at present no implementation
 * for help, except a Toast popup. The Settings entry is located just above Help. This location is also recommended (<a
 * href="http://developer.android.com/design/patterns/settings.html">click here</a>). Several Settings options are
 * available (discussed shortly). </p>
 *
 * The top item in the Options Menu is Quiz. If the user taps Quiz, the application transitions from viewing quotations
 * to a quiz. The user can take a quiz to test his or her knowledge of <i>who said what</i>. An example follows. </p>
 *
 * <center> 1-<a href="./javadoc_images/DemoQuotation-7.jpg"><img src="./javadoc_images/DemoQuotation-7.jpg" width="200"></a> 2-<a
 * href="./javadoc_images/DemoQuotation-8.jpg"><img src="./javadoc_images/DemoQuotation-8.jpg" width="200"></a> 3-<a href="./javadoc_images/DemoQuotation-9.jpg"><img
 * src="./javadoc_images/DemoQuotation-9.jpg" width="200"></a> 4-<a href="./javadoc_images/DemoQuotation-10.jpg"><img src="./javadoc_images/DemoQuotation-10.jpg"
 * width="200"></a> 5-<a href="./javadoc_images/DemoQuotation-11.jpg"><img src="./javadoc_images/DemoQuotation-11.jpg" width="200"></a> </center> </p>
 *
 * The first quotation in the example quiz (above, 1) is something about apple pie and the universe. I have no idea who
 * said this. I'll guess and tap the first answer, Agatha Christie (above, 2). Nope! Did you notice the text "(swipe for
 * hint)" in the first slide above (click to enlarge)? Let's swipe and get a hint (above, 3). An astronomer! Hmm, let's
 * see.... Well, George W. Bush is pretty spaced out, but I don't think he qualifies as an astronomer. Carl Sagan? Yes,
 * he's an astronomer. Tap (above, 4). Got it! After answering a few more questions, the quiz finishes. Results pop up
 * (above, 5). Not bad: 4 out of 5 correct, with only one hint given. But, I took 378.8 seconds, which is huge amount of
 * time just to answer five questions. (FYI, the reason I took so long is that I grabbed some screen snaps along the way
 * and saved them for this API). </p>
 *
 * Adding a quiz to the application provides both engagement and motivation for the user. Furthermore, there are
 * interesting issues of human performance, since the user's speed and accuracy are recorded. Speed and accuracy are
 * common dependent variables in studies to evaluate user interfaces. For further discussion, see the API for
 * Demo_TiltBall, the requirements for Lab #4, and the requirements for the course project. </p>
 *
 * A few aspects of the Quiz may be controlled through Settings: </p>
 *
 * <center> <a href="./javadoc_images/DemoQuotation-12.jpg"><img src="./javadoc_images/DemoQuotation-12.jpg" width="500"></a> </center> </p>
 *
 * The first setting, Quiz Length, sets the number of questions in the quiz. The options are presented using an instance
 * of <code>ListPreference</code> as declared in <code>res/xml/preferences.xml</code>: </p>
 *
 * <center><a href="./javadoc_images/DemoQuotation-13.jpg"><img src="./javadoc_images/DemoQuotation-13.jpg" width="250"></a> </center> </p>
 *
 * The remaining options are checkbox items. These are instances of <code>CheckBoxPreference</code>, also declared in
 * <code>res/xml/preferences.xml</code>. The second setting, Quiz Hints, enables or disables quiz hints. If disabled,
 * the text "(swipe for hint)" changes to "(sorry no hint)" and swipes are ignored. The third setting, Quiz Vibration,
 * enables or disables vibration during a quiz. If enabled, a wrong answer is accompanied with a 50 ms vibrotactile
 * pulse. The fourth setting, Quiz Audio, enables or disables auditory feedback during a quiz. If enabled, unique
 * auditory sounds are emitted for a correct answer, a wrong answer, and completing the quiz. </p>
 *
 * All the Settings are saved as shared preferences using the services provided by Android's <code>Preference</code>
 * APIs. The details were demonstrated and discussed in Demo_Settings. Consult for details. </p>
 *
 * And that's about it. Have fun. And, oh yes, consult the source code for complete details. </a>
 *
 * @author Scott MacKenzie, 2014-2018
 */
public class DemoQuotationActivity extends Activity implements View.OnTouchListener
{
    public final static String QUIZ_LENGTH_KEY = "number_of_questions"; // also used in QuizActivity
    public final static String QUIZ_HINTS_KEY = "quiz_hints"; // also used in QuizActivity
    public final static String QUIZ_VIBRATE_KEY = "quiz_vibrate"; // also used in QuizActivity
    public final static String QUIZ_AUDIO_KEY = "quiz_audio"; // also used in QuizActivity

    private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // Options menu items (used for groupId and itemId)
    private final static int QUIZ = 0;
    private final static int SETTINGS = 1;
    private final static int HELP = 2;

    /*
     * NOTE: the following int is the resource Id for the FIRST drawable. If any new drawable is
     * inserted that lexically precedes the entry below, edit accordingly. Defining an int like this
     * enables the use of a for-loop to load resources (see below).
     *
     * NOTE: The drawable resources (in res/drawable-nodpi) must correspond one for one with the
     * quotations (in res/values/quotations.xml).
     */
    private final static int FIRST_DRAWABLE = R.drawable.quotation_abraham_lincoln;
    // keys for get/put methods
    private final static String SHOWING_BACK_KEY = "showing_back";
    private final static String QUOTE_INDEX_KEY = "quote_index";
    private final static String BACK_STACK_KEY = "back_stack";
    private final static String SHUFFLED_ARRAY_KEY = "shuffled_array";
    private final static String RANDOM_INDEX_KEY = "random_index";
    private final static String NAMES_KEY = "names";
    private final static String ALLOW_HINTS_KEY = "allow_hints";
    private final static String VIBRATE_ON_WRONG_KEY = "vibrate_on_wrong";
    private final static String AUDIO_KEY = "audio_key";
    private final static String NUMBER_OF_QUESTIONS_KEY = "number_of_questions";

    /*
     * These variables are static because they are referenced in the Fragments used to create the UI
     * for the activity. The Fragments themselves are static, as required. See...
     *
     * http://developer.android.com/guide/components/fragments.html
     */
    static Quotation[] quotation;
    static int quotationIdx; // keep track of the card currently being viewed

    FrameLayout topView, bottomView; // we need these to attach the touch listeners
    GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
    Vibrator vib; // vibrate when the dialog pops up
    Random r;
    ArrayList<Integer> myBackStack;
    SharedPreferences sp;
    boolean showingBack; // true = showing back of card
    int firstImageId;
    boolean toastBeforeExit;
    boolean allowHints, vibrateOnWrong, audio;
    int[] shuffled;
    int randomIdx; // to step through the array of random-without-replacement indices
    int numberOfQuestions;
    String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        toastBeforeExit = true; // the user gets one reminder before exiting app

        // UI responds to touch gestures
        topView = (FrameLayout)findViewById(R.id.container1);
        bottomView = (FrameLayout)findViewById(R.id.container2);
        topView.setOnTouchListener(this);
        bottomView.setOnTouchListener(this);
        gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());

        // init vibrator (used for long-press gesture)
        vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

        if (savedInstanceState == null) // the activity is being created for the 1st time
        {
            // initialize SharedPreferences instance and load the settings
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();

            // load the quotations (and related info) from the string array resource
            String[] quotationResourceArray = getResources().getStringArray(R.array.quotations);
            quotation = new Quotation[quotationResourceArray.length];

            // read the quotations info and create an array of Quotation objects
            firstImageId = FIRST_DRAWABLE;
            for (int i = 0; i < quotation.length; ++i)
            {
                String[] s = quotationResourceArray[i].split("#");
                quotation[i] = new Quotation(s[0], s[1], s[2], s[3], firstImageId + i);
            }
            Log.i(MYDEBUG, "Number of quotations = " + quotation.length);

            // create an array of indices and initialize it with sequential indices
            shuffled = new int[quotation.length];
            for (int i = 0; i < shuffled.length; ++i)
                shuffled[i] = i;

            // shuffle the array (allows randomQuotation to be random-without-replacement)
            r = new Random();
            int tmp;
            for (int i = 0; i < shuffled.length; ++i)
            {
                int randomIdx = r.nextInt(shuffled.length);
                tmp = shuffled[i];
                shuffled[i] = shuffled[randomIdx];
                shuffled[randomIdx] = tmp;
            }

            // build a string of names (used in showNamesListDialog)
            names = new String[quotation.length];
            for (int i = 0; i < names.length; ++i)
                names[i] = quotation[i].famousPerson;

            // a simple backstack (just store the indices of the quotations visited)
            myBackStack = new ArrayList<Integer>();

            randomIdx = 0; // ... to access the 1st entry in the array of shuffled indices
            quotationIdx = shuffled[randomIdx];

            // put the fragments UIs in the activity's content
            getFragmentManager().beginTransaction().add(R.id.container1, new ImageFragment()).commit();
            getFragmentManager().beginTransaction().add(R.id.container2, new QuoteFragment()).commit();
        }
    }

    private void loadSettings()
    {
        // build the keys (makes the code more readable)
        final String PREF_QUIZ_LENGTH_KEY = getBaseContext().getString(R.string.pref_quiz_length_key);
        final String PREF_QUIZ_HINTS_KEY = getBaseContext().getString(R.string.pref_quiz_hints_key);
        final String PREF_QUIZ_VIBRATE_KEY = getBaseContext().getString(R.string.pref_quiz_vibrate_key);
        final String PREF_QUIZ_AUDIO_KEY = getBaseContext().getString(R.string.pref_quiz_audio_key);

        numberOfQuestions = Integer.parseInt(sp.getString(PREF_QUIZ_LENGTH_KEY, "1"));
        allowHints = sp.getBoolean(PREF_QUIZ_HINTS_KEY, true);
        vibrateOnWrong = sp.getBoolean(PREF_QUIZ_VIBRATE_KEY, false);
        audio = sp.getBoolean(PREF_QUIZ_AUDIO_KEY, false);
    }

    // Setup an Options menu (used for Quiz, etc.).
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        menu.add(0, QUIZ, QUIZ, R.string.menu_quiz);
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
            case QUIZ:
                final Bundle b = new Bundle();
                b.putInt(QUIZ_LENGTH_KEY, numberOfQuestions);
                b.putBoolean(QUIZ_HINTS_KEY, allowHints);
                b.putBoolean(QUIZ_VIBRATE_KEY, vibrateOnWrong);
                b.putBoolean(QUIZ_AUDIO_KEY, audio);

                Intent quizIntent = new Intent(getApplicationContext(), QuizActivity.class);
                quizIntent.putExtras(b);
                startActivityForResult(quizIntent, QUIZ);
                return true;

            case SETTINGS:
                // launch the SettingsActivity to allow the user to change the app's settings
                Intent i = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(i, SETTINGS);
                break;

            case HELP:
                Toast.makeText(this, "Not Implemented!", Toast.LENGTH_SHORT).show();

        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        // if (requestCode == QUIZ) nothing to do... (just continue viewing the quotations)

        if (requestCode == SETTINGS)
        {
            sp = PreferenceManager.getDefaultSharedPreferences(this);
            loadSettings();
        }
    }

    /*
     * Select the next random quotation and show it.
     */
    private void randomQuotation()
    {
        myBackStack.add(quotationIdx);
        quotationIdx = shuffled[++randomIdx % quotation.length];
        showingBack = false;
        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                R.animator.view_appear_exit).replace(R.id.container1, new ImageFragment()).addToBackStack(null)
                .commit();

        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                R.animator.view_appear_exit).replace(R.id.container2, new QuoteFragment()).addToBackStack(null)
                .commit();
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

    @Override
    public void onBackPressed()
    {
        int n = myBackStack.size();
        if (n > 0)
        {
            quotationIdx = myBackStack.remove(n - 1);

            showingBack = false;
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                    R.animator.view_appear_exit).replace(R.id.container1, new ImageFragment()).commit();

            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                    R.animator.view_appear_exit).replace(R.id.container2, new QuoteFragment()).commit();
            return;
        } else if (toastBeforeExit)
        {
            toastBeforeExit = false;
            Toast.makeText(this, "Press Back once more to exit", Toast.LENGTH_LONG).show();
            return;
        }
        finish();
    }

    private void showNameListDialog()
    {
        // Initialize the dialog
        AlertDialog.Builder parameters = new AlertDialog.Builder(this);
        parameters.setCancelable(true).setTitle(R.string.menu_title).setItems(names,
                new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        myBackStack.add(quotationIdx); // idx of quote that *was* showing
                        quotationIdx = which; // new Idx
                        showingBack = false;

                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                                R.animator.view_appear_exit).replace(R.id.container1, new ImageFragment()).commit();

                        getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_appear_enter,
                                R.animator.view_appear_exit).replace(R.id.container2, new QuoteFragment()).commit();
                    }
                }).show();
    }

    private void flipCard()
    {
        /*
         * Create and commit a new fragment transaction to replace the current fragment. The
		 * transition includes a "card flip" animation.
		 */

        if (showingBack)
        {
            // getFragmentManager().popBackStack();
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
                    R.animator.view_flip_exit).replace(R.id.container1, new ImageFragment()).commit();
        } else
        {
            getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
                    R.animator.view_flip_exit).replace(R.id.container1, new BiographyFragment()).commit();
        }
        showingBack = !showingBack; // toggle showing back/front
    }

    /*
     * We also want to save and restore the Quotation array. Since this is an Object, we are using
     * the method described in the API Guide (Topic: Retaining an Object During a Configuration
     * Change). See...
     *
     * http://developer.android.com/guide/topics/resources/runtime-changes.html
     *
     * See, also, the discussion and code in Demo Ink.
     */
    @Override
    public Object onRetainNonConfigurationInstance()
    {
        return quotation;
    }

    // restore state variables after a screen rotation
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
        quotation = (Quotation[])getLastNonConfigurationInstance(); // see onRetainConfigurationInstance
        quotationIdx = savedInstanceState.getInt(QUOTE_INDEX_KEY);
        showingBack = savedInstanceState.getBoolean(SHOWING_BACK_KEY);
        myBackStack = savedInstanceState.getIntegerArrayList(BACK_STACK_KEY);
        shuffled = savedInstanceState.getIntArray(SHUFFLED_ARRAY_KEY);
        randomIdx = savedInstanceState.getInt(RANDOM_INDEX_KEY);
        names = savedInstanceState.getStringArray(NAMES_KEY);
        allowHints = savedInstanceState.getBoolean(ALLOW_HINTS_KEY);
        vibrateOnWrong = savedInstanceState.getBoolean(VIBRATE_ON_WRONG_KEY);
        audio = savedInstanceState.getBoolean(AUDIO_KEY);
        numberOfQuestions = savedInstanceState.getInt(NUMBER_OF_QUESTIONS_KEY);
    }

    // save state variables in the event of a screen rotation
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState)
    {
        savedInstanceState.putInt(QUOTE_INDEX_KEY, quotationIdx);
        savedInstanceState.putBoolean(SHOWING_BACK_KEY, showingBack);
        savedInstanceState.putIntegerArrayList(BACK_STACK_KEY, myBackStack);
        savedInstanceState.putIntArray(SHUFFLED_ARRAY_KEY, shuffled);
        savedInstanceState.putInt(RANDOM_INDEX_KEY, randomIdx);
        savedInstanceState.putStringArray(NAMES_KEY, names);
        savedInstanceState.putBoolean(ALLOW_HINTS_KEY, allowHints);
        savedInstanceState.putBoolean(VIBRATE_ON_WRONG_KEY, vibrateOnWrong);
        savedInstanceState.putBoolean(AUDIO_KEY, audio);
        savedInstanceState.putInt(NUMBER_OF_QUESTIONS_KEY, numberOfQuestions);
        super.onSaveInstanceState(savedInstanceState);
    }

    // ====================
    // inner classes at end
    // ====================

    // ==================================================================================================
    // A fragment representing the front of the card (select image accordingly)
    public static class ImageFragment extends Fragment
    {
        ImageView frontView;

        public ImageFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            frontView = (ImageView)inflater.inflate(R.layout.famous_person, container, false);
            frontView.setImageResource(quotation[quotationIdx].imageId);
            return frontView;
        }
    }

    // ==================================================================================================
    // A fragment representing the back of the card (select text and background color accordingly)
    public static class QuoteFragment extends Fragment
    {
        View backView;
        TextView titleView, textView;

        public QuoteFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            backView = inflater.inflate(R.layout.quote, container, false);
            titleView = (TextView)backView.findViewById(R.id.text1_title);
            textView = (TextView)backView.findViewById(R.id.text2_title);
            titleView.setText(quotation[quotationIdx].quote);
            textView.setText(String.format("- %s",quotation[quotationIdx].famousPerson));
            return backView;
        }
    }

    // ==================================================================================================
    // A fragment representing the back of the card (select text and background color accordingly)
    public static class BiographyFragment extends Fragment // implements View.OnTouchListener
    {
        View backView;
        TextView titleView;
        TextView biographyView;

        public BiographyFragment()
        {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            backView = inflater.inflate(R.layout.biography, container, false);
            titleView = (TextView)backView.findViewById(R.id.bio_title);
            biographyView = (TextView)backView.findViewById(R.id.bio_text);
            titleView.setText(String.format("%s %s", quotation[quotationIdx].famousPerson, quotation[quotationIdx]
                    .dates));
            biographyView.setText(quotation[quotationIdx].biography);
            return backView;
        }
    }

    // ==================================================================================================
    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {
        @Override
        public boolean onSingleTapUp(MotionEvent me)
        {
            randomQuotation(); // tap to select a new (random) quotation
            return true;
        }

        @Override
        public void onLongPress(MotionEvent me)
        {
            vib.vibrate(50);
            showNameListDialog(); // pop up a list of names
        }

        @Override
        public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
        {
            flipCard(); // flip the image/bio card
            return true;
        }
    }
}
