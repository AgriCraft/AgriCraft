package k4unl.minecraft.Hydraulicraft.api;

import net.minecraft.item.ItemStack;

/**
 * Use this class to register content to Hydraulicraft. The system uses IMC. Create a class that has the following signature:
 * 
 * package aaa.bbb.ccc;
 * public class HydraulicraftHandler{
 *      public static void init(IHydraulicraftRegistrar registrar){
 *      
 *      }
 * }
 * 
 * You can then tell Hydraulicraft to invoke this method by invoking this line in the preInit phase of your mod:
 * 
 * FMLInterModComms.sendMessage("HydCraft", "aa.bbb.ccc.HydraulicraftHandler", "init");
 * 
 * Hydraulicraft then will invoke this between the preInit and Init phase.
 *
 * Deprecated. Name changed to ITrolleyRegistrar
 */

@Deprecated
public interface IHydraulicraftRegistrar {
    /**
     * Use this method to register your own Harvester trolley.
     * @param toRegister
     */
	public void registerTrolley(IHarvesterTrolley toRegister);
	
	/**
	 * Use this to get the trolley item that's associated with the key. This key is the same as the one returned in {@link IHarvesterTrolley#getName()}.
	 * You can use this to register crafting recipes for example.
	 * @param trolleyName
	 * @return
	 */
    public ItemStack getTrolleyItem(String trolleyName);
}
