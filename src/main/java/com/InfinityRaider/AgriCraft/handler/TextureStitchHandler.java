package com.InfinityRaider.AgriCraft.handler;

import com.InfinityRaider.AgriCraft.api.v1.IconRegisterable;
import com.InfinityRaider.AgriCraft.utility.icon.IconRegistrar;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameData;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SideOnly(Side.CLIENT)
@SuppressWarnings("unused")
public class TextureStitchHandler {
    public List<IconRegisterable> getIconRegisterables() {
        List<IconRegisterable> list = new ArrayList<>();
        for (Block block : Block.blockRegistry) {
            if (block instanceof IconRegisterable) {
                list.add((IconRegisterable) block);
            }
        }
        for (Item item : Item.itemRegistry) {
            if (item instanceof IconRegisterable) {
                list.add((IconRegisterable) item);
            }
        }
        return list;
    }


    @SubscribeEvent
    public void stitchTextures(TextureStitchEvent.Pre event) {
        for (IconRegisterable iconRegisterable : getIconRegisterables()) {
            iconRegisterable.registerIcons(IconRegistrar.getInstance());
        }
    }
}
