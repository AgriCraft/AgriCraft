package com.infinityraider.agricraft.capability;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationTank;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.capability.IInfSerializableCapabilityImplementation;
import com.infinityraider.infinitylib.utility.ISerializable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

import java.util.Map;
import java.util.function.Supplier;

public class CapabilityMultiBlockData implements IInfSerializableCapabilityImplementation<Chunk, CapabilityMultiBlockData.Impl> {
    private static final CapabilityMultiBlockData INSTANCE = new CapabilityMultiBlockData();

    public static CapabilityMultiBlockData getInstance() {
        return INSTANCE;
    }

    public static ResourceLocation KEY = new ResourceLocation(AgriCraft.instance.getModId().toLowerCase(), Names.Blocks.TANK);

    @CapabilityInject(Impl.class)
    public static final Capability<Impl> CAPABILITY = null;

    public Impl getMultiBlockData(Chunk chunk) {
        return this.getCapability(chunk).orElse(new Empty(chunk));
    }

    public IAgriIrrigationNode getIrrigationNode(TileEntityIrrigationTank tank) {
        BlockPos min = tank.getMultiBlockMin();
        BlockPos max = tank.getMultiBlockMax();
        if(min.equals(max)) {
            return tank;
        }
        TileEntityIrrigationTank origin = tank.getMultiBlockOrigin();
        if(origin.getChunk() == null) {
            return tank;
        }
        return this.getMultiBlockData(origin.getChunk()).getNode(origin.getPos(), origin::createNewMultiBlockNode);
    }

    public void removeMultiBlockNode(World world, BlockPos pos) {
        this.getCapability(world.getChunkAt(pos)).ifPresent(impl -> impl.removeNode(pos));
    }

    @Override
    public Class<Impl> getCapabilityClass() {
        return Impl.class;
    }

    @Override
    public Capability<Impl> getCapability() {
        return CAPABILITY;
    }

    @Override
    public boolean shouldApplyCapability(Chunk carrier) {
        return true;
    }

    @Override
    public Impl createNewValue(Chunk chunk) {
        return new Impl(chunk);
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return KEY;
    }

    @Override
    public Class<Chunk> getCarrierClass() {
        return Chunk.class;
    }

    public static class Impl implements ISerializable {
        private final Chunk chunk;
        private final Map<BlockPos, IAgriIrrigationNode> nodeMap;

        private Impl(Chunk chunk) {
            this.chunk = chunk;
            this.nodeMap = Maps.newHashMap();
        }

        public Chunk getChunk() {
            return this.chunk;
        }

        public World getWorld() {
            return this.getChunk().getWorld();
        }

        public IAgriIrrigationNode getNode(BlockPos pos, Supplier<IAgriIrrigationNode> factory) {
            return this.nodeMap.computeIfAbsent(pos, (aPos) -> factory.get());
        }

        public void removeNode(BlockPos pos) {
            this.nodeMap.remove(pos);
        }

        @Override
        public CompoundNBT writeToNBT() {
            CompoundNBT tag = new CompoundNBT();
            ListNBT tagList = new ListNBT();
            this.nodeMap.forEach((pos, value) -> {
                CompoundNBT entryTag = new CompoundNBT();
                entryTag.putInt(AgriNBT.X1, pos.getX());
                entryTag.putInt(AgriNBT.Y1, pos.getY());
                entryTag.putInt(AgriNBT.Z1, pos.getZ());
                tagList.add(entryTag);
            });
            tag.put(AgriNBT.ENTRIES, tagList);
            return tag;
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {
            this.nodeMap.clear();
            if(tag.contains(AgriNBT.ENTRIES)) {
                tag.getList(AgriNBT.ENTRIES, 10).stream()
                        .filter(entryTag -> entryTag instanceof CompoundNBT)
                        .map(entryTag -> (CompoundNBT) entryTag)
                        .forEach(entryTag -> {
                            if(entryTag.contains(AgriNBT.X1) && entryTag.contains(AgriNBT.Y1) && entryTag.contains(AgriNBT.Z1)) {
                                BlockPos pos = new BlockPos(
                                        entryTag.getInt(AgriNBT.X1),
                                        entryTag.getInt(AgriNBT.Y1),
                                        entryTag.getInt(AgriNBT.Z1)
                                );
                                TileEntity tile = this.getWorld().getTileEntity(pos);
                                if(tile instanceof TileEntityIrrigationTank) {
                                    TileEntityIrrigationTank tank = (TileEntityIrrigationTank) tile;
                                    if(!tank.getMultiBlockMin().equals(tank.getMultiBlockMax())) {
                                        IAgriIrrigationNode node = tank.createNewMultiBlockNode();
                                        this.nodeMap.put(pos, node);
                                    }
                                }
                            }
                        });
            }
        }
    }

    public static class Empty extends Impl {
        private Empty(Chunk chunk) {
            super(chunk);
        }

        @Override
        public IAgriIrrigationNode getNode(BlockPos pos, Supplier<IAgriIrrigationNode> factory) {
            return factory.get();
        }

        @Override
        public CompoundNBT writeToNBT() {
            return new CompoundNBT();
        }

        @Override
        public void readFromNBT(CompoundNBT tag) {}
    }
}
