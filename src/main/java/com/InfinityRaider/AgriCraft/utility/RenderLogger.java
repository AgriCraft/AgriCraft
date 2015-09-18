package com.InfinityRaider.AgriCraft.utility;


import java.util.concurrent.atomic.AtomicInteger;

import com.InfinityRaider.AgriCraft.reference.Constants;
import com.InfinityRaider.AgriCraft.renderers.blocks.RenderChannel;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;

public class RenderLogger {

    private AtomicInteger timer = new AtomicInteger(0);

    @SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (Constants.LOG_RENDER_CALLS) {
			timer.addAndGet(1);
			if (timer.get() % 40 == 0) {
				timer.set(0);
				LogHelper.debug("Calls since previous output: " + RenderChannel.renderCallCounter.getAndSet(0));
			}
		}
	}
}
