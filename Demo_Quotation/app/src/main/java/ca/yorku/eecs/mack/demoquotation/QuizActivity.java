package ca.yorku.eecs.mack.demoquotation;

import java.util.Locale;
import java.util.Random;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/*
 * The activity to implement the quiz.  
 */

public class QuizActivity extends Activity implements View.OnTouchListener, ResultsDialog.OnResultsDialogClickListener
{
	//private final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	// keys for get/put methods
	private final static String QUESTION_INDEX_KEY = "question_index";
	private final static String START_TIME_KEY = "start_time";
	private final static String FIRST_ANSWER_FLAG_KEY = "first_answer_flag";
	private final static String SHOWING_BACK_KEY = "showing_back";
	
	// the following are public because they are also used in ResultsDialog
	public final static String NUMBER_CORRECT_KEY = "number_correct"; 
	public final static String NUMBER_INCORRECT_KEY = "number_incorrect"; 																			
	public final static String NUMBER_OF_HINTS_KEY = "number_of_hints"; 																			
	public final static String ELAPSED_TIME_KEY = "elapsed_time"; 
	
	private final static int NUMBER_OF_ANSWERS = 5; // ... for each question in the quiz

	long startTime, elapsedTime;
	int numberCorrect, numberIncorrect, numberOfHints;
	boolean firstAnswerFlag, showingBack;
	String timeString;

	GestureDetector gestureDetector; // use Android's GestureDetector for touch gestures
	FrameLayout bottomView; // we need these to attach the touch listeners
	Vibrator vib;
	MediaPlayer click, blip, miss;

	/*
	 * These variables are static because they are referenced in the Fragments used to create the UI
	 * for the quiz. The Fragments themselves are static, as required. See...
	 * 
	 * http://developer.android.com/guide/components/fragments.html
	 */
	static int numberOfQuestions; // a Setting passed in via onCreate bundle
	static boolean allowHints, vibrateOnWrong, audio; // Setting(passed in via onCreate bundle
	static Quotation[] quotation;
	static Question[] q;
	static int questionIdx;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_quiz);

		bottomView = (FrameLayout)findViewById(R.id.quiz_container2);
		bottomView.setOnTouchListener(this);
		gestureDetector = new GestureDetector(this.getBaseContext(), new MyGestureListener());

		if (savedInstanceState == null) // the activity is being created for the 1st time
		{
			// data passed from the calling activity in startActivityForResult
			Bundle b = getIntent().getExtras();
			numberOfQuestions = b.getInt(DemoQuotationActivity.QUIZ_LENGTH_KEY, 1);
			allowHints = b.getBoolean(DemoQuotationActivity.QUIZ_HINTS_KEY, true);
			vibrateOnWrong = b.getBoolean(DemoQuotationActivity.QUIZ_VIBRATE_KEY, false);
			audio = b.getBoolean(DemoQuotationActivity.QUIZ_AUDIO_KEY, false);

			// init vibrator (used for incorrect answer, if enabled)
			if (vibrateOnWrong)
				vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);

			/*
			 * Initializing the MediaPlayer object is simple enough (see below). However, calls to
			 * "start" sometimes have no effect -- no audio is heard. This is probably because the
			 * MediaPlayer is in a state that precludes (re)starting the audio. Unfortunately, the
			 * API, API Guides, and StackOverflow provide very little insight.
			 * 
			 * The fix seems to be creating a fresh instance of the MediaPlayer immediately before
			 * calling start. This works fine, so the following lines are commented-out. They remain
			 * simply to draw attention to this discussion.
			 */
			// click = MediaPlayer.create(getBaseContext(), R.raw.click);
			// blip = MediaPlayer.create(getBaseContext(), R.raw.blip);
			// miss = MediaPlayer.create(getBaseContext(), R.raw.miss);

			// load the Quotations array
			String[] quotationResourceArray = getResources().getStringArray(R.array.quotations);
			quotation = new Quotation[quotationResourceArray.length];

			// load the quotations info into an array of Quotation objects
			for (int i = 0; i < quotation.length; ++i)
			{
				String[] s = quotationResourceArray[i].split("#");
				quotation[i] = new Quotation(s[0], s[1], s[2], s[3], 0); // NOTE: imageId not needed
			}

			/*
			 * Creating the questions for the quiz is two-step process:
			 * 
			 * First, we create an array of the indices of the quotations to use for the quiz
			 * questions. This is a bit tricky because each question in the quiz must be unique.
			 */
			int[] temp; // an array of unique indices for the questions in the quiz
			do
			{
				temp = randomArray(numberOfQuestions, quotation.length);
			} while (repeats(temp)); // Note: repeats(temp) will return false most of the time

			/*
			 * Second, now that we've got the indices of the quotations for the questions in the
			 * quiz (see above), build the array of questions. Each entry in the array is an
			 * instance of Question (which holds all the information necessary to form a question).
			 * Consult the Question class source code for further details.
			 */
			q = new Question[numberOfQuestions];
			for (int i = 0; i < q.length; ++i)
				q[i] = new Question(temp[i], quotation.length, NUMBER_OF_ANSWERS);

			startTime = System.nanoTime(); // the time when the quiz started

			/*
			 * The following initializations are not necessary, since the values are the default
			 * values. We're including this mostly as reminder of the variables that we need to
			 * think about when there is a configuration change due to a screen rotation. See
			 * onSaveInstanceState and onRestoreInstanceState.
			 */
			// questionCount = 0;
			questionIdx = 0;
			elapsedTime = 0;
			numberCorrect = 0;
			numberIncorrect = 0;
			numberOfHints = 0;
			firstAnswerFlag = false;
			showingBack = false;
			showingBack = false;
			questionIdx = 0;

			// create the UI for the quiz
			getFragmentManager().beginTransaction().add(R.id.quiz_container2, new QuizQuoteFragment()).commit();
			getFragmentManager().beginTransaction().add(R.id.quiz_container1, new QuizAnswersFragment()).commit();
		}
	}

	// return an array of random integers in the specified range
	private int[] randomArray(int arraySize, int range)
	{
		int[] array = new int[arraySize];
		Random r = new Random();
		for (int i = 0; i < array.length; ++i)
			array[i] = r.nextInt(range);
		return array;
	}

	// return true if there are any repeated values in an array
	private boolean repeats(int[] array)
	{
		for (int i = 0; i < array.length; ++i)
			for (int j = i + 1; j < array.length; ++j)
				if (array[i] == array[j])
					return true;
		return false;
	}

	@Override
	public void onStop()
	{
		/*
		 * Release the audio resources. We do this in onStop, as suggested in the MediaPlayer API
		 * Guide. See...
		 * 
		 * http://developer.android.com/guide/topics/media/mediaplayer.html#releaseplayer
		 */
		if (click != null)
		{
			click.release();
			click = null;
		}
		if (blip != null)
		{

			blip.release();
			blip = null;
		}
		if (miss != null)
		{
			miss.release();
			miss = null;
		}
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent me)
	{
		// let the gesture detector process the touch event (see MyGestureListener below)
		gestureDetector.onTouchEvent(me);
		return true;
	}

	/*
	 * The user's answer is processed here. The appropriate dialog is popped up depending on whether
	 * the answer is correct or incorrect.
	 */
	public void buttonClick(View v)
	{
		String correctAnswer = quotation[q[questionIdx].quoteIdx].famousPerson;

		if (((Button)v).getText().equals(correctAnswer)) // correct answer
		{
			if (audio)
			{
				// MediaPlayer instance created here (see comment in onCreate)
				click = MediaPlayer.create(getBaseContext(), R.raw.click);
				click.start();
			}
			if (!firstAnswerFlag)
			{
				++numberCorrect;
				firstAnswerFlag = true;
			}
			showCorrectDialog();

		} else
		// wrong answer
		{
			if (audio)
			{
				// MediaPlayer instance created here (see comment in onCreate)
				miss = MediaPlayer.create(getBaseContext(), R.raw.miss);
				miss.start();
			}
			if (vibrateOnWrong)
				vib.vibrate(50);
			if (!firstAnswerFlag)
			{
				++numberIncorrect;
				firstAnswerFlag = true;
			}
			showIncorrectDialog();
		}
	}

	/*
	 * Get the next question for the quiz. Before doing this, increment the question count and check
	 * if we are done. If we are done, compute the elapsed time, output some audio (if enabled), and
	 * pop up a results dialog. The user's response to the results dialog will terminate the quiz
	 * activity and pass control back to the main activity.
	 */
	private void nextQuestion()
	{
		++questionIdx;
		if (questionIdx == numberOfQuestions)
		{
			// The quiz is done! Stop the clock (figuratively speaking) and build the time string
			elapsedTime = System.nanoTime() - startTime;
			timeString = String.format(Locale.getDefault(), "%1.1f sec", (elapsedTime / 1000000000f));

			if (audio) // as per Settings in Options Menu
			{
				// MediaPlayer instance created here (see comment in onCreate)
				blip = MediaPlayer.create(getBaseContext(), R.raw.blip);
				blip.start();
			}
			showResultsDialog(); // ... and then finish (see onClick in showResultsDialog)

		} else
		{
			firstAnswerFlag = false;
			getFragmentManager().beginTransaction().replace(R.id.quiz_container2, new QuizQuoteFragment()).commit();
			getFragmentManager().beginTransaction().replace(R.id.quiz_container1, new QuizAnswersFragment()).commit();
		}
	}

	/*
	 * The dialog that pops up to show results at the end of a quiz.
	 * 
	 * This dialog is a bit more involved because we want to pass data in to the dialog to display.
	 * We're using a custom layout (res/layout/results_dialog.xml) and defining the dialog as a
	 * DialogFragment in a separate file (ResultsDialog.java). Consult for details. The approach
	 * here closely follows the recommendations in the DialogFragment API. See...
	 * 
	 * http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
	 */
	private void showResultsDialog()
	{
		// Create an instance of the dialog fragment (passing in the data we want to show)
		DialogFragment df = ResultsDialog.newInstance(numberCorrect, numberIncorrect, numberOfHints, timeString);

		// show the dialog
		df.show(getFragmentManager(), "ResultsDialogFragment");
	}

	@Override
	public void onResultsDialogClick(DialogFragment dialog)
	{
		// The user tapped the results dialog Continue button (this quiz is done)
		this.finish();
	}

	// The dialog that pops up if the user taps the correct answer
	private void showCorrectDialog()
	{
		// Initialize the dialog
		AlertDialog.Builder parameters = new AlertDialog.Builder(this);
		parameters.setCancelable(false).setTitle(R.string.correct_title).setNeutralButton(R.string.continue_string,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel(); // close this dialog
						nextQuestion();
					}
				}).show();
	}

	// The dialog that pops up if the user guesses incorrectly
	private void showIncorrectDialog()
	{
		// Initialize the dialog
		AlertDialog.Builder parameters = new AlertDialog.Builder(this);
		parameters.setCancelable(false).setTitle(R.string.incorrect_title).setNeutralButton(R.string.try_again,
				new DialogInterface.OnClickListener()
				{
					public void onClick(DialogInterface dialog, int id)
					{
						dialog.cancel(); // close this dialog
					}
				}).show();
	}

	/*
	 * We also want to save and restore the Questions array. Since this is an Object, we are using
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
		return q;
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);
		q = (Question[])getLastNonConfigurationInstance(); // see onRetainConfigurationInstance
		questionIdx = savedInstanceState.getInt(QUESTION_INDEX_KEY);
		elapsedTime = savedInstanceState.getLong(ELAPSED_TIME_KEY);
		startTime = savedInstanceState.getLong(START_TIME_KEY);
		numberCorrect = savedInstanceState.getInt(NUMBER_CORRECT_KEY);
		numberIncorrect = savedInstanceState.getInt(NUMBER_INCORRECT_KEY);
		numberOfHints = savedInstanceState.getInt(NUMBER_OF_HINTS_KEY);
		firstAnswerFlag = savedInstanceState.getBoolean(FIRST_ANSWER_FLAG_KEY);
		showingBack = savedInstanceState.getBoolean(SHOWING_BACK_KEY);
		numberOfQuestions = savedInstanceState.getInt(DemoQuotationActivity.QUIZ_LENGTH_KEY);
		allowHints = savedInstanceState.getBoolean(DemoQuotationActivity.QUIZ_HINTS_KEY, true);
		vibrateOnWrong = savedInstanceState.getBoolean(DemoQuotationActivity.QUIZ_VIBRATE_KEY, false);
		audio = savedInstanceState.getBoolean(DemoQuotationActivity.QUIZ_AUDIO_KEY, false);

		if (vibrateOnWrong)
			vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE); // for long-press

		// put the fragment UIs in the activity's content
		if (showingBack)
			getFragmentManager().beginTransaction().replace(R.id.quiz_container2, new QuizQuoteHintFragment()).commit();
		else
			getFragmentManager().beginTransaction().replace(R.id.quiz_container2, new QuizQuoteFragment()).commit();

		getFragmentManager().beginTransaction().replace(R.id.quiz_container1, new QuizAnswersFragment()).commit();
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState)
	{
		savedInstanceState.putInt(QUESTION_INDEX_KEY, questionIdx);
		savedInstanceState.putLong(ELAPSED_TIME_KEY, elapsedTime);
		savedInstanceState.putLong(START_TIME_KEY, startTime);
		savedInstanceState.putInt(NUMBER_CORRECT_KEY, numberCorrect);
		savedInstanceState.putInt(NUMBER_INCORRECT_KEY, numberIncorrect);
		savedInstanceState.putInt(NUMBER_OF_HINTS_KEY, numberOfHints);
		savedInstanceState.putBoolean(FIRST_ANSWER_FLAG_KEY, firstAnswerFlag);
		savedInstanceState.putBoolean(SHOWING_BACK_KEY, showingBack);
		savedInstanceState.putInt(DemoQuotationActivity.QUIZ_LENGTH_KEY, numberOfQuestions);
		savedInstanceState.putBoolean(DemoQuotationActivity.QUIZ_HINTS_KEY, allowHints);
		savedInstanceState.putBoolean(DemoQuotationActivity.QUIZ_VIBRATE_KEY, vibrateOnWrong);
		savedInstanceState.putBoolean(DemoQuotationActivity.QUIZ_AUDIO_KEY, audio);
		super.onSaveInstanceState(savedInstanceState);
	}

	private void flipCard()
	{
		/*
		 * Create and commit a new fragment transaction to replace the current fragment. The
		 * transition includes a "card flip" animation.
		 */

		if (showingBack)
		{
			getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
					R.animator.view_flip_exit).replace(R.id.quiz_container2, new QuizQuoteFragment()).commit();
		} else
		{
			getFragmentManager().beginTransaction().setCustomAnimations(R.animator.view_flip_enter,
					R.animator.view_flip_exit).replace(R.id.quiz_container2, new QuizQuoteHintFragment()).commit();
		}
		// toggle showing back/front
		showingBack = !showingBack;
	}

	// ==================================================================================================
	public static class QuizAnswersFragment extends Fragment
	{
		View quizStatusView;
		Button answer1View, answer2View, answer3View, answer4View, answer5View;

		public QuizAnswersFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			quizStatusView = inflater.inflate(R.layout.quiz_answer, container, false);
			answer1View = (Button)quizStatusView.findViewById(R.id.quiz_answer_1);
			answer2View = (Button)quizStatusView.findViewById(R.id.quiz_answer_2);
			answer3View = (Button)quizStatusView.findViewById(R.id.quiz_answer_3);
			answer4View = (Button)quizStatusView.findViewById(R.id.quiz_answer_4);
			answer5View = (Button)quizStatusView.findViewById(R.id.quiz_answer_5);

			/*
			 * This adjustment is necessary in case the screen is rotated WHILE THE RESULTS DIALOG
			 * IS SHOWING. Without this, the app crashes with an array index out of bounds
			 * exception.
			 */
			if (questionIdx == numberOfQuestions)
				--questionIdx;

			answer1View.setText(quotation[q[questionIdx].answerArray[0]].famousPerson);
			answer2View.setText(quotation[q[questionIdx].answerArray[1]].famousPerson);
			answer3View.setText(quotation[q[questionIdx].answerArray[2]].famousPerson);
			answer4View.setText(quotation[q[questionIdx].answerArray[3]].famousPerson);
			answer5View.setText(quotation[q[questionIdx].answerArray[4]].famousPerson);

			return quizStatusView;
		}
	}

	// ==================================================================================================
	public static class QuizQuoteFragment extends Fragment
	{
		View quizQuestionView;
		TextView quoteView, questionView, swipeForHintView;

		public QuizQuoteFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			quizQuestionView = inflater.inflate(R.layout.quiz_question, container, false);
			quoteView = (TextView)quizQuestionView.findViewById(R.id.quiz_quote);
			questionView = (TextView)quizQuestionView.findViewById(R.id.quiz_question);
			swipeForHintView = (TextView)quizQuestionView.findViewById(R.id.quiz_swipe_for_hint);

			/*
			 * This adjustment is necessary in case the screen is rotated WHILE THE RESULTS DIALOG
			 * IS SHOWING. Without this, the app crashes with an array index out of bounds
			 * exception.
			 */
			if (questionIdx == numberOfQuestions)
				--questionIdx;

			quoteView.setText(quotation[q[questionIdx].quoteIdx].quote);
			questionView.setText(R.string.quiz_question);
			if (allowHints)
				swipeForHintView.setText(R.string.quiz_question_swipe_for_hint);
			else
				swipeForHintView.setText(R.string.quiz_question_no_hints);
			return quizQuestionView;
		}
	}

	// ==================================================================================================
	public static class QuizQuoteHintFragment extends Fragment
	{
		View quizQuestionHintView;
		TextView hintView;

		public QuizQuoteHintFragment()
		{
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			quizQuestionHintView = inflater.inflate(R.layout.quiz_question_hint, container, false);
			hintView = (TextView)quizQuestionHintView.findViewById(R.id.quiz_question_hint_title);

			/*
			 * This adjustment is necessary in case the screen is rotated WHILE THE RESULTS DIALOG
			 * IS SHOWING. Without this, the app crashes with an array index out of bounds
			 * exception.
			 */
			if (questionIdx == numberOfQuestions)
				--questionIdx;

			String[] bio = quotation[q[questionIdx].quoteIdx].biography.split(" ");
			String hint = bio[0] + " " + bio[1] + " " + bio[2]; // use 1st three words as hint
			hintView.setText(hint);

			return quizQuestionHintView;
		}
	}

	// ==================================================================================================
	private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
	{
		@Override
		public boolean onSingleTapUp(MotionEvent me)
		{
			// nothing implemented (yet!)
			return true;
		}

		@Override
		public void onLongPress(MotionEvent me)
		{
			// nothing implemented (yet!)
		}

		@Override
		public boolean onFling(MotionEvent me1, MotionEvent me2, float velocityX, float velocityY)
		{
			// flip the question over to show a hint (if this option is enabled in the settings)
			if (allowHints)
			{
				++numberOfHints;
				flipCard();
			}
			return true;
		}
	}
}
