package com.InfinityRaider.AgriCraft.compatibility.NEI;

import codechicken.nei.api.API;
import codechicken.nei.api.IConfigureNEI;
import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockCustomWood;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.arsmagica.ArsMagicaHelper;
import com.InfinityRaider.AgriCraft.compatibility.botania.BotaniaHelper;
import com.InfinityRaider.AgriCraft.compatibility.thaumcraft.ThaumcraftHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.*;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class NEIConfig implements IConfigureNEI {
    @Override
    public void loadConfig() {
        //register NEI recipe handler
        LogHelper.debug("Registering NEI recipe handlers");
        //mutation handler
        NEICropMutationHandler mutationHandler = new NEICropMutationHandler();
        API.registerRecipeHandler(mutationHandler);
        API.registerUsageHandler(mutationHandler);
        //crop product handler
        NEICropProductHandler productHandler = new NEICropProductHandler();
        API.registerRecipeHandler(productHandler);
        API.registerUsageHandler(productHandler);
        //hide crop blocks in NEI
        hideItems();
    }

    private static void hideItems() {
        LogHelper.debug("Hiding crops in NEI");
        for (int i = 0; i < 16; i++) {
            //hide crops block
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Blocks.blockCrop, 1, i));
            //hide water pad
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Blocks.blockWaterPad, 1, i));
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Blocks.blockWaterPadFull, 1, i));
            //hide clippings
            AgriCraft.proxy.hideItemInNEI(new ItemStack(Items.clipping, 1, i));
            //hide debugger
            if(!ConfigurationHandler.debug) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(Items.debugItem, 1, i));
            }
            //hide plant blocks
            for(BlockModPlant plant : Crops.crops) {
                AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
            }
            //hide botania crops
            if(ModHelper.allowIntegration(Names.Mods.botania)) {
                for(BlockModPlant plant : BotaniaHelper.botaniaCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide thaumcraft crops
            if(ModHelper.allowIntegration(Names.Mods.thaumcraft)) {
                for(BlockModPlant plant : ThaumcraftHelper.thaumcraftCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide ars magica crops
            if(ModHelper.allowIntegration(Names.Mods.arsMagica)) {
                for(BlockModPlant plant : ArsMagicaHelper.arsMagicaCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide resource crops
            if(ConfigurationHandler.resourcePlants) {
                for(BlockModPlant plant : ResourceCrops.vanillaCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
                for(BlockModPlant plant : ResourceCrops.modCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(plant, 1, i));
                }
            }
            //hide custom crops
            if(ConfigurationHandler.customCrops) {
                for (BlockModPlant customCrop:CustomCrops.customCrops) {
                    AgriCraft.proxy.hideItemInNEI(new ItemStack(customCrop, 1, i));
                }
            }
        }
        if(ConfigurationHandler.condenseCustomWoodInNei) {
            LogHelper.debug("Hiding custom wood objects");
            Field[] blocks = Blocks.class.getDeclaredFields();
            for (Field field : blocks) {
                try {
                    Object obj = field.get(null);
                    if(obj == null) {
                        continue;
                    }
                    if (BlockCustomWood.class.isAssignableFrom(obj.getClass())) {
                        Block block = (Block) obj;
                        ItemStack stack = new ItemStack(block);
                        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
                        list.add(stack);
                        API.setItemListEntries(stack.getItem(), list);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getName() {
        return Reference.MOD_ID+"_NEI";
    }

    @Override
    public String getVersion() {
        return  "1.0";
    }

}
