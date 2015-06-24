package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.init.Items;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntitySprinkler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockSprinkler extends BlockContainer {
    public BlockSprinkler() {
        super(Material.iron);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.maxX = Constants.unit*12;
        this.minX = Constants.unit*4;
        this.maxZ = this.maxX;
        this.minZ = this.minX;
        this.maxY = Constants.unit*20;
        this.minY = Constants.unit*12;

    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySprinkler();
    }

    @Override
    public boolean isReplaceable(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, meta, 0);
            }
            world.setBlockToAir(x,y,z);
            world.removeTileEntity(x,y,z);
        }
    }

    @Override
    public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int i) {
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(Items.sprinkler, 1);
            this.dropBlockAsItem(world, x, y, z, drop);
        }
    }

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
        Block channel = world.getBlock(x,y+1,z);
        return (channel==com.InfinityRaider.AgriCraft.init.Blocks.blockWaterChannel);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public Item getItem(World world, int x, int y, int z) {
        return Items.sprinkler;
    }

    //rendering stuff
    @Override
    public int getRenderType() {return -1;}                 //get default render type: net.minecraft.client.renderer
    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)
    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering
    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return false;}

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(0, 0);
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {
        //NOOP
    }
}
