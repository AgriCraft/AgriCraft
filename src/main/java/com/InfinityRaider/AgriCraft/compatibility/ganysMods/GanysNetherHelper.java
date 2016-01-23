package com.InfinityRaider.AgriCraft.compatibility.ganysMods;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;

public class GanysNetherHelper extends ModHelper {
    @Override
    protected void initPlants() {
        ItemSeeds seedGhost = (ItemSeeds) Item.itemRegistry.getObject("ganysnether:ghostSeeds");
        Item cropGhost = (Item) Item.itemRegistry.getObject("ganysnether:spectreWheatItem");
        if(seedGhost != null && cropGhost != null) {
            Block plantGhost = seedGhost.getPlant(null, 0, 0, 0);
            if(plantGhost != null) {
                try {
                    CropPlantHandler.registerPlant(new CropPlantGanysNether(seedGhost, plantGhost, new ItemStack(cropGhost), 0));
                } catch (DuplicateCropPlantException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        ItemSeeds seedQuartzBerry = (ItemSeeds) Item.itemRegistry.getObject("ganysnether:quarzBerrySeeds");
        Item cropQuartzBerry = (Item) Item.itemRegistry.getObject("ganysnether:quarzBerry");
        if(seedQuartzBerry != null && cropQuartzBerry != null) {
            Block plantQuartzBerry = seedQuartzBerry.getPlant(null, 0, 0, 0);
            if(plantQuartzBerry != null) {
                try {
                    CropPlantHandler.registerPlant(new CropPlantGanysNether(seedQuartzBerry, plantQuartzBerry, new ItemStack(cropQuartzBerry), 1));
                } catch (DuplicateCropPlantException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        ItemSeeds seedHellBush = (ItemSeeds) Item.itemRegistry.getObject("ganysnether:hellBushSeeds");
        Item cropHellBush = (Item) Item.itemRegistry.getObject("ganysnether:lavaBerry");
        if(seedHellBush != null && cropHellBush != null) {
            Block plantHellBush = seedHellBush.getPlant(null, 0, 0, 0);
            if(plantHellBush != null) {
                try {
                    CropPlantHandler.registerPlant(new CropPlantGanysNether(seedHellBush, plantHellBush, new ItemStack(cropHellBush), 1));
                } catch (DuplicateCropPlantException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }

        ItemSeeds seedWither = (ItemSeeds) Item.itemRegistry.getObject("ganysnether:witherShrubSeeds");
        if(seedWither != null) {
            Block plantWither = seedWither.getPlant(null, 0, 0, 0);
            if(plantWither != null) {
                try {
                    CropPlantHandler.registerPlant(new CropPlantGanysNether(seedWither, plantWither, new ItemStack(Items.skull, 1, 1), 2));
                } catch (DuplicateCropPlantException e) {
                    LogHelper.printStackTrace(e);
                }
            }
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.ganysNether;
    }
}
