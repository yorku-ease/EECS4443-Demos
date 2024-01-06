package ca.yorku.eecs.mack.demosettings;

import android.os.Bundle;

import android.preference.PreferenceFragment;

public class MyPreferenceFragment extends PreferenceFragment
{
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}
}
