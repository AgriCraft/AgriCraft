package com.infinityraider.agricraft.compat.baubles;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.util.Tuple;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@AgriPlugin
public class BaublesPlugin implements IAgriPlugin {
    
    @AgriConfigurable(
            category = AgriConfigCategory.DEBUG,
            key = "Enable AgriCraft Bauble",
            comment = "Set to true to enable the AgriCraft Bauble."
    )
    public static boolean ENABLE_AGRI_BAUBLE = false;

    @Override
    public boolean isEnabled() {
        return Loader.isModLoaded("baubles");
    }

    @Override
    public String getId() {
        return "baubles";
    }

    @Override
    public String getName() {
        return "Baubles Integration";
    }

    @Override
    public void initPlugin() {
        // Meh
    }

    @SubscribeEvent
    public void registerItems(RegistryEvent.Register<Item> event) {
        if (ENABLE_AGRI_BAUBLE) {
            final ItemAgriBauble agriBauble = new ItemAgriBauble();
            agriBauble.setUnlocalizedName("agricraft:agri_bauble");
            agriBauble.setRegistryName("agricraft:agri_bauble");
            event.getRegistry().register(agriBauble);
            if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                for (Tuple<Integer, ModelResourceLocation> entry : agriBauble.getModelDefinitions()) {
                    ModelLoader.setCustomModelResourceLocation(agriBauble, entry.getFirst(), entry.getSecond());
                }
            }
        }
    }
    
    // Add to Configurator
    static {
        AgriCore.getConfig().addConfigurable(BaublesPlugin.class);
    }

}
