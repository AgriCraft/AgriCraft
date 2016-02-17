package com.infinityraider.agricraft.renderers.renderinghacks;

import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public interface IItemRenderer {
    boolean shouldRender3D(ItemStack stack);

    void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType);
}
