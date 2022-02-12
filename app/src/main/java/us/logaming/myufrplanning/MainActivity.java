package us.logaming.myufrplanning;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.MaterialToolbar;

import us.logaming.myufrplanning.ui.firstconfig.FirstConfigActivity;
import us.logaming.myufrplanning.ui.overview.OverviewFragment;
import us.logaming.myufrplanning.ui.settings.SettingsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setTheme();
        startFirstConfigActivityIfNeeded();

        MaterialToolbar toolbar = findViewById(R.id.top_app_bar_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, OverviewFragment.newInstance())
                    .commitNow();
        }

        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.menu_settings) {
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            return false;
        });
    }

    private void setTheme() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String themePreference = sharedPreferences.getString(getString(R.string.preference_theme_key), getString(R.string.preference_theme_value_system));

        if (themePreference.equals(getString(R.string.preference_theme_value_light))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        else if (themePreference.equals(getString(R.string.preference_theme_value_dark))) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        }
    }

    private void startFirstConfigActivityIfNeeded() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean shouldStartFirstConfig = sharedPreferences.getBoolean(getString(R.string.preference_start_first_config_key), true);

        if (shouldStartFirstConfig) {
            Intent intent = new Intent(getApplicationContext(), FirstConfigActivity.class);
            startActivity(intent);
            finish();
        }
    }
}