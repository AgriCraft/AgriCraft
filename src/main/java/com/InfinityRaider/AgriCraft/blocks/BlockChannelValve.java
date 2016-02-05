package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderValve;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;


public class BlockChannelValve extends AbstractBlockWaterChannel {
	
    public BlockChannelValve() {
        super(Names.Objects.valve);
        this.setBlockBounds(4*Constants.UNIT, 0, 4*Constants.UNIT, 12*Constants.UNIT, 1, 12*Constants.UNIT);
    }

    @Override
    public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block block) {
        if (!world.isRemote) {
            super.onNeighborBlockChange(world, pos, state, block);
            updatePowerStatus(world, pos);
            if(block instanceof BlockLever) {
                world.markBlockForUpdate(pos);
            }
        }
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (!world.isRemote) {
            updatePowerStatus(world, pos);
        }
    }

    private void updatePowerStatus(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te !=null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            valve.updatePowerStatus();
        }
    }

    //allows levers to be attached to the block
    @Override
    public boolean isSideSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side!=EnumFacing.UP;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityValve();
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderValve();
    }

    @Override
    protected Class<? extends ItemBlockCustomWood> getItemBlockClass() {
    	return ItemBlockValve.class;
    }
    
    public static class ItemBlockValve extends ItemBlockCustomWood {
        public ItemBlockValve(Block block) {
            super(block);
        }

        @Override
        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.valve"));
        }
    }

}
