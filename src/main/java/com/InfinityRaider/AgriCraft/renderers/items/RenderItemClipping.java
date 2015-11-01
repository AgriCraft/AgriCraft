package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.reference.Constants;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemClipping extends RenderItemBase {
    public RenderItemClipping(Item item) {
        super(item);
    }

    private void drawIcons(Tessellator tessellator, IIcon mainIcon, IIcon plantIcon) {
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);

        tessellator.startDrawingQuads();

        tessellator.addVertexWithUV(0, 0, 0, mainIcon.getMinU(), mainIcon.getMaxV());
        tessellator.addVertexWithUV(1, 0, 0, mainIcon.getMaxU(), mainIcon.getMaxV());
        tessellator.addVertexWithUV(1, 1, 0, mainIcon.getMaxU(), mainIcon.getMinV());
        tessellator.addVertexWithUV(0, 1, 0, mainIcon.getMinU(), mainIcon.getMinV());

        tessellator.addVertexWithUV(0, 0, 0, mainIcon.getMinU(), mainIcon.getMaxV());
        tessellator.addVertexWithUV(0, 1, 0, mainIcon.getMinU(), mainIcon.getMinV());
        tessellator.addVertexWithUV(1, 1, 0, mainIcon.getMaxU(), mainIcon.getMinV());
        tessellator.addVertexWithUV(1, 0, 0, mainIcon.getMaxU(), mainIcon.getMaxV());

        tessellator.draw();

        if(plantIcon != null) {
            float unit = Constants.UNIT;
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

            tessellator.startDrawingQuads();

            tessellator.addVertexWithUV(4*unit, 4*unit, 0.001F, plantIcon.getMinU(), plantIcon.getMaxV());
            tessellator.addVertexWithUV(12*unit, 4*unit, 0.001F, plantIcon.getMaxU(), plantIcon.getMaxV());
            tessellator.addVertexWithUV(12*unit, 12*unit, 0.001F, plantIcon.getMaxU(), plantIcon.getMinV());
            tessellator.addVertexWithUV(4*unit, 12*unit, 0.001F, plantIcon.getMinU(), plantIcon.getMinV());

            tessellator.addVertexWithUV(4*unit, 4*unit, -0.001F, plantIcon.getMinU(), plantIcon.getMaxV());
            tessellator.addVertexWithUV(4*unit, 12*unit, -0.001F, plantIcon.getMinU(), plantIcon.getMinV());
            tessellator.addVertexWithUV(12*unit, 12*unit, -0.001F, plantIcon.getMaxU(), plantIcon.getMinV());
            tessellator.addVertexWithUV(12*unit, 4*unit, -0.001F, plantIcon.getMaxU(), plantIcon.getMaxV());

            tessellator.draw();

        }
    }

    @Override
    protected void renderItemEntity(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityItem entityItem) {
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslatef(-0.5F, 0, 0);
        drawIcons(tessellator, stack.getIconIndex(), getPlantIcon(stack));
        GL11.glTranslatef(0.5F, 0, 0);
        GL11.glRotatef(-180, 0, 1, 0);
    }

    @Override
    protected void renderItemEquipped(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player) {
        float dx = 0F;
        float dz = 0.5F;
        GL11.glTranslatef(dx, 0, dz);
        drawIcons(tessellator, stack.getIconIndex(), getPlantIcon(stack));
        GL11.glTranslatef(-dx, 0, -dz);
    }

    @Override
    protected void renderItemEquippedFirstPerson(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks, EntityPlayer player) {
        float a = 45;
        float dx = -0.5F;
        float dy = 0;
        float dz = 1;
        float scale = 1.5F;

        GL11.glRotatef(a, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        drawIcons(tessellator, stack.getIconIndex(), getPlantIcon(stack));

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemInventory(ItemStack stack, Tessellator tessellator, RenderBlocks renderBlocks) {
        float unit = Constants.UNIT;

        float dx = -13*unit;
        float dy = -17*unit;
        float dz = 1;

        float a = 45;
        float b = -45;

        float scale = unit*25;

        GL11.glRotatef(a, 0, 1, 0);
        GL11.glRotatef(b, 1, 0, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LIGHTING);
        drawIcons(tessellator, stack.getIconIndex(), getPlantIcon(stack));
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-b, 1, 0, 0);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemMap(ItemStack stack, Tessellator tessellator) {

    }

    private IIcon getPlantIcon(ItemStack stack) {
        if(stack==null || stack.getItem()==null || stack.stackTagCompound==null) {
            return null;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.stackTagCompound);
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return null;
        }
        return plant.getPlantIcon(7);
    }
}
