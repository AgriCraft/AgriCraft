/*
 * An intermediate for the water pad.
 */
package com.infinityraider.agricraft.blocks.pad;

import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderWaterPad;
import java.util.List;

import com.infinityraider.infinitylib.block.BlockCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.block.blockstate.SidedConnection;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractBlockWaterPad extends BlockCustomRenderedBase {
    public static final AxisAlignedBB BOX = new AxisAlignedBB(0, 0, 0, 1, Constants.UNIT * (Constants.WHOLE / 2), 1);

	public AbstractBlockWaterPad(Material mat, String internalName) {
		super("water_pad_" + internalName, mat);
		this.setHardness(0.5F);
        this.setSoundType(Blocks.GRAVEL.getSoundType());
	}

    @Override
    protected InfinityProperty[] getPropertyArray() {
        return new InfinityProperty[0];
    }

    @Override
    protected IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[] {AgriProperties.CONNECTIONS};
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        SidedConnection connection = new SidedConnection();
        for(EnumFacing facing : EnumFacing.HORIZONTALS) {
            IBlockState stateAt = world.getBlockState(pos.offset(facing));
            connection.setConnected(facing, stateAt.getBlock() == state.getBlock());
        }
        return ((IExtendedBlockState) state).withProperty(AgriProperties.CONNECTIONS, connection);
    }

	@Override
    @SideOnly(Side.CLIENT)
	public RenderWaterPad getRenderer() {
		return new RenderWaterPad(this);
	}

    @Override
    @SideOnly(Side.CLIENT)
    public ModelResourceLocation getBlockModelResourceLocation() {
        return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + getInternalName());
    }
	
	public static class ItemBlockWaterPad extends net.minecraft.item.ItemBlock {
        public ItemBlockWaterPad(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            list.add(AgriCore.getTranslator().translate("agricraft_tooltip.waterPadDry"));
        }
    }
	
}
