package com.infinityraider.agricraft.handler;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.agricraft.agricore.plant.AgriSoil;
import com.infinityraider.agricraft.network.json.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.json.MessageSyncPlantJson;
import com.infinityraider.agricraft.network.json.MessageSyncSoilJson;
import java.util.List;
import com.infinityraider.infinitylib.network.NetworkWrapper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
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
		List<AgriSoil> soils = AgriCore.getSoils().getAll();
		for (int i = 0; i < soils.size(); i++) {
			log.info("Sending Soil: {0} ({1} of {2})", soils.get(i).getName(), i + 1, soils.size());
			NetworkWrapper.getInstance().sendTo(
					new MessageSyncSoilJson(soils.get(i), i, soils.size()),
					player
			);
		}
		log.info("Finished sending soils to player: " + player.getDisplayNameString());
	}
    
    private void syncPlants(EntityPlayerMP player) {
		log.info("Sending plants to player: " + player.getDisplayNameString());
		List<AgriPlant> plants = AgriCore.getPlants().getAll();
		for (int i = 0; i < plants.size(); i++) {
			log.info("Sending plant: {0} ({1} of {2})", plants.get(i).getPlantName(), i + 1, plants.size());
			NetworkWrapper.getInstance().sendTo(
					new MessageSyncPlantJson(plants.get(i), i, plants.size()),
					player
			);
		}
		log.info("Finished sending plants to player: " + player.getDisplayNameString());
	}

	private void syncMutations(EntityPlayerMP player) {
		log.info("Sending mutations to player: " + player.getDisplayNameString());
		List<AgriMutation> mutations = AgriCore.getMutations().getAll();
		for (int i = 0; i < mutations.size(); i++) {
			log.info("Sending mutation: ({0} of {1})", i + 1, mutations.size());
			NetworkWrapper.getInstance().sendTo(
					new MessageSyncMutationJson(mutations.get(i), i, mutations.size()),
					player
			);
		}
		log.info("Finished sending mutations to player: " + player.getDisplayNameString());
	}

}
