# **文漫H5 android SDK使用说明**

## 1.概述

网易文漫安卓H5 SDK可以帮助合作方安卓客户端快速接入网易文漫提供的功能丰富的H5网站，具体提供如下支持：

1.提供了一个自定义的WebView，可以按需通过xml或代码添加到界面布局中。

2.通过简单的接口调用就可以实现H5唤起app登录界面和回传帐号信息的功能。

3.内部封装了对微信和支付宝支付的跳转处理，如有其它支付方式也可以通过覆写方法实现。

4.支持添加客户端跟H5之间的自定义接口调用和回调，以支持合作方某些自定义的扩展需求。

合作方通常只需要一个开发人员使用半天时间就能接入这个SDK。

SDK和Demo的github地址：https://github.com/WYWM/WMH5SDK-Android

## 2.使用方法

(1)导入SDK的aar包。在libs下加入WMH5SDK-1.0.2.aar文件，并且修改build.gralde文件，加入如下的配置。

```groovy
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    compile(name:'WMH5SDK-1.0.2',ext:'aar')
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
// 设置白天或夜间模式
mReadWebView.setTheme(false);
// 开始加载
if (isAnonymous) {
    mReadWebView.startLoad(url, mAppChannel, null);
} else {
    mReadWebView.startLoad(url, mAppChannel, mSDKAuth);
}
```

## 3.接口说明

ReadWebView提供了4个方法可以调用。

### (1)startLoad

public void startLoad(String url, String appChannel, String sdkAuth)

该方法用于打开网站。

#### 参数说明

| 参数名     | 参数类型 | 是否可为空 | 说明                                                   |
| ---------- | -------- | ---------- | ------------------------------------------------------ |
| url        | String   | 否         | 网站地址。                                             |
| appChannel | String   | 否         | 合作方在文漫侧的唯一标识，由文漫提供                   |
| sdkAuth    | String   | 是         | 用户签名信息，从应用服务器获取。传空表示匿名进入页面。 |

#### 调用示例

有帐号体系的app调用方式:

```java
if (isAnonymous) {
    mReadWebView.startLoad(url, mAppChannel, null);
} else {
    mReadWebView.startLoad(url, mAppChannel, mSDKAuth);
}
```

无帐号体系的app调用方式：

```java
if (NoAccountSystemStorage.sSDKAuth == null) {
    mReadWebView.startLoad(url, appChannel, null);
} else {
    mReadWebView.startLoad(url, appChannel, NoAccountSystemStorage.sSDKAuth);
}
```

### (2)setReadWapCallback

public void setReadWapCallback(IReadWapCallback readWapCallback)

该方法用于设置IReadWapCallback接口实现类的实例对象。IReadWapCallback中的方法可以根据需要实现。

参数说明：

| 参数名          | 参数类型         | 是否可为空 | 说明     |
| --------------- | ---------------- | ---------- | -------- |
| readWapCallback | IReadWapCallback | 否         | 回调接口 |

IReadWapCallback的定义如下：

```java
public interface IReadWapCallback {

    /**
     * 登录，有帐号体系的接入方需实现该方法
     * @param setSDKAuthListener 用于设置sdkAuth的接口
     * @param from 登录来源
     */
    void doLogin(ISetSDKAuthListener setSDKAuthListener, String from);

    /**
     * 保存SDKAuth，用于下次打开时使用，无帐号体系的接入方需实现该方法
     * @param SDKAuth 无帐号体系时可使用的SDKAuth
     */
    void saveSDKAuth(String SDKAuth);

    /**
     * 支付，希望使用应用自己支付方式的app需要实现该方法，完成支付后使用IPayResultListener通知支付结果。如果不用自己支付方式不用实现该方法。
     * @param transactionId 订单号
     * @param amount 金额
     * @param payment 支付方式，0表示支付宝，1表示微信
     * @param payResultListener 支付结果回调接口
     */
    void doPay(String transactionId, int amount, int payment, IPayResultListener payResultListener);

    /**
     * 通知主题切换，如果无此需求不用实现该方法
     * @param isNightMode 是否切换成夜间模式
     */
    void notifyThemeChanged(boolean isNightMode);

    /**
     * 通知当前打开书籍的阅读进度
     * @param bookId 书籍id
     * @param bookName 书籍名称
     * @param progress 阅读进度
     */
    void notifyCurrentBookProgress(String bookId, String bookName, double progress);
}
```

调用示例：

```java
private IReadWapCallback mReadWapCallback = new IReadWapCallback() {
    
    @Override
        public void doLogin(ISetSDKAuthListener setSDKAuthListener, String from) {
            mISetSDKAuthListener = setSDKAuthListener;
          LoginActivity.startLoginActivityForResult(HaveAccountSystemReadWapActivity.this, LoginActivity.REQUEST_CODE);
        }

        @Override
        public void saveSDKAuth(String SDKAuth) {
            NoAccountSystemStorage.sSDKAuth = SDKAuth;
        }

        @Override
        public void doPay(String transactionId, int amount, int payment, IPayResultListener payResultListener) {
            mIPayResultListener = payResultListener;
            pay(transactionId, amount, payment);
        }

        @Override
        public void notifyThemeChanged(boolean isNightMode) {
            changeTheme(isNightMode);
        }

        @Override
        public void notifyCurrentBookProgress(String bookId, String bookName, double progress) {
            setBookProgress(bookId, bookName, progress);
        }
};
```

```java
// 设置IReadWapCallback
readWebView.setReadWapCallback(mReadWapCallback);
```

```java
// 设置SDKAuth
if (mISetSDKAuthListener != null) {
    mISetSDKAuthListener.setSDKAuth(Account.sSDKAuth);
}
```

```java
// 通知支付结果
if (mIPayResultListener != null) {
    // true表示支付成功，false表示支付失败
    mIPayResultListener.onPayResult(true, transactionId);
}
```

### (3)setTheme

public void setTheme(boolean isNightMode)

该方法用于设置H5的主题是白天模式或夜间模式。

参数说明：

| 参数名      | 参数类型 | 是否可为空 | 说明         |
| ----------- | -------- | ---------- | ------------ |
| isNightMode | Boolean  | 否         | 是否夜间模式 |

调用实例：

```java
// 设置白天或夜间模式
mReadWebView.setTheme(false);
```

### (4)registerNativeFunction

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

有帐号体系的第三方app带sdk auth启动sdk的时序图如下：

![image](/Demo/H5SDKDemo/image/loginSD.png)

有帐号体系的第三方app不带sdk auth启动sdk的时序图如下：

![image](/Demo/H5SDKDemo/image/anonymousSD.png)

无帐号体系的第三方app不带sdk auth启动sdk的时序图如下：

![image](/Demo/H5SDKDemo/image/noAccountAndNoSDKAuthSD.png)

无帐号体系的第三方app带sdk auth启动sdk的时序图如下：

![image](/Demo/H5SDKDemo/image/noAccountAndHaveSDKAuthSDK.png)

## 5.拦截url的处理方法

可以覆写ReadWebView中定义的shouldOverrideUrlLoading(WebView view, String url)方法来处理url。这里需要注意的是，对于微信和支付宝的支付跳转在ReadWebView中已经处理，如果覆写shouldOverrideUrlLoading方法返回true，将不会处理微信和支付宝的支付跳转。

可以覆写ReadWebView中定义的onPageFinished(WebView view, String url)方法和onReceivedError(WebView view, int errorCode, String description, String failingUrl)方法进行特定需求的处理。

## 6.FAQ

### Q:为什么在阅读H5充值页面点击微信支付的选项无法调起微信支付？

A:请检查下代码中有没有调用WebView的pauseTimers方法，调用这个方法不仅仅会针对当前的WebView，也会对全应用的WebView起效，如果调用了这个方法就会出现问题中描述的现象。



### Q:如果app在接入SDK的时候发现有问题，我们可以如何排查问题？

A:可以使用如下步骤：

第一步：在不同的设备上运行程序，确定该问题是否和个别设备相关还是和设备无关。

第二步：在有问题的设备上运行Demo，确保Demo运行是正常的，如果Demo有问题，请直接联系我们。

第三步：如果Demo正常，将你们的app_channel和sdk_auth替换Demo中Constants类中相应的常量，再运行Demo看看有没有问题，如果有问题，问题可能是app_channel或sdk_auth的值不正确。如果没有问题，把Demo代码放到你们的app中运行看看有没有问题。如果在app中运行没有问题，问题可能是你们的接入调用的方法和Demo中有不同的地方。如果在app中运行有问题，问题可能和app本身的一些情况有关，可以联系我们一起排查。