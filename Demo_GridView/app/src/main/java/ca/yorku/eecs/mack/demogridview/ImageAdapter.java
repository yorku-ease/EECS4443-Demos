package ca.yorku.eecs.mack.demogridview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;

public class ImageAdapter extends BaseAdapter
{
    String[] filenames;
    File directory;
    ImageDownloader imageDownloader = new ImageDownloader();
    int columnWidth;

    private Context context;

    public ImageAdapter(Context c)
    {
        context = c;
    }

    public int getCount()
    {
        return filenames.length;
    }

    public Object getItem(int position)
    {
        return null;
    }

    public long getItemId(int position)
    {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ImageView imageView;
        if (convertView == null)
        {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(columnWidth, columnWidth));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else
        {
            imageView = (ImageView)convertView;
        }

        // download the image and associate it with the ImageView
        String path = directory + File.separator + filenames[position];
        imageDownloader.download(path, imageView, 250);
        return imageView;
    }

    // give the ImageAdapter the filenames array and the directory
    public void setFilenames(String[] filenamesArg, File directoryArg)
    {
        filenames = filenamesArg;
        directory = directoryArg;
    }

    public void setColumnWidth(int widthArg)
    {
        columnWidth = widthArg;
    }
}
