package com.InfinityRaider.AgriCraft.tileentity.peripheral;

import com.InfinityRaider.AgriCraft.compatibility.ModHelper;
import com.InfinityRaider.AgriCraft.reference.Names;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.method.IMethod;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.method.MethodCropBase;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.method.MethodException;
import cpw.mods.fml.common.Optional;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;


@Optional.Interface(modid = Names.Mods.computerCraft, iface = "dan200.computercraft.api.peripheral.IPeripheral")
public class TileEntityPeripheral extends TileEntitySeedAnalyzer implements IPeripheral {
    private static IMethod[] methods;

    public TileEntityPeripheral() {
        super();
        getMethods();
    }

    private void getMethods() {
        if(methods ==null) {
            if(ModHelper.allowIntegration(Names.Mods.computerCraft)) {
                methods = MethodCropBase.getMethods();
            }
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
