package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.blocks.BlockBase;
import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.BlockStates;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase {
    public RenderCrop() {
        super(Blocks.blockCrop, new TileEntityCrop(), false);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, BlockPos pos, IBlockState state, BlockBase block, TileEntity tile, int modelId, float f) {
        if (tile instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tile;
            tessellator.startDrawingQuads();
            tessellator.addTranslation(0, -3 * Constants.UNIT, 0);
            TextureAtlasSprite texture = block.getIcon();

            drawScaledPrism(tessellator, 2, 0, 2, 3, 16, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
            drawScaledPrism(tessellator, 13, 0, 2, 14, 16, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
            drawScaledPrism(tessellator, 13, 0, 13, 14, 16, 14, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
            drawScaledPrism(tessellator, 2, 0, 13, 3, 16, 14, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);

            tessellator.addTranslation(0, 3 * Constants.UNIT, 0);

            tessellator.draw();
            if (crop.isCrossCrop()) {
                tessellator.startDrawingQuads();
                drawScaledPrism(tessellator, 0, 10, 2, 16, 11, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
                drawScaledPrism(tessellator, 0, 10, 13, 16, 11, 14, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
                drawScaledPrism(tessellator, 2, 10, 0, 3, 11, 16, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
                drawScaledPrism(tessellator, 13, 10, 0, 14, 11, 16, RenderBlockBase.COLOR_MULTIPLIER_STANDARD, texture);
                tessellator.draw();
            }
            else if (crop.hasPlant()) {
                //render the plant
                tessellator.startDrawingQuads();
                crop.getPlant().renderPlantInCrop(world, pos, state, state.getValue(BlockStates.GROWTHSTAGE));
                tessellator.draw();
            }
            else if(crop.hasWeed()) {
                //render weeds
                //tessellator.startDrawingQuads();
                PlantRenderer.renderPlantLayer(world, pos, 6, 0, ((BlockCrop) block).getWeedTexture(state.getValue(BlockStates.GROWTHSTAGE)));
                //tessellator.draw();
            }
        }
        return true;
    }
}
