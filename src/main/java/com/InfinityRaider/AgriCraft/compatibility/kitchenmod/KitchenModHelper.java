package com.InfinityRaider.AgriCraft.compatibility.kitchenmod;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;

public class KitchenModHelper extends ModHelper {
    @Override
    protected String modId() {
        return Names.Mods.kitchenMod;
    }

    @Override
    protected void initPlants() {
        Item fruitTomato = (Item) Item.itemRegistry.getObject("kitchen:tomato");
        Block plantTomato = (Block) Block.blockRegistry.getObject("kitchen:tomato_crop");
        if(fruitTomato != null && plantTomato != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantKitchenMod(plantTomato, fruitTomato));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }

        Item fruitLettuce = (Item) Item.itemRegistry.getObject("kitchen:lettuce");
        Block plantLettuce = (Block) Block.blockRegistry.getObject("kitchen:lettuce_crop");
        if(fruitLettuce != null && plantLettuce != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantKitchenMod(plantLettuce, fruitLettuce));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }

        Item fruitPeanut = (Item) Item.itemRegistry.getObject("kitchen:peanut");
        Block plantPeanut = (Block) Block.blockRegistry.getObject("kitchen:peanut_crop");
        if(fruitPeanut != null && plantPeanut != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantKitchenMod(plantPeanut, fruitPeanut));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }

        Item fruitStrawberry = (Item) Item.itemRegistry.getObject("kitchen:strawberry");
        Block plantStrawberry = (Block) Block.blockRegistry.getObject("kitchen:strawberry_crop");
        if(fruitStrawberry != null && plantStrawberry != null) {
            try {
                CropPlantHandler.registerPlant(new CropPlantKitchenMod(plantStrawberry, fruitStrawberry));
            } catch (DuplicateCropPlantException e) {
                LogHelper.printStackTrace(e);
            }
        }
    }
}
