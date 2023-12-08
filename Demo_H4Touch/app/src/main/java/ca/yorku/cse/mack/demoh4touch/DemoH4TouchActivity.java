package ca.yorku.cse.mack.demoh4touch;

import ca.yorku.cse.mack.demoh4touch.H4Keyboard.H4Listener;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

/**
 * DemoH4Writer
 * 
 * @author (c) Scott MacKenzie, 2011-2013
 * 
 */

public class DemoH4TouchActivity extends Activity implements OnClickListener, H4Listener
{
	private final long PULSE_DURATION = 10;
	private final int MARGIN_LEFT = 5;  // left margin (should match value in main.xml)
	
	private H4Keyboard h4k;
	private EditText transcribedTextField;
	private boolean vibrotactileFeedback, auditoryFeedback;
	private boolean hideLetters;
	private Vibrator vib;
	private MediaPlayer tick, miss;
	private String transcribedText;
	private CheckBox hideLettersCheckBox;
	private Button clearButton, exitButton;
	
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);  
        
		transcribedTextField = (EditText)findViewById(R.id.transcribedtextfield);
		transcribedText = "";
		transcribedTextField.setText(transcribedText);
						
		// prevent system's IME soft keyboard from popping up
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		h4k = (H4Keyboard)findViewById(R.id.h4keys);
		h4k.showHuffmanCodes = !hideLetters;
		
		// determine display width and height
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		float scalingFactor = dm.density;
		int width = dm.widthPixels - (int)(2f * MARGIN_LEFT * scalingFactor);
		h4k.setKeyboardSize(width, dm.heightPixels, scalingFactor);	
		
		hideLettersCheckBox = (CheckBox)findViewById(R.id.hideletterscheckbox);
		clearButton = (Button)findViewById(R.id.clearbutton);
		exitButton = (Button)findViewById(R.id.exitbutton);

		// attach listeners
		h4k.setH4Listener(this);	
		hideLettersCheckBox.setOnClickListener(this);
		clearButton.setOnClickListener(this);
		exitButton.setOnClickListener(this);
		
		// prevent soft keyboard from popping up by default (will still pop up if text field is tapped)
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		
		// initialize vibrotactile and auditory feedback objects
		vib = (Vibrator)getSystemService(Context.VIBRATOR_SERVICE);
		tick = MediaPlayer.create(getApplicationContext(), R.raw.click1);
		miss = MediaPlayer.create(getApplicationContext(), R.raw.miss);
		//enter = MediaPlayer.create(getApplicationContext(), R.raw.blip1);
		
		vibrotactileFeedback = true;
		auditoryFeedback = false; // not used in demo setup
    }
    
    public void onH4Code(H4Keyboard.H4Event h4e)
    {
    	String symbol = h4e.symbol;     	
    	
    	if (!symbol.equals(H4Keyboard.ENTER))
    	{
			// first, update the transcribed text field, as appropriate, and output the appropriate feedback
			if (symbol.equals(H4Keyboard.SPACE))
			{
				transcribedText += " "; 
		    	if (vibrotactileFeedback) vib.vibrate(PULSE_DURATION);
		    	if (auditoryFeedback) tick.start();
			}
			else if (symbol.equals(H4Keyboard.BACKSPACE))
			{
				if (transcribedText.length() >= 1)
				{
					transcribedText = transcribedText.substring(0, transcribedText.length() - 1);
			    	if (vibrotactileFeedback) vib.vibrate(PULSE_DURATION);
			    	if (auditoryFeedback) tick.start();
				}
				else
					if (auditoryFeedback) miss.start();
			} else
			{
				transcribedText += symbol;
		    	if (vibrotactileFeedback) vib.vibrate(PULSE_DURATION);
		    	if (auditoryFeedback) tick.start();
			}	
			transcribedTextField.setText(transcribedText);
			transcribedTextField.setSelection(transcribedText.length()); // move cursor to end
    	}
   	}
    
    public void onH4Keystroke() {} // don't need this information
    
	public void onClick(View v) 
	{
		if (v == hideLettersCheckBox)
		{
			h4k.showHuffmanCodes = !hideLettersCheckBox.isChecked();
			h4k.resetState();			
		}
		else if (v == clearButton)
		{
			transcribedText = "";
			transcribedTextField.setText(transcribedText);
			h4k.resetState();
		}
		else if (v == exitButton)
		{
		    this.finish();     // terminate	 			
		}
	}    
}