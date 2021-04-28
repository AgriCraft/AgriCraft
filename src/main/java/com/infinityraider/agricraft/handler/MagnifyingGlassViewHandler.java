package com.infinityraider.agricraft.handler;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenePair;
import com.infinityraider.agricraft.api.v1.genetics.IAgriGenome;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.infinityraider.agricraft.render.plant.AgriGenomeRenderer;
import com.infinityraider.agricraft.util.AnimatedScrollPosition;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class MagnifyingGlassViewHandler {
    private static final MagnifyingGlassViewHandler INSTANCE = new MagnifyingGlassViewHandler();

    public static MagnifyingGlassViewHandler getInstance() {
        return INSTANCE;
    }

    private static final int ANIMATION_DURATION = 20;
    private static final int SCROLL_DURATION = 10;

    private static final double DX = 0.475;
    private static final double DY = -0.3125;
    private static final double DZ = 0.45;

    private static final double GENOME_OFFSET = 0.15;

    private static final float ANGLE = 45.0F;
    private static final Quaternion ROTATION_LEFT;
    private static final Quaternion ROTATION_RIGHT;

    private boolean active = false;
    private Hand hand = null;
    private int animationCounter = 0;
    private int animationCounterPrev = 0;

    private final AnimatedScrollPosition scrollPosition;
    private BlockPos lastPos;
    private List<IAgriGenePair<?>> genomeCache;

    private MagnifyingGlassViewHandler() {
        this.scrollPosition = new AnimatedScrollPosition(() -> SCROLL_DURATION, () -> this.genomeCache == null ? 0 : this.genomeCache.size());
    }

    public void toggle(Hand hand) {
        if(this.isHandActive(hand)) {
            this.active = false;
            this.lastPos = null;
            this.genomeCache = null;
        } else {
            this.hand = hand;
            this.active = true;
        }
        this.scrollPosition.reset();
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
                ? this.getAnimationProgress(partialTicks) == 1.0
                : this.getAnimationProgress(partialTicks) == 0;
    }

    protected float getAnimationProgress(float partialTick) {
        return MathHelper.lerp(partialTick, this.animationCounterPrev, this.animationCounter)/ANIMATION_DURATION;
    }

    public int getScrollIndex() {
        return this.scrollPosition.getIndex();
    }

    public float getScrollProgress(float partialTick) {
        return this.scrollPosition.getProgress(partialTick);
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
    }

    protected void updateGeneCache() {
        RayTraceResult target = Minecraft.getInstance().objectMouseOver;
        if(target instanceof BlockRayTraceResult) {
            BlockRayTraceResult blockTarget = (BlockRayTraceResult) target;
            if(!blockTarget.getPos().equals(this.lastPos)) {
                World world = AgriCraft.instance.getClientWorld();
                this.lastPos = blockTarget.getPos();
                this.genomeCache = AgriApi.getCrop(world, lastPos)
                        .map(crop -> crop.getGenome().map(IAgriGenome::getGeneList).orElse(ImmutableList.of()))
                        .orElse(ImmutableList.of());
            }
        } else {
            this.lastPos = null;
            this.genomeCache = ImmutableList.of();
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRenderHand(RenderHandEvent event) {
        if(!(this.getPlayer().getHeldItem(event.getHand()).getItem() instanceof ItemMagnifyingGlass)) {
            return;
        }
        if(this.isHandActive(event.getHand())) {
            // Manipulate matrix stack
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

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void renderGenome(RenderWorldLastEvent event) {
        // Check if the genome can be rendered
        List<IAgriGenePair<?>> genome = this.genomeCache;
        if(genome == null || genome.size() <= 0) {
            return;
        }

        // Check if the player is in first person
        if(!Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a()) {
            return;
        }

        // Fetch and push matrix to the matrix stack
        MatrixStack transforms = event.getMatrixStack();
        transforms.push();

        // Correct for render view
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        // Move to the player's eye position
        Vector3d pos = this.getPlayer().getEyePosition(event.getPartialTicks());
        transforms.translate(pos.getX(), pos.getY(), pos.getZ());

        // Fetch player look orientation;
        float yaw = (float) (Math.PI*this.getPlayer().getYaw(event.getPartialTicks()))/180;
        float pitch = (float) (Math.PI*this.getPlayer().getPitch(event.getPartialTicks()))/180;

        // Rotate for yaw
        transforms.rotate(Vector3f.YP.rotation(-yaw));

        // Translate offset according to pitch
        transforms.translate(0, -GENOME_OFFSET*MathHelper.sin(pitch), GENOME_OFFSET*MathHelper.cos(pitch));

        // Scale down
        transforms.scale(0.125F, 0.125F, 0.125F);

        // helix dimensions
        float h = Constants.HALF;
        float r = h / 10;

        // Make sure the helix is centered
        transforms.translate(0, -h/2, 0);

        // render helix
        AgriGenomeRenderer.getInstance().renderDoubleHelix(
                genome, transforms, this.getScrollIndex(), this.getScrollProgress(event.getPartialTicks()), r, h, 1.0F, false);

        // Pop last transformation matrix from the stack
        transforms.pop();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        this.animationCounterPrev = this.animationCounter;
        if(this.isActive()) {
            // Increment animation counter
            if(this.animationCounter < ANIMATION_DURATION) {
                this.animationCounter += 1;
            } else {
                this.animationCounter = ANIMATION_DURATION;
            }
            if(this.isAnimationComplete()) {
                // Tick scroll animator
                this.scrollPosition.tick();
                // Update the gene cache
                this.updateGeneCache();
            }
        } else {
            // Decrement animation counter
            if(this.animationCounter > 0) {
                this.animationCounter -= 1;
            } else {
                this.animationCounter = 0;
                this.hand = null;
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        // Check if the handler is active
        if(this.isActive() && this.isAnimationComplete()) {
            // Tell the scroll animation object that the player has scrolled
            this.scrollPosition.scroll((int) event.getScrollDelta());
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMovementInput(InputUpdateEvent event) {
        if(this.isActive()) {
            // Prevent player from moving and force sneak
            event.getMovementInput().moveStrafe = 0;
            event.getMovementInput().moveForward = 0;
            event.getMovementInput().forwardKeyDown = false;
            event.getMovementInput().backKeyDown = false;
            event.getMovementInput().leftKeyDown = false;
            event.getMovementInput().rightKeyDown = false;
            event.getMovementInput().jump = false;
            event.getMovementInput().sneaking = true;
        }
    }

    static {
        ROTATION_RIGHT = Vector3f.ZP.rotationDegrees(ANGLE);
        ROTATION_LEFT = Vector3f.ZP.rotationDegrees(-ANGLE);
    }
}
