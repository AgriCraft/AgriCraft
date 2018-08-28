package com.infinityraider.agricraft.tiles.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.misc.IAgriFluidComponent;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannelValve extends TileEntityChannel implements IDebuggable {

    private boolean powered;

    public TileEntityChannelValve() {
        this.powered = false;
    }

    @Override
    protected byte classifyConnection(@Nonnull EnumFacing side) {
        final Block b = WorldHelper.getBlock(world, pos.offset(side), Block.class).orElse(null);
        final IAgriFluidComponent component = WorldHelper.getTile(world, pos.offset(side), IAgriFluidComponent.class).orElse(null);
        if (b instanceof BlockLever) {
            return -1;
        } else if (component == null) {
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
    protected final void writeChannelNBT(NBTTagCompound tag) {
        tag.setBoolean(AgriNBT.POWER, powered);
    }

    //this loads the saved data for the tile entity
    @Override
    protected final void readChannelNBT(NBTTagCompound tag) {
        this.powered = tag.getBoolean(AgriNBT.POWER);
    }

    public void updatePowerStatus() {
        final boolean wasPowered = powered;
        powered = this.getWorld().isBlockIndirectlyGettingPowered(getPos()) > 0;
        if (powered != wasPowered) {
            markForUpdate();
        }
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public int acceptFluid(int inputHeight, int inputAmount, boolean partial, boolean simulate) {
        if (!this.powered) {
            return super.acceptFluid(inputHeight, inputAmount, partial, simulate);
        } else {
            return inputAmount;
        }
    }

    @Override
    public void update() {
        if (!this.powered) {
            super.update();
        }
    }

    @Override
    public void addClientDebugInfo(Consumer<String> consumer) {
        super.addClientDebugInfo(consumer);
        consumer.accept("VALVE:");
        consumer.accept("  - State: " + (this.isPowered() ? "closed" : "open"));
    }

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        super.addServerDebugInfo(consumer);
        consumer.accept("VALVE:");
        consumer.accept("  - State: " + (this.isPowered() ? "closed" : "open"));
    }

    @Override
    public void addDisplayInfo(@Nonnull Consumer<String> information) {
        // Required super call (handles validation for us).
        super.addDisplayInfo(information);
        // show status
        String status = AgriCore.getTranslator().translate(powered ? "agricraft_tooltip.closed" : "agricraft_tooltip.open");
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.state") + ": " + status);
    }

}
