package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.blocks.BlockCrop;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import net.minecraft.block.Block;
import net.minecraft.client.audio.ISound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.sound.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class SoundHandler {
    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onSoundPlayed(SoundEvent.SoundSourceEvent event) {
        if(!AgriCraftConfig.disableSounds) {
            return;
        }
        ISound sound = event.getSound();
        if(sound == null) {
            return;
        }
        World world = AgriCraft.proxy.getClientWorld();
        int x = (int) (sound.getXPosF() - 0.5F);
        int y = (int) (sound.getYPosF() - 0.5F);
        int z = (int) (sound.getZPosF() - 0.5F);
        if(world != null) {
            Block block = world.getBlockState(new BlockPos(x, y, z)).getBlock();
            if(block instanceof BlockCrop) {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }
}
