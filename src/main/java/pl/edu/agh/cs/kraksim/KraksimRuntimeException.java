package pl.edu.agh.cs.kraksim;

// Base class for all runtime exception types specific to kraksim
@SuppressWarnings("serial")
public class KraksimRuntimeException extends RuntimeException
{

  public KraksimRuntimeException() {
    super();
  }

  public KraksimRuntimeException(String message) {
    super( message );
  }

  public KraksimRuntimeException(String message, Throwable cause) {
    super( message, cause );
  }

  public KraksimRuntimeException(Throwable cause) {
    super( cause );
  }
}
