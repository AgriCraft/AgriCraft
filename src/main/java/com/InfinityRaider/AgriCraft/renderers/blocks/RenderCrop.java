package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.init.AgriCraftBlocks;
import com.InfinityRaider.AgriCraft.reference.BlockStates;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase {
    public RenderCrop() {
        super(AgriCraftBlocks.blockCrop, false);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, BlockPos pos, Block block, IBlockState state, TileEntity tile, float partialTicks, int destroyStage, WorldRenderer renderer, boolean callFromTESR) {
        if (tile instanceof TileEntityCrop) {
            BlockCrop blockCrop = (BlockCrop) block;
            TileEntityCrop crop = (TileEntityCrop) tile;
            tessellator.translate(0, -3* Constants.UNIT, 0);
            drawScaledPrism(tessellator, 2, 0, 2, 3, 16, 3, blockCrop.getIcon());
            drawScaledPrism(tessellator, 13, 0, 2, 14, 16, 3, blockCrop.getIcon());
            drawScaledPrism(tessellator, 13, 0, 13, 14, 16, 14, blockCrop.getIcon());
            drawScaledPrism(tessellator, 2, 0, 13, 3, 16, 14, blockCrop.getIcon());
            tessellator.translate(0, 3* Constants.UNIT, 0);
            if (crop.isCrossCrop()) {
                drawScaledPrism(tessellator, 0, 10, 2, 16, 11, 3, blockCrop.getIcon());
                drawScaledPrism(tessellator, 0, 10, 13, 16, 11, 14, blockCrop.getIcon());
                drawScaledPrism(tessellator, 2, 10, 0, 3, 11, 16, blockCrop.getIcon());
                drawScaledPrism(tessellator, 13, 10, 0, 14, 11, 16, blockCrop.getIcon());
            }
            else if (crop.hasPlant()) {
                //render the plant
                crop.getPlant().renderPlantInCrop(renderer, world, pos, state, state.getValue(BlockStates.GROWTHSTAGE));
            }
            else if(crop.hasWeed()) {
                //render weeds
                tessellator.setBrightness(net.minecraft.init.Blocks.wheat.getMixedBrightnessForBlock(world, pos));
                tessellator.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
                PlantRenderer.renderHashTagPattern(tessellator, blockCrop.getWeedTexture(state.getValue(BlockStates.GROWTHSTAGE)), 0);
            }
        }
        return true;
    }

    @Override
    protected void doInventoryRender(TessellatorV2 tessellator, Block block, ItemStack item, ItemCameraTransforms.TransformType transformType) {
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
