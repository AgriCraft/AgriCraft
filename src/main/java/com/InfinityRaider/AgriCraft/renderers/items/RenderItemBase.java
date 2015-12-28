package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.renderers.RenderUtil;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderItemBase {
    private static HashMap<Item, RenderItemBase> renderers = new HashMap<>();
    private static RenderUtil renderUtil = RenderUtil.getInstance();

    protected RenderItemBase(Item item) {
        if(!renderers.containsKey(item)) {
            //MinecraftForgeClient.registerItemRenderer(item, this);
            renderers.put(item, this);
        }
    }

    protected abstract void renderItemEntity(ItemStack stack, TessellatorV2 tessellator, EntityItem entityItem);

    protected abstract void renderItemEquipped(ItemStack stack, TessellatorV2 tessellator, EntityPlayer player);

    protected abstract void renderItemEquippedFirstPerson(ItemStack stack, TessellatorV2 tessellator,EntityPlayer player);

    protected abstract void renderItemInventory(ItemStack stack, TessellatorV2 tessellator);

    protected abstract void renderItemMap(ItemStack stack, TessellatorV2 tessellator);
}
