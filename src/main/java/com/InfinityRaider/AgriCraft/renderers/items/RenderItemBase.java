package com.InfinityRaider.AgriCraft.renderers.items;

import com.InfinityRaider.AgriCraft.renderers.RenderUtil;
import com.InfinityRaider.AgriCraft.renderers.TessellatorV2;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.BlockRendererDispatcherWrapped;
import com.InfinityRaider.AgriCraft.renderers.renderinghacks.IItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.HashMap;

@SideOnly(Side.CLIENT)
public abstract class RenderItemBase implements IItemRenderer {
    private static HashMap<Item, RenderItemBase> renderers = new HashMap<>();
    protected static RenderUtil renderUtil = RenderUtil.getInstance();

    protected RenderItemBase(Item item) {
        if(!renderers.containsKey(item)) {
            renderers.put(item, this);
        }
        BlockRendererDispatcherWrapped.getInstance().registerItemRenderingHandler(item, this);
    }

    @SuppressWarnings("deprecation")
    public final void renderItem(ItemStack stack, ItemCameraTransforms.TransformType transformType) {
        TessellatorV2 tessellator = TessellatorV2.getInstance(Tessellator.getInstance());
        switch(transformType) {
            case NONE:
                renderItemDefault(tessellator, stack);
                break;
            case THIRD_PERSON:
                renderItemThirdPerson(tessellator, stack);
                break;
            case FIRST_PERSON:
                renderItemFirstPerson(tessellator, stack);
                break;
            case HEAD:
                renderItemHead(tessellator, stack);
                break;
            case GUI:
                renderItemGui(tessellator, stack);
                break;
            case GROUND:
                renderItemGround(tessellator, stack);
                break;
            case FIXED:
                renderItemFixed(tessellator, stack);
                break;

        }
    }

    protected abstract void renderItemDefault(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemThirdPerson(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemFirstPerson(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemHead(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemGui(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemGround(TessellatorV2 tessellator, ItemStack item);

    protected abstract void renderItemFixed(TessellatorV2 tessellator, ItemStack item);
}

