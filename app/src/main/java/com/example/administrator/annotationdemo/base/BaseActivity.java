package com.example.administrator.annotationdemo.base;

import android.os.Bundle;
import android.util.Log;

import com.blankj.rxbus.RxBus;
import com.example.administrator.annotationdemo.AApplication;
import com.example.administrator.annotationdemo.EventConstants;
import com.example.administrator.annotationdemo.GithubService;
import com.example.administrator.annotationdemo.MainActivity;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;


/**
 * @author zhangshuai
 */
public abstract class BaseActivity<B extends ViewDataBinding> extends RxAppCompatActivity {

    protected B mBinding = null;

    public abstract int setLayoutResId();

    public abstract void init();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, setLayoutResId());
        init();

        RxBus.getDefault().subscribe(this, EventConstants.NETWORK_STATUES, new RxBus.Callback<String>() {
            @Override
            public void onEvent(String s) {
                if (s.equals(EventConstants.NETWORK_CONNECTED)) {
                    Log.i(MainActivity.TAG, "网络连接: " + s);
                } else if (s.equals(EventConstants.NETWORK_DISCONNECTED)) {
                    Log.e(MainActivity.TAG, "网络断开: " + s);
                } else {
                    Log.e(MainActivity.TAG, "网络状态: " + s);
                }
            }
        });
    }

    protected GithubService requetData() {
        return AApplication.initRetrofit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            mBinding.unbind();
        }
        RxBus.getDefault().unregister(this);
    }
}
