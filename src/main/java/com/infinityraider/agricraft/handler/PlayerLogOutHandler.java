package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.impl.v1.CoreHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerLogOutHandler {
    private static final PlayerLogOutHandler INSTANCE = new PlayerLogOutHandler();

    public static PlayerLogOutHandler getInstance() {
        return INSTANCE;
    }

    private PlayerLogOutHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onPlayerLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        // This is also called when an integrated server is being setup
        // We don't care though, as in that case, it is called before the CoreHandler would be initialized
        CoreHandler.onLogout();
    }
}
