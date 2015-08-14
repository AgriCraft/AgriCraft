package com.InfinityRaider.AgriCraft.blocks;

import com.InfinityRaider.AgriCraft.items.ItemBlockCustomWood;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockBase;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderBlockGrate;
import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import com.InfinityRaider.AgriCraft.utility.PlayerHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;
import java.util.List;

public class BlockGrate extends BlockCustomWood {
    @Override
    protected String getTileEntityName() {
        return Names.Objects.grate;
    }

    @Override
    public RenderBlockBase getRenderer() {
        return new RenderBlockGrate();
    }

    @Override
    protected String getInternalName() {
        return Names.Objects.grate;
    }

    @Override
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_) {
        return new TileEntityGrate();
    }

    @Override
    protected Class<? extends ItemBlock> getItemBlockClass() {
        return ItemBlockGrate.class;
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float fX, float fY, float fZ) {
        if (world.isRemote) {
            return false;
        }
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return true;
        }
        TileEntityGrate grate = (TileEntityGrate) tile;
        boolean front = grate.isPlayerInFront(player);
        if(player.isSneaking()) {
            if(grate.removeVines(front)) {
                this.dropBlockAsItem(world, x, y, z, new ItemStack(Blocks.vine, 1));
            }
        }
        else if(player.getCurrentEquippedItem()!=null && player.getCurrentEquippedItem().getItem()== Item.getItemFromBlock(Blocks.vine)) {
            if(grate.addVines(front) && !player.capabilities.isCreativeMode) {
                player.getCurrentEquippedItem().stackSize = player.getCurrentEquippedItem().stackSize-1;
            }
        }
        return true;
    }

    @Override
     public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
        ArrayList<ItemStack> items = super.getDrops(world, x, y, z, metadata, fortune);
        if(!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te != null && (te instanceof TileEntityGrate)) {
                TileEntityGrate grate = (TileEntityGrate) te;
                int stackSize = 0;
                stackSize = grate.hasVines(true)?stackSize+1:stackSize;
                stackSize = grate.hasVines(false)?stackSize+1:stackSize;
                if(stackSize>0) {
                    items.add(new ItemStack(Blocks.vine, stackSize));
                }
            }
        }
        return items;
    }

    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        return getBoundingBox(world, x, y, z);
    }

    @SideOnly(Side.CLIENT)
    public AxisAlignedBB getSelectedBoundingBoxFromPool(World world, int x, int y, int z) {
        return getBoundingBox(world, x, y, z);
    }

    public AxisAlignedBB getBoundingBox(World world, int x, int y, int z) {
        TileEntity tile = world.getTileEntity(x, y, z);
        if (tile == null || !(tile instanceof TileEntityGrate)) {
            return super.getCollisionBoundingBoxFromPool(world, x, y, z);
        }
        return ((TileEntityGrate) tile).getBoundingBox();
    }

    public static class ItemBlockGrate extends ItemBlockCustomWood {
        public ItemBlockGrate(Block block) {
            super(block);
        }

        @SideOnly(Side.CLIENT)
        public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
            super.addInformation(stack, player, list, flag);
            list.add(StatCollector.translateToLocal("agricraft_tooltip.grate"));
        }

        @Override
        public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata) {
            if (super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata)) {
                TileEntityGrate grate = (TileEntityGrate) world.getTileEntity(x, y, z);
                if (side == 0 || side == 1) {
                    ForgeDirection dir = PlayerHelper.getPlayerFacing(player);
                    if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
                        grate.setOrientationValue((short) 0);
                        setOffset(grate, hitZ);
                    } else {
                        grate.setOrientationValue((short) 1);
                        setOffset(grate, hitX);
                    }
                } else {
                    grate.setOrientationValue((short) 2);
                    setOffset(grate, hitY);
                }
                return true;
            } else {
                return false;
            }
        }

        private void setOffset(TileEntityGrate grate, float hit) {
            if (hit <= 0.3333F) {
                grate.setOffSet((short) 0);
            } else if (hit <= 0.6666F) {
                grate.setOffSet((short) 1);
            } else {
                grate.setOffSet((short) 2);
            }
        }
    }
}
