package us.logaming.myufrplanning.data;

import java.io.BufferedReader;

public interface PlanningAPI {
    BufferedReader fetchLatestPlanning(String groupId, String nbDays, String showCampusSportsReservations);
    BufferedReader fetchLatestPlanning(String groupId, String nbDays, String showCampusSportsReservations, String studentId, String connectionToken);
    BufferedReader fetchGroupOrSubGroup(String groupId);
    BufferedReader connectToAccount(String username, String password);
}
