package ca.yorku.cse.mack.demoh4touch;

import java.util.Locale;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RadialGradient;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class H4Keyboard extends View // implements View.OnTouchListener
{
	final float KEYBOARD_HEIGHT_FACTOR = 0.6f; // keyboard height is 60% of display height
	final float MAXIMUM_ASPECT_RATIO = 1.2f; // maximum aspect ratio (needed for triangular keys
												// only)
	final float STEM_CODE_TEXT_SIZE = 18f;
	final float DEFAULT_OFFSET = 5f; // for positioning red stem code on H4Keyboard
	final float DEFAULT_EXTRA_OFFSET = 6 * DEFAULT_OFFSET; // as above, but further over for
															// Triangular keys
	final int DEFAULT_THICK_LINE_SIZE = 5;

	final static int SQUARE = 1;
	final static int TRIANGLE = 2;

	final int DEFAULT_KEY_WIDTH = 200;
	final int DEFAULT_KEY_HEIGHT = 600;
	final float KEY_TEXT_SIZE = 24f;
	final float SCALE_FACTOR = 10000;

	// ==================
	// Huffman Code Trees
	// ==================

	final static String SPACE = "[Space]";
	final static String BACKSPACE = "[Bksp]";
	final static String ENTER = "[Entr]";
	final static String SHIFT = "[Shft]";
	final static String CAPS_LOCK = "[CapsLock]";
	final static String SYMBOL = "[Symbol]";
	final static String SYMBOL_LOCK = "[SymLock]";
	final static String TAB = "[Tab]";

	final static String[][] LETTER_CODES = { { SPACE, "33" }, { BACKSPACE, "00" }, { ENTER, "01" }, { SHIFT, "02" },
			{ CAPS_LOCK, "030" }, { ".", "031" }, { ",", "032" }, { SYMBOL, "033" }, { "e", "11" }, { "t", "22" },
			{ "a", "23" }, { "o", "20" }, { "i", "13" }, { "n", "12" }, { "s", "31" }, { "h", "10" }, { "r", "322" },
			{ "l", "300" }, { "d", "321" }, { "c", "303" }, { "u", "302" }, { "f", "301" }, { "m", "323" },
			{ "w", "213" }, { "y", "212" }, { "p", "211" }, { "g", "210" }, { "b", "3203" }, { "v", "3202" },
			{ "k", "3201" }, { "x", "32003" }, { "j", "32002" }, { "q", "32001" }, { "z", "32000" } };

	// NOTE: symbol codes implicitly begin with "033" (see above)
	// NOTE: BACKSPACE is repeated here (as a matter of convenience to simplify corrections if in
	// symbol lock mode)
	final static String[][] SYMBOL_CODES = { { SYMBOL_LOCK, "12" }, { "?", "11" }, { "%", "10" }, { "-", "03" },
			{ "@", "02" }, { ";", "01" }, { BACKSPACE, "00" }, { ":", "333" }, { "!", "332" }, { "1", "331" },
			{ "2", "330" }, { "3", "323" }, { "4", "322" }, { "5", "321" }, { "6", "320" }, { "7", "313" },
			{ "8", "312" }, { "9", "311" }, { "0", "310" }, { "=", "303" }, { "+", "302" }, { "<", "301" },
			{ ">", "300" }, { "/", "233" }, { "|", "232" }, { "\\", "231" }, { "_", "230" }, { "\"", "223" },
			{ TAB, "222" }, { "#", "221" }, { "$", "220" }, { "^", "212" }, { "&", "211" }, { "*", "210" },
			{ "(", "203" }, { ")", "202" }, { "[", "201" }, { "]", "200" }, { "{", "133" }, { "}", "132" },
			{ "'", "131" }, { "~", "130" } };
	// end Huffman code trees

	// NOTE: The code tree is easily extended. For example, the code for the dollar sign above ("$"
	// = 220)
	// could be replaced with...
	// { "$", "2200" },
	// { "\u20ac", "2201" },
	// { "\u00a3", "2202" },
	// { "\u00a5", "2203" },
	// The latter three symbols are for the Euro, Pound, and Yuan, respectively.

	boolean capOn, capLockOn, symOn, symLockOn;
	//boolean vibrotactileFeedback, auditoryFeedback;
	String[] keyNoText = { "", "", "", "" };

	int width = DEFAULT_KEY_WIDTH;
	int height = DEFAULT_KEY_HEIGHT;
	PointF[][] pxy;
	PointF[] cross1, cross2;
	Path[] keyPath;
	H4Key[] h4key = new H4Key[4];
	Paint keyLinePaint, keyTextPaint, keyThickLinePaint, stemCodePaint;
	Paint keyNumberPaint;
	int activeKey = -1;
	int keyStyle;

	// a scaling Factor to adjust for screen density (=1 on a 160 dpi display; see DisplayMetrics)
	float scalingFactor = 1f;
	float xOffset = DEFAULT_OFFSET;
	float xExtraOffset = DEFAULT_EXTRA_OFFSET;
	float yOffset = STEM_CODE_TEXT_SIZE;
	float thickLineSize = DEFAULT_THICK_LINE_SIZE;

	StringBuilder huffmanStem;
	String[][] codes;
	boolean showHuffmanCodes;

	float pixelDensity;
	Bitmap featheredTouchPoint;
	float xTouch, yTouch, offset;

	H4Listener h4Listener;

	public H4Keyboard(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		initialize(context);
	}

	public H4Keyboard(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		initialize(context);
	}

	public H4Keyboard(Context context)
	{
		super(context);
		initialize(context);
	}

	// will be called from the Activity using the H4Keyboard
	public void setH4Listener(H4Listener h4ListenerArg)
	{
		h4Listener = h4ListenerArg;
	}

	/*
	 * Set the size of the keyboard according to the display width and height. This is coded as a
	 * method since the size must be set initially (from onCreate) and (potentially) when the device
	 * orientation changes.
	 */
	void setKeyboardSize(int displayWidthArg, int displayHeightArg, float scalingFactorArg)
	{
		scalingFactor = scalingFactorArg;
		int keyboardWidth = displayWidthArg;
		
		// keyboard height is 60% of display height
		int keyboardHeight = (int)(displayHeightArg * KEYBOARD_HEIGHT_FACTOR + 0.5f);
		
		xOffset = DEFAULT_OFFSET * scalingFactor;
		xExtraOffset = DEFAULT_EXTRA_OFFSET * scalingFactor;
		yOffset = STEM_CODE_TEXT_SIZE * scalingFactor;
		keyThickLinePaint.setStrokeWidth(DEFAULT_THICK_LINE_SIZE * scalingFactor);

		// if the keys are triangular, check width:height ratio and adjust if > 1.2 (needed for
		// landscape orientation)
		if (keyStyle == H4Keyboard.TRIANGLE && (float)keyboardWidth / keyboardHeight > MAXIMUM_ASPECT_RATIO)
			keyboardWidth = (int)(1.2f * keyboardHeight);

		configureKeys(keyboardWidth, keyboardHeight, keyStyle);

		updateHuffmanKeys(huffmanStem.toString());
	}

	private void initialize(Context c)
	{
		pixelDensity = c.getResources().getDisplayMetrics().density;

		// this.setOnTouchListener(this);
		huffmanStem = new StringBuilder();
		keyStyle = SQUARE;
		// keyStyle = TRIANGLE;
		showHuffmanCodes = true;

		keyLinePaint = new Paint();
		keyLinePaint.setStyle(Paint.Style.STROKE);
		keyLinePaint.setStrokeWidth(0); // 0 = hairline (1 px)
		keyLinePaint.setAntiAlias(true);
		keyLinePaint.setColor(0xff444444);

		keyThickLinePaint = new Paint();
		keyThickLinePaint.setStyle(Paint.Style.STROKE);
		keyThickLinePaint.setStrokeWidth(DEFAULT_THICK_LINE_SIZE);
		keyThickLinePaint.setStrokeCap(Paint.Cap.ROUND);
		keyThickLinePaint.setAntiAlias(true);
		keyThickLinePaint.setColor(0xff444444);

		keyTextPaint = new Paint();
		keyTextPaint.setTextAlign(Paint.Align.CENTER);
		keyTextPaint.setTextSize(KEY_TEXT_SIZE * scalingFactor);
		keyTextPaint.setAntiAlias(true);

		stemCodePaint = new Paint();
		stemCodePaint.setTextAlign(Paint.Align.LEFT);
		stemCodePaint.setTextSize(STEM_CODE_TEXT_SIZE * scalingFactor);
		stemCodePaint.setAntiAlias(true);
		stemCodePaint.setColor(0xffff0000);

		featheredTouchPoint = getFeatheredTouchPoint((int)(160 * pixelDensity), (int)(160 * pixelDensity)); 
		offset = featheredTouchPoint.getWidth() / 2f;
		resetState();
	}

	public void resetState()
	{
		configureKeys(width, height, keyStyle);
		capOn = false;
		capLockOn = false;
		symOn = false;
		symLockOn = false;
		codes = LETTER_CODES;
		huffmanStem.setLength(0);
		updateHuffmanKeys(huffmanStem.toString());
		invalidate();
	}

	public void configureKeys(int widthArg, int heightArg, int shape)
	{
		width = widthArg;
		height = heightArg;

		if (shape == SQUARE)
		{
			pxy = new PointF[4][4];

			pxy[0][0] = new PointF(0, height / 2f);
			pxy[0][1] = new PointF(width / 2f, height / 2f);
			pxy[0][2] = new PointF(width / 2f, height);
			pxy[0][3] = new PointF(0, height);

			pxy[1][0] = new PointF(0, 0);
			pxy[1][1] = new PointF(width / 2f, 0);
			pxy[1][2] = new PointF(width / 2f, height / 2f);
			pxy[1][3] = new PointF(0, height / 2);

			pxy[2][0] = new PointF(width / 2f, 0);
			pxy[2][1] = new PointF(width, 0);
			pxy[2][2] = new PointF(width, height / 2f);
			pxy[2][3] = new PointF(width / 2f, height / 2f);

			pxy[3][0] = new PointF(width / 2f, height / 2f);
			pxy[3][1] = new PointF(width, height / 2f);
			pxy[3][2] = new PointF(width, height);
			pxy[3][3] = new PointF(width / 2f, height);

			// ... and lines for a cross (+) in the middle
			cross1 = new PointF[2];
			cross1[0] = new PointF(0.25f * width, 0.5f * height);
			cross1[1] = new PointF(0.75f * width, 0.5f * height);

			cross2 = new PointF[2];
			cross2[0] = new PointF(0.5f * width, 0.25f * height);
			cross2[1] = new PointF(0.5f * width, 0.75f * height);
		} else
		// TRIANGULAR
		{
			pxy = new PointF[4][3];

			pxy[0][0] = new PointF(0, 0);
			pxy[0][1] = new PointF(width / 2f, height / 2f);
			pxy[0][2] = new PointF(0, height);

			pxy[1][0] = new PointF(0, 0);
			pxy[1][1] = new PointF(width, 0);
			pxy[1][2] = new PointF(width / 2f, height / 2f);

			pxy[2][0] = new PointF(width, 0);
			pxy[2][1] = new PointF(width, height);
			pxy[2][2] = new PointF(width / 2f, height / 2f);

			pxy[3][0] = new PointF(width, height);
			pxy[3][1] = new PointF(0, height);
			pxy[3][2] = new PointF(width / 2f, height / 2f);

			// ... and lines for a cross (x) in the middle
			cross1 = new PointF[2];
			cross1[0] = new PointF(0.25f * width, 0.25f * height);
			cross1[1] = new PointF(0.75f * width, 0.75f * height);

			cross2 = new PointF[2];
			cross2[0] = new PointF(0.25f * width, 0.75f * height);
			cross2[1] = new PointF(0.75f * width, 0.25f * height);
		}

		// create polygons for each key
		keyPath = new Path[4];
		for (int i = 0; i < 4; ++i)
		{
			keyPath[i] = new Path();
			keyPath[i].moveTo(pxy[i][0].x, pxy[i][0].y);
			keyPath[i].lineTo(pxy[i][1].x, pxy[i][1].y);
			keyPath[i].lineTo(pxy[i][2].x, pxy[i][2].y);
			if (shape == SQUARE)
				keyPath[i].lineTo(pxy[i][3].x, pxy[i][3].y);
			keyPath[i].close();
		}

		// initialize keys (with information computed above)
		if (shape == SQUARE)
		{
			h4key[0] = new H4Key(keyPath[0], width * 0.25f, height * 0.75f, width * 0.40f);
			h4key[1] = new H4Key(keyPath[1], width * 0.25f, height * 0.25f, width * 0.40f);
			h4key[2] = new H4Key(keyPath[2], width * 0.75f, height * 0.25f, width * 0.40f);
			h4key[3] = new H4Key(keyPath[3], width * 0.75f, height * 0.75f, width * 0.40f);
		} else
		{
			h4key[0] = new H4Key(keyPath[0], width * 0.20f, height * 0.50f, width * 0.40f);
			h4key[1] = new H4Key(keyPath[1], width * 0.50f, height * 0.15f, width * 0.50f);
			h4key[2] = new H4Key(keyPath[2], width * 0.80f, height * 0.50f, width * 0.40f);
			h4key[3] = new H4Key(keyPath[3], width * 0.50f, height * 0.85f, width * 0.50f);
		}

		// keyTextPaint.setTextSize(height * width / SCALE_FACTOR);
		keyTextPaint.setTextSize(KEY_TEXT_SIZE * scalingFactor);
		stemCodePaint.setTextSize(STEM_CODE_TEXT_SIZE * scalingFactor);
	}

	@Override
	public boolean onTouchEvent(MotionEvent me)
	{
		if (me.getAction() == MotionEvent.ACTION_UP)
		{
			clearKey();
		} else if (me.getAction() == MotionEvent.ACTION_DOWN)
		{
			xTouch = me.getX();
			yTouch = me.getY();
			h4Listener.onH4Keystroke(); // callback to count keystrokes
			int n = getKey(me.getX(), me.getY()); // get key number (0, 1, 2, 3)
			huffmanStem.append(String.format("%d", n)); // add the key string to the stem

			// if the stem is invalid, don't accept the key code
			if (!valid(huffmanStem.toString()))
			{
				huffmanStem.deleteCharAt(huffmanStem.length() - 1);
			} else if (isPrefix(huffmanStem.toString())) // if prefix, process the huffman code
			{
				String symbol = getHuffmanSymbol(huffmanStem.toString());

				if (symbol != null && symbol.equals(H4Keyboard.SHIFT))
				{
					capOn = true;
				} else if (symbol != null && symbol.equals(H4Keyboard.CAPS_LOCK))
				{
					capLockOn = !capLockOn;
				} else if (symbol != null && symbol.equals(H4Keyboard.SYMBOL))
				{
					symOn = true;
					codes = H4Keyboard.SYMBOL_CODES;
				} else if (symbol != null && symbol.equals(H4Keyboard.SYMBOL_LOCK))
				{
					symOn = false; // just in case last code was SYMBOL_MODE
					symLockOn = !symLockOn;
					if (symLockOn)
						codes = H4Keyboard.SYMBOL_CODES;
					else
						codes = H4Keyboard.LETTER_CODES;
				} else
				{
					if (symOn)
					{
						symOn = false;
						codes = H4Keyboard.LETTER_CODES;
					}
					if (capOn || capLockOn)
					{
						if (symbol.charAt(0) != '[') // don't convert commands to uppercase (e.g.,
														// "[space]")
							symbol = symbol.toUpperCase(Locale.US);
						capOn = false;
					}

					h4Listener.onH4Code(new H4Event(symbol, System.currentTimeMillis()));
				}
				huffmanStem.setLength(0);
				updateHuffmanKeys(huffmanStem.toString());
			} else
			// not a prefix code (just a partial code)
			{
				// symbol = "";
				updateHuffmanKeys(huffmanStem.toString());
			}
		}
		return true;
	}

	protected void onDraw(Canvas c)
	{
		for (int i = 0; i < 4; ++i)
		{
			c.drawPath(h4key[i].p, keyLinePaint);
			drawText(c, keyTextPaint, h4key[i].text, h4key[i].xText, h4key[i].yText, h4key[i].textWidth);
		}

		c.drawLine(cross1[0].x, cross1[0].y, cross1[1].x, cross1[1].y, keyThickLinePaint);
		c.drawLine(cross2[0].x, cross2[0].y, cross2[1].x, cross2[1].y, keyThickLinePaint);

		if (keyStyle == SQUARE)
			c.drawText(huffmanStem.toString(), xOffset, yOffset, stemCodePaint);
		else
			c.drawText(huffmanStem.toString(), xExtraOffset, yOffset, stemCodePaint);

		if (activeKey >= 0)
			c.drawBitmap(featheredTouchPoint, xTouch - offset, yTouch - offset, null);
	}

	private void drawText(Canvas c, Paint p, String text, float x, float y, float width)
	{
		// does the text fit in the available 'width'?
		if (p.measureText(text) <= width) // YES
			c.drawText(text, x, y, p);
		else
		// NO (some finagling needed)
		{
			int rightOfCenterSplitPoint = text.length() / 2;
			while (text.charAt(rightOfCenterSplitPoint) != ' ')
				++rightOfCenterSplitPoint;

			int leftOfCenterSplitPoint = text.length() / 2;
			while (text.charAt(leftOfCenterSplitPoint) != ' ')
				--leftOfCenterSplitPoint;

			int splitPoint = Math.abs(text.length() / 2 - leftOfCenterSplitPoint) < Math.abs(text.length() / 2
					- rightOfCenterSplitPoint) ? leftOfCenterSplitPoint : rightOfCenterSplitPoint;

			String s1 = text.substring(0, splitPoint);
			float scaleFactor = 1.0f;
			while (p.measureText(s1) > width)
			{
				scaleFactor *= .95f;
				p.setTextScaleX(scaleFactor);
			}
			c.drawText(s1, x, y + (p.ascent() / 2f), p);
			p.setTextScaleX(1.0f);
			s1 = text.substring(splitPoint);
			scaleFactor = 1.0f;
			while (p.measureText(s1) > width)
			{
				scaleFactor *= 0.95f;
				p.setTextScaleX(scaleFactor);
			}
			c.drawText(s1, x, y - (2f * p.ascent() / 2f), p);
			p.setTextScaleX(1.0f);
		}
	}

	public int getKey(float xArg, float yArg)
	{
		activeKey = -1;
		for (int i = 0; i < 4 && activeKey == -1; ++i)
			if (contains(pxy[i], xArg, yArg))
				activeKey = i;
		invalidate();
		return activeKey;
	}

	public void clearKey()
	{
		activeKey = -1;
		invalidate();
	}

	private String getHuffmanSymbol(String stem)
	{
		for (int i = 0; i < codes.length; ++i)
		{
			if (codes[i][1].equals(stem))
				return codes[i][0];
		}
		return null;
	}

	private boolean isPrefix(String stem)
	{
		for (int i = 0; i < codes.length; ++i)
		{
			if (codes[i][1].equals(stem))
				return true;
		}
		return false;
	}

	private boolean valid(String stem)
	{
		for (int i = 0; i < codes.length; ++i)
		{
			if (codes[i][1].indexOf(stem) == 0)
				return true;
		}
		return false;
	}

	private String getSymbolsForKey(int key, String stemArg)
	{
		String stem = stemArg + key;
		String s = "";
		for (int i = 0; i < codes.length; ++i)
		{
			if (codes[i][1].indexOf(stem) == 0)
				s += codes[i][0] + " ";
		}
		if (capOn || capLockOn)
			return s.toUpperCase(Locale.US).trim();
		else
			return s.trim();
	}

	private void updateHuffmanKeys(String stem)
	{
		/**
		 * Apologies for the complicated boolean. The effect is to show the h4 key text for the
		 * 0-branch (commands and symbols), even if "Show Huffman Codes" is unchecked. The idea is
		 * that even experienced users are likely to need the h4 key text when entering some of the
		 * more esoteric symbols or commands.
		 */
		if (showHuffmanCodes || ((stem.length() > 0 && (stem.charAt(0) == '0'))) || symOn)
		{
			setTextForKeys(getSymbolsForKey(0, stem), getSymbolsForKey(1, stem), getSymbolsForKey(2, stem),
					getSymbolsForKey(3, stem));
		} else
			setTextForKeys("", "", "", "");
	}

	/*
	 * Create a feathered oval to display at the touch contact point. This is based on a
	 * StackOverflow posting. See...
	 * 
	 * http://stackoverflow.com/questions/9954395/how-to-achieve-feathering-effect-in-android
	 */
	private Bitmap getFeatheredTouchPoint(int widthArg, int heightArg)
	{
		// Determine largest dimension, use for rectangle.
		int size = Math.max(widthArg, heightArg);

		RadialGradient gradient = new RadialGradient(size / 2, size / 2, size / 2,
				new int[] { 0xFF4444ff, 0x004444ff }, new float[] { 0.0f, 1.0f },
				android.graphics.Shader.TileMode.CLAMP);
		Paint paint = new Paint();
		paint.setShader(gradient);

		Bitmap bitmap = Bitmap.createBitmap(size, size, Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(size / 2, size / 2, size / 2, paint);
		return Bitmap.createScaledBitmap(bitmap, widthArg, heightArg, true);
	}

	public void setTextForKeys(String s0, String s1, String s2, String s3)
	{
		h4key[0].text = s0;
		h4key[1].text = s1;
		h4key[2].text = s2;
		h4key[3].text = s3;
		invalidate();
	}

	public boolean contains(PointF[] pxy, float x, float y)
	{
		// this is the "crossing number algorithm"
		int polySides = pxy.length;
		boolean oddTransitions = false;
		for (int i = 0, j = polySides - 1; i < polySides; j = i++)
			if ((pxy[i].y < y && pxy[j].y >= y) || (pxy[j].y < y && pxy[i].y >= y))
				if (pxy[i].x + (y - pxy[i].y) / (pxy[j].y - pxy[i].y) * (pxy[j].x - pxy[i].x) < x)
					oddTransitions = !oddTransitions;
		return oddTransitions;
	}

	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		setMeasuredDimension(measureWidth(widthMeasureSpec), measureHeight(heightMeasureSpec));
	}

	private int measureWidth(int widthMeasureSpec)
	{
		return width;
	}

	private int measureHeight(int heightMeasureSpec)
	{
		return height;
	}

	// ========================================================
	// Simple class to hold the information for a single H4 key
	// ========================================================
	class H4Key
	{
		Path p;
		float xText, yText, textWidth;
		String text;

		H4Key(Path pArg, float xTextArg, float yTextArg, float textWidthArg)
		{
			p = pArg;
			xText = xTextArg;
			yText = yTextArg;
			textWidth = textWidthArg;
		}
	}

	// ==============================================================================================
	// Interface to pass H4 event data to an Activity. An Activity using a H4Keyboard must...
	// 1. Include "implements H4Keyboard.OnH4Listener" in its signature
	// 2. Implement the onH4Code & onH4Keystroke call back methods (see below)
	// 3. Include "setOnH4Listener(this)" in the onCreate method
	// ==============================================================================================
	public interface H4Listener
	{
		// Called when a complete huffman code is entered, other than a mode shift (CAPS, CAPS_LOCK,
		// SYMBOL, SYMBOL_LOCK)
		void onH4Code(H4Event h4e);

		// Called for each keystroke (in case the Activity wants to count keystrokes/taps)
		void onH4Keystroke();
	}

	// ===========================================================
	// Define the event class for the data to pass to the Activity
	// ===========================================================
	public class H4Event
	{
		String symbol;
		long timeStamp;

		H4Event(String symbolArg, long timeStampArg)
		{
			symbol = symbolArg;
			timeStamp = timeStampArg;
		}
	}
}
