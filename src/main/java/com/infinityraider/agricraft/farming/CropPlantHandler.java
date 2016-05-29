package com.infinityraider.agricraft.farming;

import com.infinityraider.agricraft.compatibility.CompatibilityHandler;
import com.infinityraider.agricraft.farming.cropplant.*;
import com.infinityraider.agricraft.farming.growthrequirement.GrowthRequirementHandler;
import com.infinityraider.agricraft.farming.mutation.Mutation;
import com.infinityraider.agricraft.handler.config.AgriCraftConfig;
import com.infinityraider.agricraft.reference.Constants;
import com.infinityraider.agricraft.reference.AgriCraftNBT;
import com.infinityraider.agricraft.utility.IOHelper;
import com.agricraft.agricore.core.AgriCore;
import com.infinityraider.agricraft.api.v1.IGrowthRequirement;
import com.infinityraider.agricraft.utility.exception.DuplicateCropPlantException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import java.util.*;
import java.util.stream.Collectors;
import com.infinityraider.agricraft.api.v1.IAgriCraftPlant;
import com.infinityraider.agricraft.items.ItemAgriCraftSeed;

public class CropPlantHandler {

	/**
	 * None object to avoid NPE's with block states
	 */
	public static final IAgriCraftPlant NONE = CropPlantNone.NONE;
	/**
	 * HashMap containing all plants known to AgriCraft
	 */
	private static final HashMap<String, IAgriCraftPlant> plants = new HashMap<>();
	/**
	 * Queue to store plants registered via the API before the cropPlants
	 * HashMap has been initialized
	 */
	private static ArrayList<IAgriCraftPlant> plantsToRegister = new ArrayList<>();

	/**
	 * Registers the plant into the cropPlants HashMap.
	 *
	 * @param plant the plant to be registered.
	 * @throws DuplicateCropPlantException thrown if the plant has already been
	 * registered. This could signal a major issue.
	 */
	public static void registerPlant(IAgriCraftPlant plant) throws DuplicateCropPlantException {
		AgriCore.getLogger("AgriCraft").debug("Registering plant: " + plant.getPlantName());
		if (!plants.containsKey(plant.getPlantName())) {
			plants.put(plant.getPlantName(), plant);
		} else {
			throw new DuplicateCropPlantException();
		}
	}

	/**
	 * Registers the plant into the cropPlants HashMap, and automatically
	 * catches DuplicateCropPlantExceptions and BlacklistedCropPlantExeptions.
	 *
	 * This method replaces the many switch statements previously found in the
	 * init() function. In doing so we cut back on code duplication.
	 * Furthermore, this is a targeted switch statement, instead of a catch all,
	 * allowing exceptions that should not have been thrown to surface properly.
	 *
	 * This function is a wrapper for the registerPlant() function.
	 *
	 * @see #registerPlant(CropPlant)
	 *
	 * @param plant the plant to be registered.
	 */
	private static void suppressedRegisterPlant(IAgriCraftPlant plant) {
		try {
			registerPlant(plant);
			GrowthRequirementHandler.addSoil(plant.getGrowthRequirement().getSoil());
		} catch (DuplicateCropPlantException e) {
			AgriCore.getLogger("AgriCraft").debug("Unable to register duplicate plant: " + plant.getPlantName());
			AgriCore.getLogger("AgriCraft").trace(e);
		}
	}

	/**
	 * Adds a crop to the registration queue, to be registered during the
	 * initialization phase.
	 *
	 * @param plant the plant to be registered.
	 */
	public static void addCropToRegister(IAgriCraftPlant plant) {
		if (plantsToRegister != null) {
			plantsToRegister.add(plant);
		}
	}

	public static boolean isAnalyzedSeed(ItemStack seedStack) {
		return isValidSeed(seedStack)
				&& (seedStack.hasTagCompound())
				&& (seedStack.getTagCompound().hasKey(AgriCraftNBT.ANALYZED))
				&& (seedStack.getTagCompound().getBoolean(AgriCraftNBT.ANALYZED));
	}

	/**
	 * Tests to see if the provided stack is a valid {@link #plants SEED}.
	 *
	 * @param seed the stack to test as a SEED.
	 * @return if the stack is a valid SEED.
	 */
	public static boolean isValidSeed(ItemStack seed) {
		return getPlantFromStack(seed) != null;
	}

	/**
	 * Writes the plant (or SEED) to an NBTTag.
	 *
	 * @see #readPlantFromNBT(NBTTagCompound)
	 *
	 * @param plant the plant (or SEED) to write to an NBTTag.
	 * @return a NBTTagCompound, the serialized representation of the plant.
	 */
	public static NBTTagCompound writePlantToNBT(IAgriCraftPlant plant) {
		final NBTTagCompound tag = new NBTTagCompound();
		tag.setString(AgriCraftNBT.ID, plant.getPlantName());
		return tag;
	}

	/**
	 * Reads a plant (a.k.a SEED) from an NBTTag.
	 *
	 * @see #writePlantToNBT(CropPlant)
	 *
	 * @param tag the serialized version of the plant.
	 * @return the deserialized plant.
	 */
	public static IAgriCraftPlant readPlantFromNBT(NBTTagCompound tag) {
		if (tag != null) {
			return plants.get(tag.getString(AgriCraftNBT.ID));
		} else {
			return null;
		}
	}

	/**
	 * Retrieves the {@link #plants plant} from a stack.
	 *
	 * @param stack the stack (possibly) containing the SEED to retrieve.
	 * @return the plant in the stack, or null, if the stack does not contain a
	 * valid plant.
	 */
	public static IAgriCraftPlant getPlantFromStack(ItemStack stack) {
		if (stack != null) {
			return readPlantFromNBT(stack.getTagCompound());
		} else {
			return null;
		}
	}

	public static IGrowthRequirement getGrowthRequirement(ItemStack stack) {
		final IAgriCraftPlant plant = getPlantFromStack(stack);
		return plant == null ? GrowthRequirementHandler.NULL : plant.getGrowthRequirement();
	}

	/**
	 * Retrieves a list of registered plants.
	 *
	 * @return the registered plants, taken from the internal HashMap, and
	 * placed into an ArrayList.
	 */
	public static List<IAgriCraftPlant> getPlants() {
		return new ArrayList<>(plants.values());
	}
	
	public static IAgriCraftPlant getPlant(String id) {
		return plants.get(id);
	}
	
	public static List<String> getPlantIds() {
		return new ArrayList<>(plants.keySet());
	}
	
	public static ItemStack getSeed(IAgriCraftPlant plant) {
		if(plant != null) {
		ItemStack stack = new ItemStack(ItemAgriCraftSeed.getInstance());
		stack.setTagCompound(CropPlantHandler.writePlantToNBT(plant));
		return stack;
		} else {
			return null;
		}
	}

	/**
	 * Retrieves a list of registered plants, with tier equal to or below the
	 * provided tier.
	 *
	 * @param tier the inclusive tier cap.
	 * @return the registered plants within the provided range.
	 */
	public static List<IAgriCraftPlant> getPlantsUpToTier(int tier) {
		List<IAgriCraftPlant> result = new ArrayList<>(plants.values().size());
		for (IAgriCraftPlant plant : plants.values()) {
			if (plant.getTier() <= tier) {
				result.add(plant);
			}
		}
		return result;
	}

	/**
	 * Gets a random SEED which is recognized as a plant by AgriCraft
	 *
	 * @param rand Random object to be used
	 * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG
	 * containing random stats
	 * @return an ItemStack containing a random SEED
	 */
	public static ItemStack getRandomSeed(Random rand, boolean setTag) {
		return getRandomSeed(rand, setTag, 5);
	}

	/**
	 * Gets a random SEED which is recognized as a plant by AgriCraft
	 *
	 * @param rand Random object to be used
	 * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG
	 * containing random stats
	 * @param maxTier The maximum tier of the SEED (inclusive)
	 * @return an ItemStack containing a random SEED
	 */
	public static ItemStack getRandomSeed(Random rand, boolean setTag, int maxTier) {
		return getRandomSeed(rand, setTag, CropPlantHandler.getPlantsUpToTier(maxTier));
	}

	/**
	 * Gets a random SEED from a list of plants
	 *
	 * @param rand Random object to be used
	 * @param setTag If the SEED should be initialized with an AgriCraftNBT TAG
	 * containing random stats
	 * @param plants List of plants to grab a random SEED from
	 * @return an ItemStack containing a random SEED
	 */
	public static ItemStack getRandomSeed(Random rand, boolean setTag, List<IAgriCraftPlant> plants) {
		boolean flag = false;
		ItemStack seed = null;
		while (!flag) {
			IAgriCraftPlant plant = plants.get(rand.nextInt(plants.size()));
			seed = plant.getSeed().copy();
			flag = (seed.getItem() != null);
		}
		if (setTag) {
			NBTTagCompound tag = new NBTTagCompound();
			setSeedNBT(tag, (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), (short) (rand.nextInt(AgriCraftConfig.cropStatCap) / 2 + 1), false);
			seed.setTagCompound(tag);
		}
		return seed;
	}

	/**
	 * Sets the AgriCraftNBT TAG for a SEED to have stats, this method modifies
	 * the NBTTagCompound it is given to add the needed data and then returns it
	 * again
	 *
	 * @param tag the AgriCraftNBT TAG of the ItemStack, is returned again
	 * @param growth the GROWTH stat
	 * @param gain the GAIN stat
	 * @param strength the STRENGTH stat
	 * @param analyzed if the SEED is ANALYZED
	 * @return the AgriCraftNBT TAG
	 */
	public static NBTTagCompound setSeedNBT(NBTTagCompound tag, short growth, short gain, short strength, boolean analyzed) {
		short cap = (short) AgriCraftConfig.cropStatCap;
		tag.setShort(AgriCraftNBT.GROWTH, growth == 0 ? Constants.DEFAULT_GROWTH : growth > cap ? cap : growth);
		tag.setShort(AgriCraftNBT.GAIN, gain == 0 ? Constants.DEFAULT_GAIN : gain > cap ? cap : gain);
		tag.setShort(AgriCraftNBT.STRENGTH, strength == 0 ? Constants.DEFAULT_GAIN : strength > cap ? cap : strength);
		tag.setBoolean(AgriCraftNBT.ANALYZED, analyzed);
		return tag;
	}

	public static List<Mutation> getDefaultMutations() {
		List<Mutation> list = new ArrayList<>();
		for (IAgriCraftPlant plant : getPlants()) {
			list.addAll(plant.getDefaultMutations().stream().map(Mutation::new).collect(Collectors.toList()));
		}
		return list;
	}

	/**
	 * The primary plant initialization function.
	 *
	 * Begins by initializing vanilla plants, then moves to mod plants, followed
	 * by plants in the plantsToRegister queue. Finally initializes plants found
	 * in the ore dictionary.
	 */
	public static void init() {
		//Register vanilla plants. Now with less duplication.

		//Register crops specified through the API.
		plantsToRegister.forEach(CropPlantHandler::suppressedRegisterPlant);
		plantsToRegister = null;

		//Register mod crops.
		CompatibilityHandler.getInstance().getCropPlants().forEach(CropPlantHandler::suppressedRegisterPlant);

		//Register crops found in the ore dictionary.
//        List<ItemStack> seeds = OreDictionary.getOres("listAllseed");
//        seeds.stream().filter(seed -> !isValidSeed(seed) && (seed.getItem() instanceof ItemSeeds)).forEach(seed -> {
//            ArrayList<ItemStack> fruits = OreDictHelper.getFruitsFromOreDict(seed);
//            if (fruits != null && fruits.size() > 0) {
//                suppressedRegisterPlant(new CropPlantOreDict((ItemSeeds) seed.getItem()));
//            }
//        });
		//Set spread chance overrides
		IOHelper.initSpreadChancesOverrides();

	}
}
