package ca.yorku.eecs.mack.democamera;

import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;

@SuppressWarnings("unused")
public class ImageListViewerActivity extends ListActivity
{
	final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

	final static String IMAGE_INDEX_KEY = DemoCameraActivity.IMAGE_INDEX_KEY;
	final static String DIRECTORY_KEY = DemoCameraActivity.DIRECTORY_KEY;
	final static String IMAGE_FILENAMES_KEY = DemoCameraActivity.IMAGE_FILENAMES_KEY;
	
	String[] imageFilenames;
	String directory;
	
    // Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listviewlayout); 
        init();              
    }
    
    private void init()
    {    	
		// data passed from the calling activity in startActivityForResult (see DemoCameraIntentActivity)
		Bundle b = getIntent().getExtras();
		imageFilenames = b.getStringArray(IMAGE_FILENAMES_KEY);
		directory = b.getString(DIRECTORY_KEY);
		
		// determine display width (will be used to scale images)
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int displayWidth = dm.widthPixels;
		
		ImageAdapter adapter = new ImageAdapter(imageFilenames, directory, displayWidth);
    	setListAdapter(adapter);
    }
    
	@Override
	public void onBackPressed()
	{
		this.setResult(Activity.RESULT_OK);
		super.onBackPressed();
	}
}
