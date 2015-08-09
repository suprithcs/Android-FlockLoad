package com.flockload.flockload;

import android.os.Bundle;
import com.suprith.flockload.R;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;

public class PrefsFragment extends PreferenceFragment {
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);

                // Load the preferences from an XML resource
                addPreferencesFromResource(R.xml.preferences);
    }
}