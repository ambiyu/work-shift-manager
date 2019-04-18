package shiftman.server;

import java.util.Iterator;

public interface Repository<T> extends Iterable<T>{

    void add(T item);

    boolean contains(T item);

    Iterator<T> iterator();
}
