package com.InfinityRaider.AgriCraft.renderers.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderItemBase implements IItemRenderer {
    private static HashMap<Item, RenderItemBase> renderers = new HashMap<Item, RenderItemBase>();

    protected RenderItemBase(Item item) {
        if(!renderers.containsKey(item)) {
            MinecraftForgeClient.registerItemRenderer(item, this);
            renderers.put(item, this);
        }
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
        Tessellator tessellator = Tessellator.instance;
        GL11.glPushMatrix();
        switch(type) {
            case ENTITY: renderItemEntity(item, tessellator, (RenderBlocks) data[0], (EntityItem) data[1]); break;
            case EQUIPPED: renderItemEquipped(item, tessellator, (RenderBlocks) data[0], (EntityPlayer) data[1]); break;
            case EQUIPPED_FIRST_PERSON: renderItemEquippedFirstPerson(item, tessellator, (RenderBlocks) data[0], (EntityPlayer) data[1]); break;
            case INVENTORY: renderItemInventory(item, tessellator, (RenderBlocks) data[0]); break;
            case FIRST_PERSON_MAP: renderItemMap(item, tessellator); break;
        }
        GL11.glPopMatrix();
    }

    protected abstract void renderItemEntity(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityItem entityItem);

    protected abstract void renderItemEquipped(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player);

    protected abstract void renderItemEquippedFirstPerson(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player);

    protected abstract void renderItemInventory(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks);

    protected abstract void renderItemMap(ItemStack stack, Tessellator tessellator);

    /**
     * utility method used for debugging rendering
     */
    @SuppressWarnings("unused")
    protected void drawAxisSystem(boolean startDrawing) {
        Tessellator tessellator = Tessellator.instance;

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
