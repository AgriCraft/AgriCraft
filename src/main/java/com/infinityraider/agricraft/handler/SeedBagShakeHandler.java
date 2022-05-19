package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.tools.ItemSeedBag;
import com.mojang.math.Vector3f;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class SeedBagShakeHandler {
    private static final SeedBagShakeHandler INSTANCE = new SeedBagShakeHandler();

    private static final int DURATION = 60;
    private static final int AMPLITUDE = 30;
    private static final float PERIOD = 20;

    public static SeedBagShakeHandler getInstance() {
        return INSTANCE;
    }

    private int counterMain;
    private int prevCounterMain;

    private int counterOff;
    private int prevCounterOff;

    private SeedBagShakeHandler() {}

    public void shake(InteractionHand hand) {
        if(hand == InteractionHand.MAIN_HAND) {
            this.counterMain = DURATION;
            this.prevCounterMain = DURATION;
        } else {
            this.counterOff = DURATION;
            this.prevCounterOff = DURATION;
        }
    }

    protected Player getPlayer() {
        return AgriCraft.instance.proxy().getClientPlayer();
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onTick(TickEvent.PlayerTickEvent event) {
        this.prevCounterMain = this.counterMain;
        if(this.counterMain > 0) {
            this.counterMain -= 1;
        }
        this.prevCounterOff = this.counterOff;
        if(this.counterOff > 0) {
            this.counterOff -= 1;
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRenderHand(RenderHandEvent event) {
        if(!(this.getPlayer().getItemInHand(event.getHand()).getItem() instanceof ItemSeedBag)) {
            return;
        }
        if(event.getHand() == InteractionHand.MAIN_HAND) {
            float fraction = AMPLITUDE - Mth.lerp(event.getPartialTicks(), this.prevCounterMain, this.counterMain);
            float angle = AMPLITUDE*Mth.sin((float) (fraction*2*Math.PI/PERIOD));
            event.getPoseStack().mulPose(Vector3f.ZP.rotationDegrees(angle));
        } else {
            float fraction = AMPLITUDE - Mth.lerp(event.getPartialTicks(), this.prevCounterOff, this.counterOff);
            float angle = AMPLITUDE*Mth.sin((float) (fraction*2*Math.PI/PERIOD));
            event.getPoseStack().mulPose(Vector3f.ZP.rotationDegrees(angle));
        }
    }
}
