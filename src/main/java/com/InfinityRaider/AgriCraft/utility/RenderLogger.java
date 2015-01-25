package com.InfinityRaider.AgriCraft.utility;


import com.InfinityRaider.AgriCraft.renderers.RenderChannel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderLogger {

    private long timer = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        timer++;
        if (timer % 40 == 0) {
            timer = 0;

            int callsPerSecond = RenderChannel.renderCallCounter.getAndSet(0);
            LogHelper.info("Calls since previous output: " + callsPerSecond);
        }
    }
}
