package com.InfinityRaider.AgriCraft.compatibility.NEI;

import com.InfinityRaider.AgriCraft.AgriCraft;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import net.minecraft.entity.player.EntityPlayerMP;

public class NEIHelper extends ModHelper {
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
            boolean enableMutationHandler = ConfigurationHandler.config.getBoolean("NEI Mutations", ConfigurationHandler.CATEGORY_COMPATIBILITY, true, "set to false to disable seed mutations in NEI");
            boolean enableProductHandler = ConfigurationHandler.config.getBoolean("NEI Products", ConfigurationHandler.CATEGORY_COMPATIBILITY, true, "set to false to disable seed products in NEI");
            setHandlerStatus(NEICropMutationHandler.class, enableMutationHandler);
            setHandlerStatus(NEICropProductHandler.class, enableProductHandler);
        }
    }

    public static void setHandlerStatus(Class<? extends AgriCraftNEIHandler> handlerClass, boolean status) {
        if(ModHelper.allowIntegration(Names.Mods.nei)) {
            AgriCraftNEIHandler.setActive(handlerClass, status);
        }
    }

    @SuppressWarnings("unchecked")
    public static void setHandlerStatus(String className, boolean status) {
        try {
            setHandlerStatus(((Class<? extends AgriCraftNEIHandler>) Class.forName(className)), status);
        } catch (ClassNotFoundException e) {
            LogHelper.printStackTrace(e);
        }
    }

    public static void sendSettingsToClient(EntityPlayerMP player) {
        if(ModHelper.allowIntegration(Names.Mods.nei)) {
            AgriCraftNEIHandler.sendSettingsToClient(player);
        }
    }
}
