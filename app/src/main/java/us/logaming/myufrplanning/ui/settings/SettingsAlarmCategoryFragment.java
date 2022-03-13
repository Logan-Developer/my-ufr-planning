package us.logaming.myufrplanning.ui.settings;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.SetAlarmWorker;

public class SettingsAlarmCategoryFragment extends Fragment {

    private SharedPreferences sharedPreferences;

    private SwitchMaterial switchGlobal, switch8Am, switch9h30Am, switch11Am, switchOther;
    private MaterialCardView timepicker8Am, timepicker9h30Am, timepicker11Am, timepickerOther;
    private MaterialTextView textSummaryAlarm8Am, textSummaryAlarm9h30Am, textSummaryAlarm11Am, textSummaryAlarmOther;

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
        this.timepicker8Am = root.findViewById(R.id.timepicker_alarm_8am);
        this.timepicker9h30Am = root.findViewById(R.id.timepicker_alarm_9h30am);
        this.timepicker11Am = root.findViewById(R.id.timepicker_alarm_11am);
        this.timepickerOther = root.findViewById(R.id.timepicker_alarm_other);
        this.textSummaryAlarm8Am = root.findViewById(R.id.text_alarm_summary_8am);
        this.textSummaryAlarm9h30Am = root.findViewById(R.id.text_alarm_summary_9h30am);
        this.textSummaryAlarm11Am = root.findViewById(R.id.text_alarm_summary_11am);
        this.textSummaryAlarmOther = root.findViewById(R.id.text_alarm_summary_other);

        retrieveAllFieldsValue();

        this.switchGlobal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_global_key), isChecked).apply();
            if (isChecked) {
                PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(SetAlarmWorker.class, 30, TimeUnit.MINUTES).build();
                WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(getString(R.string.set_alarm_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
            }
            else {
                WorkManager.getInstance(requireContext()).cancelUniqueWork(getString(R.string.set_alarm_worker_name));
                sharedPreferences.edit().putLong(getString(R.string.preference_next_alarm_timestamp_key), 0).apply();
            }
        });

        this.switch8Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timepicker8Am.setEnabled(isChecked);
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_8am_key), isChecked).apply();
        });
        this.switch9h30Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timepicker9h30Am.setEnabled(isChecked);
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_9h30am_key), isChecked).apply();
        });
        this.switch11Am.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timepicker11Am.setEnabled(isChecked);
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_11am_key), isChecked).apply();
        });
        this.switchOther.setOnCheckedChangeListener((buttonView, isChecked) -> {
            timepickerOther.setEnabled(isChecked);
            this.sharedPreferences.edit().putBoolean(getString(R.string.preference_enable_alarm_other_key), isChecked).apply();
        });

        Calendar calendar = Calendar.getInstance();

        root.findViewById(R.id.timepicker_alarm_8am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(getClockFormat())
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), calendar.get(Calendar.HOUR_OF_DAY)))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_minutes_key), calendar.get(Calendar.MINUTE)))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = buildTime(timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm8Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_8am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_8am_minutes_key), timePicker.getMinute())
                        .apply();
            });
        });

        root.findViewById(R.id.timepicker_alarm_9h30am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(getClockFormat())
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), -1))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), -1))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = buildTime(timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm9h30Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_9h30am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), timePicker.getMinute())
                        .apply();
            });
        });

        root.findViewById(R.id.timepicker_alarm_11am).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(getClockFormat())
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), -1))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_minutes_key), -1))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = buildTime(timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarm11Am.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_11am_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_11am_minutes_key), timePicker.getMinute())
                        .apply();
            });
        });

        root.findViewById(R.id.timepicker_alarm_other).setOnClickListener(v -> {
            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(getClockFormat())
                    .setHour(sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), -1))
                    .setMinute(sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_minutes_key), -1))
                    .build();
            timePicker.show(requireActivity().getSupportFragmentManager(), getString(R.string.preference_time_picker_tag));
            timePicker.addOnPositiveButtonClickListener(v1 -> {
                String time = buildTime(timePicker.getHour(), timePicker.getMinute());
                this.textSummaryAlarmOther.setText(time);

                this.sharedPreferences.edit().putInt(getString(R.string.preference_time_alarm_other_hour_key), timePicker.getHour())
                        .putInt(getString(R.string.preference_time_alarm_other_minutes_key), timePicker.getMinute())
                        .apply();
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

        this.timepicker8Am.setEnabled(this.switch8Am.isChecked());
        this.timepicker9h30Am.setEnabled(this.switch9h30Am.isChecked());
        this.timepicker11Am.setEnabled(this.switch11Am.isChecked());
        this.timepickerOther.setEnabled(this.switchOther.isChecked());

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), -1) == -1) {
            this.textSummaryAlarm8Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm8Am.setText(buildTime(sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_8am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), -1) == -1) {
            this.textSummaryAlarm9h30Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm9h30Am.setText(buildTime(sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_9h30am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), -1) == -1) {
            this.textSummaryAlarm11Am.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarm11Am.setText(buildTime(sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_11am_minutes_key), -1)));
        }

        if (sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), -1) == -1) {
            this.textSummaryAlarmOther.setText(getString(R.string.preference_time_undefined));
        }
        else {
            this.textSummaryAlarmOther.setText(buildTime(sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_hour_key), -1),
                    sharedPreferences.getInt(getString(R.string.preference_time_alarm_other_minutes_key), -1)));
        }
    }

    private String buildTime(int hour, int minutes) {
        StringBuilder stringBuilder = new StringBuilder();

        int adaptedHour = hour <= 12 || getClockFormat() == TimeFormat.CLOCK_24H ? hour : hour - 12;

        if (adaptedHour < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(adaptedHour).append(":");

        if (minutes < 10) {
            stringBuilder.append("0");
        }
        stringBuilder.append(minutes);
        if (getClockFormat() == TimeFormat.CLOCK_12H) {
            stringBuilder.append(" ")
                    .append(hour < 12 ? "A.M." : "P.M.");
        }
        return stringBuilder.toString();
    }

    private int getClockFormat() {
        return android.text.format.DateFormat.is24HourFormat(requireContext()) ? TimeFormat.CLOCK_24H : TimeFormat.CLOCK_12H;
    }
}