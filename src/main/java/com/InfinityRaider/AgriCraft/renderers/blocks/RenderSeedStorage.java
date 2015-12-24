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
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
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
    protected void renderInInventory(ItemStack item, Object... data) {
        TessellatorV2 tessellator = TessellatorV2.instance;
        if(data.length>1) {
            this.teDummy.setOrientation(ForgeDirection.WEST);
        } else {
            this.teDummy.setOrientation(ForgeDirection.EAST);
        }
        this.rotateMatrix(teDummy, tessellator, false);
        tessellator.startDrawingQuads();
        this.doWorldRender(tessellator, Minecraft.getMinecraft().theWorld, 0, 0, 0, null, null, Blocks.blockSeedStorage, teDummy, 0, 0);
        tessellator.draw();
        this.rotateMatrix(teDummy, tessellator, true);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, IBlockState state, Block block, TileEntity tile, int modelId, float f) {
        //call correct drawing methods
        if (tile instanceof TileEntitySeedStorage) {
            tessellator.startDrawingQuads();
            TileEntitySeedStorage storage = (TileEntitySeedStorage) tile;
            //seed
            if (storage.hasLockedSeed()) {
                drawSeed(storage.getLockedSeed());
            }
            int cm = storage.colorMultiplier();
            ResourceLocation texture = storage.getTexture(state, null);
            ResourceLocation ironTexture = Block.blockRegistry.getNameForObject(net.minecraft.init.Blocks.iron_block);
            //casing
            drawScaledPrism(tessellator, 0, 0, 0, 16, 1, 16, cm, texture);
            drawScaledPrism(tessellator, 0, 15, 0, 16, 16, 16, cm, texture);
            drawScaledPrism(tessellator, 0, 1, 0, 1, 15, 16, cm, texture);
            drawScaledPrism(tessellator, 15, 1, 0, 16, 15, 16, cm, texture);
            drawScaledPrism(tessellator, 1, 1, 15, 15, 15, 16, cm, texture);
            //drawer
            drawScaledPrism(tessellator, 1.1F, 1.1F, 1, 14.9F, 14.9F, 2, cm, texture);
            drawScaledPrism(tessellator, 7, 12, 0, 9, 13, 1, cm, ironTexture);
            drawScaledPrism(tessellator, 4, 3, 0, 5, 10, 1, cm, texture);
            drawScaledPrism(tessellator, 11, 3, 0, 12, 10, 1, cm, texture);
            drawScaledPrism(tessellator, 4, 10, 0, 12, 11, 1, cm, texture);
            drawScaledPrism(tessellator, 4, 3, 0, 12, 4, 1, cm, texture);
            //trace
            renderSides(tessellator, texture);
            tessellator.draw();
        }
        return true;
    }

    private void renderSides(TessellatorV2 tessellator, ResourceLocation texture) {
        bindTexture(texture);

        addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 2, 4);
        addScaledVertexWithUV(tessellator, 1.5F, 15, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 1.5F, 1, 0.99F, 3, 3);

        addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 14.5F, 1, 0.99F, 3, 3);
        addScaledVertexWithUV(tessellator, 14.5F, 15, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4);

        addScaledVertexWithUV(tessellator, 15, 14.5F, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 1, 14.5F, 0.99F, 3, 3);
        addScaledVertexWithUV(tessellator, 1, 15, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 15, 15, 0.99F, 2, 4);

        addScaledVertexWithUV(tessellator, 15, 1, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 1, 1, 0.99F, 3, 3);
        addScaledVertexWithUV(tessellator, 1, 1.5F, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 15, 1.5F, 0.99F, 2, 4);

        addScaledVertexWithUV(tessellator, 3.5F, 2.5F, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 3.5F, 11.5F, 0.99F, 2, 4);
        addScaledVertexWithUV(tessellator, 5.5F, 11.5F, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 5.5F, 2.5F, 0.99F, 3, 3);

        addScaledVertexWithUV(tessellator, 10.5F, 2.5F, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 10.5F, 11.5F, 0.99F, 2, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 11.5F, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 2.5F, 0.99F, 3, 3);

        addScaledVertexWithUV(tessellator, 3.5F, 2.5F, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 3.5F, 4.5F, 0.99F, 2, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 4.5F, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 2.5F, 0.99F, 3, 3);

        addScaledVertexWithUV(tessellator, 3.5F, 9.5F, 0.99F, 2, 3);
        addScaledVertexWithUV(tessellator, 3.5F, 11.5F, 0.99F, 2, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 11.5F, 0.99F, 3, 4);
        addScaledVertexWithUV(tessellator, 12.5F, 9.5F, 0.99F, 3, 3);
    }

    /**
     * Render the seed as TESR
     */
    private void drawSeed(ItemStack seed) {
        float a = 180;
        float dx = 8*Constants.UNIT;
        float dy = 5*Constants.UNIT;
        float dz = 0.99F*Constants.UNIT;
        float f = 0.75F;

        GL11.glPushMatrix();
        GL11.glTranslatef(dx, dy, dz);
        GL11.glRotatef(a, 0, 1, 0);
        GL11.glScalef(f, f, f);

        EntityItem item = new EntityItem(AgriCraft.proxy.getClientWorld(), 0, 0, 0, seed);
        item.hoverStart = 0;

        //TODO: render item

        GL11.glScalef(1F/f, 1F/f, 1F/f);
        GL11.glRotatef(-a, 0, 1, 0);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glPopMatrix();
    }
}
