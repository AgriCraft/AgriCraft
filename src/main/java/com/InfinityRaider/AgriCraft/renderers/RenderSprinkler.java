package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.models.ModelSprinkler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySprinkler;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class RenderSprinkler extends TileEntitySpecialRenderer {
    private ResourceLocation texture;
    private final ModelSprinkler model;

    public RenderSprinkler() {
        this.texture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/sprinkler.png");
        this.model = new ModelSprinkler();
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float f) {
        TileEntitySprinkler sprinkler= (TileEntitySprinkler) tileEntity;
        //render the model
        GL11.glPushMatrix();                                                            //initiate first gl renderer
            GL11.glDisable(GL11.GL_LIGHTING);                                           //disable lighting
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);            //loads texture for the model
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);    //sets the rendering origin to the right spot
            GL11.glRotatef(sprinkler.angle, 0, -1, 0);                                  //rotate around y-axis
            GL11.glPushMatrix();                                                        //initiate second gl renderer
                GL11.glRotatef(180, 0F, 0F, 1F);                                        //rotate the renderer so the model doesn't render upside down
                this.model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);        //actually renders the model
            GL11.glPopMatrix();                                                         //close second gl renderer
            GL11.glEnable(GL11.GL_LIGHTING);                                            //enable lighting
        GL11.glPopMatrix();                                                             //close first gl renderer
        renderConnection(sprinkler, x, y, z);
    }

    private void renderConnection(TileEntitySprinkler sprinkler, double x, double y, double z) {
        //set up tessellator
        Tessellator tessellator = Tessellator.instance;
        //grab the texture
        ResourceLocation resource = RenderHelper.getBlockResource(sprinkler.getChannelIcon());
        //start GL
        GL11.glPushMatrix();
            GL11.glTranslated(x,y,z);
            //disable lighting so the plants render bright
            GL11.glDisable(GL11.GL_LIGHTING);
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(resource);
            tessellator.startDrawingQuads();
                //first face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 12, 4, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 12, 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 12, 12, -4);
                //second face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 4, 4, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 12, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 12, 12, -4);
                //third face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 4, 4, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 4, 12, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 4, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                //fourth face
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 4, 4, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 12, 12, -4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 4, 4, 4);
                //bottom face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 4, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 12, 4, 12);
            tessellator.draw();
            //don't forget to enable lighting again
            GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
