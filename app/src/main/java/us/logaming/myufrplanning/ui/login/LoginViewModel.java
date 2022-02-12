package us.logaming.myufrplanning.ui.login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.concurrent.Executor;

import us.logaming.myufrplanning.data.repositories.LoginRepository;
import us.logaming.myufrplanning.data.Result;
import us.logaming.myufrplanning.data.model.LoggedInUserItem;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<LoggedInUserItem> loggedInUserItem;
    private final LoginRepository loginRepository;

    public LoginViewModel(Executor executor) {
        this.loggedInUserItem = new MutableLiveData<>(null);
        this.loginRepository = new LoginRepository(executor);
    }

    public LiveData<LoggedInUserItem> getUser(String username, String password) {
        loadUserItem(username, password);
        return this.loggedInUserItem;
    }

    private void loadUserItem(String username, String password) {
        this.loginRepository.login(result -> {
            if (result instanceof Result.Success) {
                this.loggedInUserItem.postValue(((Result.Success<LoggedInUserItem>) result).data);
            } else {
                this.loggedInUserItem.postValue(null);
            }
        }, username, password);
    }
}