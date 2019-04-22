package shiftman.server;

import java.util.Iterator;

public interface Repository<T> extends Iterable<T>{

    void add(T item);

    boolean contains(T item);

    void sort();

    Iterator<T> iterator();
}
