package us.logaming.myufrplanning.data.datasources;

import us.logaming.myufrplanning.data.PlanningAPI;
import us.logaming.myufrplanning.data.PlanningAPIImpl;

import java.io.BufferedReader;
import java.io.IOException;

public class LoginRemoteDataSource {
    private final PlanningAPI planningAPI;

    public LoginRemoteDataSource() {
        this.planningAPI = new PlanningAPIImpl();
    }

    public String login(String username, String password) {
        String result = null;

        try {
            BufferedReader bufferedReader = planningAPI.connectToAccount(username, password);
            bufferedReader.readLine();
            result = bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}