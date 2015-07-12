package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
public class PlayerEffectRendererNavi extends PlayerEffectRenderer {
    @Override
    ArrayList<String> getDisplayNames() {
        ArrayList<String> list = new ArrayList<String>();
        list.add("SkeletonPunk");
        return list;
    }

    @Override
    void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick) {

    }
}
