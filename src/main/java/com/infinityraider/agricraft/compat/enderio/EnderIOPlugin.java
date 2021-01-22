package com.infinityraider.agricraft.compat.enderio;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import crazypants.enderio.api.farm.IFarmerJoe;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@AgriPlugin
public class EnderIOPlugin implements IAgriPlugin {

    @Override
    public boolean isEnabled() {
        return Loader.isModLoaded("enderio");
    }

    @Override
    public String getId() {
        return "enderio";
    }

    @Override
    public String getName() {
        return "EnderIO Integration";
    }

    @Override
    public void initPlugin() {

    }

    @SubscribeEvent
    public void registerFarmer(RegistryEvent.Register<IFarmerJoe> event) {
        AgriFarmerJoe farmer = new AgriFarmerJoe();
        farmer.setRegistryName(AgriCraft.instance.getModId(), "farmer");

        event.getRegistry().register(farmer);
    }

}
