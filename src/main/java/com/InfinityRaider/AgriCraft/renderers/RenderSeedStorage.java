package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderSeedStorage extends RenderBlockBase {
    public RenderSeedStorage(Block block, int id) {
        super(block, id);
    }

    @Override
    protected void doInventoryRender(Block block, int meta, int model, RenderBlocks renderer) {

    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        //call correct drawing methods
        if (tileEntity instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage storage = (TileEntitySeedStorage) tileEntity;
            IIcon icon = storage.getIcon();
            //casing
            RenderHelper.drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon);
            RenderHelper.drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, icon);
            RenderHelper.drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, icon);
            RenderHelper.drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, icon);
            RenderHelper.drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, icon);
            //drawer
            RenderHelper.drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, icon);
            RenderHelper.drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, Blocks.iron_block.getIcon(0, 0));
            RenderHelper.drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, icon);
            RenderHelper.drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, icon);
            RenderHelper.drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, icon);
            RenderHelper.drawScaledPrism(tessellator, 4, 2, 0, 12, 4, 1, icon);
            //seed
            if(storage.hasLockedSeed()) {
                ItemStack seed = storage.getLockedSeed();
                drawSeed(tessellator, storage, seed);
            }
            //trace
            renderSides(tessellator, storage);
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        //translate tessellator back
        tessellator.addTranslation(-x, -y, -z);
        return true;
    }

    @Override
    protected Class<? extends TileEntity> getTileEnityClass() {
        return TileEntitySeedStorage.class;
    }

    private void renderSides(Tessellator tessellator, TileEntitySeedStorage seedStorage) {
        IIcon icon = seedStorage.getIcon();

        RenderHelper.addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 2, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 2, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1.5F, 15, 0.99F, 3, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1.5F, 1, 0.99F, 3, 3, icon);

        RenderHelper.addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 14.5F, 1, 0.99F, 3, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 14.5F, 15, 0.99F, 3, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4, icon);

        RenderHelper.addScaledVertexWithUV(tessellator, 15, 14.5F, 0.99F, 2, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1, 14.5F, 0.99F, 3, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 3, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4, icon);

        RenderHelper.addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 3, 3, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 1, 1.5F, 0.99F, 3, 4, icon);
        RenderHelper.addScaledVertexWithUV(tessellator, 15, 1.5F, 0.99F, 2, 4, icon);
    }

    private void drawSeed(Tessellator tessellator, TileEntitySeedStorage seedStorage, ItemStack seed) {
        tessellator.draw();
        tessellator.startDrawingQuads();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1, 1, 1);

        IIcon icon = seed.getItem().getIconFromDamage(seed.getItemDamage());
        RenderHelper.drawScaledFaceXY(tessellator, 4, 3, 11, 10, icon, Constants.unit-0.001F);

        GL11.glPopMatrix();
        tessellator.draw();
        tessellator.startDrawingQuads();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
    }

    @Override
    public boolean shouldRender3DInInventory(int modelId) {
        return true;
    }

    @Override
    public int getRenderId() {
        return AgriCraft.proxy.getRenderId(Constants.seedStorageId);
    }
}
