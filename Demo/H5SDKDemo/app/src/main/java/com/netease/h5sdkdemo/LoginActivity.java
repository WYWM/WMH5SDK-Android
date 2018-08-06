package com.netease.h5sdkdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class LoginActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 1000;

    public static void startLoginActivityForResult(Activity activity, int requestCode) {
        activity.startActivityForResult(new Intent(activity, LoginActivity.class), requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        TextView testView = findViewById(R.id.login);
        testView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account.sIsAnonymous = false;
                setResult(RESULT_OK);
                finish();
            }
        });
    }

}
