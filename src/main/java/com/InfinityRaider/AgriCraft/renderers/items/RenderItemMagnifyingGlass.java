package com.InfinityRaider.AgriCraft.renderers.items;

import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class RenderItemMagnifyingGlass extends RenderItemBase {
    protected RenderItemMagnifyingGlass(Item item) {
        super(item);
    }

    @Override
    protected void renderItemEntity(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityItem entityItem) {

    }

    @Override
    protected void renderItemEquipped(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player) {

    }

    @Override
    protected void renderItemEquippedFirstPerson(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player) {

    }

    @Override
    protected void renderItemInventory(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks) {

    }

    @Override
    protected void renderItemMap(ItemStack stack, Tessellator tessellator) {

    }
}
