package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.BlockStates;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase {
    public RenderCrop() {
        super(Blocks.blockCrop, false);
    }

    @Override
    protected boolean doWorldRender(TessellatorV2 tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, TileEntity tile, Block block, float f, int modelId, boolean callFromTESR) {
        int x = (int) xCoord;
        int y = (int) yCoord;
        int z = (int) zCoord;
        BlockPos pos = new BlockPos(x, y, z);
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tileEntity;
            tessellator.addTranslation(0, -3*Constants.UNIT, 0);
            drawScaledPrism(tessellator, 2, 0, 2, 3, 16, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 13, 0, 2, 14, 16, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 13, 0, 13, 14, 16, 14,RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 2, 0, 13, 3, 16, 14, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            tessellator.addTranslation(0, 3*Constants.UNIT, 0);
            if (crop.isCrossCrop()) {
                drawScaledPrism(tessellator, 0, 10, 2, 16, 11, 3, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 0, 10, 13, 16, 11, 14, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 2, 10, 0, 3, 11, 16, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 13, 10, 0, 14, 11, 16, RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            }
            else if (crop.hasPlant()) {
                //render the plant
                IBlockState state = world.getBlockState(pos);
                crop.getPlant().renderPlantInCrop(world, pos, state, state.getValue(BlockStates.AGE));
            }
            else if(crop.hasWeed()) {
                //render weeds
                PlantRenderer.renderPlantLayer(world, pos, 6, 0);
            }
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
