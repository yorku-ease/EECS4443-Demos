package ca.yorku.eecs.mack.demoink;

import java.io.Serializable;
import java.util.ArrayList;

// =========================================================================================
/*
 * A simple class to hold the sketch, which is an ArrayList of Stroke objects.
 * 
 * Declared with "implements Serializable" so the sketch can be put into a Bundle and
 * retrieved from a Bundle, as occurs when the screen is rotated and the application is shut
 * down and restarted. See onSaveInstanceState and onRestoreInstanceState.
 */
class Sketch implements Serializable
{
    static final long serialVersionUID = 42L;

    ArrayList<Stroke> strokeArray;

    Sketch()
    {
        strokeArray = new ArrayList<Stroke>();
    }
}
