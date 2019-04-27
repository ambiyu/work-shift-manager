package shiftman.server;

public interface Repository<T> {

    void add(T item);

    boolean contains(T item);
}
