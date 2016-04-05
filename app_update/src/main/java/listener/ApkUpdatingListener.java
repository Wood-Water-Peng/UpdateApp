package listener;

import assist.FailReason;

/**
 * Created by pj on 2016/3/30.
 */
public interface ApkUpdatingListener {
    void onLoadingStarted(String uri);

    void onLoadingFailed(String uri, FailReason.FailType failType);

    void onLoadingCompleted(String uri);

    void onLoadingCancelled(String uri);
}
