package com.InfinityRaider.AgriCraft.utility;


import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderChannel;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderLogger {

    private long timer = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (!Constants.LOG_RENDER_CALLS)
            return;

        timer++;
        if (timer % 40 == 0) {
            timer = 0;

            int callsPerSecond = RenderChannel.renderCallCounter.getAndSet(0);
            LogHelper.debug("Calls since previous output: " + callsPerSecond);
        }
    }
}
