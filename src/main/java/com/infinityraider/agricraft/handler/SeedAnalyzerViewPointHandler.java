package com.infinityraider.agricraft.handler;

import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.infinitylib.modules.dynamiccamera.ModuleDynamicCamera;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.function.IntSupplier;

@OnlyIn(Dist.CLIENT)
public class SeedAnalyzerViewPointHandler {
    private static final SeedAnalyzerViewPointHandler INSTANCE = new SeedAnalyzerViewPointHandler();

    public static SeedAnalyzerViewPointHandler getInstance() {
        return INSTANCE;
    }

    /** Activity tracker  */
    private boolean active;

    /** Smooth scroll progress tracker */
    private int scrollDuration;
    private final AnimatedScrollPosition scrollPosition;

    private SeedAnalyzerViewPointHandler() {
        this.setScrollDuration(10);
        this.scrollPosition = new AnimatedScrollPosition(this::getScrollDuration, () -> AgriApi.getGeneRegistry().count());
    }
    public int getScrollDuration() {
        return this.scrollDuration;
    }

    public void setScrollDuration(int duration) {
        this.scrollDuration = duration;
    }

    public AnimatedScrollPosition getScrollPosition() {
        return this.scrollPosition;
    }

    public void setActive(boolean active) {
        this.active = active;
        this.scrollPosition.reset();
    }

    public boolean isActive() {
        return this.active;
    }

    public int getScrollIndex() {
        return this.getScrollPosition().getIndex();
    }

    public float getScrollProgress() {
        return this.getScrollPosition().getProgress();
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
                this.getScrollPosition().tick();
            }
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

    /**
     * Utility class which keeps track of the scroll position, and allows to interpolate for smooth transitions
     */
    public static class AnimatedScrollPosition {
        private final IntSupplier durationSupplier;
        private final IntSupplier maxSupplier;

        private int current;
        private int target;
        private int counter;

        public AnimatedScrollPosition(IntSupplier durationSupplier, IntSupplier maxSupplier) {
            this.durationSupplier = durationSupplier;
            this.maxSupplier = maxSupplier;
            this.reset();
        }

        public int getIndex() {
            return this.current;
        }

        public float getProgress() {
            return (this.target > this.current ? 1 : -1) * ((this.counter + 0.0F) / this.getDuration());
        }

        public int getDuration() {
            return this.durationSupplier.getAsInt();
        }

        public int getMax() {
            return this.maxSupplier.getAsInt();
        }

        public void tick() {
            if(this.current != this.target) {
                this.counter += 1;
                if(counter >= this.getDuration()) {
                    this.counter = 0;
                    this.current += (this.target > this.current ? 1 : -1);
                }
            } else {
                this.counter = 0;
            }
        }

        public void scroll(int delta) {
            this.target = Math.min(this.getMax(), Math.max(0, this.target - delta));
        }

        public void reset() {
            this.current = 0;
            this.target = 0;
            this.counter = 0;
        }
    }
}
