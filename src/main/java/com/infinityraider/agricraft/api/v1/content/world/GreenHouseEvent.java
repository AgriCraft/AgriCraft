package com.infinityraider.agricraft.api.v1.content.world;

import net.minecraftforge.eventbus.api.Event;

/**
 * Fired when the state of a greenhouse changes, this can be when it is created, removed, or its state modified.
 * Only fired on the server thread.
 *
 * None of these events are Cancellable
 * All these events are fired on the MinecraftForge EVENT_BUS
 */
public abstract class GreenHouseEvent extends Event {
    private final IAgriGreenHouse greenHouse;

    protected GreenHouseEvent(IAgriGreenHouse greenHouse) {
        this.greenHouse = greenHouse;
    }

    /**
     * @return the greenhouse in question
     */
    public final IAgriGreenHouse getGreenHouse() {
        return this.greenHouse;
    }

    /**
     * Fired when a greenhouse is created
     */
    public static final class Created extends GreenHouseEvent {
        public Created(IAgriGreenHouse greenHouse) {
            super(greenHouse);
        }
    }

    /**
     * Fired when a greenhouse is updated
     */
    public static final class Updated extends GreenHouseEvent {
        public Updated(IAgriGreenHouse greenHouse) {
            super(greenHouse);
        }
    }

    /**
     * Fired when a greenhouse is removed
     */
    public static final class Removed extends GreenHouseEvent {
        public Removed(IAgriGreenHouse greenHouse) {
            super(greenHouse);
        }
    }
}
