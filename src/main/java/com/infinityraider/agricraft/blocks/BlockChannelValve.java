package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannelValve;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannelValve;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class BlockChannelValve extends AbstractBlockWaterChannel<TileEntityChannelValve> {
    public static final AxisAlignedBB BOX = new AxisAlignedBB(4*Constants.UNIT, 0, 4*Constants.UNIT, 12*Constants.UNIT, 1, 12*Constants.UNIT);

    public BlockChannelValve() {
        super("valve");
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        if (!world.isRemote) {
            super.onNeighborBlockChange(world, pos, state, block);
            updatePowerStatus(world, pos);
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
        if (!world.isRemote) {
            updatePowerStatus(world, pos);
        }
    }

    private void updatePowerStatus(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te !=null && te instanceof TileEntityChannelValve) {
            TileEntityChannelValve valve = (TileEntityChannelValve) te;
            valve.updatePowerStatus();
        }
    }

    //allows levers to be attached to the block
    @Override
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side!=EnumFacing.UP;
    }

    @Override
    public TileEntityChannelValve createNewTileEntity(World world, int meta) {
        return new TileEntityChannelValve();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderChannelValve getRenderer() {
        return new RenderChannelValve(this);
    }

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
    	return ItemBlockValve.class;
    }

    @Override
    public AxisAlignedBB getDefaultBoundingBox() {
        return BOX;
    }

    public static class ItemBlockValve extends ItemBlockCustomWood {
        public ItemBlockValve(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(I18n.translateToLocal("agricraft_tooltip.valve"));
        }
    }

}
