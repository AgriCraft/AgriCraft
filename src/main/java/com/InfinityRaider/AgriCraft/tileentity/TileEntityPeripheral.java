package com.InfinityRaider.AgriCraft.tileentity;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.ComputerCraftHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.IMethod;
import com.InfinityRaider.AgriCraft.compatibility.computercraft.method.MethodException;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.nbt.NBTTagCompound;


@Optional.Interface(modid = Names.Mods.computerCraft, iface = "dan200.computercraft.api.peripheral.IPeripheral")
public class TileEntityPeripheral extends TileEntitySeedAnalyzer implements IPeripheral {
    private static IMethod[] methods;
    private boolean mayAnalyze = false;

    public TileEntityPeripheral() {
        super();
        getMethods();
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

    private void getMethods() {
        if(methods ==null) {
            if(ModHelper.allowIntegration(Names.Mods.computerCraft)) {
                methods = ComputerCraftHelper.getMethods();
            }
        }
    }


    @Override
    public void updateEntity() {
        if(mayAnalyze) {
            if(this.hasSpecimen() && !isSpecimenAnalyzed()) {
                super.updateEntity();
            } else {
                reset();
            }
        }
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
