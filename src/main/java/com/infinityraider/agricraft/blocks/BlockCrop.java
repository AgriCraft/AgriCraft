package com.infinityraider.agricraft.blocks;

import com.agricraft.agricore.util.MathHelper;
import com.agricraft.agricore.util.TypeHelper;
import com.infinityraider.agricraft.api.fertilizer.IAgriFertilizer;
import com.infinityraider.agricraft.api.items.IAgriClipperItem;
import com.infinityraider.agricraft.api.items.IAgriRakeItem;
import com.infinityraider.agricraft.api.items.IAgriTrowelItem;
import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.api.seed.AgriSeed;
import com.infinityraider.agricraft.apiimpl.FertilizerRegistry;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.init.AgriItems;
import com.infinityraider.agricraft.items.ItemDebugger;
import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.renderers.blocks.RenderCrop;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCrop;

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
import net.minecraft.tileentity.TileEntity;
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

/**
 * The most important block in the mod.
 */
public class BlockCrop extends BlockTileCustomRenderedBase<TileEntityCrop> implements IGrowable, IPlantable {

    public static final Class[] ITEM_EXCLUDES = new Class[]{
        IAgriRakeItem.class,
        IAgriClipperItem.class,
        IAgriTrowelItem.class,
        ItemDebugger.class
    };

    public static final AxisAlignedBB BOX = new AxisAlignedBB(Constants.UNIT * 2, 0, Constants.UNIT * 2, Constants.UNIT * (Constants.WHOLE - 2), Constants.UNIT * (Constants.WHOLE - 3), Constants.UNIT * (Constants.WHOLE - 2));

    /**
     * The default constructor for the block.
     */
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
    public IBlockState getStateFromMeta(int meta) {
        return AgriProperties.GROWTHSTAGE.applyToBlockState(getDefaultState(), MathHelper.inRange(meta, 0, Constants.MATURE));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return AgriProperties.GROWTHSTAGE.getValue(state);
    }

    /**
     * Creates a new tile entity every time the block is placed.
     */
    @Override
    public TileEntityCrop createNewTileEntity(World world, int meta) {
        return new TileEntityCrop();
    }

    /**
     * Randomly called to apply GROWTH ticks
     */
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rand) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if (crop.hasPlant()) {
            if (crop.isMature()) {
                crop.spread(rand);
            } else if (crop.isFertile()) {
                //multiplier from GROWTH stat
                double growthBonus = 1.0 + crop.getStat().getGrowth() / 10.0;
                //multiplier defined in the config
                float global = AgriCraftConfig.growthMultiplier;
                //crop dependent base GROWTH rate
                float growthRate = (float) crop.getGrowthRate();
                //determine if GROWTH tick should be applied or skipped
                boolean shouldGrow = (rand.nextDouble() <= (growthRate * growthBonus * global) / 100);
                if (shouldGrow) {
                    crop.applyGrowthTick();
                }
            }
        } else if (crop.isCrossCrop() && (Math.random() < AgriCraftConfig.weedSpawnChance)) {
            crop.crossOver();
        } else {
            crop.spawn(rand);
        }
    }

    /**
     * Harvests the crop from a TileEntity (instance).
     *
     * @param world the World object for this block
     * @param pos the block position
     * @param player the player harvesting the crop. May be null if harvested
     * through automation.
     * @return if the block was harvested
     */
    public boolean harvest(World world, BlockPos pos, IBlockState state, EntityPlayer player, TileEntityCrop crop) {
        if (!world.isRemote) {
            crop = crop == null ? ((TileEntityCrop) world.getTileEntity(pos)) : crop;
            if (crop.canWeed()) {
                crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
                return false;
            } else if (crop.isCrossCrop()) {
                crop.setCrossCrop(false);
                spawnAsEntity(world, pos, new ItemStack(AgriItems.getInstance().CROPS, 1));
                return false;
            } else if (crop.canHarvest()) {
                crop.getWorld().setBlockState(crop.getPos(), AgriProperties.GROWTHSTAGE.applyToBlockState(state, 2), 2);
                List<ItemStack> drops = crop.getFruits();
                for (ItemStack drop : drops) {
                    if (drop == null || drop.getItem() == null) {
                        continue;
                    }
                    spawnAsEntity(world, pos, drop);
                }
                return true;
            }
        }
        return false;
    }

    public boolean harvest(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        return harvest(world, pos, state, player, null);
    }

    /**
     * Changes the crop from normal operation, to cross-crop operation.
     *
     * @param world the World object for this block
     * @param pos the block position
     * @param player the player applying the cross crop
     * @param cropStack stack containing crop sticks
     */
    public void setCrossCrop(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack cropStack) {
        if (!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            if (!crop.canWeed() && !crop.isCrossCrop() && !crop.hasPlant()) {
                crop.setCrossCrop(true);
                cropStack.stackSize = cropStack.stackSize - (player.capabilities.isCreativeMode ? 0 : 1);
            } else {
                this.harvest(world, pos, state, player, crop);
            }
        }
    }

    /**
     * Attempts to plant a SEED contained in the provided ItemStack.
     *
     * @param stack the SEED(s) to plant.
     * @param world the World object for this block
     * @param pos the block position
     * @return if the planting operation was successful.
     */
    public boolean plantSeed(ItemStack stack, World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            //is the cropEmpty a crosscrop or does it already have a plant
            if (crop.isCrossCrop() || crop.hasPlant()) {
                return false;
            }
            //the SEED can be planted here
            Optional<AgriSeed> seed = SeedRegistry.getInstance().valueOf(stack);
            return seed.isPresent()
                    && seed.get().getPlant().getGrowthRequirement().isMet(world, pos)
                    && crop.setSeed(seed.get());
        }
        return false;
    }

    /**
     * Handles right-clicks from the player. Allows the player to 'use' the
     * block.
     *
     * TODO: Clean up this horrible mess of a method.
     *
     * @return if the right-click was consumed.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        //only make things happen serverside
        if (world.isRemote) {
            return true;
        }
        // When hand rake is enabled and the block has weeds, abandon all hope
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            if (AgriItems.getInstance().HAND_RAKE.isEnabled() && heldItem == null && crop.canWeed()) {
                //if weeds can only be removed by using a hand rake, nothing should happen
                return false;
            } else if (player.isSneaking() || heldItem == null || heldItem.getItem() == null) {
                this.harvest(world, pos, state, player, crop);
            } else if (TypeHelper.isAnyType(heldItem.getItem(), ITEM_EXCLUDES)) {
                // Allow the excludes to do their things.
                return false;
            } else if (FertilizerRegistry.getInstance().hasAdapter(heldItem)) {
                Optional<IAgriFertilizer> fert = FertilizerRegistry.getInstance().valueOf(heldItem);
                return fert.isPresent() && fert.get().applyFertilizer(player, world, pos, crop, heldItem, RANDOM);
            } else if (plantSeed(heldItem, world, pos)) {
                return true;
            } //check to see if the player clicked with crops (crosscrop attempt)
            else if (heldItem.getItem() == AgriItems.getInstance().CROPS) {
                this.setCrossCrop(world, pos, state, player, heldItem);
            } else {
                //harvest operation
                this.harvest(world, pos, state, player, crop);
            }
        }
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
            IAgriPlant plant = ((TileEntityCrop) world.getTileEntity(pos)).getPlant();
            if (!player.capabilities.isCreativeMode) {
                //drop items if the player is not in creative
                this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            }
            if (plant != null) {
                plant.onPlantRemoved(world, pos);
            }
            world.removeTileEntity(pos);
            world.setBlockToAir(pos);
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
     * Handles the block drops. Called when the block is left-clicked or
     * otherwise breaks.
     */
    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if (!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            if (crop != null) {
                List<ItemStack> drops = new ArrayList<>();
                if (crop.isCrossCrop()) {
                    drops.add(new ItemStack(AgriItems.getInstance().CROPS, 2));
                } else {
                    if (!(crop.canWeed() && AgriCraftConfig.weedsDestroyCropSticks)) {
                        drops.add(new ItemStack(AgriItems.getInstance().CROPS, 1));
                    }
                    if (crop.hasPlant()) {
                        if (crop.isMature()) {
                            drops.addAll(crop.getFruits());
                            drops.add(crop.getSeed().toStack());
                        } else if (!AgriCraftConfig.onlyMatureDropSeeds) {
                            drops.add(crop.getSeed().toStack());
                        }
                    }
                }
                for (ItemStack drop : drops) {
                    if (drop != null && drop.getItem() != null) {
                        spawnAsEntity(world, pos, drop);
                    }
                }
            }
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
        return AgriProperties.GROWTHSTAGE.getValue(state) < Constants.MATURE;
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
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if (crop.hasPlant() || crop.canWeed()) {
            int l = AgriProperties.GROWTHSTAGE.getValue(state) + 2 + world.rand.nextInt(3);
            if (l > Constants.MATURE) {
                l = Constants.MATURE;
            }
            world.setBlockState(pos, AgriProperties.GROWTHSTAGE.applyToBlockState(state, l), 2);
        } else if (crop.isCrossCrop() && AgriCraftConfig.bonemealMutation) {
            crop.crossOver();
        }
    }

    /*
	 * Handles changes in the crop's neighbors.
     */
    @Override
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
        return world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityCrop && ((TileEntityCrop) world.getTileEntity(pos)).isFertile();
    }

    /**
     * Determines if the crops contain a mature plant by checking if the
     * metadata matches {@link Constants#MATURE}.
     *
     * @return if the crop is done growing.
     */
    public boolean isMature(World world, BlockPos pos) {
        return AgriProperties.GROWTHSTAGE.getValue(world.getBlockState(pos)) >= Constants.MATURE;
    }

    /**
     * Handles the plant being harvested from Ancient Warfare crop farms. This
     * is a separate method from
     * {@link #onBlockHarvested(World, BlockPos pos, IBlockState state, EntityPlayer)}
     * which handles the crops breaking.
     *
     * @return a list of drops from the harvested plant.
     */
    public List<ItemStack> doHarvest(World world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<>();
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if (crop.canWeed()) {
            crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
        } else if (crop.isCrossCrop()) {
            crop.setCrossCrop(false);
            drops.add(new ItemStack(AgriItems.getInstance().CROPS, 1));
        } else if (crop.canHarvest()) {
            crop.getWorld().setBlockState(pos, AgriProperties.GROWTHSTAGE.applyToBlockState(state, 2), 2);
            for (ItemStack stack : crop.getFruits()) {
                if (stack == null || stack.getItem() == null) {
                    continue;
                }
                drops.add(stack);
            }
        }
        return drops;
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
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<>();
        if (world.getTileEntity(pos) != null && world.getTileEntity(pos) instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            if (crop.isCrossCrop()) {
                items.add(new ItemStack(AgriItems.getInstance().CROPS, 2));
            } else {
                items.add(new ItemStack(AgriItems.getInstance().CROPS, 1));
            }
            if (crop.hasPlant()) {
                items.add(crop.getSeed().toStack());
                if (crop.isMature()) {
                    items.addAll(crop.getFruits());
                }
            }
        }
        return items;
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
     * Tries to uproot the plant: remove the plant, but keep the crop sticks in
     * place
     *
     * @return false, since the crops can't uproot normally.
     */
    public boolean upRoot(World world, BlockPos pos) {
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if (te != null && te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if (crop.hasPlant()) {
                    ArrayList<ItemStack> drops = new ArrayList<>();
                    if (crop.isMature()) {
                        drops.addAll(crop.getFruits());
                    }
                    drops.add(crop.getSeed().toStack());
                    for (ItemStack drop : drops) {
                        spawnAsEntity(world, pos, drop);
                    }
                }
                crop.removePlant();
            }
        }
        return false;
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

    /**
     * Determines if the block is a normal block, such as cobblestone. This
     * tells Minecraft if crops are not a normal block (meaning no levers can be
     * placed on it, it's transparent, ...).
     *
     * @return false - the block is not a normal block.
     */
    @Override
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
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
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
        // TileEntity tileEntity = world.getTileEntity(pos);
        return world.getBlockState(pos);
    }

    public Optional<TileEntityCrop> getCropTile(IBlockAccess world, BlockPos pos) {
        return Optional.of(world.getTileEntity(pos))
                .filter(t -> t instanceof TileEntityCrop)
                .map(t -> (TileEntityCrop) t);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    protected IBlockState extendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityCrop> tile = getCropTile(world, pos);
        return ((IExtendedBlockState) state)
                .withProperty(AgriProperties.PLANT_ID, tile.map(t -> t.getPlant()).map(p -> p.getId()).orElse("None"));
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        Optional<TileEntityCrop> tile = getCropTile(world, pos);
        return state
                .withProperty(AgriProperties.CROSSCROP.getProperty(), tile.map(t -> t.isCrossCrop()).orElse(false));
    }

    @Override
    public IUnlistedProperty[] getUnlistedPropertyArray() {
        return new IUnlistedProperty[]{
            AgriProperties.PLANT_ID
        };
    }

}
