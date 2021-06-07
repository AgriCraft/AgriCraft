package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugModeGreenHouse extends DebugMode {
    @Override
    public String debugName() {
        return "greenhouse";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {

    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {
        if(!world.isRemote()) {
            this.fillVolume(world, player.getPosition());
        }
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {

    }

    protected void fillVolume(World world, BlockPos origin) {
        BlockState original = world.getBlockState(origin);
        BlockPos.Mutable pos = origin.toMutable();
        int minX = this.findEnd(world, original, origin, pos, Direction.WEST);
        int minY = this.findEnd(world, original, origin, pos, Direction.DOWN);
        int minZ = this.findEnd(world, original, origin, pos, Direction.NORTH);
        int maxX = this.findEnd(world, original, origin, pos, Direction.EAST);
        int maxY = this.findEnd(world, original, origin, pos, Direction.UP);
        int maxZ = this.findEnd(world, original, origin, pos, Direction.SOUTH);
        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    pos.setPos(x, y, z);
                    BlockState state = world.getBlockState(pos);
                    if(state.getBlock().isAir(state, world, pos)) {
                        world.setBlockState(pos, AgriCraft.instance.getModBlockRegistry().greenhouse_air.getDefaultState());
                    }
                }
            }
        }
    }

    protected int findEnd(World world, BlockState original, BlockPos origin, BlockPos.Mutable pos, Direction dir) {
        BlockState state = original;
        pos.setPos(origin);
        while (state.getBlock().isAir(state, world, pos) || !state.isSolidSide(world, pos, dir.getOpposite())) {
            pos.setPos(pos.getX() + dir.getXOffset(), pos.getY() + dir.getYOffset(), pos.getZ() + dir.getZOffset());
            state = world.getBlockState(pos);
        }
        return pos.getX() * Math.abs(dir.getXOffset())
                + pos.getY() * Math.abs(dir.getYOffset())
                + pos.getZ() * Math.abs(dir.getZOffset())
                - (dir.getXOffset() + dir.getYOffset() + dir.getZOffset());
    }
}
