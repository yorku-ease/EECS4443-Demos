package ca.yorku.eecs.mack.demolistview3;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter
{
	final int THUMBNAIL_HEIGHT = 300;
	private final ImageDownloader imageDownloader = new ImageDownloader();

	String[] urls;

	ImageAdapter(String[] urlsArg)
	{
		urls = urlsArg;
	}

	@Override
	public int getCount()
	{
		return urls.length;
	}

	@Override
	public String getItem(int position)
	{
		return urls[position];
	}

	@Override
	public long getItemId(int position)
	{
		return urls[position].hashCode();
	}

	@Override
	public View getView(int position, View view, ViewGroup parent)
	{
		if (view == null)
		{
			view = new ImageView(parent.getContext());
			view.setPadding(6, 6, 6, 6);
			view.setBackgroundColor(Color.LTGRAY);
			view.setMinimumHeight(THUMBNAIL_HEIGHT);
			((ImageView)view).setScaleType(ImageView.ScaleType.CENTER);
		}
		imageDownloader.download(urls[position], (ImageView)view);
		return view;
	}
}
