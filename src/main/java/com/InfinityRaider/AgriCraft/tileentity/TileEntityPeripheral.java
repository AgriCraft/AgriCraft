package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.ComputerCraftHelper;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.IMethod;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodException;
import com.InfinityRaider.AgriCraft.reference.Names;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.HashMap;


@Optional.Interface(modid = Names.Mods.computerCraft, iface = "dan200.computercraft.api.peripheral.IPeripheral")
public class TileEntityPeripheral extends TileEntitySeedAnalyzer implements IPeripheral {
    private static IMethod[] methods;
    private boolean mayAnalyze = false;
    /** Data to animate the peripheral client side */
    @SideOnly(Side.CLIENT)
    private int updateCheck;
    @SideOnly(Side.CLIENT)
    private HashMap<ForgeDirection, Integer> timers;
    @SideOnly(Side.CLIENT)
    private HashMap<ForgeDirection, Boolean> activeSides;

    public static final ForgeDirection[] VALID_DIRECTIONS = {ForgeDirection.NORTH, ForgeDirection.EAST, ForgeDirection.SOUTH, ForgeDirection.WEST};
    public static final int MAX = 60;

    public TileEntityPeripheral() {
        super();
        initMethods();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setBoolean(Names.NBT.flag, mayAnalyze);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        mayAnalyze = tag.hasKey(Names.NBT.flag) && tag.getBoolean(Names.NBT.flag);
    }

    private void initMethods() {
        if(methods ==null) {
            if(ModHelper.allowIntegration(Names.Mods.computerCraft)) {
                methods = ComputerCraftHelper.getMethods();
            }
        }
    }

    public IMethod[] getMethods() {
        initMethods();
        return methods;
    }

    @Override
    public void updateEntity() {
        if(mayAnalyze) {
            if(this.hasSpecimen() && !isSpecimenAnalyzed()) {
                super.updateEntity();
            } else {
                reset();
            }
        } if(worldObj.isRemote) {
            if(updateCheck == 0) {
                checkSides();
            }
            for(ForgeDirection dir:VALID_DIRECTIONS) {
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
    public int getTimer(ForgeDirection dir) {
        if(updateCheck == 0) {
            checkSides();
        }
        return timers.get(dir);
    }

    @SideOnly(Side.CLIENT)
    private boolean isSideActive(ForgeDirection dir) {
        return activeSides.containsKey(dir) && activeSides.get(dir);
    }

    @SideOnly(Side.CLIENT)
    public void checkSides() {
        for(ForgeDirection dir:VALID_DIRECTIONS) {
            checkSide(dir);
        }
        updateCheck = 0;
    }

    @SideOnly(Side.CLIENT)
    private void checkSide(ForgeDirection dir) {
        if(timers == null) {
            timers = new HashMap<ForgeDirection, Integer>();
        }
        if(!timers.containsKey(dir)) {
            timers.put(dir, 0);
        }
        if(activeSides == null) {
            activeSides = new HashMap<ForgeDirection, Boolean>();
        }
        activeSides.put(dir, isCrop(dir));
    }

    private boolean isCrop(ForgeDirection dir) {
        return worldObj.getBlock(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ) instanceof BlockCrop;
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

    @Override
    public String getType() {
        return "agricraft_peripheral";
    }

    @Override
    public String[] getMethodNames() {
        String[] names = new String[methods.length];
        for(int i=0;i<names.length;i++) {
            names[i] = methods[i].getName();
        }
        return names;
    }

    @Override
    public Object[] callMethod(IComputerAccess computer, ILuaContext context, int method, Object[] arguments) throws LuaException, InterruptedException {
        IMethod calledMethod = methods[method];
        try {
            return calledMethod.call(this, worldObj, xCoord, yCoord, zCoord, this.getJournal(), computer, context, arguments);
        } catch(MethodException e) {
            throw new LuaException(e.getDescription());
        }
    }

    @Override
    public void attach(IComputerAccess computer) {
    }

    @Override
    public void detach(IComputerAccess computer) {
    }

    @Override
    public boolean equals(IPeripheral other) {
        return other instanceof TileEntityPeripheral;
    }

}
