/*
 */
package com.infinityraider.agricraft.items.modes;

import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.BlockColored;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import java.util.Optional;
import javax.annotation.Nonnull;

/**
 *
 *
 */
public class DebugModeTestBlockRange extends DebugMode {

    @Override
    public String debugName() {
        return "test BlockRange";
    }

    /**
     * This method allows the user to test what Block Positions are covered by the BlockRange
     * iterator. The expected result will be the full cuboid between opposing corner positions. Also
     * remember that the BlockRange min and max positions aren't necessarily the input positions.
     *
     * Usage: Right-click on two blocks to specify the opposite corners. Some blocks should get
     * replaced with free wool. Be careful. In case of terrible bugs, might destroy or overwrite
     * unexpected locations!
     */
    @Override
    public void debugActionBlockClicked(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return;
        }
        Optional<BlockPos> startPos = getStartPos(stack);
        if (!startPos.isPresent()) {
            // This is the first click. Save 'pos' as the starting coordinate.
            setStartPos(stack, pos);
            player.sendMessage(new TextComponentString("Starting corner set: (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")"));
            player.sendMessage(new TextComponentString("Next right click will set the opposite/ending corner."));
            player.sendMessage(new TextComponentString("WARNING: this mode will destroy blocks, be careful."));
        } else {
            // This is the second click. Load the starting coordinate. Use 'pos' as the ending coordinate. Then fill the cuboid with wool.
            int count = 0;
            IBlockState wool = Blocks.WOOL.getDefaultState().withProperty(BlockColored.COLOR, EnumDyeColor.BLACK);
            //
            // IMPORTANT PART OF THE TEST IS BELOW
            //
            BlockRange range = new BlockRange(startPos.get(), pos);
            for (BlockPos target : range) {                         // <-- Is the iterator giving a complete set?
                IBlockState old = world.getBlockState(target);
                world.destroyBlock(target, true);
                world.setBlockState(target, wool);
                world.notifyBlockUpdate(target, old, wool, 2);
                count += 1;
            }
            //
            // IMPORTANT PART OF THE TEST IS ABOVE
            //
            player.sendMessage(new TextComponentString("Volume:     " + range.getVolume()));
            player.sendMessage(new TextComponentString("Replaced:  " + count));
            player.sendMessage(new TextComponentString("Coverage: " + (range.getVolume() == count ? "Complete" : "INCOMPLETE")));
            setStartPos(stack, null);
        }
    }

    /**
     * Utility get and set methods to save the BlockPos between clicks.
     */
    private static final String NBT_START = "agri_debug_blockrange_startpos";

    /**
     * @param stack Which ItemStack to read from. Typically is the debugger.
     * @return An Optional of BlockPos if there is a length 3 int array with the right key. Empty
     * otherwise.
     */
    private Optional<BlockPos> getStartPos(@Nonnull ItemStack stack) {
        NBTTagCompound tag;
        if (!stack.hasTagCompound()) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        } else {
            tag = stack.getTagCompound();
        }
        Optional<BlockPos> start = Optional.empty();
        assert tag != null;
        if (tag.hasKey(NBT_START)) {
            int[] raw = (tag.getIntArray(NBT_START));
            if (raw.length == 3) {
                BlockPos p = new BlockPos(raw[0], raw[1], raw[2]);
                start = Optional.of(p);
            }
        }
        return start;
    }

    /**
     * @param stack Where to save the BlockPos. Typically is the debugger.
     * @param p The position to save, or null if you want to delete the tag.
     */
    private void setStartPos(@Nonnull ItemStack stack, BlockPos p) {
        NBTTagCompound tag;
        if (!stack.hasTagCompound()) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        } else {
            tag = stack.getTagCompound();
        }
        assert tag != null;
        if (p == null) {
            tag.removeTag(NBT_START);
        } else {
            int[] start = new int[3];
            start[0] = p.getX();
            start[1] = p.getY();
            start[2] = p.getZ();
            tag.setIntArray(NBT_START, start);
        }
    }

    /**
     * Interface method stubs.
     */
    @Override
    public void debugActionClicked(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, EntityPlayer player, EntityLivingBase target, EnumHand hand) {
        // NOP
    }

}
