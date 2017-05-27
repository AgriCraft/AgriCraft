package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.apiimpl.PluginHandler;
import com.infinityraider.agricraft.core.CoreHandler;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class TextureStitchHandler {

    private static final TextureStitchHandler INSTANCE = new TextureStitchHandler();

    public static TextureStitchHandler getInstance() {
        return INSTANCE;
    }

    private TextureStitchHandler() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    @SuppressWarnings("unused")
    public void onTextureStitch(TextureStitchEvent e) {
        CoreHandler.loadTextures(e.getMap()::registerSprite);
        PluginHandler.loadTextures(e.getMap()::registerSprite);
    }
}
