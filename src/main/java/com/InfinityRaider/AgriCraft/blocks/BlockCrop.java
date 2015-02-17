package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.ModIntegration;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.GrowthRequirements;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.items.ItemDebugger;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import com.mark719.magicalcrops.items.ItemMagicalCropFertilizer;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import tconstruct.items.tools.Scythe;
import tconstruct.library.tools.AbilityHelper;
import vazkii.botania.api.item.IGrassHornExcempt;

import java.util.ArrayList;
import java.util.Random;

@Optional.Interface(modid = Names.Mods.botania, iface = "vazkii.botania.api.item.IGrassHornExcempt")
public class BlockCrop extends BlockModPlant implements ITileEntityProvider, IGrowable, IGrassHornExcempt {

    @SideOnly(Side.CLIENT)
    private IIcon[] weedIcons;

    public BlockCrop() {
        super(Blocks.farmland, null, null, 0, 0, 6);
        this.isBlockContainer = true;
        //set the bounding box dimensions
        this.maxX = Constants.unit*14;
        this.minX = Constants.unit*2;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = Constants.unit*13;
        this.minY = 0;
    }

    //this makes a new tile entity every time you place the block
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityCrop();
    }

    //this makes the plant grow
    @Override
    public void updateTick(World world, int x, int y, int z, Random rnd) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.hasPlant()) {
            Event.Result allowGrowthResult = AppleCoreHelper.validateGrowthTick(this, world, x, y, z, rnd);
            if (allowGrowthResult != Event.Result.DENY) {
                int meta = this.getPlantMetadata(world, x, y, z);
                if (meta < 7 && crop.isFertile()) {
                    double multiplier = 1.0 + (crop.growth + 0.00) / 10;
                    float growthRate = (float) SeedHelper.getBaseGrowth((ItemSeeds) crop.seed, crop.seedMeta);
                    boolean shouldGrow = (rnd.nextDouble()<=(growthRate * multiplier)/100);
                    if (shouldGrow) {
                        meta++;
                        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
                        AppleCoreHelper.announceGrowthTick(this, world, x, y, z);
                    }
                }
            }
        } else if(crop.weed) {
            Event.Result allowGrowthResult = AppleCoreHelper.validateGrowthTick(this, world, x, y, z, rnd);
            if (allowGrowthResult != Event.Result.DENY) {
                int meta = this.getPlantMetadata(world, x, y, z);
                if (meta<7) {
                    double multiplier = 1.0 + (10 + 0.00) / 10;
                    float growthRate = (float) Constants.growthTier1;
                    boolean shouldGrow = (rnd.nextDouble()<=(growthRate * multiplier)/100);
                    if (shouldGrow) {
                        meta++;
                        world.setBlockMetadataWithNotify(x, y, z, meta, 2);
                        AppleCoreHelper.announceGrowthTick(this, world, x, y, z);
                    }
                }
                else {
                    if(ConfigurationHandler.enableWeeds) {
                        crop.spreadWeed();
                    }
                }
            }
        } else {
            //10%chance to spawn weeds
            if(ConfigurationHandler.enableWeeds && Math.random()<0.10) {
                crop.spawnWeed();
            }
            else if(crop.crossCrop) {
                crop.crossOver();
            }
        }
    }

    //this harvests the crop
    public boolean harvest(World world, int x, int y, int z) {
        if(!world.isRemote) {
            boolean update = false;
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if(crop.weed) {
                crop.clearWeed();   //update is not needed because it is called in the clearWeed() method
            }else if(crop.crossCrop) {
                crop.crossCrop = false;
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Items.crops, 1));
                update = true;
            } else if(crop.isMature()) {
                crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
                update = true;
                ArrayList<ItemStack> drops = SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta);
                for (ItemStack drop : drops) {
                    this.dropBlockAsItem(world, x, y, z, drop);
                }
            }
            if (update) {
                crop.markForUpdate();
            }
            return update;
        }
        return false;
    }

    public void setCrossCrop(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            boolean update = false;
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if (crop.weed) {
                return;
            }

            if(!crop.crossCrop && !crop.hasPlant()) {
                crop.crossCrop=true;
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode?player.getCurrentEquippedItem().stackSize:player.getCurrentEquippedItem().stackSize - 1;
                update = true;
            }
            else {
                this.harvest(world, x, y, z);
            }
            if (update) {
                crop.markForUpdate();
            }
        }
    }

    public void plantSeed(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            //is the cropEmpty a crosscrop or does it already have a plant
            if (crop.crossCrop || crop.hasPlant() || !(player.getCurrentEquippedItem().getItem() instanceof ItemSeeds)) {
                return;
            }
            //the seed can be planted here
            else {
                ItemStack stack = player.getCurrentEquippedItem();
                if (!SeedHelper.isValidSeed((ItemSeeds) stack.getItem(), stack.getItemDamage()) || !GrowthRequirements.getGrowthRequirement((ItemSeeds) stack.getItem(), stack.getItemDamage()).canGrow(world, x, y, z)) {
                    return;
                }
                //get NBT data from the seeds
                if (player.getCurrentEquippedItem().stackTagCompound != null && player.getCurrentEquippedItem().stackTagCompound.hasKey(Names.NBT.growth)) {
                    //NBT data was found: copy data to plant
                    crop.setPlant(stack.stackTagCompound.getInteger(Names.NBT.growth), stack.stackTagCompound.getInteger(Names.NBT.gain), stack.stackTagCompound.getInteger(Names.NBT.strength), stack.stackTagCompound.getBoolean(Names.NBT.analyzed), (ItemSeeds) stack.getItem(), stack.getItemDamage());
                } else {
                    //NBT data was not initialized: set defaults
                    crop.setPlant(Constants.defaultGrowth, Constants.defaultGain, Constants.defaultStrength, false, (ItemSeeds) stack.getItem(), stack.getItemDamage());
                }
                //take one seed away if the player is not in creative
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode ? player.getCurrentEquippedItem().stackSize : player.getCurrentEquippedItem().stackSize - 1;
            }
            crop.markForUpdate();
        }
    }

    //This gets called when the block is right clicked (player uses the block)
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        //only make things happen serverside
        if(!world.isRemote) {
            // When hand rake is enabled and the block has weeds, abandon all hope
            TileEntity te = world.getTileEntity(x, y, z);
            if (ConfigurationHandler.enableHandRake && te != null && te instanceof TileEntityCrop && ((TileEntityCrop)te).weed) {
                return false;
            }

            if(player.isSneaking()) {
                this.harvest(world, x, y, z);
            }
            else if(player.getCurrentEquippedItem()==null) {
                //harvest operation
                this.harvest(world, x, y, z);
            }
            else if(player.getCurrentEquippedItem().getItem()==Items.debugItem) {
                return false;
            }
            //check to see if the player clicked with crops (crosscrop attempt)
            else if(player.getCurrentEquippedItem().getItem()==Items.crops) {
                this.setCrossCrop(world, x, y, z, player);
            }
            //check to see if the player wants to use bonemeal
            else if(player.getCurrentEquippedItem().getItem()==net.minecraft.init.Items.dye && player.getCurrentEquippedItem().getItemDamage()==15) {
                return false;
            }
            //magical crops fertiliser
            else if(ModIntegration.LoadedMods.magicalCrops && ConfigurationHandler.integration_allowMagicFertiliser && player.getCurrentEquippedItem().getItem() instanceof ItemMagicalCropFertilizer) {
                return this.applyMagicalFertiliser(world, x, y, z, player);
            }
            //allow the debugger to be used
            else if(player.getCurrentEquippedItem().getItem() instanceof ItemDebugger) {
                return false;
            }
            //tinker's construct scythe
            else if(ModIntegration.LoadedMods.tconstruct && player.getCurrentEquippedItem().getItem() instanceof Scythe) {
                for(int xPos=x-1;xPos<=x+1;xPos++) {
                    for(int zPos=z-1;zPos<=z+1;zPos++) {
                        if(world.getBlock(xPos, y, zPos) instanceof BlockCrop && this.harvest(world, xPos, y, zPos)) {
                            AbilityHelper.damageTool(player.getCurrentEquippedItem(), 1, player, false);
                        }
                    }
                }
            }
            else {
                //harvest operation
                this.harvest(world, x, y, z);
                //check to see if clicked with seeds
                if(player.getCurrentEquippedItem().getItem() instanceof ItemSeeds) {
                    this.plantSeed(world, x, y, z, player);
                }
            }
        }
        //Returning true will prevent other things from happening
        return true;
    }

    //This gets called when the block is left clicked (player hits the block)
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x,y,z), 0);
            }
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x, y, z);
        }
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        this.onBlockClicked(world, x, y, z, player);
    }

    //item drops
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if (crop != null) {
                ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                if (crop.crossCrop) {
                    drops.add(new ItemStack(Items.crops, 2));
                } else {
                    drops.add(new ItemStack(Items.crops, 1));
                    if (crop.hasPlant()) {
                        drops.add(crop.getSeedStack());
                        if (this.isMature(world, x, y, z)) {
                            drops.addAll(SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta));
                        }
                    }
                }
                for (ItemStack drop : drops) {
                    this.dropBlockAsItem(world, x, y, z, drop);
                }
            }
        }
    }

    //bonemeal can be applied to this plant
    @Override
    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.crossCrop) {
            return ConfigurationHandler.bonemealMutation;
        }
        if(crop.hasPlant()) {
            if(SeedHelper.getSeedTier((ItemSeeds) crop.seed, crop.seedMeta)<4) {
                return !this.isMature(world, x, y , z);
            }
        }
        return false;
    }

    //this gets called when the player uses bonemeal on the crop
    @Override
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.hasPlant() && this.isFertile(world, x, y ,z)) {
            super.func_149853_b(world, rand, x, y, z);
            crop.markForUpdate();
        }
        else if(crop.crossCrop && ConfigurationHandler.bonemealMutation) {
            crop.crossOver();
        }
    }

    //magical fertiliser
    private boolean applyMagicalFertiliser(World world, int x, int y, int z, EntityPlayer player) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        boolean allow = crop.hasPlant() && !crop.isMature() && crop.isFertile();
        if (allow) {
            if (ConfigurationHandler.integration_instantMagicFertiliser)  {
                world.setBlockMetadataWithNotify(x, y, z, 7, 2);
            } else {
                this.func_149853_b(world, world.rand, x, y, z);
            }

            if(!player.capabilities.isCreativeMode) {
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize-1;
            }
        }
        return allow;
    }

    //neighboring blocks get updated
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

    //see if the block can stay
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return GrowthRequirements.isSoilValid(world, x, y-1, z);
    }

    //see if the block can grow
    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop && ((TileEntityCrop) world.getTileEntity(x, y, z)).isFertile();
    }

    //get a list with items dropped by the the crop
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = new ArrayList<ItemStack>();
        if (world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if (crop.crossCrop) {
                items.add(new ItemStack(Items.crops, 2));
            } else {
                items.add(new ItemStack(Items.crops, 1));
            }
            if (crop.hasPlant()) {
                ItemStack seedStack = crop.getSeedStack().copy();
                items.add(seedStack);
                if(crop.isMature()) {
                    items.addAll(SeedHelper.getPlantFruits((ItemSeeds) crop.seed, crop.getWorldObj(), crop.xCoord, crop.yCoord, crop.zCoord, crop.gain, crop.seedMeta));
                }
            }
        }
        return items;
    }

    //when the block is broken
    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        super.breakBlock(world,x,y,z,block,meta);
        world.removeTileEntity(x,y,z);
    }

    //Botania horn of the wild support
    @Override
    public boolean canUproot(World world, int x, int y, int z) {
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if(te!=null && te instanceof TileEntityCrop) {
                TileEntityCrop crop = (TileEntityCrop) te;
                if(crop.hasPlant()) {
                    ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
                    if(crop.isMature()) {
                        drops.addAll(SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta));
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

    //return the crops item if this block is called
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Items.crops;
    }

    //rendering stuff
    @Override public int getRenderType() {return AgriCraft.proxy.getRenderId(Constants.cropId);}       //get the correct render type

    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return true;}
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addHitEffects(World worldObj, MovingObjectPosition target, EffectRenderer effectRenderer) {return false;}        //no particles when this block gets hit
    @Override
    @SideOnly(Side.CLIENT)
    public boolean addDestroyEffects(World world, int x, int y, int z, int meta, EffectRenderer effectRenderer) {return false;}     //no particles when destroyed
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        this.blockIcon = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1));
        this.weedIcons = new IIcon[4];
        for(int i=0;i<weedIcons.length;i++) {
            this.weedIcons[i] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1) + "WeedTexture" + (i + 1));
        }
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getWeedIcon(int meta) {
        int index = 0;
        switch(meta) {
            case 0:index = 0;break;
            case 1:index = 0;break;
            case 2:index = 1;break;
            case 3:index = 1;break;
            case 4:index = 1;break;
            case 5:index = 2;break;
            case 6:index = 2;break;
            case 7:index = 3;break;
        }
        return this.weedIcons[index];
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world,x,y,z,id,data);
        TileEntity tileEntity = world.getTileEntity(x,y,z);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id,data));
    }

}
