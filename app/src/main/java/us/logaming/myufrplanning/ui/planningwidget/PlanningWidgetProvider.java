package us.logaming.myufrplanning.ui.planningwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.RemoteViews;

import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.MainActivity;
import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.UpdateWidgetWorker;

public class PlanningWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "us.logaming.myufrplanning.planningwidget.EXTRA_ITEM";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        Intent intent = new Intent(context, PlanningWidgetService.class);

        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.planning_widget);
        views.setRemoteAdapter(R.id.list_view_planning_widget, intent);
        views.setEmptyView(R.id.list_view_planning_widget, R.id.empty_view);

        Intent clickIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setPendingIntentTemplate(R.id.list_view_planning_widget, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }
    @Override
    public void onEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String refreshFrequencyString = sharedPreferences.getString(context.getString(R.string.preference_refresh_frequency_key), context.getString(R.string.preference_refresh_frequency_value_half_hour));
        long refreshFrequency = Long.parseLong(refreshFrequencyString);

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdateWidgetWorker.class, refreshFrequency, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(context.getString(R.string.planning_widget_update_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
        super.onEnabled(context);
    }
    @Override
    public void onDisabled(Context context) {
        WorkManager.getInstance(context).cancelAllWork();
        super.onDisabled(context);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if (intent.getAction().equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)) {
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int [] appWidgetIds = intent.getIntArrayExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS);
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list_view_planning_widget);

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId);
            }
        }
    }
}