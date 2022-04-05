package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;

public class MessageSyncResearchCapability extends MessageBase {
    private CompoundTag tag;

    public MessageSyncResearchCapability() {
        super();
    }

    public MessageSyncResearchCapability(CapabilityResearchedPlants.Impl capability) {
        this();
        this.tag = capability.serializeNBT();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        Player player = AgriCraft.instance.getClientPlayer();
        if (player != null && this.tag != null) {
            CapabilityResearchedPlants.getInstance().getCapability(player).ifPresent(impl -> {
                impl.deserializeNBT(this.tag);
                impl.configureJei();
            });
        }
    }
}
