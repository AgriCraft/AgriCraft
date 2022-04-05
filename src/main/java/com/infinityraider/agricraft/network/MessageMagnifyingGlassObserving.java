package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public abstract class MessageMagnifyingGlassObserving extends MessageBase {
    public static void sendToServer(Player player, boolean status) {
        new ToServer(player, status).sendToServer();
    }

    public static void sendToClient(Player player, boolean status) {
        new ToClient(player, status).sendToAll();
    }

    private Player player;
    private boolean status;

    private MessageMagnifyingGlassObserving() {
        super();
    }

    private MessageMagnifyingGlassObserving(Player player, boolean status) {
        this();
        this.player = player;
        this.status = status;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        ItemMagnifyingGlass.setObserving(this.player, this.status);
    }

    public static class ToServer extends MessageMagnifyingGlassObserving {
        @SuppressWarnings("unused")
        public ToServer() {
            super();
        }

        protected ToServer(Player player, boolean status) {
            super(player, status);
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_SERVER;
        }
    }

    public static class ToClient extends MessageMagnifyingGlassObserving {
        @SuppressWarnings("unused")
        public ToClient() {
            super();
        }

        protected ToClient(Player player, boolean status) {
            super(player, status);
        }

        @Override
        public NetworkDirection getMessageDirection() {
            return NetworkDirection.PLAY_TO_CLIENT;
        }
    }
}
