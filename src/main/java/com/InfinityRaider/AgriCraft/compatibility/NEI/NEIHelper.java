package com.InfinityRaider.AgriCraft.compatibility.NEI;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;

public class NEIHelper extends ModHelper {
    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {

    }

    @Override
    protected String modId() {
        return Names.Mods.nei;
    }

    @Override
    protected void postTasks() {
        AgriCraft.proxy.initNEI();
    }
}
