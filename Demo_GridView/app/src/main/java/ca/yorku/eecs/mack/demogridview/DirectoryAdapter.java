package ca.yorku.eecs.mack.demogridview;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.File;
import java.util.ArrayList;

/**
 * This code is more-or-less taken from the Android API Guide for GridView:
 *
 * http://developer.android.com/guide/topics/ui/layout/gridview.html
 */
public class DirectoryAdapter extends BaseAdapter
{
    private ArrayList<DirectoryInfo> directoryInfo;
    private ImageDownloader imageDownloader = new ImageDownloader();
    private int columnWidth;
    private Context context;

    DirectoryAdapter(Context c, ArrayList<DirectoryInfo> directoryInfoArg)
    {
        context = c;
        directoryInfo = directoryInfoArg;
    }

    @Override
    public int getCount()
    {
        return directoryInfo.size();
    }

    @Override
    public Object getItem(int position)
    {
        return null;
    }

    @Override
    public long getItemId(int position)
    {
        return 0;
    }

    /**
     * Create a new DirectoryImageView for each item referenced by the Adapter.
     *
     * Note: DirectoryImageView is a subclass of ImageView. The only difference is the addition of a semi-transparent
     * grey band across the bottom of the view. The band display the directory name and the number of JPG files within
     * the directory.
     *
     * Note: As scrolling takes place on the GridView, old views are recycled, if possible.  In other words, when a view
     * in the grid is scrolled off the display, that same view is used for the data/image of a view being scrolled onto
     * the display.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        DirectoryImageView directoryImageView;
        if (convertView == null)
        {
            // the view not being recycled; create a new ImageView and initialize some attributes
            directoryImageView = new DirectoryImageView(context);
            directoryImageView.setLayoutParams(new GridView.LayoutParams(columnWidth, columnWidth));
            directoryImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            directoryImageView.setSize(columnWidth, columnWidth);
        } else
        {
            // the view is being recycled
            directoryImageView = (DirectoryImageView)convertView;
        }

        /*
         * Download the image and associate it with the ImageView.
         *
         * Note:  The image is only downloaded is if it is *not* in the cache.  This is handled in the
         * ImageDownloader class.  Consult for details.
         */
        String path = directoryInfo.get(position).toString() + File.separator + directoryInfo.get(position).sampleImageFileName;
        imageDownloader.download(path, directoryImageView, columnWidth);

        // give the view the text identifying the directory
        String s = directoryInfo.get(position).toString() + " (" + directoryInfo.get(position).numberOfImageFiles + ")";
        directoryImageView.setText(s);

        return directoryImageView;
    }

    void setColumnWidth(int widthArg)
    {
        columnWidth = widthArg;
    }
}
