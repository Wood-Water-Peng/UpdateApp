package domain;

/**
 * Created by pj on 2016/3/23.
 */
public class DownloadInfo {
    private long mTotalLength;
    private long mDownloadedLength;

    public long getDownloadedLength() {
        return mDownloadedLength;
    }

    public void setDownloadedLength(long downloadedLength) {
        mDownloadedLength = downloadedLength;
    }

    public long getTotalLength() {
        return mTotalLength;
    }

    public void setTotalLength(long totalLength) {
        mTotalLength = totalLength;
    }
}
