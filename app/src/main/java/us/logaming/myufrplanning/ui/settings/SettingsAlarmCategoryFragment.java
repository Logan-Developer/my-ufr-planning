package us.logaming.myufrplanning.ui.settings;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.SetAlarmWorker;
import us.logaming.myufrplanning.data.utils.AlarmUtils;

public class SettingsAlarmCategoryFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private SwitchMaterial switchGlobal, switch8Am, switch9h30Am, switch11Am, switchOther;
    private MaterialTextView textSummaryAlarm8Am, textSummaryAlarm9h30Am, textSummaryAlarm11Am, textSummaryAlarmOther;
    private LinearLayout layoutAlarms;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_settings_alarm_category, container, false);

        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());

        this.switchGlobal = root.findViewById(R.id.switch_alarm_global);
        this.switch8Am = root.findViewById(R.id.switch_alarm_8am);
        this.switch9h30Am = root.findViewById(R.id.switch_alarm_9h30am);
        this.switch11Am = root.findViewById(R.id.switch_alarm_11am);
        this.switchOther = root.findViewById(R.id.switch_alarm_other);
        this.textSummaryAlarm8Am = root.findViewById(R.id.text_alarm_summary_8am);
        this.textSummaryAlarm9h30Am = root.findViewById(R.id.text_alarm_summary_9h30am);
        this.textSummaryAlarm11Am = root.findViewById(R.id.text_alarm_summary_11am);
        this.textSummaryAlarmOther = root.findViewById(R.id.text_alarm_summary_other);
        this.layoutAlarms = root.findViewById(R.id.layout_settings_alarms);

        retrieveAllFieldsValue();

        this.switchGlobal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.layoutAlarms.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_global_key), isChecked).apply();
            if (isChecked) {
                if (!Settings.canDrawOverlays(getContext())) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + requireContext().getPackageName()))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), getString(R.string.notifications_channel_permissions_id))
                            .setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(getString(R.string.notifications_display_over_other_apps_title))
                            .setContentText(getString(R.string.notifications_display_over_other_apps_text_short))
                            .setStyle(new NotificationCompat.BigTextStyle()
                                    .bigText(getString(R.string.notifications_display_over_other_apps_text_long)))
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(pendingIntent)
                            .setAutoCancel(true);

                    NotificationManagerCompat.from(requireContext()).notify(1, builder.build());
                }
                refreshSetAlarmWorker();
            }
            else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(getString(R.string.set_alarm_worker_name));
                sharedPreferences.edit().putLong(getString(R.string.preference_next_alarm_timestamp_key), 0).apply();
            }
        });

        this.switch8Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_8am_key), isChecked).apply();
            refreshSetAlarmWorker();
        });
        this.switch9h30Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_9h30am_key), isChecked).apply();
            refreshSetAlarmWorker();
        });
        this.switch11Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_11am_key), isChecked).apply();
            refreshSetAlarmWorker();
        });
        this.switchOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_other_key), isChecked).apply();
            refreshSetAlarmWorker();
        });

        Calendar calendar = Calendar.getInstance();

        root.findViewById(R.id.timepicker_alarm_8am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(AlarmUtils.getClockFormat(requireContext()))
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), calendar.get(Calendar.HOUR_OF_DAY)))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_minutes_key), calendar.get(Calendar.MINUTE)))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = AlarmUtils.buildTime(requireContext(), timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm8Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_8am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_8am_minutes_key), timePicker.getMinute())
                        .apply();
                refreshSetAlarmWorker();
            });
        });

        root.findViewById(R.id.timepicker_alarm_9h30am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(AlarmUtils.getClockFormat(requireContext()))
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), calendar.get(Calendar.HOUR_OF_DAY)))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), calendar.get(Calendar.MINUTE)))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = AlarmUtils.buildTime(requireContext(), timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm9h30Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_9h30am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), timePicker.getMinute())
                        .apply();
                refreshSetAlarmWorker();
            });
        });

        root.findViewById(R.id.timepicker_alarm_11am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(AlarmUtils.getClockFormat(requireContext()))
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), calendar.get(Calendar.HOUR_OF_DAY)))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_minutes_key), calendar.get(Calendar.MINUTE)))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = AlarmUtils.buildTime(requireContext(), timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm11Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_11am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_11am_minutes_key), timePicker.getMinute())
                        .apply();
                refreshSetAlarmWorker();
            });
        });

        root.findViewById(R.id.timepicker_alarm_other).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(AlarmUtils.getClockFormat(requireContext()))
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), calendar.get(Calendar.HOUR_OF_DAY)))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_minutes_key), calendar.get(Calendar.MINUTE)))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = AlarmUtils.buildTime(requireContext(), timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarmOther.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_other_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_other_minutes_key), timePicker.getMinute())
                        .apply();
                refreshSetAlarmWorker();
            });
        });

        return root;
    }

    private void retrieveAllFieldsValue() {
        this.switchGlobal.setChecked(this.sharedPreferences.getBoolean(getString(R.string.preference_enable_alarm_global_key), false));
        this.switch8Am.setChecked(this.sharedPreferences.getBoolean(getString(R.string.preference_enable_alarm_8am_key), false));
        this.switch9h30Am.setChecked(this.sharedPreferences.getBoolean(getString(R.string.preference_enable_alarm_9h30am_key), false));
        this.switch11Am.setChecked(this.sharedPreferences.getBoolean(getString(R.string.preference_enable_alarm_11am_key), false));
        this.switchOther.setChecked(this.sharedPreferences.getBoolean(getString(R.string.preference_enable_alarm_other_key), false));

        this.layoutAlarms.setVisibility(this.switchGlobal.isChecked() ? View.VISIBLE : View.GONE);

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), -1) == -1) {
            this.textSummaryAlarm8Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm8Am.setText(AlarmUtils.buildTime(requireContext(), sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), -1) == -1) {
            this.textSummaryAlarm9h30Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm9h30Am.setText(AlarmUtils.buildTime(requireContext(), sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), -1) == -1) {
            this.textSummaryAlarm11Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm11Am.setText(AlarmUtils.buildTime(requireContext(), sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), -1) == -1) {
            this.textSummaryAlarmOther.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarmOther.setText(AlarmUtils.buildTime(requireContext(), sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_minutes_key), -1)));
        }
    }

    private void refreshSetAlarmWorker() {
        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(SetAlarmWorker.class, 2, TimeUnit.HOURS).build();
        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(getString(R.string.set_alarm_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }
}