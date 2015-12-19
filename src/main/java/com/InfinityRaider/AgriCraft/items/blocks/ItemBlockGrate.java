package com.InfinityRaider.AgriCraft.items.blocks;

import com.InfinityRaider.AgriCraft.tileentity.decoration.TileEntityGrate;
import com.InfinityRaider.AgriCraft.utility.ForgeDirection;
import com.InfinityRaider.AgriCraft.utility.PlayerHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * The item form of the Grate block.
 */
public class ItemBlockGrate extends ItemBlockCustomWood {
	
    /**
     * The default constructor for the Grate Item.
     * 
     * @param block the block that the item is associated with.
     */
    public ItemBlockGrate(Block block) {
        super(block);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean flag) {
        super.addInformation(stack, player, list, flag);
        list.add(StatCollector.translateToLocal("agricraft_tooltip.grate"));
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            TileEntityGrate grate = (TileEntityGrate) world.getTileEntity(pos);
            if (side == EnumFacing.UP || side == EnumFacing.DOWN) {
                ForgeDirection dir = PlayerHelper.getPlayerFacing(player);
                if (dir == ForgeDirection.NORTH || dir == ForgeDirection.SOUTH) {
                    setOffsetAndOrientation(grate, hitZ, (short) 0);
                } else {
                    setOffsetAndOrientation(grate, hitX, (short) 1);
                }
            } else {
                setOffsetAndOrientation(grate, hitY, (short) 2);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sets the freshly placed TileEntityGrate's orientation.
     * 
     * @param grate the grate in question.
     * @param hit the hit location, on the axis matching the orientation.
     * @param orientation the orientation.
     */
    private static void setOffsetAndOrientation(TileEntityGrate grate, float hit, short orientation) {
        grate.setOrientationValue(orientation);
        if (hit <= 0.3333F) {
            grate.setOffSet((short) 0);
        } else if (hit <= 0.6666F) {
            grate.setOffSet((short) 1);
        } else {
            grate.setOffSet((short) 2);
        }
        grate.calculateBounds();
    }
}
