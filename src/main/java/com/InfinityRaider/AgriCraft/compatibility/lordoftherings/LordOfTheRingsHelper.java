package com.InfinityRaider.AgriCraft.compatibility.lordoftherings;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.oredict.OreDictionary;

public class LordOfTheRingsHelper extends ModHelper {
    @Override
    protected void initPlants() {
        Item lettuce = (Item) Item.itemRegistry.getObject("lotr:item.lotr:lettuce");
        Block lettucePlant = (Block) Block.blockRegistry.getObject("lotr:tile.lotr:lettuce");
        OreDictionary.registerOre("seedLettuce", lettuce);
        OreDictionary.registerOre("cropLettuce", lettuce);
        OreDictionary.registerOre(Names.OreDict.listAllseed, lettuce);
        try {
            CropPlantHandler.registerPlant(new CropPlantLotR(lettuce, lettuce, lettucePlant, true));
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }

        Item flaxSeed = (Item) Item.itemRegistry.getObject("lotr:item.lotr:flaxSeeds");
        Item flax = (Item) Item.itemRegistry.getObject("lotr:item.lotr:flax");
        Block flaxPlant = (Block) Block.blockRegistry.getObject("lotr:tile.lotr:flax");
        OreDictionary.registerOre("seedFlax", flaxSeed);
        OreDictionary.registerOre("cropFlax", flax);
        OreDictionary.registerOre(Names.OreDict.listAllseed, flaxSeed);
        try {
            CropPlantHandler.registerPlant(new CropPlantLotR(flaxSeed, flax, flaxPlant, false));
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }

        Item pipeWeedSeed = (Item) Item.itemRegistry.getObject("lotr:item.lotr:pipeweedSeeds");
        Item pipeWeed = (Item) Item.itemRegistry.getObject("lotr:item.lotr.pipeweedLeaf");
        Block pipeWeedPlant = (Block) Block.blockRegistry.getObject("lotr:tile.lotr:pipeweed");
        OreDictionary.registerOre("seedFlax", pipeWeedSeed);
        OreDictionary.registerOre("cropFlax", pipeWeed);
        OreDictionary.registerOre(Names.OreDict.listAllseed, pipeWeedSeed);
        try {
            CropPlantHandler.registerPlant(new CropPlantLotR(pipeWeedSeed, pipeWeed, pipeWeedPlant, false));
        } catch (Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    @Override
    protected String modId() {
        return Names.Mods.lordOfTheRingsMod;
    }
}
