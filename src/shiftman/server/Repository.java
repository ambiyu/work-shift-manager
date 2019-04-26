package shiftman.server;

import java.util.List;

public interface Repository<T>{

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
     * Returns a copy of all the values of the repository sorted.
     */
    List<T> getAllValues();
}
