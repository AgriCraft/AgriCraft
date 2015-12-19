package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.reference.Reference;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityBase;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.InfinityRaider.AgriCraft.utility.multiblock.IMultiBlockComponent;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * The base class for all AgriCraft container blocks.
 */
public abstract class BlockContainerBase extends BlockBase implements ITileEntityProvider {

    /**
     * The default constructor.
     *
     * @param material the material the block is composed of.
     */
    protected BlockContainerBase(Material material) {
        super(material);
        registerTileEntity();
    }

    /**
     * Attempts to register the TileEntity for this block. If the process fails, an error is printed to the log via {@link LogHelper#printStackTrace(Exception)}.
     */
    private void registerTileEntity() {
        try {
            TileEntity tile = this.createNewTileEntity(null, 0);
            if(tile != null) {
                Class<? extends TileEntity> tileClass = tile.getClass();
                GameRegistry.registerTileEntity(tileClass, wrapName(getTileEntityName()));
            }
        } catch(Exception e) {
            LogHelper.printStackTrace(e);
        }
    }

    /**
     * Retrieves the name of the TileEntity to this container block.
     *
     * @return the name of the block's TileEntity.
     */
    protected abstract String getTileEntityName();

    private static String wrapName(String name) {
        return Reference.MOD_ID + ':' + Names.TileEntity.tileEntity + '_' + name;
    }

    /**
     * Sets the block's orientation based upon the direction the player is looking when the block is placed.
     */
    @Override
    @SuppressWarnings("unchecked")
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase entity, ItemStack stack) {
        TileEntity te = world.getTileEntity(pos);
        if(te!=null && te instanceof TileEntityBase) {
            TileEntityBase tile = (TileEntityBase) world.getTileEntity(pos);
            if(tile.isRotatable()) {
                int direction = MathHelper.floor_double(entity.rotationYaw * 4.0F / 360.0F + 0.5D) & 3;
                switch (direction) {
                    case 0:
                        tile.setOrientation(ForgeDirection.NORTH);
                        break;
                    case 1:
                        tile.setOrientation(ForgeDirection.EAST);
                        break;
                    case 2:
                        tile.setOrientation(ForgeDirection.SOUTH);
                        break;
                    case 3:
                        tile.setOrientation(ForgeDirection.WEST);
                        break;
                }
            }
            if(this.isMultiBlock() && !world.isRemote) {
                IMultiBlockComponent component = (IMultiBlockComponent) world.getTileEntity(pos);
                component.getMultiBlockManager().onBlockPlaced(world, pos, component);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        if(this.isMultiBlock() && !world.isRemote) {
            IMultiBlockComponent component = (IMultiBlockComponent) world.getTileEntity(pos);
            component.getMultiBlockManager().onBlockBroken(world, pos, component);
        }
        super.breakBlock(world, pos, state);
        world.removeTileEntity(pos);
    }

    @Override
    public boolean onBlockEventReceived(World world, BlockPos pos, IBlockState state, int id, int data) {
        super.onBlockEventReceived(world, pos, state, id, data);
        TileEntity tileentity = world.getTileEntity(pos);
        return tileentity!=null && tileentity.receiveClientEvent(id, data);
    }

    public abstract boolean isMultiBlock();
}
