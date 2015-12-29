package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.items.ItemClipping;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderItemClipping extends RenderItemBase {
    private final TextureAtlasSprite mainIcon;

    public RenderItemClipping(Item item) {
        super(item);
        this.mainIcon = ((ItemClipping) Items.clipping).getIcon(new ItemStack(item));
    }

    @Override
    protected void renderItemDefault(TessellatorV2 tessellator, ItemStack item) {
        drawIcons(tessellator, item);
    }

    private void drawIcons(TessellatorV2 tessellator, ItemStack clipping) {

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

        TextureAtlasSprite plantIcon = getPlantIcon(clipping);
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
    protected void renderItemGround(TessellatorV2 tessellator, ItemStack stack) {
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslatef(-0.5F, 0, 0);
        drawIcons(tessellator, stack);
        GL11.glTranslatef(0.5F, 0, 0);
        GL11.glRotatef(-180, 0, 1, 0);
    }

    @Override
    protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack stack) {
        float dx = 0F;
        float dz = 0.5F;
        GL11.glTranslatef(dx, 0, dz);
        drawIcons(tessellator, stack);
        GL11.glTranslatef(-dx, 0, -dz);
    }

    @Override
    protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack stack) {
        float a = 45;
        float dx = -0.5F;
        float dy = 0;
        float dz = 1;
        float scale = 1.5F;

        GL11.glRotatef(a, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        drawIcons(tessellator, stack);

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemHead(TessellatorV2 tessellator, ItemStack item) {
        drawIcons(tessellator, item);

    }

    @Override
    protected void renderItemGui(TessellatorV2 tessellator, ItemStack stack) {
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
        drawIcons(tessellator, stack);
        GL11.glEnable(GL11.GL_LIGHTING);

        GL11.glScalef(1/scale, 1/scale, 1/scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-b, 1, 0, 0);
        GL11.glRotatef(-a, 0, 1, 0);
    }

    @Override
    protected void renderItemFixed(TessellatorV2 tessellator, ItemStack stack) {

    }

    private TextureAtlasSprite getPlantIcon(ItemStack stack) {
        if(stack==null || stack.getItem()==null || stack.getTagCompound()==null) {
            return null;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return null;
        }
        return plant.getPrimaryPlantTexture(7);
    }
}
