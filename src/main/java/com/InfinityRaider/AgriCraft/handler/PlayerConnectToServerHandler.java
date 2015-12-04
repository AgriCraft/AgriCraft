package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.NEI.NEIHelper;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.farming.mutation.MutationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSyncMutation;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("unused")
public class PlayerConnectToServerHandler {
    /** Receive mutations from the server when connecting to the server */
    @SubscribeEvent
    public void sendNEIconfig(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.worldObj.isRemote) {
            NEIHelper.sendSettingsToClient((EntityPlayerMP) event.player);
        }
    }

    @SubscribeEvent
    public void syncMutations(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.worldObj.isRemote) {
            if(MinecraftServer.getServer().isDedicatedServer()) {
                //for dedicated server sync to every player
                syncMutations((EntityPlayerMP) event.player);
            } else {
                EntityPlayer connecting = event.player;
                EntityPlayer local = AgriCraft.proxy.getClientPlayer();
                if(local!=null && local!=connecting) {
                    //for local LAN, only sync if the connecting player is not the host, because the host will already have the correct mutations
                    syncMutations((EntityPlayerMP) event.player);
                }
            }
        }
    }

    private void syncMutations(EntityPlayerMP player) {
        LogHelper.info("Sending mutations to player: " + player.getDisplayName());
        Mutation[] mutations = MutationHandler.getMutations();
        for (int i = 0; i < mutations.length; i++) {
            NetworkWrapperAgriCraft.wrapper.sendTo(new MessageSyncMutation(mutations[i], i == mutations.length - 1), player);
        }
    }
}
