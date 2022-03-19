package us.logaming.myufrplanning.ui.choosegroup;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.UpdateWidgetWorker;
import us.logaming.myufrplanning.data.model.GroupItem;
import us.logaming.myufrplanning.ui.firstconfig.FirstConfigWelcomeFragment;
import us.logaming.myufrplanning.ui.login.LoginFragment;
import us.logaming.myufrplanning.ui.settings.SettingsActivity;

public class ChooseGroupFragment extends Fragment implements GroupsAdapter.OnGroupItemClickListener {

    private ChooseGroupViewModel viewModel;
    private RecyclerView groupsRecyclerView;
    private List<GroupItem> groupItems;
    private OnBackPressedCallback onBackPressedCallback;

    public static ChooseGroupFragment newInstance() {
        return new ChooseGroupFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_first_config_choose_group, container, false);

        MaterialButton btnGoBack = root.findViewById(R.id.btn_first_config_go_back);

        this.onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (requireActivity().getLocalClassName().endsWith(SettingsActivity.class.getSimpleName())) {
                    getParentFragmentManager().popBackStack();
                }
                else {
                    getParentFragmentManager().beginTransaction().replace(R.id.container_first_config, FirstConfigWelcomeFragment.newInstance())
                            .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                            .commit();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this.onBackPressedCallback);

        if (requireActivity().getLocalClassName().endsWith(SettingsActivity.class.getSimpleName())) {
            btnGoBack.setVisibility(View.GONE);
        }
        else {
            btnGoBack.setOnClickListener(v -> this.onBackPressedCallback.handleOnBackPressed());
        }

        this.groupItems = new ArrayList<>();

        this.groupsRecyclerView = root.findViewById(R.id.recycler_view_groups);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        GroupsAdapter groupsAdapter = new GroupsAdapter(new ArrayList<>(), this);

        this.groupsRecyclerView.setLayoutManager(linearLayoutManager);
        this.groupsRecyclerView.setAdapter(groupsAdapter);

        this.viewModel = new ChooseGroupViewModel(getContext(), Executors.newFixedThreadPool(4));
        fetchGroups("0");
        return root;
    }

    @Override
    public void OnGroupItemClick(int position) {
        fetchGroups(this.groupItems.get(position).getId());
    }

    public void fetchGroups(String groupId) {
        this.viewModel.getGroups(groupId).observe(getViewLifecycleOwner(), groupItems -> {

            if (groupItems != null) {
                if (groupItems.size() >= 2) {
                    this.groupItems = groupItems;
                    this.groupsRecyclerView.setAdapter(new GroupsAdapter(groupItems, this));
                }
                else if (groupItems.size() == 1){
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(requireContext());
                    sharedPreferences.edit().putString(getString(R.string.preference_group_id_key), groupId).apply();

                    if (requireActivity().getLocalClassName().endsWith(SettingsActivity.class.getSimpleName())) {
                        sharedPreferences.edit().putLong(getString(R.string.preference_local_planning_last_modification_key), 0).apply();
                        String refreshFrequencyString = sharedPreferences.getString(getString(R.string.preference_refresh_frequency_key), getString(R.string.preference_refresh_frequency_entry_half_hour));
                        long refreshFrequency = Long.parseLong(refreshFrequencyString);

                        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdateWidgetWorker.class, refreshFrequency, TimeUnit.MILLISECONDS).build();
                        WorkManager.getInstance(this.requireContext()).enqueueUniquePeriodicWork(getString(R.string.planning_widget_update_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);

                        this.onBackPressedCallback.handleOnBackPressed();
                    }
                    else {
                        getParentFragmentManager().beginTransaction().replace(R.id.container_first_config, LoginFragment.newInstance())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    }
                }
            }
        });
    }
}