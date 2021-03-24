package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunk;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class IrrigationNetworkHandler {
    private static final IrrigationNetworkHandler INSTANCE = new IrrigationNetworkHandler();

    public static IrrigationNetworkHandler getInstance() {
        return INSTANCE;
    }

    private IrrigationNetworkHandler() {}

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkLoaded(ChunkEvent.Load event) {
        IChunk chunk = event.getChunk();
        if(chunk instanceof Chunk) {
            CapabilityIrrigationNetworkManager.getInstance().onChunkLoaded((Chunk) chunk);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onChunkUnloaded(ChunkEvent.Unload event) {
        IChunk chunk = event.getChunk();
        if(chunk instanceof Chunk) {
            CapabilityIrrigationNetworkManager.getInstance().onChunkUnloaded((Chunk) chunk);
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onNeighbourNotifyEvent(BlockEvent.NeighborNotifyEvent event) {
        TileEntity tile = event.getWorld().getTileEntity(event.getPos());
        if(!this.isIrrigationComponent(tile)) {
            return;
        }
        IAgriIrrigationComponent component = (IAgriIrrigationComponent) tile;
        event.getNotifiedSides().forEach(dir -> {
            TileEntity tileAt = event.getWorld().getTileEntity(event.getPos().offset(dir));
            if(isIrrigationComponent(tileAt)) {
                //TODO: Irrigation network formation / updating logic
                AgriCraft.instance.getLogger().info("Found two adjacent irrigation components");
            }
        });
    }

    protected boolean isIrrigationComponent(TileEntity tile) {
        return CapabilityIrrigationComponent.getInstance().isIrrigationComponent(tile);
    }
}
