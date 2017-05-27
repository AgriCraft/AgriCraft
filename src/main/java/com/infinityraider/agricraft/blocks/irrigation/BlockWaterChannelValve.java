package com.infinityraider.agricraft.blocks.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.crafting.CustomWoodRecipeHelper;
import com.infinityraider.agricraft.init.AgriBlocks;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderChannelValve;
import com.infinityraider.agricraft.tiles.irrigation.TileEntityChannelValve;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.IRecipeRegister;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockWaterChannelValve extends AbstractBlockWaterChannel<TileEntityChannelValve> implements IRecipeRegister {

	public static final AxisAlignedBB BOX = new AxisAlignedBB(4 * Constants.UNIT, 0, 4 * Constants.UNIT, 12 * Constants.UNIT, 1, 12 * Constants.UNIT);

	public BlockWaterChannelValve() {
		super("valve");
	}

	@Override
	protected InfinityProperty[] getPropertyArray() {
        InfinityProperty[] properties = Arrays.copyOf(super.getPropertyArray(), super.getPropertyArray().length + 1);
        properties[properties.length - 1] = AgriProperties.POWERED;
        return properties;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        Optional<TileEntityChannelValve> tile = WorldHelper.getTile(worldIn, pos, TileEntityChannelValve.class);
		return AgriProperties.POWERED.applyToBlockState(super.getActualState(state, worldIn, pos), tile.isPresent() && tile.get().isPowered());
	}

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
		super.neighborChanged(state, world, pos, block);
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityChannelValve) {
            TileEntityChannelValve valve = (TileEntityChannelValve) te;
            valve.updatePowerStatus();
        }
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		super.onBlockPlacedBy(world, pos, state, placer, stack);
		if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityChannelValve) {
                ((TileEntityChannelValve) te).updatePowerStatus();
            }
		}
	}

	//allows levers to be attached to the block
	@Override
	public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return side != EnumFacing.UP;
	}

	@Override
	public TileEntityChannelValve createNewTileEntity(World world, int meta) {
		return new TileEntityChannelValve();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderChannelValve getRenderer() {
		return new RenderChannelValve(this);
	}

	@Override
	public Class<? extends ItemBlockCustomWood> getItemBlockClass() {
		return ItemBlockValve.class;
	}

	public static class ItemBlockValve extends ItemBlockCustomWood {

		public ItemBlockValve(Block block) {
			super(block);
		}

		@Override
		@SideOnly(Side.CLIENT)
		public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
			super.addInformation(stack, player, list, flag);
			list.add(AgriCore.getTranslator().translate("agricraft_tooltip.valve"));
		}
	}

    @Override
    public void registerRecipes() {
        CustomWoodRecipeHelper.registerCustomWoodRecipe(AgriBlocks.getInstance().CHANNEL_VALVE, 1, false, new ItemStack(Items.IRON_INGOT, 1), new ItemStack(Blocks.LEVER, 1), AgriBlocks.getInstance().CHANNEL);
    }

}
