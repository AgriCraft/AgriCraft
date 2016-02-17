package com.infinityraider.agricraft.blocks;


import com.infinityraider.agricraft.api.v1.*;
import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.farming.cropplant.CropPlantAgriCraftShearable;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.CropProduce;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.items.ItemModSeed;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.utility.LogHelper;
import com.infinityraider.agricraft.utility.RegisterHelper;
import com.infinityraider.agricraft.utility.exception.MissingArgumentsException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import com.infinityraider.agricraft.reference.AgriCraftProperties;

public class BlockModPlant extends BlockCrops implements IAgriCraftPlant {
    private IGrowthRequirement growthRequirement;
    public CropProduce products = new CropProduce();
    private ItemModSeed seed;
    public int tier;
    @SideOnly(Side.CLIENT)
    private RenderMethod renderType;
    @SideOnly(Side.CLIENT)
    private TextureAtlasSprite[] icons;

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
    protected final BlockState createBlockState() {
        return new BlockState(this, AgriCraftProperties.GROWTHSTAGE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AgriCraftProperties.GROWTHSTAGE, Math.max(Math.min(0, meta), Constants.MATURE));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AgriCraftProperties.GROWTHSTAGE);
    }

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
    public boolean renderAsFlower() {
        return this.renderType==RenderMethod.CROSSED;
    }

    @Override
    public EnumPlantType getPlantType(IBlockAccess world, BlockPos pos) {
        return EnumPlantType.Crop;
    }

    @Override
    public boolean canGrow(World world, BlockPos pos, IBlockState state, boolean isRemote) {
        return this.tier<=3 && super.canGrow(world, pos, state, isRemote);
    }

    //growing
    @Override
    public void updateTick(World world, BlockPos pos, IBlockState state, Random rnd) {
        int meta = state.getValue(AgriCraftProperties.GROWTHSTAGE);
        if (meta < Constants.MATURE && this.isFertile(world, pos)) {
            //Base growth rate
            int growthRate = (tier > 0 && tier <= Constants.GROWTH_TIER.length)?Constants.GROWTH_TIER[tier]:Constants.GROWTH_TIER[0];
            //Bonus for growth stat (because these crops are not planted on crop sticks, growth of 1 is applied)
            double bonus = 1.0 + (1 + 0.00) / 10;
            //Global multiplier as defined in the config
            float global = 2.0F - ConfigurationHandler.growthMultiplier;
            int newMeta = (rnd.nextDouble() > (growthRate * bonus * global) / 100) ? meta : meta + 1;
            if (newMeta != meta) {
                world.setBlockState(pos, state.withProperty(AgriCraftProperties.GROWTHSTAGE, newMeta), 2);
                CompatibilityHandler.getInstance().announceGrowthTick(world, pos, state);
            }
        }
    }

    //check if the plant is mature
    public boolean isMature(World world, BlockPos pos, IBlockState state) {
        state = state == null ? world.getBlockState(pos) : state;
        return state.getValue(AgriCraftProperties.GROWTHSTAGE) >= Constants.MATURE;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(this.seed, 1, 0));
        if(state.getValue(AgriCraftProperties.GROWTHSTAGE)==7) {
            list.add(this.getRandomFruit(world instanceof World ? ((World) world).rand : new Random()));
        }
        return list;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return state.getValue(AgriCraftProperties.GROWTHSTAGE) >= Constants.MATURE ? this.getCrop() : this.getSeed();
    }

    //fruit gain
    @Override
    public int quantityDropped(Random rand) {
        return 1;
    }

    //neighboring blocks get updated
    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block neighborBlock) {
        //check if crops can stay
        if(!this.canBlockStay(world, pos, state)) {
            //the crop will be destroyed
            this.dropBlockAsItem(world, pos, state, 0);
            world.setBlockToAir(pos);
        }
    }

    //see if the block can stay
    @Override
    public boolean canBlockStay(World world, BlockPos pos, IBlockState state) {
        return (this.growthRequirement.isValidSoil(world, pos.add(0, -1, 0)));
    }

    //check if the plant can grow
    @Override
    public boolean isFertile(World world, BlockPos pos) {
        return this.growthRequirement.canGrow(world, pos);
    }

    //return the seeds
    @Override
    public ItemModSeed getSeed() {return this.seed;}

    //return the fruit
    @Override
    protected Item getCrop() {
        Item randomFruit = this.getRandomFruit(new Random()).getItem();
        return randomFruit==null?null:randomFruit;
    }

    @Override
    public int getRenderType() {
        return 3;
    }

    @Override
    public IGrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public TextureAtlasSprite getPlantIcon(int growthStage) {
        switch(growthStage) {
            case 0:
            case 1: return this.icons[0];
            case 2:
            case 3:
            case 4: return this.icons[1];
            case 5:
            case 6: return this.icons[2];
            case 7: return this.icons[3];
        }
        return this.icons[growthStage%icons.length];
    }

	@Override
	public TextureAtlasSprite getIcon() {
		return this.icons[0];
	}

    @Override
    public void registerIcons(IIconRegistrar iconRegistrar) {
        icons = new TextureAtlasSprite[4];
        String name = this.getUnlocalizedName();
        int index = name.indexOf(":");
        name = index > 0 ? name.substring(index+1) : name;
        index = name.indexOf(".");
        name = index > 0 ? name.substring(index+1) : name;
        for(int i = 1; i <= icons.length; i++) {
            icons[i-1] = iconRegistrar.registerIcon("agricraft:blocks/"+name+i);
        }
    }
}
