package com.InfinityRaider.AgriCraft.blocks;


import com.InfinityRaider.AgriCraft.api.v1.*;
import com.InfinityRaider.AgriCraft.api.v2.IGrowthRequirementBuilder;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAgriCraftShearable;
import com.InfinityRaider.AgriCraft.compatibility.applecore.AppleCoreHelper;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.CropProduce;
import com.InfinityRaider.AgriCraft.farming.growthrequirement.GrowthRequirementHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.reference.Constants;
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
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;

import java.util.ArrayList;
import java.util.Random;

public class BlockModPlant extends BlockCrops implements IAgriCraftPlant {
    private IGrowthRequirement growthRequirement;
    public CropProduce products = new CropProduce();
    private ItemModSeed seed;
    public int tier;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    private RenderMethod renderType;

    /** Parameters can be given in any order, parameters can be: @param args:
     * Arguments can be given in any order, parameters can be:
     *               String name (needed)
     *               ItemStack fruit(needed)
     *               BlockWithMeta soil (optional, pass this argument before the RequirementType, else it will be interpreted as a baseblock)
     *               RequirementType (optional)
     *               BlockWithMeta baseBlock (optional, only if a RequirementType is specified first, else it will be set a a soil)
     *               int tier (necessary)
     *               RenderMethod renderType (necessary)
     *               ItemStack shearDrop (optional, first ItemStack argument will be the regular fruit, second ItemStack argument is the shear drop)
     *               int[] brightness (optional, if not given it will default to {8, 16}. Only works if the array is size 2: {minBrightness, maxBrightness})
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
        BlockWithMeta soil = null;
        RequirementType type = RequirementType.NONE;
        BlockWithMeta base = null;
        int tier = -1;
        RenderMethod renderType = null;
        int[] brightness = null;
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
            if(arg instanceof RequirementType) {
                type = (RequirementType) arg;
            }
            if(arg instanceof BlockWithMeta) {
                if(type != RequirementType.NONE) {
                    base = (BlockWithMeta) arg;
                    base = base.getBlock() == null ? null : base;
                } else {
                    soil = (BlockWithMeta) arg;
                    soil = soil.getBlock() == null ? null : soil;
                }
                continue;
            }
            if(arg instanceof RenderMethod) {
                renderType = (RenderMethod) arg;
                continue;
            }
            if(arg instanceof Integer) {
                tier = (Integer) arg;
            }
            if(arg instanceof int[]) {
                int[] array = (int[]) arg;
                brightness = array.length==2?array:brightness;
            }
        }
        //check if necessary parameters have been passed
        if(name==null || tier<0 || renderType==null) {
            throw new MissingArgumentsException();
        }
        //set fields
        IGrowthRequirementBuilder builder = GrowthRequirementHandler.getNewBuilder();
        if (base != null && type != RequirementType.NONE) {
            builder.requiredBlock(base, type, true);
        }
        if(brightness != null) {
            builder.brightnessRange(brightness[0], brightness[1]);
        }
        if (soil == null) {
            growthRequirement = builder.build();
        } else {
            growthRequirement = builder.soil(soil).build();
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
            LogHelper.printStackTrace(e);
        }
    }

    @Override
    public ItemModSeed getSeed() {return this.seed;}

    @Override
    public Block getBlock() {
        return this;
    }

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

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, int x, int y, int z) {
        return EnumPlantType.Crop;
    }

    @Override
    public boolean func_149851_a(World world, int x, int y, int z, boolean isRemote) {
        return this.tier<=3 && super.func_149851_a(world, x, y, z, isRemote);
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
        if (meta < Constants.MATURE && this.isFertile(world, x, y ,z)) {
            //Base growth rate
            int growthRate = (tier > 0 && tier <= Constants.GROWTH_TIER.length)?Constants.GROWTH_TIER[tier]:Constants.GROWTH_TIER[0];
            //Bonus for growth stat (because these crops are not planted on crop sticks, growth of 1 is applied)
            double bonus = 1.0 + (1 + 0.00) / 10;
            //Global multiplier as defined in the config
            float global = 2.0F - ConfigurationHandler.growthMultiplier;
            int newMeta = (rnd.nextDouble() > (growthRate * bonus * global) / 100) ? meta : meta + 1;
            if (newMeta != meta) {
                world.setBlockMetadataWithNotify(x, y, z, newMeta, 2);
                AppleCoreHelper.announceGrowthTick(this, world, x, y, z);
            }
        }
    }

    //check if the plant is mature
    public boolean isMature(World world, int x, int y, int z) {
        return world.getBlockMetadata(x, y, z) >= Constants.MATURE;
    }

    //render different stages
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        switch(meta) {
            case 0:
            case 1: return this.icons[0];
            case 2: 
            case 3: 
            case 4: return this.icons[1];
            case 5: 
            case 6: return this.icons[2];
            case 7: return this.icons[3];
        }
        return this.icons[meta/5];
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
        return meta >= Constants.MATURE ? this.func_149865_P() : this.func_149866_i();
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
    public IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }
}
