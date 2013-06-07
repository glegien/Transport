package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

/* See assumptions about action in Action.java file */
@SuppressWarnings("serial")
public class InvalidActionException extends KraksimException
{

  public InvalidActionException() {
    super();
  }

  public InvalidActionException(String message, Throwable cause) {
    super( message, cause );
  }

  public InvalidActionException(String message) {
    super( message );
  }

  public InvalidActionException(Throwable cause) {
    super( cause );
  }
}
