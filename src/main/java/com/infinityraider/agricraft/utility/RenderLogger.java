package com.infinityraider.agricraft.utility;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import com.agricraft.agricore.config.AgriConfigCategory;
import com.agricraft.agricore.config.AgriConfigurable;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.renderers.blocks.RenderChannel;

public class RenderLogger {

    @AgriConfigurable(
            category = AgriConfigCategory.CORE,
            key = "Log Render Calls",
            comment = "Set to true if render calls should be logged."
    )
    public static boolean LOG_RENDER_CALLS = false;

    static {
        AgriCore.getConfig().addConfigurable(RenderLogger.class);
    }

    private long timer = 0;

    @SubscribeEvent
    public void onTick(TickEvent.ServerTickEvent event) {
        if (LOG_RENDER_CALLS) {
            timer++;
            if (timer % 40 == 0) {
                timer = 0;

                int callsPerSecond = RenderChannel.renderCallCounter.getAndSet(0);
                AgriCore.getLogger("AgriRender").debug("Calls since previous output: " + callsPerSecond);
            }
        }
    }

}
