package com.jiek.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jiek.jeventbus.JEventBus;

public class Page2Activity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page2);
    }

    public void backPage(View view) {
        JEventBus.getDefault().post("data from page2");
        this.finish();
    }

    public void goPage3(View view) {
        startActivity(new Intent(this, Page3Activity.class));
    }
}
