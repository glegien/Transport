package pl.edu.agh.cs.kraksim;

// Base class for all exceptions types specific to Kraksim
@SuppressWarnings("serial")
public class KraksimException extends Exception
{

  public KraksimException() {
    super();
  }

  public KraksimException(String message) {
    super( message );
  }

  public KraksimException(String message, Throwable cause) {
    super( message, cause );
  }

  public KraksimException(Throwable cause) {
    super( cause );
  }
}
