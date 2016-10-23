package com.infinityraider.agricraft.utility;

import java.util.*;

public class WeightedRandom<T> {

    private int totalWeight;
    private final HashMap<T, Integer> entries;

    public WeightedRandom() {
        this.entries = new HashMap<>();
        this.totalWeight = 0;
    }

    public void addEntry(T entry, int weight) {
        this.entries.put(entry, weight);
        this.totalWeight += weight;
    }

    public void removeEntry(T entry) {
        Integer e = entries.remove(entry);
        if (e != null) {
            this.totalWeight -= e;
        }
    }

    public T getRandomEntry(Random rand) {
        double threshold = rand.nextDouble() * this.totalWeight;
        for (Map.Entry<T, Integer> mapEntry : entries.entrySet()) {
            threshold = threshold - mapEntry.getValue();
            if (threshold <= 0.0) {
                return mapEntry.getKey();
            }
        }
        return null;
    }

    public int getWeight(T entry) {
        // (On the old code) -> WAT!?
        return entries.getOrDefault(entry, 0);
    }

}
