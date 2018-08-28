package com.infinityraider.agricraft.tiles.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.agricraft.agricore.exception.ExceptionMessageBuilder;
import com.google.common.base.Preconditions;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.misc.IAgriConnectable;
import com.infinityraider.agricraft.api.v1.misc.IAgriFluidComponent;
import com.infinityraider.agricraft.api.v1.util.AgriSideMetaMatrix;
import com.infinityraider.agricraft.network.MessageSyncFluidAmount;
import com.infinityraider.agricraft.utility.IAgriFluidComponentSyncable;
import com.infinityraider.infinitylib.utility.WorldHelper;

public class TileEntityChannel extends TileEntityCustomWood implements ITickable, IAgriFluidComponent, IAgriFluidComponentSyncable, IDebuggable {

    public static final int CHANNEL_FLUID_CAPACITY = 1_000;
    public static final int CHANNEL_FLUID_HEIGHT_MIN = 250;
    public static final int CHANNEL_FLUID_HEIGHT_MAX = 750;
    public static final int CHANNEL_FLUID_SYNC_THRESHOLD = 100;
    public static final long CHANNEL_FLUID_SYNC_TIMEOUT = 1000;

    private final int fluidCapacity;
    private final int fluidHeightMin;
    private final int fluidHeightMax;

    private final int fluidSyncThreshold;
    private final long fluidSyncTimeout;

    private final int fluidDensity;

    @Nonnull
    private final AgriSideMetaMatrix connections;

    private int fluidAmount;
    private int oldFluidAmount;

    private long last_update;

    public TileEntityChannel() {
        this(CHANNEL_FLUID_CAPACITY, CHANNEL_FLUID_HEIGHT_MIN, CHANNEL_FLUID_HEIGHT_MAX, CHANNEL_FLUID_SYNC_THRESHOLD, CHANNEL_FLUID_SYNC_TIMEOUT);
    }

    public TileEntityChannel(int fluidCapacity, int fluidHeightMin, int fluidHeightMax, int fluidSyncThreshold, long fluidSyncTimeout) {
        // Validate inputs.
        Preconditions.checkArgument(fluidCapacity > 0);
        Preconditions.checkArgument(fluidHeightMin >= 0);
        Preconditions.checkArgument(fluidHeightMax <= 1_000);
        Preconditions.checkArgument(fluidHeightMin < fluidHeightMax);
        Preconditions.checkArgument(fluidSyncThreshold > 0);
        Preconditions.checkArgument(fluidSyncTimeout >= 500);

        // Save the given values.
        this.fluidCapacity = fluidCapacity;
        this.fluidHeightMin = fluidHeightMin;
        this.fluidHeightMax = fluidHeightMax;
        this.fluidSyncThreshold = fluidSyncThreshold;
        this.fluidSyncTimeout = fluidSyncTimeout;

        // Calculate density.
        this.fluidDensity = fluidCapacity / (fluidHeightMax - fluidHeightMin);

        // Validate fluid density.
        Preconditions.checkArgument(fluidDensity > 0, "Invalid fluid density!\n\tfc: {0}\n\tmh: {1}\n\tMh: {2}", fluidCapacity, fluidHeightMin, fluidHeightMax);

        // Set the other stuff.
        this.fluidAmount = 0;
        this.connections = new AgriSideMetaMatrix();
        this.last_update = 0;

        // Perform initial refresh.
        this.refreshConnections();
    }

    //this saves the data on the tile entity
    @Override
    protected final void writeNBT(NBTTagCompound tag) {
        // Write fluid amount.
        tag.setInteger(AgriNBT.FLUID_AMOUNT, this.fluidAmount);

        // Write connection matrix.
        this.connections.writeToNbt(tag);

        // Write other stuff.
        writeChannelNBT(tag);
    }

    void writeChannelNBT(NBTTagCompound tag) {
    }

    //this loads the saved data for the tile entity
    @Override
    protected final void readNBT(NBTTagCompound tag) {
        // Place to hold the new fluid amount.
        int newFluidAmount = 0;

        // Read fluid amount.
        if (tag.hasKey(AgriNBT.FLUID_AMOUNT)) {
            newFluidAmount = tag.getInteger(AgriNBT.FLUID_AMOUNT);
        }

        // Bring the fluid amount into the proper range (in case of old, bad saves).
        if (newFluidAmount < 0) {
            AgriCore.getLogger("agricraft").warn("Save file has negative fluid amount ({0} mB) for fluid component! Replacing with 0 mB instead!", newFluidAmount);
            newFluidAmount = 0;
        } else if (newFluidAmount > this.fluidCapacity) {
            AgriCore.getLogger("agricraft").warn("Save file has fluid amount ({0} mB) that exceeds the capacity of a fluid component ({1} mB)! Replacing maximum allowed fluid amount ({1} mB) instead!", newFluidAmount, this.fluidCapacity);
            newFluidAmount = this.fluidCapacity;
        }

        // Update the fluid amount.
        this.fluidAmount = newFluidAmount;

        // Read connection matrix.
        this.connections.readFromNbt(tag);

        // Read other stuff.
        readChannelNBT(tag);
    }

    void readChannelNBT(NBTTagCompound tag) {
    }

    @Override
    public boolean canConnectTo(EnumFacing side, IAgriConnectable connectable) {
        return (side != EnumFacing.UP)
                && (connectable instanceof IAgriFluidComponent)
                && (connectable.canConnectTo(EnumFacing.UP, this));
    }

    @Override
    @Nonnull
    public AgriSideMetaMatrix getConnections() {
        return this.connections.copy();
    }

    @Override
    public final void refreshConnections() {
        // Check the connections.
        for (EnumFacing side : EnumFacing.values()) {
            // Classify the side.
            this.connections.set(side, classifyConnection(side));
        }
    }

    protected byte classifyConnection(@Nonnull EnumFacing side) {
        final IAgriFluidComponent component = WorldHelper.getTile(world, pos.offset(side), IAgriFluidComponent.class).orElse(null);
        if (component == null) {
            return 0;
        } else if (side.getAxis().isHorizontal()) {
            return 1;
        } else if (component instanceof TileEntitySprinkler) {
            return 2;
        } else {
            return 0;
        }
    }

    @Override
    public void setFluidAmount(int newFluidAmount) {
        // Ensure amount is in the proper range.
        if (newFluidAmount < 0) {
            AgriCore.getLogger("agricraft").warn("Attempted to set fluid amount of a component to a negative number ({0}mB)!", newFluidAmount);
            newFluidAmount = 0;
        } else if (newFluidAmount > this.fluidCapacity) {
            AgriCore.getLogger("agricraft").warn("Attempted to set fluid amount of a component with capacity {0}mB to {1}mB!", this.fluidCapacity, newFluidAmount);
            newFluidAmount = this.fluidCapacity;
        }

        // Update the amounts.
        this.fluidAmount = newFluidAmount;
        this.oldFluidAmount = newFluidAmount;

        // Mark the component as dirty as it changed.
        this.world.markChunkDirty(pos, this);
    }

    @Override
    public int getFluidAmount() {
        return this.fluidAmount;
    }

    @Override
    public int getFluidHeight() {
        return this.fluidHeightMin + (this.fluidAmount / this.fluidDensity);
    }

    @Override
    public int getMinFluidHeight() {
        return this.fluidHeightMin;
    }

    @Override
    public int getMaxFluidHeight() {
        return this.fluidHeightMax;
    }

    @Override
    public int getFluidCapacity() {
        return this.fluidCapacity;
    }

    @Override
    public int acceptFluid(int inputHeight, int inputAmount, boolean partial, boolean simulate) {
        // Validate inputs.
        Preconditions.checkArgument(inputHeight >= 0, "Negative input heights are not allowed!");
        Preconditions.checkArgument(inputAmount >= 0, "Negative input amounts are not allowed!");

        // First get own fluid height.
        final int fluidHeight = this.getFluidHeight();

        // If incoming level is lower than own level, do nothing.
        if (fluidHeight >= inputHeight) {
            return inputAmount;
        }

        // Calculate the total.
        final int totalFluid = this.fluidAmount + inputAmount;
        final int remainingFluid = Math.max(totalFluid - this.fluidCapacity, 0);
        final int consumedFluid = inputAmount - remainingFluid;

        // Validate everything.
        if (consumedFluid < 0) {
            final ExceptionMessageBuilder eb = new ExceptionMessageBuilder();
            eb.withTitle("Irrigation Component Error");
            eb.withDescription("With %1$d mB of total fluid, somehow consumed %2$d mB, which should be impossible!", totalFluid, consumedFluid);
            eb.withContext("Component Fluid Amount", this.fluidAmount + "mB");
            eb.withContext("Component Fluid Capacity", this.fluidCapacity + "mB");
            eb.withContext("Input Fluid Amount", inputAmount + "mB");
            eb.withContext("Total Fluid Amount", totalFluid + "mB");
            eb.withContext("Remaining Fluid Amount", remainingFluid + "mB");
            eb.withContext("Consumed Fluid Amount", consumedFluid + "mB");
            throw new AssertionError(eb.build());
        }

        // If there was a remainder, but we aren't allowed to have remainders, abort.
        if (!partial && remainingFluid != 0) {
            return inputAmount;
        }

        // If the remainder doesn't equal input, and we are not simulating, then we need to update.
        if (!simulate && remainingFluid != inputAmount) {
            // Update the fluid amount.
            this.fluidAmount = this.fluidAmount + consumedFluid;
            // Mark the component as dirty as it changed.
            this.world.markChunkDirty(pos, this);
        }

        // Return the amount accepted.
        return remainingFluid;
    }

    @Override
    public void update() {
        // Push down.
        if (this.connections.get(EnumFacing.DOWN) > 0) {
            // Get the component.
            final IAgriFluidComponent component = WorldHelper.getTile(world, pos.offset(EnumFacing.DOWN), IAgriFluidComponent.class).orElse(null);
            // Push all fluid.
            if (component != null) {
                int newFluidAmount = component.acceptFluid(1000, fluidAmount, true, false);
                if (newFluidAmount < 0) {
                    throw new AssertionError("A component acccepted too much fluid!");
                }
                this.fluidAmount = newFluidAmount;
            }
        }

        // Push Out.
        for (EnumFacing side : EnumFacing.HORIZONTALS) {
            // Check if connected.
            if (this.connections.get(side) > 0) {
                // Get the component and attempt to push.
                final IAgriFluidComponent component = WorldHelper.getTile(world, pos.offset(side), IAgriFluidComponent.class).orElse(null);
                // If present, do the thing.
                if (component != null) {
                    this.pushToComponent(component);
                }
            }
        }

        // If the fluid amount changed then need to do an update?
        if (Math.abs(this.oldFluidAmount - this.fluidAmount) > this.fluidSyncThreshold) {
            // Check the time.
            long currentTime = System.currentTimeMillis();
            // If time is greater than delta, then do sync.
            if (currentTime - this.last_update > fluidSyncTimeout) {
                // Update the old amount.
                this.oldFluidAmount = this.fluidAmount;

                // If on server side, need to tell client.
                if (!this.world.isRemote) {
                    new MessageSyncFluidAmount(world, pos, fluidAmount).sendToAll();
                }
            }
        }

        // If the component fluid amount changed, mark this as dirty.
        if (this.fluidAmount != this.oldFluidAmount) {
            this.world.markChunkDirty(pos, this);
        }
    }

    private void pushToComponent(@Nonnull IAgriFluidComponent component) {
        // Triple-check not null.
        Preconditions.checkNotNull(component);

        // Get the fluid heights.
        final int fluidHeight = this.getFluidHeight();
        final int otherHeight = component.getFluidHeight();

        // Validate fluid heights.
        if (fluidHeight < 0) {
            throw new AssertionError("A IAgriFluidComponent reported an negative fluid height of " + fluidHeight + "!");
        } else if (otherHeight < 0) {
            throw new AssertionError("A IAgriFluidComponent reported an negative fluid height of " + otherHeight + "!");
        }

        // If the other component is higher or equal, do nothing.
        if (fluidHeight < otherHeight) {
            return;
        }

        // Now calculate deltas.
        final int deltaHeight = fluidHeight - otherHeight;
        int deltaAmount = deltaHeight * this.fluidDensity;

        // If the delta amount is less than two, do nothing.
        if (deltaAmount < 2) {
            return;
        }

        // The delta needs to be less than what the component currently has.
        if (deltaAmount > this.fluidAmount) {
            deltaAmount = this.fluidAmount;
        }

        // Now halve the amounts to get the push amounts.
        final int pushHeight = otherHeight + (deltaHeight / 2);
        final int pushAmount = (deltaAmount / 2);

        // Calculate the amount used.
        final int usedAmount = pushAmount - component.acceptFluid(pushHeight, pushAmount, true, false);

        // Assert that everything worked properly.
        if (usedAmount > pushAmount) {
            throw new AssertionError("Component used " + usedAmount + "mB but was only given " + pushAmount + "mb!");
        } else if (usedAmount < 0) {
            throw new AssertionError("Component used less than zero mB of fluid (" + usedAmount + "mB) which should be impossible!");
        }

        // Ensure that we can actually remove the fluid amount.
        if (usedAmount > this.fluidAmount) {
            throw new AssertionError("Detected possible concurrent modification issue.");
        }

        // Now perform the push.
        this.fluidAmount = this.fluidAmount - usedAmount;

        // Mark the component as dirty as it changed.
        this.world.markChunkDirty(pos, this);
    }

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        consumer.accept("CHANNEL:");
        super.addServerDebugInfo(consumer);
        consumer.accept(" - Fluid Amount: " + this.getFluidAmount() + " / " + this.getFluidCapacity());
        consumer.accept(" - Fluid Height: " + this.getFluidHeight());
        this.connections.toString(s -> consumer.accept(" " + s));
    }

    @Override
    public void addClientDebugInfo(Consumer<String> consumer) {
        consumer.accept("CHANNEL:");
        super.addServerDebugInfo(consumer);
        consumer.accept(" - Fluid Amount: " + this.getFluidAmount() + " / " + this.getFluidCapacity());
        consumer.accept(" - Fluid Height: " + this.getFluidHeight());
        this.connections.toString(s -> consumer.accept(" " + s));
    }

    @Override
    public void addDisplayInfo(@Nonnull Consumer<String> information) {
        //Required call to super (handles validation for us).
        super.addDisplayInfo(information);
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.waterLevel") + ": " + this.getFluidAmount() + "/" + this.getFluidCapacity());
    }

}
