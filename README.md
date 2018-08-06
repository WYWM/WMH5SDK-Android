# **文漫H5 android SDK使用说明** 

## 1.概述

网易文漫安卓H5 SDK可以帮助合作方安卓客户端快速接入网易文漫提供的功能丰富的H5网站，具体提供如下支持：

1.提供了一个自定义的WebView，可以按需通过xml或代码添加到界面布局中。

2.通过简单的接口调用就可以实现H5唤起app登录界面和回传帐号信息的功能。

3.内部封装了对微信和支付宝支付的跳转处理，如有其它支付方式也可以通过覆写方法实现。

4.支持添加客户端跟H5之间的自定义接口调用和回调，以支持合作方某些自定义的扩展需求。

合作方通常只需要一个开发人员使用半天时间就能接入这个SDK。 

## 2.使用方法

(1)导入SDK的aar包。在libs下加入WMH5SDK-1.0.0.aar文件，并且修改build.gralde文件，加入如下的配置。 

```groovy
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile(name:'WMH5SDK-1.0.0',ext:'aar')
}
```

(2)在混淆配置中加入 

```
-dontwarn com.netease.jsbridge.**
```

(3)将ReadWebView添加到提供H5网站打开功能的Activity中，可以在xml布局中添加，也可以通过Java代码添加。在Activity的onCreate方法中获取到ReadWebView对象后，如以下示例代码(示例代码均来自Demo工程，下同)调用： 

```java
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
```

## 3.接口说明

ReadWebView提供了3个方法可以调用。 

### (1)startLoad

public void startLoad(String url, String appChannel, String sdkAuth)

该方法用于打开网站。

参数说明：

| 参数名     | 参数类型 | 是否可为空 | 说明                                                   |
| ---------- | -------- | ---------- | ------------------------------------------------------ |
| url        | String   | 否         | 网站地址。                                             |
| appChannel | String   | 否         | 合作方在文漫侧的唯一标识，由文漫提供                   |
| sdkAuth    | String   | 是         | 用户签名信息，从应用服务器获取。传空表示匿名进入页面。 |

调用示例：

```java
if (isAnonymous) {
    mReadWebView.startLoad(url, mAppChannel, null);
} else {
    mReadWebView.startLoad(url, mAppChannel, mSDKAuth);
}
```

### (2)setReadWapCallback

public void setReadWapCallback(IReadWapCallback readWapCallback) 

该方法用于设置IReadWapCallback接口实现类的实例对象。IReadWapCallback中有1个方法需要实现。doLogin方法用于ReadWebView回调发起登录来获取sdk auth,通过调用参数ISetSDKAuthListener的setSDKAuth(String sdkAuth)返回sdkAuth。

参数说明：

| 参数名          | 参数类型         | 是否可为空 | 说明     |
| --------------- | ---------------- | ---------- | -------- |
| readWapCallback | IReadWapCallback | 否         | 回调接口 |

IReadWapCallback的定义如下： 

```java
public interface IReadWapCallback {

    /**
     * 登录
     * @param setSDKAuthListener 用于设置sdkAuth的接口
     */
    void doLogin(ISetSDKAuthListener setSDKAuthListener);
    
}
```

调用示例：

```java
private IReadWapCallback mReadWapCallback = new IReadWapCallback() {
    @Override
    public void doLogin(ISetSDKAuthListener setSDKAuthListener) {
        mISetSDKAuthListener = setSDKAuthListener;
        LoginActivity.startLoginActivityForResult(ReadWapActivity.this, 																	LoginActivity.REQUEST_CODE);
    }
};
```

```java
// 设置IReadWapCallback
readWebView.setReadWapCallback(mReadWapCallback);
```

```java
if (mISetSDKAuthListener != null) {
    mISetSDKAuthListener.setSDKAuth(Account.sSDKAuth);
}
```

### (3)registerNativeFunction 

public void registerNativeFunction(String functionName, IRegisterNativeFunctionCallback registerNativeFunctionCallback) 

该方法用于注册本地方法，供JS调用。第三方如果有特殊定制的页面功能需求，可使用该方法。JS调用后ReadWebView会回调IRegisterNativeFunctionCallback的onHandle方法，通过调用参数IHandlerCallback的onCallback(String result)返回本地方法调用后的返回值。

参数说明：

| 参数名                         | 参数类型                        | 是否可为空 | 说明       |
| ------------------------------ | ------------------------------- | ---------- | ---------- |
| functionName                   | String                          | 否         | 本地方法名 |
| registerNativeFunctionCallback | IRegisterNativeFunctionCallback | 否         | 回调接口   |

IRegisterNativeFunctionCallback的定义如下：

```java
public interface IRegisterNativeFunctionCallback {

    /**
     * 处理JS调用注册的本地接口
     * @param handlerName 本地方法名
     * @param value 方法参数
     * @param handlerCallback 回调接口
     */
    void onHandle(String handlerName, String value, IHandlerCallback handlerCallback);

}
```

调用示例：

```java
private IRegisterNativeFunctionCallback mRegisterNativeFunctionCallback = new IRegisterNativeFunctionCallback() {
    @Override
    public void onHandle(String handlerName, String value, IHandlerCallback 										handlerCallback) {
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
```

```java
// 注册本地接口
readWebView.registerNativeFunction(Constants.NATIVE_FUNCTION, mRegisterNativeFunctionCallback);
```

## 4.**调用流程时序图** 

第三方app带sdk auth启动sdk的时序图如下： 

![image](/Demo/H5SDKDemo/image/loginSD.png)

第三方app不带sdk auth启动sdk的时序图如下： 

![image](/Demo/H5SDKDemo/image/anonymousSD.png)

## 5.拦截url的处理方法

​可以覆写ReadWebView中定义的shouldOverrideUrlLoading(WebView view, String url)方法来处理url。这里需要注意的是，对于微信和支付宝的支付跳转在ReadWebView中已经处理，如果覆写shouldOverrideUrlLoading方法返回true，将不会处理微信和支付宝的支付跳转。 