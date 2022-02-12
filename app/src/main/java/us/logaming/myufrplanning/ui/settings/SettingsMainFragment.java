package us.logaming.myufrplanning.ui.settings;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import us.logaming.myufrplanning.R;

public class SettingsMainFragment extends PreferenceFragmentCompat {

    public static SettingsMainFragment newInstance() {
        return new SettingsMainFragment();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}