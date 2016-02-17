package com.infinityraider.agricraft.items;

import com.infinityraider.agricraft.renderers.items.RenderableItemRenderer;
import com.infinityraider.agricraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
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
