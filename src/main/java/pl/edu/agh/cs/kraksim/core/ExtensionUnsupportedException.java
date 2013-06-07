package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

@SuppressWarnings("serial")
public class ExtensionUnsupportedException extends KraksimException
{

  public ExtensionUnsupportedException() {
    super();
  }

  public ExtensionUnsupportedException(String message) {
    super( message );
  }

  public ExtensionUnsupportedException(String message, Throwable cause) {
    super( message, cause );
  }

  public ExtensionUnsupportedException(Throwable cause) {
    super( cause );
  }
}
