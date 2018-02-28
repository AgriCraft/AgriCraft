package com.infinityraider.agricraft.compat.actuallyadditions;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import de.ellpeck.actuallyadditions.api.ActuallyAdditionsAPI;
import net.minecraftforge.fml.common.Loader;

@AgriPlugin
public class ActuallyAdditionsPlugin implements IAgriPlugin {

    @AgriConfigurable(
            key = "ActuallyAdditions Farmer Energy Cost",
            category = AgriConfigCategory.COMPATIBILITY,
            comment = "The amount of energy that the ActuallyAdditions farmer expends to harvest or plant an AgriCraft crop.",
            min = "250",
            max = "10000"
    )
    public static int ENERGY_COST = 250;

    @Override
    public boolean isEnabled() {
        return Loader.isModLoaded(ActuallyAdditionsAPI.MOD_ID);
    }

    @Override
    public String getId() {
        return "actuallyadditions";
    }

    @Override
    public String getName() {
        return "ActuallyAdditions Integration";
    }

    @Override
    public void initPlugin() {
        ActuallyAdditionsAPI.addFarmerBehavior(new AgriCraftFarmerBehavior());
    }

    static {
        AgriCore.getConfig().addConfigurable(ActuallyAdditionsPlugin.class);
    }

}
