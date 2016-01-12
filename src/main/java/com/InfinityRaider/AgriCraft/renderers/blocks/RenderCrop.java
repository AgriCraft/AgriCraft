package com.InfinityRaider.AgriCraft.renderers.blocks;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.PlantRenderer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;

@SideOnly(Side.CLIENT)
public class RenderCrop extends RenderBlockBase {
    public RenderCrop() {
        super(Blocks.blockCrop, false);
    }

    @Override
    protected boolean doWorldRender(Tessellator tessellator, IBlockAccess world, double xCoord, double yCoord, double zCoord, TileEntity tile, Block block, float f, int modelId, RenderBlocks renderer, boolean callFromTESR) {
        int x = (int) xCoord;
        int y = (int) yCoord;
        int z = (int) zCoord;
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if (tileEntity instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) tileEntity;
            tessellator.addTranslation(0, -3*Constants.UNIT, 0);
            drawScaledPrism(tessellator, 2, 0, 2, 3, 16, 3, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 13, 0, 2, 14, 16, 3, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 13, 0, 13, 14, 16, 14, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            drawScaledPrism(tessellator, 2, 0, 13, 3, 16, 14, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            tessellator.addTranslation(0, 3 * Constants.UNIT, 0);
            if (crop.isCrossCrop()) {
                drawScaledPrism(tessellator, 0, 10, 2, 16, 11, 3, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 0, 10, 13, 16, 11, 14, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 2, 10, 0, 3, 11, 16, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
                drawScaledPrism(tessellator, 13, 10, 0, 14, 11, 16, block.getIcon(0, 0), RenderBlockBase.COLOR_MULTIPLIER_STANDARD);
            }
            if(ConfigurationHandler.renderCropPlantsAsTESR) {
                if(callFromTESR) {
                    renderPlant(world, x, y, z, crop, renderer);
                }
            } else {
                renderPlant(world, x, y, z, crop, renderer);
            }
        }
        return true;
    }

    private void renderPlant(IBlockAccess world, int x, int y, int z, TileEntityCrop crop, RenderBlocks renderer) {
        if (crop.hasPlant()) {
            //render the plant
            crop.getPlant().renderPlantInCrop(world, x, y, z, renderer);
        } else if (crop.hasWeed()) {
            //render weeds
            PlantRenderer.renderPlantLayer(x, y, z, renderer, 6, crop.getPlantIcon(), 0);
        }
    }

    @Override
    protected void doInventoryRender(ItemRenderType type, ItemStack item, Object... data) {
    }

    @Override
    public boolean shouldBehaveAsTESR() {
        return true;
    }

    @Override
    public boolean shouldBehaveAsISBRH() {
        return true;
    }
}
