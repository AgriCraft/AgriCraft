package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.blocks.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderValve;
import com.InfinityRaider.AgriCraft.tileentity.irrigation.TileEntityValve;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.List;


public class BlockChannelValve extends BlockWaterChannel {
    public BlockChannelValve() {
        super();
        this.setBlockBounds(4*Constants.UNIT, 0, 4*Constants.UNIT, 12*Constants.UNIT, 1, 12*Constants.UNIT);
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
        if (!world.isRemote) {
            super.onNeighborBlockChange(world, x, y, z, block);
            updatePowerStatus(world, x, y, z);
            if(block instanceof BlockLever) {
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public void onPostBlockPlaced(World world, int x, int y, int z, int metadata) {
        if (!world.isRemote) {
            updatePowerStatus(world, x, y, z);
        }
    }

    private void updatePowerStatus(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if (te !=null && te instanceof TileEntityValve) {
            TileEntityValve valve = (TileEntityValve) te;
            valve.updatePowerStatus();
        }
    }

    //allows levers to be attached to the block
    @Override
    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
        return side!=ForgeDirection.UP;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityValve();
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int i) {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public RenderBlockBase getRenderer() {
        return new RenderValve();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.valve;
    }

    @Override
    protected String getTileEntityName() {
        return Names.Objects.valve;
    }

    @Override
    public boolean isMultiBlock() {
        return false;
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
        @SuppressWarnings("unchecked")
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.valve"));
        }
    }

}
