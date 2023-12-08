package ca.yorku.eecs.mack.demoink;

import java.io.Serializable;

// =========================================================================================
/*
 * A simple class to hold the start/end coordinates and stroke width for a line segment in a
 * sketch.
 */
class Line implements Serializable
{

	private static final long serialVersionUID = 1L;
	
	float x1, y1, x2, y2, strokeWidth;

	Line(float x1Arg, float y1Arg, float x2Arg, float y2Arg, float strokeWidthArg)
	{
		x1 = x1Arg;
		y1 = y1Arg;
		x2 = x2Arg;
		y2 = y2Arg;
		strokeWidth = strokeWidthArg;
	}
}

