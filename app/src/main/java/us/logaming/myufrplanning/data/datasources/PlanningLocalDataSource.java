package us.logaming.myufrplanning.data.datasources;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import us.logaming.myufrplanning.R;

public class PlanningLocalDataSource {

    private final Context context;

    public PlanningLocalDataSource(Context context) {
        this.context = context;
    }

    public String fetchLatestPlanning() {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        return sharedPreferences.getString(context.getString(R.string.preference_local_planning_key), "");
    }
}
