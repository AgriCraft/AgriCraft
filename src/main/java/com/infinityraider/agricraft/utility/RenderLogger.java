package com.infinityraider.agricraft.utility;

import com.infinityraider.agricraft.handler.config.AgriCraftConfigurable;
import com.infinityraider.agricraft.handler.config.ConfigCategory;
import com.infinityraider.agricraft.handler.config.ConfigurationHandler;
import com.infinityraider.agricraft.client.renderers.blocks.RenderChannel;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class RenderLogger {

	@AgriCraftConfigurable(
			category = ConfigCategory.CORE,
			key = "Log Render Calls",
			comment = "Set to true if render calls should be logged."
	)
	public static boolean LOG_RENDER_CALLS = false;
	
	static {
		ConfigurationHandler.addConfigurable(RenderLogger.class);
	}

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
