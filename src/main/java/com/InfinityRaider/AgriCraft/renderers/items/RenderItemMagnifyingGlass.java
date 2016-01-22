package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderItemMagnifyingGlass extends RenderItemBase {
    protected RenderItemMagnifyingGlass(Item item) {
        super(item);
    }

    @Override
    protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemGui(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemGround(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    protected void renderItemFixed(TessellatorV2 tessellator, ItemStack item) {

    }

    @Override
    public boolean shouldRender3D(ItemStack stack) {
        return true;
    }
}
