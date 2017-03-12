package com.infinityraider.agricraft.tiles.irrigation;

import com.infinityraider.agricraft.reference.AgriProperties;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.infinitylib.block.blockstate.SidedConnection;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import net.minecraft.block.BlockLever;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import com.agricraft.agricore.core.AgriCore;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

import com.infinityraider.agricraft.reference.AgriNBT;
import java.util.function.Consumer;

public class TileEntityChannelValve extends TileEntityChannel implements IDebuggable {

    private boolean powered = false;
    private SidedConnection levers = new SidedConnection();

    @Override
    protected final void writeChannelNBT(NBTTagCompound tag) {
        tag.setBoolean(AgriNBT.POWER, powered);
        this.levers.writeToNBT(tag);
    }

    //this loads the saved data for the tile entity
    @Override
    protected final void readChannelNBT(NBTTagCompound tag) {
        this.powered = tag.getBoolean(AgriNBT.POWER);
        this.levers.readFromNBT(tag);
    }

    @Override
    public void update() {
        if (!this.worldObj.isRemote) {
            if (!this.powered) {
                super.update();
            } else if (++ticksSinceNeighbourCheck > NEIGHBOUR_CHECK_DELAY) {
                checkConnections();
                ticksSinceNeighbourCheck = 0;
            }
        }
    }

    public void updatePowerStatus() {
        final boolean wasPowered = powered;
        powered = worldObj.isBlockIndirectlyGettingPowered(getPos()) > 0;
        if (powered != wasPowered) {
            markForUpdate();
        }
    }

    public void updateLevers() {
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            IBlockState neighbour = this.getWorld().getBlockState(this.getPos().add(dir.getFrontOffsetX(), 0, dir.getFrontOffsetZ()));
            this.levers.setConnected(dir, neighbour.getBlock() instanceof BlockLever && neighbour.getValue(BlockLever.FACING).getFacing() == dir);
        }
        this.markForUpdate();
    }

    public IExtendedBlockState addLeversToState(IExtendedBlockState state) {
        return state.withProperty(AgriProperties.CONNECTIONS, this.levers);
    }

    public boolean isPowered() {
        return powered;
    }

    @Override
    public boolean canAcceptFluid(int y, int amount, boolean partial) {
        return !powered && super.canAcceptFluid(y, amount, partial);
    }

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        consumer.accept("VALVE");
        consumer.accept("  - State: " + (this.isPowered() ? "closed" : "open"));
        consumer.accept("  - FluidLevel: " + this.getFluidAmount(0) + "/" + Constants.BUCKET_mB / 2);
        consumer.accept("  - FluidHeight: " + this.getFluidHeight());
        consumer.accept("  - Material: " + this.getMaterialBlock().getRegistryName() + ":" + this.getMaterialMeta()); //Much Nicer.
    }

    @Override
    @SideOnly(Side.CLIENT)
    @SuppressWarnings("unchecked")
    public void addDisplayInfo(List information) {
        //Required super call
        super.addDisplayInfo(information);
        //show status
        String status = AgriCore.getTranslator().translate(powered ? "agricraft_tooltip.closed" : "agricraft_tooltip.open");
        information.add(AgriCore.getTranslator().translate("agricraft_tooltip.state") + ": " + status);
    }
}
