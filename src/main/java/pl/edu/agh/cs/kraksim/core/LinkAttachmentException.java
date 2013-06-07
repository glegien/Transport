package pl.edu.agh.cs.kraksim.core;

import pl.edu.agh.cs.kraksim.KraksimException;

@SuppressWarnings("serial")
public class LinkAttachmentException extends KraksimException
{

  public LinkAttachmentException() {
    super();
  }

  public LinkAttachmentException(String message, Throwable cause) {
    super( message, cause );
  }

  public LinkAttachmentException(String message) {
    super( message );
  }

  public LinkAttachmentException(Throwable cause) {
    super( cause );
  }
}
