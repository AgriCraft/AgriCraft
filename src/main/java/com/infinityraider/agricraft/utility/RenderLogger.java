package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.renderers.blocks.RenderChannel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderLogger {

	public static final boolean LOG_RENDER_CALLS = false;

	private long timer = 0;

	@SubscribeEvent
	public void onTick(TickEvent.ServerTickEvent event) {
		if (LOG_RENDER_CALLS) {
			timer++;
			if (timer % 40 == 0) {
				timer = 0;

				int callsPerSecond = RenderChannel.renderCallCounter.getAndSet(0);
				LogHelper.debug("Calls since previous output: " + callsPerSecond);
			}
		}
	}

}
