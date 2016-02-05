package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.handler.GuiHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderSeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class BlockSeedStorage extends BlockCustomWood {

	public BlockSeedStorage() {
		super(Names.Objects.seedStorage, false);
	}
	
    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntitySeedStorage();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float fX, float fY, float fZ) {
        if (!world.isRemote) {
            player.openGui(AgriCraft.instance, GuiHandler.seedStorageID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int data) {
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity != null && tileentity.receiveClientEvent(id, data);

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
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    protected IProperty[] getPropertyArray() {
        return new IProperty[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderSeedStorage();
    }
    
}
