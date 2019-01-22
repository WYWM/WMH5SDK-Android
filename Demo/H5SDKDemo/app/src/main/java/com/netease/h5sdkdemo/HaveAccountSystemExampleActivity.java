package com.netease.h5sdkdemo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class HaveAccountSystemExampleActivity extends AppCompatActivity {

    private TextView mAccountStateTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_have_account_system_example);

        Button anonymousOpenYDBtn = findViewById(R.id.btn_open_yd_anonymous);
        anonymousOpenYDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYDAnonymous();
            }
        });

        Button notAnonymousYDBtn = findViewById(R.id.btn_open_yd_not_anonymous);
        notAnonymousYDBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYDNotAnonymous();
            }
        });

        Button logoutBtn = findViewById(R.id.btn_logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Account.sIsAnonymous = true;
                mAccountStateTV.setText("帐号状态:未登录");
            }
        });

        mAccountStateTV = findViewById(R.id.tv_account_state);
    }

    private void openYDAnonymous() {
        Log.d(Constants.LOG_TAG, "openAnonymous");
        if (Account.sIsAnonymous) {
            HaveAccountSystemReadWapActivity.startHaveAccountSystemReadWapActivity(this, true, Constants.YD_URL, Constants.YD_APP_HAVE_ACCOUNT_SYSTEM_CHANNEL);
        } else {
            Toast.makeText(this, "请先退出登录", Toast.LENGTH_SHORT).show();
        }
    }

    private void openYDNotAnonymous() {
        Log.d(Constants.LOG_TAG, "openNotAnonymous:" + Account.sIsAnonymous);
        if (Account.sIsAnonymous) {
            LoginActivity.startLoginActivityForResult(this, LoginActivity.REQUEST_CODE);
        } else {
            HaveAccountSystemReadWapActivity.startHaveAccountSystemReadWapActivity(this, false, Constants.YD_URL, Constants.YD_APP_HAVE_ACCOUNT_SYSTEM_CHANNEL);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (Account.sIsAnonymous) {
            mAccountStateTV.setText("帐号状态:未登录");
        } else {
            mAccountStateTV.setText("帐号状态:已登录");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 向app服务器获取SDKAuth
                new GetSDKAuthTask().execute();
            }
        }
    }

    class GetSDKAuthTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Account.sSDKAuth = Constants.YD_SDK_HAVE_ACCOUNT_SYSTEM_AUTH;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            HaveAccountSystemReadWapActivity.startHaveAccountSystemReadWapActivity(HaveAccountSystemExampleActivity.this,
                    false, Constants.YD_URL, Constants.YD_APP_HAVE_ACCOUNT_SYSTEM_CHANNEL);
        }

    }

}
