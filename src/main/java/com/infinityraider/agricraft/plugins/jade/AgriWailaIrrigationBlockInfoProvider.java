package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationChannel;
import com.infinityraider.agricraft.content.irrigation.TileEntityIrrigationComponent;
import com.infinityraider.agricraft.reference.AgriToolTips;
import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.config.IPluginConfig;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.level.block.entity.BlockEntity;

public class AgriWailaIrrigationBlockInfoProvider implements IComponentProvider {
    private static final AgriWailaIrrigationBlockInfoProvider INSTANCE = new AgriWailaIrrigationBlockInfoProvider();

    public static AgriWailaIrrigationBlockInfoProvider getInstance() {
        return INSTANCE;
    }

    private AgriWailaIrrigationBlockInfoProvider() {}

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig config) {
        BlockEntity tile = accessor.getBlockEntity();
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
            tooltip.add(new TextComponent(component.getContent() + " / " + component.getCapacity() + " mB"));
        }
    }
}
