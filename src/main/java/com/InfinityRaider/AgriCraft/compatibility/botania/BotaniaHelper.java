package com.InfinityRaider.AgriCraft.compatibility.botania;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Data;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class BotaniaHelper extends ModHelper {
    public static ArrayList<BlockModPlant> botaniaCrops = new ArrayList<BlockModPlant>();
    public static ArrayList<ItemModSeed> botaniaSeeds = new ArrayList<ItemModSeed>();

    @Override
    protected void initPlants() {
        for (int i = 0; i < 16; i++) {
            Object[] args = {Data.botania[i], new ItemStack((Item) Item.itemRegistry.getObject("Botania:petal"), 1, i), new BlockWithMeta(Blocks.dirt, 2), 3, RenderMethod.CROSSED, new ItemStack((Block) Block.blockRegistry.getObject("Botania:flower"), 1, i)};
            BlockModPlant plant;
            try {
                plant = new BlockModPlantBotania(args);
            } catch (Exception e) {
                if (ConfigurationHandler.debug) {
                    e.printStackTrace();
                }
                return;
            }
            botaniaCrops.add(plant);
            botaniaSeeds.add(plant.getSeed());
        }
        LogHelper.info("Botania crops registered");
    }

    @Override
    protected String modId() {
        return Names.Mods.botania;
    }

    static boolean useAlternateTextures() {
        try {
            Class configClass = Class.forName("vazkii.botania.common.core.handler.ConfigHandler");
            return (Boolean) configClass.getField("altFlowerTextures").get(null);
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
            return false;
        }
    }
}
