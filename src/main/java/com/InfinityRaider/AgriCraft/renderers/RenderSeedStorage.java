package com.InfinityRaider.AgriCraft.renderers;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.blocks.BlockSeedStorage;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.utility.RenderHelper;
import cpw.mods.fml.client.registry.ISimpleBlockRenderingHandler;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderSeedStorage implements ISimpleBlockRenderingHandler {
    private final TileEntitySeedStorage teDummy = new TileEntitySeedStorage();

    @Override
    public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
        Tessellator tessellator = Tessellator.instance;
        //set colors
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        tessellator.setColorRGBA_F(1, 1, 1, 1);
        //call correct drawing methods
        IIcon icon = teDummy.getIcon();
        //casing
        RenderHelper.drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, icon);
        RenderHelper.drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, icon);
        //drawer
        RenderHelper.drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 14.9F, icon);
        RenderHelper.drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, Blocks.iron_block.getIcon(0, 0));
        RenderHelper.drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, icon);
        RenderHelper.drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, icon);
        RenderHelper.drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, icon);
        RenderHelper.drawScaledPrism(tessellator, 4, 2, 0, 12, 4, 1, icon);
        //trace
        renderSides(tessellator, teDummy);

        //clear texture overrides
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glPopMatrix();
        renderer.clearOverrideBlockTexture();
    }

    @Override
    public boolean renderWorldBlock(IBlockAccess world, int x, int y, int z, Block block, int modelId, RenderBlocks renderer) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        Tessellator tessellator = Tessellator.instance;
        //translate Tesselator to the right position
        tessellator.addTranslation(x, y, z);
        //set colors
        tessellator.setBrightness(Blocks.planks.getMixedBrightnessForBlock(renderer.blockAccess, x, y, z));
        tessellator.setColorRGBA_F(1, 1, 1, 1);
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
            RenderHelper.drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 14.9F, icon);
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

    private void renderSides(Tessellator tessellator, TileEntitySeedStorage seedStorage) {
        BlockSeedStorage block = (BlockSeedStorage) com.InfinityRaider.AgriCraft.init.Blocks.blockSeedStorage;
        IIcon frontIcon = block.getIconForSide(0);
        IIcon sideIcon = block.getIconForSide(1);

        tessellator.draw();
        tessellator.startDrawingQuads();
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glColor3f(1, 1, 1);

        RenderHelper.drawScaledFaceXY(tessellator, 1, 1, 15, 15, frontIcon, Constants.unit - 0.0005F);
        RenderHelper.drawScaledFaceXY(tessellator, 5, 4, 11, 10, sideIcon, Constants.unit - 0.0005F);

        GL11.glPopMatrix();
        tessellator.draw();
        tessellator.startDrawingQuads();
        /*
        RenderHelper.drawScaledFaceXY(tessellator, 0, 0, 16, 16, sideIcon, 1);
        RenderHelper.drawScaledFaceYZ(tessellator, 0, 0, 16, 16, sideIcon, 0);
        RenderHelper.drawScaledFaceYZ(tessellator, 0, 0, 16, 16, sideIcon, 1);
        RenderHelper.drawScaledFaceXZ(tessellator, 0, 0, 16, 16, sideIcon, 0);
        RenderHelper.drawScaledFaceXZ(tessellator, 0, 0, 16, 16, sideIcon, 1);
        */
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
