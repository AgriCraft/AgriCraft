package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemClipping extends RenderItemBase {
    public RenderItemClipping(Item item) {
        super(item);
    }

    private void drawIcons(TessellatorV2 tessellator) {
        tessellator.startDrawingQuads();

        //This uses clipping texture
        tessellator.addVertexWithUV(0, 0, 0, 0, 16);
        tessellator.addVertexWithUV(1, 0, 0,16, 16);
        tessellator.addVertexWithUV(1, 1, 0, 16, 0);
        tessellator.addVertexWithUV(0, 1, 0, 0, 0);

        tessellator.addVertexWithUV(0, 0, 0, 0, 16);
        tessellator.addVertexWithUV(0, 1, 0, 0, 0);
        tessellator.addVertexWithUV(1, 1, 0, 16, 0);
        tessellator.addVertexWithUV(1, 0, 0, 16, 16);

        tessellator.draw();

        if(true /* check if planticon is not null */) {
            float unit = Constants.UNIT;
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

            tessellator.startDrawingQuads();

            //this uses the plant texture
            tessellator.addVertexWithUV(4*unit, 4*unit, 0.001F, 0, 16);
            tessellator.addVertexWithUV(12*unit, 4*unit, 0.001F, 16, 16);
            tessellator.addVertexWithUV(12*unit, 12*unit, 0.001F, 16, 0);
            tessellator.addVertexWithUV(4*unit, 12*unit, 0.001F, 0, 0);

            tessellator.addVertexWithUV(4*unit, 4*unit, -0.001F, 0, 16);
            tessellator.addVertexWithUV(4*unit, 12*unit, -0.001F, 0, 0);
            tessellator.addVertexWithUV(12*unit, 12*unit, -0.001F, 16, 0);
            tessellator.addVertexWithUV(12*unit, 4*unit, -0.001F, 16, 16);

            tessellator.draw();

        }
    }

    @Override
    protected void renderItemEntity(ItemStack stack, TessellatorV2 tessellator, EntityItem entityItem) {
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslatef(-0.5F, 0, 0);
        drawIcons(tessellator);
        GL11.glTranslatef(0.5F, 0, 0);
        GL11.glRotatef(-180, 0, 1, 0);
    }

    @Override
    protected void renderItemEquipped(ItemStack stack, TessellatorV2 tessellator, EntityPlayer player) {
        float dx = 0F;
        float dz = 0.5F;
        GL11.glTranslatef(dx, 0, dz);
        drawIcons(tessellator);
        GL11.glTranslatef(-dx, 0, -dz);
    }

    @Override
    protected void renderItemEquippedFirstPerson(ItemStack stack, TessellatorV2 tessellator, EntityPlayer player) {
        float a = 45;
        float dx = -0.5F;
        float dy = 0;
        float dz = 1;
        float scale = 1.5F;

        GL11.glRotatef(a, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        drawIcons(tessellator);

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemInventory(ItemStack stack, TessellatorV2 tessellator) {
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
        drawIcons(tessellator);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-b, 1, 0, 0);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemMap(ItemStack stack, TessellatorV2 tessellator) {

    }

    /*
    private IIcon getPlantIcon(ItemStack stack) {
        if(stack==null || stack.getItem()==null || stack.getTagCompound()==null) {
            return null;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return null;
        }
        return plant.getPlantIcon(7);
    }
    */
}
