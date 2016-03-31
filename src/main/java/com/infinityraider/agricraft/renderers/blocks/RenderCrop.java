package com.infinityraider.agricraft.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.PlantRenderer;
import com.infinityraider.agricraft.renderers.tessellation.ITessellator;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.infinityraider.agricraft.reference.AgriCraftProperties;

import javax.annotation.Nullable;

/*
 * TODO: Convert to new Renderer.
 */
@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase<TileEntityCrop> {	
    public RenderCrop() {
        super(AgriCraftBlocks.blockCrop, null, false, false, true);
    }

    @Override
    public void renderWorldBlock(ITessellator tessellator, World world, BlockPos pos, double x, double y, double z, IBlockState state, Block block,
                                 @Nullable TileEntityCrop crop, boolean dynamicRender, float partialTick, int destroyStage) {
        if(crop != null) {
            //TODO: get crop icon
            TextureAtlasSprite icon = null;
            BlockCrop blockCrop = (BlockCrop) block;
            // Draw Vertical Bars
            tessellator.translate(0, -3* Constants.UNIT, 0);
            tessellator.drawScaledPrism(2, 0, 2, 3, 16, 3, icon);
            tessellator.drawScaledPrism(13, 0, 2, 14, 16, 3, icon);
            tessellator.drawScaledPrism(13, 0, 13, 14, 16, 14, icon);
            tessellator.drawScaledPrism(2, 0, 13, 3, 16, 14, icon);
            tessellator.translate(0, 3 * Constants.UNIT, 0);

            // Draw Horizontal Bars
            if (crop.isCrossCrop()) {
                tessellator.drawScaledPrism(0, 10, 2, 16, 11, 3, icon);
                tessellator.drawScaledPrism(0, 10, 13, 16, 11, 14, icon);
                tessellator.drawScaledPrism(2, 10, 0, 3, 11, 16, icon);
                tessellator.drawScaledPrism(13, 10, 0, 14, 11, 16, icon);
            } else if (crop.hasPlant()) {
                //render the plant
                crop.getPlant().renderPlantInCrop(world, pos, state.getValue(AgriCraftProperties.GROWTHSTAGE));
            } else if(crop.hasWeed()) {
                //render weeds
                //tessellator.setBrightness(RenderUtil.getMixedBrightness(world, pos, Blocks.wheat.getDefaultState()));
                tessellator.setColorRGBA(1.0F, 1.0F, 1.0F, 1.0F);
                PlantRenderer.renderHashTagPattern(tessellator, blockCrop.getWeedTexture(state.getValue(AgriCraftProperties.GROWTHSTAGE)), 0);
            }
        }
    }

    @Override
    public TextureAtlasSprite getIcon() {
        return null;
    }
}
