package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.container.ContainerPeripheral;
import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.container.ContainerSeedStorage;
import com.InfinityRaider.AgriCraft.container.ContainerSeedStorageController;
import com.InfinityRaider.AgriCraft.gui.GuiPeripheral;
import com.InfinityRaider.AgriCraft.gui.GuiSeedAnalyzer;
import com.InfinityRaider.AgriCraft.gui.GuiSeedStorage;
import com.InfinityRaider.AgriCraft.gui.GuiSeedStorageController;
import com.InfinityRaider.AgriCraft.gui.journal.GuiJournal;
import com.InfinityRaider.AgriCraft.tileentity.peripheral.TileEntityPeripheral;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorage;
import com.InfinityRaider.AgriCraft.tileentity.storage.TileEntitySeedStorageController;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{
    public static final int seedAnalyzerID = 1;
    public static final int journalID = 2;
    public static final int seedStorageID = 3;
    public static final int seedStorageControllerID = 4;
    public static final int peripheralID = 5;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
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
        TileEntity te = world.getTileEntity(x, y, z);
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
