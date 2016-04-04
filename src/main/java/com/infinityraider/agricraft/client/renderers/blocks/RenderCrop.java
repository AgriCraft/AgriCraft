package com.infinityraider.agricraft.client.renderers.blocks;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.init.AgriCraftBlocks;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.client.renderers.PlantRenderer;
import com.infinityraider.agricraft.client.renderers.TessellatorV2;
import com.infinityraider.agricraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static com.infinityraider.agricraft.client.renderers.RenderUtil.*;
import com.infinityraider.agricraft.reference.AgriCraftProperties;

/*
 * TODO: Convert to new Renderer.
 */
@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockAgriCraft {
	
    public RenderCrop() {
        super(AgriCraftBlocks.blockCrop, null, false, false, true);
    }

	@Override
	protected void doRenderBlock(TessellatorV2 tess, IBlockAccess world, Block block, IBlockState state, BlockPos pos) {
		TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            BlockCrop blockCrop = (BlockCrop) block;
            TileEntityCrop crop = (TileEntityCrop) te;
			
			// Draw Vertical Bars
			tess.pushMatrix();
            tess.translate(0, -3* Constants.UNIT, 0);
            drawScaledPrism(tess, 2, 0, 2, 3, 16, 3, blockCrop.getIcon());
            drawScaledPrism(tess, 13, 0, 2, 14, 16, 3, blockCrop.getIcon());
            drawScaledPrism(tess, 13, 0, 13, 14, 16, 14, blockCrop.getIcon());
            drawScaledPrism(tess, 2, 0, 13, 3, 16, 14, blockCrop.getIcon());
            tess.popMatrix();
			
			// Draw Horizontal Bars
            if (crop.isCrossCrop()) {
                drawScaledPrism(tess, 0, 10, 2, 16, 11, 3, blockCrop.getIcon());
                drawScaledPrism(tess, 0, 10, 13, 16, 11, 14, blockCrop.getIcon());
                drawScaledPrism(tess, 2, 10, 0, 3, 11, 16, blockCrop.getIcon());
                drawScaledPrism(tess, 13, 10, 0, 14, 11, 16, blockCrop.getIcon());
            } else if (crop.hasPlant()) {
                //render the plant
                crop.getPlant().renderPlantInCrop(tess.getWorldRenderer(), world, pos, state, state.getValue(AgriCraftProperties.GROWTHSTAGE));
            } else if(crop.hasWeed()) {
                //render weeds
                tess.setBrightness(net.minecraft.init.Blocks.wheat.getMixedBrightnessForBlock(world, pos));
                tess.setColorRGBA_F(1.0F, 1.0F, 1.0F, 1.0F);
                PlantRenderer.renderHashTagPattern(tess, blockCrop.getWeedTexture(state.getValue(AgriCraftProperties.GROWTHSTAGE)), 0);
            }
        }
    }

}
