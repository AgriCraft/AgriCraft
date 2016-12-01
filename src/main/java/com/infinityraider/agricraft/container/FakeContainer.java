/*
 */
package com.infinityraider.agricraft.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

/**
 * A fake container for unifying the GUI hierarchy.
 */
public class FakeContainer extends Container {

    @Override
    public boolean canInteractWith(EntityPlayer ep) {
        return true;
    }
    
}
