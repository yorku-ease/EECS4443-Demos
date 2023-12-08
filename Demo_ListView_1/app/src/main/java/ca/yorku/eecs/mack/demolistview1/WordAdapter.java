package ca.yorku.eecs.mack.demolistview1;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

// See...
// http://stackoverflow.com/questions/6883785/android-sectionindexer-tutorial

class WordAdapter extends BaseAdapter implements SectionIndexer
{
    final static String MYDEBUG = "MYDEBUG"; // for Log.i messages

    // content of the ListView
    private String[] words;

    // variables needed to support section indexing (see URL above for additional details)
    private String[] sections;
    private HashMap<String, Integer> alphaIndexer;

    WordAdapter(String[] wordsArg)
    {
        words = wordsArg;

        // the code below is added so the ListView supports indexed scrolling

        alphaIndexer = new HashMap<String, Integer>();

        for (int i = 0; i < words.length; i++)
        {
            String s = words[i];
            String ch = s.substring(0, 1);
            ch = ch.toUpperCase();
            if (!alphaIndexer.containsKey(ch))
                alphaIndexer.put(ch, i);
        }

        Set<String> sectionLetters = alphaIndexer.keySet();
        ArrayList<String> sectionList = new ArrayList<String>(sectionLetters);
        Collections.sort(sectionList);
        sections = new String[sectionList.size()];
        sectionList.toArray(sections);
    }

    @Override
    public int getCount()
    {
        return words.length;
    }

    @Override
    public String getItem(int position)
    {
        return words[position];
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        if (view == null)
        {
            view = new TextView(parent.getContext());
            view.setPadding(6, 6, 6, 6);
            view.setBackgroundColor(Color.LTGRAY);
            ((TextView)view).setTextColor(Color.BLACK);
            ((TextView)view).setTextSize(24);
        }
        ((TextView)view).setText(words[position]);
        return view;
    }

    /**
     * The next three methods implement the SectionIndexer interface
     */

    @Override
    public int getPositionForSection(int sectionIndex)
    {
        return alphaIndexer.get(sections[sectionIndex]);
    }

    @Override
    public int getSectionForPosition(int position)
    {
        return 0;
    }

    @Override
    public Object[] getSections()
    {
        return sections;
    }
}
