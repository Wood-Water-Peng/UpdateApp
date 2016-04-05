package assist;

/**
 * Created by pj on 2016/3/30.
 */
public class FailReason {
    private final FailType type;

    public FailReason(FailType type) {
        this.type = type;
    }

    public FailType getType() {
        return type;
    }

    public static enum FailType {
        IO_ERROR,
        NETWORK_ERROR,
        UNKNOWN
    }
}
