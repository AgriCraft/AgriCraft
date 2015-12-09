package com.InfinityRaider.AgriCraft.compatibility.NEI;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.network.MessageSendNEISetting;
import com.InfinityRaider.AgriCraft.network.NetworkWrapperAgriCraft;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayerMP;

import java.util.HashMap;
import java.util.Map;

public class NEIHelper extends ModHelper {
    private static HashMap<String, Boolean> handlerStatuses = new HashMap<String, Boolean>();

    @Override
    protected String modId() {
        return Names.Mods.nei;
    }

    @Override
    protected void onPostInit() {
        AgriCraft.proxy.initNEI();
    }

    public static void setServerConfigs() {
        if(ModHelper.allowIntegration(Names.Mods.nei)) {
            boolean enableMutationHandler = ConfigurationHandler.config.getBoolean("NEI Mutations", ConfigurationHandler.Categories.CATEGORY_COMPATIBILITY, true, "set to false to disable seed mutations in NEI");
            boolean enableProductHandler = ConfigurationHandler.config.getBoolean("NEI Products", ConfigurationHandler.Categories.CATEGORY_COMPATIBILITY, true, "set to false to disable seed products in NEI");
            handlerStatuses.put("com.InfinityRaider.AgriCraft.compatibility.NEI.NEICropMutationHandler", enableMutationHandler);
            handlerStatuses.put("com.InfinityRaider.AgriCraft.compatibility.NEI.NEICropProductHandler", enableProductHandler);
        }
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public static void setHandlerStatus(String className, boolean status) {
        if(ModHelper.allowIntegration(Names.Mods.nei)) {
            try {
                AgriCraftNEIHandler.setActive(((Class<? extends AgriCraftNEIHandler>) Class.forName(className)), status);
            } catch (ClassNotFoundException e) {
                LogHelper.printStackTrace(e);
            }
        }
    }

    public static void sendSettingsToClient(EntityPlayerMP player) {
        if(ModHelper.allowIntegration(Names.Mods.nei)) {
            for (Map.Entry<String, Boolean> entry : handlerStatuses.entrySet()) {
                NetworkWrapperAgriCraft.wrapper.sendTo(new MessageSendNEISetting(entry.getKey(), entry.getValue()), player);
            }
        }
    }
}
