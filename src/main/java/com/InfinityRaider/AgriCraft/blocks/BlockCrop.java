package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.network.MessageFertiliserApplied;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.BlockStates;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.*;

/**
 * The most important block in the mod.
 */
public class BlockCrop extends BlockContainerBase implements IGrowable, IPlantable {
    /** The set of icons used to render weeds. */

    /** The default constructor for the block. */
    public BlockCrop() {
        super(Material.plants);
        this.setTickRandomly(true);
        this.isBlockContainer = true;
        this.setStepSound(soundTypeGrass);
        this.setHardness(0.0F);
        this.disableStats();
        //set the bounding box dimensions
        this.maxX = Constants.UNIT*(Constants.WHOLE - 2);
        this.minX = Constants.UNIT*2;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = Constants.UNIT*(Constants.WHOLE - 3);
        this.minY = 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(BlockStates.AGE, Math.max(Math.min(0, meta), Constants.MATURE));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockStates.AGE);
    }

    /** Creates a new tile entity every time the block is placed. */
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCrop();
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.crop;
    }

    /** Randomly called to apply growth ticks */
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rnd) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if(crop.hasPlant() || crop.hasWeed()) {
            Event.Result allowGrowthResult = AppleCoreHelper.validateGrowthTick(this, world, pos, rnd);
            if (allowGrowthResult != Event.Result.DENY) {
            	if (crop.isMature() && crop.hasWeed() && ConfigurationHandler.enableWeeds){
                	crop.spreadWeed();
                }
            	else if (crop.isFertile()) {
                    //multiplier from growth stat
                    double growthBonus = 1.0 + crop.getGrowth() / 10.0;
                    //multiplier defined in the config
                    float global = ConfigurationHandler.growthMultiplier;
                    //crop dependent base growth rate
                    float growthRate = (float) crop.getGrowthRate();
                    //determine if growth tick should be applied or skipped
                    boolean shouldGrow = (rnd.nextDouble()<=(growthRate * growthBonus * global)/100);
                    if (shouldGrow) {
                        crop.applyGrowthTick();
                    }
                }
            }
        } else {
            //15% chance to spawn weeds
            if(ConfigurationHandler.enableWeeds && (Math.random() < ConfigurationHandler.weedSpawnChance)) {
                crop.spawnWeed();
            }
            else if(crop.isCrossCrop()) {
                crop.crossOver();
            }
        }
    }

    /**
     * Harvests the crop from a TileEntity (instance).
     * 
     * @param world the World object for this block
     * @param pos the block position
     * @param player the player harvesting the crop. May be null if harvested through automation.
     * @return if the block was harvested
     */
    public boolean harvest(World world, BlockPos pos, IBlockState state, EntityPlayer player, TileEntityCrop crop) {
        if(!world.isRemote) {
            crop = crop==null?((TileEntityCrop) world.getTileEntity(pos)):crop;
            if(crop.hasWeed()) {
                crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
                return false;
            } else if(crop.isCrossCrop()) {
                crop.setCrossCrop(false);
                spawnAsEntity(world, pos, new ItemStack(Items.crops, 1));
                return false;
            } else if(crop.isMature() && crop.allowHarvest(player)) {
                crop.getWorld().setBlockState(crop.getPos(), state.withProperty(BlockStates.AGE, 2), 2);
                ArrayList<ItemStack> drops = crop.getFruits();
                for (ItemStack drop : drops) {
                    if(drop==null || drop.getItem()==null) {
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
        return harvest (world, pos, state, player, null);
    }

    /**
     * Changes the crop from normal operation, to cross-crop operation.
     * 
     * @param world the World object for this block
     * @param pos the block position
     * @param player the player applying the cross crop
     */
    public void setCrossCrop(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            if(!crop.hasWeed() && !crop.isCrossCrop() && !crop.hasPlant()) {
                crop.setCrossCrop(true);
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode?player.getCurrentEquippedItem().stackSize:player.getCurrentEquippedItem().stackSize - 1;
            }
            else {
                this.harvest(world, pos, state, player, crop);
            }
        }
    }

    /**
     * Attempts to plant a seed contained in the provided ItemStack.
     * 
     * @param stack the seed(s) to plant.
     * @param world the World object for this block
     * @param pos the block position
     * @return if the planting operation was successful.
     */
    public boolean plantSeed(ItemStack stack, World world, BlockPos pos) {
        if (!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            //is the cropEmpty a crosscrop or does it already have a plant
            if (crop.isCrossCrop() || crop.hasPlant() || !(CropPlantHandler.isValidSeed(stack))) {
                return false;
            }
            //the seed can be planted here
            if (!CropPlantHandler.getGrowthRequirement(stack).isValidSoil(world, pos.add(0, -1, 0))) {
                return false;
            }
            //get NBT data from the seeds
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey(Names.NBT.growth)) {
                //NBT data was found: copy data to plant
                crop.setPlant(tag.getInteger(Names.NBT.growth), tag.getInteger(Names.NBT.gain), tag.getInteger(Names.NBT.strength), tag.getBoolean(Names.NBT.analyzed), stack.getItem(), stack.getItemDamage());
            } else {
                //NBT data was not initialized: set defaults
                crop.setPlant(Constants.DEFAULT_GROWTH, Constants.DEFAULT_GAIN, Constants.DEFAULT_STRENGTH, false, stack.getItem(), stack.getItemDamage());
            }
            return true;
        }
        return false;
    }

    /**
     * Handles right-clicks from the player. Allows the player to 'use' the block.
     * 
     * @return if the right-click was consumed.
     */
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
        //only make things happen serverside
        if (world.isRemote) {
            return true;
        }
        // When hand rake is enabled and the block has weeds, abandon all hope
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            ItemStack heldItem = player.getCurrentEquippedItem();
            if (ConfigurationHandler.enableHandRake && crop.hasWeed() && heldItem==null) {
                //if weeds can only be removed by using a hand rake, nothing should happen
                return false;
            }
            if (player.isSneaking()) {
                this.harvest(world, pos, state, player, crop);
            } else if (heldItem == null || heldItem.getItem() == null) {
                //harvest operation
                this.harvest(world, pos, state, player, crop);
            } else if (heldItem.getItem() == net.minecraft.init.Items.reeds) {
                //Enables reed planting, temporary code until I code in seed proxy's
                //TODO: create seed proxy handler to plant other things directly onto crops (for example the Ex Nihilo seeds)
                if(crop.hasPlant()) {
                    this.harvest(world, pos, state, player, crop);
                } else if (!crop.isCrossCrop() && !crop.hasWeed()) {
                    CropPlant sugarcane = CropPlantHandler.getPlantFromStack(new ItemStack(Item.itemRegistry.getObject(new ResourceLocation("AgriCraft:seedSugarcane"))));
                    if (sugarcane != null && sugarcane.getGrowthRequirement().canGrow(world, pos)) {
                        crop.setPlant(1, 1, 1, false, sugarcane);
                        if (!player.capabilities.isCreativeMode) {
                            heldItem.stackSize = heldItem.stackSize - 1;
                        }
                    }
                }
            }
            //check to see if the player clicked with crops (crosscrop attempt)
            else if (heldItem.getItem() == Items.crops) {
                this.setCrossCrop(world, pos, state, player);
            }
            //trowel usage
            else if (heldItem.getItem() instanceof ITrowel) {
                crop.onTrowelUsed((ITrowel) heldItem.getItem(), heldItem);
            }
            //clipper usage
            else if(heldItem.getItem() instanceof IClipper) {
                this.onClipperUsed(world, pos, state, crop);
                ((IClipper) heldItem.getItem()).onClipperUsed(world, pos, state, player);
            }
            else if(heldItem.getItem() instanceof IRake) {
                if(crop.hasPlant()) {
                    return this.upRoot(world, pos);
                } else if(crop.hasWeed()) {
                    ((IRake) heldItem.getItem()).removeWeeds(world, pos, state, crop, heldItem);
                }
            }
            //check to see if the player wants and is allowed to use bonemeal
            else if (heldItem.getItem() == net.minecraft.init.Items.dye && heldItem.getItemDamage() == 15) {
                return !crop.canBonemeal();
            }
            //fertiliser
            else if (heldItem.getItem() instanceof IFertiliser) {
                IFertiliser fertiliser = (IFertiliser) heldItem.getItem();
                if (crop.allowFertiliser(fertiliser)) {
                    crop.applyFertiliser(fertiliser, world.rand);
                    NetworkWrapperAgriCraft.wrapper.sendToAllAround(new MessageFertiliserApplied(heldItem, pos), new NetworkRegistry.TargetPoint(world.provider.getDimensionId(), pos.getX(), pos.getY(), pos.getZ(), 32));
                    if (!player.capabilities.isCreativeMode) {
                        heldItem.stackSize = heldItem.stackSize - 1;
                    }
                }
                return false;
            }
            //allow the debugger to be used
            else if (heldItem.getItem() instanceof ItemDebugger) {
                return false;
            }
            //mod interaction
            else if (ModHelper.isRightClickHandled(heldItem.getItem())) {
                return ModHelper.handleRightClickOnCrop(world, pos, state, player, heldItem, this, crop);
            } else {
                //harvest operation
                this.harvest(world, pos, state, player, crop);
                //check to see if clicked with seeds
                if (CropPlantHandler.isValidSeed(heldItem)) {
                    if(this.plantSeed(player.getCurrentEquippedItem(), world, pos)) {
                        //take one seed away if the player is not in creative
                        player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode ? player.getCurrentEquippedItem().stackSize : player.getCurrentEquippedItem().stackSize - 1;
                    }
                }
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
        if(!world.isRemote) {
            CropPlant plant = ((TileEntityCrop) world.getTileEntity(pos)).getPlant();
            if(!player.capabilities.isCreativeMode) {
                //drop items if the player is not in creative
                this.dropBlockAsItem(world, pos, world.getBlockState(pos), 0);
            }
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
            if(plant!= null) {
                plant.onPlantRemoved(world, pos);
            }
        }
    }

    /**
     * Handles the block being harvested by calling {@link #onBlockClicked(World, BlockPos pos, EntityPlayer)}.
     */
    @Override
    public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        this.onBlockClicked(world, pos, player);
    }

    /**
     * Handles the block drops. Called when the block is left-clicked or otherwise breaks.
     */
    @Override
    public void dropBlockAsItemWithChance(World world, BlockPos pos, IBlockState state, float chance, int fortune) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
            if (crop != null) {
                ArrayList<ItemStack> drops = new ArrayList<>();
                if (crop.isCrossCrop()) {
                    drops.add(new ItemStack(Items.crops, 2));
                } else {
                    if(!(crop.hasWeed() && ConfigurationHandler.weedsDestroyCropSticks)) {
                        drops.add(new ItemStack(Items.crops, 1));
                    }
                    if (crop.hasPlant()) {
                        if (crop.isMature()) {
                            drops.addAll(crop.getFruits());
                            drops.add(crop.getSeedStack());
                        }
                        else if(!ConfigurationHandler.onlyMatureDropSeeds) {
                            drops.add(crop.getSeedStack());
                        }
                    }
                }
                for (ItemStack drop : drops) {
                    if(drop!=null && drop.getItem()!=null) {
                        spawnAsEntity(world, pos, drop);
                    }
                }
            }
        }
    }

    /**
     * Determines if bonemeal may be applied to the plant contained in the crops.
     * 
     * @return if bonemeal may be applied.
     */
    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isClient) {
        return state.getValue(BlockStates.AGE) < Constants.MATURE;
    }

    /**
     * Determines if bonemeal speeds up the growth of the contained plant.
     * 
     * @return true, bonemeal may speed up any contained plant.
     */
    @Override
    public boolean canUseBonemeal(World world, Random rand, BlockPos pos, IBlockState state) {
        return true;
    }

    /**
     * Increments the contained plant's growth stage.
     * Called when bonemeal is applied to the block.
     */
    public void grow(World world, Random rand, BlockPos pos, IBlockState state) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if(crop.hasPlant() || crop.hasWeed()) {
            int l = state.getValue(BlockStates.AGE) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
            if (l > Constants.MATURE) {
                l = Constants.MATURE;
            }
            world.setBlockState(pos, state.withProperty(BlockStates.AGE, l), 2);
        }
        else if(crop.isCrossCrop() && ConfigurationHandler.bonemealMutation) {
            crop.crossOver();
        }
    }

    /**
     * Handles changes in the crops neighbors. Used to detect if the crops had the soil stolen from under them and they should now break.
     */
    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        //check if crops can stay
        if(!this.canBlockStay(world, pos)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
            world.removeTileEntity(pos);
        }
    }

    /**
     * Tests to see if the crop is still on valid soil.
     * 
     * @return if the crop is placed in a valid location.
     */
    public boolean canBlockStay(World world, BlockPos pos) {
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
     * Determines if the crops contain a mature plant by checking if the metadata matches {@link Constants#MATURE}.
     * 
     * @return if the crop is done growing.
     */
    public boolean isMature(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(BlockStates.AGE) >= Constants.MATURE;
    }

    /**
     * Handles the plant being harvested from Ancient Warfare crop farms.
     * This is a separate method from {@link #onBlockHarvested(World, BlockPos pos, IBlockState state, EntityPlayer)} which handles the crops breaking.
     * 
     * @return a list of drops from the harvested plant.
     */
    public List<ItemStack> doHarvest(World world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        if (crop.hasWeed()) {
            crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
        } else if (crop.isCrossCrop()) {
            crop.setCrossCrop(false);
            drops.add(new ItemStack(Items.crops, 1));
        } else if (crop.isMature() && crop.allowHarvest(null)) {
            crop.getWorld().setBlockState(pos, state.withProperty(BlockStates.AGE, 2), 2);
            for(ItemStack stack:crop.getFruits()) {
                if(stack==null || stack.getItem()==null) {
                    continue;
                }
                drops.add(stack);
            }
        }
        return drops;
    }

    /** Performs the needed action when this crop is clipped: check if there is a valid plant, drop a clipping and decrement the growth stage */
    public void onClipperUsed(World world, BlockPos pos, IBlockState state, TileEntityCrop crop) {
        if(!crop.hasPlant()) {
            return;
        }
        int growthStage = state.getValue(BlockStates.AGE);
        if(growthStage<=0) {
            return;
        }
        ItemStack clipping = new ItemStack(Items.clipping, 1, 0);
        clipping.setTagCompound(crop.getSeedStack().writeToNBT(new NBTTagCompound()));
        spawnAsEntity(world, pos, clipping);
        world.setBlockState(pos, state.withProperty(BlockStates.AGE, growthStage-1), 3);
    }

    /**
     * Retrieves the block's item form to be dropped when the block is broken.
     * 
     * @return the item form of the crop.
     */
    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Items.crops;
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
                items.add(new ItemStack(Items.crops, 2));
            } else {
                items.add(new ItemStack(Items.crops, 1));
            }
            if (crop.hasPlant()) {
                ItemStack seedStack = crop.getSeedStack().copy();
                items.add(seedStack);
                if(crop.isMature()) {
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
     * Tries to uproot the plant: remove the plant, but keep the crop sticks in place
     * @return false, since the crops can't uproot normally.
     */
    public boolean upRoot(World world, BlockPos pos) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(pos);
            if(te!=null && te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(crop.hasPlant()) {
                    ArrayList<ItemStack> drops = new ArrayList<>();
                    if(crop.isMature()) {
                        drops.addAll(crop.getFruits());
                    }
                    drops.add(crop.getSeedStack());
                    for (ItemStack drop : drops) {
                        spawnAsEntity(world, pos, drop);
                    }
                }
                crop.clearPlant();
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
    public Item getItem(World world, BlockPos pos) {
        return Items.crops;
    }

    /**
     * Retrieves the block's collision bounding box. Since we want to be able to walk through the crops,
     * they should not collide anywhere, and their bounding box is therefore null.
     * 
     * @return null - the crops cannot be collided with.
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        return null;
    }

    /**
     * Retrieves the block's outline box for selections.
     * 
     * @return a bounding box representing the area occupied by the crops.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBox(World world, BlockPos pos) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(pos);
        return new AxisAlignedBB(pos.getX() + this.minX, pos.getY() + this.minY, pos.getZ() + this.minZ, pos.getX() + this.maxX, pos.getY() + crop.getCropHeight(), pos.getZ() + this.maxZ);
    }

    /**
     * Determines if the block is a normal block, such as cobblestone.
     * This tells Minecraft if crops are not a normal block (meaning no levers can be placed on it, it's transparent, ...).
     * 
     * @return false - the block is not a normal block.
     */
    @Override
    public boolean isOpaqueCube() {return false;}

    /**
     * Determines if a side of the block should be rendered, such as one flush with a wall that wouldn't need rendering.
     * 
     * @return false - all of the crop's sides need to be rendered.
     */
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {return true;}

    /**
     * Renders the hit effects, such as the flying particles when the block is hit.
     * 
     * @return false - the block is one-shot and needs no hit particles.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {return false;}

    /**
     * Tells Minecraft if there should be destroy effects, such as particles.
     * 
     * @return false - there are no destroy particles.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, net.minecraft.client.particle.EffectRenderer effectRenderer) {return false;}

    /**
     * Handles the block receiving events.
     * 
     * @return if the event was received properly.
     */
    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int param) {
        super.onBlockEventReceived(world, pos, state, id, param);
        TileEntity tileEntity = world.getTileEntity(pos);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id, param));
    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[] {BlockStates.AGE};
    }

    /**
     * Retrieves the custom renderer for the crops.
     * 
     * @return the block's renderer.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderCrop();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return null;
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.crops;
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
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity==null || !(tileEntity instanceof ICrop)) {
            return world.getBlockState(pos);
        }
        ICrop crop = (ICrop) tileEntity;
        if(crop.hasPlant()) {
            return crop.getPlantBlockState().getBaseState();
        }
        return world.getBlockState(pos);
    }
    
    @Override
    public ItemStack getWailaStack(BlockBase block, TileEntityBase tea) {
    	return new ItemStack(Items.crops, 1, 0);
    }
}
