package us.logaming.myufrplanning.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class PlanningAPIImpl implements PlanningAPI {

    private static final String URL_BASE_CHECK_PLANNING = "https://sedna.univ-fcomte.fr/jsp/custom/ufc/wmplanif.jsp";
    private static final String URL_BASE_CHECK_GROUP = "https://sedna.univ-fcomte.fr/jsp/custom/ufc/wmselect.jsp";
    private static final String URL_BASE_CONNECT_ACCOUNT = "https://sedna.univ-fcomte.fr/jsp/custom/ufc/wmconnect.jsp";

    private BufferedReader sendRequestToServer(String url) {
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            URL requestUrl = new URL(url);
            urlConnection = (HttpURLConnection) requestUrl.openConnection();
            urlConnection.setReadTimeout(60000);
            urlConnection.setConnectTimeout(60000);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();

            int responseCode = urlConnection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Invalid response from server: " + responseCode);
            }
            inputStream = urlConnection.getInputStream();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
    }

    @Override
    public BufferedReader fetchLatestPlanning(String groupId, String nbDays, String showCampusSportsReservations, String studentId, String connectionToken) {
        StringBuilder urlBuilder = new StringBuilder(URL_BASE_CHECK_PLANNING + "?id=" + groupId + "&jours=" + nbDays + "&sports=" + showCampusSportsReservations + "&mode=2");
        if (studentId != null && connectionToken != null) {
            urlBuilder.append("&idetu=").append(studentId).append("&connexion=").append(connectionToken);
        }

        return sendRequestToServer(urlBuilder.toString());
    }

    @Override
    public BufferedReader fetchLatestPlanning(String groupId, String nbDays, String showCampusSportsReservations) {
        return fetchLatestPlanning(groupId, nbDays, showCampusSportsReservations, null, null);
    }

    @Override
    public BufferedReader fetchGroupOrSubGroup(String groupId) {
        String url = URL_BASE_CHECK_GROUP + "?id=" + groupId;
        return sendRequestToServer(url);
    }

    @Override
    public BufferedReader connectToAccount(String username, String password) {
        String url = URL_BASE_CONNECT_ACCOUNT + "?login=" + username + "&mdp=" + password;
        return sendRequestToServer(url);
    }
}
