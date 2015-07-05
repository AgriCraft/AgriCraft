package com.InfinityRaider.AgriCraft.compatibility.gardenstuff;

import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirementHandler;
import com.jaquadro.minecraft.gardencore.block.tile.TileEntityGarden;
import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class GardenStuffHelper extends ModHelper {
    public static BlockWithMeta getSoil(TileEntityGarden garden) {
        ItemStack substrate = garden.getSubstrate();
        BlockWithMeta block = null;
        if(substrate!=null && substrate.getItem()!=null && substrate.getItem() instanceof ItemBlock) {
            block = new BlockWithMeta(((ItemBlock) substrate.getItem()).field_150939_a, substrate.getItemDamage());
        }
        return block;
    }

    @Override
    protected void init() {

    }

    @Override
    protected void initPlants() {

    }

    @Override
    protected void postTasks() {
        registerSoils();
    }

    private void registerSoils() {
        GrowthRequirementHandler.addDefaultSoil(new BlockWithMeta((Block) Block.blockRegistry.getObject("GardenCore:garden_farmland"), 0));
    }

    @Override
    protected String modId() {
        return null;
    }
}

