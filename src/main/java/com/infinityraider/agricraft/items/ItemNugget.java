package com.InfinityRaider.AgriCraft.items;

import com.InfinityRaider.AgriCraft.renderers.items.RenderableItemRenderer;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import net.minecraft.creativetab.CreativeTabs;

public class ItemNugget extends ItemBase {

    public ItemNugget(String name) {
        super("nugget"+name);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }
	
	@Override
	public void registerItemRenderer() {
        BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(this, RenderableItemRenderer.getInstance());
    }
	
}
