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
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
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
import net.minecraftforge.client.event.*;
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

    private static final double GENOME_OFFSET = 0.10;
    private static final float GENOME_SCALE = 0.075F;
    private static final double TEXT_OFFSET = -0.025;
    private static final float TEXT_SCALE = 0.75F;
    private static final Quaternion TEXT_FLIPPER = Vector3f.ZP.rotationDegrees(180);

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
                this.scrollPosition.reset();
                this.lastPos = blockTarget.getPos();
                this.genomeCache = AgriApi.getCrop(world, lastPos)
                        .map(crop -> crop.getGenome().map(IAgriGenome::getGeneList).orElse(ImmutableList.of()))
                        .orElse(ImmutableList.of());
            }
        } else {
            this.lastPos = null;
            this.genomeCache = ImmutableList.of();
            this.scrollPosition.reset();
        }
    }

    protected void renderDoubleHelix(List<IAgriGenePair<?>> genome, MatrixStack transforms, float partialTicks) {
        // Push matrix for helix render
        transforms.push();

        // helix dimensions
        float h = Constants.HALF;
        float r = h / 10;

        // Scale down
        transforms.scale(GENOME_SCALE, GENOME_SCALE, GENOME_SCALE);

        // Make sure the helix is centered
        transforms.translate(0, -h/2, 0);

        // Render helix
        AgriGenomeRenderer.getInstance().renderDoubleHelix(
                genome, transforms, this.getScrollIndex(), this.getScrollProgress(partialTicks), r, h, 1.0F, false);

        // Pop matrix after helix render
        transforms.pop();
    }

    protected void renderTextOverlay(List<IAgriGenePair<?>> genome, MatrixStack transforms) {
        // Push matrix for overlay render
        transforms.push();

        // Translate down
        transforms.translate(0, TEXT_OFFSET, 0);

        // Flip text
        transforms.rotate(TEXT_FLIPPER);

        // Scale down
        float width = AgriGenomeRenderer.getInstance().getScaledWindowWidth();
        float height = AgriGenomeRenderer.getInstance().getScaledWindowHeight();
        float scale = TEXT_SCALE/Math.max(width, height);
        transforms.scale(scale, scale, scale);

        // Render overlay
        int index = Math.max(0, Math.min(genome.size() - 1, this.getScrollIndex()));
        AgriGenomeRenderer.getInstance().renderTextOverlay(transforms, genome.get(index));

        // Pop matrix for overlay render
        transforms.pop();
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

        // Check if the player is targeting something
        if(this.lastPos == null) {
            return;
        }

        // Fetch and push matrix to the matrix stack
        MatrixStack transforms = event.getMatrixStack();
        transforms.push();

        // Correct for render view
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        // Move to the player's eye position
        Vector3d eyes = this.getPlayer().getEyePosition(event.getPartialTicks());
        transforms.translate(eyes.getX(), eyes.getY(), eyes.getZ());

        // Fetch the player's target
        Vector3d hit = new Vector3d(this.lastPos.getX() + 0.5D, this.lastPos.getY() + 0.5D, this.lastPos.getZ() + 0.5D);
        Vector3d view = hit.subtract(eyes).normalize();

        // Translate offset
        transforms.translate(GENOME_OFFSET*view.getX(), GENOME_OFFSET*view.getY(), GENOME_OFFSET*view.getZ());

        // Fetch player look orientation;
        float yaw = (float) (Math.PI*this.getPlayer().getYaw(event.getPartialTicks()))/180;
        float pitch = (float) (Math.PI*this.getPlayer().getPitch(event.getPartialTicks()))/180;

        // Rotate for yaw
        transforms.rotate(Vector3f.YP.rotation(-yaw));

        // Render helix
        this.renderDoubleHelix(genome, transforms, event.getPartialTicks());

        // Render text overlay
        this.renderTextOverlay(genome, transforms);

        // Pop last transformation matrix from the stack
        transforms.pop();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        if(this.isActive()) {
            PlayerRenderer renderer = event.getRenderer();
            PlayerModel<AbstractClientPlayerEntity> model = renderer.getEntityModel();
            Hand hand = this.getActiveHand();
            HandSide side = Minecraft.getInstance().gameSettings.mainHand;
            if((hand == Hand.MAIN_HAND && side == HandSide.RIGHT) || (hand == Hand.OFF_HAND && side == HandSide.LEFT)) {
                model.rightArmPose = BipedModel.ArmPose.BLOCK;
                model.leftArmPose = BipedModel.ArmPose.EMPTY;
            } else if((hand == Hand.MAIN_HAND && side == HandSide.LEFT) || (hand == Hand.OFF_HAND && side == HandSide.RIGHT)) {
                model.leftArmPose = BipedModel.ArmPose.BLOCK;
                model.rightArmPose = BipedModel.ArmPose.EMPTY;
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        this.animationCounterPrev = this.animationCounter;
        if(this.isActive()) {
            // Check if the player is still holding the item and if not, deactivate
            if(this.getActiveHand() == null || !(this.getPlayer().getHeldItem(this.getActiveHand()).getItem() instanceof ItemMagnifyingGlass)) {
                this.active = false;
                this.lastPos = null;
                this.genomeCache = null;
                this.scrollPosition.reset();
                return;
            }
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
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClick(InputEvent.ClickInputEvent event) {
        if(this.isActive() ) {
            if(event.getKeyBinding() == Minecraft.getInstance().gameSettings.keyBindAttack) {
                // If this is active, we do not want left clicks to pass
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
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
