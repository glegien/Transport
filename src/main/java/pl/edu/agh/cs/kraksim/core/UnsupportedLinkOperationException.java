package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimRuntimeException;

@SuppressWarnings("serial")
public class UnsupportedLinkOperationException extends KraksimRuntimeException
{

  public UnsupportedLinkOperationException() {
    super();
  }

  public UnsupportedLinkOperationException(String message) {
    super( message );
  }

  public UnsupportedLinkOperationException(String message, Throwable cause) {
    super( message, cause );
  }

  public UnsupportedLinkOperationException(Throwable cause) {
    super( cause );
  }
}
