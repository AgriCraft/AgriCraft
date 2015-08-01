package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
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

@SideOnly(Side.CLIENT)
public class RenderSeedStorage extends RenderBlockCustomWood {
    public RenderSeedStorage() {
        super(Blocks.blockSeedStorage, new TileEntitySeedStorage(), true);
    }

    @Override
    protected void renderInInventory(ItemRenderType type, ItemStack item, Object... data) {
        TessellatorV2 tessellator = TessellatorV2.instance;
        if(data.length>1) {
            this.teDummy.setOrientation(ForgeDirection.WEST);
        } else {
            this.teDummy.setOrientation(ForgeDirection.EAST);
        }
        this.rotateMatrix(teDummy, tessellator, false);
        tessellator.startDrawingQuads();
        this.doWorldRender(tessellator, Minecraft.getMinecraft().theWorld, 0, 0, 0, teDummy, Blocks.blockSeedStorage, 0, 0, RenderBlocks.getInstance(), false);
        tessellator.draw();
        this.rotateMatrix(teDummy, tessellator, true);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double x, double y, double z, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        //call correct drawing methods
        if (tile instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage storage = (TileEntitySeedStorage) tile;
            IIcon icon = storage.getIcon();
            //casing
            drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, icon);
            drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, icon);
            drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, icon);
            drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, icon);
            drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, icon);
            //drawer
            drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, icon);
            drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, net.minecraft.init.Blocks.iron_block.getIcon(0, 0));
            drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, icon);
            drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, icon);
            drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, icon);
            drawScaledPrism(tessellator, 4, 2, 0, 12, 4, 1, icon);
            //seed
            if(storage.hasLockedSeed()) {
                ItemStack seed = storage.getLockedSeed();
                drawSeed(tessellator, seed);
            }
            //trace
            renderSides(tessellator, storage);
        }
        //clear texture overrides
        renderer.clearOverrideBlockTexture();
        return true;
    }

    private void renderSides(Tessellator tessellator, TileEntitySeedStorage seedStorage) {
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
    }

    private void drawSeed(Tessellator tessellator, ItemStack seed) {
        tessellator.draw();
        tessellator.startDrawingQuads();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationItemsTexture);

        IIcon icon = seed.getItem().getIconFromDamage(seed.getItemDamage());
        drawScaledFaceXY(tessellator, 4, 3, 11, 10, icon, Constants.unit - 0.001F);

        tessellator.draw();
        tessellator.startDrawingQuads();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
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
