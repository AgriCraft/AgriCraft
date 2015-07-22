package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.common.util.ForgeDirection;

public class RenderItemSeedAnalyzer implements IItemRenderer {
    TileEntitySpecialRenderer renderer;
    private TileEntitySeedAnalyzer seedAnalyzer;

    public RenderItemSeedAnalyzer(TileEntitySpecialRenderer renderer, TileEntity tileEntity) {
        this.renderer = renderer;
        this.seedAnalyzer = (TileEntitySeedAnalyzer) tileEntity;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        seedAnalyzer.setDirection(ForgeDirection.SOUTH.ordinal());
        this.renderer.renderTileEntityAt(seedAnalyzer, 0.0, 0.0, 0.0, 0.0F);
    }
}
