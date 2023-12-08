package ca.yorku.eecs.mack.demograffiti;

/**
 * StrokeDef - a class to hold the thirteen entries for a row in the dictionary
 * 
 * See the API for the <code>Unistroke</code> class for complete details on the dictionary entries.
 * <p>
 * @author (c) Scott MacKenzie, 2001-2013
 *
 */
class StrokeDef 
{
    private String DEFs; // recognized symbol (actually a string)
    private int DEFquadf; // first quadrant
    private int DEFquads; // second quadrant
    private int DEFquadp; // penultimate quadrant
    private int DEFquadl; // last quadrant
    private double DEFkxmin; // minimum cumulative x distance
    private double DEFkxmax; // maximum cumulative x distance
    private double DEFkymin; // minimum cumulative y distance
    private double DEFkymax; // minimum cumulative y distance
    private int DEFstartx; // starting direction, x axis
    private int DEFstarty; // starting direction, y axis
    private int DEFstopx; // stopping direction, x axis
    private int DEFstopy; // stopping direction, y axis
    
    public StrokeDef(String s, int i, int j, int k, int l, double d,
            double d1, double d2, double d3, int i1,
            int j1, int k1, int l1) 
    {
        DEFs = s;
        DEFquadf = i;
        DEFquads = j;
        DEFquadp = k;
        DEFquadl = l;
        DEFkxmin = d;
        DEFkxmax = d1;
        DEFkymin = d2;
        DEFkymax = d3;
        DEFstartx = i1;
        DEFstarty = j1;
        DEFstopx = k1;
        DEFstopy = l1;
    }
    
    public String getDEFsymbol() 
    {
        return DEFs;
    }
    
    public int getDEFquadf() {
        return DEFquadf;
    }
    
    public int getDEFquads() {
        return DEFquads;
    }
    
    public int getDEFquadsl() {
        return DEFquadp;
    }
    
    public int getDEFquadl() {
        return DEFquadl;
    }
    
    public double getDEFkxmin() {
        return DEFkxmin;
    }
    
    public double getDEFkxmax() {
        return DEFkxmax;
    }
    
    public double getDEFkymin() {
        return DEFkymin;
    }
    
    public double getDEFkymax() {
        return DEFkymax;
    }
    
    public int getDEFstartX() {
        return DEFstartx;
    }
    
    public int getDEFstartY() {
        return DEFstarty;
    }
    
    public int getDEFstopX() {
        return DEFstopx;
    }
    
    public int getDEFstopY() {
        return DEFstopy;
    }
    
    public String toString() {
        return DEFs + ", " + DEFquadf + ", " + DEFquads + ", " + DEFquadp + ", " + DEFquadl + ", " + DEFkxmin + ", " + DEFkxmax + ", " + DEFkymin + ", " + DEFkymax + ", " + DEFstartx + ", " + DEFstarty + ", " + DEFstopx + ", " + DEFstopy;
    }
}
