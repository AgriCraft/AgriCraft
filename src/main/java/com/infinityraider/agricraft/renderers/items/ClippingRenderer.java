package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.farming.cropplant.CropPlant;
import com.InfinityRaider.AgriCraft.init.AgriCraftItems;
import com.InfinityRaider.AgriCraft.items.ItemClipping;
import com.InfinityRaider.AgriCraft.renderers.RenderUtil;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.GL11;

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

        tessellator.startDrawingQuads();
		
		RenderUtil.drawScaledPrism(tessellator, 0, 0, 0, 0, 16, 16, mainIcon);

        tessellator.draw();

        TextureAtlasSprite plantIcon = getPlantIcon(clipping);
        if(plantIcon != null) {
			
            Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.locationBlocksTexture);

            tessellator.startDrawingQuads();

			RenderUtil.drawScaledPrism(tessellator, 0, 4, 4, 0, 12, 12, plantIcon);

            tessellator.draw();

        }
    }

    @Override
    protected void renderItemGround(TessellatorV2 tessellator, ItemStack stack) {
        GL11.glRotatef(180, 0, 1, 0);
        GL11.glTranslatef(-0.5F, 0, 0);
        renderItemDefault(tessellator, stack);
        GL11.glTranslatef(0.5F, 0, 0);
        GL11.glRotatef(-180, 0, 1, 0);
    }

    @Override
    protected void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack stack) {
        float dx = 0F;
        float dz = 0.5F;
        GL11.glTranslatef(dx, 0, dz);
        renderItemDefault(tessellator, stack);
        GL11.glTranslatef(-dx, 0, -dz);
    }

    @Override
    protected void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack stack) {
        float a = 45;
        float dx = -0.5F;
        float dy = 0;
        float dz = 1;
        float scale = 1.5F;

        GL11.glRotatef(a, 0, 1, 0);
        GL11.glTranslatef(dx, dy, dz);
        GL11.glScalef(scale, scale, scale);

        renderItemDefault(tessellator, stack);

        GL11.glScalef(1 / scale, 1 / scale, 1 / scale);
        GL11.glTranslatef(-dx, -dy, -dz);
        GL11.glRotatef(-a, 0, 1, 0);
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
