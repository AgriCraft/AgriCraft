package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.utility.WorldCoordinates;
import net.minecraft.tileentity.TileEntity;

import java.util.HashMap;

public final class MultiBlockCache {
    private static HashMap<Integer, MultiBlockCache> INSTANCES = new HashMap<Integer, MultiBlockCache>();

    private HashMap<WorldCoordinates, MultiBlockLogic> cache;
    private HashMap<WorldCoordinates, Integer> cacheCount;

    private MultiBlockCache() {
        this.cache = new HashMap<WorldCoordinates, MultiBlockLogic>();
        this.cacheCount = new HashMap<WorldCoordinates, Integer>();
    }

    public static MultiBlockCache getCache(int dimension) {
        if(!INSTANCES.containsKey(dimension)) {
            INSTANCES.put(dimension, new MultiBlockCache());
        }
        return INSTANCES.get(dimension);
    }

    public void addToCache(MultiBlockLogic logic, int rootX, int rootY, int rootZ) {
        WorldCoordinates coords = new WorldCoordinates(rootX, rootY, rootZ);
        if(!this.isCached(coords)) {
            createCache(logic, coords);
        } else {
            MultiBlockLogic cachedLogic = cache.get(coords);
            TileEntity newLogicRoot = logic.getRootComponent().getTileEntity();
            if(newLogicRoot.xCoord==rootX && newLogicRoot.yCoord==rootY && newLogicRoot.zCoord==rootZ) {
                cachedLogic.setRootComponent(logic.getRootComponent());
            }
            logic.getRootComponent().setMultiBlockLogic(cachedLogic);
        }
    }

    private boolean isCached(WorldCoordinates coords) {
        return cache.containsKey(coords);
    }

    private void createCache(MultiBlockLogic logic, WorldCoordinates coords) {
        cache.put(coords, logic);
        cacheCount.put(coords, 1);
    }
}
