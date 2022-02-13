package us.logaming.myufrplanning.data.repositories;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.RepositoryCallback;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.datasources.PlanningLocalDataSource;
import us.logaming.myufrplanning.data.datasources.PlanningRemoteDataSource;
import us.logaming.myufrplanning.data.model.PlanningItem;

public class PlanningRepository {
    private final PlanningRemoteDataSource remoteDataSource;
    private final PlanningLocalDataSource localDataSource;
    private final Executor executor;
    private final Context context;

    public PlanningRepository(Context context, Executor executor) {
        this.remoteDataSource = new PlanningRemoteDataSource(context);
        this.localDataSource = new PlanningLocalDataSource(context);
        this.context = context;
        this.executor = executor;
    }

    public void fetchLatestPlanning(final RepositoryCallback<List<PlanningItem>> callback, boolean tryToFetchOnlineVersion) {
        this.executor.execute(() -> {
            try {
                Result<List<PlanningItem>> result = fetchLatestPlanningSynchronous(tryToFetchOnlineVersion);
                callback.onComplete(result);
            } catch (Exception e) {
                Result<List<PlanningItem>> errorResult = new Result.Error<>(e);
                callback.onComplete(errorResult);
            }
        });
    }

    private Result<List<PlanningItem>> fetchLatestPlanningSynchronous(boolean tryToFetchOnlineVersion) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String refreshFrequencyString = sharedPreferences.getString(context.getString(R.string.preference_refresh_frequency_key), context.getString(R.string.preference_refresh_frequency_value_half_hour));
        long refreshFrequency = Long.parseLong(refreshFrequencyString);
        long lastModificationDate = sharedPreferences.getLong(context.getString(R.string.preference_local_planning_last_modification_key), 0);

        String planningString = this.localDataSource.fetchLatestPlanning();
        if ((planningString.equals("") || (refreshFrequency != 0 && refreshFrequency <= (Calendar.getInstance().getTimeInMillis() - lastModificationDate))) && isConnectedToInternet()) {
            planningString = this.remoteDataSource.fetchLatestPlanning();
        }

        if (planningString.equals("")) {
            return new Result.Error<>(new Exception(""));
        }
        List<PlanningItem> planningItems = new ArrayList<>();
        String[] elements  = planningString.split(context.getString(R.string.planning_string_delimiter));

        for (String line : elements) {
            PlanningItem planningItem;

            if (Objects.requireNonNull(line).startsWith("*date*")) {
                planningItem = new PlanningItem(line.split(";")[1], null, null, null, false);
            }
            else {
                String[] splitLine = line.split(";");
                String[] firstPart = splitLine[0].split(" : ", 2);
                String locationAndProfessor = splitLine.length > 1 ? splitLine[1] : null;
                String info1;
                String info2 = null;
                if (locationAndProfessor != null && locationAndProfessor.contains(" - ")) {
                    String[] info = locationAndProfessor.split(" - ");
                    info1 = info[0];
                    info2 = info[1];
                }
                else {
                    info1 = locationAndProfessor;
                }

                String hour = firstPart[0];
                String subject = firstPart[1];
                planningItem = new PlanningItem(subject, hour, info1, info2, true);
            }
            planningItems.add(planningItem);
        }
        return new Result.Success<>(planningItems);
    }

    private boolean isConnectedToInternet() {
        final ConnectivityManager connMgr = (ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connMgr != null) {
            NetworkInfo activeNetworkInfo = connMgr.getActiveNetworkInfo();

            if (activeNetworkInfo != null) { // connected to the internet
                // connected to the mobile provider's data plan
                if (activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    return true;
                } else return activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
}
