package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
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
    protected void renderItemEntity(ItemStack stack, TessellatorV2 tessellator, EntityItem entityItem) {

    }

    @Override
    protected void renderItemEquipped(ItemStack stack, TessellatorV2 tessellator, EntityPlayer player) {

    }

    @Override
    protected void renderItemEquippedFirstPerson(ItemStack stack, TessellatorV2 tessellator, EntityPlayer player) {

    }

    @Override
    protected void renderItemInventory(ItemStack stack, TessellatorV2 tessellator) {

    }

    @Override
    protected void renderItemMap(ItemStack stack, TessellatorV2 tessellator) {

    }
}
