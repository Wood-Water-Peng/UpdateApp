package utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;

import java.io.IOException;

/**
 * 包管理器
 *
 * @author evan.liu
 */
public class PackageUtil {
    /**
     * 检测有没此应用
     *
     * @param context 上下文
     * @return
     */
    public static boolean checkApkExist(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            if (pi != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前版本号
     *
     * @param context 上下文
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                versionCode = pi.versionCode;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取当前版本名
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 安装apk
     *
     * @param context   上下文
     * @param cachePath apk缓存路径
     */
    public static void installApk(Context context, String cachePath) {
        chmod("777", cachePath);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + cachePath), "application/vnd.android.package-archive");
        if (context instanceof Activity) {
            ((Activity) context).startActivityForResult(intent, 0);
        } else {
            context.startActivity(intent);
        }
    }

    private static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
