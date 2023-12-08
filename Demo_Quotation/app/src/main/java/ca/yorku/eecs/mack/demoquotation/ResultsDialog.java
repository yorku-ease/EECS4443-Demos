package ca.yorku.eecs.mack.demoquotation;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/*
 * For the rationale in defining a dialog using a dedicated class, see "Dialogs" in the Android API
 * Guides:
 * 
 * http://developer.android.com/guide/topics/ui/dialogs.html
 * 
 * In particular, see the subsection "Passing Events Back to the Dialog's Host":
 * 
 * http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
 * 
 * See as well the section "Alert Dialog" in the DialogFragment API:
 * 
 * http://developer.android.com/reference/android/app/DialogFragment.html#AlertDialog
 * 
 */

public class ResultsDialog extends DialogFragment
{
	@SuppressWarnings("unused")
	private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface,
	 * and indicate such by appending "implements ResultsDialog.OnResultsDialogClickListener" to its
	 * signature. In doing so the host activity will receive the button click callback. The method
	 * passes the DialogFragment in case the host needs to query it.
	 */
	public interface OnResultsDialogClickListener
	{
		void onResultsDialogClick(DialogFragment dialog);
	}

	// Use this instance of the interface to deliver action events
	OnResultsDialogClickListener myListener;

	public static ResultsDialog newInstance(int numberCorrect, int numberIncorrect, int numberOfHints, String timeString)
	{
		// instantiate the results dialog using the no-arg constructor, as required
		ResultsDialog rd = new ResultsDialog();

		// bundle up the data we want pass in to the dialog
		Bundle b = new Bundle();
		b.putInt(QuizActivity.NUMBER_CORRECT_KEY, numberCorrect);
		b.putInt(QuizActivity.NUMBER_INCORRECT_KEY, numberIncorrect);
		b.putInt(QuizActivity.NUMBER_OF_HINTS_KEY, numberOfHints);
		b.putString(QuizActivity.ELAPSED_TIME_KEY, timeString);

		// set the bundle as the "arguments" for the dialog
		rd.setArguments(b);

		// return the results dialog to the activity that called newInstance (see QuizActivity)
		return rd;
	}

	@Override
	public Dialog onCreateDialog(Bundle content)
	{
		// get the data from the "arguments" bundle
		int numberCorrect = getArguments().getInt(QuizActivity.NUMBER_CORRECT_KEY);
		int numberIncorrect = getArguments().getInt(QuizActivity.NUMBER_INCORRECT_KEY);
		int numberOfHints = getArguments().getInt(QuizActivity.NUMBER_OF_HINTS_KEY);
		String timeString = getArguments().getString(QuizActivity.ELAPSED_TIME_KEY);

		// get the layout inflater
		LayoutInflater inflater = getActivity().getLayoutInflater();

		// get the top-level view group
		View view = inflater.inflate(R.layout.results_dialog, null);

		// get the views and give them the content
		((TextView)view.findViewById(R.id.correct_value)).setText(String.format("%d", numberCorrect));
		((TextView)view.findViewById(R.id.incorrect_value)).setText(String.format("%d", numberIncorrect));
		((TextView)view.findViewById(R.id.number_of_hints_value)).setText(String.format("%d", numberOfHints));
		((TextView)view.findViewById(R.id.elapsed_time_value)).setText(timeString);

		// build the dialog and set up the button click handler
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		// set the top-level view group as the layout for the dialog
		builder.setView(view);

		// configure the button
		builder.setNeutralButton(R.string.continue_string, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// The Callback! (executes in host activity)
				myListener.onResultsDialogClick(ResultsDialog.this);
			}
		});

		// give the dialog a title
		builder.setTitle(R.string.results_title);

		// create the dialog (with arguments supplied above)
		return builder.create();
	}

	// Override the Fragment.onAttach() method to get a reference to the OnResultsDialogClickListener
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