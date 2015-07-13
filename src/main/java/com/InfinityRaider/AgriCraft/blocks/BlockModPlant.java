package com.InfinityRaider.AgriCraft.blocks;


import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.RenderMethod;
import com.InfinityRaider.AgriCraft.api.v1.RequirementType;
import com.InfinityRaider.AgriCraft.apiimpl.v1.GrowthRequirement;
import com.InfinityRaider.AgriCraft.apiimpl.v1.cropplant.CropPlantAgriCraftShearable;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.CropProduce;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import com.InfinityRaider.AgriCraft.utility.exception.MissingArgumentsException;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockModPlant extends BlockCrops implements IAgriCraftPlant {
    private GrowthRequirement growthRequirement;
    public CropProduce products = new CropProduce();
    private ItemModSeed seed;
    public int tier;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    private RenderMethod renderType;

    /** Only used for the subclass which is the actual crop sticks */
    protected BlockModPlant() {
        super();
    }

    /** Parameters can be given in any order, parameters can be:
     * String name (needed), ItemStack fruit(needed), Block soil (optional), BlockWithMeta baseBlock (optional), int tier (necessary), RenderMethod renderType (necessary), ItemStack shearDrop (optional)
     * Will throw MissingArgumentsException if the needed arguments are not given.
     * This constructor creates the seed for this plant which can be gotten via blockModPlant.getSeed().
     * This constructor also registers this block and the item for the seed to the minecraft item/block registry and to the AgriCraft CropPlantHandler.
     * */
    public BlockModPlant(Object... arguments) throws MissingArgumentsException {
        super();
        //get parameters
        String name = null;
        ItemStack fruit = null;
        ItemStack shearable = null;
        Block soil = null;
        BlockWithMeta base = null;
        int tier = -1;
        RenderMethod renderType = null;
        for(Object arg:arguments) {
            if(arg == null) {
                continue;
            }
            if(arg instanceof String) {
                name = (String) arg;
                continue;
            }
            if(arg instanceof  ItemStack) {
                if(fruit==null) {
                    fruit = (ItemStack) arg;
                } else {
                    shearable = (ItemStack) arg;
                }
                continue;
            }
            if(arg instanceof Block) {
                soil = (Block) arg;
                continue;
            }
            if(arg instanceof BlockWithMeta) {
                base = (BlockWithMeta) arg;
                base = base.getBlock()==null?null:base;
                continue;
            }
            if(arg instanceof RenderMethod) {
                renderType = (RenderMethod) arg;
                continue;
            }
            if(arg instanceof Integer) {
                tier = (Integer) arg;
            }
        }
        //check if necessary parameters have been passed
        if(name==null || fruit==null || tier<0 || renderType==null) {
            throw new MissingArgumentsException();
        }
        //set fields
        GrowthRequirement.Builder builder = new GrowthRequirement.Builder();
        if (base != null) {
            builder.requiredBlock(base, RequirementType.BELOW, true);
        }
        if (soil == null) {
            growthRequirement = builder.build();
        } else {
            growthRequirement = builder.soil(new BlockWithMeta(soil)).build();
        }
        this.products.addProduce(fruit);
        this.tier = tier;
        this.setTickRandomly(true);
        this.useNeighborBrightness = true;
        this.renderType = renderType;
        //register this plant
        RegisterHelper.registerCrop(this, name);
        //create seed for this plant
        this.seed = new ItemModSeed(this, "agricraft_journal."+Character.toLowerCase(name.charAt(0))+name.substring(1));
        //register this plant to the CropPlantHandler
        try {
            if(shearable == null) {
                CropPlantHandler.registerPlant(this);
            } else {
                CropPlantHandler.registerPlant(new CropPlantAgriCraftShearable(this, shearable));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public ItemModSeed getSeed() {return this.seed;}

    @Override
    public ItemStack getSeedStack(int amount) {
        return new ItemStack(this.seed, amount);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {return this.products.getAllProducts();}

    @Override
    public ItemStack getRandomFruit(Random rand) {
        ArrayList<ItemStack> fruits = this.getFruit(1, rand);
        return fruits.size()>0?fruits.get(0):null;
    }

    @Override
    public ArrayList<ItemStack> getFruit(int nr, Random rand) {return this.products.getProduce(nr, rand);}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta) {
        return this.getIcon(0, meta);
    }

    @Override
    public boolean renderAsFlower() {
        return this.renderType==RenderMethod.CROSSED;
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        LogHelper.debug("registering icon for: " + this.getUnlocalizedName());
        this.icons = new IIcon[4];
        for(int i=1;i<this.icons.length+1;i++) {
            this.icons[i-1] = reg.registerIcon(this.getUnlocalizedName().substring(this.getUnlocalizedName().indexOf('.') + 1)+i);
        }
    }

    //growing
    @Override
    public void updateTick(World world, int x, int y, int z, Random rnd) {
        int meta = this.getPlantMetadata(world, x, y, z);
        if (meta < 7 && this.isFertile(world, x, y ,z)) {
            TileEntity te = world.getTileEntity(x, y, z);
            //base growth rate
            float growthRate;
            if(te==null || !(te instanceof TileEntityCrop)) {
                switch(tier) {
                    case 2: growthRate = Constants.growthTier2; break;
                    case 3: growthRate = Constants.growthTier3; break;
                    case 4: growthRate = Constants.growthTier4; break;
                    case 5: growthRate = Constants.growthTier5; break;
                    default: growthRate = Constants.growthTier1;
                }
            } else {
                TileEntityCrop crop = (TileEntityCrop) te;
                growthRate = (float) crop.getGrowthRate();
            }
            //bonus for growth stat (growth is 1 for basic crops)
            double bonus = 1.0 + (1 + 0.00) / 10;
            //global multiplier as defined in the config
            float global = 2.0F - ConfigurationHandler.growthMultiplier;
            int newMeta = (rnd.nextDouble() > (growthRate * bonus * global)/100) ? meta : meta + 1;
            if(newMeta != meta) {
                world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                AppleCoreHelper.announceGrowthTick(this, world, x, y, z);
            }
        }
    }

    //check if the plant is mature
    public boolean isMature(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te==null || !(te instanceof TileEntityCrop)) {
            return world.getBlockMetadata(x, y, z) == 7;
        }
        return ((TileEntityCrop) world.getTileEntity(x, y, z)).isMature();
    }

    //render different stages
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch(meta) {
            case 0: return this.icons[0];
            case 1: return this.icons[0];
            case 2: return this.icons[1];
            case 3: return this.icons[1];
            case 4: return this.icons[1];
            case 5: return this.icons[2];
            case 6: return this.icons[2];
            case 7: return this.icons[3];
        }
        return this.icons[(int)Math.floor(meta/5)];
    }

    //item drops
    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        super.dropBlockAsItemWithChance(world, x, y, z, meta, f, 0);
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<ItemStack>();
        list.add(new ItemStack(this.seed, 1, 0));
        if(metadata==7) {
            list.add(this.getRandomFruit(world.rand));
        }
        return list;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int side) {
        return meta == 7 ? this.func_149865_P() : this.func_149866_i();
    }

    //fruit gain
    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    //neighboring blocks get updated
    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        //check if crops can stay
        if(!this.canBlockStay(world,x,y,z)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
            world.setBlockToAir(x, y, z);
        }
    }

    //see if the block can stay
    @Override
    public boolean canBlockStay(World world, int x, int y, int z) {
        return (this.growthRequirement.isValidSoil(world, x, y-1, z));
    }

    //check if the plant can grow
    @Override
    public boolean isFertile(World world, int x, int y, int z) {
        return this.growthRequirement.canGrow(world, x, y, z);
    }

    //return the seeds
    @Override
    protected Item func_149866_i() {
        return this.seed;
    }

    //return the fruit
    @Override
    protected Item func_149865_P() {
        Item randomFruit = this.getRandomFruit(new Random()).getItem();
        return randomFruit==null?null:randomFruit;
    }

    @Override
    public int getRenderType() {
        return this.renderType.renderId();
    }

    @Override
    public GrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }
}
