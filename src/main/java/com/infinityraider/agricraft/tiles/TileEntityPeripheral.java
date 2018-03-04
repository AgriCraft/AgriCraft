package com.infinityraider.agricraft.tiles;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Context;
import li.cil.oc.api.network.ManagedPeripheral;
import li.cil.oc.api.network.SimpleComponent;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import com.infinityraider.agricraft.api.v1.misc.IAgriPeripheralMethod;

@SimpleComponent.SkipInjection
@Optional.Interface(modid = "open_computers", iface = "li.cil.oc.api.network.SimpleComponent")
@Optional.Interface(modid = "open_computers", iface = "li.cil.oc.api.network.ManagedPeripheral")
public class TileEntityPeripheral extends TileEntitySeedAnalyzer implements SimpleComponent, ManagedPeripheral {

    // ==================================================
    // Constants <editor-fold desc="Constants">
    // --------------------------------------------------
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
    @SideOnly(Side.CLIENT)
    private int updateCheck;

    @SideOnly(Side.CLIENT)
    private HashMap<EnumFacing, Integer> timers;

    @SideOnly(Side.CLIENT)
    private HashMap<EnumFacing, Boolean> activeSides;
    // --------------------------------------------------
    // </editor-fold>
    // ==================================================

    // ==================================================
    // Constructors <editor-fold desc="Constructors">
    // --------------------------------------------------
    public TileEntityPeripheral() {
        super();
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
    
    @SideOnly(Side.CLIENT)
    public int getTimer(EnumFacing dir) {
        if (updateCheck == 0 || timers == null) {
            checkSides();
        }
        return timers.get(dir);
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
        if (mayAnalyze) {
            if (this.hasSpecimen() && !isSpecimenAnalyzed()) {
                super.update();
            } else {
                reset();
            }
        }
        if (this.getWorld().isRemote) {
            if (updateCheck == 0) {
                checkSides();
            }
            for (EnumFacing dir : VALID_DIRECTIONS) {
                int timer = timers.get(dir);
                timer = timer + (isSideActive(dir) ? 1 : -1);
                timer = timer < 0 ? 0 : timer;
                timer = timer > MAX ? MAX : timer;
                timers.put(dir, timer);
            }
            updateCheck = (updateCheck + 1) % 1200;
        }
    }

    @SideOnly(Side.CLIENT)
    private boolean isSideActive(EnumFacing dir) {
        return activeSides.containsKey(dir) && activeSides.get(dir);
    }

    @SideOnly(Side.CLIENT)
    public void checkSides() {
        for (EnumFacing dir : VALID_DIRECTIONS) {
            checkSide(dir);
        }
        updateCheck = 0;
    }

    @SideOnly(Side.CLIENT)
    private void checkSide(EnumFacing dir) {
        if (timers == null) {
            timers = new HashMap<>();
        }
        if (!timers.containsKey(dir)) {
            timers.put(dir, 0);
        }
        if (activeSides == null) {
            activeSides = new HashMap<>();
        }
        activeSides.put(dir, isCrop(dir));
    }

    private boolean isCrop(EnumFacing dir) {
        return this.getWorld().getBlockState(new BlockPos(xCoord() + dir.getFrontOffsetX(), yCoord() + dir.getFrontOffsetY(), zCoord() + dir.getFrontOffsetZ())).getBlock() instanceof BlockCrop;
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
