package com.infinityraider.agricraft.blocks.storage;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorageController;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import com.infinityraider.infinitylib.render.block.RenderBlock;

public class BlockSeedStorageController extends BlockCustomWood<TileEntitySeedStorageController> {

	public BlockSeedStorageController() {
		super("seed_storage_controller");
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
            player.openGui(AgriCraft.instance, GuiHandler.SEED_CONTROLLER_GUI_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
	public RenderBlock<BlockSeedStorageController> getRenderer() {
		return null;
	}

    @Override
	public boolean isEnabled() {
		return AgriCraftConfig.disableSeedWarehouse;
	}

}
