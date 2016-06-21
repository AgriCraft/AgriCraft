package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.network.NetworkWrapper;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.network.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.MessageSyncPlantJson;
import java.util.List;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class PlayerConnectToServerHandler {

	@SubscribeEvent
	public void onConnect(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		syncPlants(player);
		syncMutations(player);
	}

	private void syncMutations(EntityPlayerMP player) {
		AgriCore.getLogger("Agri-Net").info("Sending mutations to player: " + player.getDisplayNameString());
		List<AgriMutation> mutations = AgriCore.getMutations().getAll();
		for (int i = 0; i < mutations.size(); i++) {
			AgriCore.getLogger("Agri-Net").info("Sending mutation: ({0} of {1})", i + 1, mutations.size());
			NetworkWrapper.getInstance().sendTo(
					new MessageSyncMutationJson(mutations.get(i), i, mutations.size()),
					player
			);
		}
		AgriCore.getLogger("Agri-Net").info("Finished sending mutations to player: " + player.getDisplayNameString());
	}

	private void syncPlants(EntityPlayerMP player) {
		AgriCore.getLogger("Agri-Net").info("Sending plants to player: " + player.getDisplayNameString());
		List<AgriPlant> plants = AgriCore.getPlants().getAll();
		for (int i = 0; i < plants.size(); i++) {
			AgriCore.getLogger("Agri-Net").info("Sending plant: {0} ({1} of {2})", plants.get(i).getName(), i + 1, plants.size());
			NetworkWrapper.getInstance().sendTo(
					new MessageSyncPlantJson(plants.get(i), i, plants.size()),
					player
			);
		}
		AgriCore.getLogger("Agri-Net").info("Finished sending plants to player: " + player.getDisplayNameString());
	}

}
