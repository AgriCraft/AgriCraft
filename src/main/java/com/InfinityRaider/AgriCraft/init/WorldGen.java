package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.VillageCreationHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouse;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouseIrrigated;
import cpw.mods.fml.common.registry.VillagerRegistry;
import net.minecraft.world.gen.structure.MapGenStructureIO;

public class WorldGen {
    public static void init() {
        //add greenhouses to villages
        try {
            MapGenStructureIO.func_143031_a(StructureGreenhouse.class, Reference.MOD_ID + ":Greenhouse");
        } catch (Exception exception) {
            LogHelper.info("Failed to load greenhouse to villages");
        }
        VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseHandler());

        //add irrigated greenhouses to villages
        if(!ConfigurationHandler.disableIrrigation) {
            try {
                MapGenStructureIO.func_143031_a(StructureGreenhouseIrrigated.class, Reference.MOD_ID + ":GreenhouseIrrigated");
            } catch (Exception exception) {
                LogHelper.info("Failed to load greenhouse to villages");
            }
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseIrrigatedHandler());
        }
    }
}
