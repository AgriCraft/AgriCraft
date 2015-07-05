package com.InfinityRaider.AgriCraft.entity;

import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Iterator;

public class EntityAIClearWeeds extends EntityAIBase {
    private static final int range = 8;
    private EntityVillagerFarmer villager;
    private ArrayList<TileEntityCrop> weedsToClear;
    private TileEntityCrop nextCrop;

    public EntityAIClearWeeds(EntityVillagerFarmer villager) {
        this.villager = villager;
    }

    @Override
    public boolean shouldExecute() {
        return villager!=null && !villager.isDead && !villager.isTrading();
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        return this.shouldExecute() && !this.isTaskFinished();
    }

    public boolean isTaskFinished() {
        return (this.weedsToClear==null || this.weedsToClear.size()==0) && this.nextCrop==null;
    }

    /**
     * Determine if this AI Task is interruptible by a higher (= lower value) priority task.
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
        if(isTaskFinished()) {
            this.resetTask();
        }
        if(nextCrop!=null) {
            double dist = this.getDistanceFromCrop();
            if(dist<0) {
                getNextCrop();
            }
            else if (dist <= 1) {
                nextCrop.clearWeed();
                getNextCrop();
            }
            else if(!villager.getNavigator().tryMoveToXYZ(nextCrop.xCoord+0.5D, nextCrop.yCoord, nextCrop.zCoord+0.5D, 1)) {
                getNextCrop();
            }
        } else {
            getNextCrop();
        }
    }

    private void getNextCrop() {
        Iterator<TileEntityCrop> it = weedsToClear.iterator();
        nextCrop = null;
        while(it.hasNext() && nextCrop==null) {
            nextCrop = it.next();
            it.remove();
            if(!villager.getNavigator().tryMoveToXYZ(nextCrop.xCoord+0.5D, nextCrop.yCoord, nextCrop.zCoord+0.5D, 1)) {
                nextCrop = null;
            }
        }
    }

    private double getDistanceFromCrop() {
        if(this.nextCrop==null) {
            return -1;
        }
        double dx = (villager.posX-(nextCrop.xCoord+0.5D));
        double dy = (villager.posY-nextCrop.yCoord);
        double dz = (villager.posZ-(nextCrop.zCoord+0.5D));
        return  dx*dx + dy*dy + dz*dz;
    }

    private void findWeeds() {
        this.weedsToClear = new ArrayList<TileEntityCrop>();
        for (int dx = -range; dx < range; dx++) {
            for (int dy = -range; dy < range; dy++) {
                for (int dz = -range; dz < range; dz++) {
                    int x = ((int) villager.posX) + dx;
                    int y = ((int) villager.posY) + dy;
                    int z = ((int) villager.posZ) + dz;
                    TileEntity te = villager.worldObj.getTileEntity(x, y, z);
                    if (te != null && te instanceof TileEntityCrop) {
                        TileEntityCrop crop = (TileEntityCrop) te;
                        if (crop.hasWeed()) {
                            weedsToClear.add(crop);
                        }
                    }
                }
            }
        }
    }
}