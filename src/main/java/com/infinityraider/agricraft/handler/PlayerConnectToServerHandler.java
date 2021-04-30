package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class PlayerConnectToServerHandler {
    private static final PlayerConnectToServerHandler INSTANCE = new PlayerConnectToServerHandler();

    public static PlayerConnectToServerHandler getInstance() {
        return INSTANCE;
    }

    private PlayerConnectToServerHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onConnect(PlayerEvent.PlayerLoggedInEvent event) {
        // Notify magnifying glass tracker
        ItemMagnifyingGlass.setObserving(event.getPlayer(), false);
        // Configure JEI
        CapabilityResearchedPlants.getInstance().configureJei(event.getPlayer());
    }
}
