package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;

import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class RenderSeedStorageController extends RenderBlockCustomWood<TileEntitySeedStorageController> {
	
    public RenderSeedStorageController() {
        super(Blocks.blockSeedStorageController, new TileEntitySeedStorageController(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        Tessellator tessellator = Tessellator.instance;
        if(data.length>1) {
            this.teDummy.setOrientation(ForgeDirection.WEST);
        } else {
            this.teDummy.setOrientation(ForgeDirection.EAST);
        }
        this.rotateMatrix(teDummy, tessellator, false);
        tessellator.startDrawingQuads();
        this.doWorldRender(tessellator, Minecraft.getMinecraft().theWorld, 0, 0, 0, teDummy, Blocks.blockSeedStorageController, 0, 0, RenderBlocks.getInstance(), false);
        tessellator.draw();
        this.rotateMatrix(teDummy, tessellator, true);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        //call correct drawing methods
        if (tile instanceof TileEntitySeedStorageController) {
        	IIcon iron = net.minecraft.init.Blocks.iron_block.getIcon(0, 0);
        	
            TileEntitySeedStorageController storage = (TileEntitySeedStorageController) tile;
            IIcon icon = storage.getIcon();
            int cm = storage.colorMultiplier();
            //casing
            drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, iron, cm);
            drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, iron, cm);
            drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, iron, cm);
            drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, iron, cm);
            drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, iron, cm);
            //drawer
            drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, icon, cm);
            //handle
            drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, iron, cm);
            //frame
            drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, icon, cm);
            drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, icon, cm);
            drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, icon, cm);
            drawScaledPrism(tessellator, 4, 3, 0, 12, 4, 1, icon, cm);
            //trace
            renderSides(tessellator, storage);
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        return true;
    }

    private void renderSides(Tessellator tessellator, TileEntitySeedStorageController seedStorage) {
        IIcon icon = seedStorage.getIcon();

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

    @Override
    public boolean shouldBehaveAsTESR() {
        return false;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
