package us.logaming.myufrplanning.ui.overview;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;
import java.util.concurrent.Executor;

import us.logaming.myufrplanning.data.repositories.PlanningRepository;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.model.PlanningItem;

public class OverviewViewModel extends ViewModel {
    private final MutableLiveData<List<PlanningItem>> planningItems;
    private final PlanningRepository planningRepository;

    public OverviewViewModel(Context context, Executor executor) {
        this.planningItems = new MutableLiveData<>(null);
        this.planningRepository = new PlanningRepository(context, executor);
    }

    public LiveData<List<PlanningItem>> getPlanningItems() {
        loadPlanningItems();
        return this.planningItems;
    }

    private void loadPlanningItems() {
        this.planningRepository.fetchLatestPlanning(result -> {
            if (result instanceof Result.Success) {
               this.planningItems.postValue(((Result.Success<List<PlanningItem>>) result).data);
            } else {
                this.planningItems.postValue(null);
            }
        }, true);
    }
}
