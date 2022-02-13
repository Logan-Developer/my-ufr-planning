package us.logaming.myufrplanning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.sql.Time;
import java.util.Calendar;
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

        if (sharedPreferences.getLong("next_alarm", 0) < Calendar.getInstance().getTimeInMillis()) {
            if (sharedPreferences.getBoolean("enable_alarm_8am", false)) {
                Object[] timeArray = sharedPreferences.getStringSet("time_alarm_8am", null).toArray();
                setAlarmIfNeeded("8h", sharedPreferences.getInt("time_alarm_8am_hour", -1),
                        sharedPreferences.getInt("time_alarm_8am_minutes", -1));
            }
            if (sharedPreferences.getBoolean("enable_alarm_9h30am", false)) {
                Object[] timeArray = sharedPreferences.getStringSet("time_alarm_9h30am", null).toArray();
                setAlarmIfNeeded("9h30", sharedPreferences.getInt("time_alarm_9h30am_hour", -1),
                        sharedPreferences.getInt("time_alarm_9h30am_minutes", -1));
            }
            if (sharedPreferences.getBoolean("enable_alarm_11am", false)) {
                Object[] timeArray = sharedPreferences.getStringSet("time_alarm_11am", null).toArray();
                setAlarmIfNeeded("11h", sharedPreferences.getInt("time_alarm_11am_hour", -1),
                        sharedPreferences.getInt("time_alarm_11am_minutes", -1));
            }
            if (sharedPreferences.getBoolean("enable_alarm_other", false)) {
                Object[] timeArray = sharedPreferences.getStringSet("time_alarm_other", null).toArray();
                setAlarmIfNeeded("other", sharedPreferences.getInt("time_alarm_other_hour", -1),
                        sharedPreferences.getInt("time_alarm_other_minutes", -1));
            }
        }return Result.success();
    }

    private void setAlarmIfNeeded(String firstCourseTime, int hourAlarm, int minuteAlarm) {
        this.planningRepository.fetchLatestPlanning(result -> {
            if (result instanceof us.logaming.myufrplanning.data.Result.Success) {
                List<PlanningItem> planningItems = ((us.logaming.myufrplanning.data.Result.Success<List<PlanningItem>>) result).data;

                if (planningItems.get(0).getTitle().startsWith("Demain") && (planningItems.get(1).getHour().startsWith(firstCourseTime))) {

                    Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
                    alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hourAlarm);
                    alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minuteAlarm);
                    alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
                    alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    this.context.startActivity(alarmIntent);

                    Calendar calendar = Calendar.getInstance();
                    calendar.set(Calendar.getInstance().get(Calendar.YEAR),
                            Calendar.getInstance().get(Calendar.MONTH),
                            Calendar.getInstance().get(Calendar.DATE + 1), hourAlarm, minuteAlarm);

                    sharedPreferences.edit().putLong("next_alarm", calendar.getTimeInMillis()).apply();
                }
            }
        }, false);
    }
}
