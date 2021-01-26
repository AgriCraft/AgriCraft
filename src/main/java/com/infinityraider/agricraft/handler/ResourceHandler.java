package com.infinityraider.agricraft.handler;

import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
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
        event.addListener(new AgriJsonReloadListener(event.getDataPackRegistries()));
    }

    public static class AgriJsonReloadListener implements IFutureReloadListener {
        private final DataPackRegistries dataPacks;

        private AgriJsonReloadListener(DataPackRegistries dataPacks) {
            this.dataPacks = dataPacks;
        }

        public DataPackRegistries getDataPacks() {
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
        public CompletableFuture<Void> reload(IStage stage, IResourceManager resourceManager, IProfiler preparationsProfiler,
                                              IProfiler reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
            return CompletableFuture.runAsync(() -> this.readJsons(resourceManager), backgroundExecutor)
                    .thenAccept((v) -> this.uploadTextures(resourceManager))
                    .thenAccept((v) -> this.uploadModels(resourceManager))
                    .thenAccept((v) -> this.finalize(resourceManager));
        }

        protected void readJsons(IResourceManager manager) {

        }

        protected void uploadTextures(IResourceManager manager) {

        }

        protected void uploadModels(IResourceManager manager) {

        }

        public void finalize(IResourceManager manager) {

        }
    }
}
