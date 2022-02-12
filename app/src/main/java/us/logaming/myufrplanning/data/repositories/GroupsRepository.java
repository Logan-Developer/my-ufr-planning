package us.logaming.myufrplanning.data.repositories;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.RepositoryCallback;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.datasources.GroupsRemoteDataSource;
import us.logaming.myufrplanning.data.model.GroupItem;

public class GroupsRepository {
    private final GroupsRemoteDataSource remoteDataSource;
    private final Executor executor;
    private final Context context;

    public GroupsRepository(Context context, Executor executor) {
        this.remoteDataSource = new GroupsRemoteDataSource(context);
        this.executor = executor;
        this.context = context;
    }

    public void fetchGroups(final RepositoryCallback<List<GroupItem>> callback, String groupId) {
        this.executor.execute(() -> {
            try {
                Result<List<GroupItem>> result = fetchGroupsSynchronous(groupId);
                callback.onComplete(result);
            } catch (Exception e) {
                Result<List<GroupItem>> errorResult = new Result.Error<>(e);
                callback.onComplete(errorResult);
            }
        });
    }

    private Result<List<GroupItem>> fetchGroupsSynchronous(String groupId) {
        String groupsString = this.remoteDataSource.fetchGroup(groupId);

        if (groupsString.equals("")) {
            return new Result.Error<>(new Exception(""));
        }
        List<GroupItem> groupItems = new ArrayList<>();
        String[] elements = groupsString.split(context.getString(R.string.planning_string_delimiter));

        for (String line : elements) {
            String[] splitLine = line.split(";");
            String id = splitLine[1];
            String title = splitLine[2];

            groupItems.add(new GroupItem(id, title));
        }
        return new Result.Success<>(groupItems);
    }
}
