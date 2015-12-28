package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.utility.icon.IconRegisterable;
import com.InfinityRaider.AgriCraft.utility.icon.IconRegistrar;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TextureStitchHandler {
    private static List<IconRegisterable> iconsToRegister;

    public static void addIconRegisterable(IconRegisterable iconRegisterable) {
        if(iconsToRegister == null) {
            iconsToRegister = new ArrayList<>();
        }
        iconsToRegister.add(iconRegisterable);
    }



    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {
        if(iconsToRegister != null) {
            for (IconRegisterable iconRegisterable : iconsToRegister) {
                iconRegisterable.registerIcons(IconRegistrar.getInstance());
            }
        }

    }
}
