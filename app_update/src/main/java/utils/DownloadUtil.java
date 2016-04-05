package utils;

import android.content.Intent;
import android.util.Log;

import constants.DownLoadConstants;
import engine.UpdateConfigure;
import engine.UpdateEngine;
import listener.ApkUpdatingListener;
import listener.SimpleApkUpdatingListener;
import service.UpdateService;

/**
 * Created by pj on 2016/3/23.
 */
public class DownloadUtil {
    private static final String ERROR_WRONG_ARGUMENTS = "Wrong arguments were passed to downloadApk() method (uri must not be validate)";
    private static final String TAG = "DownloadUtil";
    private UpdateConfigure configuration;
    private UpdateEngine engine;
    private volatile static DownloadUtil instance;
    private static final String ERROR_INIT_CONFIG_WITH_NULL = "ImageLoader configuration can not be initialized with null";

    private ApkUpdatingListener defaultListener = new SimpleApkUpdatingListener();

    public static DownloadUtil getInstance() {
        if (instance == null) {
            synchronized (DownloadUtil.class) {
                if (instance == null) {
                    instance = new DownloadUtil();
                }
            }
        }
        return instance;
    }

    protected DownloadUtil() {
    }

    public synchronized void init(UpdateConfigure configure) {
        if (configure == null) {
            throw new IllegalArgumentException(ERROR_INIT_CONFIG_WITH_NULL);
        }
        if (this.configuration == null) {
            engine = new UpdateEngine(configure);
            this.configuration = configure;
        }
    }

    public void downloadApk() {
        /**
         * 问题:
         *    listener对象的引用无法带到service中
         *    可以考虑用bindservice拿到service的binder,但此时略显麻烦
         */
        Log.i(TAG, "apkUrl:" + configuration.apkUrl + "---apkSavePath:" + configuration.apkSavePath);
        Intent intent = new Intent(configuration.getContext(), UpdateService.class);
        intent.putExtra(DownLoadConstants.APK_URL, configuration.apkUrl);
        intent.putExtra(DownLoadConstants.SAVE_PATH, configuration.apkSavePath);
        intent.putExtra(DownLoadConstants.NOTIF_ICON, configuration.notificationIcon);
        intent.putExtra(DownLoadConstants.CACHE_APK_NAME, DownLoadConstants.CACHE_APK_NAME);
        intent.putExtra(DownLoadConstants.CACHE_APK_NAME_TEMP, DownLoadConstants.CACHE_APK_NAME_TEMP);
        engine.submit(intent);
    }

    public void releaseService() {
        engine.releaseService();
    }
}
