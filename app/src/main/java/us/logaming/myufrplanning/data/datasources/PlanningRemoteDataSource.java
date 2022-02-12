package us.logaming.myufrplanning.data.datasources;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.PlanningAPI;
import us.logaming.myufrplanning.data.PlanningAPIImpl;

public class PlanningRemoteDataSource {

    private final PlanningAPI planningAPI;
    private final Context context;

    public PlanningRemoteDataSource(Context context) {
        this.context = context;
        this.planningAPI = new PlanningAPIImpl();
    }

    public String fetchLatestPlanning() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
        String nbDays = sharedPreferences.getString(context.getString(R.string.preference_showing_period_key), context.getString(R.string.preference_showing_period_value_two_weeks));
        String groupId = sharedPreferences.getString(context.getString(R.string.preference_group_id_key),"0");
        String userId = sharedPreferences.getString(context.getString(R.string.preference_user_id_key),null);
        String connectionToken = sharedPreferences.getString(context.getString(R.string.preference_connection_token_key),null);

        BufferedReader reader;
        if (userId != null && connectionToken != null) {
            reader = this.planningAPI.fetchLatestPlanning(groupId, nbDays, userId, connectionToken);
        }
        else {
            reader = this.planningAPI.fetchLatestPlanning(groupId, nbDays);
        }
        String planningString = this.getStringFromReader(reader);
        savePlanningLocally(planningString);
        return planningString;
    }

    private String getStringFromReader(BufferedReader reader) {
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if ((line = reader.readLine()).equals("")) break;
            } catch (IOException ignored) {
            }
            stringBuilder.append(line).append(context.getString(R.string.planning_string_delimiter));
        }
        return stringBuilder.toString();
    }

    private void savePlanningLocally(String planningString) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(context.getString(R.string.preference_local_planning_key), planningString);
        editor.putLong(context.getString(R.string.preference_local_planning_last_modification_key), Calendar.getInstance().getTimeInMillis());
        editor.apply();
    }
}
