package org.map;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 16;

    // must be power of 2, max capacity = 1073741824
    private static final int MAXIMUM_CAPACITY = 1 << 30;

    // The load factor used when none specified in constructor
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    // The table, resized as necessary. Length must be a power of two.
    private Node<V>[] nodeTable;

    // The number of key-value pairs in the weak hash map.
    private int size;

    // The next size value at which to resize (capacity * load factor).
    private int threshold;

    // Load factor for hash table
    private final float loadFactor;

    public LongMapImpl() {
        this(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int initialCapacity) {
        this(initialCapacity, DEFAULT_LOAD_FACTOR);
    }

    public LongMapImpl(int initialCapacity, float loadFactor) {
        if (initialCapacity < 0) {
            throw new IllegalArgumentException("Initial capacity must be more than 0: " + initialCapacity);
        }

        if (initialCapacity > MAXIMUM_CAPACITY) {
            initialCapacity = MAXIMUM_CAPACITY;
        }

        if (loadFactor <= 0 || Float.isNaN(loadFactor)) {
            throw new IllegalArgumentException("Illegal load factor: " + loadFactor);
        }

        nodeTable = newTable(initialCapacity);
        this.loadFactor = loadFactor;
        threshold = (int) (initialCapacity * loadFactor);
    }

    @SuppressWarnings("unchecked")
    private Node<V>[] newTable(int capacity) {
        return (Node<V>[]) new Node<?>[capacity];
    }

    private int generateHashCode(Long key) {
        return Math.abs(Objects.hashCode(key)) % nodeTable.length;
    }

    @Override
    public V put(long key, V value) {

        int hash = generateHashCode(key);

        for (Node<V> node = nodeTable[hash]; node != null; node = node.next) {
            if (hash == node.hash && Objects.equals(key, node.key)) {
                V oldValue = node.value;
                if (value != oldValue) {
                    node.value = value;
                }
                return oldValue;
            }
        }

        Node<V> node = nodeTable[hash];
        nodeTable[hash] = new Node<>(hash, key, value, node);

        if (size++ > threshold) {
            resize(nodeTable.length * 2);
        }
        return null;
    }

    @Override
    public V get(long key) {
        int hash = generateHashCode(key);
        Node<V> node = nodeTable[hash];
        while (node != null) {
            if (node.hash == hash && Objects.equals(key, node.key)) {
                return node.value;
            }
            node = node.next;
        }
        return null;
    }

    private void resize(int newCapacity) {
        Node<V>[] oldTable = nodeTable;
        int oldCapacity = oldTable.length;
        if (oldCapacity == MAXIMUM_CAPACITY) {
            threshold = Integer.MAX_VALUE;
            return;
        }

        Node<V>[] newTable = newTable(newCapacity);
        transposition(oldTable, newTable);
        nodeTable = newTable;
        threshold = (int) (newCapacity * loadFactor);
    }

    // Transfers all entries from old to the new table
    private void transposition(Node<V>[] oldTable, Node<V>[] newTable) {
        for (int i = 0; i < oldTable.length; i++) {
            Node<V> node = oldTable[i];
            oldTable[i] = null;
            while (node != null) {
                Node<V> next = node.next;
                if (node.key == null) {
                    node.next = null;
                    node.value = null;
                    size--;
                } else {
                    node.next = newTable[node.hash];
                    newTable[node.hash] = node;
                }
                node = next;
            }
        }
    }

    @Override
    public V remove(long key) {
        int hash = generateHashCode(key);
        Node<V> prev = nodeTable[hash];
        Node<V> node = prev;

        while (node != null) {
            Node<V> next = node.next;
            if (hash == node.hash && Objects.equals(key, node.key)) {
                size--;
                if (prev == node) {
                    nodeTable[hash] = next;
                } else {
                    prev.next = next;
                }
                return node.value;
            }
            prev = node;
            node = next;
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public boolean containsKey(long key) {
        for (Node<V> longVNode : nodeTable) {
            for (Node<V> node = longVNode; node != null; node = node.next) {
                if (node.key.equals(key)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(V value) {
        for (Node<V> longVNode : nodeTable) {
            for (Node<V> node = longVNode; node != null; node = node.next) {
                if (Objects.equals(value, node.value)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public long[] keys() {
        long[] keysArray = new long[(int) size()];
        int index = 0;
        for (Node<V> longVNode : nodeTable) {
            Node<V> node = longVNode;
            while (node != null) {
                keysArray[index] = node.key;
                index++;
                node = node.next;
            }
        }
        return keysArray;
    }

    @Override
    public V[] values() {
        List<V> valuesList = new ArrayList<>();
        for (Node<V> longVNode : nodeTable) {
            Node<V> node = longVNode;
            while (node != null) {
                valuesList.add(node.getValue());
                node = node.next;
            }
        }

        V[] valuesArray = (V[]) Array.newInstance(valuesList.get(0).getClass(), valuesList.size());
        for (int i = 0; i < valuesList.size(); i++) {
            valuesArray[i] = valuesList.get(i);
        }
        return valuesArray;
    }

    @Override
    public long size() {
        return size;
    }

    @Override
    public void clear() {
        Node<V>[] nodes = nodeTable;
        if (nodes != null && size > 0) {
            size = 0;
            Arrays.fill(nodes, null);
        }
    }

    private static class Node<V> {
        private final Long key;
        private V value;
        final int hash;
        private Node<V> next;

        public Node(int hash, Long key, V value, Node<V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
            this.hash = hash;
        }

        public Long getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        @Override
        @SuppressWarnings("unchecked")
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            Node<V> e = (Node<V>) o;
            Long k1 = getKey();
            Object k2 = e.getKey();
            if (Objects.equals(k1, k2)) {
                V v1 = getValue();
                Object v2 = e.getValue();
                return Objects.equals(v1, v2);
            }
            return false;
        }

        @Override
        public int hashCode() {
            Long k = getKey();
            V v = getValue();

            int result = 1;
            final int prime = 31;
            result = prime * result + ((k == null) ? 0 : k.hashCode());
            result = prime * result + ((v == null) ? 0 : v.hashCode());

            return result;
        }

        @Override
        public String toString() {
            return getKey() + " -> " + getValue();
        }
    }
}
