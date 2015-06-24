package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.VillageCreationHandler;
import com.InfinityRaider.AgriCraft.handler.VillagerTradeHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouse;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouseIrrigated;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class WorldGen {
    public static void init() {
        //register villagers
        if (ConfigurationHandler.villagerEnabled) {
            VillagerRegistry.instance().registerVillagerId(ConfigurationHandler.villagerID);
            VillagerRegistry.instance().registerVillageTradeHandler(ConfigurationHandler.villagerID, new VillagerTradeHandler());
        }

        //add greenhouses to villages
        MapGenStructureIO.func_143031_a(StructureGreenhouse.class, Reference.MOD_ID + ":Greenhouse");
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseHandler());

        //add irrigated greenhouses to villages
        if(!ConfigurationHandler.disableIrrigation) {
            MapGenStructureIO.func_143031_a(StructureGreenhouseIrrigated.class, Reference.MOD_ID + ":GreenhouseIrrigated");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseIrrigatedHandler());
        }
    }
}
