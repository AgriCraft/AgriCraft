package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.ItemBlockGrate;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockGrate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockGrate extends BlockCustomWood {
    
    @Override
    protected String getTileEntityName() {
        return Names.Objects.grate;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderBlockGrate();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.grate;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGrate();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockGrate.class;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return false; //In this case something wonky is going on and we don't care.
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        boolean front = grate.isPlayerInFront(player);
        if(player.isSneaking()) {
            if(grate.removeVines(front)) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.vine, 1));
                return true; //The vines were taken off, so consume the right click.
            }
        }
        else if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem()== Item.getItemFromBlock(Blocks.vine)) {
            if(grate.addVines(front) && !player.capabilities.isCreativeMode) {
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize-1;
                return true; //The vines were placed, so consume the right click.
            }
        }
        return false; //We didn't consume the click, so let it move on down the line.
    }

    @Override
     public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = super.getDrops(world, x, y, z, metadata, fortune);
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && (te instanceof TileEntityGrate)) {
                TileEntityGrate grate = (TileEntityGrate) te;
                int stackSize = 0;
                stackSize = grate.hasVines(true)?stackSize+1:stackSize;
                stackSize = grate.hasVines(false)?stackSize+1:stackSize;
                if(stackSize>0) {
                    items.add(new ItemStack(Blocks.vine, stackSize));
                }
            }
        }
        return items;
    }
    
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
    	return getBounds(world, x, y, z);
    }

    // Seems like the returned box is ever so slightly wider than it should be.
    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
    	return getBounds(world, x, y, z);
    }
    
	private void setBounds(AxisAlignedBB box, int x, int y, int z) {
		this.minX = box.minX - x;
		this.minY = box.minY - y;
		this.minZ = box.minZ - z;
		this.maxX = box.maxX - x;
		this.maxY = box.maxY - y;
		this.maxZ = box.maxZ - z;
	}
	
	private AxisAlignedBB getBounds(World world, int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
        if (te == null || !(te instanceof TileEntityGrate)) {
        	return null;
        }
        AxisAlignedBB bounds = ((TileEntityGrate) te).getBoundingBox();
        setBounds(bounds, x, y, z);
        return bounds;
	}

}
