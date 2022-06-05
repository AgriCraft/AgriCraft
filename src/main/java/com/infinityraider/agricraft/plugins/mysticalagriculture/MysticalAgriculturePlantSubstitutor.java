package com.infinityraider.agricraft.plugins.mysticalagriculture;

import com.infinityraider.agricraft.api.v1.event.AgriRegistryEvent;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class MysticalAgriculturePlantSubstitutor {
    private static final MysticalAgriculturePlantSubstitutor INSTANCE = new MysticalAgriculturePlantSubstitutor();

    public static MysticalAgriculturePlantSubstitutor getInstance() {
        return INSTANCE;
    }

    private MysticalAgriculturePlantSubstitutor() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlantRegistered(AgriRegistryEvent.Register.Plant event) {
        IAgriPlant current = event.getSubstitute();
        if(MysticalAgricultureCompatClient.getCropFromPlantId(current.getId()) != null) {
            event.setSubstitute(new MysticalAgriculturePlantOverride(current));
        }
    }
}
