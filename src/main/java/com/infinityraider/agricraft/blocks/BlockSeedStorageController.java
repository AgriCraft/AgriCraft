package com.infinityraider.agricraft.blocks;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.renderers.blocks.IBlockRenderingHandler;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorageController;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockSeedStorageController extends BlockCustomWood<TileEntitySeedStorageController> {

	public BlockSeedStorageController() {
		super("seed_storage_controller", true);
	}
	
    @Override
    public TileEntitySeedStorageController createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorageController();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if(player.isSneaking()) {
            return false;
        }
        if(!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageControllerID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

	/* TODO!!!
    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int data) {
        super.onBlockEventReceived(world, pos, state, id, data);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, data);
    }*/

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    public AxisAlignedBB getDefaultBoundingBox() {
        return null;
    }

    @Override
	public IBlockRenderingHandler<TileEntitySeedStorageController> getRenderer() {
		return null;
	}

}
