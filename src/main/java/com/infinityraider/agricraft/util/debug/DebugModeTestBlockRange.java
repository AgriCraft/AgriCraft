package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.api.v1.util.BlockRange;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Hand;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import java.util.Optional;
import javax.annotation.Nonnull;

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
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        if (context.getWorld().isRemote) {
            return;
        }
        BlockPos pos = context.getPos();
        PlayerEntity player = context.getPlayer();
        World world = context.getWorld();
        Optional<BlockPos> startPos = getStartPos(stack);
        if (!startPos.isPresent()) {
            // This is the first click. Save 'pos' as the starting coordinate.
            setStartPos(stack, context.getPos());
            player.sendMessage(new StringTextComponent("Starting corner set: (" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + ")"), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent("Next right click will set the opposite/ending corner."), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent("WARNING: this mode will destroy blocks, be careful."), Util.DUMMY_UUID);
        } else {
            // This is the second click. Load the starting coordinate. Use 'pos' as the ending coordinate. Then fill the cuboid with wool.
            int count = 0;
            BlockState wool = Blocks.BLACK_WOOL.getDefaultState();
            //
            // IMPORTANT PART OF THE TEST IS BELOW
            //
            BlockRange range = new BlockRange(startPos.get(), pos);
            for (BlockPos target : range) {                         // <-- Is the iterator giving a complete set?
                BlockState old = world.getBlockState(target);
                world.destroyBlock(target, true);
                world.setBlockState(target, wool);
                world.notifyBlockUpdate(target, old, wool, 2);
                count += 1;
            }
            //
            // IMPORTANT PART OF THE TEST IS ABOVE
            //
            player.sendMessage(new StringTextComponent("Volume:     " + range.getVolume()), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent("Replaced:  " + count), Util.DUMMY_UUID);
            player.sendMessage(new StringTextComponent("Coverage: " + (range.getVolume() == count ? "Complete" : "INCOMPLETE")), Util.DUMMY_UUID);
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
        CompoundNBT tag;
        if (!stack.hasTag()) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        } else {
            tag = stack.getTag();
        }
        Optional<BlockPos> start = Optional.empty();
        assert tag != null;
        if (tag.contains(NBT_START)) {
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
        CompoundNBT tag;
        if (!stack.hasTag()) {
            tag = new CompoundNBT();
            stack.setTag(tag);
        } else {
            tag = stack.getTag();
        }
        assert tag != null;
        if (p == null) {
            tag.remove(NBT_START);
        } else {
            int[] start = new int[3];
            start[0] = p.getX();
            start[1] = p.getY();
            start[2] = p.getZ();
            tag.putIntArray(NBT_START, start);
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        // NOOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
        // NOOP
    }

}
