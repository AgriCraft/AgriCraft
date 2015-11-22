package com.InfinityRaider.AgriCraft.tileentity.peripheral.method;

import com.InfinityRaider.AgriCraft.api.v2.ITrowel;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

import java.util.ArrayList;

public abstract class MethodBase implements IMethod {
    private final String name;

    public MethodBase(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        if(this.appliesToPeripheral() && (args==null || args.length==0)) {
            return callMethodForPeripheral(peripheral, journal);
        }
        else if(this.appliesToCrop()) {
            ForgeDirection dir=getDirection(args);
            if (dir==ForgeDirection.UNKNOWN) {
                throw new MethodException(this, "Invalid direction");
            }
            TileEntity tile = world.getTileEntity(x + dir.offsetX, y + dir.offsetY, z + dir.offsetZ);
            if (tile == null || !(tile instanceof TileEntityCrop)) {
                throw new MethodException(this, "There is no crop there");
            }
            TileEntityCrop crop = (TileEntityCrop) tile;
            return callMethodForCrop(crop, journal);
        }
        throw new MethodException(this, "Invalid arguments");
    }

    private ForgeDirection getDirection(Object... args) {
        for (Object obj : args) {
            ForgeDirection dir = ForgeDirection.UNKNOWN;
            if(obj==null) {
                continue;
            }
            if(obj instanceof Object[]) {
                dir = getDirection((Object[]) obj);
            }
            else if (obj instanceof String) {
                dir = ForgeDirection.valueOf((String) obj);
            }
            if (dir!=null && dir!=ForgeDirection.UNKNOWN) {
                return dir;
            }
        }
        return ForgeDirection.UNKNOWN;
    }

    private Object[] callMethodForPeripheral(TileEntityPeripheral peripheral, ItemStack journal) throws MethodException {
        if(requiresJournal()) {
            if(journal == null || journal.getItem() == null) {
                throw new MethodException(this, "Journal is missing");
            }
            ItemStack specimen = peripheral.getSpecimen();
            ItemStack seed = specimen.copy();
            if(specimen.getItem() instanceof ITrowel) {
                seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
            }
            if(!isSeedDiscovered(journal, seed)) {
                throw new MethodException(this, "No information about this seed in the journal");
            }
        }
        return onMethodCalled(peripheral);
    }

    private Object[] callMethodForCrop(TileEntityCrop crop, ItemStack journal) throws MethodException {
        boolean hasJournal = journal != null;
        if (requiresJournal()) {
            if (!hasJournal) {
                throw new MethodException(this, "Journal is missing");
            }
            if (!isSeedDiscovered(journal, crop.getSeedStack())) {
                throw new MethodException(this, "No information about this seed in the journal");
            }
        }
        return onMethodCalled(crop);
    }

    protected abstract boolean appliesToCrop();

    protected abstract Object[] onMethodCalled(TileEntityCrop crop) throws MethodException;

    protected abstract boolean appliesToPeripheral();

    protected abstract Object[] onMethodCalled(TileEntityPeripheral peripheral) throws MethodException;

    protected abstract boolean requiresJournal();

    protected boolean isSeedDiscovered(ItemStack journal, ItemStack seed) {
        if(journal == null || journal.getItem() == null || !(journal.getItem() instanceof ItemJournal)) {
            return false;
        }
        return ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, seed);
    }

    protected CropPlant getCropPlant(ItemStack specimen) {
        ItemStack seed = specimen;
        if(specimen == null || specimen.getItem() == null) {
            return null;
        }
        if(specimen.getItem() instanceof ITrowel) {
            seed = ((ITrowel) specimen.getItem()).getSeed(specimen);
        }
        return CropPlantHandler.getPlantFromStack(seed);
    }

    protected CropPlant getCropPlant(TileEntityCrop crop) {
        return crop.getPlant();
    }
    @Override
    public final String getDescription() {
        return StatCollector.translateToLocal("agricraft_description.method." + this.getName());
    }

    @Override
    public final String signature() {
        StringBuilder signature = new StringBuilder(this.getName() + "(");
        boolean separator = false;
        for(MethodParameter parameter:getParameters()) {
            if(separator) {
                signature.append(", ");
            } else {
                separator = true;
            }
            signature.append(parameter.getName());
        }
        signature.append(")");
        return signature.toString();
    }

    protected abstract ArrayList<MethodParameter> getParameters();
}
