package com.infinityraider.agricraft.blocks.storage;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.blocks.BlockCustomWood;
import com.infinityraider.agricraft.config.AgriCraftConfig;
import com.infinityraider.agricraft.handler.GuiHandler;
import com.infinityraider.agricraft.renderers.blocks.RenderSeedStorage;
import com.infinityraider.agricraft.tiles.storage.TileEntitySeedStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockSeedStorage extends BlockCustomWood<TileEntitySeedStorage> {
	public BlockSeedStorage() {
		super("seed_storage", false);
	}
	
    @Override
    public TileEntitySeedStorage createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack stack, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        List<ItemStack> items = super.getDrops(world, pos, state, fortune);
        TileEntity te = world.getTileEntity(pos);
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

        return items;
    }

    //render methods
    //--------------
    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    public AxisAlignedBB getDefaultBoundingBox() {
        return Block.FULL_BLOCK_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderSeedStorage getRenderer() {
        return new RenderSeedStorage(this);
    }
	
	@Override
	public boolean isEnabled() {
		return !AgriCraftConfig.disableSeedStorage;
	}
    
}
