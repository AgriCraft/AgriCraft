package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.content.core.BlockCropPlant;
import com.infinityraider.agricraft.reference.Names;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.block.Block;

@WailaPlugin
@SuppressWarnings("unused")
public class JadePlugin implements IAgriPlugin, IWailaPlugin {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getId() {
        return Names.Mods.JADE;
    }

    @Override
    public String getDescription() {
        return "Jade compatibility";
    }

    @Override
    public void register(IRegistrar registrar) {
        registrar.registerStackProvider(AgriWailaCropBlockInfoProvider.getInstance(), BlockCropPlant.class);
        registrar.registerComponentProvider(AgriWailaCropBlockInfoProvider.getInstance(), TooltipPosition.HEAD, BlockCropPlant.class);
        registrar.registerComponentProvider(AgriWailaCropBlockInfoProvider.getInstance(), TooltipPosition.BODY, Block.class);
        registrar.registerComponentProvider(AgriWailaIrrigationBlockInfoProvider.getInstance(), TooltipPosition.BODY, Block.class);
    }
}
