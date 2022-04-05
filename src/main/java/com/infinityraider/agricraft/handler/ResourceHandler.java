package com.infinityraider.agricraft.handler;

import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class ResourceHandler {
    private static final ResourceHandler INSTANCE = new ResourceHandler();

    public static ResourceHandler getInstance() {
        return INSTANCE;
    }

    private ResourceHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onReloadListenerRegistration(AddReloadListenerEvent event) {
        event.addListener(new AgriJsonReloadListener(event.getServerResources()));
    }

    public static class AgriJsonReloadListener implements PreparableReloadListener {
        private final ReloadableServerResources dataPacks;

        private AgriJsonReloadListener(ReloadableServerResources dataPacks) {
            this.dataPacks = dataPacks;
        }

        public ReloadableServerResources getDataPacks() {
            return this.dataPacks;
        }
        // TODO: multi-thread magic which:
        //  1) reads jsons in time (on the server)
        //  2) uploads the required textures on the correct SpriteUploader in due time (on the client)
        //  3) registers the required models on the ModelManager in due time (on the client)
        //  4) Waits for the NetworkTagManager to finish processing the tags to then finish loading the jsons (on the server)
        @Override
        @Nonnull
        @ParametersAreNonnullByDefault
        public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resourceManager, ProfilerFiller prepProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> this.readJsons(resourceManager), backgroundExecutor)
                    .thenAccept((v) -> this.uploadTextures(resourceManager))
                    .thenAccept((v) -> this.uploadModels(resourceManager))
                    .thenAccept((v) -> this.finalize(resourceManager));
        }

        protected void readJsons(ResourceManager manager) {

        }

        protected void uploadTextures(ResourceManager manager) {

        }

        protected void uploadModels(ResourceManager manager) {

        }

        public void finalize(ResourceManager manager) {

        }
    }
}
