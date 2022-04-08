package com.infinityraider.agricraft.plugins.jade;

import mcp.mobius.waila.api.BlockAccessor;
import mcp.mobius.waila.api.IComponentProvider;
import mcp.mobius.waila.api.config.IPluginConfig;
import mcp.mobius.waila.api.ui.IElement;
import mcp.mobius.waila.api.ui.IElementHelper;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public abstract class AgriWailaBlockInfoProviderAbstract implements IComponentProvider {
    private final IElementHelper elementHelper;

    protected AgriWailaBlockInfoProviderAbstract(IElementHelper helper) {
        this.elementHelper = helper;
    }

    public final IElementHelper getElementHelper() {
        return this.elementHelper;
    }

    @Nullable
    public IElement getIcon(BlockAccessor accessor, IPluginConfig config, IElement currentIcon) {
        return this.getElementHelper().item(this.getStack(accessor));
    }

    public abstract ItemStack getStack(BlockAccessor accessor);

}
