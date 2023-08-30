package net.cmr.gaze.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ClosestValueMap<K extends Comparable<K>, V> {
    private List<Pair<K, V>> data;

    public ClosestValueMap() {
        data = new ArrayList<>();
    }

    public void put(K key, V value) {
        Pair<K, V> pair = new Pair<>(key, value);
        int index = Collections.binarySearch(data, pair);
        
        if (index < 0) {
            index = -(index + 1);
        }
        
        data.add(index, pair);
    }

    public V get(K targetKey) {
        if (data.isEmpty()) {
            return null;
        }

        int index = Collections.binarySearch(data, new Pair<>(targetKey, null));
        
        if (index < 0) {
            index = -(index + 1);
        }
        
        if (index == 0) {
            return data.get(0).value;
        } else if (index == data.size()) {
            return data.get(data.size() - 1).value;
        } else {
            Pair<K, V> prevPair = data.get(index - 1);
            Pair<K, V> nextPair = data.get(index);
            
            if (targetKey.compareTo(prevPair.key) <= targetKey.compareTo(nextPair.key)) {
                return prevPair.value;
            } else {
                return nextPair.value;
            }
        }
    }
    
    public int getClosestIndexOf(K targetKey) {
    	if (data.isEmpty()) {
            return -1;
        }

        int index = Collections.binarySearch(data, new Pair<>(targetKey, null));
        
        if (index < 0) {
            index = -(index + 1);
        }
        
        if (index == 0) {
            return 0;
        } else if (index == data.size()) {
            return data.size() - 1;
        } else {
            Pair<K, V> prevPair = data.get(index - 1);
            Pair<K, V> nextPair = data.get(index);
            
            if (targetKey.compareTo(prevPair.key) <= targetKey.compareTo(nextPair.key)) {
                return index-1;
            } else {
                return index;
            }
        }
    }
    
    public List<Pair<K, V>> getInternalList() {
    	return data;
    }
    
    public void clear() {
    	data.clear();
    }

    public int size() {
    	return data.size();
    }
    
    private static class Pair<K, V> implements Comparable<Pair<K, V>> {
        K key;
        V value;

        public Pair(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Pair<K, V> other) {
            return PairComparator.INSTANCE.compare(this, other);
        }
    }
    private static class PairComparator<K extends Comparable<K>, V> implements Comparator<Pair<K, V>> {
        static final PairComparator INSTANCE = new PairComparator<>();

        @Override
        public int compare(Pair<K, V> p1, Pair<K, V> p2) {
            return p1.key.compareTo(p2.key);
        }
    }
}
