package com.infinityraider.agricraft.capability;

import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNode;
import com.infinityraider.infinitylib.capability.IInfCapabilityImplementation;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CapabilityIrrigationComponent implements IInfCapabilityImplementation<TileEntity, IAgriIrrigationComponent> {
    private static final CapabilityIrrigationComponent INSTANCE = new CapabilityIrrigationComponent();

    public static CapabilityIrrigationComponent getInstance() {
        return INSTANCE;
    }

    private CapabilityIrrigationComponent() {}

    @Override
    public Class<IAgriIrrigationComponent> getCapabilityClass() {
        return IAgriIrrigationComponent.class;
    }

    @Override
    public Serializer<IAgriIrrigationComponent> getSerializer() {
        return new Serializer<IAgriIrrigationComponent>() {
            @Override
            public CompoundNBT writeToNBT(IAgriIrrigationComponent object) {
                // No need to serialize anything
                return new CompoundNBT();
            }

            @Override
            public void readFromNBT(IAgriIrrigationComponent object, CompoundNBT nbt) {
                // No need to deserialize anything
            }
        };
    }

    @Override
    public void copyData(IAgriIrrigationComponent from, IAgriIrrigationComponent to) {
        // No need
    }

    @Override
    public Capability<IAgriIrrigationComponent> getCapability() {
        return IAgriIrrigationComponent.Cap.INSTANCE;
    }

    @Override
    public boolean shouldApplyCapability(TileEntity carrier) {
        return false;
    }

    @Override
    public IAgriIrrigationComponent createNewValue(TileEntity carrier) {
        // Will never be applied
        return null;
    }

    @Override
    public ResourceLocation getCapabilityKey() {
        return IAgriIrrigationComponent.Cap.KEY;
    }

    @Override
    public Class<TileEntity> getCarrierClass() {
        return TileEntity.class;
    }

    public boolean isIrrigationComponent(TileEntity tile) {
        return tile instanceof IAgriIrrigationComponent
                || tile.getCapability(this.getCapability()).isPresent();
    }

    public Optional<IAgriIrrigationComponent> getIrrigationComponent(TileEntity tile) {
        if(tile instanceof IAgriIrrigationComponent) {
            return Optional.of((IAgriIrrigationComponent) tile);
        } else {
            return tile.getCapability(this.getCapability()).map(c -> c);
        }
    }

    public void acceptForNode(TileEntity tile, Direction dir, Consumer<IAgriIrrigationNode> consumer) {
        this.acceptForComponent(tile, dir, (component) -> component.getNode(dir).ifPresent(consumer));
    }

    public void acceptForComponent(TileEntity tile, Direction dir, NonNullConsumer<IAgriIrrigationComponent> consumer) {
        if(tile instanceof IAgriIrrigationComponent) {
            consumer.accept((IAgriIrrigationComponent) tile);
        } else {
            tile.getCapability(this.getCapability(), dir).ifPresent(consumer);
        }
    }

    public <T> Optional<T> applyForNode(TileEntity tile, Direction dir, Function<IAgriIrrigationNode, T> function) {
        return this.applyForComponent(tile, dir, (component) -> component.getNode(dir).map(function)).map(Optional::get);
    }

    public <T> Optional<T> applyForComponent(TileEntity tile, Direction dir, NonNullFunction<IAgriIrrigationComponent, T> function) {
        if(tile instanceof IAgriIrrigationComponent) {
            return Optional.of(function.apply((IAgriIrrigationComponent) tile));
        } else {
            return tile.getCapability(this.getCapability(), dir).map(function);
        }
    }
}
