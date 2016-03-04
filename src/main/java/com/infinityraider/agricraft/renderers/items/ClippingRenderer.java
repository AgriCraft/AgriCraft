package com.infinityraider.agricraft.renderers.items;

import com.infinityraider.agricraft.farming.CropPlantHandler;
import com.infinityraider.agricraft.farming.cropplant.CropPlant;
import com.infinityraider.agricraft.init.AgriCraftItems;
import com.infinityraider.agricraft.items.ItemClipping;
import com.infinityraider.agricraft.renderers.RenderUtil;
import com.infinityraider.agricraft.renderers.TessellatorV2;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public final class ClippingRenderer extends AbstractItemRenderer {
	
	private static final ClippingRenderer INSTANCE = new ClippingRenderer();

	public static AbstractItemRenderer getInstance() {
		return INSTANCE;
	}

    @Override
    protected void renderItemDefault(TessellatorV2 tessellator, ItemStack clipping) {
		
		if (!(clipping.getItem() instanceof ItemClipping)) {
			// ... Really?
			return;
		}
		
		TextureAtlasSprite mainIcon = ((ItemClipping) AgriCraftItems.clipping).getIcon();
		
		RenderUtil.drawScaledPrism(tessellator, 0.1F, 0, 0, 0.1F, 16, 16, mainIcon);

        TextureAtlasSprite plantIcon = getPlantIcon(clipping);
        if(plantIcon != null) {
			RenderUtil.drawScaledPrism(tessellator, 0.05F, 4, 4, 0.15F, 12, 12, plantIcon);
        }
		
    }

    private TextureAtlasSprite getPlantIcon(ItemStack stack) {
        if(stack==null || stack.getItem()==null || stack.getTagCompound()==null) {
            return null;
        }
        ItemStack seed = ItemStack.loadItemStackFromNBT(stack.getTagCompound());
        CropPlant plant = CropPlantHandler.getPlantFromStack(seed);
        if(plant == null) {
            return null;
        }
        return plant.getPrimaryPlantTexture(7);
    }
	
}
