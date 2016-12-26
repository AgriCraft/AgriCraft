package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderCrop;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;
import com.infinityraider.infinitylib.utility.WorldHelper;

import java.util.*;

import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCrop extends BlockTileCustomRenderedBase<TileEntityCrop> implements IGrowable, IPlantable {
    public static final AxisAlignedBB BOX = new AxisAlignedBB(Constants.UNIT * 2, 0, Constants.UNIT * 2, Constants.UNIT * (Constants.WHOLE - 2), Constants.UNIT * (Constants.WHOLE - 3), Constants.UNIT * (Constants.WHOLE - 2));

    public BlockCrop() {
        super("crop", Material.PLANTS);
        this.setTickRandomly(true);
        this.isBlockContainer = true;
        this.setSoundType(SoundType.PLANT);
        this.setHardness(0.0F);
        //this.disableStats();
        this.setCreativeTab(null);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    public TileEntityCrop createNewTileEntity(World world, int meta) {
        return new TileEntityCrop();
    }

    public Optional<TileEntityCrop> getCrop(IBlockAccess world, BlockPos pos) {
        return WorldHelper.getTile(world, pos, TileEntityCrop.class);
    }

    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        this.getCrop(world, pos).ifPresent(TileEntityCrop::growthTick);
    }

    /**
     * Handles right-clicks from the player (a.k.a usage).
     * <br>
     * When the block is right clicked, the behaviour depends on the crop, and what item it was clicked with.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand,
                                    ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        return this.getCrop(world, pos).map(crop -> crop.onCropRightClicked(player, heldItem)).orElse(false);
    }

    /**
     * Handles left-clicks from the player (a.k.a hits).
     * <br>
     * When the block is left clicked, it breaks.
     */
    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        this.getCrop(world, pos).ifPresent(crop -> crop.onCropLeftClicked(player));
    }

    /**
     * Handles the block being harvested by calling
     * {@link #onBlockClicked(World, BlockPos pos, EntityPlayer)}.
     */
    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.onBlockClicked(world, pos, player);
    }

    /**
     * Handles the block drops. Called when the block is left-clicked or
     * otherwise breaks.
     */
    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(!world.isRemote) {
            this.getCrop(world, pos).ifPresent(crop ->
                crop.getDrops().stream().forEach(drop -> spawnAsEntity(world, pos, drop))
            );
        }
    }

    /**
     * Determines if bonemeal may be applied to the plant contained in the
     * crops.
     *
     * @return if bonemeal may be applied.
     */
    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return this.getCrop(world, pos).map(crop -> !crop.isMature()).orElse(false);
    }

    /**
     * Determines if bonemeal speeds up the GROWTH of the contained plant.
     *
     * @return true, bonemeal may speed up any contained plant.
     */
    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    /**
     * Increments the contained plant's GROWTH stage. Called when bonemeal is
     * applied to the block.
     */
    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        this.getCrop(world, pos).ifPresent(TileEntityCrop::applyBoneMeal);
    }

    /**
     * Handles changes in the crop's neighbors.
     */
    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn) {
        if (!this.canBlockStay(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.removeTileEntity(pos);
            worldIn.setBlockToAir(pos);
        }
    }

    /**
     * Tests to see if the crop is still on valid soil.
     *
     * @return if the crop is placed in a valid location.
     */
    public boolean canBlockStay(IBlockAccess world, BlockPos pos) {
        return GrowthRequirementHandler.isSoilValid(world, pos.add(0, -1, 0));
    }

    /**
     * Determines if the the plant is fertile, and can grow.
     *
     * @return if the plant can grow.
     */
    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return this.getCrop(world, pos).map(TileEntityCrop::isFertile).orElse(false);
    }

    /**
     * Determines if the crops contain a mature plant by checking if the
     * metadata matches {@link Constants#MATURE}.
     *
     * @return if the crop is done growing.
     */
    public boolean isMature(World world, BlockPos pos) {
        return this.getCrop(world, pos).map(TileEntityCrop::isMature).orElse(false);
    }

    /**
     * Retrieves the block's item form to be dropped when the block is broken.
     *
     * @return the item form of the crop.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return AgriItems.getInstance().CROPS;
    }

    /**
     * Determines a list of what is dropped when the crops are broken.
     *
     * @return a list of the items to drop.
     */
    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return this.getCrop(world, pos).map(TileEntityCrop::getDrops).orElse(Collections.emptyList());
    }

    /**
     * Handles the block being broken.
     */
    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    /**
     * Retrieves the item form of the block.
     *
     * @return the block's item form.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(AgriItems.getInstance().CROPS);
    }

    @Override
    @Deprecated
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return BOX;
    }

    @Override
    @Nullable
    @Deprecated
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos) {
        return Block.NULL_AABB;
    }

    @Override
    @Deprecated
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return BOX.offset(pos);
    }

    /**
     * Determines if the block is a normal block, such as cobblestone. This
     * tells Minecraft if crops are not a normal block (meaning no levers can be
     * placed on it, it's transparent, ...).
     *
     * @return false - the block is not a normal block.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * Determines if a side of the block should be rendered, such as one flush
     * with a wall that wouldn't need rendering.
     *
     * @return false - all of the crop's sides need to be rendered.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    /**
     * Renders the hit effects, such as the flying particles when the block is
     * hit.
     *
     * @return false - the block is one-shot and needs no hit particles.
     */
    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World worldObj, RayTraceResult target, ParticleManager manager) {
        return false;
    }

    /**
     * Tells Minecraft if there should be destroy effects, such as particles.
     *
     * @return false - there are no destroy particles.
     */
    @Override
    @SideOnly(value = Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return false;
    }

    /**
     * Retrieves the custom renderer for the crops.
     *
     * @return the block's renderer.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public RenderCrop getRenderer() {
        return new RenderCrop(this);
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
        return new InfinityProperty[]{
            AgriProperties.GROWTHSTAGE,
            AgriProperties.CROSSCROP
        };
    }

    @Override
    public Class<? extends ItemBlock> getItemBlockClass() {
        return null;
    }

    /**
     * Retrieves the type of plant growing within the crops.
     *
     * @return the plant type in the crops.
     */
    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public IBlockState getPlant(IBlockAccess world, BlockPos pos) {
        // TODO: Fix propertycropplant
        // TileEntity tile = world.getTileEntity(pos);
        return world.getBlockState(pos);
    }

    public Optional<TileEntityCrop> getCropTile(IBlockAccess world, BlockPos pos) {
        return Optional.of(world.getTileEntity(pos))
                .filter(t -> t instanceof TileEntityCrop)
                .map(t -> (TileEntityCrop) t);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityCrop> tile = getCropTile(world, pos);
        return ((IExtendedBlockState) state)
                .withProperty(AgriProperties.CROP_PLANT, tile.flatMap(TileEntityCrop::getPlant).orElse(null));
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityCrop> tile = getCropTile(world, pos);
        return state
                .withProperty(AgriProperties.GROWTHSTAGE.getProperty(), tile.map(TileEntityCrop::getGrowthStage).orElse(0))
                .withProperty(AgriProperties.CROSSCROP.getProperty(), tile.map(TileEntityCrop::isCrossCrop).orElse(false));
    }

    @Override
    public IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[]{
            AgriProperties.CROP_PLANT
        };
    }

}
