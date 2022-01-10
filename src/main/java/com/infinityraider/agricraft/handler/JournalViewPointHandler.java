package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.render.items.journal.*;
import com.infinityraider.agricraft.util.PlayerAngleLocker;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.infinityraider.infinitylib.modules.playeranimations.IAnimatablePlayerModel;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.IngameMenuScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class JournalViewPointHandler implements IDynamicCameraController {
    private static final JournalViewPointHandler INSTANCE = new JournalViewPointHandler();

    private static final Map<ResourceLocation, IJournalDataDrawer<?>> PAGE_DRAWERS = Maps.newHashMap();

    private static final int OPENING_DURATION = 20;

    public static final double DX = 0.35;
    public static final double DY = -0.95;
    public static final double DZ = 0.425;

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
    private JournalClientData journal;

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
                this.journal = new JournalClientData(journal, hand);
            }
        } else {
            Hand other = hand == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND;
            if(!this.isActive(other)) {
                this.setActive(hand, true);
                if(AgriCraft.instance.proxy().toggleDynamicCamera(this, true)) {
                    this.journal = new JournalClientData(journal, hand);
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
        this.mainHandActive = status;
        if(status) {
            PlayerAngleLocker.storePlayerAngles();
        }
    }

    public void setMainHandActive(boolean status) {
        this.mainHandActive = status;
        if(status) {
            PlayerAngleLocker.storePlayerAngles();
        }
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
    public JournalClientData getJournalData() {
        return this.journal;
    }

    @Nullable
    public ItemStack getJournal() {
        return this.getJournalData() == null ? null : this.getJournalData().getJournalStack();
    }

    public float getOpeningProgress(float partialTicks) {
        return MathHelper.lerp(partialTicks, this.openingCounterPrev, this.openingCounter) / OPENING_DURATION;
    }

    public float getFlippingProgress(float partialTicks) {
        return this.getJournalData() == null ? 0 : this.getJournalData().getFlippingProgress(partialTicks);
    }

    public void renderViewedPageLeft(JournalRenderContextInHand context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getCurrentPage();
            getPageDrawer(page).drawLeftSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderViewedPageRight(JournalRenderContextInHand context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getCurrentPage();
            getPageDrawer(page).drawRightSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderFlippedPageLeft(JournalRenderContextInHand context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getFlippedPage();
            getPageDrawer(page).drawLeftSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderFlippedPageRight(JournalRenderContextInHand context, MatrixStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getFlippedPage();
            getPageDrawer(page).drawRightSheet(page, context, transforms, stack, journal);
        }
    }

    @Override
    public int getTransitionDuration() {
        return AgriCraft.instance.getConfig().journalAnimationDuration();
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
        double d = ((0.35/2)/Math.tan(0.5*Math.PI * fov / 360));
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
            // Check if a journal is still being held
            if(this.isOffHandActive()) {
                if(!(this.getObserver().getHeldItem(Hand.OFF_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setOffHandActive(false);
                    this.journal = null;
                    return;
                }
            } else if(this.isMainHandActive()) {
                if(!(this.getObserver().getHeldItem(Hand.MAIN_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setMainHandActive(false);
                    this.journal = null;
                    return;
                }
            }
            // Check for movement inputs
            boolean up = Minecraft.getInstance().gameSettings.keyBindForward.isKeyDown();
            boolean down = Minecraft.getInstance().gameSettings.keyBindBack.isKeyDown();
            boolean left = Minecraft.getInstance().gameSettings.keyBindLeft.isKeyDown();
            boolean right = Minecraft.getInstance().gameSettings.keyBindRight.isKeyDown();
            if (up || down || left || right) {
                // Stop observing
                ModuleDynamicCamera.getInstance().stopObserving();
            }
            // Force player orientation
            PlayerAngleLocker.forcePlayerAngles();
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

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMouseClick(InputEvent.ClickInputEvent event) {
        // Check if the handler is active
        if(this.isActive() && !event.isUseItem()) {
            // If this is active, we do not want any click behaviour (except right clicks)
            event.setSwingHand(false);
            event.setResult(Event.Result.DENY);
            event.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPlayerRender(RenderPlayerEvent.Pre event) {
        if(event.getPlayer() == AgriCraft.instance.getClientPlayer()) {
            if (event.getRenderer().getEntityModel() instanceof IAnimatablePlayerModel) {
                IAnimatablePlayerModel model = (IAnimatablePlayerModel) event.getRenderer().getEntityModel();
                model.setDoArmWobble(!this.isActive());
            }
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

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onGuiOpened(GuiOpenEvent event) {
        // Check if the handler is active
        if(this.isActive() && event.getGui() != null) {
            // Allow chatting
            if(event.getGui() instanceof ChatScreen) {
                return;
            }
            // Stop observing
            ModuleDynamicCamera.getInstance().stopObserving();
            // Cancel the event in case of pause
            if(event.getGui() instanceof IngameMenuScreen) {
                event.setResult(Event.Result.DENY);
                event.setCanceled(true);
            }
        }
    }

    // Static methods
    public static void registerJournalDataDrawer(IJournalDataDrawer<?> drawer) {
        PAGE_DRAWERS.put(drawer.getId(), drawer);
    }

    @SuppressWarnings("unchecked")
    public static  <P extends IAgriJournalItem.IPage> IJournalDataDrawer<P> getPageDrawer(P page) {
        return (IJournalDataDrawer<P>) PAGE_DRAWERS.getOrDefault(page.getDataDrawerId(), JournalDataDrawerMissing.INSTANCE);
    }

    static {
        registerJournalDataDrawer(new JournalDataDrawerFrontPage());
        registerJournalDataDrawer(new JournalDataDrawerGenetics());
        registerJournalDataDrawer(new JournalDataDrawerGrowthReqs());
        registerJournalDataDrawer(new JournalDataDrawerIntroduction());
        registerJournalDataDrawer(new JournalDataDrawerMutations());
        registerJournalDataDrawer(new JournalDataDrawerPlant());
    }
}
