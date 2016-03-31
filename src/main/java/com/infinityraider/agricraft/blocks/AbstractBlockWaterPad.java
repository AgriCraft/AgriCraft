/*
 * An intermediate for the water pad.
 */
package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderWaterPad;
import java.util.List;

import com.infinityraider.agricraft.tileentity.TileEntityBase;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 *
 * @author rlonryan
 */
public abstract class AbstractBlockWaterPad extends BlockBaseNoTile {
    public static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, Constants.UNIT * (Constants.WHOLE / 2), 1);

	public AbstractBlockWaterPad(Material mat, String internalName) {
		super(mat, "water_pad_" + internalName);
		this.setHardness(0.5F);
        this.setStepSound(Blocks.gravel.getStepSound());
	}

    @Override
    public TileEntityBase getTileEntity(IBlockAccess world, BlockPos pos) {
        return null;
    }


    @Override
    public AxisAlignedBB getDefaultBoundingBox() {
        return BOX;
    }
	
	@Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public RenderWaterPad getRenderer() {
        return new RenderWaterPad(this);
    }
	
	public static class ItemBlockWaterPad extends net.minecraft.item.ItemBlock {
        public ItemBlockWaterPad(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add(I18n.translateToLocal("agricraft_tooltip.waterPadDry"));
        }
    }
	
}
