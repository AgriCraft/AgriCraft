package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSeedStorageController extends BlockCustomWood {

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorageController();
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.seedStorageController;
    }
    
    @Override
    protected String getInternalName() {
        return Names.Objects.seedStorageController;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        if(player.isSneaking()) {
            return false;
        }
        if(!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageControllerID, world, x, y, z);
        }
        return true;
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int id, int data) {
        super.onBlockEventReceived(world, x, y, z, id, data);
        TileEntity tileentity = world.getTileEntity(x, y, z);
        return tileentity != null && tileentity.receiveClientEvent(id, data);
    }

    @Override
    public boolean isMultiBlock() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return null;
    }

}
