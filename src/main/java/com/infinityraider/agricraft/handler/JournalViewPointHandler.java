package com.infinityraider.agricraft.handler;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.client.IJournalDataDrawer;
import com.infinityraider.agricraft.api.v1.content.items.IAgriJournalItem;
import com.infinityraider.agricraft.render.items.journal.*;
import com.infinityraider.agricraft.util.PlayerAngleLocker;
import com.infinityraider.infinitylib.modules.dynamiccamera.IDynamicCameraController;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.MovementInputUpdateEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.ScreenOpenEvent;
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
    private Vec3 observerStart;
    private Vec3 cameraPosition;
    private Vec2 cameraOrientation;
    private float yawOffset;

    /** Journal data */
    private JournalClientData journal;

    /** Animation counter */
    private int openingCounter;
    private int openingCounterPrev;

    private JournalViewPointHandler() {}

    public boolean toggle(Player player, InteractionHand hand) {
        if(this.isActive(hand)) {
            this.setActive(hand, false);
            if(AgriCraft.instance.proxy().toggleDynamicCamera(this, false)) {
                this.journal = null;
                return true;
            } else {
                this.setActive(hand, true);
                this.journal = new JournalClientData(player, hand);
            }
        } else {
            InteractionHand other = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if(!this.isActive(other)) {
                this.setActive(hand, true);
                if(AgriCraft.instance.proxy().toggleDynamicCamera(this, true)) {
                    this.journal = new JournalClientData(player, hand);
                    return true;
                } else {
                    this.setActive(hand, false);
                    this.journal = null;
                }
            }
        }
        return false;
    }

    public void setActive(InteractionHand hand, boolean status) {
        if(hand == InteractionHand.MAIN_HAND) {
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

    public boolean isActive(InteractionHand hand) {
        return hand == InteractionHand.MAIN_HAND ? this.isMainHandActive() : this.isOffHandActive();
    }

    public boolean isActive(HumanoidArm hand) {
        HumanoidArm main = Minecraft.getInstance().options.mainHand;
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

    protected Player getObserver() {
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
        return Mth.lerp(partialTicks, this.openingCounterPrev, this.openingCounter) / OPENING_DURATION;
    }

    public float getFlippingProgress(float partialTicks) {
        return this.getJournalData() == null ? 0 : this.getJournalData().getFlippingProgress(partialTicks);
    }

    public void renderViewedPageLeft(JournalRenderContextInHand context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getCurrentPage();
            getPageDrawer(page).drawLeftSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderViewedPageRight(JournalRenderContextInHand context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getCurrentPage();
            getPageDrawer(page).drawRightSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderFlippedPageLeft(JournalRenderContextInHand context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
        if(this.getJournalData() != null) {
            IAgriJournalItem.IPage page = this.getJournalData().getFlippedPage();
            getPageDrawer(page).drawLeftSheet(page, context, transforms, stack, journal);
        }
    }

    public void renderFlippedPageRight(JournalRenderContextInHand context, PoseStack transforms, ItemStack stack, IAgriJournalItem journal) {
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
        this.observerStart = this.getObserver().position();
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
        return this.observerStart != null && this.observerStart.equals(this.getObserver().position());
    }

    @Override
    public Vec3 getObserverPosition() {
        if(this.cameraPosition == null || this.yawOffset != this.getObserver().yBodyRot) {
            this.cameraPosition = this.calculateObserverPosition(AgriCraft.instance.proxy().getFieldOfView());
        }
        return this.cameraPosition;
    }

    protected Vec3 calculateObserverPosition(double fov) {
        // calculate offset based on fov
        double d = ((0.35/2)/Math.tan(0.5*Math.PI * fov / 360));
        // Determine x, y and z offsets
        double dx, dz;
        double dy = DY + d*Mth.sin((float) Math.PI * PITCH / 180);
        double dHor = d*Mth.cos((float) Math.PI * PITCH / 180);
        float yaw = this.getObserverOrientation().y;
        double cosYaw = Mth.cos((float) Math.PI * yaw / 180);
        double sinYaw = Mth.sin((float) Math.PI * yaw / 180);
        if (this.isActive(HumanoidArm.RIGHT)) {
            dx = (dHor * cosYaw) - (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) - (DX * sinYaw) + (DZ * cosYaw);
        } else {
            dx = (dHor * cosYaw) + (DX * cosYaw) - (DZ * sinYaw);
            dz = (dHor * sinYaw) + (DX * sinYaw) + (DZ * cosYaw);
        }
        return this.getObserver().position().add(dx, dy + this.getObserver().getEyeHeight(), dz);
    }

    @Override
    public Vec2 getObserverOrientation() {
        if(this.cameraOrientation == null || this.yawOffset != this.getObserver().yBodyRot) {
            this.cameraOrientation = this.calculateObserverOrientation();
        }
        return this.cameraOrientation;
    }

    protected Vec2 calculateObserverOrientation() {
        this.yawOffset = this.getObserver().yBodyRot;
        float yaw = this.yawOffset;
        if (this.isActive(HumanoidArm.RIGHT)) {
            yaw += YAW;
        } else if (this.isActive(HumanoidArm.LEFT)) {
            yaw -= YAW;
        }
        return new Vec2(PITCH, yaw);
    }

    @Override
    public void onFieldOfViewChanged(double fov) {
        this.cameraPosition = this.calculateObserverPosition(fov);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        // This must only run on the client thread
        if(!event.player.getLevel().isClientSide()) {
            return;
        }
        // Check if the handler is active
        if (this.isActive()) {
            // Check if a journal is still being held
            if(this.isOffHandActive()) {
                if(!(this.getObserver().getItemInHand(InteractionHand.OFF_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setOffHandActive(false);
                    this.journal = null;
                    return;
                }
            } else if(this.isMainHandActive()) {
                if(!(this.getObserver().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IAgriJournalItem)) {
                    AgriCraft.instance.proxy().toggleDynamicCamera(this, false);
                    this.setMainHandActive(false);
                    this.journal = null;
                    return;
                }
            }
            // Check for movement inputs
            boolean up = Minecraft.getInstance().options.keyUp.isDown();
            boolean down = Minecraft.getInstance().options.keyDown.isDown();
            boolean left = Minecraft.getInstance().options.keyLeft.isDown();
            boolean right = Minecraft.getInstance().options.keyRight.isDown();
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
            // TODO: arm wobble
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onMovement(MovementInputUpdateEvent event) {
        // Check if the handler is active
        if(this.isActive()) {
            // If this is active, we do not want any jumping or sneaking
            event.getInput().shiftKeyDown = false;
            event.getInput().jumping = false;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent(priority = EventPriority.HIGHEST, receiveCanceled = true)
    public void onGuiOpened(ScreenOpenEvent event) {
        // Check if the handler is active
        if(this.isActive() && event.getScreen() != null) {
            // Allow chatting
            if(event.getScreen() instanceof ChatScreen) {
                return;
            }
            // Stop observing
            ModuleDynamicCamera.getInstance().stopObserving();
            // Cancel the event in case of pause
            if(event.getScreen() instanceof PauseScreen) {
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
