package com.jiek.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.jiek.jeventbus.Subscribe;
import com.jiek.jeventbus.ThreadMode;

public class MainActivity extends BaseActivity {

    TextView received_tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        received_tv = findViewById(R.id.received_tv);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                startActivity(new Intent(MainActivity.this, Page2Activity.class));
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    void jeb_mainReceived(String msg) {
        received_tv.setText(msg);
    }

}
