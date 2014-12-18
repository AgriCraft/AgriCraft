package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.models.ModelCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import net.minecraft.block.BlockBush;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemSeeds;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderCrop extends TileEntitySpecialRenderer{
    private ResourceLocation textureCross;
    private ResourceLocation textureEmpty;
    private final ModelCrop model;

    public RenderCrop() {
        this.model = new ModelCrop();
        this.textureCross = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/cropsCross.png");
        this.textureEmpty = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/cropsEmpty.png");
    }


    //This method gets called when Minecraft wants to render a crop
    @Override
    public void renderTileEntityAt(TileEntity entity, double x, double y, double z, float f) {
        TileEntityCrop crop = (TileEntityCrop) entity;
       //render the model
        GL11.glPushMatrix();                                                            //initiate first gl renderer
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);    //sets the rendering origin to the right spot
            this.setTexture(crop);                                                      //loads the right texture for the model
            GL11.glPushMatrix();                                                        //initiate second gl renderer
                GL11.glRotatef(180, 0F, 0F, 1F);                                        //rotate the renderer so the model doesn't render upside down
                this.model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);        //actually renders the model
            GL11.glPopMatrix();                                                         //close second gl renderer
        GL11.glPopMatrix();                                                             //close first gl renderer
        if(crop.hasPlant()) {overlay(crop,x,y,z);}                                      //if the crop has a plant, render the plant texture
    }

    //sets the correct texture for the crop
    private void setTexture(TileEntityCrop crop) {
        if(crop.crossCrop) {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.textureCross);
        }
        else {
            Minecraft.getMinecraft().renderEngine.bindTexture(this.textureEmpty);
        }
    }

    //overlays the plant texture
    private void overlay(TileEntityCrop crop,double x,double y,double z) {
        //set up tessellator
        Tessellator tessellator = Tessellator.instance;
        //grab the texture
        ResourceLocation resource = getResource(crop);
        //start GL
        GL11.glPushMatrix();
            GL11.glTranslated(x,y,z);
            //disable lighting so the plants render bright
            GL11.glDisable(GL11.GL_LIGHTING);
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(resource);
           drawPlant(tessellator, RenderHelper.getRenderType((ItemSeeds) crop.seed, crop.seedMeta));
        //don't forget to enable lighting again
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

    //draws the plant on the crops
    private static void drawPlant(Tessellator tessellator, int renderType) {
        tessellator.startDrawingQuads();
        if(renderType != 6) {
            //the shift along x and z axis
            int shift = 4;
            //shrink textures
            GL11.glScalef(0.8F, 0.8F, 0.8F);
            //plane 1 front right
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 16,  4, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 0, 4, 0, 16);
            //plane 1 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 16, 4, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 0, 4, 0, 16);
            //plane 1 back right
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 0, 4, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 16, 4, 16, 0);
            //plane 1 back left
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 0, 4, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 16, 4, 16, 0);
            //plane 2 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, shift*2, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16+shift*2, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16+shift*2, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, shift*2, 0, 0);
            //plane 2 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0-shift, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16-shift, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16-shift, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0-shift, 0, 0);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, shift*2, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, shift*2, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16+shift*2, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16+shift*2, 16, 16);
            //plane 2 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0-shift, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0-shift, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16-shift, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16-shift, 16, 16);
            //plane 3 front right
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 16, 16, 0, 0);
            //plane 3 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 16, 16, 0, 0);
            //plane 3 back right
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, shift*2, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16+shift*2, 0, 16, 16, 16);
            //plane 3 back left
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0-shift, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16-shift, 0, 16, 16, 16);
            //plane 4 front right
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16+shift*2, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, shift*2, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, shift*2, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16+shift*2, 0, 0);
            //plane 4 front left
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16-shift, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0-shift, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0-shift, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16-shift, 0, 0);
            //plane 4 back right
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16+shift*2, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16+shift*2, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, shift*2, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, shift*2, 16, 16);
            //plane 4 back left
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 16-shift, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 16-shift, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 0-shift, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 0-shift, 16, 16);
        }
        else {
            //plane 1 front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 4, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 4, 0, 16);
            //plane 1 back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 4, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 4, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 4, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 4, 16, 0);
            //plane 2 front
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0, 0, 0);
            //plane 2 back
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 0, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 0, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 16, 16, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 4, 0, 16, 16, 16);
            //plane 3 front
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 12, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 12, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 12, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 12, 0, 0);
            //plane 3 back
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 0, 12, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 0, 16, 12, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 16, 12, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 16, 0, 12, 16, 16);
            //plane 4 front
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 0, 16, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 0, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 16, 0, 0);
            //plane 4 back
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 16, 0, 16);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 16, 0, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 16, 0, 16, 0);
            RenderHelper.addScaledVertexWithUV(tessellator, 12, 0, 0, 16, 16);
        }
        tessellator.draw();
    }

    //gets the location for the crop texture from a seed
    private ResourceLocation getResource(TileEntityCrop crop) {
        BlockBush plant = SeedHelper.getPlant((ItemSeeds) crop.seed);
        if(plant!=null) {
            return RenderHelper.getResource(plant, RenderHelper.plantIconIndex((ItemSeeds) crop.seed, crop.seedMeta, crop.getBlockMetadata()));
        }
        else {
            return new ResourceLocation(Reference.MOD_ID.toLowerCase() + "textures/blocks/" + "fileNotFound" + ".png");
        }
    }
}
