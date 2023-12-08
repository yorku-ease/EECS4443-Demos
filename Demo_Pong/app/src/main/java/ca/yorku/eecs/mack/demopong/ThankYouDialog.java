package ca.yorku.eecs.mack.demopong;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.ImageView;
 
/**
 * This is a simple custom DialogFragment, as per the example in Demo Settings. The only minor point
 * of customization is that we are using an image for the title, rather than text. See the
 * implementation of onCreateDialog below for details.
 */

public class ThankYouDialog extends DialogFragment
{
	//private static final String MYDEBUG = "MYDEBUG"; // for Log.i messages

	/*
	 * The activity that creates an instance of this dialog fragment must implement this interface
	 * and append "implements ThankYouDialog.OnThankYouDialogClickListener" to its signature. In
	 * doing so, the host activity will receive the button click callback. The method does *not*
	 * receive any arguments, because none is needed. This is a slight simplification for the
	 * implementation in DemoSettings (see ExitAlertDialog) or the example code in the Dialogs API
	 * Guide. See...
	 * 
	 * http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
	 */
	public interface OnThankYouDialogClickListener
	{
		void onThankYouDialogClick(); // implemented in host activity
	}

	// Use this instance of the interface to deliver action events
	OnThankYouDialogClickListener myListener;

	public static ThankYouDialog newInstance()
	{
		// instantiate and return the results dialog using the no-arg constructor, as required
		return new ThankYouDialog();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		// an image will be used as the "title" of the dialog
		ImageView thankYouImage = new ImageView((this.getActivity().getApplicationContext()));
		thankYouImage.setImageResource(R.drawable.thank_you);
		thankYouImage.setPadding(20, 20, 20, 20);

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		/*
		 * We're using setCustomTitle to create a title that contains only an image (thank_you.png).
		 * It seems that setIcon cannot be used unless setTitle is also used (with a non-empty text
		 * string). But, we want just the image in the title, so we're using setCustomTitle, which
		 * requires a View as an argument.
		 */
		builder.setCustomTitle(thankYouImage);

		/*
		 * This implementation of setPositiveButton is slightly simplified from the code used in
		 * ExitAlertDialog in DemoSettings or in the example code in the Dialogs API Guide. See...
		 * 
		 * http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
		 * 
		 * Here, the listener method is defined to *not* include any arguments that are send back to
		 * the host activity. In the host, the callback simply terminates the app, so there isn't a
		 * need to include any arguments.
		 */
		builder.setPositiveButton(R.string.continue_string, new DialogInterface.OnClickListener()
		{
			public void onClick(DialogInterface dialog, int id)
			{
				// Send the positive button id back to the host activity
				myListener.onThankYouDialogClick();
			}
		});

		return builder.create();
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
		myListener.onThankYouDialogClick();
	}

	// Override Fragment.onAttach() to get a reference to the OnResultsDialogClickListener
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);

		// Verify that the host activity implements the callback interface
		try
		{
			// Get a reference to the OnResultsDialogClickListener so we can send events to the host
			myListener = (OnThankYouDialogClickListener)activity;

		} catch (ClassCastException e)
		{
			// The activity doesn't implement the interface, throw exception
			throw new ClassCastException(activity.toString() + " must implement OnResultsDialogClickListener");
		}
	}
}