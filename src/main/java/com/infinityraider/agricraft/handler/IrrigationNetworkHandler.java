package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.capability.CapabilityIrrigationNetworkManager;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
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
        CapabilityIrrigationComponent.getInstance().getIrrigationComponent(event.getWorld().getTileEntity(event.getPos())).ifPresent(component ->
            event.getNotifiedSides().forEach(dir -> component.getNode(dir).ifPresent(node ->
                    this.handleDirection(event.getWorld(), event.getPos(), component, node, dir))));
    }

    protected void handleDirection(IWorld world, BlockPos pos, IAgriIrrigationComponent source, IAgriIrrigationNode sourceNode, Direction dir) {
        CapabilityIrrigationComponent.getInstance().getIrrigationComponent(world.getTileEntity(pos.offset(dir))).ifPresent(target ->
                target.getNode(dir.getOpposite()).ifPresent(toNode -> {
                    IAgriIrrigationNetwork fromNetwork = source.getNetwork(dir);
                    IAgriIrrigationNetwork toNetwork = target.getNetwork(dir.getOpposite());
                    // Try form network from first node to second
                    if (this.tryFormNetwork(fromNetwork, sourceNode, target, dir)) {
                        // A network was formed, update target network
                        toNetwork = target.getNetwork(dir.getOpposite());
                    }
                    // Try form network from second node to the first
                    this.tryFormNetwork(toNetwork, toNode, source, dir.getOpposite());
                }));
    }

    protected boolean tryFormNetwork(IAgriIrrigationNetwork network, IAgriIrrigationNode from, IAgriIrrigationComponent target, Direction dir) {
        return network.tryJoinComponent(from, target, dir).isPresent();
    }
}
