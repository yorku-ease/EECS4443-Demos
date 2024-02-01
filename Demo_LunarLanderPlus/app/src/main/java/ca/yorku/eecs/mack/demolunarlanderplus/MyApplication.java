package ca.yorku.eecs.mack.demolunarlanderplus;

import android.app.Application;
import android.os.Bundle;

/**
 * MyApplication - Extended Application class to save/restore the game setup and state when the game
 * is stopped, but not destroyed. The setBundle method is called from the main activity's onStop
 * method. The getBundle method is called from the main activity's onRestart method. Note that the
 * onRestart method is only called if the app is being launched having previously been stopped but
 * not destroyed.
 * <p>
 *
 * See <a href="http://developer.android.com/guide/components/activities.html">Activities</a>
 * <p>
 *
 * @author (c) Scott MacKenzie, 2013
 *
 */
public class MyApplication extends Application
{
	Bundle b;

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	public void setBundle(Bundle bArg)
	{
		b = bArg;
	}

	public Bundle getBundle()
	{
		return b;
	}
}
