package project.vilsoncake.common.exceptions;

public class TemplateNotFound extends RuntimeException {
  public TemplateNotFound(String message) {
    super(message);
  }

  public TemplateNotFound(String message, Throwable cause) {
    super(message, cause);
  }
}
