package pl.edu.agh.cs.kraksim.main;

@SuppressWarnings("serial")
class AlgorithmConfigurationException extends Exception
{

  public AlgorithmConfigurationException() {
    super();
  }

  public AlgorithmConfigurationException(String message, Throwable cause) {
    super( message, cause );
  }

  public AlgorithmConfigurationException(String message) {
    super( message );
  }

  public AlgorithmConfigurationException(Throwable cause) {
    super( cause );
  }
}
