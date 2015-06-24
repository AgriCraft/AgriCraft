package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSyncMutation;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class SyncMutationsHandler {
    /** Receive mutations from the server when connecting to the server */
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.worldObj.isRemote) {
            if(MinecraftServer.getServer().isDedicatedServer()) {
                LogHelper.info("Sending mutations to player: " + event.player.getDisplayName());
                Mutation[] mutations = MutationHandler.getMutations();
                for (int i = 0; i < mutations.length; i++) {
                    NetworkWrapperAgriCraft.wrapper.sendTo(new MessageSyncMutation(mutations[i], i == mutations.length - 1), (EntityPlayerMP) event.player);
                }
            }
        }
    }
}
