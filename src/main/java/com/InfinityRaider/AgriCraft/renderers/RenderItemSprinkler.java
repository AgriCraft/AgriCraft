package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.models.ModelSprinkler;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderItemSprinkler implements IItemRenderer {
    private ResourceLocation texture;
    private final ModelSprinkler model;

    public RenderItemSprinkler() {
        this.texture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/sprinkler.png");
        this.model = new ModelSprinkler();
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
        this.renderModel(0, 0, 0);
    }

    private void renderModel(double x, double y, double z) {
        //render the model
        GL11.glPushMatrix();                                                            //initiate first gl renderer
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);    //sets the rendering origin to the right spot
            Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);            //loads texture for the model
            GL11.glPushMatrix();                                                        //initiate second gl renderer
                GL11.glRotatef(180, 0F, 0F, 1F);                                        //rotate the renderer so the model doesn't render upside down
                this.model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);        //actually renders the model
            GL11.glPopMatrix();                                                         //close second gl renderer
            GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        renderConnection(x, y, z);
    }

    private void renderConnection(double x, double y, double z) {
        //set up tessellator
        Tessellator tessellator = Tessellator.instance;
        //grab the texture
        ResourceLocation resource = RenderHelper.getBlockResource(Blocks.planks.getIcon(0, 0));
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
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 12, 12, 4, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 12, 4, 12, 4);
                //top face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 4, 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 12, 4, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 12, 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 4, 12, 4);
                //top face
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 4, 4, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 4, 12, 4);
                RenderHelper.addScaledVertexWithUV(tessellator, 12, 20, 12, 12, 12);
                RenderHelper.addScaledVertexWithUV(tessellator, 4, 20, 12, 4, 12);
            tessellator.draw();
        //don't forget to enable lighting again
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }

}
