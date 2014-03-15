package ${packageName};

public class ApiException extends Exception {
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

  public int getCode() {
      return code;
  }
}