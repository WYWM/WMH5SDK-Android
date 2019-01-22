package com.netease.h5sdkdemo;

import android.content.Context;
import android.content.Intent;
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

public class NoAccountSystemReadWapActivity extends AppCompatActivity {

    private ReadWebView mReadWebView;

    public static void startNoAccountSystemReadWapActivity(Context context, String url, String appChannel) {
        Intent intent = new Intent(context, NoAccountSystemReadWapActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("appChannel", appChannel);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String appChannel = intent.getStringExtra("appChannel");

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
        if (NoAccountSystemStorage.sSDKAuth == null) {
            mReadWebView.startLoad(url, appChannel, null);
        } else {
            mReadWebView.startLoad(url, appChannel, NoAccountSystemStorage.sSDKAuth);
        }
    }

    private IReadWapCallback mReadWapCallback = new IReadWapCallback() {
        @Override
        public void doLogin(ISetSDKAuthListener setSDKAuthListener) {
            // 无帐号体系的app不用实现该方法
        }

        @Override
        public void saveSDKAuth(String SDKAuth) {
            // 保存SDKAuth
            NoAccountSystemStorage.sSDKAuth = SDKAuth;
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
