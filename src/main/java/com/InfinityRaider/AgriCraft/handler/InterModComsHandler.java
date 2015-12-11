package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.api.v2.ICropPlant;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlantAPIv1;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.item.ItemStack;

@SuppressWarnings("unused")
public class InterModComsHandler {
    @Mod.EventHandler
    @SuppressWarnings("unchecked")
    public void receiveMessage(FMLInterModComms.IMCEvent event) {
        for(FMLInterModComms.IMCMessage message:event.getMessages()) {
            if(message.isItemStackMessage()) {
                try {
                    Class cropPlantClass = Class.forName(message.key);
                    if(ICropPlant.class.isAssignableFrom(cropPlantClass)) {
                        ICropPlant cropPlant = null;
                        ItemStack seed = message.getItemStackValue();
                        if(seed==null || seed.getItem()==null) {
                            LogHelper.error("[IMC] CropPlant registering errored: ItemStack does not contain an item");
                            continue;
                        }
                        try {
                            cropPlant = (ICropPlant) cropPlantClass.getConstructor().newInstance(seed);
                        } catch (Exception e) {
                            LogHelper.error("[IMC] CropPlant registering errored: "+message.getStringValue()+" does not have a valid constructor, constructor should be public with ItemStack as parameter");
                        }
                        CropPlantHandler.addCropToRegister(new CropPlantAPIv1(cropPlant));
                        LogHelper.error("[IMC] Successfully registered CropPlant for "+seed.getUnlocalizedName());
                    } else {
                        LogHelper.error("[IMC] CropPlant registering errored: Class "+cropPlantClass.getName()+" does not implement "+ICropPlant.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    LogHelper.error("[IMC] CropPlant registering errored: No class found for "+message.key);
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
