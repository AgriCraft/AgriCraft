package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.container.ContainerPeripheral;
import com.infinityraider.agricraft.container.ContainerSeedAnalyzer;
import com.infinityraider.agricraft.container.ContainerSeedStorage;
import com.infinityraider.agricraft.container.ContainerSeedStorageController;
import com.infinityraider.agricraft.gui.GuiPeripheral;
import com.infinityraider.agricraft.gui.GuiSeedAnalyzer;
import com.infinityraider.agricraft.gui.GuiSeedStorage;
import com.infinityraider.agricraft.gui.GuiSeedStorageController;
import com.infinityraider.agricraft.gui.journal.GuiJournal;
import com.infinityraider.agricraft.tileentity.TileEntityPeripheral;
import com.infinityraider.agricraft.tileentity.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.tileentity.storage.TileEntitySeedStorage;
import com.infinityraider.agricraft.tileentity.storage.TileEntitySeedStorageController;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler {
    public static final int seedAnalyzerID = 1;
    public static final int journalID = 2;
    public static final int seedStorageID = 3;
    public static final int seedStorageControllerID = 4;
    public static final int peripheralID = 5;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch(ID) {
            case(seedAnalyzerID):
                if(te != null && te instanceof TileEntitySeedAnalyzer) {
                    return new ContainerSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
                }
            case(journalID): break;
            case(seedStorageID):
                if(te != null && te instanceof TileEntitySeedStorage) {
                    return new ContainerSeedStorage(player.inventory, (TileEntitySeedStorage) te);
                }
            case(seedStorageControllerID):
                if(te != null && te instanceof TileEntitySeedStorageController) {
                    return new ContainerSeedStorageController(player.inventory, (TileEntitySeedStorageController) te);
                }
            case(peripheralID):
                if(te!= null && te instanceof TileEntityPeripheral) {
                    return new ContainerPeripheral(player.inventory, (TileEntityPeripheral) te);
                }
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID) {
            case (seedAnalyzerID):
                if (te != null && te instanceof TileEntitySeedAnalyzer) {
                    return new GuiSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
                }
            case (journalID):
                ItemStack journal = player.getCurrentEquippedItem();
                return new GuiJournal(journal);
            case (seedStorageID):
                if (te != null && te instanceof TileEntitySeedStorage) {
                    return new GuiSeedStorage(player.inventory, (TileEntitySeedStorage) te);
                }
            case (seedStorageControllerID):
                if (te != null && te instanceof TileEntitySeedStorageController) {
                    return new GuiSeedStorageController(player.inventory, (TileEntitySeedStorageController) te);
                }
            case (peripheralID)   :
                if(te!= null && te instanceof TileEntityPeripheral) {
                    return new GuiPeripheral(player.inventory, (TileEntityPeripheral) te);
                }
            default:
                return null;
        }
    }
}
