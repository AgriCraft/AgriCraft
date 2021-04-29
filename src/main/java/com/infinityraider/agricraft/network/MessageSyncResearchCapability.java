package com.infinityraider.agricraft.network;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityResearchedPlants;
import com.infinityraider.infinitylib.network.MessageBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;

public class MessageSyncResearchCapability extends MessageBase {
    private CompoundNBT tag;

    public MessageSyncResearchCapability() {
        super();
    }

    public MessageSyncResearchCapability(CapabilityResearchedPlants.Impl capability) {
        this();
        this.tag = capability.writeToNBT();
    }

    @Override
    public NetworkDirection getMessageDirection() {
        return NetworkDirection.PLAY_TO_CLIENT;
    }

    @Override
    protected void processMessage(NetworkEvent.Context ctx) {
        PlayerEntity player = AgriCraft.instance.getClientPlayer();
        if (player != null && this.tag != null) {
            CapabilityResearchedPlants.getInstance().getCapability(player).ifPresent(impl -> {
                impl.readFromNBT(this.tag);
                impl.configureJei();
            });
        }
    }
}
