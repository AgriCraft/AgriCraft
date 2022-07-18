package com.infinityraider.agricraft.plugins.jade;

import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.content.core.BlockCrop;
import com.infinityraider.agricraft.reference.Names;
import mcp.mobius.waila.api.IRegistrar;
import mcp.mobius.waila.api.IWailaPlugin;
import mcp.mobius.waila.api.TooltipPosition;
import mcp.mobius.waila.api.WailaPlugin;
import net.minecraft.world.level.block.Block;

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
    @Deprecated
    @SuppressWarnings("deprecation")
    public void register(IRegistrar registrar) {
        AgriWailaCropBlockInfoProvider cropProvider = new AgriWailaCropBlockInfoProvider(registrar.getElementHelper());
        registrar.registerIconProvider(cropProvider, BlockCrop.class);
        registrar.registerComponentProvider(cropProvider, TooltipPosition.BODY, BlockCrop.class);
        registrar.registerComponentProvider(AgriWailaIrrigationBlockInfoProvider.getInstance(), TooltipPosition.BODY, Block.class);
    }
}
