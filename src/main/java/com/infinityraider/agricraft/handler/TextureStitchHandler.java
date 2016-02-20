package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.utility.icon.IconRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;
import com.infinityraider.agricraft.api.v1.IAgriCraftRenderable;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TextureStitchHandler {
    public List<IAgriCraftRenderable> getIconRegisterables() {
        List<IAgriCraftRenderable> list = new ArrayList<>();
        for (Block block : Block.blockRegistry) {
            if (block instanceof IAgriCraftRenderable) {
                list.add((IAgriCraftRenderable) block);
            }
        }
        for (Item item : Item.itemRegistry) {
            if (item instanceof IAgriCraftRenderable) {
                list.add((IAgriCraftRenderable) item);
            }
        }
        return list;
    }


    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {
        for (IAgriCraftRenderable iconRegisterable : getIconRegisterables()) {
            iconRegisterable.registerIcons(IconRegistrar.getInstance());
        }
    }
}
