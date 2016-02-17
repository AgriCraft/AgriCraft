package com.infinityraider.agricraft.tileentity.peripheral;

import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.reference.AgriCraftMods;
import com.infinityraider.agricraft.tileentity.peripheral.method.*;
import com.infinityraider.agricraft.tileentity.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.utility.AgriForgeDirection;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;


@Optional.InterfaceList( value = {
        @Optional.Interface(modid = AgriCraftMods.computerCraft, iface = "dan200.computercraft.api.peripheral.IPeripheral"),
        @Optional.Interface(modid = AgriCraftMods.openComputers, iface = "li.cil.oc.api.network.SimpleComponent"),
        @Optional.Interface(modid = AgriCraftMods.openComputers, iface = "li.cil.oc.api.network.ManagedPeripheral")
})
public class TileEntityPeripheral extends TileEntitySeedAnalyzer {
    private static IMethod[] methods;
    private boolean mayAnalyze = false;
    /** Data to animate the peripheral client side */
    @SideOnly(Side.CLIENT)
    private int updateCheck;
    @SideOnly(Side.CLIENT)
    private HashMap<AgriForgeDirection, Integer> timers;
    @SideOnly(Side.CLIENT)
    private HashMap<AgriForgeDirection, Boolean> activeSides;

    public static final AgriForgeDirection[] VALID_DIRECTIONS = {AgriForgeDirection.NORTH, AgriForgeDirection.EAST, AgriForgeDirection.SOUTH, AgriForgeDirection.WEST};
    public static final int MAX = 60;

    public TileEntityPeripheral() {
        super();
        initMethods();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(AgriCraftNBT.FLAG, mayAnalyze);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        mayAnalyze = tag.hasKey(AgriCraftNBT.FLAG) && tag.getBoolean(AgriCraftNBT.FLAG);
    }

    private void initMethods() {
        if(methods ==null) {
            methods = methodList();
        }
    }

    public IMethod[] getMethods() {
        initMethods();
        return methods;
    }

    @Override
    public void tick() {
        if(mayAnalyze) {
            if(this.hasSpecimen() && !isSpecimenAnalyzed()) {
                super.tick();
            } else {
                reset();
            }
        } if(worldObj.isRemote) {
            if(updateCheck == 0) {
                checkSides();
            }
            for(AgriForgeDirection dir:VALID_DIRECTIONS) {
                int timer = timers.get(dir);
                timer = timer + (isSideActive(dir)?1:-1);
                timer = timer<0?0:timer;
                timer = timer>MAX?MAX:timer;
                timers.put(dir, timer);
            }
            updateCheck = (updateCheck+1)%1200;
        }
    }

    @SideOnly(Side.CLIENT)
    public int getTimer(AgriForgeDirection dir) {
        if(updateCheck == 0 || timers == null) {
            checkSides();
        }
        return timers.get(dir);
    }

    @SideOnly(Side.CLIENT)
    private boolean isSideActive(AgriForgeDirection dir) {
        return activeSides.containsKey(dir) && activeSides.get(dir);
    }

    @SideOnly(Side.CLIENT)
    public void checkSides() {
        for(AgriForgeDirection dir:VALID_DIRECTIONS) {
            checkSide(dir);
        }
        updateCheck = 0;
    }

    @SideOnly(Side.CLIENT)
    private void checkSide(AgriForgeDirection dir) {
        if(timers == null) {
            timers = new HashMap<>();
        }
        if(!timers.containsKey(dir)) {
            timers.put(dir, 0);
        }
        if(activeSides == null) {
            activeSides = new HashMap<>();
        }
        activeSides.put(dir, isCrop(dir));
    }

    private boolean isCrop(AgriForgeDirection dir) {
        return worldObj.getBlockState(new BlockPos(xCoord() + dir.offsetX, yCoord() + dir.offsetY, zCoord() + dir.offsetZ)).getBlock() instanceof BlockCrop;
    }

    public void startAnalyzing() {
        if(!mayAnalyze && this.hasSpecimen() && !this.isSpecimenAnalyzed()) {
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
        if(mayAnalyze) {
            mayAnalyze = false;
            this.markForUpdate();
        }
    }

    @Override
    public boolean isRotatable() {
        return false;
    }

    public String getName() {
        return "agricraft_peripheral";
    }

    public String[] getAllMethodNames() {
        String[] names = new String[methods.length];
        for(int i=0;i<names.length;i++) {
            names[i] = methods[i].getName();
        }
        return names;
    }

    public Object[] invokeMethod(IMethod method, Object... arguments) throws MethodException {
        return method.call(this, worldObj, getPos(), this.getJournal(), arguments);
    }

    private static IMethod[] methodList() {
        return new IMethod[] {
                new MethodAnalyze(),
                new MethodGetBaseBlock(),
                new MethodGetBaseBlockType(),
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
                new MethodHasWeeds(),
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
