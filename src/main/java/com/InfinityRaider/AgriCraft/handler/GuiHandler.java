package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.container.ContainerSeedAnalyzer;
import com.InfinityRaider.AgriCraft.gui.GuiJournal;
import com.InfinityRaider.AgriCraft.gui.GuiSeedAnalyzer;
import com.InfinityRaider.AgriCraft.tileentity.TileEntitySeedAnalyzer;
import cpw.mods.fml.common.network.IGuiHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class GuiHandler implements IGuiHandler{
    public static final int seedAnalyzerID = 1;
    public static final int journalID = 2;

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch(ID) {
            case(seedAnalyzerID):
                if(te != null && te instanceof TileEntitySeedAnalyzer) {
                    return new ContainerSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
                }
            case(journalID): break;
        }
        return null;
    }

    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        switch(ID) {
            case (seedAnalyzerID):
                if (te != null && te instanceof TileEntitySeedAnalyzer) {
                    return new GuiSeedAnalyzer(player.inventory, (TileEntitySeedAnalyzer) te);
                }
            case (journalID):
                return new GuiJournal(player);
            default: return null;
        }
    }
}
