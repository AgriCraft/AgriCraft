package com.infinityraider.agricraft;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.config.Config;
import com.infinityraider.agricraft.content.*;
import com.infinityraider.agricraft.impl.v1.CoreHandler;
import com.infinityraider.agricraft.network.*;
import com.infinityraider.agricraft.network.json.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.json.MessageSyncPlantJson;
import com.infinityraider.agricraft.network.json.MessageSyncSoilJson;
import com.infinityraider.agricraft.network.json.MessageSyncWeedJson;
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

    @Override
    public String getModId() {
        return Reference.MOD_ID;
    }

    @Override
    protected void onModConstructed() {
        instance = this;
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
    public AgriBlockRegistry getModBlockRegistry() {
        return AgriBlockRegistry.getInstance();
    }

    @Override
    public AgriItemRegistry getModItemRegistry() {
        return AgriItemRegistry.getInstance();
    }

    @Override
    public AgriFluidRegistry getModFluidRegistry() {
        return AgriFluidRegistry.getInstance();
    }

    @Override
    public AgriTileRegistry getModTileRegistry() {
        return AgriTileRegistry.getInstance();
    }

    @Override
    public AgriEnchantmentRegistry getModEnchantmentRegistry() {
        return AgriEnchantmentRegistry.getInstance();
    }

    @Override
    public AgriRecipeSerializerRegistry getModRecipeSerializerRegistry() {
        return AgriRecipeSerializerRegistry.getInstance();
    }

    @Override
    public void registerMessages(INetworkWrapper wrapper) {
        wrapper.registerMessage(MessageCompareLight.class);
        wrapper.registerMessage(MessageIrrigationNeighbourUpdate.class);
        wrapper.registerMessage(MessageMagnifyingGlassObserving.ToClient.class);
        wrapper.registerMessage(MessageMagnifyingGlassObserving.ToServer.class);
        wrapper.registerMessage(MessagePlantResearched.class);
        wrapper.registerMessage(MessageSyncSoilJson.class);
        wrapper.registerMessage(MessageSyncPlantJson.class);
        wrapper.registerMessage(MessageSyncWeedJson.class);
        wrapper.registerMessage(MessageSyncMutationJson.class);
        wrapper.registerMessage(MessageSyncResearchCapability.class);
        wrapper.registerMessage(MessageSyncSeedBagSortMode.class);
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
