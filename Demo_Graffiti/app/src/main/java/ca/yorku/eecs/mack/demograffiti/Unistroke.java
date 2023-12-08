package ca.yorku.eecs.mack.demograffiti;

import android.graphics.Point;

import java.util.ArrayList;

/**
 * Unistroke - a class for performing handwriting recognition on gestures <p>
 *
 * A <i>Unistroke</i> is a set of <I>x-y</I> sample points created in a single gesture with a stylus or finger on a
 * digitizing surface. The sample points map out the shape of the gesture.  The gestures represent characters, symbols,
 * or commands. A gesture is also called a <i>stroke</i>. <p>
 *
 * The idea for Unistrokes as a handwriting method for computers originated in 1993 in the paper <a href=
 * "http://dl.acm.org/citation.cfm?id=164632.164677&coll=DL&dl=GUIDE&CFID=187414962&CFTOKEN=96829056" >Touch Typing with
 * a Stylus</a> by D. Goldberg and C. Richardson, There is also a <a href="http://www.google
 * .ca/patents?hl=en&lr=&vid=USPAT5596656&id=RuECAAAAEBAJ&oi=fnd&dq=unistrokes&printsec=abstract#v=onepage&q
 * =unistrokes&f=false">patent</a> for Unistrokes, naming Goldberg as the inventor.  The patent issued in January 1997
 * and is assigned to Xerox Corporation. <p>
 *
 * The most widely-known commercial example of Unistrokes is the <a href="http://en.wikipedia
 * .org/wiki/Graffiti_%28Palm_OS%29">Graffiti</a> text entry method used on the <a
 * href="http://en.wikipedia.org/wiki/Palm_pilot">Palm Pilot</a> personal digital assistant (PDA), first introduced in
 * 1997.  The paper <a href="http://www.yorku.ca/mack/GI97a.html">The Immediate Usability of Graffiti</a> provides
 * discussion and historical context for both Unistrokes and Graffiti.  The paper also presents an empirical evaluation
 * of Graffiti. <p>
 *
 * Applications using the <code>Unistroke</code> class generally need to instantiate only one <code>Unistroke</code>
 * object. Thereafter, all processing, such as stroke recognition, loading customDictionary dictionaries, and switching
 * between the built-in and customDictionary dictionaries, is performed through the fields and instance methods of the
 * <code>Unistroke</code> class. <p>
 *
 * The <code>Unistroke</code> class includes three built-in dictionaries (<code>GRAFFITI, UNISTROKES, DIGITS</code>) and
 * support for one customDictionary dictionary. <p>
 *
 * Recognition involves computing the <i>features</i> of an inputted stroke and then comparing the features against
 * those in the dictionary to find a match. <p>
 *
 * <h3>Dictionary Description</h3> <p>
 *
 * Each row in the dictionary contains thirteen (13) values delimited by spaces or commas. As well, a dictionary may
 * include comment lines beginning with "<code>#</code>" or blank lines. These are ignored. <p>
 *
 * The thirteen values consist of a recognized string for the stroke (<code>symbol</code>) followed by twelve stroke
 * features. The features consist of four values representing the stroke's transition through quadrants of a bounding
 * box (<code>quadFirst, quadSecond, quadPenultimate, quadLast</code>), four values representing the cumulative distance
 * of the stroke along the <I>x</I> and <I>y</I> axes (<code>kxmin, kxmax, kymin, kymax</code>), and four values
 * representing the starting and terminating directions of the stroke along the <I>x</I> and <I>y</I> axes
 * (<code>startx, starty, stopx, stopy</code>). <p>
 *
 * In calculating the features in a stroke, the sample points are first normalized to fit within a unit bounding box.
 * The bounding box is divided into four quadrants, "0" at the top right, "1" at the lower right, "2" at the lower left,
 * and "3" at the upper left: <p>
 *
 * <center> <a href="Unistroke-BoundingBox.jpg"><img src = "Unistroke-BoundingBox.jpg" width="300" alt="image"></a>
 * </center> <p>
 *
 * Besides these four values representing a quadrant, a dictionary entry may contain the value "4", implying "don't
 * care". In this case, the recognizer will not check the corresponding quadrant feature in the current stroke. <p>
 *
 * The dictionary entries are described in greater detail below. <p>
 *
 * <blockquote> <dl> <dt> <code>symbol</code> <dd> the string the recognizer returns if the features in the current
 * stroke match the defined values in a particular row of the dictionary. <p>
 *
 * In most cases, the returned string is a single character representing the inputted stroke, such as "a", "b", etc.
 * <p>
 *
 * By convention, "=string" is used for strokes which are recognized, but for which no particular character is assigned
 * by the recognizer. For these strokes, the application is expected to provide an appropriate interpretation. For
 * example,
 *
 * <blockquote> <code>=SW</code> for a straight-line stroke in a south-west direction </blockquote>
 *
 * Since the recognizer returns a string, not a character, it is possible to define shorthand strokes &mdash; single
 * strokes that return a complete word or phrase. These may be defined in or added to the home dictionary or in a
 * dedicated mode-shift dictionary. A mode-shift dictionary is a dictionary invoked through a dedicated mode-shift
 * stroke in the home dictionary (much like the "symbol shift" stroke in Graffiti as implemented on the Palm Pilot).
 * <p>
 *
 * The four quadrant features characterize the beginning and ending of a stroke in terms of the expected quadrants. For
 * example, the stroke for a Graffiti "b" is expected to begin in quadrant 2, then transition into quadrant 3. The
 * stroke ends in quadrant 2, having transitioned from quadrant 1: <p>
 *
 * <center> <a href="Unistroke-Quadrant-Example.jpg"><img src="Unistroke-Quadrant-Example.jpg" width="500"
 * alt="image"></a> </center> <p>
 *
 * The quadrant features are thus defined: <p>
 *
 * <dt> <code>quadFirst</code> <dd> an integer representing the required first quadrant for the stroke. <p>
 *
 * <dt> <code>quadSecond</code> <dd> an integer representing the required second quadrant for the stroke. <p>
 *
 * <dt> <code>quadPenultimate</code> <dd> an integer representing the required penultimate (second last) quadrant for
 * the stroke. <p>
 *
 * <dt> <code>quadLast</code> <dd> an integer representing the required last quadrant for the stroke. </dl> <p>
 *
 * The four cumulative distance features are calculated in the recognizer by summing the absolute distances between
 * successive pairs of sample points along the <I>x</I> and <I>y</I> axes. <p>
 *
 * The calculation is performed on the normalized sample points; that is, on the sample points after they are scaled to
 * fit within the unit bounding box (width = 1, height = 1). <p>
 *
 * For example, a Graffiti "a", if entered perfectly, will have a cumulative <I>x</I> distance of 1 unit and a
 * cumulative <I>y</I> distance of 2 units: <p>
 *
 * <center> <a href="Unistroke-Alphabet-Graffiti-A.jpg"><img src = "Unistroke-Alphabet-Graffiti-A.jpg" width="300"
 * alt="image"></a> </center> <p>
 *
 * Similarly, a Graffiti "z", if entered perfectly, will have a cumulative <I>x</I> distance of 3 units and a cumulative
 * <I>y</I> distance of 1 unit: <p>
 *
 * <center> <a href="Unistroke-Alphabet-Graffiti-Z.jpg"><img src = "Unistroke-Alphabet-Graffiti-Z.jpg" width="300"
 * alt="image"></a> </center> <p>
 *
 * Obviously, users to do not enter their strokes perfectly. For a stroke to be recognized, the calculated cumulative
 * distance features must fall within the minimum and maximum values (inclusive) in the stroke dictionary. These values
 * are specified as reals (e.g., 2.14). <p>
 *
 * Thus, the four cumulative distance values in the dictionary are defined as follows. <p> <p>
 *
 * <dl> <dt> <code>kxmin</code> <dd> the minimum cumulative <I>x</I> distance for the stroke <p>
 *
 * <dt> <code>kxmax</code> <dd> the maximum cumulative <I>x</I> distance for the stroke <p>
 *
 * <dt> <code>kymin</code> <dd> the minimum cumulative <I>y</I> distance for the stroke <p>
 *
 * <dt> <code>kxmax</code> <dd> the maximum cumulative <I>y</I> distance for the stroke </dl> <p>
 *
 * The last four dictionary values represent the starting and terminating direction of the stroke. In the recognizer,
 * these are computed on the first and last 25% of the samples. The computed value is either 0 or 1, where "0" implies
 * motion to the left along the <I>x</I> axis or downward along the <I>y</I> axis, and "1" implies motion to the right
 * along the <I>x</I> axis or upward along the <I>y</I> axis. <p>
 *
 * For example, the stroke for a Graffiti "a" should begin with motion "to the right" along the <I>x</I> axis and motion
 * "up" along the <I>y</I> axis. It should terminate with motion "to the right" along the <I>x</I> axis and motion
 * "down" along the <I>y</I> axis: <p>
 *
 * <center> <a href="Unistroke-StartStopXY.jpg"><img src = "Unistroke-StartStopXY.jpg" width="300" alt="image"></a>
 * </center> <p>
 *
 * Depending on the desired stroke shape, the corresponding dictionary entries should be either 0 or 1. As well, "4" may
 * appear in the dictionary as a "don't care" condition. In this case, the recognizer does not check the stroke's
 * corresponding feature. <p>
 *
 * The four direction values are as follows. <p>
 *
 * <dl> <dt> <code>startx</code> <dd> an integer representing the required starting <i>x</i> direction for the stroke
 * <p>
 *
 * <dt> <code>starty</code> <dd> an integer representing the required starting <i>y</i> direction for the stroke <p>
 *
 * <dt> <code>stopx</code> <dd> an integer representing the required terminating <i>x</i> direction for the stroke <p>
 *
 * <dt> <code>stopy</code> <dd> an integer representing the required terminating <i>y </i> direction for the stroke
 * </dl> <p>
 *
 * </blockquote>
 *
 * @author (c) Scott MacKenzie, 2001-2018
 */

public class Unistroke
{
    public static final String UNRECOGNIZED_STROKE = "#";

    /**
     * A constant identifying the built-in Graffiti stroke dictionary.<p>
     *
     * Use as an argument to the <code>setDictionary()</code> method.<p>
     *
     * The Graffiti alphabet is shown below:<p>
     *
     * <center> <a href="Unistroke-Alphabet.Graffiti.jpg"><img src = "Unistroke-Alphabet-Graffiti.jpg" width="600"
     * alt="image"></a> </center> <p>
     *
     * The stroke shapes above do not exactly match those in the original Graffiti alphabet.  Slight modifications have
     * been introduced to improve recognition. <p>
     *
     * A few entries above (e.g., Caps) include two strokes.  The interpretation is for a mode shift, which must be
     * implemented and managed by the application using the <code>Unistroke</code> class. <p>
     *
     * @see #setDictionary
     */
    public static final int GRAFFITI = 6;

    /**
     * A constant identifying the built-in Unistrokes stroke dictionary.<p>
     *
     * Use as an argument to the <code>setDictionary()</code> method.<p>
     *
     *
     * The Unistrokes alphabet is shown below:<p>
     *
     * <center> <a href="Unistroke-Alphabet-Unistrokes.jpg"><img src = "Unistroke-Alphabet-Unistrokes.jpg" width="600"
     * alt="image"></a> </center> <p>
     *
     * @see #setDictionary
     */
    public static final int UNISTROKES = 7;

    /**
     * A constant identifying the built-in stroke dictionary for digits.<p>
     *
     * Use as an argument to the <code>setDictionary()</code> method.<p>
     *
     * The digit strokes are is shown below:<p>
     *
     * <center> <a href="Unistroke-Alphabet-Digits.jpg"><img src = "Unistroke-Alphabet-Digits.jpg" width="600"
     * alt="image"></a> </center> <p>
     *
     * @see #setDictionary
     */
    public static final int DIGITS = 8;
    private static final int POS = 1;
    private static final int NEG = 0;

    // if the dictionary is used to find a match, this ArrayList holds all the matches
    public static ArrayList<String> dictionaryMatches;

    // =========================================================================================
    // NOTE: The ten stroke features, in order, are...
    // quadFirst        - first quadrant (0 = top-right, 1 = bottom-right, 2 = bottom-left, 3 = top-left)
    // quadSecond       - second quadrant
    // quadsPenultimate - second-last quadrant
    // quadLast         - last quadrant
    // kx               - cumulative x motion (1 = once across in the x direction)
    // ky               - cumulative y motion (1 = once across in the y direction)
    // startx           - starting x direction (0 = left, 1 = right, 4 = don't care)
    // starty           - starting y direction (0 = down, 1 = up,  4 = don't care)
    // stopx            - stopping x direction
    // stopy            - stopping y direction
    // ==========================================================================================

    // ==========================
    // graffiti stroke dictionary
    // ==========================
    private static StrokeDef[] graffitiDictionary =
            {
                    new StrokeDef("a", 2, 3, 0, 1, 0.9, 1.2, 1.5, 2.0, 4, 1, 4, 0),
                    new StrokeDef("b", 3, 2, 1, 2, 2.3, 4.0, 2.5, 3.5, 4, 0, 0, 4), // "B" starting at the top-left
                    new StrokeDef("b", 2, 3, 1, 2, 2.3, 4.0, 1.5, 2.5, 4, 1, 0, 4), // "B" starting at the bottom-left
                    new StrokeDef("c", 0, 3, 2, 1, 1.5, 2.0, 0.9, 2.0, 0, 4, 1, 4),
                    new StrokeDef("d", 3, 2, 1, 2, 1.5, 2.3, 2.0, 3.0, 4, 0, 0, 4), // "D" starting at the top-left
                    new StrokeDef("d", 2, 3, 1, 2, 1.5, 2.3, 1.5, 2.5, 4, 1, 0, 4), // "D" starting at the bottom-left
                    new StrokeDef("e", 0, 3, 2, 1, 2.7, 4.0, 0.9, 2.0, 0, 4, 1, 4),
                    new StrokeDef("f", 0, 3, 3, 2, 0.9, 1.3, 0.9, 1.25, 0, 4, 4, 0),
                    new StrokeDef("g", 0, 3, 1, 2, 1.5, 3.5, 1.3, 2.5, 0, 4, 0, 4), // lowercase "g"
                    new StrokeDef("g", 0, 3, 2, 1, 1.8, 2.7, 1.2, 2.2, 0, 4, 1, 4), // uppercase "G"
                    new StrokeDef("h", 3, 2, 0, 1, 0.9, 1.5, 1.2, 2.5, 4, 0, 4, 0),
                    new StrokeDef("h", 3, 2, 2, 1, 0.9, 1.5, 1.2, 2.5, 4, 0, 4, 0),
                    new StrokeDef("h", 3, 2, 3, 1, 0.9, 1.5, 0.9, 2.5, 4, 0, 4, 0),
                    // "i" is a straight-line stroke down (SOUTH).  See toCharacter
                    new StrokeDef("j", 0, 1, 1, 2, 0.9, 1.4, 0.9, 1.25, 4, 0, 0, 4),
                    new StrokeDef("k", 0, 4, 4, 1, 1.5, 2.5, 1.5, 2.5, 0, 0, 1, 0),
                    new StrokeDef("l", 3, 2, 2, 1, 0.9, 1.3, 0.9, 1.25, 4, 0, 1, 4),
                    new StrokeDef("m", 2, 3, 0, 1, 0.9, 1.5, 2.0, 4.0, 4, 1, 4, 0),
                    new StrokeDef("m", 3, 2, 0, 1, 0.9, 1.5, 3.0, 4.5, 4, 0, 4, 0),
                    new StrokeDef("n", 2, 3, 1, 0, 0.9, 1.5, 2.3, 4.0, 4, 1, 4, 1),
                    new StrokeDef("o", 3, 2, 1, 0, 1.5, 2.5, 1.5, 2.5, 0, 0, 0, 1),
                    new StrokeDef("o", 3, 2, 0, 3, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("o", 0, 3, 1, 0, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("o", 0, 3, 0, 3, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("o", 0, 1, 3, 0, 1.5, 2.5, 1.5, 2.5, 1, 4, 1, 4),
                    new StrokeDef("o", 0, 1, 2, 3, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    new StrokeDef("o", 3, 0, 3, 0, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    new StrokeDef("o", 3, 0, 2, 3, 1.5, 2.5, 1.5, 2.5, 1, 4, 4, 1),
                    new StrokeDef("p", 3, 2, 2, 3, 1.6, 3.0, 2.0, 3.5, 4, 0, 0, 4),
                    new StrokeDef("p", 3, 2, 0, 3, 1.6, 3.0, 2.0, 3.5, 4, 0, 0, 4),
                    new StrokeDef("p", 3, 2, 1, 3, 1.6, 3.0, 2.0, 3.5, 4, 0, 0, 4),
                    new StrokeDef("p", 2, 3, 0, 3, 1.6, 3.0, 1.2, 3.5, 4, 1, 0, 4),
                    new StrokeDef("q", 0, 3, 1, 0, 1.5, 2.5, 1.8, 3.0, 0, 0, 1, 4),
                    new StrokeDef("q", 0, 3, 3, 0, 1.5, 2.5, 1.8, 3.0, 0, 0, 1, 4),
                    new StrokeDef("q", 3, 2, 1, 0, 1.5, 2.5, 1.8, 3.0, 0, 0, 1, 4),
                    new StrokeDef("q", 3, 2, 3, 0, 1.5, 2.5, 1.8, 3.0, 0, 0, 1, 4),
                    new StrokeDef("q", 0, 3, 4, 1, 2.2, 4.0, 2.5, 5.0, 0, 4, 1, 0),
                    new StrokeDef("q", 3, 2, 4, 1, 2.2, 4.0, 2.5, 5.0, 0, 4, 1, 0),
                    new StrokeDef("r", 2, 3, 4, 1, 2.0, 3.0, 1.6, 3.0, 4, 1, 1, 0),
                    new StrokeDef("s", 0, 3, 1, 2, 2.0, 3.5, 0.9, 2.0, 0, 4, 0, 4),
                    new StrokeDef("t", 3, 0, 0, 1, 0.9, 1.2, 0.9, 1.25, 1, 4, 4, 0),
                    new StrokeDef("u", 3, 2, 1, 0, 0.9, 2.5, 0.9, 2.0, 4, 0, 4, 1),
                    new StrokeDef("v", 3, 4, 4, 0, 0.9, 1.2, 1.5, 2.0, 1, 0, 1, 0),
                    new StrokeDef("v", 0, 1, 2, 3, 0.9, 1.4, 1.5, 2.0, 4, 0, 4, 1),
                    new StrokeDef("w", 3, 2, 1, 0, 0.9, 1.5, 2.0, 4.0, 4, 0, 4, 1),
                    new StrokeDef("x", 3, 4, 4, 2, 1.5, 2.5, 1.5, 3.5, 1, 0, 0, 0),
                    new StrokeDef("y", 3, 4, 4, 2, 0.9, 2.0, 1.5, 2.5, 1, 0, 0, 4),
                    new StrokeDef("y", 3, 4, 4, 0, 1.5, 2.8, 1.5, 2.9, 1, 0, 1, 1),
                    new StrokeDef("y", 3, 4, 4, 1, 1.4, 3.0, 1.5, 2.5, 1, 0, 1, 4),
                    new StrokeDef("z", 3, 0, 2, 1, 2.2, 3.5, 0.9, 2.0, 1, 4, 1, 4)
            };

    private static StrokeDef[] unistrokesDictionary =
            {
                    new StrokeDef("a", 1, 4, 4, 0, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("a", 2, 4, 4, 0, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("a", 1, 4, 4, 3, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("a", 2, 4, 4, 3, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("i", 0, 4, 4, 1, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("i", 3, 4, 4, 1, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("i", 0, 4, 4, 2, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("i", 3, 4, 4, 2, 0.0, 0.0, .98, 1.0, 4, 4, 4, 4),
                    new StrokeDef("t", 3, 4, 4, 0, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("t", 2, 4, 4, 0, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("t", 3, 4, 4, 1, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("t", 2, 4, 4, 1, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("e", 0, 4, 4, 3, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("e", 1, 4, 4, 3, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("e", 0, 4, 4, 2, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("e", 1, 4, 4, 2, .98, 1.0, 0.0, 0.0, 4, 4, 4, 4),
                    new StrokeDef("k", 2, 4, 4, 0, .99, 1.0, .99, 1.0, 4, 4, 4, 4),
                    new StrokeDef("r", 3, 4, 4, 1, .99, 1.0, .99, 1.0, 4, 4, 4, 4),
                    new StrokeDef("y", 0, 4, 4, 2, .99, 1.0, .99, 1.0, 4, 4, 4, 4),
                    new StrokeDef("b", 3, 0, 1, 2, 1.5, 2.5, 0.9, 1.5, 1, 0, 0, 0),
                    new StrokeDef("c", 1, 2, 3, 0, 1.5, 2.5, 0.9, 1.5, 0, 1, 1, 1),
                    new StrokeDef("d", 0, 3, 2, 1, 1.5, 2.5, 0.9, 1.5, 0, 0, 1, 0),
                    new StrokeDef("f", 0, 3, 3, 2, 0.9, 1.5, 0.9, 1.5, 0, 4, 4, 0),
                    new StrokeDef("g", 2, 1, 1, 0, 0.9, 1.5, 0.9, 1.5, 1, 4, 4, 1),
                    new StrokeDef("h", 3, 0, 0, 1, 0.9, 1.5, 0.9, 1.5, 1, 4, 4, 0),
                    new StrokeDef("j", 0, 1, 1, 2, 0.9, 1.5, 0.9, 1.5, 4, 0, 0, 4),
                    new StrokeDef("l", 3, 2, 2, 1, 0.9, 1.5, 0.9, 1.5, 4, 0, 1, 4),
                    new StrokeDef("m", 1, 0, 3, 2, 0.9, 1.5, 1.5, 2.5, 0, 1, 0, 0),
                    new StrokeDef("n", 2, 3, 0, 1, 0.9, 1.5, 1.5, 2.5, 1, 1, 1, 0),
                    new StrokeDef("o", 0, 4, 4, 3, 1.5, 2.5, 1.5, 2.5, 0, 0, 0, 1),
                    new StrokeDef("p", 3, 4, 4, 2, 1.5, 2.5, 1.5, 2.5, 1, 0, 0, 0),
                    new StrokeDef("q", 0, 4, 4, 1, 1.5, 2.5, 1.5, 2.5, 0, 0, 1, 0),
                    new StrokeDef("r", 3, 4, 4, 1, 0.9, 1.5, 0.9, 1.5, 1, 0, 1, 0),
                    new StrokeDef("s", 0, 3, 1, 2, 2.0, 3.0, 0.9, 1.5, 0, 4, 0, 4),
                    new StrokeDef("u", 0, 1, 2, 3, 0.9, 1.5, 1.5, 2.5, 0, 0, 0, 1),
                    new StrokeDef("v", 3, 2, 1, 0, 0.9, 1.5, 1.5, 2.0, 1, 0, 1, 1),
                    new StrokeDef("w", 3, 2, 0, 1, 0.9, 1.5, 2.0, 3.0, 4, 0, 4, 0),
                    new StrokeDef("x", 3, 4, 4, 0, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    new StrokeDef("y", 2, 4, 4, 0, 0.9, 1.5, 0.9, 1.5, 1, 1, 1, 1),
                    new StrokeDef("z", 3, 0, 2, 1, 2.0, 3.0, 0.9, 1.5, 1, 4, 1, 4),
                    new StrokeDef("=CR", 2, 1, 0, 3, 1.5, 2.5, 0.9, 1.5, 1, 1, 0, 1)
            };

    // digits stroke dictionary
    private static StrokeDef[] digitsDictionary =
            {
                    new StrokeDef("0", 3, 2, 1, 0, 1.5, 2.5, 1.5, 2.5, 0, 0, 0, 1),
                    new StrokeDef("0", 3, 2, 0, 3, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("0", 0, 3, 1, 0, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("0", 0, 3, 0, 3, 1.5, 2.5, 1.5, 2.5, 0, 4, 0, 4),
                    new StrokeDef("0", 0, 1, 3, 0, 1.5, 2.5, 1.5, 2.5, 1, 4, 1, 4),
                    new StrokeDef("0", 0, 1, 2, 3, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    new StrokeDef("0", 3, 0, 3, 0, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    new StrokeDef("0", 3, 0, 2, 3, 1.5, 2.5, 1.5, 2.5, 1, 0, 1, 1),
                    // "1" is a straight-line stroke down (SOUTH).  See toCharacter
                    new StrokeDef("2", 3, 0, 2, 1, 2.0, 4.0, 0.9, 1.5, 1, 4, 1, 4),
                    new StrokeDef("2", 3, 0, 2, 1, 2.0, 4.0, 0.9, 1.5, 1, 4, 1, 4),
                    new StrokeDef("3", 3, 0, 1, 2, 2.0, 4.5, 0.9, 1.5, 1, 4, 0, 4),
                    new StrokeDef("4", 3, 2, 2, 1, 0.9, 1.5, 0.9, 1.5, 4, 0, 1, 4),
                    new StrokeDef("4", 0, 3, 2, 1, 0.9, 2.0, 0.9, 1.5, 0, 0, 1, 4),
                    new StrokeDef("5", 0, 3, 1, 2, 2.0, 3.5, 0.9, 1.30, 0, 4, 0, 4),
                    new StrokeDef("5", 3, 0, 1, 2, 2.0, 3.5, 0.9, 1.30, 0, 4, 0, 4),
                    new StrokeDef("6", 0, 3, 4, 2, 1.5, 3.0, 1.31, 2.5, 0, 4, 0, 4),
                    new StrokeDef("6", 3, 2, 4, 2, 1.5, 2.5, 1.31, 2.5, 0, 4, 0, 4),
                    new StrokeDef("7", 3, 0, 0, 1, 0.9, 1.5, 0.9, 1.5, 1, 4, 4, 0),
                    new StrokeDef("7", 3, 0, 1, 2, 0.9, 2.0, 0.9, 1.5, 1, 4, 4, 0),
                    new StrokeDef("7", 3, 0, 3, 2, 0.9, 2.0, 0.9, 1.5, 1, 4, 4, 0),
                    new StrokeDef("8", 0, 4, 4, 0, 2.5, 4.0, 1.5, 3.5, 4, 4, 4, 1),
                    new StrokeDef("8", 0, 4, 4, 3, 2.5, 4.0, 1.5, 3.5, 4, 4, 4, 1),
                    new StrokeDef("8", 3, 4, 4, 0, 2.5, 4.0, 1.5, 3.5, 4, 4, 4, 1),
                    new StrokeDef("8", 3, 4, 4, 3, 2.5, 4.0, 1.5, 3.5, 4, 4, 4, 1),
                    new StrokeDef("9", 0, 3, 0, 1, 1.5, 2.5, 1.3, 2.5, 0, 4, 4, 0)
            };

    /**
     * Provides access to the currently active stroke dictionary.<p>
     *
     * A variety of debugging and advanced programming services are available through this public variable.
     *
     * Given a <code>Unistroke</code> object <code>u</code>, the number of entries in the currently active dictionary
     * is
     *
     * <pre>
     *     u.activeDictionary.length
     * </pre>
     *
     * Each entry in the currently active dictionary may be retrieved.  For example
     *
     * <pre>
     *     for (int i = 0; i &lt; u.activeDictionary.length; ++i)
     *        System.out.println(u.activeDictionary[i]);
     * </pre>
     *
     * outputs the entire dictionary to the console.<p>
     *
     * Each row in the dictionary is returned as a string containing thirteen comma-delimited values.  For example, the
     * entry for the letter "a" in the built-in Graffiti dictionary appears as follows:<p>
     *
     * <pre>
     *     a, 2, 3, 0, 1, 0.9, 1.2, 1.5, 2.0, 4, 1, 4, 0
     * </pre>
     *
     * where
     *
     * <pre>
     *     symbol = a
     *     quadFirst  = 2
     *     quadSecond = 3
     *     quadsPenultimate = 0
     *     quadLast = 1
     *     kxmin = 0.9
     *     kxmax = 1.2
     *     kymin = 1.5
     *     kymax = 2.0
     *     startx = 4
     *     starty = 1
     *     stopx = 4
     *     stopy = 0
     * </pre>
     *
     * See the description for the <code>loadDictionary()</code> method for further discussion on each entry in the
     * dictionary.
     *
     */
    public StrokeDef[] activeDictionary;

    private int currentDictionary;
    private String undefinedStroke;
    private float aspectRatio;

    /**
     * Construct a Unistroke object.<p>
     *
     * Once a Unistroke object is declared, character recognition may proceed immediately via the
     * <code>recognize()</code> method.
     *
     * The default dictionary is the graffiti dictionary.<p>
     */
    Unistroke()
    {
        // a stroke is considered a straight line if its thickness-to-length ratio is less than this value
        aspectRatio = 0.2f;

        undefinedStroke = UNRECOGNIZED_STROKE;
        setDictionary(GRAFFITI); // set default dictionary to graffiti
    }

    /**
     * Set or change the active dictionary. For example,<p>
     *
     * <pre>
     *     setDictionary(Unistroke.DIGITS);
     * </pre>
     *
     * changes the currently active dictionary to the digits dictionary.<p>
     *
     * @param d an integer representing a dictionary.<p> The following dictionaries are defined:<p>
     *
     *          <ul> <li> <code>Unistroke.GRAFFITI</code> - built-in Graffiti dictionary (default) <li>
     *          <code>Unistroke.UNISTROKES</code> - built-in Unistrokes dictionary <li> <code>Unistroke .DIGITS</code> -
     *          build-in digits dictionary <li> <code>Unistroke.CUSTOM</code> - user-provided custom dictionary </ul>
     */
    public void setDictionary(int d)
    {
        currentDictionary = d;
        if (d == GRAFFITI)
            activeDictionary = graffitiDictionary;      // graffiti stroke dictionary
        else if (d == UNISTROKES)
            activeDictionary = unistrokesDictionary;      // unistrokes stroke dictionary
        else if (d == DIGITS)
            activeDictionary = digitsDictionary;      // digits stroke dictionary
    }

    /**
     * Perform handwriting recognition (<code>Point</code> class version).
     *
     * @param p      an array of <code>Point</code> objects.
     * @param length an integer representing the number of elements in the <code>Point</code> array to process
     * @return a string representation of the recognized stroke<p>
     */
    String recognize(Point p[], int length)
    {
        // make arrays
        int[] xx = new int[length];
        int[] yy = new int[length];
        for (int i = 0; i < length; ++i)
        {
            xx[i] = p[i].x;
            yy[i] = p[i].y;
        }

        // do the recognition and return result
        return toCharacter(xx, yy, length);
    }

    /**
     * Perform handwriting recognition (<code>ArrayList</code> version).
     */
    String recognize(ArrayList<Point> gesture)
    {
        return recognize(gesture.toArray(new Point[gesture.size()]), gesture.size());
    }

    //========================================================
    // All the work is done here!
    //========================================================
    private String toCharacter(int[] x, int[] y, int n)
    {
        float kx = 0.0f;
        float ky = 0.0f;
        float kz = 0.0f; // cumulative distance of the entire stroke
        int startx;
        int starty;
        int stopx;
        int stopy;
        String stroke = ""; // the result will be put here

        //------------------------------------------------------
        // 1. Normalize points to fit in a unit bounding box
        //------------------------------------------------------

        float[] nx = new float[n];
        float[] ny = new float[n];

        // find min and max for the x coordinates in the sample point array
        float xMin = Float.MAX_VALUE;
        float xMax = Float.MIN_VALUE;
        for (int xSample : x)
        {
            xMin = Math.min(xSample, xMin);
            xMax = Math.max(xSample, xMax);
        }
        float xSpread = xMax - xMin;

        // repeat for y coordinates
        float yMin = Float.MAX_VALUE;
        float yMax = Float.MIN_VALUE;
        for (int ySample : y)
        {
            yMin = Math.min(ySample, yMin);
            yMax = Math.max(ySample, yMax);
        }
        float ySpread = yMax - yMin;

        // Normalize points to fit in a "unit" bounding box
        for (int j = 0; j < n; ++j)
        {
            if ((xMax - xMin) != 0.0)
                nx[j] = x[j] / xSpread - xMin / xSpread;
            else
                nx[j] = 0.5f;

            if ((yMax - yMin) != 0.0)
                ny[j] = y[j] / ySpread - yMin / ySpread;
            else
                ny[j] = 0.5f;
        }

        // ---------------------------------
        // 2. Look for straight-line strokes
        // ---------------------------------

        //---------------------------------------------
        // 2a. Check for vertical or horizontal strokes
        //---------------------------------------------
        if (xSpread < aspectRatio * ySpread && ny[0] > ny[n - 1]) // north
            stroke = "=N";
        else if (ySpread < aspectRatio * xSpread && nx[0] < nx[n - 1]) // east
            stroke = "=E";
        else if (ySpread < aspectRatio * xSpread && nx[0] > nx[n - 1]) // west
            stroke = "=W";
        else if (xSpread < aspectRatio * ySpread && ny[0] < ny[n - 1]) // south
        {
            if (currentDictionary == GRAFFITI)
                stroke = "i";
            else if (currentDictionary == DIGITS)
                stroke = "1";
            else
                stroke = "=S";
        }

        if (stroke.length() > 0) // we're done, return now (with recognized stroke)
            return stroke;

        // --------------------------------------------
        // 2b. Check for diagonal straight-line strokes
        // --------------------------------------------
        float adjustedAspectRatio = 1.4f * aspectRatio;  // to accommodate the diagonal (it's a trig thing)

        boolean equalDiagonal = true;    // x and y both increasing or both decreasing along array
        boolean oppositeDiagonal = true; // x and y moving in opposite directions along stroke
        for (int i = 0; i < n; ++i)
        {
            if (nx[i] < ny[i] - adjustedAspectRatio || nx[i] > ny[i] + adjustedAspectRatio)
                equalDiagonal = false;
            if (1 - nx[i] < ny[i] - adjustedAspectRatio || 1 - nx[i] > ny[i] + adjustedAspectRatio)
                oppositeDiagonal = false;
            if (!equalDiagonal && !oppositeDiagonal)
                break; // no need to check any further
        }

        if (equalDiagonal && nx[0] < nx[n - 1])  // x is decreasing
            stroke = "=SE";
        else if (oppositeDiagonal && nx[0] < nx[n - 1]) // x is increasing
            stroke = "=NE";
        else if (oppositeDiagonal && nx[0] > nx[n - 1]) // x is decreasing
            stroke = "=SW";
        else if (equalDiagonal && nx[0] > nx[n - 1]) // x is increasing
            stroke = "=NW";

        if (stroke.length() > 0) // we're done, return now (with recognized stroke)
            return stroke;

        // ------------------------------
        // 3. Compute ten stroke features
        // ------------------------------

        //-------------------------------------------------------------
        // 3a. Calculate cumulative length of stroke along x and y axes
        //-------------------------------------------------------------
        for (int j = 1; j < n; ++j)
        {
            kx += Math.abs(nx[j] - nx[j - 1]);
            ky += Math.abs(ny[j] - ny[j - 1]);
            kz += (float)Math.sqrt((nx[j] - nx[j - 1]) * (nx[j] - nx[j - 1])
                    + (ny[j] - ny[j - 1]) * (ny[j] - ny[j - 1]));
        }

        //---------------------------------------------------
        // 3b. Determine 1st, 2nd, 2nd last, & last quadrants
        //---------------------------------------------------
        // where...           -------
        // 0 = upper-right   | 3 | 0 |
        // 1 = lower-right   |-------|
        // 2 = lower-left    | 2 | 1 |
        // 3 = upper-left     -------
        //
        int quadFirst = findQuad(nx[0], ny[0]);
        int quadLast = findQuad(nx[n - 1], ny[n - 1]);

        int quadSecond;
        int j = 0;
        while (j < n && findQuad(nx[j], ny[j]) == quadFirst)
            ++j;
        quadSecond = findQuad(nx[j], ny[j]);

        int quadPenultimate;
        j = n - 1;
        while (j >= 0 && findQuad(nx[j], ny[j]) == quadLast)
            --j;
        quadPenultimate = findQuad(nx[j], ny[j]);

        //-----------------------------------------------------------
        // 3c. Determine starting and terminating direction of stroke
        //-----------------------------------------------------------
        // Note: Starting direction is based on a comparison between the first sample point and the sample
        // point corresponding to approximately 10% of the cumulative distance along the stroke.
        // The stopping direction is based on a comparison between the last sample point and the sample
        // point corresponding to about 90% of the cumulative distance along the stroke.
        // E.g., if startx = 1, movement is to the right
        //       if starty = 1, movement is down

        int idxNearStart = -1;
        int idxNearEnd = -1;
        float zTemp = 0; // cumulative distance along the stroke
        for (j = 1; j < n; ++j)
        {
            zTemp += (float)Math.sqrt((nx[j] - nx[j - 1]) * (nx[j] - nx[j - 1]) + (ny[j] - ny[j - 1]) * (ny[j] - ny[j
                    - 1]));
            if (idxNearStart == -1 && zTemp >= (kz * 0.10))
                idxNearStart = j;
            if (idxNearEnd == -1 && zTemp >= (kz * 0.90))
                idxNearEnd = j;
        }

        startx = nx[idxNearStart] - nx[0] > 0 ? POS : NEG; // POS = right, NEG = left
        starty = ny[idxNearStart] - ny[0] < 0 ? POS : NEG; // POS = down, NEG = up
        stopx = nx[n - 1] - nx[idxNearEnd] > 0 ? POS : NEG;
        stopy = ny[n - 1] - ny[idxNearEnd] < 0 ? POS : NEG;

        //-------------------------------------------------------------
        // 4. Use stroke features to look for a match in the dictionary
        //-------------------------------------------------------------
        StrokeDef[] d = activeDictionary; // just make a copy for convenience
        dictionaryMatches = new ArrayList<String>(); // store all the matches here

        // NOTE: This routine finds the "first match" rather than the "best match".  Finding the best match makes
        // more sense, so this is something for the "to do" list.

        // this is the "don't care" constant used for recognition of some of the stroke features
        final int DONT_CARE = 4;

        for (j = 0; j < d.length; j++)
        {
            if ((quadFirst == d[j].getDEFquadf() || d[j].getDEFquadf() == DONT_CARE) &&
                    (quadSecond == d[j].getDEFquads() || d[j].getDEFquads() == DONT_CARE) &&
                    (quadPenultimate == d[j].getDEFquadsl() || d[j].getDEFquadsl() == DONT_CARE) &&
                    (quadLast == d[j].getDEFquadl() || d[j].getDEFquadl() == DONT_CARE) &&
                    kx >= d[j].getDEFkxmin() &&
                    kx <= d[j].getDEFkxmax() &&
                    ky >= d[j].getDEFkymin() &&
                    ky <= d[j].getDEFkymax() &&
                    (startx == d[j].getDEFstartX() || d[j].getDEFstartX() == DONT_CARE) &&
                    (starty == d[j].getDEFstartY() || d[j].getDEFstartY() == DONT_CARE) &&
                    (stopx == d[j].getDEFstopX() || d[j].getDEFstopX() == DONT_CARE) &&
                    (stopy == d[j].getDEFstopY() || d[j].getDEFstopY() == DONT_CARE))
            {
                dictionaryMatches.add(d[j].getDEFsymbol());
            }
        }

        // Take the first match (TO DO: find the "best" match)
        stroke = dictionaryMatches.size() > 0 ? dictionaryMatches.get(0) : undefinedStroke;

        return stroke; // done!
    }

    private int findQuad(float x, float y)
    {
        if (x >= 0.5 && y <= 0.5) return 0; // upper right
        if (x >= 0.5 && y > 0.5) return 1; // lower right
        if (x < 0.5 && y > 0.5) return 2; // lower left
        if (x < 0.5 && y <= 0.5) return 3; // upper left
        return -1; // should never get here!
    }
}