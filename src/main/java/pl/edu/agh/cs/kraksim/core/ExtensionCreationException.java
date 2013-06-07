package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

@SuppressWarnings("serial")
public class ExtensionCreationException extends KraksimException
{

  public ExtensionCreationException() {
    super();
  }

  public ExtensionCreationException(String message) {
    super( message );
  }

  public ExtensionCreationException(String message, Throwable cause) {
    super( message, cause );
  }

  public ExtensionCreationException(Throwable cause) {
    super( cause );
  }
}
