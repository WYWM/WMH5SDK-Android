package com.netease.h5sdkdemo;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.netease.readwap.IHandlerCallback;
import com.netease.readwap.IReadWapCallback;
import com.netease.readwap.IRegisterNativeFunctionCallback;
import com.netease.readwap.ISetSDKAuthListener;
import com.netease.readwap.view.ReadWebView;

public class ReadWapActivity extends AppCompatActivity {

    private ReadWebView mReadWebView;
    private ISetSDKAuthListener mISetSDKAuthListener;
    private boolean mIsYDH5;
    private String mAppChannel;
    private String mSDKAuth;

    public static void startReadWapActivity(Context context, boolean isAnonymous, String url) {
        Intent intent = new Intent(context, ReadWapActivity.class);
        intent.putExtra("isAnonymous", isAnonymous);
        intent.putExtra("url", url);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        boolean isAnonymous = intent.getBooleanExtra("isAnonymous", true);
        String url = intent.getStringExtra("url");

        mIsYDH5 = url.contains(Constants.YD_URL);
        mAppChannel = mIsYDH5 ? Constants.YD_APP_CHANNEL : Constants.MH_APP_CHANNEL;
        mSDKAuth = mIsYDH5 ? Account.sYDSDKAuth : Account.sMHSDKAuth;

        TextView closeTV = findViewById(R.id.tv_close);
        closeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mReadWebView = findViewById(R.id.readwebView);

        // 设置IReadWapCallback
        mReadWebView.setReadWapCallback(mReadWapCallback);
        // 注册本地接口
        mReadWebView.registerNativeFunction(Constants.NATIVE_FUNCTION, mRegisterNativeFunctionCallback);
        // 开始加载
        if (isAnonymous) {
            mReadWebView.startLoad(url, mAppChannel, null);
        } else {
            mReadWebView.startLoad(url, mAppChannel, mSDKAuth);
        }
    }

    private IReadWapCallback mReadWapCallback = new IReadWapCallback() {
        @Override
        public void doLogin(ISetSDKAuthListener setSDKAuthListener) {
            mISetSDKAuthListener = setSDKAuthListener;
            LoginActivity.startLoginActivityForResult(ReadWapActivity.this, LoginActivity.REQUEST_CODE);
        }
    };

    private IRegisterNativeFunctionCallback mRegisterNativeFunctionCallback = new IRegisterNativeFunctionCallback() {
        @Override
        public void onHandle(String handlerName, String value, IHandlerCallback handlerCallback) {
            String result = handle(handlerName, value);
            handlerCallback.onCallback(result);
        }
    };

    private String handle(String handlerName, String value) {
        if (handlerName.equals(Constants.NATIVE_FUNCTION)) {
            // 调用本地方法的具体实现
        }

        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LoginActivity.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // 获取sdkAuth
                new GetSDKAuthTask().execute();
            }
        }
    }

    class GetSDKAuthTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Account.sYDSDKAuth = Constants.YD_SDK_AUTH;
            Account.sMHSDKAuth = Constants.MH_SDK_AUTH;
            mSDKAuth = mIsYDH5 ? Account.sYDSDKAuth : Account.sMHSDKAuth;

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (mISetSDKAuthListener != null) {
                mISetSDKAuthListener.setSDKAuth(mSDKAuth);
            }
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mReadWebView.canGoBack()) {
                mReadWebView.goBack();

                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

}
