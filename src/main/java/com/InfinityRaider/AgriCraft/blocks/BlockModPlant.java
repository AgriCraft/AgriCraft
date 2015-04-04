package com.InfinityRaider.AgriCraft.blocks;


import com.InfinityRaider.AgriCraft.farming.CropProduce;
import com.InfinityRaider.AgriCraft.api.v1.GrowthRequirement;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftSeed;
import com.InfinityRaider.AgriCraft.items.ItemModSeed;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.api.v1.BlockWithMeta;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockModPlant extends BlockCrops implements IGrowable, IAgriCraftPlant {

    private GrowthRequirement growthRequirement;
    public CropProduce products = new CropProduce();
    public ArrayList<ItemStack> fruits;
    private ItemModSeed seed;
    public int tier;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    private int renderType;
    private boolean isEditable;

    //give data in this order: {String name, Item fruit, int fuitMeta, Block soil, Block base, int baseMeta, int tier, int renderType}
    public BlockModPlant(Object[] data) {
        this((Item) data[1], (Integer) data[2], (Block) data[3], (Block) data[4], (Integer) data[5], (Integer) data[6], (Integer) data[7], false);
    }

    public BlockModPlant(Item fruit, int fruitMeta, Block soil, Block base, int baseMeta, int tier, int renderType, boolean isCustom) {
        super();

        GrowthRequirement.Builder builder = new GrowthRequirement.Builder();
        if (base != null) {
            builder.requiredBlock(new BlockWithMeta(base, baseMeta), GrowthRequirement.RequirementType.BELOW, true);
        }
        if (soil == null) {
            growthRequirement = builder.build();
        } else {
            growthRequirement = builder.soil(new BlockWithMeta(soil)).build();
        }

        this.products.addProduce(new ItemStack(fruit, 1, fruitMeta));
        this.tier = tier;
        this.setTickRandomly(true);
        this.useNeighborBrightness = true;
        this.renderType = renderType==1?renderType:6;
        this.isEditable = isCustom;
    }

    //set seed
    public void initializeSeed(ItemModSeed seed) {
        if(this.seed==null) {
            this.seed = seed;
        }
    }

    @Override
    public IAgriCraftSeed getSeed() {return this.seed;}

    @Override
    public ItemStack getSeedStack(int amount) {
        return new ItemStack(this.seed, amount);
    }

    @Override
    public ArrayList<ItemStack> getAllFruits() {return this.products.getAllProducts();}

    @Override
    public ItemStack getRandomFruit(Random rand) {return this.getFruit(1, rand).get(0);}

    @Override
    public ArrayList<ItemStack> getFruit(int nr, Random rand) {return this.products.getProduce(nr, rand);}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int meta) {
        return this.getIcon(0, meta);
    }

    @Override
    public boolean renderAsFlower() {
        return false;
    }

    public boolean canEdit() {
        return this.isEditable;
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
            TileEntityCrop crop = (TileEntityCrop) world.getTileEntity(x, y, z);
            double rate = 1.0 + (1 + 0.00) / 10;
            float growthRate = (float) crop.getGrowthRate();
            meta = (rnd.nextDouble() > (growthRate * rate)/100) ? meta : meta + 1;
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }

    }

    //check if the plant is mature
    public boolean isMature(World world, int x, int y, int z) {
        return ((TileEntityCrop) world.getTileEntity(x, y, z)).isMature();
    }

    //render different stages
    @Override
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
        Block soil = world.getBlock(x,y-1,z);
        return (soil instanceof net.minecraft.block.BlockFarmland);
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
        return this.getRandomFruit(new Random()).getItem();
    }

    @Override
    public int getRenderType() {
        return this.renderType;
    }

    @Override
    public GrowthRequirement getGrowthRequirement() {
        return growthRequirement;
    }

    public void setGrowthRequirement(GrowthRequirement req) {
        this.growthRequirement = req;
    }
}
