package com.jiek.app;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.jiek.jeventbus.JEventBus;
import com.jiek.jeventbus.Subscribe;
import com.jiek.jeventbus.ThreadMode;

abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        JEventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        JEventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    private void onRecevice(String msg) {
        l("main: "+msg);
    }

    @Subscribe(threadMode = ThreadMode.ASYNC)
    private void receviceB(String msg) {
        l("async: "+msg);
    }

    private void l(String msg) {
        Log.e(this.getClass().getSimpleName(), msg + " ; " + Thread.currentThread());
    }
}
