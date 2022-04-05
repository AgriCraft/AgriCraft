package com.infinityraider.agricraft;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.content.*;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.impl.v1.PluginHandler;
import com.infinityraider.agricraft.network.*;
import com.infinityraider.agricraft.network.json.*;
import com.infinityraider.agricraft.proxy.ClientProxy;
import com.infinityraider.agricraft.proxy.IProxy;
import com.infinityraider.agricraft.proxy.ServerProxy;
import com.infinityraider.agricraft.reference.Reference;
import com.infinityraider.agricraft.render.models.AgriPlantModelLoader;
import com.infinityraider.agricraft.render.models.AgriSeedBagSeedModelLoader;
import com.infinityraider.agricraft.render.models.AgriSeedModelLoader;
import com.infinityraider.infinitylib.InfinityMod;
import com.infinityraider.infinitylib.network.INetworkWrapper;
import com.infinityraider.infinitylib.render.model.InfModelLoader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod(Reference.MOD_ID)
public class AgriCraft extends InfinityMod<IProxy, Config> {
    public static AgriCraft instance;

    public AgriCraft() {
        super();
    }

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
        PluginHandler.initPlugins();
    }

    @Override
    protected IProxy createClientProxy() {
        return new ClientProxy();
    }

    @Override
    protected IProxy createServerProxy() {
        return new ServerProxy();
    }

    @Override
    public Class<?> getModBlockRegistry() {
        return AgriBlockRegistry.class;
    }

    @Override
    public Class<?> getModItemRegistry() {
        return AgriItemRegistry.class;
    }

    @Override
    public Class<?> getModFluidRegistry() {
        return AgriFluidRegistry.class;
    }

    @Override
    public Class<?> getModTileRegistry() {
        return AgriTileRegistry.class;
    }

    @Override
    public Class<?> getModEnchantmentRegistry() {
        return AgriEnchantmentRegistry.class;
    }

    @Override
    public Class<?> getModRecipeSerializerRegistry() {
        return AgriRecipeSerializerRegistry.class;
    }

    @Override
    public Class<?> getModLootModifierSerializerRegistry() {
        return AgriLootModifierRegistry.class;
    }

    @Override
    public Class<?> getModSoundRegistry() {
        return AgriSoundRegistry.class;
    }

    @Override
    public Class<?> getModParticleRegistry() {
        return AgriParticleRegistry.class;
    }

    @Override
    public Class<?> getStructureRegistry() {
        return AgriStructureRegistry.class;
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        // AgriCraft functional messages
        wrapper.registerMessage(MessageCompareLight.class);
        wrapper.registerMessage(MessageFlipJournalPage.class);
        wrapper.registerMessage(MessageIrrigationNeighbourUpdate.class);
        wrapper.registerMessage(MessageMagnifyingGlassObserving.ToClient.class);
        wrapper.registerMessage(MessageMagnifyingGlassObserving.ToServer.class);
        wrapper.registerMessage(MessageSyncResearchCapability.class);
        wrapper.registerMessage(MessageSyncSeedBagSortMode.class);
        // Json sync messages
        wrapper.registerMessage(MessageNotifySyncComplete.class);
        wrapper.registerMessage(MessageSyncFertilizerJson.class);
        wrapper.registerMessage(MessageSyncMutationJson.class);
        wrapper.registerMessage(MessageSyncPlantJson.class);
        wrapper.registerMessage(MessageSyncSoilJson.class);
        wrapper.registerMessage(MessageSyncWeedJson.class);
    }

    @Override
    public void initializeAPI() {
        // this will load the AgriApi class
        getLogger().info("Intializing API for " + AgriApi.MOD_ID);
        // load jsons
        CoreHandler.loadJsons();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<InfModelLoader<?>> getModModelLoaders() {
        return ImmutableList.of(
                AgriPlantModelLoader.getInstance(),
                AgriSeedBagSeedModelLoader.getInstance(),
                AgriSeedModelLoader.getInstance()
        );
    }
}
