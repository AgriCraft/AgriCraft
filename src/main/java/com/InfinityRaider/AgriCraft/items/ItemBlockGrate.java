package com.InfinityRaider.AgriCraft.items;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import com.InfinityRaider.AgriCraft.utility.PlayerHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ItemBlockGrate extends ItemBlockCustomWood {
	
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
                if (dir == ForgeDirection.EAST || dir == ForgeDirection.WEST) {
                    grate.setOrientationValue(0);
                    setOffset(grate, hitX);
                } else {
                    grate.setOrientationValue(1);
                    setOffset(grate, hitZ);
                }
            } else {
                grate.setOrientationValue(2);
                setOffset(grate, hitY);
            }
            return true;
        } else {
            return false;
        }
    }

    private void setOffset(TileEntityGrate grate, float hit) {
        if (hit <= 0.3333F) {
            grate.setOffSet(0);
        } else if (hit <= 0.6666F) {
            grate.setOffSet(1);
        } else {
            grate.setOffSet(2);
        }
    }
}
