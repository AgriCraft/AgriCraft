package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedStorage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockSeedStorage extends BlockCustomWood {
    public BlockSeedStorage() {
        super();
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorage();
    }

    //this sets the block's orientation based upon the direction the player is looking when the block is placed
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if(world.getTileEntity(x, y, z) instanceof TileEntitySeedStorage) {
            TileEntitySeedStorage te = (TileEntitySeedStorage) world.getTileEntity(x, y, z);
            int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
            switch(direction) {
                case 0: te.setDirection(ForgeDirection.NORTH.ordinal()); break;
                case 1: te.setDirection(ForgeDirection.EAST.ordinal()); break;
                case 2: te.setDirection(ForgeDirection.SOUTH.ordinal()); break;
                case 3: te.setDirection(ForgeDirection.WEST.ordinal()); break;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        if(player.isSneaking()) {
            return false;
        }
        if(!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageID, world, x, y, z);
        }
        return true;
    }
}
