package com.infinityraider.agricraft.items.blocks;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.tiles.decoration.TileEntityGrate;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

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
    public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag) {
        super.addInformation(stack, world, tooltip, flag);
        tooltip.add(AgriCore.getTranslator().translate("agricraft_tooltip.grate"));
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        // If player is null, thereby throw a fit.
        if (player == null) {
            return false;
        }

        // Otherwise we are good to go.
        if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            // Get the tile representing the grate.
            final TileEntityGrate grate = (TileEntityGrate) world.getTileEntity(pos);

            // Determine axis to use.
            if (side.getAxis().isHorizontal()) {
                setOffsetOrientation(grate, hitX, hitY, hitZ, EnumFacing.Axis.Y);
            } else {
                setOffsetOrientation(grate, hitX, hitY, hitZ, player.getHorizontalFacing().getAxis());
            }

            // Success
            return true;
        }
        
        // Failure
        return false;
    }

    /**
     * Sets the freshly placed TileEntityGrate's orientation.
     *
     * @param grate the grate in question.
     * @param hitX the hit x-coordinate.
     * @param hitY the hit y-coordinate.
     * @param hitZ the hit z-coordinate.
     * @param axis the axis the block is on.
     */
    private static void setOffsetOrientation(TileEntityGrate grate, float hitX, float hitY, float hitZ, EnumFacing.Axis axis) {
        // Set the grate's axis.
        grate.setAxis(axis);

        // Resolve the hit value.
        final float hit = getAxialValue(axis, hitX, hitY, hitZ);

        // Set the grate's offset.
        if (hit <= 0.3333F) {
            grate.setOffset(TileEntityGrate.EnumOffset.NEAR);
        } else if (hit <= 0.6666F) {
            grate.setOffset(TileEntityGrate.EnumOffset.CENTER);
        } else {
            grate.setOffset(TileEntityGrate.EnumOffset.FAR);
        }
    }

    private static float getAxialValue(@Nonnull EnumFacing.Axis axis, float x, float y, float z) {
        switch (axis) {
            case X:
                return x;
            case Y:
                return y;
            case Z:
                return z;
            default:
                throw new NullPointerException();
        }
    }

}
