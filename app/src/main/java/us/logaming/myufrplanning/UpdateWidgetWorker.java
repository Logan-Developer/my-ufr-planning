package us.logaming.myufrplanning;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import us.logaming.myufrplanning.ui.planningwidget.PlanningWidgetProvider;

public class UpdateWidgetWorker extends Worker {

    private final Context context;

    public UpdateWidgetWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {

        Intent intent = new Intent(this.context, PlanningWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);

        int[] ids = AppWidgetManager.getInstance(this.context).getAppWidgetIds(new ComponentName(this.context, PlanningWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        this.context.sendBroadcast(intent);

        return Result.success();
    }
}
