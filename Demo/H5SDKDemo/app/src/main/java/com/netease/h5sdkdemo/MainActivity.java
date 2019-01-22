package com.netease.h5sdkdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button haveAccountSystemBtn = findViewById(R.id.btn_have_account_system);
        haveAccountSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, HaveAccountSystemExampleActivity.class));
            }
        });

        Button noAccountSystemBtn = findViewById(R.id.btn_no_account_system);
        noAccountSystemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NoAccountSystemExampleActivity.class));
            }
        });

    }


}
