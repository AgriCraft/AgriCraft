package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.api.v1.IFertiliser;
import com.InfinityRaider.AgriCraft.api.v2.IRake;
import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.api.v2.IClipper;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.network.MessageFertiliserApplied;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.shadowmage.ancientwarfare.api.IAncientWarfareFarmable;
import vazkii.botania.api.item.IHornHarvestable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The most important block in the mod.
 */
@Optional.InterfaceList(value = {
        @Optional.Interface(modid = Names.Mods.botania, iface = "vazkii.botania.api.item.IHornHarvestable"),
        @Optional.Interface(modid = Names.Mods.ancientWarfare, iface = "net.shadowmage.ancientwarfare.api.IAncientWarfareFarmable")
})
public class BlockCrop extends BlockContainerAgriCraft implements IGrowable, IPlantable, IHornHarvestable, IAncientWarfareFarmable {
    /** The set of icons used to render weeds. */
    @SideOnly(Side.CLIENT)
    private IIcon[] weedIcons;

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
    public void updateTick(World world, int x, int y, int z, Random rnd) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.hasPlant() || crop.hasWeed()) {
            Event.Result allowGrowthResult = AppleCoreHelper.validateGrowthTick(this, world, x, y, z, rnd);
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
     * @param x the x coordinate for this block
     * @param y the y coordinate for this block
     * @param z the z coordinate for this block
     * @param player the player harvesting the crop. May be null if harvested through automation.
     * @return if the block was harvested
     */
    public boolean harvest(World world, int x, int y, int z, EntityPlayer player, TileEntityCrop crop) {
        if(!world.isRemote) {
            crop = crop==null?((TileEntityCrop) world.getTileEntity(x, y, z)):crop;
            if(crop.hasWeed()) {
                crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
                return false;
            } else if(crop.isCrossCrop()) {
                crop.setCrossCrop(false);
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Items.crops, 1));
                return false;
            } else if(crop.isMature() && crop.allowHarvest(player)) {
                crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
                ArrayList<ItemStack> drops = crop.getFruits();
                for (ItemStack drop : drops) {
                    if(drop==null || drop.getItem()==null) {
                        continue;
                    }
                    this.dropBlockAsItem(world, x, y, z, drop);
                }
                return true;
            }
        }
        return false;
    }

    public boolean harvest(World world, int x, int y, int z, EntityPlayer player) {
        return harvest (world, x, y, z, player, null);
    }

    public boolean harvest(World world, int x, int y, int z, TileEntityCrop crop) {
        return harvest (world, x, y, z, null, crop);
    }

    public boolean harvest(World world, int x, int y, int z) {
        return harvest (world, x, y, z, null, null);
    }

    /**
     * Changes the crop from normal operation, to cross-crop operation.
     * 
     * @param world the World object for this block
     * @param x the x coordinate for this block
     * @param y the y coordinate for this block
     * @param z the z coordinate for this block
     * @param player the player applying the cross crop
     */
    public void setCrossCrop(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if(!crop.hasWeed() && !crop.isCrossCrop() && !crop.hasPlant()) {
                crop.setCrossCrop(true);
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode?player.getCurrentEquippedItem().stackSize:player.getCurrentEquippedItem().stackSize - 1;
            }
            else {
                this.harvest(world, x, y, z, player, crop);
            }
        }
    }

    /**
     * Attempts to plant a seed contained in the provided ItemStack.
     * 
     * @param stack the seed(s) to plant.
     * @param world the World object for this block
     * @param x the x coordinate for this block
     * @param y the y coordinate for this block
     * @param z the z coordinate for this block
     * @return if the planting operation was successful.
     */
    public boolean plantSeed(ItemStack stack, World world, int x, int y, int z) {
        if (!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            //is the cropEmpty a crosscrop or does it already have a plant
            if (crop.isCrossCrop() || crop.hasPlant() || !(CropPlantHandler.isValidSeed(stack))) {
                return false;
            }
            //the seed can be planted here
            if (!CropPlantHandler.getGrowthRequirement(stack).isValidSoil(world, x, y - 1, z)) {
                return false;
            }
            //get NBT data from the seeds
            if (stack.stackTagCompound != null && stack.stackTagCompound.hasKey(Names.NBT.growth)) {
                //NBT data was found: copy data to plant
                crop.setPlant(stack.stackTagCompound.getInteger(Names.NBT.growth), stack.stackTagCompound.getInteger(Names.NBT.gain), stack.stackTagCompound.getInteger(Names.NBT.strength), stack.stackTagCompound.getBoolean(Names.NBT.analyzed), stack.getItem(), stack.getItemDamage());
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
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        //only make things happen serverside
        if (world.isRemote) {
            return true;
        }
        // When hand rake is enabled and the block has weeds, abandon all hope
        TileEntity te = world.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) te;
            ItemStack heldItem = player.getCurrentEquippedItem();
            if (ConfigurationHandler.enableHandRake && crop.hasWeed()) {
                //if weeds can only be removed by using a hand rake, nothing should happen
                if(heldItem==null || heldItem.getItem()==null || !(heldItem.getItem() instanceof IRake))
                return false;
            }
            if (player.isSneaking()) {
                this.harvest(world, x, y, z, player, crop);
            } else if (heldItem == null || heldItem.getItem() == null) {
                //harvest operation
                this.harvest(world, x, y, z, player, crop);
            } else if (heldItem.getItem() == net.minecraft.init.Items.reeds) {
                //Enables reed planting, temporary code until I code in seed proxy's
                //TODO: create seed proxy handler to plant other things directly onto crops (for example the Ex Nihilo seeds)
                if(crop.hasPlant()) {
                    this.harvest(world, x, y, z, player, crop);
                } else if (!crop.isCrossCrop() && !crop.hasWeed()) {
                    CropPlant sugarcane = CropPlantHandler.getPlantFromStack(new ItemStack((ItemSeeds) Item.itemRegistry.getObject("AgriCraft:seedSugarcane")));
                    if (sugarcane != null && sugarcane.getGrowthRequirement().canGrow(world, x, y, z)) {
                        crop.setPlant(1, 1, 1, false, sugarcane);
                        if (!player.capabilities.isCreativeMode) {
                            heldItem.stackSize = heldItem.stackSize - 1;
                        }
                    }
                }
            }
            //check to see if the player clicked with crops (crosscrop attempt)
            else if (heldItem.getItem() == Items.crops) {
                this.setCrossCrop(world, x, y, z, player);
            }
            //trowel usage
            else if (heldItem.getItem() instanceof ITrowel) {
                crop.onTrowelUsed((ITrowel) heldItem.getItem(), heldItem);
            }
            //clipper usage
            else if(heldItem.getItem() instanceof IClipper) {
                this.onClipperUsed(world, x, y, z, crop);
                ((IClipper) heldItem.getItem()).onClipperUsed(world, x, y, z, player);
            }
            else if(heldItem.getItem() instanceof IRake) {
                if(crop.hasPlant()) {
                    return this.upRoot(world, x, y, z);
                } else if(crop.hasWeed()) {
                    ((IRake) heldItem.getItem()).removeWeeds(crop, heldItem);
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
                    NetworkWrapperAgriCraft.wrapper.sendToAllAround(new MessageFertiliserApplied(heldItem, x, y, z), new NetworkRegistry.TargetPoint(world.provider.dimensionId, x, y, z, 32));
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
                return ModHelper.handleRightClickOnCrop(world, x, y, z, player, heldItem, this, crop);
            } else {
                //harvest operation
                this.harvest(world, x, y, z, player, crop);
                //check to see if clicked with seeds
                if (CropPlantHandler.isValidSeed(heldItem)) {
                    if(this.plantSeed(player.getCurrentEquippedItem(), world, x, y, z)) {
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
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            CropPlant plant = ((TileEntityCrop) world.getTileEntity(x, y, z)).getPlant();
            if(!player.capabilities.isCreativeMode) {
                //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x,y,z), 0);
            }
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x, y, z);
            if(plant!= null) {
                plant.onPlantRemoved(world, x, y, z);
            }
        }
    }

    /**
     * Handles the block being harvested by calling {@link #onBlockClicked(World, int, int, int, EntityPlayer)}.
     */
    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        this.onBlockClicked(world, x, y, z, player);
    }

    /**
     * Handles the block drops. Called when the block is left-clicked or otherwise breaks.
     */
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if (crop != null) {
                ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
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
                        this.dropBlockAsItem(world, x, y, z, drop);
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
    public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
        return world.getBlockMetadata(x, y, z) < Constants.MATURE;
    }

    /**
     * Determines if bonemeal speeds up the growth of the contained plant.
     * 
     * @return true, bonemeal may speed up any contained plant.
     */
    @Override
    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        return true;
    }

    /**
     * Increments the contained plant's growth stage.
     * Called when bonemeal is applied to the block.
     */
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.hasPlant() || crop.hasWeed()) {
            int l = world.getBlockMetadata(x, y, z) + MathHelper.getRandomIntegerInRange(world.rand, 2, 5);
            if (l > Constants.MATURE) {
                l = Constants.MATURE;
            }
            world.setBlockMetadataWithNotify(x, y, z, l, 2);
        }
        else if(crop.isCrossCrop() && ConfigurationHandler.bonemealMutation) {
            crop.crossOver();
        }
    }

    /**
     * Handles changes in the crops neighbors. Used to detect if the crops had the soil stolen from under them and they should now break.
     */
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        //check if crops can stay
        if(!this.canBlockStay(world,x,y,z)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x, y, z);
        }
    }

    /**
     * Tests to see if the crop is still on valid soil.
     * 
     * @return if the crop is placed in a valid location.
     */
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return GrowthRequirementHandler.isSoilValid(world, x, y - 1, z);
    }

    /**
     * Determines if the the plant is fertile, and can grow.
     * 
     * @return if the plant can grow.
     */
    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop && ((TileEntityCrop) world.getTileEntity(x, y, z)).isFertile();
    }

    /**
     * Determines if the crops contain a mature plant by checking if the metadata matches {@link Constants#MATURE}.
     * 
     * @return if the crop is done growing.
     */
    public boolean isMature(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= Constants.MATURE;
    }

    /**
     * Handles the plant being harvested from Ancient Warfare crop farms.
     * This is a separate method from {@link #onBlockHarvested(World, int, int, int, int, EntityPlayer)} which handles the crops breaking.
     * 
     * @return a list of drops from the harvested plant.
     */
    @Override
    public List<ItemStack> doHarvest(World world, int x, int y, int z, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if (crop.hasWeed()) {
            crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
        } else if (crop.isCrossCrop()) {
            crop.setCrossCrop(false);
            drops.add(new ItemStack(Items.crops, 1));
        } else if (crop.isMature() && crop.allowHarvest(null)) {
            crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
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
    public void onClipperUsed(World world, int x, int y, int z, TileEntityCrop crop) {
        if(!crop.hasPlant()) {
            return;
        }
        int growthStage = world.getBlockMetadata(x, y, z);
        if(growthStage<=0) {
            return;
        }
        ItemStack clipping = new ItemStack(Items.clipping, 1, 0);
        clipping.setTagCompound(crop.getSeedStack().writeToNBT(new NBTTagCompound()));
        this.dropBlockAsItem(world, x, y, z, clipping);
        world.setBlockMetadataWithNotify(x, y, z, growthStage-1, 3);
    }

    /**
     * Retrieves the block's item form to be dropped when the block is broken.
     * 
     * @return the item form of the crop.
     */
    @Override
    public Item getItemDropped(int meta, Random rand, int side) {
        return Items.crops;
    }

    /**
     * Determines a list of what is dropped when the crops are broken.
     * 
     * @return a list of the items to drop.
     */
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
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
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world,x,y,z,block,meta);
        world.removeTileEntity(x,y,z);
    }

    /**
     * Tries to uproot the plant: remove the plant, but keep the crop sticks in place
     * @return false, since the crops can't uproot normally.
     */
    public boolean upRoot(World world, int x, int y, int z) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if(te!=null && te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(crop.hasPlant()) {
                    ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                    if(crop.isMature()) {
                        drops.addAll(crop.getFruits());
                    }
                    drops.add(crop.getSeedStack());
                    for (ItemStack drop : drops) {
                        this.dropBlockAsItem(world, x, y, z, drop);
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
    public Item getItem(World world, int x, int y, int z) {
        return Items.crops;
    }

    /**
     * Retrieves the block's collision bounding box. Since we want to be able to walk through the crops,
     * they should not collide anywhere, and their bounding box is therefore null.
     * 
     * @return null - the crops cannot be collided with.
     */
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return null;
    }

    /**
     * Retrieves the block's outline box for selections.
     * 
     * @return a bounding box representing the area occupied by the crops.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        return AxisAlignedBB.getBoundingBox((double)x + this.minX, (double)y + this.minY, (double)z + this.minZ, (double)x + this.maxX, (double)y + crop.getCropHeight(), (double)z + this.maxZ);
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
     * Determines if the crops should render as any normal block.
     * This tells Minecraft whether or not to call the custom renderer.
     * 
     * @return false - the block has custom rendering.
     */
    @Override
    public boolean renderAsNormalBlock() {return false;}

    /**
     * Determines if a side of the block should be rendered, such as one flush with a wall that wouldn't need rendering.
     * 
     * @return false - all of the crop's sides need to be rendered.
     */
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return true;}

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
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {return false;}

    /**
     * Registers the block's icons.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
        this.weedIcons = new IIcon[4];
        for(int i=0;i<weedIcons.length;i++) {
            this.weedIcons[i] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1) + "WeedTexture" + (i + 1));
        }
    }

    /**
     * Retrieve the icon for a side of the block.
     * 
     * @param side the side to get the icon for.
     * @param meta the metadata of the block.
     * @return the icon representing the side of the block.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    /**
     * Retrieve the icon for the weeds at a certain growth level represented by the metadata.
     * 
     * @param meta the growth level of the weeds.
     * @return the icon representing the current weed growth.
     */
    @SideOnly(Side.CLIENT)
    public IIcon getWeedIcon(int meta) {
        int index = 0;
        switch(meta) {
            case 0:
            case 1:index = 0;break;
            case 2:
            case 3:
            case 4:index = 1;break;
            case 5:
            case 6:index = 2;break;
            case 7:index = 3;break;
        }
        return this.weedIcons[index];
    }

    /**
     * Handles the block receiving events.
     * 
     * @return if the event was received properly.
     */
    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world,x,y,z,id,data);
        TileEntity tileEntity = world.getTileEntity(x,y,z);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id,data));
    }

    @Override
    public boolean isMultiBlock() {
        return false;
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
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Crop;
    }

    /**
     * Retrieves the block form of the contained plant.
     * 
     * @return the Block isntance of the plant currently planted
     */
    @Override
    public Block getPlant(IBlockAccess world, int x, int y, int z) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);
        if(tileEntity==null || !(tileEntity instanceof TileEntityCrop)) {
            return this;
        }
        TileEntityCrop crop = (TileEntityCrop) tileEntity;
        if(crop.hasPlant()) {
            return crop.getPlantBlock();
        }
        return this;
    }

    /**
     * Retrieves the metadata of the plant.
     * 
     * @return metadata representing the growth stage, ranges from 0 (inclusive) to 8 (exclusive)
     */
    @Override
    public int getPlantMetadata(IBlockAccess world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z);
    }
    
    @Override
    public ItemStack getWailaStack(BlockAgriCraft block, TileEntityAgricraft tea) {
    	return new ItemStack(Items.crops, 1, 0);
    }

    @Override
    @Optional.Method(modid=Names.Mods.botania)
    public boolean canHornHarvest(World world, int x, int y, int z, ItemStack stack, EnumHornType hornType) {
        return hornType.ordinal()==0 && this.isMature(world, x, y, z);
    }

    @Override
    @Optional.Method(modid=Names.Mods.botania)
    public boolean hasSpecialHornHarvest(World world, int x, int y, int z, ItemStack stack, EnumHornType hornType) {
        return hornType.ordinal()==0;
    }

    @Override
    @Optional.Method(modid=Names.Mods.botania)
    public void harvestByHorn(World world, int x, int y, int z, ItemStack stack, EnumHornType hornType) {
        if(hornType.ordinal()!=0) {
            return;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile == null || !(tile instanceof TileEntityCrop)) {
            return;
        }
        TileEntityCrop crop = (TileEntityCrop) tile;
        if(crop.hasPlant()) {
            if(this.harvest(world, x, y ,z, null, crop) && stack!=null && stack.getItem()!=null) {
                stack.attemptDamageItem(1, world.rand);
            }
        }
    }
}
