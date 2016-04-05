package engine;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.example.pj.app_update.R;

import listener.ApkUpdatingListener;
import listener.ApkUpdatingProgressListener;

/**
 * Created by pj on 2016/3/23.
 */
public final class UpdateConfigure {
    public final int notificationIcon;
    public final String apkUrl;
    public final String apkSavePath;
    final Resources resource;
    final Context context;
    final ApkUpdatingProgressListener mApkUpdatingProgressListener;
    final ApkUpdatingListener mApkUpdatingListener;

    public Context getContext() {
        return context;
    }

    private UpdateConfigure(final Builder builder) {
        resource = builder.context.getResources();
        notificationIcon = builder.notificationIcon;
        apkUrl = builder.apkUrl;
        apkSavePath = builder.apkSavePath;
        context = builder.context;
        mApkUpdatingListener = builder.mApkUpdatingListener;
        mApkUpdatingProgressListener = builder.mApkUpdatingProgressListener;
    }

    public static class Builder {
        private int notificationIcon;
        private String apkUrl;
        private String apkSavePath;
        private Context context;
        private ApkUpdatingProgressListener mApkUpdatingProgressListener;
        private ApkUpdatingListener mApkUpdatingListener;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public Builder notificationIcon(int drawable) {
            notificationIcon = drawable;
            return this;
        }

        public Builder apkUpdatingProgressListener(ApkUpdatingProgressListener apkUpdatingProgressListener) {
            this.mApkUpdatingProgressListener = apkUpdatingProgressListener;
            return this;
        }

        public Builder apkUpdateListener(ApkUpdatingListener apkUpdatingListener) {
            this.mApkUpdatingListener = apkUpdatingListener;
            return this;
        }

        public Builder apkUrl(String apkUrl) {
            this.apkUrl = apkUrl;
            return this;
        }

        public Builder apkSavePath(String apkSavePath) {
            this.apkSavePath = apkSavePath;
            return this;
        }

        public UpdateConfigure build() {
            initEmptyFieldsWithDefaultValues();
            return new UpdateConfigure(this);
        }

        private void initEmptyFieldsWithDefaultValues() {
            if (notificationIcon == 0) {
                notificationIcon = (R.drawable.default_icon);
            }
            if (TextUtils.isEmpty(apkSavePath)) {
                apkSavePath = context.getCacheDir().getPath();
            }
        }
    }
}
