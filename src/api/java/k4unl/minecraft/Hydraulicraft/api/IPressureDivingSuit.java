package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * This interface needs to be implemented by the full diving suit.
 * The code will only check the implementing items.
 * You will have to do the check for a full suit
 * @author Koen Beckers (K-4U)
 */
public interface IPressureDivingSuit {

    /**
     * Return true if this diving suit can help the player survive under water.
     * It's possible to see this function as a "playerIsInWater" event.
     * Note that this function will get called every second.
     * If it returns false, the player will die instantly.
     * @param player The player that wears the armour
     * @param stack  The itemstack of the armour. For NBT purposes.
     * @param pressure The pressure that the player is under
     * @return
     */
    boolean isPressureSafe(EntityPlayer player, ItemStack stack, int pressure);
}
