package ${packageName};

/**
 * Result wrapper.
 */
public class Result<SUCCESS, FAILURE> {
    public SUCCESS success;
    public FAILURE failure;
    public int errorCode;

    public Result(SUCCESS success) {
        this.success = success;
    }

    public Result(FAILURE failure, int errorCode) {
        this.failure = failure;
        this.errorCode = errorCode;
    }

    public boolean isSuccess() {
        return success != null;
    }
}
