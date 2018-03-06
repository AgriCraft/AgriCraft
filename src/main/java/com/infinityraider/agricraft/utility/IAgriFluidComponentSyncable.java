/*
 *
 */
package com.infinityraider.agricraft.utility;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Interface marking tiles that can be synced by messages.
 *
 * @author Ryan
 */
public interface IAgriFluidComponentSyncable {
    
    /**
     * Function used internally by message sync fluid level.
     *
     * @param fluidAmount the fluid amount to set to.
     */
    @SideOnly(Side.CLIENT)
    public void setFluidAmount(int fluidAmount);
    
}
