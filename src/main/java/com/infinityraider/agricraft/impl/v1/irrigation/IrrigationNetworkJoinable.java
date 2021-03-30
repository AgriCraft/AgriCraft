package com.infinityraider.agricraft.impl.v1.irrigation;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import net.minecraft.util.Direction;

import javax.annotation.Nonnull;
import java.util.Optional;

public abstract class IrrigationNetworkJoinable implements IAgriIrrigationNetwork {
    @Nonnull
    @Override
    public Optional<IAgriIrrigationNetwork> tryJoinComponent(
            @Nonnull IAgriIrrigationNode from,
            @Nonnull IAgriIrrigationComponent component,
            @Nonnull Direction dir) {
        return component.getNode(dir.getOpposite()).flatMap(to -> {
            IAgriIrrigationNetwork other = component.getNetwork(dir.getOpposite());
            // Make sure that both networks are valid and distinct
            if(this.isInvalidOrEqual(other)) {
                return Optional.empty();
            }
            // Make sure that the node can connect to the joining node
            if(!from.canConnect(to, dir)) {
                return Optional.empty();
            }
            return this.joinComponent(from, to, other, component, dir);
        });

    }

    protected boolean isInvalidOrEqual(@Nonnull IAgriIrrigationNetwork other) {
        if(!this.isValid()) {
            // this network not a valid one
            return true;
        }
        if(!other.isValid()) {
            // other network is not a valid one
            return true;
        }
        // both networks are valid, check if they are distinct
        return this.equals(other);
    }

    protected abstract Optional<IAgriIrrigationNetwork> joinComponent(
            @Nonnull IAgriIrrigationNode from,
            @Nonnull IAgriIrrigationNode to,
            @Nonnull IAgriIrrigationNetwork other,
            @Nonnull IAgriIrrigationComponent component,
            @Nonnull Direction dir);
}
