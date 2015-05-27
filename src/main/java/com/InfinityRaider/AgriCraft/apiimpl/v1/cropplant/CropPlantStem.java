package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import java.util.ArrayList;

public class CropPlantStem extends CropPlantGeneric {
    private final Block block;

    public CropPlantStem(ItemSeeds seed, Block block) {
        super(seed);
        this.block = block;
    }

    @Override
    public int transformMeta(int growthStage) {
        return growthStage;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getPlantIcon(int growthStage) {
        if(growthStage<7) {
            //for the Vanilla SeedItem class the arguments for this method are not used
            return super.getPlantIcon(growthStage);
        }
        else {
            return getStemIcon();
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getStemIcon() {
        BlockStem plant = (BlockStem) ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0 ,0);
        return plant.getStemIcon();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean renderAsFlower() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderPlantInCrop(IBlockAccess world, int x, int y, int z, RenderBlocks renderer) {
        int meta = world.getBlockMetadata(x, y, z);
        boolean mature = isMature(world, x, y ,z);
        Block vine = ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
        PlantRenderer.renderStemPlant(x, y, z, renderer, getPlantIcon(meta), meta, vine, block, mature);
    }

    @Override
    public String getInformation() {
        return getSeed().getUnlocalizedName();
    }
}
