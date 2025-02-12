package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MyDictionary<K, V> {
    private final List<Entry<K, V>> entries;
    private final ReentrantReadWriteLock lock;

    public MyDictionary() {
        entries = new ArrayList<>();
        lock = new ReentrantReadWriteLock();
    }


    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            for (Entry<K, V> entry : entries) {
                if (entry.getKey().equals(key)) {
                    entry.setValue(value);
                    return;
                }
            }
            entries.add(new Entry<>(key, value));
        } finally {
            lock.writeLock().unlock();
        }
    }


    public V get(K key) {
        lock.readLock().lock();
        try {
            for (Entry<K, V> entry : entries) {
                if (entry.getKey().equals(key)) {
                    return entry.getValue();
                }
            }
            return null;
        } finally {
            lock.readLock().unlock();
        }
    }


    public void addToList(K key, V item) {
        lock.writeLock().lock();
        try {
            for (Entry<K, V> entry : entries) {
                if (entry.getKey().equals(key)) {
                    if (entry.getValue() instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<V> list = (List<V>) entry.getValue();
                        list.add(item);
                    }
                    return;
                }
            }

            List<V> newList = new ArrayList<>();
            newList.add(item);
            entries.add(new Entry<>(key, (V) newList));
        } finally {
            lock.writeLock().unlock();
        }
    }


    public List<Entry<K, V>> getAllEntries() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(entries);
        } finally {
            lock.readLock().unlock();
        }
    }


    public static class Entry<K, V> {
        private final K key;
        private V value;

        public Entry(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public K getKey() { return key; }

        public V getValue() { return value; }

        public void setValue(V value) { this.value = value; }

        @Override
        public String toString() {
            return "{" + key + ": " + value + "}";
        }
    }
}
