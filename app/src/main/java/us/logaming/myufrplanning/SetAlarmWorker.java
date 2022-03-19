package us.logaming.myufrplanning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import us.logaming.myufrplanning.data.model.PlanningItem;
import us.logaming.myufrplanning.data.repositories.PlanningRepository;

public class SetAlarmWorker extends Worker {

    private final Context context;
    private final PlanningRepository planningRepository;
    private final SharedPreferences sharedPreferences;

    public SetAlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.planningRepository = new PlanningRepository(context, Executors.newFixedThreadPool(4));
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    @NonNull
    @Override
    public Result doWork() {
        if (sharedPreferences.getLong(context.getString(R.string.preference_next_alarm_timestamp_key), 0) < Calendar.getInstance().getTimeInMillis()) {
            if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_8am_key), false)) {
                setAlarmIfNeeded("8h", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_8am_hour_key), -1),
                        sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_8am_minutes_key), -1));
            }
            if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_9h30am_key), false)) {
                setAlarmIfNeeded("9h30", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_9h30am_hour_key), -1),
                        sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_9h30am_minutes_key), -1));
            }
            if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_11am_key), false)) {
                setAlarmIfNeeded("11h", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_11am_hour_key), -1),
                        sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_11am_minutes_key), -1));
            }
            if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_other_key), false)) {
                setAlarmIfNeeded("other", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_other_hour_key), -1),
                        sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_other_minutes_key), -1));
            }
        }
        return Result.success();
    }

    private void setAlarmIfNeeded(String firstCourseTime, int hourAlarm, int minuteAlarm) {
        this.planningRepository.fetchLatestPlanning(result -> {
            if (result instanceof us.logaming.myufrplanning.data.Result.Success) {
                List<PlanningItem> planningItems = ((us.logaming.myufrplanning.data.Result.Success<List<PlanningItem>>) result).data;

                if (tomorrowFirstCourseMatches(planningItems, firstCourseTime)) {
                    Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hourAlarm);
                    alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minuteAlarm);
                    alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.context.startActivity(alarmIntent);

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                    calendar.set(Calendar.HOUR, hourAlarm + 1);
                    calendar.set(Calendar.MINUTE, minuteAlarm);

                    sharedPreferences.edit().putLong(context.getString(R.string.preference_next_alarm_timestamp_key), calendar.getTimeInMillis()).apply();
                }
            }
        }, false);
    }

    private boolean tomorrowFirstCourseMatches(List<PlanningItem> planningItems, String firstCourseTime) {
        for (int i = 0; i < planningItems.size(); i++) {
            if (planningItems.get(i).getTitle().contains("Demain")) {
                return planningItems.get(i + 1).getHour().startsWith(firstCourseTime);
            }
        }
        return false;
    }
}
