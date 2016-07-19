package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.network.NetworkWrapper;
import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.log.AgriLogger;
import com.agricraft.agricore.plant.AgriMutation;
import com.agricraft.agricore.plant.AgriPlant;
import com.infinityraider.agricraft.network.json.MessageSyncMutationJson;
import com.infinityraider.agricraft.network.json.MessageSyncPlantJson;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class PlayerConnectToServerHandler {
	
	private static final AgriLogger log = AgriCore.getLogger("Agri-Net");

	@SubscribeEvent
	public void onConnect(PlayerEvent.PlayerLoggedInEvent event) {
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		syncPlants(player);
		syncMutations(player);
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

}
