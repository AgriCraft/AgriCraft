package com.InfinityRaider.AgriCraft.utility.multiblock;

import com.InfinityRaider.AgriCraft.utility.WorldCoordinates;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.HashMap;

public final class MultiBlockCache {
    private static MultiBlockCache INSTANCE = new MultiBlockCache();

    private HashMap<WorldCoordinates, ArrayList<IMultiBlockComponent>> cache;
    private HashMap<WorldCoordinates, Integer> cacheLimits;

    private MultiBlockCache() {
        this.cache = new HashMap<WorldCoordinates, ArrayList<IMultiBlockComponent>>();
        this.cacheLimits = new HashMap<WorldCoordinates, Integer>();
    }

    public static MultiBlockCache getCache() {
        return INSTANCE;
    }

    public void addToCache(IMultiBlockComponent component, int rootX, int rootY, int rootZ) {
        WorldCoordinates coords = new WorldCoordinates(rootX, rootY, rootZ);
        if(!cache.containsKey(coords)) {
            cache.put(coords, new ArrayList<IMultiBlockComponent>());
            cacheLimits.put(coords, getSize(component));
        }
        ArrayList<IMultiBlockComponent> cachedComponents = cache.get(coords);
        if(isRoot(component, coords)) {
            cachedComponents.add(0, component);
        } else {
            cachedComponents.add(component);
        }
        if(cachedComponents.size()>=cacheLimits.get(coords)) {
            loadCache(cachedComponents);
            cache.remove(coords);
            cacheLimits.remove(coords);
        }
    }

    private boolean isRoot(IMultiBlockComponent component, WorldCoordinates coords) {
        TileEntity tile = component.getTileEntity();
        return tile.xCoord == coords.x() && tile.yCoord == coords.y() && tile.zCoord == coords.z();
    }

    private void loadCache(ArrayList<IMultiBlockComponent> components) {
        //We made sure the root is first in the array
        IMultiBlockComponent root = components.get(0);
        MultiBlockLogic logic = root.getMultiBlockLogic();
        logic.createMultiBlock();
    }

    private int getSize(IMultiBlockComponent<? extends MultiBlockLogic> component) {
        MultiBlockLogic logic = component.getMultiBlockLogic();
        return logic.getMultiBlockCount();
    }
}
