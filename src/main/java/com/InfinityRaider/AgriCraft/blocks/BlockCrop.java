package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.init.Crops;
import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.IGrowable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.ArrayList;
import java.util.Random;

public class BlockCrop extends BlockModPlant implements ITileEntityProvider, IGrowable {
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
        if (crop.hasPlant()) {
            super.updateTick(world, x, y, z, rnd);
        } else if (crop.crossCrop) {
            crop.crossOver(world, x, y, z);
        }
    }

    //this harvests the crop
    public void harvest(World world, int x, int y, int z) {
        if(!world.isRemote) {
            boolean update = false;
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if(crop.crossCrop) {
                crop.crossCrop = false;
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Items.crops, 1));
                update = true;
            }
            else if(crop.isMature()) {
                crop.getWorldObj().setBlockMetadataWithNotify(crop.xCoord, crop.yCoord, crop.zCoord, 2, 2);
                update = true;
                ArrayList<ItemStack> drops = SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta);
                for (ItemStack drop : drops) {
                    LogHelper.debug("Spawning item in world: " + Item.itemRegistry.getNameForObject(drop.getItem()) + ":" + drop.getItemDamage());
                    this.dropBlockAsItem(world, x, y, z, drop);
                }
            }
            if (update) {
                this.syncAndUpdate(world, x, y ,z);
            }
        }
    }

    public void setCrossCrop(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            boolean update = false;
            LogHelper.debug("Trying to set crosscrop");
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            if(!crop.crossCrop && !crop.hasPlant()) {
                crop.crossCrop=true;
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode?player.getCurrentEquippedItem().stackSize:player.getCurrentEquippedItem().stackSize - 1;
                update = true;
                LogHelper.debug("Crosscrop set");
            }
            if (update) {
                this.syncAndUpdate(world, x, y ,z);
            }
        }
    }

    public void plantSeed(World world, int x, int y, int z, EntityPlayer player) {
        if(!world.isRemote) {
            boolean update = false;
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            LogHelper.debug("Trying to plant " + player.getCurrentEquippedItem().getItem().getUnlocalizedName());
            //is the cropEmpty a crosscrop or does it already have a plant
            if (crop.crossCrop || crop.hasPlant()) {
                return;
            }
            //the seed can be planted here
            else {
                ItemStack stack = player.getCurrentEquippedItem();
                if (!SeedHelper.isValidSeed((ItemSeeds) stack.getItem())) {
                    return;
                }
                //get NBT data from the seeds
                if (player.getCurrentEquippedItem().stackTagCompound != null && player.getCurrentEquippedItem().stackTagCompound.hasKey(Names.growth)) {
                    //NBT data was found: copy data to plant
                    crop.setPlant(stack.stackTagCompound.getInteger(Names.growth), stack.stackTagCompound.getInteger(Names.gain), stack.stackTagCompound.getInteger(Names.strength), (ItemSeeds) stack.getItem(), stack.getItemDamage());
                } else {
                    //NBT data was not initialized: set defaults
                    crop.setPlant(Constants.defaultGrowth, Constants.defaultGain, Constants.defaultStrength, (ItemSeeds) stack.getItem(), stack.getItemDamage());
                }
                //take one seed away if the player is not in creative
                player.getCurrentEquippedItem().stackSize = player.capabilities.isCreativeMode ? player.getCurrentEquippedItem().stackSize : player.getCurrentEquippedItem().stackSize - 1;
                update = true;
            }
            if (update) {
                this.syncAndUpdate(world, x, y ,z);
            }
        }
    }

    public void syncAndUpdate(World world, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y ,z);
        world.addBlockEvent(x,y,z,this,1,0); //lets the tile entity know it has been updated
        crop.markDirty(); //lets Minecraft know the tile entity has changed
        world.notifyBlockChange(x,y,z,this); //lets the neighbors know this has been updated
    }

    //This gets called when the block is right clicked (player uses the block)
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        //only make things happen serverside
        if(!world.isRemote) {   //very important, makes sure everything happens on the server
            //check to see if the player has an empty hand
            if (player.getCurrentEquippedItem() == null) {
                this.harvest(world, x, y, z);
            }
            //check to see if the player clicked with crops (crosscrop attempt)
            else if (player.getCurrentEquippedItem().getItem() == Items.crops) {
                this.setCrossCrop(world, x, y, z, player);
            }
            //check to see if clicked with seeds
            else if (player.getCurrentEquippedItem().getItem() instanceof ItemSeeds) {
                this.plantSeed(world, x, y, z, player);
            }
        }
        return false;
    }

    //This gets called when the block is left clicked (player hits the block)
    @Override
    public void onBlockClicked(World world, int x, int y, int z, EntityPlayer player) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x,y,z), 0);
            }
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x,y,z);
        }
    }

    //item drops
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            ArrayList<ItemStack> drops= new ArrayList<ItemStack>();
            if(crop.crossCrop) {
                drops.add(new ItemStack(Items.crops, 2));
            }
            else {
                drops.add(new ItemStack(Items.crops, 1));
                if(crop.hasPlant()) {
                    drops.add(crop.getSeedStack());
                    if (this.isMature(world, x, y, z)) {
                        drops.addAll(SeedHelper.getPlantFruits((ItemSeeds) crop.seed, world, x, y, z, crop.gain, crop.seedMeta));
                    }
                }
            }
            for(ItemStack drop:drops) {
                this.dropBlockAsItem(world, x, y, z, drop);
            }
        }
    }

    //bonemeal can be applied to this plant
    @Override
    public boolean func_149852_a(World world, Random rand, int x, int y, int z) {
        return true;
    }

    //this gets called when the player uses bonemeal on the crop
    @Override
    public void func_149853_b(World world, Random rand, int x, int y, int z) {
        TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
        if(crop.hasPlant()) {
            super.func_149853_b(world, rand, x, y, z);
        }
        else if(crop.crossCrop && ConfigurationHandler.bonemealMutation) {
            crop.crossOver(world, x, y, z);
        }
    }

    //neighboring blocks get updated
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        //check if crops can stay
        if(!this.canBlockStay(world,x,y,z)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x,y,z);
        }
    }

    //see if the block can grow
    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return world.getTileEntity(x, y, z) != null && world.getTileEntity(x, y, z) instanceof TileEntityCrop && ((TileEntityCrop) world.getTileEntity(x, y, z)).isFertile();
    }

    //see if the block can stay
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        Block soil = world.getBlock(x,y-1,z);
        return (soil==Blocks.farmland);
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
                ItemStack seedStack = new ItemStack((ItemSeeds) crop.seed, 1, crop.seedMeta);
                seedStack.setTagCompound(new NBTTagCompound());
                SeedHelper.setNBT(seedStack.stackTagCompound, (short) crop.growth, (short) crop.gain, (short) crop.strength, false);
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

    //return the crops item if this block is called
    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Items.crops;
    }

    //rendering stuff
    @Override
    public int getRenderType() {return -1;}                 //get default render type: net.minecraft.client.renderer
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return false;}
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
    }
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return this.blockIcon;
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world,x,y,z,id,data);
        TileEntity tileEntity = world.getTileEntity(x,y,z);
        return (tileEntity!=null)&&(tileEntity.receiveClientEvent(id,data));
    }

}
