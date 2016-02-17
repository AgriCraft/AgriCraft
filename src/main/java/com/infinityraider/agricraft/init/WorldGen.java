package com.InfinityRaider.AgriCraft.init;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.config.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.handler.VillageCreationHandler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouse;
import com.InfinityRaider.AgriCraft.world.StructureGreenhouseIrrigated;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraftforge.fml.common.registry.VillagerRegistry;

import java.util.Collection;

public class WorldGen {
    private static int villagerId;

    public static void init() {
        if (!ConfigurationHandler.disableWorldGen) {
            //register villagers
            if (ConfigurationHandler.villagerEnabled) {
                Collection<Integer> usedIds = VillagerRegistry.getRegisteredVillagers();
                int id = 5;
                while (usedIds.contains(id)) {
                    id++;
                }
                registerVillager(id);
            }

            //add greenhouses to villages
            MapGenStructureIO.registerStructureComponent(StructureGreenhouse.class, Reference.MOD_ID + ":Greenhouse");
            VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseHandler());

            //add irrigated greenhouses to villages
            if (!ConfigurationHandler.disableIrrigation) {
                MapGenStructureIO.registerStructureComponent(StructureGreenhouseIrrigated.class, Reference.MOD_ID + ":GreenhouseIrrigated");
                VillagerRegistry.instance().registerVillageCreationHandler(new VillageCreationHandler.GreenhouseIrrigatedHandler());
            }
        }
    }

    public static int getVillagerId() {
        return villagerId;
    }

    private static void registerVillager(int id) {
        VillagerRegistry.instance().registerVillagerId(id);
        AgriCraft.proxy.registerVillagerSkin(id, "textures/entities/villager.png");
        villagerId = id;
    }
}
