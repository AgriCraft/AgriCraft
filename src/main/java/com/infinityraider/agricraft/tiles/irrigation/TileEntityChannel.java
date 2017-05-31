package com.infinityraider.agricraft.tiles.irrigation;

import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.irrigation.IConnectable;
import com.infinityraider.agricraft.api.v1.irrigation.IIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IrrigationConnectionType;
import com.infinityraider.agricraft.network.MessageSyncFluidLevel;
import com.infinityraider.agricraft.reference.AgriCraftConfig;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.tiles.TileEntityCustomWood;
import com.infinityraider.infinitylib.utility.WorldHelper;
import com.infinityraider.infinitylib.utility.debug.IDebuggable;
import java.util.function.Consumer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChannel extends TileEntityCustomWood implements ITickable, IIrrigationComponent, IDebuggable {

    private final IIrrigationComponent[] neighbours = new IIrrigationComponent[4];
    protected static final int NEIGHBOUR_CHECK_DELAY = 1024;
    protected int ticksSinceNeighbourCheck = NEIGHBOUR_CHECK_DELAY;

    // Might want to move this to a static import class...
    protected static final int MIN = 5;
    protected static final int MAX = 12;
    protected static final int HEIGHT = MAX - MIN;
    protected static final int DISCRETE_MAX = 16;
    protected static final int ABSOLUTE_MAX = AgriCraftConfig.channelCapacity;
    protected static final float DISCRETE_FACTOR = (float) DISCRETE_MAX / (float) ABSOLUTE_MAX;

    private int lvl;
    private int lastDiscreteLvl = 0;

    //this saves the data on the tile entity
    @Override
    protected final void writeNBT(NBTTagCompound tag) {
        if (this.lvl > 0) {
            tag.setInteger(AgriNBT.LEVEL, this.lvl);
        }
        writeChannelNBT(tag);
    }

    void writeChannelNBT(NBTTagCompound tag) {
    }

    //this loads the saved data for the tile entity
    @Override
    protected final void readNBT(NBTTagCompound tag) {
        if (tag.hasKey(AgriNBT.LEVEL)) {
            this.lvl = tag.getInteger(AgriNBT.LEVEL);
        } else {
            this.lvl = 0;
        }
        readChannelNBT(tag);
    }

    void readChannelNBT(NBTTagCompound tag) {
    }

    @Override
    public int getFluidAmount(int y) {
        return this.lvl;
    }

    @Override
    public int getCapacity() {
        return ABSOLUTE_MAX;
    }

    @Override
    public void setFluidLevel(int lvl) {
        if (lvl >= 0 && lvl <= ABSOLUTE_MAX && lvl != this.lvl) {
            this.lvl = lvl;
            syncFluidLevel();
        }
    }

    @Override
    public int acceptFluid(int y, int amount, boolean partial) {
        if (!worldObj.isRemote && amount >= 0 && this.canAcceptFluid(0, amount, partial)) {
            int room = this.getCapacity() - this.getFluidAmount(0);
            if (room >= amount) {
                this.setFluidLevel(this.getFluidAmount(0) + amount);
                amount = 0;
            } else if (room > 0) {
                this.setFluidLevel(this.getCapacity());
                amount = amount - room;
            }
        }
        return amount;
    }

    @Override
    public boolean canConnectTo(EnumFacing side, IConnectable component) {
        return (component instanceof TileEntityCustomWood) && this.isSameMaterial((TileEntityCustomWood) component);
    }

    @Override
    public boolean canAcceptFluid(int y, int amount, boolean partial) {
        if (this.lvl + amount >= this.getCapacity()) {
            return true;
        } else {
            return partial;
        }
    }

    @Override
    public final void checkConnections() {
        for (int i = 0; i < EnumFacing.HORIZONTALS.length; i++) {
            final EnumFacing dir = EnumFacing.HORIZONTALS[i];
            neighbours[i] = WorldHelper.getTile(worldObj, pos.offset(dir), IIrrigationComponent.class)
                    .filter(n -> n.canConnectTo(dir.getOpposite(), this) || this.canConnectTo(dir, n))
                    .orElse(null);
        }
    }

    @Override
    public int getFluidHeight() {
        return (int) getFluidHeight(getFluidAmount(0));
    }

    @Override
    public float getFluidHeight(int lvl) {
        return MIN + HEIGHT * ((float) lvl) / (ABSOLUTE_MAX);
    }

    public boolean hasNeighbor(EnumFacing direction) {
        final int dir = direction.getHorizontalIndex();
        return dir >= 0 && neighbours[dir] != null;
    }

    public IIrrigationComponent getNeighbor(EnumFacing direction) {
        final int dir = direction.getHorizontalIndex();
        return dir >= 0 ? neighbours[dir] : null;
    }

    @Override
    public IrrigationConnectionType getConnectionType(EnumFacing side) {
        if (this.hasNeighbor(side)) {
            return IrrigationConnectionType.PRIMARY;
        } else {
            return IrrigationConnectionType.NONE;
        }
    }

    //updates the tile entity every tick
    @Override
    public void update() {
        if (++ticksSinceNeighbourCheck > NEIGHBOUR_CHECK_DELAY) {
            checkConnections();
            ticksSinceNeighbourCheck = 0;
        }
        if (!this.worldObj.isRemote) {
            //calculate total fluid lvl and capacity
            int totalLvl = 0;
            int nr = 1;
            int updatedLevel = this.getFluidAmount(0);
            for (IIrrigationComponent component : neighbours) {
                if (component == null) {
                    continue;
                }
                //neighbour is a channel: add its volume to the total and increase the COUNT
                if (component instanceof TileEntityChannel) {
                    if (!(component instanceof TileEntityChannelValve && ((TileEntityChannelValve) component).isPowered())) {
                        totalLvl = totalLvl + ((TileEntityChannel) component).lvl;
                        nr++;
                    }
                } //neighbour is a tank: calculate the fluid levels of the tank and the channel
                else {
                    TileEntityTank tank = (TileEntityTank) component;
                    int Y = tank.getYPosition();
                    float y_c = Constants.WHOLE * Y + this.getFluidHeight(updatedLevel);  //initial channel water Y1
                    float y_t = tank.getFluidHeight();           //initial tank water Y1
                    float y1 = (float) MIN + Constants.WHOLE * Y;   //minimum Y1 of the channel
                    float y2 = (float) MAX + Constants.WHOLE * Y;  //maximum Y1 of the channel
                    int V_tot = tank.getFluidAmount(0) + updatedLevel;
                    if (y_c != y_t) {
                        //total volume is below the channel connection
                        if (y_t <= y1) {
                            updatedLevel = 0;
                            tank.setFluidLevel(V_tot);
                        } //total volume is above the channel connection
                        else if (y_t >= y2) {
                            updatedLevel = ABSOLUTE_MAX;
                            tank.setFluidLevel(V_tot - ABSOLUTE_MAX);
                        } //total volume is between channel connection top and bottom
                        else {
                            //some parameters
                            int tankYSize = tank.getMultiBlockData().sizeY();
                            int C = tank.getCapacity();
                            //calculate the Y1 corresponding to the total volume: Y1 = f(V_tot), V_tank = f(Y1), V_channel = f(Y1)
                            float enumerator = (V_tot) + ((ABSOLUTE_MAX * y1) / (y2 - y1) + ((float) 2 * C) / (Constants.WHOLE * tankYSize - 2));
                            float denominator = ((ABSOLUTE_MAX) / (y2 - y1) + ((float) C) / ((float) (Constants.WHOLE * tankYSize - 2)));
                            float y = enumerator / denominator;
                            //convert the Y1 to volumes
                            int channelVolume = (int) Math.floor(ABSOLUTE_MAX * (y - y1) / (y2 - y1));
                            int tankVolume = (int) Math.ceil(C * (y - 2) / (Constants.WHOLE * tankYSize - 2));
                            updatedLevel = channelVolume;
                            tank.setFluidLevel(tankVolume);
                        }
                    }
                }
            }
            // Handle Sprinklers
            TileEntitySprinkler spr = WorldHelper.getTile(worldObj, this.pos.add(0, -1, 0), TileEntitySprinkler.class).orElse(null);
            if (spr != null) {
                updatedLevel = spr.acceptFluid(1000, updatedLevel, true);
            }
            //equalize water LEVEL over all neighbouring channels
            totalLvl = totalLvl + updatedLevel;
            int rest = totalLvl % nr;
            int newLvl = totalLvl / nr;
            if (nr > 1) {
                //set fluid levels
                for (IIrrigationComponent component : neighbours) {
                    //TODO: cleanup
                    if (component instanceof TileEntityChannel) {
                        if (!(component instanceof TileEntityChannelValve && ((TileEntityChannelValve) component).isPowered())) {
                            final int olvl = rest == 0 ? newLvl : newLvl + 1;
                            rest = rest == 0 ? 0 : rest - 1;
                            component.setFluidLevel(olvl);
                        }
                    }
                }
            }
            this.setFluidLevel(newLvl + rest);
        }
    }

    @Override
    public void syncFluidLevel() {
        if (!this.worldObj.isRemote) {
            int newDiscreteLvl = getDiscreteFluidLevel();
            if (newDiscreteLvl != lastDiscreteLvl) {
                lastDiscreteLvl = newDiscreteLvl;
                NetworkRegistry.TargetPoint point = new NetworkRegistry.TargetPoint(this.worldObj.provider.getDimension(),
                        this.xCoord(), this.yCoord(), this.zCoord(), 64);
                new MessageSyncFluidLevel(this.lvl, this.getPos()).sendToAllAround(point);
            }
        }
    }

    /**
     * Maps the current fluid LEVEL into the integer interval [0, 16]
     *
     * @return The discrete fluid level.
     */
    public int getDiscreteFluidLevel() {
        int discreteFluidLevel = Math.round(DISCRETE_FACTOR * lvl);
        if (discreteFluidLevel == 0 && lvl > 0) {
            discreteFluidLevel = 1;
        }
        return discreteFluidLevel;
    }

    @Override
    public void addServerDebugInfo(Consumer<String> consumer) {
        consumer.accept("CHANNEL:");
        super.addServerDebugInfo(consumer);
        consumer.accept("  - FluidLevel: " + this.getFluidAmount(0) + "/" + ABSOLUTE_MAX);
        consumer.accept("  - FluidHeight: " + this.getFluidHeight());
        consumer.accept("  - Connections: ");
        for (EnumFacing dir : EnumFacing.values()) {
            if (this.hasNeighbor(dir)) {
                consumer.accept("      - " + dir.name());
            }
        }
    }

    @Override
    public void addClientDebugInfo(Consumer<String> consumer) {
        consumer.accept("CHANNEL:");
        super.addClientDebugInfo(consumer);
        consumer.accept("  - FluidLevel: " + this.getFluidAmount(0) + "/" + ABSOLUTE_MAX);
        consumer.accept("  - FluidHeight: " + this.getFluidHeight());
        consumer.accept("  - Connections: ");
        for (EnumFacing dir : EnumFacing.values()) {
            if (this.hasNeighbor(dir)) {
                consumer.accept("      - " + dir.name());
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addDisplayInfo(Consumer<String> information) {
        //Required call to super.
        super.addDisplayInfo(information);
        information.accept(AgriCore.getTranslator().translate("agricraft_tooltip.waterLevel") + ": " + this.getFluidAmount(0) + "/" + ABSOLUTE_MAX);
    }

    protected IBlockState getStateChannel(IBlockState state) {
        return state;
    }

}
