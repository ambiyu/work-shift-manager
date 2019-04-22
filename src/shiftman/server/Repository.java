package shiftman.server;

import java.util.Iterator;

public interface Repository<T> extends Iterable<T>{

    /**
     * Add a new item to the repository
     * @param item the item to add
     */
    void add(T item);

    /**
     * Checks if the repository contains an item
     * @param item the item to check
     * @return true if the repository contains the item, otherwise false
     */
    boolean contains(T item);

    /**
     * Sorts all the values in the repository
     */
    void sort();

    Iterator<T> iterator();
}
