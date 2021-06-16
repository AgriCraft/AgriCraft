package com.infinityraider.agricraft.plugins.bloodmagic;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.*;
import com.infinityraider.agricraft.reference.Names;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.BLOOD_MAGIC)
public class BloodMagicPlugin implements IAgriPlugin {
    @Override
    public boolean isEnabled() {
        return AgriCraft.instance.getConfig().enableBloodMagicCompat();
    }

    @Override
    public String getId() {
        return Names.Mods.BLOOD_MAGIC;
    }

    @Override
    public String getDescription() {
        return "Blood Magic compatibility";
    }

    @Override
    public void onCommonSetupEvent(FMLCommonSetupEvent event) {
        BloodMagicCompat.execute();
    }
}
