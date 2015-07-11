package com.InfinityRaider.AgriCraft.renderers.player;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
abstract class PlayerEffectRenderer {
    abstract ArrayList<String> getDisplayNames();

    abstract void renderEffects(EntityPlayer player, RenderPlayer renderer, float tick);
}
