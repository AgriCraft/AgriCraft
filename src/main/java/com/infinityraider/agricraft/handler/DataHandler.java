package com.infinityraider.agricraft.handler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataHandler {
    private static final DataHandler INSTANCE = new DataHandler();

    public static DataHandler getInstance() {
        return INSTANCE;
    }

    private DataHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onGatherDataEvent(GatherDataEvent event) {
        boolean debug = true;
    }
}
