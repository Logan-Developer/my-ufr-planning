package us.logaming.myufrplanning;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.AlarmClock;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;

import us.logaming.myufrplanning.data.model.PlanningItem;
import us.logaming.myufrplanning.data.repositories.PlanningRepository;
import us.logaming.myufrplanning.data.utils.AlarmUtils;

public class SetAlarmWorker extends Worker {

    private final Context context;
    private final PlanningRepository planningRepository;
    private final SharedPreferences sharedPreferences;

    private final static int NOTIFICATION_ID_UNUSUAL_ALARM = 1;
    private final static int NOTIFICATION_ID_FIRST_COURSE_TOMORROW_CHANGED = 2;

    public SetAlarmWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
        this.planningRepository = new PlanningRepository(context, Executors.newFixedThreadPool(4));
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.context);
    }

    @NonNull
    @Override
    public Result doWork() {
        this.planningRepository.fetchLatestPlanning(result -> {
            if (result instanceof us.logaming.myufrplanning.data.Result.Success) {
                List<PlanningItem> planningItems = ((us.logaming.myufrplanning.data.Result.Success<List<PlanningItem>>) result).data;

                if (sharedPreferences.getLong(context.getString(R.string.preference_next_alarm_timestamp_key), 0) < Calendar.getInstance().getTimeInMillis()
                        || tomorrowFirstCourseChanged(planningItems)) {
                    checkAndStoreTomorrowFirstClass(planningItems);

                    if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_8am_key), false)) {
                        if (setAlarmIfNeeded("8h", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_8am_hour_key), -1),
                                sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_8am_minutes_key), -1))) {
                            return;
                        }
                    }
                    if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_9h30am_key), false)) {
                        if (setAlarmIfNeeded("9h30", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_9h30am_hour_key), -1),
                                sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_9h30am_minutes_key), -1))) {
                            return;
                        }
                    }
                    if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_11am_key), false)) {
                        if (setAlarmIfNeeded("11h", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_11am_hour_key), -1),
                                sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_11am_minutes_key), -1))) {
                            return;
                        }
                    }
                    if (sharedPreferences.getBoolean(context.getString(R.string.preference_enable_alarm_other_key), false)) {
                        setAlarmIfNeeded("other", sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_other_hour_key), -1),
                                sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_other_minutes_key), -1));
                    }
                }
            }
        }, false);
        return Result.success();
    }

    /**
     * Set an alarm if the time is valid and the alarm is not already set
     * @param firstCourseTime the time of the first course
     * @param hourAlarm the hour of the alarm
     * @param minuteAlarm the minute of the alarm
     * @return true if the alarm is set, false otherwise
     */
    private boolean setAlarmIfNeeded(String firstCourseTime, int hourAlarm, int minuteAlarm) {
        if (hourAlarm == -1 || minuteAlarm == -1) {
            return false;
        }
        if (tomorrowFirstCourseMatches(firstCourseTime)) {
            Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM);
            alarmIntent.putExtra(AlarmClock.EXTRA_HOUR, hourAlarm);
            alarmIntent.putExtra(AlarmClock.EXTRA_MINUTES, minuteAlarm);
            alarmIntent.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.context.startActivity(alarmIntent);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            calendar.set(Calendar.HOUR_OF_DAY, determineHourFarthestAlarm() + 1);
            calendar.set(Calendar.MINUTE, minuteAlarm);
            sharedPreferences.edit().putLong(context.getString(R.string.preference_next_alarm_timestamp_key), calendar.getTimeInMillis()).apply();

            return true;
        }
        return false;
    }

    private void checkAndStoreTomorrowFirstClass(List<PlanningItem> planningItems) {
        for (int i = 0; i < planningItems.size(); i++) {
            if (planningItems.get(i).getTitle().contains("Demain")) {
                sharedPreferences.edit().putString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), planningItems.get(i + 1).getHour()).apply();
                return;
            }
        }
        sharedPreferences.edit().putString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), null).apply();
    }

    private boolean tomorrowFirstCourseMatches(String firstCourseTime) {
        if (firstCourseTime.equals("other")) {
            String alarmTime = AlarmUtils.buildTime(this.context, sharedPreferences.getInt(this.context.getString(R.string.preference_time_alarm_other_hour_key), -1),
                    sharedPreferences.getInt(this.context.getString(R.string.preference_time_alarm_other_minutes_key), -1));

            firstCourseTime = sharedPreferences.getString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), null);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, this.context.getString(R.string.notifications_channel_info_id))
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setContentTitle(this.context.getString(R.string.notifications_other_time_alarm_title))
                    .setContentText(this.context.getString(R.string.notifications_other_time_alarm_text_short, firstCourseTime, alarmTime))
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(this.context.getString(R.string.notifications_other_time_alarm_text_long, firstCourseTime, alarmTime)))
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);

            NotificationManagerCompat.from(this.context).notify(NOTIFICATION_ID_UNUSUAL_ALARM, builder.build());
            return true;
        }
        return sharedPreferences.getString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), null).startsWith(firstCourseTime);
    }

    private String getTomorrowDate(List<PlanningItem> planningItems) {
        for (int i = 0; i < planningItems.size(); i++) {
            if (planningItems.get(i).getTitle().contains("Demain")) {
                return planningItems.get(i).getTitle();
            }
        }
        return null;
    }

    private int determineHourFarthestAlarm() {
        int hourAlarm8AM = sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_8am_hour_key), -1);
        int hourAlarm9h30AM = sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_9h30am_hour_key), -1);
        int hourAlarm11AM = sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_11am_hour_key), -1);
        int hourAlarmOther = sharedPreferences.getInt(context.getString(R.string.preference_time_alarm_other_hour_key), -1);

        // See which alarm is the farthest in the future
        if (hourAlarm8AM > hourAlarm9h30AM && hourAlarm8AM > hourAlarm11AM && hourAlarm8AM > hourAlarmOther) {
            return hourAlarm8AM;
        }
        if (hourAlarm9h30AM > hourAlarm8AM && hourAlarm9h30AM > hourAlarm11AM && hourAlarm9h30AM > hourAlarmOther) {
            return hourAlarm9h30AM;
        }
        if (hourAlarm11AM > hourAlarm8AM && hourAlarm11AM > hourAlarm9h30AM && hourAlarm11AM > hourAlarmOther) {
            return hourAlarm11AM;
        }
        return hourAlarmOther;
    }

    private boolean tomorrowFirstCourseChanged(List<PlanningItem> planningItems) {
        String tomorrowDate = getTomorrowDate(planningItems);
        String tomorrowDateStoredBefore = sharedPreferences.getString("tomorrow_date", tomorrowDate);

        String firstClassTomorrowStoredBefore = sharedPreferences.getString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), null);
        checkAndStoreTomorrowFirstClass(planningItems);
        String firstClassTomorrowCurrent = sharedPreferences.getString(context.getString(R.string.preference_alarm_first_class_tomorrow_key), null);

        if (firstClassTomorrowStoredBefore.equals(firstClassTomorrowCurrent) || !Objects.equals(tomorrowDate, tomorrowDateStoredBefore)) {
            return false;
        }

        // Inform the user that the first course has changed tomorrow by sending a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, this.context.getString(R.string.notifications_channel_info_id))
                .setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle(this.context.getString(R.string.notifications_first_class_tomorrow_changed_title))
                .setContentText(this.context.getString(R.string.notifications_first_class_tomorrow_changed_text_short, firstClassTomorrowStoredBefore, firstClassTomorrowCurrent))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(this.context.getString(R.string.notifications_first_class_tomorrow_changed_text_long, firstClassTomorrowStoredBefore, firstClassTomorrowCurrent)))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat.from(this.context).notify(NOTIFICATION_ID_FIRST_COURSE_TOMORROW_CHANGED, builder.build());

        return true;
    }
}
