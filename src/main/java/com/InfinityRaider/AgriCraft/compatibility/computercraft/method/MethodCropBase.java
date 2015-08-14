package com.InfinityRaider.AgriCraft.compatibility.computercraft.method;

import com.InfinityRaider.AgriCraft.items.ItemJournal;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityCrop;
import com.InfinityRaider.AgriCraft.tileentity.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public abstract class MethodCropBase implements IMethod {
    private final String name;

    public MethodCropBase(String name) {
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    @Override
    public final Object[] call(TileEntityPeripheral peripheral, World world, int x, int y, int z, ItemStack journal, Object... args) throws MethodException {
        ForgeDirection dir=getDirection(args);
        if (dir==ForgeDirection.UNKNOWN) {
            throw new MethodException(this, "INVALID DIRECTION");
        }
        TileEntity tile = world.getTileEntity(x+dir.offsetX, y+dir.offsetY, z+dir.offsetZ);
        if(tile==null || !(tile instanceof TileEntityCrop)) {
            throw new MethodException(this, "There is no crop there");
        }
        TileEntityCrop crop = (TileEntityCrop) tile;
        boolean hasJournal = journal!=null;
        if(requiresJournal()) {
            if(!hasJournal) {
                throw new MethodException(this, "Journal is missing");
            }
            if(!isSeedDiscovered(world.getTileEntity(x, y, z), crop)) {
                throw new MethodException(this, "No information about this seed in the journal");
            }
        }
        return onMethodCalled(crop);
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

    private boolean isSeedDiscovered(TileEntity tile, TileEntityCrop crop) {
        if(tile==null || !(tile instanceof TileEntitySeedAnalyzer)) {
            //should never happen
            return false;
        }
        TileEntitySeedAnalyzer analyzer = (TileEntitySeedAnalyzer) tile;
        ItemStack journal = analyzer.getJournal();
        return ((ItemJournal) journal.getItem()).isSeedDiscovered(journal, crop.getSeedStack());
    }

    protected abstract Object[] onMethodCalled(TileEntityCrop crop) throws MethodException;

    protected abstract boolean requiresJournal();
}
