package com.infinityraider.agricraft.impl.v1.irrigation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.infinityraider.agricraft.reference.AgriNBT;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * An irrigation layer section represents a set of volumes, which are part of irrigation components,
 * that are interconnected and exist between the same y-levels.
 *
 * Note that an irrigation component can contain multiple layers.
 *
 * Irrigation layer sections are dumb, and are only aware of themselves, they will accept and provide water on demand.
 * They can be expanded with new positions, or reduced by removing existing positions.
 * On removing an existing positions, it will verify if its parts are still interconnected,
 * and if not, it will split itself in multiple layers.
 */
public class IrrigationLayerSection {
    private double min;
    private double max;
    private int capacity;

    // contents
    private int content;
    private double height;

    // connectivity
    private final Map<Pos, Set<Pos>> connections;

    public IrrigationLayerSection(BlockPos pos, double min, double max, int capacity) {
        this(pos.getX(), pos.getZ(), min, max, capacity);
    }

    public IrrigationLayerSection(int x, int z, double min, double max, int capacity) {
        this.min = min;
        this.max = max;
        this.capacity = capacity;

        this.content = 0;
        this.height = this.getMin();

        this.connections = Maps.newHashMap();
        this.connections.put(new Pos(x, z, capacity), Sets.newHashSet());
    }

    protected IrrigationLayerSection(Map<Pos, Set<Pos>> connections, double min, double max) {
        this.min = min;
        this.max = max;
        this.capacity = connections.keySet().stream().mapToInt(Pos::getCapacity).sum();
        this.connections = connections;
    }

    public double getMin() {
        return this.min;
    }

    public double getMax() {
        return this.max;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public int getContent() {
        return this.content;
    }

    public double getHeight() {
        return this.height;
    }

    public void expand(BlockPos from, BlockPos to, int capacity, int content) {
        // Update capacity and contents
        this.capacity += capacity;
        this.setContent(this.getContent() + content);
        // Update connectivity
        Pos fromPos = this.connections.keySet().stream().filter(pos -> pos.matches(from)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("The given position is not part of this layer"));
        Pos toPos = new Pos(to, capacity);
        this.connections.get(fromPos).add(toPos);
        this.connections.computeIfAbsent(toPos, pos -> Sets.newHashSet()).add(fromPos);
    }

    public Optional<Set<IrrigationLayerSection>> reduce(BlockPos removed, int capacity) {
        // update connectivity
        Pos removedPos = this.connections.keySet().stream().filter(pos -> pos.matches(removed)).findAny()
                .orElseThrow(() -> new IllegalArgumentException("The given position is not part of this layer"));
        this.connections.remove(removedPos).forEach(pos -> this.connections.get(pos).remove(removedPos));
        this.capacity -= capacity;
        // check connectivity
        final double height = this.getHeight();
        Set<Map<Pos, Set<Pos>>> networks = new ConnectivityChecker(this.connections).check();
        if(networks.size() <= 1) {
            this.setHeight(height);
            return Optional.empty();
        } else {
            return Optional.of(networks.stream()
                    .map(connections -> new IrrigationLayerSection(connections, this.getMin(), this.getMax()))
                    .collect(Collectors.toSet())
            );
        }
    }

    protected void setContent(int content) {
        this.content = Math.max(0, Math.min(this.getCapacity(), content));
        this.height = this.calculateHeight(this.getContent());
    }

    protected void setHeight(double height) {
        this.height = Math.max(this.getMin(), Math.min(this.getMax(), height));
        this.content = this.calculateContents(this.getHeight());
    }

    public int fill(int maxFill, IFluidHandler.FluidAction action) {
        int fill = Math.min(this.getCapacity() - this.getContent(), Math.max(0, maxFill));
        if(action.execute()) {
            this.setContent(this.getContent() + fill);
        }
        return fill;
    }

    public int drain(int maxDrain, IFluidHandler.FluidAction action) {
        int drain = Math.min(this.getContent(), Math.max(0, maxDrain));
        if(action.execute()) {
            this.setContent(this.getContent() - drain);
        }
        return drain;
    }

    protected int calculateContents(double height) {
        if(height <= this.getMin()) {
            return 0;
        }
        if(height >= this.getMax()) {
            return this.getCapacity();
        }
        return (int) (this.getCapacity() * (height - this.getMin()) / (this.getMax() - this.getMin()));
    }

    protected double calculateHeight(int content) {
        if(content <= 0) {
            return this.getMin();
        }
        if(content >= this.getCapacity()) {
            return this.getMax();
        }
        double f = (content + 0.0D)/this.getCapacity();
        return MathHelper.lerp(f, this.getMin(), this.getMax());
    }

    public CompoundNBT writeToTag() {
        // create new tag
        CompoundNBT tag = new CompoundNBT();
        // capacity and contents
        tag.putDouble(AgriNBT.Y1, this.getMin());
        tag.putDouble(AgriNBT.Y2, this.getMax());
        tag.putInt(AgriNBT.CAPACITY, this.getCapacity());
        tag.putDouble(AgriNBT.LEVEL, this.getHeight());
        // connectivity
        ListNBT connectionTags = new ListNBT();
        this.connections.forEach((pos, connections) -> {
            ListNBT subTags = new ListNBT();
            connections.forEach(subPos -> subTags.add(subPos.writeToNBT(new CompoundNBT())));
            connectionTags.add(pos.writeToNBT(new CompoundNBT()).put(AgriNBT.CONNECTIONS, subTags));
        });
        tag.put(AgriNBT.ENTRIES, connectionTags);
        // return the tag
        return tag;
    }

    public void readFromTag(CompoundNBT tag) {
        // capacity and contents
        this.min = tag.getDouble(AgriNBT.Y1);
        this.max = tag.getDouble(AgriNBT.Y2);
        this.capacity = tag.getInt(AgriNBT.CAPACITY);
        this.height = tag.getDouble(AgriNBT.LEVEL);
        // connectivity
        this.connections.clear();
        tag.getList(AgriNBT.ENTRIES, 10).forEach(connectionTag ->
                this.connections.put(
                        new Pos(connectionTag),
                        ((CompoundNBT) connectionTag).getList(AgriNBT.CONNECTIONS, 10).stream().map(Pos::new).collect(Collectors.toSet())
                ));
    }

    public static class Pos {
        private final int x;
        private final int z;
        private final int capacity;

        public Pos(BlockPos pos, int capacity) {
            this(pos.getX(), pos.getZ(), capacity);
        }
        public Pos(INBT tag) {
            this((CompoundNBT) tag);
        }

        public Pos(CompoundNBT tag) {
            this(tag.getInt(AgriNBT.X1), tag.getInt(AgriNBT.Z1), tag.getInt(AgriNBT.CAPACITY));
        }

        public Pos(int x, int z, int capacity) {
            this.x = x;
            this.z = z;
            this.capacity = capacity;
        }

        public int getX() {
            return this.x;
        }

        public int getZ() {
            return this.z;
        }

        public int getCapacity() {
            return this.capacity;
        }

        public boolean matches(BlockPos pos) {
            return this.getX() == pos.getX() && this.getZ() == pos.getZ();
        }

        public CompoundNBT writeToNBT(CompoundNBT tag) {
            tag.putInt(AgriNBT.X1, this.getX());
            tag.putInt(AgriNBT.Z1, this.getZ());
            tag.putInt(AgriNBT.CAPACITY, this.getCapacity());
            return tag;
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) {
                return true;
            }
            if(obj instanceof Pos) {
                Pos other = (Pos) obj;
                return this.getX() == other.getX()
                        && this.getZ() == other.getZ()
                        && this.getCapacity() == other.getCapacity();
            }
            return false;
        }

        @Override
        public String toString() {
            return "(" + this.getX() + ", " + this.getZ() + ")";
        }
    }

    protected static class ConnectivityChecker {
        private final Map<Pos, Set<Pos>> original;
        private final Set<Map<Pos, Set<Pos>>> connectivity;

        protected ConnectivityChecker(Map<Pos, Set<Pos>> original) {
            this.original = original;
            this.connectivity = Sets.newIdentityHashSet();
        }

        public Set<Map<Pos, Set<Pos>>> check() {
            if(this.connectivity.isEmpty()) {
                this.runCheck();
            }
            return this.connectivity;
        }

        protected void runCheck() {
            // Copy all original connections
            Map<Pos, Set<Pos>> toVisit = Maps.newHashMap();
            this.original.forEach((pos, connections) -> toVisit.put(pos, Sets.newHashSet(connections)));
            // Iterate over the positions
            this.original.keySet().forEach(pos -> {
                if(toVisit.containsKey(pos)) {
                    // we have not yet visited this position in a previous iteration, add a new entry for it
                    this.connectivity.add(this.visitPositionRecursively(pos, Maps.newHashMap(), toVisit));
                }
            });
        }

        protected Map<Pos, Set<Pos>> visitPositionRecursively(Pos pos, Map<Pos, Set<Pos>> visited, Map<Pos, Set<Pos>> toVisit) {
            // If the positions has not yet been visited, we need to dig deeper
            if(!visited.containsKey(pos)) {
                // Fetch the connections
                Set<Pos> connections = this.original.get(pos);
                // Add the position and all its connection to the visited map
                visited.put(pos, connections);
                // Remove it from the positions which still need to be visited
                toVisit.remove(pos);
                // Visit its connections recursively
                connections.forEach(connection -> this.visitPositionRecursively(connection, visited, toVisit));
            }
            // Return the visited positions
            return visited;
        }
    }
}
