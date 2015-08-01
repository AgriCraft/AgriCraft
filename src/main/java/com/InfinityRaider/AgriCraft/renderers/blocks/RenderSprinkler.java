package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.models.ModelSprinkler;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderSprinkler extends RenderBlockBase {
    private ResourceLocation texture;
    private final ModelSprinkler model;
    private final TileEntitySprinkler sprinklerDummy;

    public RenderSprinkler() {
        super(Blocks.blockSprinkler, new TileEntitySprinkler(), true);
        this.texture = new ResourceLocation(Reference.MOD_ID.toLowerCase()+":textures/blocks/sprinkler.png");
        this.model = new ModelSprinkler();
        this.sprinklerDummy = new TileEntitySprinkler();
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        TileEntitySprinkler sprinkler= (TileEntitySprinkler) tile;
        //render the model
        GL11.glDisable(GL11.GL_LIGHTING);
        Minecraft.getMinecraft().renderEngine.bindTexture(this.texture);
        GL11.glTranslatef(0.5F, 1.5F, 0.5F);
        float angle = sprinkler.angle;
        GL11.glRotatef(angle, 0, -1, 0);
        GL11.glPushMatrix();
        GL11.glRotatef(180, 0F, 0F, 1F);
        this.model.render(null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
        GL11.glPopMatrix();
        GL11.glRotatef(-angle, 0, -1, 0);
        GL11.glTranslatef(-0.5F, -1.5F, -0.5F);
        GL11.glEnable(GL11.GL_LIGHTING);
        renderConnection(sprinkler, x, y, z);
        return true;
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glTranslatef(0, -0.25F, 0);
        this.doWorldRender(Tessellator.instance, Minecraft.getMinecraft().theWorld, 0, 0, 0, sprinklerDummy, Blocks.blockSprinkler, 0, 0, RenderBlocks.getInstance(), true);
        GL11.glTranslatef(0, 0.25F, 0);
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return false;
    }

    private void renderConnection(TileEntitySprinkler sprinkler, double x, double y, double z) {
        //set up tessellator
        Tessellator tessellator = Tessellator.instance;
        //grab the texture
        ResourceLocation resource = RenderHelper.getBlockResource(sprinkler.getChannelIcon());
        //start GL
        GL11.glPushMatrix();
            //disable lighting so the plants render bright
            GL11.glDisable(GL11.GL_LIGHTING);
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(resource);
            tessellator.startDrawingQuads();
                //first face
                addScaledVertexWithUV(tessellator, 4, 20, 12, 4, -4);
                addScaledVertexWithUV(tessellator, 4, 12, 12, 4, 4);
                addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 4);
                addScaledVertexWithUV(tessellator, 12, 20, 12, 12, -4);
                //second face
                addScaledVertexWithUV(tessellator, 4, 20, 4, 4, -4);
                addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                addScaledVertexWithUV(tessellator, 4, 12, 12, 12, 4);
                addScaledVertexWithUV(tessellator, 4, 20, 12, 12, -4);
                //third face
                addScaledVertexWithUV(tessellator, 4, 20, 4, 4, -4);
                addScaledVertexWithUV(tessellator, 12, 20, 4, 12, -4);
                addScaledVertexWithUV(tessellator, 12, 12, 4, 12, 4);
                addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                //fourth face
                addScaledVertexWithUV(tessellator, 12, 20, 4, 4, -4);
                addScaledVertexWithUV(tessellator, 12, 20, 12, 12, -4);
                addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 4);
                addScaledVertexWithUV(tessellator, 12, 12, 4, 4, 4);
                //bottom face
                addScaledVertexWithUV(tessellator, 4, 12, 4, 4, 4);
                addScaledVertexWithUV(tessellator, 12, 12, 4, 12, 4);
                addScaledVertexWithUV(tessellator, 12, 12, 12, 12, 12);
                addScaledVertexWithUV(tessellator, 4, 12, 12, 4, 12);
                //top face
                addScaledVertexWithUV(tessellator, 4, 20, 4, 4, 4);
                addScaledVertexWithUV(tessellator, 4, 20, 12, 4, 12);
                addScaledVertexWithUV(tessellator, 12, 20, 12, 12, 12);
                addScaledVertexWithUV(tessellator, 12, 20, 4, 12, 4);
            tessellator.draw();
            //don't forget to enable lighting again
            GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
