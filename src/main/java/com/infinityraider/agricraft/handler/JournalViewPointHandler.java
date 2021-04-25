package com.infinityraider.agricraft.handler;

import com.google.common.collect.ImmutableList;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.items.IAgriJournalItem;
import com.infinityraider.agricraft.api.v1.plant.IAgriPlant;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class JournalViewPointHandler implements IDynamicCameraController {
    private static final JournalViewPointHandler INSTANCE = new JournalViewPointHandler();

    public static final int TRANSITION_DURATION = 10;
    private static final int OPENING_DURATION = 20;
    private static final int FLIPPING_DURATION = 20;

    public static final double DX = 0.35;
    public static final double DY = -1.0;
    public static final double DZ = 0.45;

    public static final float PITCH = 80.0F;
    public static final float YAW = 0.0F;


    public static JournalViewPointHandler getInstance() {
        return INSTANCE;
    }

    /** Status flags  */
    private boolean offHandActive;
    private boolean mainHandActive;
    private boolean observed;

    /** Observer data */
    private Vector3d observerStart;
    private Vector3d cameraPosition;
    private Vector2f cameraOrientation;
    private float yawOffset;

    /** Journal data */
    private JournalData journal;

    /** Animation counter */
    private int openingCounter;
    private int openingCounterPrev;

    private JournalViewPointHandler() {}

    public boolean toggle(ItemStack journal, Hand hand) {
        if(this.isActive(hand)) {
            this.setActive(hand, false);
            if(AgriCraft.instance.proxy().toggleDynamicCamera(this, false)) {
                this.journal = null;
                return true;
            } else {
                this.setActive(hand, true);
                this.journal = new JournalData(journal);
            }
        } else {
            Hand other = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if(!this.isActive(other)) {
                this.setActive(hand, true);
                if(AgriCraft.instance.proxy().toggleDynamicCamera(this, true)) {
                    this.journal = new JournalData(journal);
                    return true;
                } else {
                    this.setActive(hand, false);
                    this.journal = null;
                }
            }
        }
        return false;
    }

    public void setActive(Hand hand, boolean status) {
        if(hand == Hand.MAIN_HAND) {
            this.setMainHandActive(status);
        } else {
            this.setOffHandActive(status);
        }
    }

    public void setOffHandActive(boolean status) {
        this.offHandActive = status;
    }

    public void setMainHandActive(boolean status) {
        this.mainHandActive = status;
    }

    public boolean isActive() {
        return this.isMainHandActive() || this.isOffHandActive();
    }

    public boolean isActive(Hand hand) {
        return hand == Hand.MAIN_HAND ? this.isMainHandActive() : this.isOffHandActive();
    }

    public boolean isActive(HandSide hand) {
        HandSide main = Minecraft.getInstance().gameSettings.mainHand;
        if(main == hand) {
            return this.isMainHandActive();
        } else {
            return this.isOffHandActive();
        }
    }

    public boolean isMainHandActive() {
        return this.mainHandActive;
    }

    public boolean isOffHandActive() {
        return this.offHandActive;
    }

    public boolean isObserved() {
        return this.observed;
    }

    protected PlayerEntity getObserver() {
        return AgriCraft.instance.getClientPlayer();
    }

    @Nullable
    public JournalData getJournalData() {
        return this.journal;
    }

    @Nullable
    public ItemStack getJournal() {
        return this.getJournalData() == null ? null : this.getJournalData().getJournal();
    }

    public float getOpeningProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.openingCounterPrev, this.openingCounter) / OPENING_DURATION;
    }

    public float getFlippingProgress(float partialTicks) {
        return this.getJournalData() == null ? 0 : this.getJournalData().getFlippingProgress(partialTicks);
    }

    public void renderViewedPageLeft(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        if(this.getJournalData() != null) {
            this.getJournalData().getCurrentPage().drawLeftSheet(transforms, buffer, light, overlay);
        }
    }

    public void renderViewedPageRight(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        if(this.getJournalData() != null) {
            this.getJournalData().getCurrentPage().drawRightSheet(transforms, buffer, light, overlay);
        }
    }

    public void renderFlippedPageLeft(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        if(this.getJournalData() != null) {
            this.getJournalData().getFlippedPage().drawLeftSheet(transforms, buffer, light, overlay);
        }
    }

    public void renderFlippedPageRight(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
        if(this.getJournalData() != null) {
            this.getJournalData().getFlippedPage().drawRightSheet(transforms, buffer, light, overlay);
        }
    }

    @Override
    public int getTransitionDuration() {
        return TRANSITION_DURATION;
    }

    @Override
    public void onCameraActivated() {
        this.observerStart = this.getObserver().getPositionVec();
    }

    @Override
    public void onObservationStart() {
        this.observed = true;
    }

    @Override
    public void onObservationEnd() {
        this.observed = false;
    }

    @Override
    public void onCameraDeactivated() {
        this.setMainHandActive(false);
        this.setOffHandActive(false);
        this.observerStart = null;
        this.cameraPosition = null;
        this.cameraOrientation = null;
        this.journal = null;
    }

    @Override
    public boolean shouldContinueObserving() {
        return this.observerStart != null && this.observerStart.equals(this.getObserver().getPositionVec());
    }

    @Override
    public Vector3d getObserverPosition() {
        if(this.cameraPosition == null || this.yawOffset != this.getObserver().renderYawOffset) {
            this.cameraPosition = this.calculateObserverPosition(AgriCraft.instance.proxy().getFieldOfView());
        }
        return this.cameraPosition;
    }

    protected Vector3d calculateObserverPosition(double fov) {
        // calculate offset based on fov
        double d = 0.75 * (0.5/Math.tan(Math.PI * fov / 360));
        // Determine x, y and z offsets
        double dx, dz;
        double dy = DY + d*MathHelper.sin((float) Math.PI * PITCH / 180);
        double dHor = d*MathHelper.cos((float) Math.PI * PITCH / 180);
        float yaw = this.getObserverOrientation().y;
        double cosYaw = MathHelper.cos((float) Math.PI * yaw / 180);
        double sinYaw = MathHelper.sin((float) Math.PI * yaw / 180);
        if (this.isActive(HandSide.RIGHT)) {
            dx = (dHor * cosYaw) - (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) - (DX * sinYaw) + (DZ * cosYaw);
        } else {
            dx = (dHor * cosYaw) + (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) + (DX * sinYaw) + (DZ * cosYaw);
        }
        return this.getObserver().getPositionVec().add(dx, dy + this.getObserver().getEyeHeight(), dz);
    }

    @Override
    public Vector2f getObserverOrientation() {
        if(this.cameraOrientation == null || this.yawOffset != this.getObserver().renderYawOffset) {
            this.cameraOrientation = this.calculateObserverOrientation();
        }
        return this.cameraOrientation;
    }

    protected Vector2f calculateObserverOrientation() {
        this.yawOffset = this.getObserver().renderYawOffset;
        float yaw = this.yawOffset;
        if (this.isActive(HandSide.RIGHT)) {
            yaw += YAW;
        } else if (this.isActive(HandSide.LEFT)) {
            yaw -= YAW;
        }
        return new Vector2f(PITCH, yaw);
    }

    @Override
    public void onFieldOfViewChanged(float fov) {
        this.cameraPosition = this.calculateObserverPosition(fov);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // Check if the handler is active
        if (this.isActive()) {
            // Check for movement inputs
            boolean up = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
            boolean down = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
            boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
            if (up || down || left || right) {
                // Stop observing
                ModuleDynamicCamera.getInstance().stopObserving();
            }
        }
        // Tick animation timer
        this.openingCounterPrev = this.openingCounter;
        if (this.isObserved()) {
            // Tick the book opening counter to open
            this.openingCounter = (this.openingCounter < OPENING_DURATION) ? this.openingCounter + 1 : OPENING_DURATION;
        } else {
            // Tick the book opening counter to close
            this.openingCounter = (this.openingCounter > 0) ? this.openingCounter - 1 : 0;
        }
        // Tick the scroll position to increment its animation timer
        if(this.getJournalData() != null) {
            this.getJournalData().tick();
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseScroll(InputEvent.MouseScrollEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // Tell the journal data that the player has scrolled, and a page must be flipped
            if(this.getJournalData() != null) {
                if(event.getScrollDelta() < 0) {
                    this.getJournalData().incrementPage();
                } else if(event.getScrollDelta() > 0) {
                    this.getJournalData().decrementPage();
                }
            }
            // If this is active, we do not want any other scroll behaviour
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    public static class JournalData {
        private final ItemStack journal;
        private final List<Page> pages;

        private int page;
        private int target;

        private int animationCounter;
        private int prevAnimationCounter;

        protected JournalData(ItemStack journalStack) {
            this.journal = journalStack;
            ImmutableList.Builder<Page> builder = new ImmutableList.Builder<>();
            builder.add(FRONT_PAGE);
            if(journalStack.getItem() instanceof IAgriJournalItem) {
                IAgriJournalItem journalItem = (IAgriJournalItem) journalStack.getItem();
                journalItem.getDiscoveredSeeds(journalStack).stream()
                        .map(JournalData::plantPage)
                        .forEach(builder::add);
            }
            this.pages = builder.build();
        }

        public ItemStack getJournal() {
            return this.journal;
        }

        public Page getCurrentPage() {
            if(this.target >= this.page) {
                return this.pages.get(this.page);
            } else {
                return this.pages.get(this.page - 1);
            }
        }

        public Page getFlippedPage() {
            if(this.target > this.page) {
                return this.pages.get(this.page + 1);
            }else  {
                return this.pages.get(this.page);
            }
        }

        public void incrementPage() {
            this.target = Math.min(this.pages.size() - 1, this.target + 1);
        }

        public void decrementPage() {
            this.target = Math.max(0, this.target - 1);
        }

        public void tick() {
            this.prevAnimationCounter = this.animationCounter;
            if(this.target > this.page) {
                if(this.animationCounter == 0) {
                    this.animationCounter = FLIPPING_DURATION;
                    this.prevAnimationCounter = this.animationCounter;
                }
                this.animationCounter -= 1;
                if(this.animationCounter <= 0) {
                    this.animationCounter = 0;
                    this.page += 1;
                }
            } else if(this.target < this.page) {
                if(this.animationCounter == 0) {
                    this.animationCounter = -FLIPPING_DURATION;
                    this.prevAnimationCounter = this.animationCounter;
                }
                this.animationCounter += 1;
                if(this.animationCounter >= 0) {
                    this.animationCounter = 0;
                    this.page -= 1;
                }
            }
        }

        public float getFlippingProgress(float partialTicks) {
            return MathHelper.lerp(partialTicks, this.prevAnimationCounter, this.animationCounter)/FLIPPING_DURATION;
        }

        public static abstract class Page {
            public abstract void drawLeftSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay);

            public abstract void drawRightSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay);
        }

        public static final Page FRONT_PAGE = new Page() {
            @Override
            public void drawLeftSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
                // Draw nothing
            }

            @Override
            public void drawRightSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
                // TODO: Draw front page sprite
            }
        };

        public static Page plantPage(IAgriPlant plant) {
            return new Page() {
                @Override
                public void drawLeftSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
                    // TODO
                }

                @Override
                public void drawRightSheet(MatrixStack transforms, IRenderTypeBuffer.Impl buffer, int light, int overlay) {
                    // TODO
                }
            };
        }
    }
}
