package ca.yorku.eecs.mack.demograffiti;

import android.graphics.Point;
import android.view.KeyEvent;

import java.util.ArrayList;

/**
 * GraffitiEvent - This class represents a Graffiti event and holds information about the event. A Graffiti event occurs
 * on finger lift, after the stroke data are processed by the recognizer. The information available in a
 * <code>GraffitiEvent</code> object includes the the character code, character type, etc.
 *
 * @author (c) Scott MacKenzie, 2013-2017
 */
class GraffitiEvent
{
    static final int TYPE_UNRECOGNIZED = -2;
    static final int TYPE_NONE = -1;
    static final int TYPE_ALPHA = 0;
    static final int TYPE_NUMERIC = 1;
    static final int TYPE_SPACE = 2;
    static final int TYPE_ENTER = 3;
    static final int TYPE_BACKSPACE = 4;
    static final int TYPE_SYMBOL = 5;
    static final int TYPE_TAP = 6;
    static final int TYPE_NORTH = 7;
    static final int TYPE_NORTH_EAST = 8;
    static final int TYPE_SOUTH_EAST = 9;
    static final int TYPE_NORTH_WEST = 10;

    static final int CHAR_NULL = 0;
    static final int CHAR_UNRECOGNIZED = (char)Unistroke.UNRECOGNIZED_STROKE.charAt(0);
    static final int CHAR_ENTER = KeyEvent.KEYCODE_ENTER;
    static final int CHAR_BACKSPACE = KeyEvent.KEYCODE_DEL;
    static final int CHAR_SPACE = ' ';

    private String raw; // raw string returned by Unistroke's recognize method
    int charCode; // character code for sending to a text field
    int type; // type of gesture
    private int x, y; // x and y coordinate of finger lift
    private long timeStampFingerDown; // timestamp (ms) for finger down
    private long timeStampFingerUp; // timestamp (ms) for finger up
    private ArrayList<Point> gesture; // sample points forming the gesture

    /**
     * GraffitiEvent - event object passed to <code>onStroke</code> callback method
     *
     * @param rawArg               raw string returned by the <code>recognize</code> method in the
     *                             <code>Unistroke</code> class
     * @param charCodeArg          character code for sending to a text field
     * @param typeArg              type of gesture (see TYPE fields defined herein)
     * @param xArg                 <i>x</i> coordinate of finger lift
     * @param yArg                 <i>y</i> coordinate of finger lift
     * @param timeStampFingerDownArg    timestamp (ms) for finger down
     * @param timeStampFingerUpArg timestamp (ms) for finger up
     * @param gestureArg           the point array holding the gesture
     */
    GraffitiEvent(String rawArg, int charCodeArg, int typeArg, int xArg, int yArg, long timeStampFingerDownArg,
                         long timeStampFingerUpArg,
                         ArrayList<Point> gestureArg)
    {
        raw = rawArg;
        charCode = charCodeArg;
        type = typeArg;
        x = xArg;
        y = yArg;
        timeStampFingerDown = timeStampFingerDownArg;
        timeStampFingerUp = timeStampFingerUpArg;
        gesture = gestureArg;
    }
}
