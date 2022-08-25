package name.nkid00.rcutil.util;

import java.util.Iterator;

public class Enumerate<T> implements Iterable<Enumerate<T>.IndexedItem> {
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
    public Iterator<Enumerate<T>.IndexedItem> iterator() {
        return new EnumerateIterator();
    }

    public class IndexedItem {
        private final int index;
        private final T item;

        public IndexedItem(int index, T item) {
            this.index = index;
            this.item = item;
        }

        public int index() {
            return index;
        }

        public T item() {
            return item;
        }
    }

    public class EnumerateIterator implements Iterator<IndexedItem> {
        private int index = start;

        public int index() {
            return index;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public IndexedItem next() {
            return new IndexedItem(index++, iterator.next());
        }
    }
}
