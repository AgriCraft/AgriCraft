package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityFence;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.lwjgl.opengl.GL11;

public class RenderBlockFence extends RenderBlockCustomWood<TileEntityFence> {
    public RenderBlockFence() {
        super(Blocks.blockFence, new TileEntityFence(), true);
    }

    @Override
    protected void renderInInventory(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
        TextureAtlasSprite icon = teDummy.getIcon();
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);
        //disable lighting
        GL11.glDisable(GL11.GL_LIGHTING);
        //tell the tessellator to start drawing
        tessellator.startDrawingQuads();

        drawScaledPrism(tessellator, 6, 0, 0, 10, 16, 4, icon);
        drawScaledPrism(tessellator, 6, 0, 12, 10, 16, 16, icon);
        drawScaledPrism(tessellator, 7, 12, 4, 9, 15, 12, icon);
        drawScaledPrism(tessellator, 7, 5, 4, 9, 8, 12, icon);

        tessellator.draw();
        //enable lighting
        GL11.glEnable(GL11.GL_LIGHTING);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double x, double y, double z, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        if(tile==null || !(tile instanceof TileEntityFence)) {
            return false;
        }
        TileEntityFence fence = (TileEntityFence) tile;
        TextureAtlasSprite icon = fence.getIcon();
        int cm = block.colorMultiplier(world, pos);
        drawScaledPrism(tessellator, 6, 0, 6, 10, 16, 10, icon, cm);
        if(fence.canConnect(ForgeDirection.EAST)) {
            drawScaledPrism(tessellator, 10, 12, 7, 16, 15, 9, icon, cm);
            drawScaledPrism(tessellator, 10, 6, 7, 16, 9, 9, icon, cm);
        }
        if(fence.canConnect(ForgeDirection.WEST)) {
            drawScaledPrism(tessellator, 0, 12, 7, 6, 15, 9, icon, cm);
            drawScaledPrism(tessellator, 0, 6, 7, 6, 9, 9, icon, cm);
        }
        if(fence.canConnect(ForgeDirection.SOUTH)) {
            drawScaledPrism(tessellator, 7, 12, 10, 9, 15, 16, icon, cm);
            drawScaledPrism(tessellator, 7, 6, 10, 9, 9, 16, icon, cm);
        }
        if(fence.canConnect(ForgeDirection.NORTH)) {
            drawScaledPrism(tessellator, 7, 12, 0, 9, 15, 6, icon, cm);
            drawScaledPrism(tessellator, 7, 6, 0, 9, 9, 6, icon, cm);
        }
        return true;
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
