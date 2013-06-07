// TODO: komentarz
package pl.edu.agh.cs.kraksim.util;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ArrayIterator<T> implements Iterator<T>
{
  private final T[] array;
  private int       i;

  public ArrayIterator(T[] array) {
    this.array = array;
    i = 0;
  }

  public boolean hasNext() {
    return i < array.length;
  }

  public T next() {
    if ( i < array.length ) {
      return array[i++];
    }
    else {
      throw new NoSuchElementException();
    }
  }

  public void remove() {
    throw new UnsupportedOperationException();
  }
}
