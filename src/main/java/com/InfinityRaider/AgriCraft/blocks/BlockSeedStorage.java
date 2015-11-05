package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderSeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.ArrayList;

public class BlockSeedStorage extends BlockCustomWood {

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorage();
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        if (!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageID, world, x, y, z);
        }
        return true;
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.seedStorage;
    }
    
    @Override
    protected String getInternalName() {
        return Names.Objects.seedStorage;
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(id, data);

    }

    @Override
    public boolean isMultiBlock() {
        return false;
    }

    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = super.getDrops(world, x, y, z, metadata, fortune);
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && (te instanceof TileEntitySeedStorage)) {
                TileEntitySeedStorage storage = (TileEntitySeedStorage) te;
                for (ItemStack stack : storage.getInventory()) {
                    int total = stack.stackSize;
                    while (total > 0) {
                        ItemStack newStack = stack.copy();
                        newStack.stackSize = Math.min(total, 64);
                        total = total - newStack.stackSize;
                        items.add(newStack);
                    }
                }
            }
        }
        return items;
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube() {
        return false;
    }           //tells minecraft that this is not a block (no levers can be placed on it, it's transparent, ...)

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }    //tells minecraft that this has custom rendering

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderSeedStorage();
    }
    
}
