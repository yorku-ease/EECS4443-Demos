package ca.yorku.eecs.mack.demolistview2;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.io.File;

class ImageAdapter extends BaseAdapter
{

    private String[] fileNames;
    private String path;

    private final ImageDownloader imageDownloader = new ImageDownloader();

    ImageAdapter(String[] fileNamesArg, String pathArg)
    {
        fileNames = fileNamesArg;
        path = pathArg;
    }

    @Override
    public int getCount()
    {
        return fileNames.length;
    }

    @Override
    public String getItem(int position)
    {
        return "" + position;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        int thumbnailSize = 300;

        if (view == null)
        {
            view = new ImageView(parent.getContext());
            view.setPadding(6, 6, 6, 6);
            view.setBackgroundColor(Color.LTGRAY);
            view.setMinimumHeight(thumbnailSize);
        }

        // build the full path name of the image file ...
        String file = path + File.separator + fileNames[position];

        // ... and give it to the download method of ImageDownloader
        imageDownloader.download(file, (ImageView)view, thumbnailSize);
        return view;
    }
}
