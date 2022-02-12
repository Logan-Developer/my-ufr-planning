package us.logaming.myufrplanning.data.datasources;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;

import us.logaming.myufrplanning.R;
import us.logaming.myufrplanning.data.PlanningAPI;
import us.logaming.myufrplanning.data.PlanningAPIImpl;

public class GroupsRemoteDataSource {
    private final PlanningAPI planningAPI;
    private final Context context;


    public GroupsRemoteDataSource(Context context) {
        this.context = context;
        this.planningAPI = new PlanningAPIImpl();
    }

    public String fetchGroup(String groupId) {
        return this.getStringFromReader(this.planningAPI.fetchGroupOrSubGroup(groupId));
    }

    private String getStringFromReader(BufferedReader reader) {
        StringBuilder stringBuilder = new StringBuilder();

        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                if ((line = reader.readLine()).equals("")) break;
            } catch (IOException ignored) {
            }
            stringBuilder.append(line).append(context.getString(R.string.planning_string_delimiter));
        }
        return stringBuilder.toString();
    }
}
