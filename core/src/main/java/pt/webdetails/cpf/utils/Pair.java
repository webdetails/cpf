package pt.webdetails.cpf.utils;

/**
 * Struct for two objects. For when you don't really need a map.
 */
public class Pair<T, U> {

  public Pair( T first, U second ) {
    this.first = first;
    this.second = second;
  }

  public Pair() {
  }

  public T first;
  public U second;

}
