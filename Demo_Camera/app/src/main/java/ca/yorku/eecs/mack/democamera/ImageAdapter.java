package ca.yorku.eecs.mack.democamera;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

class ImageAdapter extends BaseAdapter
{
    private final String[] fileNames;
	private String directory;
	private int displayWidth;
	
    private final ImageDownloader imageDownloader = new ImageDownloader();
    
    ImageAdapter(String[] fileNamesArg, String directoryArg, int displayWidthArg)
    {
    	fileNames = fileNamesArg;
    	directory = directoryArg;
    	displayWidth = displayWidthArg;
    }
   
    @Override
    public int getCount() 
    {
        return fileNames.length;
    }

    @Override
    public String getItem(int position) 
    {
        return fileNames[position];
    }

    @Override
    public long getItemId(int position) 
    {
        return fileNames[position].hashCode();
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) 
    {
        if (view == null) 
        {
            view = new ImageView(parent.getContext());
            view.setBackgroundColor(Color.LTGRAY);
            ((ImageView)view).setScaleType(ImageView.ScaleType.CENTER);
            view.setPadding(6, 6, 6, 6);
        }

        String path = directory + File.separator + fileNames[position];
        imageDownloader.download(path, (ImageView)view, displayWidth);       
        return view;
    }
}
