package com.InfinityRaider.AgriCraft.renderers;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.client.IItemRenderer;

public class RenderItemSeedAnalyzer implements IItemRenderer {
    TileEntitySpecialRenderer renderer;
    private TileEntity tileEntity;

    public RenderItemSeedAnalyzer(TileEntitySpecialRenderer renderer, TileEntity tileEntity) {
        this.renderer = renderer;
        this.tileEntity = tileEntity;
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
        this.renderer.renderTileEntityAt(this.tileEntity, 0.0, 0.0, 0.0, 0.0F);
    }
}
