package com.InfinityRaider.AgriCraft.utility;

import java.util.*;

public class WeightedRandom<T> {
    private int totalWeight;
    private HashMap<T, Integer> entries;

    public WeightedRandom() {
        this.entries = new HashMap<T, Integer>();
        totalWeight = 0;
    }

    public void addEntry(T entry, int weight) {
        this.entries.put(entry, weight);
        totalWeight = totalWeight + weight;
    }

    public void removeEntry(T entry) {
        Iterator<Map.Entry<T, Integer>> iterator = entries.entrySet().iterator();
        while(iterator.hasNext()) {
            Map.Entry<T, Integer> mapEntry = iterator.next();
            if(mapEntry.getKey().equals(entry)) {
                totalWeight = totalWeight - mapEntry.getValue();
                iterator.remove();
            }
        }
    }

    public T getRandomEntry(Random rand) {
        T result = null;
        Set<Map.Entry<T, Integer>> entries = this.entries.entrySet();
        double threshold = rand.nextDouble() * totalWeight;
        for(Map.Entry<T, Integer> mapEntry:entries) {
            threshold = threshold - mapEntry.getValue();
            if (threshold <= 0.0D) {
                result = mapEntry.getKey();
                break;
            }
        }
        return result;
    }

    public int getWeight(T entry) {
        for(Map.Entry<T, Integer> mapEntry:entries.entrySet()) {
            if (entry.equals(mapEntry.getKey())) {
                return mapEntry.getValue();
            }
        }
        return 0;
    }
}
