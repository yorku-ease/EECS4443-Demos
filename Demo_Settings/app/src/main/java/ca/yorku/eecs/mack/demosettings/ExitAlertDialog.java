package ca.yorku.eecs.mack.demosettings;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * For the rationale in defining a dialog using a dedicated class, see "Dialogs" in the Android API Guides:
 *
 * http://developer.android.com/guide/topics/ui/dialogs.html
 *
 * In particular, see the subsection "Passing Events Back to the Dialog's Host":
 *
 * http://developer.android.com/guide/topics/ui/dialogs.html#PassingEvents
 */

public class ExitAlertDialog extends DialogFragment
{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Send the positive button event back to the host activity
                myListener.onDialogPositiveClick(ExitAlertDialog.this);
            }
        }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int id)
            {
                // Send the negative button event back to the host activity
                myListener.onDialogNegativeClick(ExitAlertDialog.this);
            }
        }).setMessage(R.string.exit_alert_message).setTitle(R.string.exit_alert_title);
        return builder.create();
    }

    /*
     * The activity that creates an instance of this dialog fragment must implement this interface
     * in order to receive event callbacks. Each method passes the DialogFragment in case the host
     * needs to query it.
     */
    interface ExitAlertListener
    {
        void onDialogPositiveClick(DialogFragment dialog);

        void onDialogNegativeClick(DialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    ExitAlertListener myListener;

    // Override the Fragment.onAttach() method to instantiate the ExitAlertListener
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        // Verify that the host activity implements the callback interface
        try
        {
            // Instantiate the NoticeDialogListener so we can send events to the host
            myListener = (ExitAlertListener)activity;
        } catch (ClassCastException e)
        {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
