package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizable;
import com.infinityraider.agricraft.api.v1.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.v1.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.v1.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.v1.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.v1.seed.AgriSeed;
import com.infinityraider.agricraft.api.v1.util.MethodResult;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemDebugger;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderCrop;
import com.infinityraider.agricraft.tiles.TileEntityCrop;
import com.infinityraider.agricraft.utility.StackHelper;
import com.infinityraider.infinitylib.block.BlockTileCustomRenderedBase;
import com.infinityraider.infinitylib.block.blockstate.InfinityProperty;
import com.infinityraider.infinitylib.utility.WorldHelper;
import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
import vazkii.botania.api.item.IHornHarvestable;

@net.minecraftforge.fml.common.Optional.Interface(modid = "botania", iface = "vazkii.botania.api.item.IHornHarvestable")
public class BlockCrop extends BlockTileCustomRenderedBase<TileEntityCrop> implements IGrowable, IPlantable, IHornHarvestable {

    public static final AxisAlignedBB BOX = new AxisAlignedBB(Constants.UNIT * 2, 0, Constants.UNIT * 2, Constants.UNIT * (Constants.WHOLE - 2), Constants.UNIT * (Constants.WHOLE - 3), Constants.UNIT * (Constants.WHOLE - 2));

    static final Class[] ITEM_EXCLUDES = new Class[]{
        IAgriRakeItem.class,
        IAgriClipperItem.class,
        IAgriTrowelItem.class,
        ItemDebugger.class
    };

    public BlockCrop() {
        super("crop", Material.PLANTS);
        this.setTickRandomly(true);
        this.hasTileEntity = true;
        this.setSoundType(SoundType.PLANT);
        this.setHardness(0.0F);
        //this.disableStats();
        this.setCreativeTab(null);
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
        if (!world.isRemote) {
            this.getCrop(world, pos).ifPresent(TileEntityCrop::onGrowthTick);
        }
    }

    /*
     * Handles right-clicks from the player (a.k.a usage).
     * <br>
     * When the block is right clicked, the behaviour depends on the crop, and
     * what item it was clicked with.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        ItemStack heldItem = player.getHeldItem(hand);

        // Step 0. Abort if remote.
        if (world.isRemote) {
            // I'm not sure if this is right, but oh well.
            return true;
        }

        // Step 1. Fetch the crop.
        TileEntityCrop crop = WorldHelper.getTile(world, pos, TileEntityCrop.class).orElse(null);

        // Step 2. Give up if the crop doesn't exist;
        if (crop == null) {
            // Allow others to use the click event.
            return false;
        }

        // Step 3. If the player is not holding anything, then harvest the crop.
        if (heldItem.isEmpty()) {
            crop.onHarvest((stack) -> WorldHelper.spawnItemInWorld(world, pos, stack), player);
            return true;
        }

        // Step 4. If the held item is excluded from handling, skip it.
        if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
            // Allow the excludes to do their things.
            return false;
        }

        // Step 5. If the held item is a type of fertilizer, apply it.
        if (AgriApi.getFertilizerRegistry().hasAdapter(heldItem)) {
            AgriApi.getFertilizerRegistry()
                    .valueOf(heldItem)
                    .ifPresent(f -> f.applyFertilizer(player, world, pos, crop, heldItem, crop.getRandom()));
            return true; // Even if it didn't grow or cross, we've still handled this block activation, so return true.
        }

        // Step 6. If the held item is crops, attempt to make cross-crops.
        if (heldItem.getItem() == AgriItems.getInstance().CROPS) {
            // Attempt to apply crop-sticks to crop.
            if (crop.onApplyCrops(player) == MethodResult.SUCCESS) {
                // If player isn't in creative remove an item from the stack.
                if (!player.isCreative()) {
                    heldItem.setCount(heldItem.getCount() - 1);
                }
                // The application was a success!
                return true;
            }
        }

        // Step 7. Attempt to resolve held item as a seed.
        final Optional<AgriSeed> seed = AgriApi.getSeedRegistry().valueOf(heldItem);

        // Step 8. If held item is a seed, attempt to plant it in the crop.
        if (seed.isPresent()) {
            if (crop.onApplySeeds(seed.get(), player) == MethodResult.SUCCESS) {
                // Remove the consumed seed from the player's inventory.
                // But only if the player was not in creative mode.
                // For now we really don't care if the method fails,
                // so not checking the method's result is ok.
                StackHelper.decreaseStackSize(player, heldItem, 1);
                // The planting was a success!
                return true;
            }
        }

        // Step 8. If we can't do anything else, give up and attempt to harvest instead.
        crop.onHarvest((stack) -> WorldHelper.spawnItemInWorld(world, pos, stack), player);

        //Returning true will prevent other things from happening
        return true;

    }

    /**
     * Handles left-clicks from the player (a.k.a hits).
     * <br>
     * When the block is left clicked, it breaks.
     */
    @Override
    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        if (!world.isRemote) {
            this.getCrop(world, pos).ifPresent(crop -> crop.onBroken((stack) -> WorldHelper.spawnItemInWorld(world, pos, stack), player));
        }
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
     * Handles the block drops. Called when the block is broken (not left clicked).
     */
    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            this.getCrop(world, pos).ifPresent(c -> c.onBroken((stack) -> WorldHelper.spawnItemInWorld(world, pos, stack), null));
        }
    }

    // =========================================================================
    // IGrowable Methods
    // <editor-fold>
    // =========================================================================
    /**
     * WARNING: You should be using AgriCraft's IAgriFertilizer interface instead!
     *
     * Part of the vanilla IGrowable interface. Can be enabled and disabled via the configs. In
     * vanilla Minecraft, this method signals if a bonemeal should be consumed when used on this
     * object. If true, it should be followed by a call to
     * {@link #canUseBonemeal(World, Random, BlockPos, IBlockState)}.
     *
     * Unlike vanilla, this should always return the same result as
     * {@link #canUseBonemeal(World, Random, BlockPos, IBlockState)}.
     *
     * @return true if the interface is enabled and the target will accept bonemeal.
     */
    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return AgriCraftConfig.allowIGrowableOnCrop
                && checkOrUseBonemeal(world, null, pos, false);
    }

    /**
     * WARNING: You should be using AgriCraft's IAgriFertilizer interface instead!
     *
     * Part of the vanilla IGrowable interface. Can be enabled and disabled via the configs. This
     * method tells the caller if they should continue onward and call
     * {@link #grow(World, Random, BlockPos, IBlockState)}, or if they should stop instead. In
     * vanilla Minecraft, saplings and mushrooms use this method to be able to consume bonemeal but
     * not necessarily grow into a tree every time.
     *
     * Misleadingly named! If this is false but canGrow is true, then bonemeal will be useless and
     * wasted.
     *
     * Unlike vanilla, this should always return the same result as
     * {@link #canGrow(World, BlockPos, IBlockState, boolean)}.
     *
     * @return true if the interface is enabled and the target will accept bonemeal.
     */
    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return AgriCraftConfig.allowIGrowableOnCrop
                && checkOrUseBonemeal(world, rand, pos, false);
    }

    /**
     * WARNING: You should be using AgriCraft's IAgriFertilizer interface instead!
     *
     * Part of the vanilla IGrowable interface. Can be enabled and disabled via the configs. If this
     * is enabled, and the target can accept the bonemeal fertilizer currently, then this will apply
     * it. This method is the same as bonemeal being used on the target for free. See
     * {@link #checkOrUseBonemeal(World, Random, BlockPos, boolean)} for runtime exceptions to watch
     * out for.
     */
    @Override
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        if (AgriCraftConfig.allowIGrowableOnCrop) {
            checkOrUseBonemeal(world, world.rand, pos, true);
        }
    }

    /**
     * This is the reference copy of a stack of bonemeal. This is maintained instead of a reference
     * to the actual IAgriFertilizer object, in case the mapping changes during runtime.
     */
    private static final ItemStack BONEMEAL = new ItemStack(Items.DYE, 1, 15);

    /**
     * Helper method for the IGrowable interface. Checks if bonemeal can be applied at the requested
     * position. Can also then apply the bonemeal fertilizer if the check passes and the last
     * parameter is set to true. Uses AgriCraft's Fertilizer system, specifically whatever adapter
     * is registered for bonemeal/ItemDye.
     *
     * Will throw runtime exceptions if either: - world is null, - pos is null, - rand is null while
     * tryToApplyBonemeal is true, - there is no IAgriFertilizable object at the BlockPos, -OR- -
     * bonemeal (i.e. ItemDye with meta 15) is not registered as a fertilizer.
     *
     * @param world The world to check.
     * @param rand A source of randomness. Only necessary when tryToApplyBonemeal is true, can be
     * null otherwise.
     * @param pos The location of crop to check.
     * @param tryToApplyBonemeal When true, will also apply the bonemeal if it's accepted by the
     * crop.
     * @return true if both the IGrowable interface is enabled, and the crop accepts bonemeal
     * currently. false if either the IGrowable interface is disabled, or the crop does not accept
     * bonemeal. The return value is not dependent on the result of applying the bonemeal.
     */
    private boolean checkOrUseBonemeal(@Nonnull World world, @Nullable Random rand, @Nonnull BlockPos pos, boolean tryToApplyBonemeal) {
        // Sanity check the parameters.
        Objects.requireNonNull(world, "IGrowable on BlockCrop can't function with a null world parameter.");
        Objects.requireNonNull(pos, "IGrowable on BlockCrop can't function with a null pos parameter.");
        if (tryToApplyBonemeal) {
            Objects.requireNonNull(rand, "IGrowable#grow on BlockCrop can't function with a null rand parameter.");
        }

        // Get the crop that is being targeted.
        IAgriFertilizable crop = WorldHelper
                .getTile(world, pos, IAgriFertilizable.class)
                .orElseThrow(() -> new RuntimeException("There is no IAgriFertilizable at: " + pos));

        // Get the AgriCraft fertilizer representation of bonemeal.
        IAgriFertilizer meal = AgriApi
                .getFertilizerRegistry()
                .valueOf(BONEMEAL)
                .orElseThrow(() -> new RuntimeException("Bonemeal is not registered as a fertilizer."));

        // Use those two references to perform the check.
        boolean canApplyBonemeal = crop.acceptsFertilizer(meal);

        // If the caller has requested it, and the crop allows it, then also apply the bonemeal now.
        if (tryToApplyBonemeal && canApplyBonemeal) {
            crop.onApplyFertilizer(meal, rand);
        }

        // Regardless of the outcome of applying the fertilizer, return the result of the check itself.
        return canApplyBonemeal;
    }

    // =========================================================================
    // IGrowable Methods
    // </editor-fold>
    // =========================================================================
    /**
     * Handles changes in the crop's neighbors.
     */
    @Override
    public void observedNeighborChange(IBlockState observerState, World world, BlockPos pos, Block changedBlock, BlockPos changedBlockPos) {
        if (!this.canBlockStay(world, pos)) {
            this.dropBlockAsItem(world, pos, observerState, 0);
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
        }
    }

    /**
     * Tests to see if the crop is still on valid soil.
     *
     * @return if the crop is placed in a valid location.
     */
    public boolean canBlockStay(IBlockAccess world, BlockPos pos) {
        return AgriApi.getSoilRegistry().contains(world.getBlockState(pos.down()));
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
     * Determines if the crops contain a mature plant by checking if the metadata matches
     * {@link Constants#MATURE}.
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
        List<ItemStack> drops = new ArrayList<>();
        this.getCrop(world, pos).ifPresent(c -> c.getDrops(drops::add, true, true, true));
        return drops;
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
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
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
     * Determines if the block is a normal block, such as cobblestone. This tells Minecraft if crops
     * are not a normal block (meaning no levers can be placed on it, it's transparent, ...).
     *
     * @return false - the block is not a normal block.
     */
    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    /**
     * Determines if a side of the block should be rendered, such as one flush with a wall that
     * wouldn't need rendering.
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

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess p_getBlockFaceShape_1_, IBlockState p_getBlockFaceShape_2_, BlockPos p_getBlockFaceShape_3_, EnumFacing p_getBlockFaceShape_4_) {
        // Undefined face shape prevents fence connections.
        return BlockFaceShape.UNDEFINED;
    }

    /**
     * Renders the hit effects, such as the flying particles when the block is hit.
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
        return new InfinityProperty[]{};
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
        return WorldHelper.getTile(world, pos, TileEntityCrop.class);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityCrop> tile = getCropTile(world, pos);
        return ((IExtendedBlockState) state)
                .withProperty(AgriProperties.CROP_PLANT, tile.map(TileEntityCrop::getSeed).map(s -> s.getPlant()).orElse(null))
                .withProperty(AgriProperties.GROWTH_STAGE, tile.map(TileEntityCrop::getGrowthStage).orElse(0))
                .withProperty(AgriProperties.CROSS_CROP, tile.map(TileEntityCrop::isCrossCrop).orElse(false));
    }

    @Override
    public IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[]{
            AgriProperties.CROP_PLANT,
            AgriProperties.GROWTH_STAGE,
            AgriProperties.CROSS_CROP
        };
    }

    // ==================================================
    // Botania <editor-fold desc="Botania">
    // --------------------------------------------------
    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "botania")
    public boolean canHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType eht) {
        return (eht == EnumHornType.WILD);
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "botania")
    public boolean hasSpecialHornHarvest(World world, BlockPos pos, ItemStack stack, EnumHornType eht) {
        return (eht == EnumHornType.WILD);
    }

    @Override
    @net.minecraftforge.fml.common.Optional.Method(modid = "botania")
    public void harvestByHorn(World world, BlockPos pos, ItemStack stack, EnumHornType eht) {
        if (eht == EnumHornType.WILD) {
            WorldHelper.getTile(world, pos, TileEntityCrop.class)
                    .filter(TileEntityCrop::isMature)
                    .ifPresent(crop -> {
                        crop.onHarvest((product) -> WorldHelper.spawnItemInWorld(world, pos, product), null);
                    });
        }
    }

    // --------------------------------------------------
    // </editor-fold>
    // ==================================================
}
