package com.infinityraider.agricraft.compat.computer.tiles;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.compat.computer.methods.IMethod;
import com.infinityraider.agricraft.compat.computer.methods.MethodAnalyze;
import com.infinityraider.agricraft.compat.computer.methods.MethodException;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBaseBlock;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBrightness;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetBrightnessRange;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetCurrentSoil;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetGrowthStage;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetNeededSoil;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetPlant;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetSpecimen;
import com.infinityraider.agricraft.compat.computer.methods.MethodGetStats;
import com.infinityraider.agricraft.compat.computer.methods.MethodHasJournal;
import com.infinityraider.agricraft.compat.computer.methods.MethodHasPlant;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsAnalyzed;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsCrossCrop;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsFertile;
import com.infinityraider.agricraft.compat.computer.methods.MethodIsMature;
import com.infinityraider.agricraft.compat.computer.methods.MethodNeedsBaseBlock;
import com.infinityraider.agricraft.reference.AgriNBT;
import com.infinityraider.agricraft.tiles.analyzer.TileEntitySeedAnalyzer;
import java.util.HashMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Optional.InterfaceList(value = {
    @Optional.Interface(modid = "ComputerCraft", iface = "dan200.computercraft.api.peripheral.IPeripheral")
    ,
        @Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.SimpleComponent")
    ,
        @Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.ManagedPeripheral")
})
public class TileEntityPeripheral extends TileEntitySeedAnalyzer {

    private static IMethod[] methods;
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

    public static final EnumFacing[] VALID_DIRECTIONS = {EnumFacing.NORTH, EnumFacing.EAST, EnumFacing.SOUTH, EnumFacing.WEST};
    public static final int MAX = 60;

    public TileEntityPeripheral() {
        super();
        initMethods();
    }

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

    private void initMethods() {
        if (methods == null) {
            methods = methodList();
        }
    }

    public IMethod[] getMethods() {
        initMethods();
        return methods;
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
        if (worldObj.isRemote) {
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
    public int getTimer(EnumFacing dir) {
        if (updateCheck == 0 || timers == null) {
            checkSides();
        }
        return timers.get(dir);
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
        return worldObj.getBlockState(new BlockPos(xCoord() + dir.getFrontOffsetX(), yCoord() + dir.getFrontOffsetY(), zCoord() + dir.getFrontOffsetZ())).getBlock() instanceof BlockCrop;
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

    @Override
    public String getName() {
        return "agricraft_peripheral";
    }

    public String[] getAllMethodNames() {
        String[] names = new String[methods.length];
        for (int i = 0; i < names.length; i++) {
            names[i] = methods[i].getName();
        }
        return names;
    }

    public Object[] invokeMethod(IMethod method, Object... arguments) throws MethodException {
        return method.call(this, worldObj, getPos(), this.getJournal(), arguments);
    }

    private static IMethod[] methodList() {
        return new IMethod[]{
            new MethodAnalyze(),
            new MethodGetBaseBlock(),
            new MethodGetBrightness(),
            new MethodGetBrightnessRange(),
            new MethodGetCurrentSoil(),
            new MethodGetGrowthStage(),
            new MethodGetNeededSoil(),
            new MethodGetPlant(),
            new MethodGetSpecimen(),
            new MethodGetStats(),
            new MethodHasJournal(),
            new MethodHasPlant(),
            new MethodIsAnalyzed(),
            new MethodIsCrossCrop(),
            new MethodIsFertile(),
            new MethodIsMature(),
            new MethodNeedsBaseBlock()
        };
    }

    //---------------------
    //ComputerCraft methods
    //---------------------
    /*
    @Override
    public String getType() {
        return getName();
    }

    @Override
    public String[] getMethodNames() {
        return getAllMethodNames();
    }

    @Override
    @Optional.Method(modid = Names.AgriCraftMods.computerCraft)
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        IMethod calledMethod = methods[method];
        try {
            return invokeMethod(calledMethod, arguments);
        } catch(MethodException e) {
            throw new LuaException(e.getDescription());
        }
    }

    @Override
    @Optional.Method(modid = Names.AgriCraftMods.computerCraft)
    public void attach(IComputerAccess computer) {
    }

    @Override
    @Optional.Method(modid = Names.AgriCraftMods.computerCraft)
    public void detach(IComputerAccess computer) {
    }

    @Override
    @Optional.Method(modid = Names.AgriCraftMods.computerCraft)
    public boolean equals(IPeripheral other) {
        return other instanceof TileEntityPeripheral;
    }
     */
    //---------------------
    //OpenComputers methods
    //---------------------
    /*
    @Override
    public String getComponentName() {
        return getName();
    }

    @Override
    public String[] methods() {
        return getAllMethodNames();
    }

    @Override
    @Optional.Method(modid = Names.AgriCraftMods.openComputers)
    public Object[] invoke(String method, Context context, Arguments args) throws Exception {
        IMethod calledMethod = null;
        for(IMethod iMethod: methods) {
            if(iMethod.getName().equals(method)) {
                calledMethod = iMethod;
                break;
            }
        }
        if(calledMethod == null)  {
            return null;
        }
        try {
            return invokeMethod(calledMethod, args.toArray());
        } catch(MethodException e) {
            throw new Exception(e.getDescription());
        }
    }
     */
}
