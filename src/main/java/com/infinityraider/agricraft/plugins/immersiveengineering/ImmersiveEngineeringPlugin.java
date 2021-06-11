package com.infinityraider.agricraft.plugins.immersiveengineering;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

import javax.annotation.Nullable;

@AgriPlugin
@SuppressWarnings("unused")
public class ImmersiveEngineeringPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return ModList.get().isLoaded(this.getId());
    }

    @Override
    public String getId() {
        return Names.Mods.IMMERSIVE_ENGINEERING;
    }

    @Override
    public String getName() {
        return this.getId();
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        if(this.isEnabled() && AgriCraft.instance.getConfig().enableImmersiveEngineeringCompat()) {
            ImmersiveEngineeringCompatClient.registerRenderFunction();
        }
    }

    @Override
    public void onServerSetupEvent(FMLDedicatedServerSetupEvent event) {
        if(this.isEnabled() && AgriCraft.instance.getConfig().enableImmersiveEngineeringCompat()) {
            ImmersiveEngineeringCompat.registerDummyRenderFunction();
        }
    }

    @Nullable
    public static IInfRecipeSerializer getAgriClocheRecipeSerializer() {
        if (ModList.get().isLoaded(Names.Mods.IMMERSIVE_ENGINEERING) && AgriCraft.instance.getConfig().enableImmersiveEngineeringCompat()) {
            return ImmersiveEngineeringCompat.getAgriClocheRecipeSerializer();
        } else {
            return null;
        }
    }
}
