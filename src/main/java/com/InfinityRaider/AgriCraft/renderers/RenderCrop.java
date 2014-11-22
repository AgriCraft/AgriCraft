package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.models.ModelCrop;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
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
        //define 1/16th of a block
        double unit = Constants.unit;
        tessellator.startDrawingQuads();
        if(renderType != 6) {
            //the shift along x and z axis
            double shift = 4*unit;
            //shrink textures
            GL11.glScalef(0.8F, 0.8F, 0.8F);
            //plane 1 front right
            tessellator.addVertexWithUV(0+shift*2, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(0+shift*2, 1, unit * 4, 1, 0);
            tessellator.addVertexWithUV(1+shift*2, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(1+shift*2, 0, unit * 4, 0, 1);
            //plane 1 front left
            tessellator.addVertexWithUV(0-shift, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(0-shift, 1, unit * 4, 1, 0);
            tessellator.addVertexWithUV(1-shift, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(1-shift, 0, unit * 4, 0, 1);
            //plane 1 back right
            tessellator.addVertexWithUV(0+shift*2, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(1+shift*2, 0, unit * 4, 0, 1);
            tessellator.addVertexWithUV(1+shift*2, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(0+shift*2, 1, unit * 4, 1, 0);
            //plane 1 back left
            tessellator.addVertexWithUV(0-shift, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(1-shift, 0, unit * 4, 0, 1);
            tessellator.addVertexWithUV(1-shift, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(0-shift, 1, unit * 4, 1, 0);
            //plane 2 front right
            tessellator.addVertexWithUV(unit * 4, 0, 0+shift*2, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 0, 1+shift*2, 1, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 1+shift*2, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 0+shift*2, 0, 0);
            //plane 2 front left
            tessellator.addVertexWithUV(unit * 4, 0, 0-shift, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 0, 1-shift, 1, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 1-shift, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 0-shift, 0, 0);
            //plane 2 back right
            tessellator.addVertexWithUV(unit * 4, 0, 0+shift*2, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 0+shift*2, 0, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 1+shift*2, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 0, 1+shift*2, 1, 1);
            //plane 2 back right
            tessellator.addVertexWithUV(unit * 4, 0, 0-shift, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 0-shift, 0, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 1-shift, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 0, 1-shift, 1, 1);
            //plane 3 front right
            tessellator.addVertexWithUV(0+shift*2, 0, 1 - unit * 0, 0, 1);
            tessellator.addVertexWithUV(1+shift*2, 0, 1 - unit * 0, 1, 1);
            tessellator.addVertexWithUV(1+shift*2, 1, 1 - unit * 0, 1, 0);
            tessellator.addVertexWithUV(0+shift*2, 1, 1 - unit * 0, 0, 0);
            //plane 3 front left
            tessellator.addVertexWithUV(0-shift, 0, 1 - unit * 0, 0, 1);
            tessellator.addVertexWithUV(1-shift, 0, 1 - unit * 0, 1, 1);
            tessellator.addVertexWithUV(1-shift, 1, 1 - unit * 0, 1, 0);
            tessellator.addVertexWithUV(0-shift, 1, 1 - unit * 0, 0, 0);
            //plane 3 back right
            tessellator.addVertexWithUV(0+shift*2, 0, 1 - unit * 0, 0, 1);
            tessellator.addVertexWithUV(0+shift*2, 1, 1 - unit * 0, 0, 0);
            tessellator.addVertexWithUV(1+shift*2, 1, 1 - unit * 0, 1, 0);
            tessellator.addVertexWithUV(1+shift*2, 0, 1 - unit * 0, 1, 1);
            //plane 3 back left
            tessellator.addVertexWithUV(0-shift, 0, 1 - unit * 0, 0, 1);
            tessellator.addVertexWithUV(0-shift, 1, 1 - unit * 0, 0, 0);
            tessellator.addVertexWithUV(1-shift, 1, 1 - unit * 0, 1, 0);
            tessellator.addVertexWithUV(1-shift, 0, 1 - unit * 0, 1, 1);
            //plane 4 front right
            tessellator.addVertexWithUV(1 - unit * 0, 0, 1+shift*2, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 0, 0+shift*2, 1, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 0+shift*2, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 1+shift*2, 0, 0);
            //plane 4 front left
            tessellator.addVertexWithUV(1 - unit * 0, 0, 1-shift, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 0, 0-shift, 1, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 0-shift, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 1-shift, 0, 0);
            //plane 4 back right
            tessellator.addVertexWithUV(1 - unit * 0, 0, 1+shift*2, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 1+shift*2, 0, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 0+shift*2, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 0, 0+shift*2, 1, 1);
            //plane 4 back left
            tessellator.addVertexWithUV(1 - unit * 0, 0, 1-shift, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 1-shift, 0, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 1, 0-shift, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 0, 0, 0-shift, 1, 1);
        }
        else {
            //plane 1 front
            tessellator.addVertexWithUV(0, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(0, 1, unit * 4, 1, 0);
            tessellator.addVertexWithUV(1, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(1, 0, unit * 4, 0, 1);
            //plane 1 back
            tessellator.addVertexWithUV(0, 0, unit * 4, 1, 1);
            tessellator.addVertexWithUV(1, 0, unit * 4, 0, 1);
            tessellator.addVertexWithUV(1, 1, unit * 4, 0, 0);
            tessellator.addVertexWithUV(0, 1, unit * 4, 1, 0);
            //plane 2 front
            tessellator.addVertexWithUV(unit * 4, 0, 0, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 0, 1, 1, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 1, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 0, 0, 0);
            //plane 2 back
            tessellator.addVertexWithUV(unit * 4, 0, 0, 0, 1);
            tessellator.addVertexWithUV(unit * 4, 1, 0, 0, 0);
            tessellator.addVertexWithUV(unit * 4, 1, 1, 1, 0);
            tessellator.addVertexWithUV(unit * 4, 0, 1, 1, 1);
            //plane 3 front
            tessellator.addVertexWithUV(0, 0, 1 - unit * 4, 0, 1);
            tessellator.addVertexWithUV(1, 0, 1 - unit * 4, 1, 1);
            tessellator.addVertexWithUV(1, 1, 1 - unit * 4, 1, 0);
            tessellator.addVertexWithUV(0, 1, 1 - unit * 4, 0, 0);
            //plane 3 back
            tessellator.addVertexWithUV(0, 0, 1 - unit * 4, 0, 1);
            tessellator.addVertexWithUV(0, 1, 1 - unit * 4, 0, 0);
            tessellator.addVertexWithUV(1, 1, 1 - unit * 4, 1, 0);
            tessellator.addVertexWithUV(1, 0, 1 - unit * 4, 1, 1);
            //plane 4 front
            tessellator.addVertexWithUV(1 - unit * 4, 0, 1, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 4, 0, 0, 1, 1);
            tessellator.addVertexWithUV(1 - unit * 4, 1, 0, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 4, 1, 1, 0, 0);
            //plane 4 back
            tessellator.addVertexWithUV(1 - unit * 4, 0, 1, 0, 1);
            tessellator.addVertexWithUV(1 - unit * 4, 1, 1, 0, 0);
            tessellator.addVertexWithUV(1 - unit * 4, 1, 0, 1, 0);
            tessellator.addVertexWithUV(1 - unit * 4, 0, 0, 1, 1);
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
