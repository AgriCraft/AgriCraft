package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.renderers.player.renderhooks.RenderPlayerHooks;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInterModComms;
import net.minecraftforge.fml.relauncher.Side;
import com.infinityraider.agricraft.api.plant.IAgriPlant;

@SuppressWarnings("unused")
public class InterModComsHandler {
    @Mod.EventHandler
    @SuppressWarnings("unchecked")
    public void receiveMessage(FMLInterModComms.IMCEvent event) {
        for(FMLInterModComms.IMCMessage message:event.getMessages()) {
            if(message.isItemStackMessage()) {
                try {
                    Class cropPlantClass = Class.forName(message.key);
                    if(IAgriPlant.class.isAssignableFrom(cropPlantClass)) {
                        IAgriPlant cropPlant = null;
                        ItemStack seed = message.getItemStackValue();
                        if(seed==null || seed.getItem()==null) {
                            AgriCore.getLogger("AgriCraft").error("[IMC] CropPlant registering errored: ItemStack does not contain an item");
                            continue;
                        }
                        try {
                            cropPlant = (IAgriPlant) cropPlantClass.getConstructor().newInstance(seed);
                        } catch (Exception e) {
                            AgriCore.getLogger("AgriCraft").error("[IMC] CropPlant registering errored: "+message.getStringValue()+" does not have a valid constructor, constructor should be public with ItemStack as parameter");
                        }
						// TODO: REPLACE!
                        //CropPlantHandler.addCropToRegister(new CropPlantAPIv1(cropPlant));
                        AgriCore.getLogger("AgriCraft").error("[IMC] Successfully registered CropPlant for "+seed.getUnlocalizedName());
                    } else {
                        AgriCore.getLogger("AgriCraft").error("[IMC] CropPlant registering errored: Class "+cropPlantClass.getName()+" does not implement "+IAgriPlant.class.getName());
                    }
                } catch (ClassNotFoundException e) {
                    AgriCore.getLogger("AgriCraft").error("[IMC] CropPlant registering errored: No class found for "+message.key);
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
