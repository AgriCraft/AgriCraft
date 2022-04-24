package com.infinityraider.agricraft.api.v1.genetics;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.plant.IJsonPlantCallback;

import javax.annotation.Nullable;
import java.util.Optional;

public interface IJsonMutationTrigger {
    /**
     * Finds a registered mutation trigger factory from their id
     *
     * @param id the id
     * @return optional containing the trigger, or empty if no such trigger is registered
     */
    static Optional<IJsonMutationTrigger.Factory> getTrigger(String id) {
        return AgriApi.getJsonMutationTrigger(id);
    }

    /**
     * Tries to register a json mutation trigger
     *
     * @param trigger the trigger factory to register
     * @return true if successful (will fail in case a trigger with the same id is already registered)
     */
    static boolean registerTrigger(IJsonMutationTrigger.Factory trigger) {
        return AgriApi.registerJsonMutationTrigger(trigger);
    }

    /**
     * Factory class to parse IJsonMutationTrigger objects from json
     */
    interface Factory {
        /**
         * @return a unique ID for this trigger factory
         */
        String getId();

        /**
         * Builds a callback based on the passed in json data
         *
         * @param json the json object to parse from
         * @return a trigger
         * @throws JsonParseException in case an invalid json object was passed in
         */
        IJsonPlantCallback makeTrigger(JsonObject json) throws JsonParseException;

        /**
         * Utility method to register the factory
         *
         * @return the factory itself, or null if registration has failed
         */
        @Nullable
        default IJsonMutationTrigger.Factory register() {
            if (IJsonMutationTrigger.registerTrigger(this)) {
                return this;
            }
            return null;
        }
    }
}
