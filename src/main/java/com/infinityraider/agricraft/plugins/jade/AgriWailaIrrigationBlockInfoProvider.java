package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.agricraft.reference.AgriToolTips;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.IDataAccessor;
import mcp.mobius.waila.api.IPluginConfig;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;

public class AgriWailaIrrigationBlockInfoProvider implements IComponentProvider {
    private static final AgriWailaIrrigationBlockInfoProvider INSTANCE = new AgriWailaIrrigationBlockInfoProvider();

    public static AgriWailaIrrigationBlockInfoProvider getInstance() {
        return INSTANCE;
    }

    private AgriWailaIrrigationBlockInfoProvider() {}

    @Override
    public void appendBody(List<ITextComponent> tooltip, IDataAccessor accessor, IPluginConfig config) {
        TileEntity tile = accessor.getTileEntity();
        if(tile instanceof TileEntityIrrigationComponent) {
            if (tile instanceof TileEntityIrrigationChannel) {
                TileEntityIrrigationChannel channel = (TileEntityIrrigationChannel) tile;
                if (channel.hasValve()) {
                    if (channel.isOpen()) {
                        tooltip.add(AgriToolTips.VALVE_INFO_OPEN);
                    } else {
                        tooltip.add(AgriToolTips.VALVE_INFO_CLOSED);
                    }
                }
            }
            TileEntityIrrigationComponent component = (TileEntityIrrigationComponent) tile;
            tooltip.add(new StringTextComponent(component.getContent() + " / " + component.getCapacity() + " mB"));
        }
    }
}
