package us.logaming.myufrplanning.ui.firstconfig;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import us.logaming.myufrplanning.R;

public class FirstConfigActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_config);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_first_config, FirstConfigWelcomeFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        }
        else {
            super.onBackPressed();
        }
    }
}