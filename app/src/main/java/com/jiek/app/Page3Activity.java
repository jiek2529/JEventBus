package com.jiek.app;

import android.os.Bundle;
import android.view.View;

import com.jiek.jeventbus.JEventBus;

public class Page3Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page3);
    }

    public void backPage(View view) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                JEventBus.getDefault().post("data from page3");
            }
        }).start();
        this.finish();
    }
}
