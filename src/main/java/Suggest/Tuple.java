package edu.brown.cs.suggest;
/**
* this class implements a fairly simple tuple.
* @param <T> can be anything
* @param <O> can also be anything
*/
public class Tuple<T, O> {
  /** the first element of tuple. */
  private final T first;
  /** the second element of tuple. */
  private final O second;
  /**
  * this is the constructor for the tuple class.
  * @param firstElement is the first element in the tuple
  * @param secondElement is the second element in the tuple
  */
  public Tuple(T firstElement, O secondElement) {
    this.first = firstElement;
    this.second = secondElement;
  }
  /**
  * this gets the first element.
  * @return first element is returned
  */
  public T first() {
    return first;
  }
  /**
  * this gets the second element.
  * @return second element is returned
  */
  public O second() {
    return second;
  }
  public String toString() {
    return "{"+first().toString()+", "+second().toString()+"}";
  }
}
