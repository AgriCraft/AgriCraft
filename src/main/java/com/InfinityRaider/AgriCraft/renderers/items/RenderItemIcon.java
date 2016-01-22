package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.items.ItemBase;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.item.ItemStack;

public class RenderItemIcon extends RenderItemBase {
    public RenderItemIcon(ItemBase item) {
        super(item);
    }

    private void drawItemIcon3D(TessellatorV2 tessellatorV2, ItemStack item) {

    }

    @Override
    protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);

    }

    @Override
    protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemGui(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemGround(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    protected void renderItemFixed(TessellatorV2 tessellator, ItemStack item) {
        drawItemIcon3D(tessellator, item);
    }

    @Override
    public boolean shouldRender3D(ItemStack stack) {
        return true;
    }
}
