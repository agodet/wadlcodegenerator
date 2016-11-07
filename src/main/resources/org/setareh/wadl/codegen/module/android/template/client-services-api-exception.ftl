package ${packageName};

public class ApiException extends Exception {
  public static final int NETWORK_ERROR = 1;

  int code = 0;
  String message = null;

  public ApiException(String message, Throwable throwable) {
    super(message, throwable);
  }

  public ApiException(String message) {
    super(message);
  }

  public ApiException(Throwable throwable) {
    super(throwable);
  }

  public ApiException(int code, String message) {
    super("Error code : " + code + ". Message : " + message);
    this.code = code;
    this.message = message;
  }

  public ApiException(int code, Throwable throwable) {
    super("Error code : " + code, throwable);
    this.code = code;
    this.message = throwable == null ? null : throwable.getMessage();
}

  public int getCode() {
      return code;
  }
}