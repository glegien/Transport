package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

@SuppressWarnings("serial")
public class InvalidExtensionClassException extends KraksimException
{

  public InvalidExtensionClassException() {
    super();
  }

  public InvalidExtensionClassException(String message, Throwable cause) {
    super( message, cause );
  }

  public InvalidExtensionClassException(String message) {
    super( message );
  }

  public InvalidExtensionClassException(Throwable cause) {
    super( cause );
  }
}
