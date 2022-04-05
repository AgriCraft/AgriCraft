package com.infinityraider.agricraft.handler;

import com.google.common.collect.Sets;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IMagnifyingGlassInspector;
import com.infinityraider.agricraft.content.tools.ItemMagnifyingGlass;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.InteractionHand;
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
    private Hand hand = null;
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

    protected void checkInspectedPosition() {
        RayTraceResult hit = Minecraft.getInstance().objectMouseOver;
        Target target = Target.getTarget(hit);
        if(target == null) {
            this.endInspection();
            return;
        }
        if(target.isNewTarget(this.lastTarget)) {
                this.endInspection();
                MessageMagnifyingGlassObserving.sendToServer(this.getPlayer(), true);
                World world = AgriCraft.instance.getClientWorld();
                PlayerEntity player = AgriCraft.instance.getClientPlayer();
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
    public void inspectionRender(RenderWorldLastEvent event) {
        // Check if the player is in first person
        if(!Minecraft.getInstance().gameSettings.getPointOfView().func_243192_a()) {
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
        MatrixStack transforms = event.getMatrixStack();
        transforms.push();

        // Correct for render view
        Vector3d projectedView = Minecraft.getInstance().gameRenderer.getActiveRenderInfo().getProjectedView();
        transforms.translate(-projectedView.x, -projectedView.y, -projectedView.z);

        // Move to the player's eye position
        Vector3d eyes = this.getPlayer().getEyePosition(event.getPartialTicks());
        transforms.translate(eyes.getX(), eyes.getY(), eyes.getZ());

        // Fetch the player's target
        Vector3d hit = target.getTargetVector(event.getPartialTicks());
        Vector3d view = hit.subtract(eyes).normalize();

        // Translate offset
        transforms.translate(GENOME_OFFSET*view.getX(), GENOME_OFFSET*view.getY(), GENOME_OFFSET*view.getZ());

        // Fetch player look orientation;
        float yaw = (float) (Math.PI*this.getPlayer().getYaw(event.getPartialTicks()))/180;
        float pitch = (float) (Math.PI*this.getPlayer().getPitch(event.getPartialTicks()))/180;

        // Rotate for yaw
        transforms.rotate(Vector3f.YP.rotation(-yaw));

        // Render
        transforms.push();
        inspector.doInspectionRender(transforms, event.getPartialTicks(), target.getTargetEntity());
        transforms.pop();

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
                    World world = AgriCraft.instance.getClientWorld();
                    PlayerEntity player = AgriCraft.instance.getClientPlayer();
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

    private static abstract class Target {
        @Nullable
        public static Target getTarget(RayTraceResult hit) {
            if(hit == null) {
                return null;
            }
            if(hit.getType() == RayTraceResult.Type.MISS) {
                return null;
            }
            if (hit instanceof EntityRayTraceResult) {
                return new EntityTarget(((EntityRayTraceResult) hit).getEntity());
            }
            if (hit instanceof BlockRayTraceResult) {
                return new BlockTarget(((BlockRayTraceResult) hit).getPos());
            }
            return null;
        }

        public abstract boolean isNewTarget(@Nullable Target other);

        public abstract boolean canInspect(World world, IMagnifyingGlassInspector inspector, PlayerEntity player);

        public abstract void onInspectionStart(World world, IMagnifyingGlassInspector inspector, PlayerEntity player);

        public abstract boolean onInspectionTick(World world, IMagnifyingGlassInspector inspector, PlayerEntity player);

        public abstract void onInspectionEnd(World world, IMagnifyingGlassInspector inspector, PlayerEntity player);

        public abstract Vector3d getTargetVector(float partialTicks);

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
            public boolean canInspect(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                return this.target != null && inspector.canInspect(world, this.target, player);
            }

            @Override
            public void onInspectionStart(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                inspector.onInspectionStart(world, this.target, player);
            }

            @Override
            public boolean onInspectionTick(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                return this.target != null && inspector.onInspectionTick(world, this.target, player);
            }

            @Override
            public void onInspectionEnd(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                inspector.onInspectionEnd(world, this.target, player);
            }

            @Override
            public Vector3d getTargetVector(float partialTicks) {
                double dw = this.target.getWidth() / 2;
                double dh = this.target.getHeight() / 2;
                return new Vector3d(
                        MathHelper.lerp(partialTicks, this.target.prevPosX, this.target.getPosX()) + dw,
                        MathHelper.lerp(partialTicks, this.target.prevPosY, this.target.getPosY()) + dh,
                        MathHelper.lerp(partialTicks, this.target.prevPosZ, this.target.getPosZ()) + dw
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
            public boolean canInspect(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                return inspector.canInspect(world, this.target, player);
            }

            @Override
            public void onInspectionStart(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                inspector.onInspectionStart(world, this.target, player);
            }

            @Override
            public boolean onInspectionTick(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                return inspector.onInspectionTick(world, this.target, player);
            }

            @Override
            public void onInspectionEnd(World world, IMagnifyingGlassInspector inspector, PlayerEntity player) {
                inspector.onInspectionEnd(world, this.target, player);
            }

            @Override
            public Vector3d getTargetVector(float partialTicks) {
                return new Vector3d(
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
