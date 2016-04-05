package listener;

/**
 * Created by pj on 2016/3/30.
 */
public interface ApkUpdatingProgressListener {
    void onProgressUpdate(String uri, long current, long total);
}
