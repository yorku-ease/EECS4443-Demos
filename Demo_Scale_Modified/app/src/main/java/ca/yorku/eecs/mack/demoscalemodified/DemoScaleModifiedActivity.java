package ca.yorku.eecs.mack.demoscalemodified;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;

/**
 * DemoScaleModified - DemoScale with modifications
 * 
 * The modification is to implement double-tap gestures. A double-tap scales the image by a factor
 * 3. Alternate double-taps toggle between larger (X3) and smaller (/3).
 * <p>
 * 
 * The modifications are made in PaintPanel. Check there for details.
 * <p>
 * 
 * @author (c) Scott MacKenzie, 2011-2017
 * 
 */

public class DemoScaleModifiedActivity extends Activity
{
	PaintPanel imagePanel; // the panel in which to paint the image
	StatusPanel statusPanel; // a status panel the display the image coordinates, size, and scale

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); // hide title bar
		setContentView(R.layout.main);
		init();
	}

	private void init()
	{
		imagePanel = (PaintPanel)findViewById(R.id.paintpanel);
		statusPanel = (StatusPanel)findViewById(R.id.statuspanel);

		imagePanel.setStatusPanel(statusPanel); // provide a reference to the status panel
		
		/*
		 * Determine the pixel density of the display (used to scale fonts so they are consistent in
		 * size on different displays).
		 */
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	/** Called when the "Reset" button is pressed. */
	public void clickReset(View view)
	{
		imagePanel.xPosition = 10;
		imagePanel.yPosition = 10;
		imagePanel.scaleFactor = 1f;
		imagePanel.invalidate();
	}
}
