package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoil;
import com.infinityraider.agricraft.network.json.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.json.MessageSyncPlantJson;
import com.infinityraider.agricraft.network.json.MessageSyncSoilJson;
import java.util.Iterator;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PlayerConnectToServerHandler {

    private static final AgriLogger log = AgriCore.getLogger("Agri-Net");

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event) {
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        syncSoils(player);
        syncPlants(player);
        syncMutations(player);
    }

    private void syncSoils(EntityPlayerMP player) {
        log.info("Sending soils to player: " + player.getDisplayNameString());
        final int count = AgriCore.getSoils().getAll().size();
        Iterator<AgriSoil> it = AgriCore.getSoils().getAll().iterator();
        for (int i = 0; it.hasNext() ; i++) {
            AgriSoil soil = it.next();
            log.info("Sending Soil: {0} ({1} of {2})", soil, i + 1, count);
            new MessageSyncSoilJson(soil, i, count).sendTo(player);
        }
        log.info("Finished sending soils to player: " + player.getDisplayNameString());
    }

    private void syncPlants(EntityPlayerMP player) {
        log.info("Sending plants to player: " + player.getDisplayNameString());
        final int count = AgriCore.getSoils().getAll().size();
        Iterator<AgriPlant> it = AgriCore.getPlants().getAll().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriPlant plant = it.next();
            log.info("Sending plant: {0} ({1} of {2})", plant.getPlantName(), i + 1, count);
            new MessageSyncPlantJson(plant, i, count).sendTo(player);
        }
        log.info("Finished sending plants to player: " + player.getDisplayNameString());
    }

    private void syncMutations(EntityPlayerMP player) {
        log.info("Sending mutations to player: " + player.getDisplayNameString());
        final int count = AgriCore.getMutations().getAll().size();
        final Iterator<AgriMutation> it = AgriCore.getMutations().getAll().iterator();
        for (int i = 0; it.hasNext(); i++) {
            AgriMutation mutation = it.next();
            log.info("Sending mutation: ({0} of {1})", i + 1, count);
            new MessageSyncMutationJson(mutation, i, count).sendTo(player);
        }
        log.info("Finished sending mutations to player: " + player.getDisplayNameString());
    }

}
