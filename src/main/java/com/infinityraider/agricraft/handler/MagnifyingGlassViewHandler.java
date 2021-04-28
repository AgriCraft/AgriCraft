package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.infinityraider.infinitylib.render.IRenderUtilities;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
public class MagnifyingGlassViewHandler implements IRenderUtilities {
    private static final MagnifyingGlassViewHandler INSTANCE = new MagnifyingGlassViewHandler();

    public static MagnifyingGlassViewHandler getInstance() {
        return INSTANCE;
    }

    private static final int ANIMATION_DURATION = 20;

    private static final double DX = 0.475;
    private static final double DY = -0.3125;
    private static final double DZ = 0.45;

    private static final float ANGLE = 45.0F;
    private static final Quaternion ROTATION_LEFT;
    private static final Quaternion ROTATION_RIGHT;

    private boolean active = false;
    private Hand hand = null;
    private int animationCounter = 0;
    private int animationCounterPrev = 0;

    private MagnifyingGlassViewHandler() {}

    public void toggle(Hand hand) {
        if(this.isHandActive(hand)) {
            this.active = false;
        } else {
            this.hand = hand;
            this.active = true;
        }
        MessageMagnifyingGlassObserving.sendToServer(this.getPlayer(), this.isActive());
    }

    @Nullable
    public Hand getActiveHand() {
        return this.hand;
    }

    public boolean isHandActive(@Nonnull Hand hand) {
        return this.getActiveHand() == hand;
    }

    public boolean isActive() {
        return active;
    }

    protected PlayerEntity getPlayer() {
        return AgriCraft.instance.getClientPlayer();
    }

    public boolean isAnimationComplete() {
        return this.isAnimationComplete(0);
    }

    protected boolean isAnimationComplete(float partialTicks) {
        return this.isActive()
                ? this.getAnimationProgress(partialTicks) == ANIMATION_DURATION
                : this.getAnimationProgress(partialTicks) == 0;
    }

    protected float getAnimationProgress(float partialTick) {
        return MathHelper.lerp(partialTick, this.animationCounterPrev, this.animationCounter)/ANIMATION_DURATION;
    }

    protected void manipulateMatrixStackLeft(MatrixStack stack, float partialTick) {
        if(this.isAnimationComplete(partialTick)) {
            stack.translate(DX, DY, DZ);
            stack.rotate(ROTATION_LEFT);
        } else {
            float f = this.getAnimationProgress(partialTick);
            stack.translate(f*DX, f*DY, f*DZ);
            stack.rotate(Vector3f.ZP.rotationDegrees(-f*ANGLE));
        }
        this.renderCoordinateSystem(stack, this.getRenderTypeBuffer());
    }

    protected void manipulateMatrixStackRight(MatrixStack stack, float partialTick) {
        if(this.isAnimationComplete(partialTick)) {
            stack.translate(-DX, DY, DZ);
            stack.rotate(ROTATION_RIGHT);
        } else {
            float f = this.getAnimationProgress(partialTick);
            stack.translate(-f*DX, f*DY, f*DZ);
            stack.rotate(Vector3f.ZP.rotationDegrees(f*ANGLE));
        }
        this.renderCoordinateSystem(stack, this.getRenderTypeBuffer());
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRenderHand(RenderHandEvent event) {
        if(!(this.getPlayer().getHeldItem(event.getHand()).getItem() instanceof ItemMagnifyingGlass)) {
            return;
        }
        if(this.isHandActive(event.getHand())) {
            HandSide main = Minecraft.getInstance().gameSettings.mainHand;
            if (event.getHand() == Hand.MAIN_HAND) {
                if (main == HandSide.RIGHT) {
                    this.manipulateMatrixStackRight(event.getMatrixStack(), event.getPartialTicks());
                } else {
                    this.manipulateMatrixStackLeft(event.getMatrixStack(), event.getPartialTicks());
                }
            } else {
                if (main == HandSide.LEFT) {
                    this.manipulateMatrixStackRight(event.getMatrixStack(), event.getPartialTicks());
                } else {
                    this.manipulateMatrixStackLeft(event.getMatrixStack(), event.getPartialTicks());
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        this.animationCounterPrev = this.animationCounter;
        if(this.isActive()) {
            if(this.animationCounter < ANIMATION_DURATION) {
                this.animationCounter += 1;
            } else {
                this.animationCounter = ANIMATION_DURATION;
            }
        } else {
            if(this.animationCounter > 0) {
                this.animationCounter -= 1;
            } else {
                this.animationCounter = 0;
                this.hand = null;
            }
        }
    }

    static {
        ROTATION_RIGHT = Vector3f.ZP.rotationDegrees(ANGLE);
        ROTATION_LEFT = Vector3f.ZP.rotationDegrees(-ANGLE);
    }
}
