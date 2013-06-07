package pl.edu.agh.cs.kraksim;

/**
 * Indicates a programmer error (i.e. some assumptions about object state
 * or method params are not satisfied).
 */
@SuppressWarnings("serial")
public class AssumptionNotSatisfiedException extends KraksimRuntimeException
{

  public AssumptionNotSatisfiedException() {
    super();
  }

  public AssumptionNotSatisfiedException(String message) {
    super( message );
  }

  public AssumptionNotSatisfiedException(String message, Throwable cause) {
    super( message, cause );
  }

  public AssumptionNotSatisfiedException(Throwable cause) {
    super( cause );
  }
}
