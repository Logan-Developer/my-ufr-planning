package us.logaming.myufrplanning.data;

public interface RepositoryCallback<T> {
    void onComplete(Result<T> result);
}

