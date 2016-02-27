package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.item.ItemStack;

/**
 * @author Koen Beckers (K-4U)
 */
public interface ITrolleyRegistrar {
    /**
     * Use this method to register your own Harvester trolley.
     * @param toRegister
     */
    void registerTrolley(IHarvesterTrolley toRegister);

    /**
     * Use this to get the trolley item that's associated with the key. This key is the same as the one returned in {@link IHarvesterTrolley#getName()}.
     * You can use this to register crafting recipes for example.
     * @param trolleyName
     * @return
     */
    ItemStack getTrolleyItem(String trolleyName);
}
