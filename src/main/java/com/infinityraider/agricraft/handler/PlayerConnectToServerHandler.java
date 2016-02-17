package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.farming.mutation.MutationHandler;
import com.infinityraider.agricraft.network.MessageSyncMutation;
import com.infinityraider.agricraft.network.NetworkWrapperAgriCraft;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

@SuppressWarnings("unused")
public class PlayerConnectToServerHandler {
    /** Receive mutations from the server when connecting to the server */
    @SubscribeEvent
    public void sendNEIconfig(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.player.worldObj.isRemote) {
            //NEIHelper.sendSettingsToClient((EntityPlayerMP) event.player);
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
