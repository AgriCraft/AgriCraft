package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.tabs.AgriTabs;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderSeedAnalyzer;
import com.infinityraider.agricraft.tiles.TileEntitySeedAnalyzer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.particle.ParticleManager;

public class BlockSeedAnalyzer extends BlockBaseTile<TileEntitySeedAnalyzer> {

	public static final AxisAlignedBB BOX = new AxisAlignedBB(Constants.UNIT, 0, Constants.UNIT, Constants.UNIT * (Constants.WHOLE - 1), Constants.UNIT * Constants.QUARTER, Constants.UNIT * (Constants.WHOLE - 1));

	public BlockSeedAnalyzer() {
		super(Material.GROUND, "seed_analyzer", false);
		this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
		this.isBlockContainer = true;
		this.setTickRandomly(false);
		//set mining statistics
		this.setHardness(1);
		this.setResistance(1);
	}

	//creates a new tile entity every time a block of this type is placed
	@Override
	public TileEntitySeedAnalyzer createNewTileEntity(World world, int meta) {
		return new TileEntitySeedAnalyzer();
	}

	//called when the block is broken
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		if (!world.isRemote) {
			world.removeTileEntity(pos);
			world.setBlockToAir(pos);
		}
	}

	//override this to delay the removal of the tile entity until after harvestBlock() has been called
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		return !player.capabilities.isCreativeMode || super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	//this gets called when the block is mined
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		if (!world.isRemote) {
			if (!player.capabilities.isCreativeMode) {
				this.dropBlockAsItem(world, pos, state, 0);
			}
			this.breakBlock(world, pos, state);
		}
	}

	//get a list with items dropped by the the crop
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> items = new ArrayList<>();
		items.add(new ItemStack(this, 1, 0));
		if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntitySeedAnalyzer) {
			TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) world.getTileEntity(pos);
			if (analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId) != null) {
				items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.seedSlotId));
			}
			if (analyzer.getStackInSlot(ContainerSeedAnalyzer.journalSlotId) != null) {
				items.add(analyzer.getStackInSlot(ContainerSeedAnalyzer.journalSlotId));
			}
		}
		return items;
	}

	//open the gui when the block is activated
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (player.isSneaking()) {
			return false;
		}
		if (!world.isRemote) {
			player.openGui(AgriCraft.instance, GuiHandler.seedAnalyzerID, world, pos.getX(), pos.getY(), pos.getZ());
		}
		return true;
	}

	//rendering stuff
	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
		return false;
	}

	@Override
	public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
		return false;
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderSeedAnalyzer getRenderer() {
		return new RenderSeedAnalyzer(this);
	}

	@Override
	public Class<? extends ItemBlock> getItemBlockClass() {
		return null;
	}

	@Override
	public AxisAlignedBB getDefaultBoundingBox() {
		return BOX;
	}

}
