package us.logaming.myufrplanning.ui.choosegroup;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.Executor;

import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.model.GroupItem;
import us.logaming.myufrplanning.data.repositories.GroupsRepository;

public class ChooseGroupViewModel {
    private final MutableLiveData<List<GroupItem>> groupItems;
    private final GroupsRepository groupsRepository;

    public ChooseGroupViewModel(Context context, Executor executor) {
        this.groupItems = new MutableLiveData<>(null);
        this.groupsRepository = new GroupsRepository(context, executor);
    }

    public LiveData<List<GroupItem>> getGroups(String groupId) {
        loadGroupItems(groupId);
        return this.groupItems;
    }

    private void loadGroupItems(String groupId) {
        this.groupsRepository.fetchGroups(result -> {
            if (result instanceof Result.Success) {
                this.groupItems.postValue(((Result.Success<List<GroupItem>>) result).data);
            } else {
                this.groupItems.postValue(null);
            }
        }, groupId);
    }
}
