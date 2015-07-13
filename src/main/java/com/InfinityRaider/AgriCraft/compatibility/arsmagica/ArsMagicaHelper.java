package com.InfinityRaider.AgriCraft.compatibility.arsmagica;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.blocks.BlockModPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Names;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;

public class ArsMagicaHelper extends ModHelper {
    public static ArrayList<BlockModPlant> arsMagicaCrops = new ArrayList<BlockModPlant>();
    public static ArrayList<ItemModSeed> arsMagicaSeeds = new ArrayList<ItemModSeed>();

    @Override
    protected void init() {
    }

    @Override
    protected void initPlants() {
        //aum
        Item aum = (Item) Item.itemRegistry.getObject("arsmagica2:Aum");
        BlockModPlant cropAum;
        try {
            cropAum = new BlockModPlant(new Object[]{"Aum", new ItemStack(aum), 3, RenderMethod.CROSSED});
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return;
        }
        Block log = (Block) Block.blockRegistry.getObject("arsmagica2:WitchwoodLog");
        if (log != null) {
            cropAum.getGrowthRequirement().setRequiredBlock(new BlockWithMeta(log), RequirementType.NEARBY, false);
        }
        arsMagicaCrops.add(cropAum);
        arsMagicaSeeds.add(cropAum.getSeed());

        //tarma root
        Item tarmaRoot = (Item) Item.itemRegistry.getObject("arsmagica2:TarmaRoot");
        BlockModPlant cropTarmaRoot;
        try {
            cropTarmaRoot = new BlockModPlant(new Object[]{"TarmaRoot", new ItemStack(tarmaRoot), 3, RenderMethod.CROSSED});
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return;
        }
        cropTarmaRoot.getGrowthRequirement().setBrightnessRange(0, 8);
        arsMagicaCrops.add(cropTarmaRoot);
        arsMagicaSeeds.add(cropTarmaRoot.getSeed());

        //cerublossom
        Item cerublossom = (Item) Item.itemRegistry.getObject("arsmagica2:blueOrchid");
        BlockModPlant cropCerublossom;
        try {
            cropCerublossom = new BlockModPlant(new Object[]{"Cerublossom", new ItemStack(cerublossom), 3, RenderMethod.CROSSED});
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return;
        }
        arsMagicaCrops.add(cropCerublossom);
        arsMagicaSeeds.add(cropCerublossom.getSeed());

        //desert nova
        Item desertNova = (Item) Item.itemRegistry.getObject("arsmagica2:desertNova");
        BlockModPlant cropDesertNova;
        try {
            cropDesertNova = new BlockModPlant(new Object[]{"DesertNova", new ItemStack(desertNova), 3, RenderMethod.CROSSED});
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return;
        }
        cropDesertNova.getGrowthRequirement().setSoil(new BlockWithMeta(Blocks.sand));
        arsMagicaCrops.add(cropDesertNova);
        arsMagicaSeeds.add(cropDesertNova.getSeed());

        //wakebloom
        Item wakebloom = (Item) Item.itemRegistry.getObject("arsmagica2:wakebloom");
        BlockModPlant cropWakebloom;
        try {
            cropWakebloom = new BlockModPlant(new Object[]{"Wakebloom", new ItemStack(wakebloom), 3, RenderMethod.CROSSED});
        } catch (Exception e) {
            if (ConfigurationHandler.debug) {
                e.printStackTrace();
            }
            return;
        }
        cropWakebloom.getGrowthRequirement().setSoil(new BlockWithMeta(com.InfinityRaider.AgriCraft.init.Blocks.blockWaterPadFull));
        arsMagicaCrops.add(cropWakebloom);
        arsMagicaSeeds.add(cropWakebloom.getSeed());
    }

    @Override
    protected String modId() {
        return Names.Mods.arsMagica;
    }
}
