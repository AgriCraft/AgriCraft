package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.ICropPlant;
import com.infinityraider.agricraft.farming.cropplant.CropPlantAPIv1;
import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.infinityraider.agricraft.utility.LogHelper;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;

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
