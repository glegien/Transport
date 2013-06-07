//TODO: komentarz
package pl.edu.agh.cs.kraksim.util;

import java.util.Iterator;

public class EmptySequenceIterator<T> implements Iterator<T>
{

  public boolean hasNext() {
    return false;
  }

  public T next() {
    throw new UnsupportedOperationException();
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
