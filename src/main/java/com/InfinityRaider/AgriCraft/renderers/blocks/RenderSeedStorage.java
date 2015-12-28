package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSeedStorage extends RenderBlockCustomWood<TileEntitySeedStorage> {
    public RenderSeedStorage() {
        super(Blocks.blockSeedStorage, new TileEntitySeedStorage(), true);
    }

    @Override
    protected void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        this.teDummy.setOrientation(ForgeDirection.EAST);
        this.rotateMatrix(teDummy, tessellator, false);
        tessellator.startDrawingQuads();
        this.doWorldRender(tessellator, Minecraft.getMinecraft().theWorld, 0, 0, 0, null, Blocks.blockSeedStorage, null, teDummy, 0, 0, null, false);
        tessellator.draw();
        this.rotateMatrix(teDummy, tessellator, true);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        //call correct drawing methods
        if (tile instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage storage = (TileEntitySeedStorage) tile;
            if(callFromTESR) {
                //seed
                if(storage.hasLockedSeed()) {
                    drawSeed(storage.getLockedSeed());
                }
            } else {
                TextureAtlasSprite icon = storage.getIcon();
                int cm = storage.colorMultiplier();
                //casing
                drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon, cm);
                drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, icon, cm);
                drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, icon, cm);
                drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, icon, cm);
                drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, icon, cm);
                //drawer
                drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, icon, cm);
                drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite(), cm);    //TODO: find iron block icon
                drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, icon, cm);
                drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, icon, cm);
                drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, icon, cm);
                drawScaledPrism(tessellator, 4, 3, 0, 12, 4, 1, icon, cm);
                //trace
                renderSides(tessellator, storage);
            }
        }
        //clear texture overrides
        return true;
    }

    private void renderSides(TessellatorV2 tessellator, TileEntitySeedStorage seedStorage) {
        TextureAtlasSprite icon = seedStorage.getIcon();

        addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 2, 4, icon);
        addScaledVertexWithUV(tessellator, 1.5F, 15, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 1.5F, 1, 0.99F, 3, 3, icon);

        addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 14.5F, 1, 0.99F, 3, 3, icon);
        addScaledVertexWithUV(tessellator, 14.5F, 15, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4, icon);

        addScaledVertexWithUV(tessellator, 15, 14.5F, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 1, 14.5F, 0.99F, 3, 3, icon);
        addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4, icon);

        addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 3, 3, icon);
        addScaledVertexWithUV(tessellator, 1, 1.5F, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 15, 1.5F, 0.99F, 2, 4, icon);

        addScaledVertexWithUV(tessellator, 3.5F, 2.5F, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 3.5F, 11.5F, 0.99F, 2, 4, icon);
        addScaledVertexWithUV(tessellator, 5.5F, 11.5F, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 5.5F, 2.5F, 0.99F, 3, 3, icon);

        addScaledVertexWithUV(tessellator, 10.5F, 2.5F, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 10.5F, 11.5F, 0.99F, 2, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 11.5F, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 2.5F, 0.99F, 3, 3, icon);

        addScaledVertexWithUV(tessellator, 3.5F, 2.5F, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 3.5F, 4.5F, 0.99F, 2, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 4.5F, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 2.5F, 0.99F, 3, 3, icon);

        addScaledVertexWithUV(tessellator, 3.5F, 9.5F, 0.99F, 2, 3, icon);
        addScaledVertexWithUV(tessellator, 3.5F, 11.5F, 0.99F, 2, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 11.5F, 0.99F, 3, 4, icon);
        addScaledVertexWithUV(tessellator, 12.5F, 9.5F, 0.99F, 3, 3, icon);
    }

    /**
     * Render the seed as TESR
     */
    private void drawSeed(ItemStack seed) {
        float a = 180;
        float dx = 8* Constants.UNIT;
        float dy = 5* Constants.UNIT;
        float dz = 0.99F* Constants.UNIT;
        float f = 0.75F;

        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(a, 0, 1, 0);
        GL11.glScalef(f, f, f);

        EntityItem item = new EntityItem(AgriCraft.proxy.getClientWorld(), 0, 0, 0, seed);
        item.hoverStart = 0;
        Minecraft.getMinecraft().getRenderManager().renderEntityWithPosYaw(item, 0, 0, 0, 0, 0);

        GL11.glScalef(1F / f, 1F / f, 1F / f);
        GL11.glRotatef(-a, 0, 1, 0);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
