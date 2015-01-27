package com.InfinityRaider.AgriCraft.blocks;


import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.OreDictHelper;
import com.InfinityRaider.AgriCraft.utility.SeedHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.IGrowable;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSeeds;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Random;

public class BlockModPlant extends BlockCrops implements IGrowable {
    public Block soil;
    public Block base;
    public int baseMeta;
    public Item fruit;
    private ItemSeeds seed;
    public int fruitMeta;
    public int tier;
    @SideOnly(Side.CLIENT)
    private IIcon[] icons;
    private int renderType;

    public BlockModPlant(Item fruit) {
        this(null, null, 0, fruit, 0, 1, 6);
    }

    public BlockModPlant(Block soil, Item fruit) {
        this(soil, null, 0, fruit, 0, 1, 6);
    }

    public BlockModPlant(Item fruit, int fruitMeta) {
        this(null, null, 0, fruit, fruitMeta, 1, 6);
    }

    public BlockModPlant(Item fruit, int fruitMeta, int tier) {
        this(null, null, 0, fruit, fruitMeta, tier, 6);
    }

    public BlockModPlant(Item fruit, int fruitMeta, int tier, int renderType) {
        this(null, null, 0, fruit, fruitMeta, tier, renderType);
    }

    public BlockModPlant(Block soil, Block base, Item fruit, int tier, int renderType) {
        this(soil, base, 0, fruit, 0, tier, renderType);
    }

    public BlockModPlant(Block soil, Item fruit, int fruitMeta) {
        this(soil, null, 0, fruit, fruitMeta, 1, 6);
    }

    public BlockModPlant(Block base, int baseMeta, Item fruit, int fruitMeta, int tier, int renderType) {
        this(Blocks.farmland, base, baseMeta, fruit, fruitMeta, tier, renderType);
    }

    public BlockModPlant(Block soil, Block base, Item fruit, int fruitMeta, int tier, int renderType) {
        this(soil, base, 0, fruit, fruitMeta, tier, renderType);
    }

    public BlockModPlant(Block soil, Block base, int baseMeta, Item fruit, int tier, int renderType) {
        this(soil, base, baseMeta, fruit, 0, tier, renderType);
    }

    public BlockModPlant(Block soil, Block base, int baseMeta, Item fruit, int fruitMeta, int tier, int renderType) {
        super();
        this.soil = soil;
        this.base = base;
        this.baseMeta = baseMeta;
        this.fruit = fruit;
        this.fruitMeta = fruitMeta;
        this.tier = tier;
        this.setTickRandomly(true);
        this.setBlockBounds(0.0F,0.0F,0.0F,1.0F,1.0F,1.0F);
        this.useNeighborBrightness = true;
        this.renderType = renderType==1?renderType:6;
    }

    //set seed
    public void initializeSeed(ItemSeeds seed) {
        if(this.seed==null) {
            this.seed = seed;
        }
    }

    public ItemSeeds getSeed() {return this.seed;}

    public ItemStack getFruit() {return this.getFruit(1);}

    public ItemStack getFruit(int nr) {return new ItemStack(this.fruit, nr, this.fruitMeta);}

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
            double rate = 1.0 + (1 + 0.00) / 10;
            float growthRate = (float) SeedHelper.getBaseGrowth(this.tier);
            meta = (rnd.nextDouble() > (growthRate * rate)/100) ? meta : meta + 1;
            world.setBlockMetadataWithNotify(x, y, z, meta, 2);
        }

    }

    //check if the plant is mature
    public boolean isMature(World world, int x, int y, int z) {
        return this.getPlantMetadata(world,x,y,z)==7;
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
            list.add(new ItemStack(this.fruit, 1, this.fruitMeta));
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
        if(this.soil == world.getBlock(x,y-1,z) && world.getBlockLightValue(x,y+1,z)>8) {
            if((this.base==null) || OreDictHelper.isSameOre(this.base, this.baseMeta, world.getBlock(x, y - 2, z), world.getBlockMetadata(x, y - 2, z))) {
                return true;
            }
        }
        return false;
    }

    //return the seeds
    @Override
    protected Item func_149866_i() {
        return this.seed;
    }

    //return the fruit
    @Override
    protected Item func_149865_P() {
        return this.fruit;
    }

    @Override
    public int getRenderType() {
        return this.renderType;
    }
}
