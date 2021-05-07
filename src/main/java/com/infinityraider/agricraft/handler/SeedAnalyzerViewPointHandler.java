package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.content.core.TileEntitySeedAnalyzer;
import com.infinityraider.agricraft.util.AnimatedScrollPosition;
import com.infinityraider.agricraft.util.PlayerAngleLocker;
import com.infinityraider.infinitylib.modules.dynamiccamera.DynamicCamera;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.reference.Constants;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class SeedAnalyzerViewPointHandler {
    private static final SeedAnalyzerViewPointHandler INSTANCE = new SeedAnalyzerViewPointHandler();

    public static SeedAnalyzerViewPointHandler getInstance() {
        return INSTANCE;
    }

    /** Status flags  */
    private boolean active;
    private boolean observed;

    /** Smooth scroll progress tracker */
    private int scrollDuration;
    private final AnimatedScrollPosition scrollPosition;

    /** Seed animator helper */
    private final SeedAnimator seedAnimator;

    private SeedAnalyzerViewPointHandler() {
        this.setScrollDuration(10);
        this.scrollPosition = new AnimatedScrollPosition(this::getScrollDuration, () ->
                (int) AgriApi.getGeneRegistry().stream().filter(gene -> !gene.isHidden()).count());
        this.seedAnimator = new SeedAnimator();
    }

    public int getScrollDuration() {
        return this.scrollDuration;
    }

    public void setScrollDuration(int duration) {
        this.scrollDuration = duration;
    }

    protected AnimatedScrollPosition getScrollPosition() {
        return this.scrollPosition;
    }

    protected SeedAnimator getSeedAnimator() {
        return this.seedAnimator;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.getScrollPosition().reset();
        this.getSeedAnimator().onActivation(active);
        PlayerAngleLocker.storePlayerAngles();
    }

    public void setObserved(boolean observed) {
        this.observed = observed;
        this.getScrollPosition().reset();
    }

    public boolean isActive() {
        return this.active;
    }

    public boolean isObserved() {
        return this.observed;
    }

    public int getScrollIndex() {
        return this.getScrollPosition().getIndex();
    }

    public float getPartialScrollProgress(float partialTick) {
        return this.getScrollPosition().getProgress(partialTick);
    }

    public void applySeedAnimation(TileEntitySeedAnalyzer analyzer, float partialTicks, MatrixStack transforms) {
        this.getSeedAnimator().applyAnimation(analyzer, partialTicks, transforms);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // Check for movement inputs
            boolean up = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
            boolean down = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
            boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
            if(up || down || left || right) {
                // Stop observing
                ModuleDynamicCamera.getInstance().stopObserving();
            } else {
                // Tick the scroll position to increment its animation timer
                if(this.isObserved()) {
                    this.getScrollPosition().tick();
                }
            }
            // Force player orientation
            PlayerAngleLocker.forcePlayerAngles();
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // Tell the scroll animation object that the player has scrolled
            this.getScrollPosition().scroll((int) event.getScrollDelta());
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseClick(InputEvent.RawMouseEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // If this is active, we do not want any click behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMovement(InputUpdateEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // If this is active, we do not want any jumping or sneaking
            event.getMovementInput().sneaking = false;
            event.getMovementInput().jump = false;
        }
    }

    /**
     * Utility class which helps animate the seed in a seed analyzer
     */
    public static class SeedAnimator {
        private static final float HALF = Constants.HALF;

        private static final float DX = 0;
        private static final float DY = 0.25F - HALF;
        private static final float DZ = -1 * Constants.UNIT;

        private static final int PITCH = 90;
        private static final Quaternion ROTATION_PITCH = new Quaternion(Vector3f.XP, PITCH, true);

        private float angle;

        protected void onActivation(boolean status) {
            if(status) {
                // Keep track of the angle on which the analyzer was activated
                this.angle = this.calculateAngle();
            } else {
                // Reset
                this.angle = 0;
            }
        }

        protected void applyAnimation(TileEntitySeedAnalyzer analyzer, float partialTicks, MatrixStack transforms) {
            // translate to center of block
            transforms.translate(HALF, HALF, HALF);
            // animate according to observation status
            switch (this.getObservationStatus(analyzer)) {
                case IDLE:
                    this.applyIdleTransformation(transforms);
                    break;
                case POSITIONING:
                    this.applyPositioningTransformation(analyzer, partialTicks, transforms);
                    break;
                case OBSERVING:
                    this.applyObservingTransformation(analyzer, transforms);
                    break;
                case RETURNING:
                    this.applyReturningTransformation(analyzer, partialTicks, transforms);
                    break;
            }
            // scale down the seed
            transforms.scale(HALF, HALF, HALF);
        }

        protected void applyIdleTransformation(MatrixStack transforms) {
            // define rotation angle in function of system time
            transforms.rotate(new Quaternion(Vector3f.YP, this.calculateAngle(), true));
        }

        protected void applyPositioningTransformation(TileEntitySeedAnalyzer analyzer, float partialTicks, MatrixStack transforms) {
            // fetch animation progress
            float f = (this.getAnimationFrame(analyzer) + partialTicks)/analyzer.getTransitionDuration();
            // rotate yaw
            float yaw = MathHelper.interpolateAngle(f, this.angle, analyzer.getHorizontalAngle());
            transforms.rotate(new Quaternion(Vector3f.YP, yaw, true));
            // translate
            transforms.translate(MathHelper.lerp(f,0, DX), MathHelper.lerp(f,0, DY), MathHelper.lerp(f,0, DZ));
            // rotate pitch
            float pitch = MathHelper.lerp(f,0, PITCH);
            transforms.rotate(new Quaternion(Vector3f.XP, pitch, true));
        }

        protected void applyObservingTransformation(TileEntitySeedAnalyzer analyzer, MatrixStack transforms) {
            // rotate yaw
            transforms.rotate(new Quaternion(Vector3f.YP, analyzer.getHorizontalAngle(), true));
            // translate
            transforms.translate(DX, DY, DZ);
            // rotate pitch
            transforms.rotate(ROTATION_PITCH);
        }

        protected void applyReturningTransformation(TileEntitySeedAnalyzer analyzer, float partialTicks, MatrixStack transforms) {
            // fetch animation progress
            float f = (this.getAnimationFrame(analyzer) + partialTicks)/analyzer.getTransitionDuration();
            // rotate yaw
            float yaw = MathHelper.interpolateAngle(f, analyzer.getHorizontalAngle(), this.calculateAngle());
            transforms.rotate(new Quaternion(Vector3f.YP, yaw, true));
            // translate
            transforms.translate(MathHelper.lerp(f, DX, 0), MathHelper.lerp(f, DY, 0), MathHelper.lerp(f, DZ, 0));
            // rotate pitch
            float pitch = MathHelper.lerp(f,PITCH, 0);
            transforms.rotate(new Quaternion(Vector3f.XP, pitch, true));
        }

        protected DynamicCamera.Status getObservationStatus(TileEntitySeedAnalyzer analyzer) {
            if(analyzer.isObserved()) {
                return ModuleDynamicCamera.getInstance().getCameraStatus();
            }
            return DynamicCamera.Status.IDLE;
        }

        protected int getAnimationFrame(TileEntitySeedAnalyzer analyzer) {
            if(analyzer.isObserved()) {
                return ModuleDynamicCamera.getInstance().getCameraAnimationFrame();
            }
            return 0;
        }

        protected float calculateAngle() {
            return (float) (720.0 * (System.currentTimeMillis() & 0x3FFFL) / 0x3FFFL);
        }
    }
}
