package com.InfinityRaider.AgriCraft.apiimpl.v2;

import com.InfinityRaider.AgriCraft.api.APIStatus;
import com.InfinityRaider.AgriCraft.api.v2.APIv2;
import com.InfinityRaider.AgriCraft.api.v2.ICrop;
import com.InfinityRaider.AgriCraft.api.v2.IStatCalculator;
import com.InfinityRaider.AgriCraft.api.v2.IStatStringDisplayer;
import com.InfinityRaider.AgriCraft.apiimpl.v1.APIimplv1;
import com.InfinityRaider.AgriCraft.farming.mutation.statcalculator.StatCalculator;
import com.InfinityRaider.AgriCraft.utility.statstringdisplayer.StatStringDisplayer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class APIimplv2 extends APIimplv1 implements APIv2 {
    public APIimplv2(int version, APIStatus status) {
        super(version, status);
    }

    @Override
    public ICrop getCrop(World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(x, y, z);
        if(te instanceof ICrop) {
            return (ICrop) te;
        }
        return null;
    }

    @Override
    public void setStatCalculator(IStatCalculator calculator) {
        StatCalculator.setStatCalculator(calculator);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void setStatStringDisplayer(IStatStringDisplayer displayer) {
        StatStringDisplayer.setStatStringDisplayer(displayer);
    }
}
