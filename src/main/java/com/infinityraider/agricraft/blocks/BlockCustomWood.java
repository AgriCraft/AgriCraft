package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.tabs.AgriTabs;
import com.infinityraider.agricraft.items.blocks.ItemBlockCustomWood;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class BlockCustomWood<T extends TileEntityCustomWood> extends BlockTileCustomRenderedBase<T> {

	// Should drastically speed up getTypes()
	private static final List<ItemStack> woodTypes = new ArrayList<>();

	public BlockCustomWood(String internalName) {
		super(internalName, Material.WOOD);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		setHarvestLevel("axe", 0);
		this.setCreativeTab(AgriTabs.TAB_AGRICRAFT);
		this.setSoundType(SoundType.WOOD);
	}

	/**
	 * TODO: Clean up this method. This method has already been cleaned some,
	 * which helps loading...
	 */
	public static List<ItemStack> getWoodTypes() {

		if (!woodTypes.isEmpty()) {
			return woodTypes;
		}

		for (ItemStack plank : OreDictionary.getOres("plankWood")) {
			if (plank.getItem() instanceof ItemBlock) {
				ItemBlock block = ((ItemBlock) plank.getItem());
				if (plank.getItemDamage() == OreDictionary.WILDCARD_VALUE) {
					Side side = FMLCommonHandler.instance().getEffectiveSide();
					if (side == Side.CLIENT) {
						List<ItemStack> subItems = new ArrayList<>();
						block.getSubItems(block, null, subItems);
						woodTypes.addAll(subItems);
					} else {
						for (int i = 0; i < 16; i++) {
							//on the server register every meta as a recipe. The client won't know of this, so it's perfectly ok (don't tell anyone)
							woodTypes.add(new ItemStack(block, 1, i));
						}
					}
				} else {
					woodTypes.add(plank);
				}
			}
		}
		return woodTypes;
	}

	@Override
	public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityCustomWood) {
			TileEntityCustomWood tileEntity = (TileEntityCustomWood) te;
			tileEntity.setMaterial(stack);
		}
		super.onBlockPlacedBy(world, pos, state, placer, stack);
	}

	//override this to delay the removal of the tile entity until after harvestBlock() has been called
	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
		return !player.capabilities.isCreativeMode || super.removedByPlayer(state, world, pos, player, willHarvest);
	}

	//when the block is harvested
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		if ((!world.isRemote) && (!player.isSneaking())) {
			if (!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
				this.dropBlockAsItem(world, pos, state, 0);
			}
			world.setBlockToAir(pos);
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
		if (!world.isRemote) {
			List<ItemStack> drops = this.getDrops(world, pos, state, fortune);
			for (ItemStack drop : drops) {
				spawnAsEntity(world, pos, drop);
			}
		}
	}

	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
		ArrayList<ItemStack> drops = new ArrayList<>();
		ItemStack drop = new ItemStack(this, 1);
		this.setTag(world, pos, drop);
		drops.add(drop);
		return drops;
	}

	//creative item picking
	@Override
	public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
		ItemStack stack = new ItemStack(this, 1, state.getBlock().getMetaFromState(state));
		this.setTag(world, pos, stack);
		return stack;
	}

	//prevent block from being removed by leaves
	@Override
	public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	protected void setTag(IBlockAccess world, BlockPos pos, ItemStack stack) {
		TileEntity te = world.getTileEntity(pos);
		if (te != null && te instanceof TileEntityCustomWood) {
			TileEntityCustomWood tile = (TileEntityCustomWood) te;
			stack.setTagCompound(tile.getMaterialTag());
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public List<ResourceLocation> getTextures() {
		return Collections.emptyList();
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
		return false;
	}

	/*
	 * TODO: What is this?
	 */
	@Override
	public Class<? extends ItemBlockCustomWood> getItemBlockClass() {
		return ItemBlockCustomWood.class;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ModelResourceLocation getBlockModelResourceLocation() {
		return new ModelResourceLocation(Reference.MOD_ID.toLowerCase() + ":" + getInternalName());
	}

	@Override
	public List<String> getOreTags() {
		return Collections.emptyList();
	}

	@Override
	protected InfinityProperty[] getPropertyArray() {
		return new InfinityProperty[0];
	}

	@Override
	public boolean needsRenderUpdate(World world, BlockPos pos, IBlockState state, T tile) {
		return true;
	}

}
