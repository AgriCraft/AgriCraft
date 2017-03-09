package com.infinityraider.agricraft.entity.villager;

import com.infinityraider.agricraft.tiles.TileEntityCrop;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EntityAIClearWeeds extends EntityAIBase {

    @Nonnull
    private static final int CLEAR_RANGE = 8;

    @Nonnull
    private final EntityVillagerFarmer villager;
    @Nonnull
    private List<TileEntityCrop> weedsToClear;
    @Nullable
    private TileEntityCrop nextCrop;

    public EntityAIClearWeeds(EntityVillagerFarmer villager) {
        if (villager == null) {
            throw new NullPointerException(
                    "Attack of the Ghost Farmer! No, seriously, you passed a null entity."
            );
        }
        this.villager = villager;
        this.weedsToClear = new ArrayList<>();
    }

    @Override
    public boolean shouldExecute() {
        return !villager.isDead && !villager.isTrading();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing.
     * 
     * @return If this should continue.
     */
    @Override
    public boolean continueExecuting() {
        return this.shouldExecute() && !this.isTaskFinished();
    }

    public boolean isTaskFinished() {
        return this.weedsToClear.isEmpty() && this.nextCrop == null;
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value)
     * priority task.
     * 
     * @return if this task may be interrupted.
     */
    @Override
    public boolean isInterruptible() {
        return true;
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    @Override
    public void startExecuting() {
        this.findWeeds();
        this.getNextCrop();
    }

    /**
     * Resets the task
     */
    @Override
    public void resetTask() {
        this.startExecuting();
    }

    /**
     * Updates the task
     */
    @Override
    public void updateTask() {
        if (isTaskFinished()) {
            this.resetTask();
        }
        if (nextCrop != null) {
            double dist = this.getDistanceFromCrop();
            if (dist < 0) {
                getNextCrop();
            } else if (dist <= 1) {
                nextCrop.clearWeed();
                getNextCrop();
            } else if (!villager.getNavigator().tryMoveToXYZ(nextCrop.xCoord() + 0.5D, nextCrop.yCoord(), nextCrop.zCoord() + 0.5D, 1)) {
                getNextCrop();
            }
        } else {
            getNextCrop();
        }
    }

    private void getNextCrop() {
        Iterator<TileEntityCrop> it = weedsToClear.iterator();
        nextCrop = null;
        while (it.hasNext() && nextCrop == null) {
            nextCrop = it.next();
            it.remove();
            if (!villager.getNavigator().tryMoveToXYZ(nextCrop.xCoord() + 0.5D, nextCrop.yCoord(), nextCrop.zCoord() + 0.5D, 1)) {
                nextCrop = null;
            }
        }
    }

    private double getDistanceFromCrop() {
        if (this.nextCrop == null) {
            return -1;
        }
        double dx = (villager.posX - (nextCrop.xCoord() + 0.5D));
        double dy = (villager.posY - nextCrop.yCoord());
        double dz = (villager.posZ - (nextCrop.zCoord() + 0.5D));
        return (dx * dx) + (dy * dy) + (dz * dz);
    }

    private void findWeeds() {
        this.weedsToClear.clear();
        for (int dx = -CLEAR_RANGE; dx < CLEAR_RANGE; dx++) {
            for (int dy = -CLEAR_RANGE; dy < CLEAR_RANGE; dy++) {
                for (int dz = -CLEAR_RANGE; dz < CLEAR_RANGE; dz++) {
                    int x = ((int) villager.posX) + dx;
                    int y = ((int) villager.posY) + dy;
                    int z = ((int) villager.posZ) + dz;
                    TileEntity te = villager.worldObj.getTileEntity(new BlockPos(x, y, z));
                    if (te != null && te instanceof TileEntityCrop) {
                        TileEntityCrop crop = (TileEntityCrop) te;
                        if (crop.canWeed()) {
                            weedsToClear.add(crop);
                        }
                    }
                }
            }
        }
    }
}
