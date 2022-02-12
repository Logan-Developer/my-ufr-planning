package us.logaming.myufrplanning.ui.settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.UpdateWidgetWorker;

public class SettingsGeneralCategoryFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.general_preferences, rootKey);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Preference themePreference = findPreference(getString(R.string.preference_theme_key));
            if (themePreference != null) {
                themePreference.setVisible(false);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).registerOnSharedPreferenceChangeListener(this);
        setRightLoginState();

    }

    @Override
    public void onPause() {
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        switch (key) {
            case "theme":
                updateTheme(sharedPreferences.getString(key, requireContext().getString(R.string.preference_theme_value_system)));
                break;
            case "showing_period":
                fetchPlanning();
                refreshPlanningUpdateWorker();
                break;
            case "refresh_frequency":
                refreshPlanningUpdateWorker();
                break;
        }
    }

    private void updateTheme(String newValue) {
        switch (newValue) {
            case "theme_light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "theme_dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            default:
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void fetchPlanning() {
        Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).edit().putLong(getString(R.string.preference_local_planning_last_modification_key), 0).apply();
    }

    private void refreshPlanningUpdateWorker() {
        String refreshFrequencyString = Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).getString(getString(R.string.preference_refresh_frequency_key), getString(R.string.preference_refresh_frequency_entry_half_hour));
        long refreshFrequency = Long.parseLong(refreshFrequencyString);

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdateWidgetWorker.class, refreshFrequency, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(this.requireContext()).enqueueUniquePeriodicWork(getString(R.string.planning_widget_update_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }

    private void setRightLoginState() {
        Preference preference = findPreference(getString(R.string.preference_login_key));
        String displayName = Objects.requireNonNull(getPreferenceManager().getSharedPreferences()).getString(getString(R.string.preference_user_display_name_key),
                getString(R.string.preference_user_display_name_default_value));
        if (preference != null) {
            preference.setSummary(displayName);
        }
    }
}