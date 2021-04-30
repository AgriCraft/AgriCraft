package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoil;
import com.agricraft.agricore.plant.AgriWeed;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.network.json.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.json.MessageSyncPlantJson;
import com.infinityraider.agricraft.network.json.MessageSyncSoilJson;
import com.infinityraider.agricraft.network.json.MessageSyncWeedJson;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Iterator;

public class JsonSyncHandler {
    private static final AgriLogger LOG = AgriCore.getLogger("agricraft-net");
    private static final JsonSyncHandler INSTANCE = new JsonSyncHandler();

    public static JsonSyncHandler getInstance() {
        return INSTANCE;
    }

    private JsonSyncHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event) {
        ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
        // Sync jsons
        syncSoils(player);
        syncPlants(player);
        syncWeeds(player);
        syncMutations(player);
        // Notify magnifying glass tracker
        ItemMagnifyingGlass.setObserving(player, false);
        // Configure JEI
        CapabilityResearchedPlants.getInstance().configureJei(player);
    }

    private void syncSoils(ServerPlayerEntity player) {
        LOG.debug("Sending soils to player: " + player.getDisplayName().getString());
        final int count = AgriCore.getSoils().getAll().size();
        Iterator<AgriSoil> it = AgriCore.getSoils().getAll().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriSoil soil = it.next();
            LOG.debug("Sending Soil: {0} ({1} of {2})", soil, i + 1, count);
            new MessageSyncSoilJson(soil, i, count).sendTo(player);
        }
        LOG.debug("Finished sending soils to player: " + player.getDisplayName().getString());
    }

    private void syncPlants(ServerPlayerEntity player) {
        LOG.debug("Sending plants to player: " + player.getDisplayName().getString());
        final int count = AgriCore.getPlants().getAllElements().size();
        Iterator<AgriPlant> it = AgriCore.getPlants().getAllElements().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriPlant plant = it.next();
            LOG.debug("Sending plant: {0} ({1} of {2})", plant.getId(), i + 1, count);
            new MessageSyncPlantJson(plant, i, count).sendTo(player);
        }
        LOG.debug("Finished sending plants to player: " + player.getDisplayName().getString());
    }

    private void syncWeeds(ServerPlayerEntity player) {
        LOG.debug("Sending weeds to player: " + player.getDisplayName().getString());
        final int count = AgriCore.getWeeds().getAllElements().size();
        Iterator<AgriWeed> it = AgriCore.getWeeds().getAllElements().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriWeed weed = it.next();
            LOG.debug("Sending weed: {0} ({1} of {2})", weed.getId(), i + 1, count);
            new MessageSyncWeedJson(weed, i, count).sendTo(player);
        }
        LOG.debug("Finished sending plants to player: " + player.getDisplayName().getString());
    }

    private void syncMutations(ServerPlayerEntity player) {
        LOG.debug("Sending mutations to player: " + player.getDisplayName().getString());
        final int count = AgriCore.getMutations().getAll().size();
        final Iterator<AgriMutation> it = AgriCore.getMutations().getAll().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriMutation mutation = it.next();
            LOG.debug("Sending mutation: ({0} of {1})", i + 1, count);
            new MessageSyncMutationJson(mutation, i, count).sendTo(player);
        }
        LOG.debug("Finished sending mutations to player: " + player.getDisplayName().getString());
    }

}
