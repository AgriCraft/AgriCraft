package com.infinityraider.agricraft.renderers.items;

public class RenderableItemRenderer extends AbstractItemRenderer {
	
	private static final RenderableItemRenderer INSTANCE = new RenderableItemRenderer();

	public static AbstractItemRenderer getInstance() {
		return INSTANCE;
	}
	
}
