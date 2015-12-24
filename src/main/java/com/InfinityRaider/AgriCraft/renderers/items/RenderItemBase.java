package com.InfinityRaider.AgriCraft.renderers.items;

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

    /**
     * utility method used for debugging rendering
     */
    @SuppressWarnings("unused")
    protected void drawAxisSystem(boolean startDrawing) {
        TessellatorV2 tessellator = TessellatorV2.instance;

        if(startDrawing) {
            tessellator.startDrawingQuads();
        }

        tessellator.addVertexWithUV(-0.005F, 2, 0, 1, 0);
        tessellator.addVertexWithUV(0.005F, 2, 0, 0, 0);
        tessellator.addVertexWithUV(0.005F, -1, 0, 0, 1);
        tessellator.addVertexWithUV(-0.005F, -1, 0, 1, 1);

        tessellator.addVertexWithUV(2, -0.005F, 0, 1, 0);
        tessellator.addVertexWithUV(2, 0.005F, 0, 0, 0);
        tessellator.addVertexWithUV(-1, 0.005F, 0, 0, 1);
        tessellator.addVertexWithUV(-1, -0.005F, 0, 1, 1);

        tessellator.addVertexWithUV(0, -0.005F, 2, 1, 0);
        tessellator.addVertexWithUV(0, 0.005F, 2, 0, 0);
        tessellator.addVertexWithUV(0, 0.005F, -1, 0, 1);
        tessellator.addVertexWithUV(0, -0.005F, -1, 1, 1);

        if(startDrawing) {
            tessellator.draw();
        }
    }
}
