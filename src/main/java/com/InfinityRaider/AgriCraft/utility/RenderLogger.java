package com.InfinityRaider.AgriCraft.utility;


import com.InfinityRaider.AgriCraft.renderers.RenderChannel;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderLogger {

    private long timer = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        timer++;
        if (timer % 20 == 0) {
            timer = 0;

            long current = System.currentTimeMillis();
            double seconds = (current - RenderChannel.renderCallTime) / 1000.0;
            double callsPerSecond = RenderChannel.renderCallCounter.get() / seconds;
            LogHelper.info("Calls per Second: " + callsPerSecond);
        }
    }
}
