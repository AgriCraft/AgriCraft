package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.creativetab.AgriCraftTab;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.renderers.blocks.RenderSprinkler;
import com.infinityraider.agricraft.tileentity.TileEntityBase;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntityChannel;
import com.infinityraider.agricraft.tileentity.irrigation.TileEntitySprinkler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockSprinkler extends BlockBaseTile<TileEntitySprinkler> {
	public static final AxisAlignedBB BOX = new AxisAlignedBB(
			Constants.UNIT * Constants.QUARTER,
			Constants.UNIT * Constants.THREE_QUARTER,
			Constants.UNIT * Constants.QUARTER,
			Constants.UNIT * Constants.THREE_QUARTER,
			Constants.UNIT * (Constants.WHOLE + Constants.QUARTER),
			Constants.UNIT * Constants.THREE_QUARTER
	);

	public BlockSprinkler() {
		super(Material.iron, "sprinkler", false);
		this.setCreativeTab(AgriCraftTab.agriCraftTab);
		this.setHardness(2.0F);
		this.setResistance(5.0F);
		setHarvestLevel("axe", 0);
	}

	@Override
	public TileEntitySprinkler createNewTileEntity(World world, int meta) {
		return new TileEntitySprinkler();
	}

	@Override
	public boolean isReplaceable(IBlockAccess world, BlockPos pos) {
		return false;
	}

	//prevent block from being removed by leaves
	@Override
	public boolean canBeReplacedByLeaves(IBlockState state, IBlockAccess world, BlockPos pos) {
		return false;
	}

	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te, ItemStack stack) {
		if ((!world.isRemote) && (!player.isSneaking())) {
			if (!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
				this.dropBlockAsItem(world, pos, state, 0);
			}
			world.setBlockToAir(pos);
			world.removeTileEntity(pos);
		}
	}

	@Override
	public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float f, int i) {
		if (!world.isRemote) {
			ItemStack drop = new ItemStack(this, 1);
			spawnAsEntity(world, pos, drop);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
		//check if crops can stay
		if (!this.canBlockStay(world, pos)) {
			//the crop will be destroyed
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockToAir(pos);
			world.removeTileEntity(pos);
		}
	}

	//see if the block can stay
	public boolean canBlockStay(World world, BlockPos pos) {
		return (world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockWaterChannel);
	}

	@Override
	public boolean isOpaqueCube(IBlockState state) {
		return false;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
		return false;
	}

	@Override
	public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
		return false;
	}

	@Override
	protected IProperty[] getPropertyArray() {
		return new IProperty[0];
	}

	@Override
	@SideOnly(Side.CLIENT)
	public RenderSprinkler getRenderer() {
		return new RenderSprinkler(this);
	}

	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos) {
		IBlockState state = world.getBlockState(pos);
		return world.getBlockState(pos.add(0, 1, 0)).getBlock() instanceof BlockWaterChannel && state.getBlock().getMaterial(state) == Material.air;
	}

	@Override
	protected Class<? extends ItemBlock> getItemBlockClass() {
		return null;
	}

	@Override
	public AxisAlignedBB getDefaultBoundingBox() {
		return BOX;
	}

	@SideOnly(Side.CLIENT)
	public TextureAtlasSprite getIcon(IBlockAccess world, BlockPos pos, IBlockState state, EnumFacing side, @Nullable TileEntityBase te) {
		TileEntity channel = world.getTileEntity(pos.add(0, 1, 0));
		if (channel != null && channel instanceof TileEntityChannel) {
			return ((TileEntityChannel) channel).getTexture(state, side);
		}
		return Minecraft.getMinecraft().getTextureMapBlocks().getMissingSprite();
	}

}
