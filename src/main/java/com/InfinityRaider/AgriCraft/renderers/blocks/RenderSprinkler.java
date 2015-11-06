package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.renderers.models.ModelSprinkler;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
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
        renderConnection(sprinkler);
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

    private void renderConnection(TileEntitySprinkler sprinkler) {
        //set up tessellator
        Tessellator tessellator = Tessellator.instance;
        //start GL
        GL11.glPushMatrix();
            //disable lighting so the plants render bright
            GL11.glDisable(GL11.GL_LIGHTING);
            //bind the texture
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
            tessellator.startDrawingQuads();
            tessellator.addTranslation(0, 4 * Constants.UNIT, 0);
            drawScaledPrism(tessellator, 4, 8, 4, 12, 16, 12, sprinkler.getChannelIcon(), net.minecraft.init.Blocks.planks.colorMultiplier(sprinkler.getWorldObj(), sprinkler.xCoord, sprinkler.yCoord, sprinkler.zCoord));
            tessellator.addTranslation(0, -4 * Constants.UNIT, 0);
            tessellator.draw();
            //don't forget to enable lighting again
            GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
    }
}
