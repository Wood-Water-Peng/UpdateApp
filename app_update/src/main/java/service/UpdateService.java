package service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.example.pj.app_update.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import assist.FailReason;
import constants.DownLoadConstants;
import domain.DownloadInfo;
import listener.ApkUpdatingListener;
import listener.ApkUpdatingProgressListener;
import utils.PackageUtil;

/**
 * Created by pj on 2016/3/23.
 */
public class UpdateService extends IntentService {
    private static final String TAG = "UpdateService";
    private String mApkUrl;
    private String mSavePath;
    private long mFileStart;
    private NotificationManagerCompat mNotificationManager;
    private NotificationCompat.Builder mBuilder;
    private int notification_id = 100;
    private int iconId;
    private Notification mNotification;
    private String saveName_temp;
    private String saveName;
    private UpdateBinder UpdateBinder;
    private ApkUpdatingListener mApkUpdatingListener;  //监听下载状态
    private ApkUpdatingProgressListener mUpdatingProgressListener; //监听下载进度
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    /**
     * 客户端获取服务端下载进度的方法
     * 1.采用BinderService，拿到service的实例，然后设置回调接口
     */
    public UpdateService() {
        super("PJ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "---onStartCommand---");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (UpdateBinder == null) {
            UpdateBinder = new UpdateBinder();
        }
        return UpdateBinder;
    }

    public class UpdateBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    public void setApkUpdateListener(ApkUpdatingListener listener) {
        this.mApkUpdatingListener = listener;
    }

    public void setApkUpdatingProgressListener(ApkUpdatingProgressListener listener) {
        this.mUpdatingProgressListener = listener;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mApkUrl = intent.getStringExtra(DownLoadConstants.APK_URL);
        mSavePath = intent.getStringExtra(DownLoadConstants.SAVE_PATH);
        iconId = intent.getIntExtra(DownLoadConstants.NOTIF_ICON, R.drawable.default_icon);
        saveName_temp = intent.getStringExtra(DownLoadConstants.CACHE_APK_NAME_TEMP);
        saveName = intent.getStringExtra(DownLoadConstants.CACHE_APK_NAME);
        /**
         * 进行一个参数
         * 将检测的结果返回给用户
         */
        Log.i(TAG, "thread:" + Thread.currentThread().getName());
        showNotification();
        OkHttpClient client = new OkHttpClient();
        Log.i(TAG, "apk_url:" + mApkUrl);
        Log.i(TAG, "mSavePath:" + mSavePath);
        Log.i(TAG, "saveName_temp:" + saveName_temp);
        InputStream is = null;
        try {
            File file = new File(mSavePath + "/" + saveName_temp);
            if (!file.exists()) {
                file.createNewFile();
            }
            Log.i(TAG, "file:" + file.getAbsolutePath());
            mFileStart = 0;
            FileOutputStream fos = new FileOutputStream(file, false);
            Request request = new Request
                    .Builder()
                    .url(mApkUrl)
                    .build();
            Response response = client.newCall(request).execute();
            int responseCode = response.code();
            Log.i(TAG, "responseCode:" + responseCode);
            switch (responseCode) {
                case 200:
                case 206:
                case 207:
                    is = response.body().byteStream();
//                    Log.i(TAG, "contentLength:" + response.body().contentLength());
                    break;
                case 416:    //文件已经下载到本地
                    /**
                     *此时,弹出对话框提醒用户是直接安装还是重新下载
                     *那么,这个对话框究竟应该让谁来弹出呢？
                     *
                     */
                    fos.close();
                    return;
                case 400:
                case 406:
                case 404:
                    fos.close();
                    return;

            }
            //拿到输入流后,写入到本地文件
            if (null != is) {
                byte[] buffer = new byte[1024 * 1024 * 100];
                int size;
                DownloadInfo downloadInfo = new DownloadInfo();
                downloadInfo.setTotalLength(response.body().contentLength() + mFileStart);
                if (mApkUpdatingListener != null) {
                    mApkUpdatingListener.onLoadingStarted(mApkUrl); //开始下载
                }
                int current_ratio=0; //当前下载的梯度
                while ((size = is.read(buffer, 0, buffer.length)) != -1) {
                    Thread.currentThread().sleep(10);
                    fos.write(buffer, 0, size);
                    mFileStart += size;
                    downloadInfo.setDownloadedLength(mFileStart);
                    updateNotification(downloadInfo);
                    if (checkIfNeedToSendProgress(current_ratio,downloadInfo.getDownloadedLength(), downloadInfo.getTotalLength())) {
                        if (mUpdatingProgressListener != null) {
                            /**
                             * 发送进度的时候可以做一下优化
                             * 如，每下载5%发送一次进度
                             */
                            mUpdatingProgressListener.onProgressUpdate(mApkUrl, downloadInfo.getDownloadedLength(), downloadInfo.getTotalLength());//下载进度
                            current_ratio++;
                        }
                    }
//                    Log.i(TAG, "contentLength:" + mFileStart);
                }
                //重置运行状态
                Log.i(TAG, "downloaded_length:" + mFileStart);
                //更改文件名
                File apkTemp = new File(mSavePath + "/" + saveName_temp);
                mSavePath = mSavePath + "/" + saveName;
                Log.i(TAG, "下载完后mSavePath:" + mSavePath);
                mApkUpdatingListener.onLoadingCompleted(mApkUrl);//下载完成
                apkTemp.renameTo(new File(mSavePath));
                //下载完成后发送广播
                mNotificationManager.cancel(notification_id);
                PackageUtil.installApk(this, mSavePath);
                is.close();
                fos.close();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            Log.i(TAG, "中断异常");
            if (mApkUpdatingListener != null) {
                mApkUpdatingListener.onLoadingFailed(mApkUrl, FailReason.FailType.IO_ERROR);
            }
            clearCacheApk();
            clearNotification();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "文件流出问题了");
            if (mApkUpdatingListener != null) {
                mApkUpdatingListener.onLoadingFailed(mApkUrl, FailReason.FailType.IO_ERROR);
            }
            clearCacheApk();
            clearNotification();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "难道是断网了");
            if (mApkUpdatingListener != null) {
                mApkUpdatingListener.onLoadingFailed(mApkUrl, FailReason.FailType.NETWORK_ERROR);
            }
            clearCacheApk();
            clearNotification();
        } catch (Exception e) {
            Log.i(TAG, "捕获到异常了");
            if (mApkUpdatingListener != null) {
                mApkUpdatingListener.onLoadingFailed(mApkUrl, FailReason.FailType.UNKNOWN);
            }
            clearCacheApk();
            clearNotification();
        }
    }

    /**
     * @param downloadedLength
     * @param totalLength
     * @return 判断是否需要发送进度到界面展示
     */
    private boolean checkIfNeedToSendProgress(int cur_ratio,long downloadedLength, long totalLength) {
        int interval_ratio = (int) (totalLength / 20);
        Log.i(TAG, "interval_ratio:" + interval_ratio);
        if (downloadedLength / interval_ratio > cur_ratio) {
            Log.i(TAG, "cur_ratio:" + cur_ratio);
            return true;
        }
        return false;
    }


    private void clearNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(notification_id);
        }
    }

    private void clearCacheApk() {
        File apkCache = new File(getCacheDir() + "/" + DownLoadConstants.CACHE_APK_NAME_TEMP);
        Log.i(TAG, "清理缓存apk:" + apkCache.getAbsolutePath());
        if (apkCache.exists() && apkCache.length() > 0) {
            apkCache.delete();
        }
    }

    private void updateNotification(DownloadInfo info) {
        int progress = (int) (info.getDownloadedLength() * 1.0 / info.getTotalLength() * 100);
        /**
         * 当app下载完成后，我们给notification设置一个点击事件，点击后安装app
         */
//        Log.i(TAG, "process---" + progress);
        if (null != mNotificationManager) {
            mBuilder.setProgress(100, progress, false);
            mNotificationManager.notify(notification_id, mBuilder.build());
        }
    }

    /**
     * provide a interface to user,so they can give their icon
     */
    private void showNotification() {
        mBuilder = new NotificationCompat.Builder(this)
                .setContentTitle("发现新版本")
                .setSmallIcon(iconId)
                .setContentText("版本更新中......");
        mNotification = mBuilder.build();
        mNotificationManager = NotificationManagerCompat.from(this);
        mNotificationManager.notify(notification_id, mNotification);
    }
}
