package com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant;

import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockStem;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.item.ItemSeeds;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

public abstract class CropPlantStem extends CropPlantGeneric {
    private final Block block;

    public CropPlantStem(ItemSeeds seed, Block block) {
        super(seed);
        this.block = block;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getStemIcon() {
        Block plant = ((ItemSeeds) getSeed().getItem()).getPlant(null, 0, 0, 0);
        if(plant instanceof BlockStem) {
            BlockStem stem = (BlockStem) plant;
            return stem.getStemIcon();
        }
        return this.getPlantIcon(7);
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
        IIcon icon = mature?getStemIcon():getPlantIcon(meta);
        PlantRenderer.renderStemPlant(x, y, z, renderer, icon, meta, block, mature);
    }
}
