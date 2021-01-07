package com.infinityraider.agricraft.tiles;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.crop.IAgriCrop;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.Optional;
import com.infinityraider.agricraft.api.v1.misc.IAgriPeripheralMethod;
import com.infinityraider.agricraft.api.v1.util.AgriSideMetaMatrix;
import com.infinityraider.infinitylib.utility.WorldHelper;

@Optional.Interface(modid = "opencomputers", iface = "li.cil.oc.api.network.SimpleComponent")
@Optional.Interface(modid = "opencomputers", iface = "li.cil.oc.api.network.ManagedPeripheral")
public class TileEntityPeripheral extends TileEntitySeedAnalyzer implements SimpleComponent, ManagedPeripheral {

    // ==================================================
    // Constants <editor-fold desc="Constants">
    // --------------------------------------------------
    public static final long NEIGHBOR_CHECK_TIMEOUT = 500;
    public static final EnumFacing[] VALID_DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    public static final int MAX = 60;
    // --------------------------------------------------
    // </editor-fold>
    // ==================================================

    // ==================================================
    // Variables <editor-fold desc="Variables">
    // --------------------------------------------------
    private boolean mayAnalyze = false;

    /**
     * Data to animate the peripheral client side
     */
    @Nonnull
    private final AgriSideMetaMatrix connections;

    private long last_update;

    // --------------------------------------------------
    // </editor-fold>
    // ==================================================
    // ==================================================
    // Constructors <editor-fold desc="Constructors">
    // --------------------------------------------------
    public TileEntityPeripheral() {
        // Call Super Constructor.
        super();

        // Set Values.
        this.connections = new AgriSideMetaMatrix();
        this.last_update = 0;
        this.last_update = 0;

        // Perform initial refresh.
        this.refreshConnections();
    }
    // --------------------------------------------------
    // </editor-fold>
    // ==================================================

    // ==================================================
    // Getters <editor-fold desc="Getters">
    // --------------------------------------------------
    @Override
    @Nonnull
    public String getName() {
        return "agricraft_peripheral";
    }
    // --------------------------------------------------
    // </editor-fold>
    // ==================================================

    @Override
    protected void writeRotatableTileNBT(NBTTagCompound tag) {
        super.writeRotatableTileNBT(tag);
        tag.setBoolean(AgriNBT.FLAG, mayAnalyze);
    }

    @Override
    protected void readRotatableTileNBT(NBTTagCompound tag) {
        super.readRotatableTileNBT(tag);
        mayAnalyze = tag.hasKey(AgriNBT.FLAG) && tag.getBoolean(AgriNBT.FLAG);
    }

    @Override
    public void update() {
        // Do Analyzing.
        if (mayAnalyze) {
            if (this.hasSpecimen() && !isSpecimenAnalyzed()) {
                super.update();
            } else {
                reset();
            }
        }
        
        // Check the time.
        final long currentTime = System.currentTimeMillis();
        
        // If time is greater than delta, then do update.
        if (currentTime - this.last_update > NEIGHBOR_CHECK_TIMEOUT) {
            // Do the update.
            this.refreshConnections();
        }
    }

    public final void refreshConnections() {
        // Check the connections.
        for (EnumFacing side : EnumFacing.values()) {
            // Classify the side.
            this.connections.set(side, classifyConnection(side));
        }
        // Update the timer.
        this.last_update = System.currentTimeMillis();
    }

    protected byte classifyConnection(@Nonnull EnumFacing side) {
        if (isCrop(side)) {
            return 1;
        } else {
            return 0;
        }
    }

    private boolean isCrop(EnumFacing dir) {
        return WorldHelper.getTile(this.world, this.pos.offset(dir), IAgriCrop.class).isPresent();
    }

    public void startAnalyzing() {
        if (!mayAnalyze && this.hasSpecimen() && !this.isSpecimenAnalyzed()) {
            mayAnalyze = true;
            this.markForUpdate();
        }
    }

    @Override
    public void analyze() {
        super.analyze();
        reset();
    }

    private void reset() {
        if (mayAnalyze) {
            mayAnalyze = false;
            this.markForUpdate();
        }
    }

    // ==================================================
    // OpenComputers <editor-fold desc="OpenComputers">
    // --------------------------------------------------
    @Override
    @Nonnull
    @Optional.Method(modid = "opencomputers")
    public String getComponentName() {
        return getName();
    }

    @Override
    @Nonnull
    @Optional.Method(modid = "opencomputers")
    public String[] methods() {
        return AgriApi.getPeripheralMethodRegistry().ids().toArray(new String[0]);
    }

    @Override
    @Nullable
    @Optional.Method(modid = "opencomputers")
    public Object[] invoke(@Nullable String methodName, Context context, @Nullable Arguments args) throws Exception {
        // Step 0. Fetch the respective method.
        final IAgriPeripheralMethod method = AgriApi.getPeripheralMethodRegistry().get(methodName).orElse(null);

        // Step 1. Check method actually exists.
        if (method == null) {
            return null;
        }

        // Step 2. Convert arguments to object array.
        final Object[] argObjects = (args == null) ? null : args.toArray();

        // Step 3. Attempt to evaluate the method.
        try {
            return method.call(this.getWorld(), this.getPos(), this.getJournal(), argObjects);
        } catch (IAgriPeripheralMethod.InvocationException e) {
            throw new Exception(e.getDescription());
        }
    }
    // --------------------------------------------------
    // </editor-fold>
    // ==================================================

}
