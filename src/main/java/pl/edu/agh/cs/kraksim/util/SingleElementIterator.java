//TODO: komentarz
package pl.edu.agh.cs.kraksim.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class SingleElementIterator<T> implements Iterator<T>
{

  private T       element;
  private boolean end;

  public SingleElementIterator(T element) {
    this.element = element;
    end = false;
  }

  public boolean hasNext() {
    return !end;
  }

  public T next() throws NoSuchElementException {
    if ( end ) throw new NoSuchElementException();
    end = true;
    return element;
  }

  public void remove() throws UnsupportedOperationException {
    throw new UnsupportedOperationException();
  }
}
