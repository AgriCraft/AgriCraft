package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.creativetab.AgriCraftTab;
import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityAgricraft;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCustomWood;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public abstract class BlockCustomWood extends BlockContainerAgriCraft {
	
    public BlockCustomWood() {
        super(Material.wood);
        this.setHardness(2.0F);
        this.setResistance(5.0F);
        setHarvestLevel("axe", 0);
        this.setCreativeTab(AgriCraftTab.agriCraftTab);
        this.setStepSound(soundTypeWood);
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityCustomWood) {
            TileEntityCustomWood tileEntity = (TileEntityCustomWood) world.getTileEntity(x, y, z);
            tileEntity.setMaterial(stack);
        }
    	super.onBlockPlacedBy(world, x, y, z, entity, stack);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block b, int meta) {
        super.breakBlock(world,x,y,z, b,meta);
        world.removeTileEntity(x,y,z);
    }

    //override this to delay the removal of the tile entity until after harvestBlock() has been called
    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z, boolean willHarvest) {
        return !player.capabilities.isCreativeMode || super.removedByPlayer(world, player, x, y, z, willHarvest);
    }

    //when the block is harvested
    @Override
    public void harvestBlock(World world, EntityPlayer player, int x, int y, int z, int meta) {
        if((!world.isRemote) && (!player.isSneaking())) {
            if(!player.capabilities.isCreativeMode) {       //drop items if the player is not in creative
                this.dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x,y,z), 0);
            }
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
     public void dropBlockAsItemWithChance(World world, int x, int y, int z, int meta, float f, int fortune) {
        if(!world.isRemote) {
            ArrayList<ItemStack> drops = this.getDrops(world, x, y, z, meta, fortune);
            for(ItemStack drop:drops) {
                this.dropBlockAsItem(world, x, y, z, drop);
            }
        }
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int meta, int fortune) {
        ArrayList<ItemStack> drops = new ArrayList<ItemStack>();
        if(!world.isRemote) {
            ItemStack drop = new ItemStack(this, 1);
            this.setTag(world, x, y, z, drop);
            drops.add(drop);
        }
        return drops;
    }

    //creative item picking
    @Override
    public ItemStack getPickBlock(MovingObjectPosition target, World world, int x, int y, int z, EntityPlayer player) {
        ItemStack stack = new ItemStack(this, 1, world.getBlockMetadata(x, y, z));
        this.setTag(world, x, y, z, stack);
        return stack;
    }

    //prevent block from being removed by leaves
    @Override
    public boolean canBeReplacedByLeaves(IBlockAccess world, int x, int y, int z) {
        return false;
    }

    protected void setTag(World world, int x, int y, int z, ItemStack stack) {
        if(world.getTileEntity(x, y, z)!=null && world.getTileEntity(x, y, z) instanceof TileEntityCustomWood) {
            TileEntityCustomWood te = (TileEntityCustomWood) world.getTileEntity(x, y, z);
            stack.stackTagCompound = te.getMaterialTag();
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        return Blocks.planks.getIcon(side, 0);
    }

    //register icons
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister reg) {}

    @Override
    public boolean isOpaqueCube() {return false;}           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    public boolean renderAsNormalBlock() {return false;}    //tells minecraft that this has custom rendering

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {return false;}

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
    	return ItemBlockCustomWood.class;
    }
    
    @Override
    public ItemStack getWailaStack(BlockAgriCraft block, TileEntityAgricraft te) {
    	if(te != null && te instanceof TileEntityCustomWood) {
    		ItemStack stack = new ItemStack(block, 1, 0);
    		stack.setTagCompound(((TileEntityCustomWood) te).getMaterialTag());
    		return stack;
    	} else {
    		return null;
    	}
    }
}
