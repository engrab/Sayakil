package com.oman.sayakil.ui.drawer_fragments;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.oman.sayakil.R;

public class SettingsFragmentPref extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}