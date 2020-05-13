package com.example.administrator.annotationdemo;

import android.annotation.SuppressLint;
import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.blankj.rxbus.RxBus;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.SocketInternetObservingStrategy;
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.strategy.WalledGardenInternetObservingStrategy;

import androidx.appcompat.widget.DialogTitle;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.internal.util.RxRingBuffer;


/**
 * @author zhangshuai
 */
public class AApplication extends Application {

    private static final String TAG = "MMainActivity";
    private static GithubService mService = null;

    public static final String BASE_URL = "https://api.github.com/";
    public static final String URL = "https://flbapi.358fintech.com";

    InternetObservingSettings settings = InternetObservingSettings.builder()
            .host(URL)
            .strategy(new SocketInternetObservingStrategy())
            .httpResponse(200)
            .build();

    @SuppressLint("CheckResult")
    @Override
    public void onCreate() {
        super.onCreate();

        ReactiveNetwork.observeInternetConnectivity(settings)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            initRetrofit();
                            RxBus.getDefault().post(EventConstants.NETWORK_CONNECTED, EventConstants.NETWORK_STATUES);
                        } else {
                            RxBus.getDefault().post(EventConstants.NETWORK_DISCONNECTED, EventConstants.NETWORK_STATUES);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        RxBus.getDefault().post(throwable.getMessage(), EventConstants.NETWORK_STATUES);
                    }
                });
    }

    public static GithubService initRetrofit() {
        if (mService == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            mService = retrofit.create(GithubService.class);
        }
        return mService;
    }

}
