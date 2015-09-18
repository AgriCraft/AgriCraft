package com.InfinityRaider.AgriCraft.compatibility.bluepower;

import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;

public final class BluePowerHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.bluePower;
    }

    @Override
    protected void initPlants() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, (Item) Item.itemRegistry.getObject("bluepower:flax_seeds"));
        OreDictionary.registerOre("seedFlax", (Item) Item.itemRegistry.getObject("bluepower:flax_seeds"));
        OreDictionary.registerOre("cropFlax", Items.string);

        try {
            CropPlantHandler.registerPlant(new CropPlantBluePower((ItemSeeds) (Item) Item.itemRegistry.getObject("bluepower:flax_seeds")));
        } catch (DuplicateCropPlantException e) {
            e.printStackTrace();
        } catch (BlacklistedCropPlantException e) {
            e.printStackTrace();
        }
    }
}
