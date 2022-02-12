package us.logaming.myufrplanning.ui.overview;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.UpdateWidgetWorker;
import us.logaming.myufrplanning.data.model.PlanningItem;

public class OverviewFragment extends Fragment {

    private OverviewViewModel viewModel;
    private RecyclerView planningRecyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Observer<List<PlanningItem>> planningItemsObserver;

    private LinearLayout emptyView;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.overview_fragment, container, false);

        this.emptyView = root.findViewById(R.id.empty_view_planning);

        this.swipeRefreshLayout = root.findViewById(R.id.swipe_container_overview);
        this.swipeRefreshLayout.setOnRefreshListener(this::fetchPlanning);

        this.planningRecyclerView = root.findViewById(R.id.recycler_view_planning);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        PlanningAdapter planningAdapter = new PlanningAdapter(getContext(), new ArrayList<>());
        this.planningRecyclerView.setLayoutManager(linearLayoutManager);
        this.planningRecyclerView.setAdapter(planningAdapter);
        this.viewModel = new OverviewViewModel(getContext(), Executors.newFixedThreadPool(4));

        this.planningItemsObserver = planningItems -> {
            if (planningItems != null) {
                this.planningRecyclerView.setVisibility(View.VISIBLE);
                this.emptyView.setVisibility(View.GONE);
                this.planningRecyclerView.setAdapter(new PlanningAdapter(getContext(), planningItems));
                this.swipeRefreshLayout.setRefreshing(false);
            }
            else {
                this.planningRecyclerView.setVisibility(View.GONE);
                this.emptyView.setVisibility(View.VISIBLE);
            }
            this.swipeRefreshLayout.setRefreshing(false);
        };
        this.swipeRefreshLayout.setRefreshing(true);
        this.viewModel.getPlanningItems().observe(getViewLifecycleOwner(), this.planningItemsObserver);
        return root;
    }

    private void fetchPlanning() {
        PreferenceManager.getDefaultSharedPreferences(requireContext()).edit()
                .putLong(getString(R.string.preference_local_planning_last_modification_key), 0)
                .apply();
        this.planningRecyclerView.setAdapter(new PlanningAdapter(getContext(), new ArrayList<>()));
        this.viewModel.getPlanningItems().observe(getViewLifecycleOwner(), this.planningItemsObserver);

        String refreshFrequencyString = PreferenceManager.getDefaultSharedPreferences(requireContext()).getString(getString(R.string.preference_refresh_frequency_key), getString(R.string.preference_refresh_frequency_value_half_hour));
        long refreshFrequency = Long.parseLong(refreshFrequencyString);

        PeriodicWorkRequest periodicWorkRequest = new PeriodicWorkRequest.Builder(UpdateWidgetWorker.class, refreshFrequency, TimeUnit.MILLISECONDS).build();
        WorkManager.getInstance(this.requireContext()).enqueueUniquePeriodicWork(getString(R.string.planning_widget_update_worker_name), ExistingPeriodicWorkPolicy.REPLACE, periodicWorkRequest);
    }
}