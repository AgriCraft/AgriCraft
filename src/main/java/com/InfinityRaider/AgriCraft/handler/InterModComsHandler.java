package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.api.v1.IAgriCraftPlant;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;

public class InterModComsHandler {

    @Mod.EventHandler
    public void receiveMessage(FMLInterModComms.IMCEvent event) {
        for(FMLInterModComms.IMCMessage message:event.getMessages()) {
            if(message.isItemStackMessage()) {
                try {
                    Class cropPlantClass = Class.forName(message.key);
                    if(IAgriCraftPlant.class.isAssignableFrom(cropPlantClass)) {
                        IAgriCraftPlant agriCraftPlant = null;
                        ItemStack seed = message.getItemStackValue();
                        if(seed==null || seed.getItem()==null) {
                            LogHelper.error("[IMC] AgriCraftPlantDelegate registering errored: ItemStack does not contain an item");
                            continue;
                        }
                        try {
                            agriCraftPlant = (IAgriCraftPlant) cropPlantClass.getConstructor().newInstance(seed);
                        } catch (Exception e) {
                            LogHelper.error("[IMC] AgriCraftPlantDelegate registering errored: "+message.getStringValue()+" does not have a valid constructor, constructor should be public with ItemStack as parameter");
                        }
                        CropPlantHandler.addCropToRegister(agriCraftPlant);
                        LogHelper.error("[IMC] Successfully registered AgriCraftPlantDelegate for "+seed.getUnlocalizedName());
                    } else {
                        LogHelper.error("[IMC] AgriCraftPlantDelegate registering errored: Class "+cropPlantClass.getName()+" does not implement "+IAgriCraftPlant.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    LogHelper.error("[IMC] AgriCraftPlantDelegate registering errored: No class found for "+message.key);
                }
            }
            else if (message.key.equals("renderHooks")) {
                if(FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                    RenderPlayerHooks.onIMCMessage(message);
                }
            }
        }
    }
}
