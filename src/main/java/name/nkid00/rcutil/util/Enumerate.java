package name.nkid00.rcutil.util;

import java.util.Iterator;

public class Enumerate<T> implements Iterable<IndexedObject<T>> {
    private final Iterator<T> iterator;
    private final int start;

    public Enumerate(Iterable<T> iterable) {
        this(iterable, 0);
    }

    public Enumerate(Iterator<T> iterator) {
        this(iterator, 0);
    }

    public Enumerate(Iterable<T> iterable, int start) {
        this(iterable.iterator(), start);
    }

    public Enumerate(Iterator<T> iterator, int start) {
        this.iterator = iterator;
        this.start = start;
    }

    @Override
    public Iterator<IndexedObject<T>> iterator() {
        return new EnumerateIterator();
    }

    public class EnumerateIterator implements Iterator<IndexedObject<T>> {
        private int index = start;

        public int index() {
            return index;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public IndexedObject<T> next() {
            return new IndexedObject<>(index++, iterator.next());
        }
    }
}
