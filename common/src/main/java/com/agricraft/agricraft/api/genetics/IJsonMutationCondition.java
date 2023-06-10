package com.agricraft.agricraft.api.genetics;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.crop.IAgriCrop;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Interface to which mutation conditions are parsed from jsons
 *
 * Note that the json condition does not directly give the result, as this depends on how the condition is
 * configured in the json
 */
public interface IJsonMutationCondition {

	/**
	 * Finds a registered mutation trigger factory from their id
	 *
	 * @param id the id
	 * @return optional containing the condition factory, or empty if no such condition is registered
	 */
	static Optional<Factory> getFactory(String id) {
		return AgriApi.getJsonMutationConditionFactory(id);
	}

	/**
	 * Tries to register a json mutation condition factory
	 *
	 * @param condition the factory for the condition to register
	 * @return true if successful (will fail in case a condition with the same id is already registered)
	 */
	static boolean registerFactory(Factory condition) {
		return AgriApi.registerJsonMutationCondition(condition);
	}

	/**
	 * Checks if this condition is fulfilled for a given crop where a given mutation would occur
	 *
	 * @param crop     the crop
	 * @param mutation the mutation
	 * @return true if the condition is fulfilled
	 */
	boolean isFulfilled(IAgriCrop crop, IAgriMutation mutation);

	/**
	 * Converts this json mutation condition into a usable IAgriMutation condition based on additional parameters
	 *
	 * @param isRequired            this defines if this condition must be met in order to allow the mutation
	 * @param guaranteedProbability this defines the probability ([0; 1]) that this condition will force the mutation to occur
	 * @return the condition
	 */
	default IAgriMutation.Condition convert(boolean isRequired, double guaranteedProbability) {
		return AgriApi.convertJsonMutationCondition(this, isRequired, guaranteedProbability);
	}

	/**
	 * Factory class to parse IJsonMutationCondition objects from json
	 */
	interface Factory {

		/**
		 * @return a unique ID for this condition factory
		 */
		String getId();

		/**
		 * Builds a condition based on the passed in json data
		 *
		 * @param json the json object to parse from
		 * @return a condition
		 * @throws JsonParseException in case an invalid json object was passed in
		 */
		IJsonMutationCondition parse(JsonObject json) throws JsonParseException;

		/**
		 * Utility method to register the factory
		 *
		 * @return the factory itself, or null if registration has failed
		 */
		@Nullable
		default IJsonMutationCondition.Factory register() {
			if (IJsonMutationCondition.registerFactory(this)) {
				return this;
			}
			return null;
		}

	}

}
