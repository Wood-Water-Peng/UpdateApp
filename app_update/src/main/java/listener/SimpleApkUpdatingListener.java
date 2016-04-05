package listener;

import assist.FailReason;

/**
 * Created by pj on 2016/3/30.
 */
public class SimpleApkUpdatingListener implements ApkUpdatingListener {
    @Override
    public void onLoadingStarted(String uri) {
        //Empty implementation
    }

    @Override
    public void onLoadingFailed(String uri, FailReason.FailType failType) {
        //Empty implementation
    }

    @Override
    public void onLoadingCompleted(String uri) {
        //Empty implementation
    }

    @Override
    public void onLoadingCancelled(String uri) {
        //Empty implementation
    }
}
