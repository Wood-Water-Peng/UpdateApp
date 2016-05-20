# UpdateApp
一个简单的升级Module
暂时提供一个简单的Dialog和一个notification进行后台升级
后面会完善升级流程，使用起来会更加的稳定,智能 
##思路
* 首先检查给到的Url是否有新版本<br>
  无论是否检测到新版本，我们都会提供一个自定义Dialog给用户配置，采用Builder模式可以配置color，title来搭配各种服务端的请求结果
* 如果有新版本的Apk可以更新<br>
  同样提供一个更新的Dialog,可以配置下载后的安装路径，更新的监听器，这里暂时是采取的Notification的更新方式。我们开启一个服务来运行更新程序,针对用户给到的Url以及用户本地的SD卡，网络等等状态，我们会返回不同的状态，以便在客户端做出有效的反馈。
* 检测更新服务是否在运行<br>
  在真正的项目中，往往在App内部还有一个更新入口，如果用户在进入App时已经执行了更新操作，那么，我们必须要保证只有一个更新服务在运行
  一般通过拿到系统正在运行的所有服务，然后匹配自己开启的更新服务即可。

##相关代码
```java
 UpdateConfigure.Builder builder = new UpdateConfigure.Builder(MainActivity.this);
        builder.apkUrl(url_apk)       
                .apkSavePath(null)
                .notificationIcon(R.drawable.rabbit)
                .apkUpdatingProgressListener(new ApkUpdatingProgressListener() {
                    @Override
                    public void onProgressUpdate(String uri, long current, long total) {

                    }
                })
                .apkUpdateListener
```
```java
    public void submit(Intent intent) {
        mConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG, "---onServiceConnected---");
                if (service instanceof UpdateService.UpdateBinder) {
                    UpdateService updateService = ((UpdateService.UpdateBinder) service).getService();
                    updateService.setApkUpdateListener(configure.mApkUpdatingListener);
                    updateService.setApkUpdatingProgressListener(configure.mApkUpdatingProgressListener);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        configure.getContext().bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        configure.getContext().startService(intent);
    }
```
#改进
* 在更新Apk的时候，提供一个前台切换到后台的接口，即前台使用Dialog，后台使用Notification
* 暂时没有做断点续传，后续会考虑这个功能
* 进一步研究RemoteView，使Notification上的控件显示更加灵活

#说明
仅仅是本人的练手程序，暂时无法适用于各种App，但是，随着技术的提高，这个库将会越来越完善，直至能够真正的帮助到Android开发的朋友们。
