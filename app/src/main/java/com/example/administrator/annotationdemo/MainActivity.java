package com.example.administrator.annotationdemo;

import android.annotation.SuppressLint;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import com.example.administrator.annotationdemo.base.BaseActivity;
import com.example.administrator.annotationdemo.databinding.ActivityMainBinding;
import com.github.pwittchen.reactivenetwork.library.rx2.Connectivity;
import com.github.pwittchen.reactivenetwork.library.rx2.ConnectivityPredicate;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.trello.lifecycle2.android.lifecycle.AndroidLifecycle;
import com.trello.rxlifecycle3.LifecycleProvider;

import java.util.List;

import androidx.lifecycle.Lifecycle;
import io.reactivex.SingleSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * @author zhangshuai
 */
public class MainActivity extends BaseActivity<ActivityMainBinding> {

    public static final String TAG = "MMainActivity";

    private final LifecycleProvider<Lifecycle.Event> mProvider= AndroidLifecycle.createLifecycleProvider(this);

    @Override
    public int setLayoutResId() {
        return R.layout.activity_main;
    }

    @SuppressLint("CheckResult")
    @Override
    public void init() {

        mBinding.acBtnOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReactiveNetwork.observeNetworkConnectivity(MainActivity.this)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(ConnectivityPredicate.hasState(NetworkInfo.State.CONNECTED))
                        .filter(ConnectivityPredicate.hasType(NetworkCapabilities.NET_CAPABILITY_SUPL))
                        .compose(mProvider.<Connectivity>bindToLifecycle())
                        .subscribe(new Consumer<Connectivity>() {
                            @Override
                            public void accept(Connectivity connectivity) throws Exception {
                                Log.i(TAG, "打印: " + connectivity.toString());

                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.e(TAG, "accept: " + throwable.toString());
                            }
                        });
            }
        });

        mBinding.acBtnTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReactiveNetwork.observeNetworkConnectivity(MainActivity.this)
                        .flatMapSingle(new Function<Connectivity, SingleSource<List<UserRepos>>>() {
                            @Override
                            public SingleSource<List<UserRepos>> apply(Connectivity connectivity) throws Exception {
                                return requetData().getListRepos("pwittchen");
                            }
                        })
                        .map(new Function<List<UserRepos>, String>() {
                            @Override
                            public String apply(List<UserRepos> userRepos) throws Exception {
                                return userRepos.toString();
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .compose(mProvider.<String>bindToLifecycle())
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(String data) throws Exception {
                                Log.i(TAG, "网络数据: " + data.toString());
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                Log.i(TAG, "请求异常: " + throwable.getMessage());
                            }
                        });
            }
        });

    }
}
