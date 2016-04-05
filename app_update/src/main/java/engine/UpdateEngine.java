package engine;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import service.UpdateService;

/**
 * Created by pj on 2016/3/23.
 */
public class UpdateEngine {
    private static final String TAG = "UpdateEngine";
    final UpdateConfigure configure;
    private ServiceConnection mConnection;

    public UpdateEngine(UpdateConfigure configure) {
        this.configure = configure;
    }

    /**
     * question
     * how to get feedback of the running service
     * 1.broadcast
     * 2.bindService
     */
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


    public void releaseService() {
        if (mConnection != null) {
            configure.getContext().unbindService(mConnection);
        }
    }
}
