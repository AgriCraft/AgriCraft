package com.InfinityRaider.AgriCraft.compatibility.bluepower;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.exception.BlacklistedCropPlantException;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import com.bluepowermod.init.BPItems;
import net.minecraft.init.Items;
import net.minecraft.item.ItemSeeds;
import net.minecraftforge.oredict.OreDictionary;

public final class BluePowerHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.bluePower;
    }

    @Override
    protected void init() {
        OreDictionary.registerOre(Names.OreDict.listAllseed, BPItems.flax_seeds);
        OreDictionary.registerOre("seedFlax", BPItems.flax_seeds);
        OreDictionary.registerOre("cropFlax", Items.string);
    }

    @Override
    protected void initPlants() {
        try {
            CropPlantHandler.registerPlant(new CropPlantBluePower((ItemSeeds) BPItems.flax_seeds));
        } catch (DuplicateCropPlantException e) {
            e.printStackTrace();
        } catch (BlacklistedCropPlantException e) {
            e.printStackTrace();
        }
    }
}
