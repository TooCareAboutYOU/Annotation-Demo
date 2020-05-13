package com.example.administrator.annotationdemo;

import java.util.List;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface GithubService {
    /**
     * Like this
     * https://api.github.com/users/pwittchen/repos
     */
    @GET("users/{user}/repos")
    Single<List<UserRepos>> getListRepos(@Path("user") String user);
}
