package com.infinityraider.agricraft.plugins.immersiveengineering;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.crafting.IInfRecipeSerializer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;

import javax.annotation.Nullable;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.IMMERSIVE_ENGINEERING)
public class ImmersiveEngineeringPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableImmersiveEngineeringCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.IMMERSIVE_ENGINEERING;
    }

    @Override
    public String getDescription() {
        return "Immersive Engineering compatibility";
    }

    @Override
    public void onClientSetupEvent(FMLClientSetupEvent event) {
        ImmersiveEngineeringCompatClient.registerRenderFunction();
    }

    @Override
    public void onServerSetupEvent(FMLDedicatedServerSetupEvent event) {
        ImmersiveEngineeringCompat.registerDummyRenderFunction();
    }

    @Nullable
    public static IInfRecipeSerializer<?> getAgriClocheRecipeSerializer() {
        if (ModList.get().isLoaded(Names.Mods.IMMERSIVE_ENGINEERING) && AgriCraft.instance.getConfig().enableImmersiveEngineeringCompat()) {
            return ImmersiveEngineeringCompat.getAgriClocheRecipeSerializer();
        } else {
            return null;
        }
    }
}
