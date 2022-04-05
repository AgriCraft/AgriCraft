package com.infinityraider.agricraft.handler;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class MagnifyingGlassViewHandler {
    private static final MagnifyingGlassViewHandler INSTANCE = new MagnifyingGlassViewHandler();

    public static MagnifyingGlassViewHandler getInstance() {
        return INSTANCE;
    }

    private static final int ANIMATION_DURATION = 20;

    private static final double DX = 0.475;
    private static final double DY = -0.3125;
    private static final double DZ = 0.45;

    private static final double GENOME_OFFSET = 0.10;

    private static final float ANGLE = 45.0F;
    private static final Quaternion ROTATION_LEFT;
    private static final Quaternion ROTATION_RIGHT;

    private final Set<IMagnifyingGlassInspector> inspectors;
    private IMagnifyingGlassInspector inspector;
    //private BlockPos lastPos;
    private Target lastTarget;
    private boolean active = false;
    private InteractionHand hand = null;
    private int animationCounter = 0;
    private int animationCounterPrev = 0;

    private MagnifyingGlassViewHandler() {
        this.inspectors = Sets.newIdentityHashSet();
    }

    public void registerMagnifyingGlassInspector(IMagnifyingGlassInspector inspector) {
        this.inspectors.add(inspector);
    }

    protected void endInspection() {
        if(this.inspector != null && this.lastTarget != null) {
            this.lastTarget.onInspectionEnd(AgriCraft.instance.getClientWorld(), this.inspector, AgriCraft.instance.getClientPlayer());
        }
        this.inspector = null;
        this.lastTarget = null;
        MessageMagnifyingGlassObserving.sendToServer(this.getPlayer(), false);
    }

    public void toggle(InteractionHand hand) {
        if(this.isHandActive(hand)) {
            this.active = false;
            this.endInspection();
        } else {
            this.hand = hand;
            this.active = true;
        }
    }

    @Nullable
    public InteractionHand getActiveHand() {
        return this.hand;
    }

    public boolean isHandActive(@Nonnull InteractionHand hand) {
        return this.getActiveHand() == hand;
    }

    public boolean isActive() {
        return active;
    }

    protected Player getPlayer() {
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
        return Mth.lerp(partialTick, this.animationCounterPrev, this.animationCounter)/ANIMATION_DURATION;
    }

    protected void manipulateMatrixStackLeft(PoseStack stack, float partialTick) {
        if(this.isAnimationComplete(partialTick)) {
            stack.translate(DX, DY, DZ);
            stack.mulPose(ROTATION_LEFT);
        } else {
            float f = this.getAnimationProgress(partialTick);
            stack.translate(f*DX, f*DY, f*DZ);
            stack.mulPose(Vector3f.ZP.rotationDegrees(-f*ANGLE));
        }
    }

    protected void manipulateMatrixStackRight(PoseStack stack, float partialTick) {
        if(this.isAnimationComplete(partialTick)) {
            stack.translate(-DX, DY, DZ);
            stack.mulPose(ROTATION_RIGHT);
        } else {
            float f = this.getAnimationProgress(partialTick);
            stack.translate(-f*DX, f*DY, f*DZ);
            stack.mulPose(Vector3f.ZP.rotationDegrees(f*ANGLE));
        }
    }

    protected void checkInspectedPosition() {
        HitResult hit = Minecraft.getInstance().hitResult;
        Target target = Target.getTarget(hit);
        if(target == null) {
            this.endInspection();
            return;
        }
        if(target.isNewTarget(this.lastTarget)) {
                this.endInspection();
                MessageMagnifyingGlassObserving.sendToServer(this.getPlayer(), true);
                Level world = AgriCraft.instance.getClientWorld();
                Player player = AgriCraft.instance.getClientPlayer();
                this.inspectors.stream()
                        .filter(inspector -> target.canInspect(world, inspector, player))
                        .findAny()
                        .ifPresent(inspector -> {
                            this.lastTarget = target;
                            this.inspector = inspector;
                            this.lastTarget.onInspectionStart(world, inspector, player);
                        });
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void onRenderHand(RenderHandEvent event) {
        if(!(this.getPlayer().getItemInHand(event.getHand()).getItem() instanceof ItemMagnifyingGlass)) {
            return;
        }
        if(this.isHandActive(event.getHand())) {
            // Manipulate matrix stack
            HumanoidArm main = Minecraft.getInstance().options.mainHand;
            if (event.getHand() == InteractionHand.MAIN_HAND) {
                if (main == HumanoidArm.RIGHT) {
                    this.manipulateMatrixStackRight(event.getPoseStack(), event.getPartialTicks());
                } else {
                    this.manipulateMatrixStackLeft(event.getPoseStack(), event.getPartialTicks());
                }
            } else {
                if (main == HumanoidArm.LEFT) {
                    this.manipulateMatrixStackRight(event.getPoseStack(), event.getPartialTicks());
                } else {
                    this.manipulateMatrixStackLeft(event.getPoseStack(), event.getPartialTicks());
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings("unused")
    public void inspectionRender(RenderLevelLastEvent event) {
        // Check if the player is in first person
        if(!Minecraft.getInstance().options.getCameraType().isFirstPerson()) {
            return;
        }

        // Check if an inspection renderer is present and cache it (another thread might set it to null)
        IMagnifyingGlassInspector inspector = this.inspector;
        if(inspector == null) {
            return;
        }

        // Check if the inspector has a target and cache (another thread might set it to null)
        Target target = this.lastTarget;
        if(target == null) {
            return;
        }

        // Fetch and push matrix to the matrix stack
        PoseStack transforms = event.getPoseStack();
        transforms.pushPose();

        // Correct for render view
        Vec3 projectedView = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        // Move to the player's eye position
        Vec3 eyes = this.getPlayer().getEyePosition(event.getPartialTick());
        transforms.translate(eyes.x(), eyes.y(), eyes.z());

        // Fetch the player's target
        Vec3 hit = target.getTargetVector(event.getPartialTick());
        Vec3 view = hit.subtract(eyes).normalize();

        // Translate offset
        transforms.translate(GENOME_OFFSET*view.x(), GENOME_OFFSET*view.y(), GENOME_OFFSET*view.z());

        // Fetch player look orientation;
        float yaw = (float) (Math.PI*this.getPlayer().getViewYRot(event.getPartialTick()))/180;
        float pitch = (float) (Math.PI*this.getPlayer().getViewXRot(event.getPartialTick()))/180;

        // Rotate for yaw
        transforms.mulPose(Vector3f.YP.rotation(-yaw));

        // Render
        transforms.pushPose();
        inspector.doInspectionRender(transforms, event.getPartialTick(), target.getTargetEntity());
        transforms.popPose();

        // Pop last transformation matrix from the stack
        transforms.popPose();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        if(this.isActive()) {
            PlayerRenderer renderer = event.getRenderer();
            PlayerModel<AbstractClientPlayer> model = renderer.getModel();
            InteractionHand hand = this.getActiveHand();
            HumanoidArm side = Minecraft.getInstance().options.mainHand;
            if((hand == InteractionHand.MAIN_HAND && side == HumanoidArm.RIGHT) || (hand == InteractionHand.OFF_HAND && side == HumanoidArm.LEFT)) {
                model.rightArmPose = HumanoidModel.ArmPose.BLOCK;
                model.leftArmPose = HumanoidModel.ArmPose.EMPTY;
            } else if((hand == InteractionHand.MAIN_HAND && side == HumanoidArm.LEFT) || (hand == InteractionHand.OFF_HAND && side == HumanoidArm.RIGHT)) {
                model.leftArmPose = HumanoidModel.ArmPose.BLOCK;
                model.rightArmPose = HumanoidModel.ArmPose.EMPTY;
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        this.animationCounterPrev = this.animationCounter;
        if(this.isActive()) {
            // Check if the player is still holding the item and if not, deactivate
            if(this.getActiveHand() == null || !(this.getPlayer().getItemInHand(this.getActiveHand()).getItem() instanceof ItemMagnifyingGlass)) {
                this.active = false;
                this.endInspection();
                return;
            }
            // Increment animation counter
            if(this.animationCounter < ANIMATION_DURATION) {
                this.animationCounter += 1;
            } else {
                this.animationCounter = ANIMATION_DURATION;
            }
            if(this.isAnimationComplete()) {
                // Update inspected position
                this.checkInspectedPosition();
                // Tick inspector
                if(this.inspector != null && this.lastTarget != null) {
                    Level world = AgriCraft.instance.getClientWorld();
                    Player player = AgriCraft.instance.getClientPlayer();
                    if(!this.lastTarget.onInspectionTick(world, this.inspector, player)) {
                        this.endInspection();
                    }
                }
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
            // Notify the inspector of the scrolling
            if(this.inspector != null) {
                this.inspector.onMouseScroll((int) event.getScrollDelta());
            }
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onMouseClick(InputEvent.ClickInputEvent event) {
        if(this.isActive() ) {
            if(event.getKeyMapping() == Minecraft.getInstance().options.keyAttack) {
                // If this is active, we do not want left clicks to pass
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onMovementInput(MovementInputUpdateEvent event) {
        if(this.isActive()) {
            // Prevent player from moving and force sneak
            event.getInput().leftImpulse = 0;
            event.getInput().forwardImpulse = 0;
            event.getInput().up = false;
            event.getInput().down = false;
            event.getInput().left = false;
            event.getInput().right = false;
            event.getInput().jumping = false;
            event.getInput().shiftKeyDown = true;
        }
    }

    private static abstract class Target {
        @Nullable
        public static Target getTarget(HitResult hit) {
            if(hit == null) {
                return null;
            }
            if(hit.getType() == HitResult.Type.MISS) {
                return null;
            }
            if (hit instanceof EntityHitResult) {
                return new EntityTarget(((EntityHitResult) hit).getEntity());
            }
            if (hit instanceof BlockHitResult) {
                return new BlockTarget(((BlockHitResult) hit).getBlockPos());
            }
            return null;
        }

        public abstract boolean isNewTarget(@Nullable Target other);

        public abstract boolean canInspect(Level world, IMagnifyingGlassInspector inspector, Player player);

        public abstract void onInspectionStart(Level world, IMagnifyingGlassInspector inspector, Player player);

        public abstract boolean onInspectionTick(Level world, IMagnifyingGlassInspector inspector, Player player);

        public abstract void onInspectionEnd(Level world, IMagnifyingGlassInspector inspector, Player player);

        public abstract Vec3 getTargetVector(float partialTicks);

        @Nullable
        public abstract Entity getTargetEntity();

        private static class EntityTarget extends Target {
            private final Entity target;

            private EntityTarget(Entity entity) {
                this.target = entity;
            }

            @Override
            public boolean isNewTarget(@Nullable Target other) {
                if(other == this) {
                    return false;
                }
                if(other instanceof EntityTarget) {
                    return this.target != ((EntityTarget) other).target;
                }
                return true;
            }

            @Override
            public boolean canInspect(Level world, IMagnifyingGlassInspector inspector, Player player) {
                return this.target != null && inspector.canInspect(world, this.target, player);
            }

            @Override
            public void onInspectionStart(Level world, IMagnifyingGlassInspector inspector, Player player) {
                inspector.onInspectionStart(world, this.target, player);
            }

            @Override
            public boolean onInspectionTick(Level world, IMagnifyingGlassInspector inspector, Player player) {
                return this.target != null && inspector.onInspectionTick(world, this.target, player);
            }

            @Override
            public void onInspectionEnd(Level world, IMagnifyingGlassInspector inspector, Player player) {
                inspector.onInspectionEnd(world, this.target, player);
            }

            @Override
            public Vec3 getTargetVector(float partialTicks) {
                double dw = this.target.getBbWidth() / 2;
                double dh = this.target.getBbHeight() / 2;
                return new Vec3(
                        Mth.lerp(partialTicks, this.target.xOld, this.target.getX()) + dw,
                        Mth.lerp(partialTicks, this.target.yOld, this.target.getY()) + dh,
                        Mth.lerp(partialTicks, this.target.zOld, this.target.getZ()) + dw
                );
            }

            @Nullable
            @Override
            public Entity getTargetEntity() {
                return this.target;
            }
        }

        private static class BlockTarget extends Target {
            private final BlockPos target;

            private BlockTarget(BlockPos pos) {
                this.target = pos;
            }

            @Override
            public boolean isNewTarget(@Nullable Target other) {
                if(other == this) {
                    return false;
                }
                if(other instanceof BlockTarget) {
                    return !this.target.equals(((BlockTarget) other).target);
                }
                return true;
            }

            @Override
            public boolean canInspect(Level world, IMagnifyingGlassInspector inspector, Player player) {
                return inspector.canInspect(world, this.target, player);
            }

            @Override
            public void onInspectionStart(Level world, IMagnifyingGlassInspector inspector, Player player) {
                inspector.onInspectionStart(world, this.target, player);
            }

            @Override
            public boolean onInspectionTick(Level world, IMagnifyingGlassInspector inspector, Player player) {
                return inspector.onInspectionTick(world, this.target, player);
            }

            @Override
            public void onInspectionEnd(Level world, IMagnifyingGlassInspector inspector, Player player) {
                inspector.onInspectionEnd(world, this.target, player);
            }

            @Override
            public Vec3 getTargetVector(float partialTicks) {
                return new Vec3(
                        this.target.getX() + 0.5D,
                        this.target.getY() + 0.5D,
                        this.target.getZ() + 0.5D
                );
            }

            @Nullable
            @Override
            public Entity getTargetEntity() {
                return null;
            }
        }
    }

    static {
        ROTATION_RIGHT = Vector3f.ZP.rotationDegrees(ANGLE);
        ROTATION_LEFT = Vector3f.ZP.rotationDegrees(-ANGLE);
    }
}
