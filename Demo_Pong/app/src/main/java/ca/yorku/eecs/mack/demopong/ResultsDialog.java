package ca.yorku.eecs.mack.demopong;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

/**
 * This custom DialogFragment is a bit more complicated that the ones in earlier demo programs (e.g.,
 * Demo Settings).  Here, we are using a custom layout (defined in results_dialog.xml). Because of
 * this, we implement both onCreateDialog and onCreateView. However, there are some peculiarities in
 * using *both* onCreateDialog and onCreateView.  See comment for onCreateDialog.
 */

public class ResultsDialog extends DialogFragment
{
	//private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static String HITS_KEY = "hits";
	final static String MISSES_KEY = "misses";
	final static String LEVEL_KEY = "level";
	final static String NUMBER_OF_TRIALS_KEY = "number_of_trials";
	final static String GAME_STATE_KEY = "game_state";

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface
	 * and append "implements ResultsDialog.OnResultsDialogClickListener" to its signature. In doing
	 * so, the host activity will receive the button click callback. The method passes the
	 * DialogFragment ID in case the host needs to query it.
	 */
	public interface OnResultsDialogClickListener
	{
		// public void onResultsDialogClick(DialogFragment dialog); // implemented in host activity
		void onResultsDialogClick(int buttonId); // implemented in host activity
	}

	// Use this instance of the interface to deliver action events
	OnResultsDialogClickListener myListener;

	public static ResultsDialog newInstance(int hitsArg, int missesArg, int levelArg, int numberOfTrialsArg, int gameStateArg)
	{
		// instantiate the results dialog using the no-arg constructor, as required
		ResultsDialog rd = new ResultsDialog();

		// bundle up the data we want pass in to the dialog
		Bundle b = new Bundle();
		b.putInt(HITS_KEY, hitsArg);
		b.putInt(MISSES_KEY, missesArg);
		b.putInt(LEVEL_KEY, levelArg);
		b.putInt(NUMBER_OF_TRIALS_KEY, numberOfTrialsArg);
		b.putInt(GAME_STATE_KEY, gameStateArg);
		
		// set the bundle as the "arguments" for the dialog
		rd.setArguments(b);

		// return the results dialog to the activity that called newInstance (see DemoPongActivity)
		return rd;
	}

	/*
	 * There are some peculiarities in using *both* onCreateDialog and onCreateView when
	 * implementing a DialogFragment with a custom layout, as we are doing here. Unfortunately,
	 * following the recommendations in the DialogFragment API leads to a tricky exception:
	 * "requestFeature() must be called before adding content". StackOverflow eventually lead to a
	 * working solution. For discussion on the problem and some workarounds, see...
	 * 
	 * http://stackoverflow.com/questions/13257038/custom-layout-for-dialogfragment-oncreateview-vs-
	 * oncreatedialog
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		Dialog dialog = super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}

	// see comment for onCreateDialog
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		// get the data from the "arguments" bundle
		int level = getArguments().getInt(LEVEL_KEY);
		int numberOfTrials = getArguments().getInt(NUMBER_OF_TRIALS_KEY);
		int hits = getArguments().getInt(HITS_KEY);
		int misses = getArguments().getInt(MISSES_KEY);
		int gameState = getArguments().getInt(GAME_STATE_KEY);

		// get the top-level view group
		inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.results_dialog, container, false);
	
		// give the required data to the view that will display the results
		((ResultsView)view.findViewById(R.id.results_view)).setResults(level, numberOfTrials, hits, misses, gameState);
		
		// configure the Continue button
		Button continueButton = (Button)view.findViewById(R.id.continue_button);
		continueButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				// The callback! (executed in the host activity)
				myListener.onResultsDialogClick(DialogInterface.BUTTON_POSITIVE);
			}
		});

		return view;
	}

	/*
	 * If the user touches the screen outside the dialog, the dialog is dismissed. In this case,
	 * onCancel executes. See...
	 * 
	 * http://developer.android.com/guide/topics/ui/dialogs.html#DismissingADialog
	 * 
	 * If this happens (the user touches the screen outside the dialog), we invoke
	 * onResultsDialogDialogClick and pass the BUTTON_POSITIVE id. In essence, the cancel operation
	 * is the same as the user tapping the "Continue" button -- the game continues! See the source
	 * code for onResultsDialogClick in DemoPongActivity.java.
	 */
	@Override
	public void onCancel(DialogInterface dialog)
	{
		myListener.onResultsDialogClick(DialogInterface.BUTTON_POSITIVE);
	}

	// Override the Fragment.onAttach() method to get a reference to the
	// OnResultsDialogClickListener
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// Verify that the host activity implements the callback interface
		try
		{
			// Get a reference to the OnResultsDialogClickListener so we can send events to the host
			myListener = (OnResultsDialogClickListener)activity;

		} catch (ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement OnResultsDialogClickListener");
		}
	}
}