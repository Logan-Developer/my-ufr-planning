package us.logaming.myufrplanning.data.repositories;

import java.util.concurrent.Executor;

import us.logaming.myufrplanning.data.RepositoryCallback;
import us.logaming.myufrplanning.data.datasources.LoginRemoteDataSource;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.model.LoggedInUserItem;

public class LoginRepository {

    private final LoginRemoteDataSource remoteDataSource;
    private final Executor executor;

    public LoginRepository(Executor executor) {
        this.remoteDataSource = new LoginRemoteDataSource();
        this.executor = executor;
    }

    public void login(final RepositoryCallback<LoggedInUserItem> callback, String username, String password) {
        this.executor.execute(() -> {
            try {
                Result<LoggedInUserItem> result = loginSynchronous(username, password);
                callback.onComplete(result);
            } catch (Exception e) {
                Result<LoggedInUserItem> errorResult = new Result.Error<>(e);
                callback.onComplete(errorResult);
            }
        });
    }

    public Result<LoggedInUserItem> loginSynchronous(String username, String password) {
        String result = this.remoteDataSource.login(username, password);
        String[] info = result.split(";");
        String firstChar = info[0];

        if (firstChar.equals("4")) {
            return new Result.Error<>(new Exception("Incorrect login"));
        }
        String userId = info[1];
        String connectionToken = info[2];
        String displayName = info[3];

        return new Result.Success<>(new LoggedInUserItem(userId, connectionToken, displayName));
    }
}