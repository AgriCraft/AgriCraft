package com.InfinityRaider.AgriCraft.farming.mutation;

import com.InfinityRaider.AgriCraft.api.v1.IMutation;
import com.InfinityRaider.AgriCraft.api.v3.ICrop;
import com.InfinityRaider.AgriCraft.api.v3.IMutationHandler;
import com.InfinityRaider.AgriCraft.api.v3.IMutationLogic;
import com.InfinityRaider.AgriCraft.farming.CropPlantHandler;
import com.InfinityRaider.AgriCraft.handler.ConfigurationHandler;
import com.InfinityRaider.AgriCraft.utility.IOHelper;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class MutationHandler implements IMutationHandler {
    private static final MutationHandler INSTANCE = new MutationHandler();

    public static MutationHandler getInstance() {
        return INSTANCE;
    }

    private List<IMutation> mutations;
    private boolean isSyncing = false;
    private IMutationLogic mutationLogic = MutationLogicAgriCraft.getInstance();

    private MutationHandler() {}

    public  void init() {
        //Read mutations & initialize the mutation arrays
        String[] data = IOHelper.getLinesArrayFromData(ConfigurationHandler.readMutationData());
        
        mutations = new ArrayList<IMutation>();
        
      //print registered mutations to the log
        LogHelper.info("Registered Mutations:");
        
        for(String line:data) {
            Mutation mutation = readMutation(line);
            if(mutation!=null && !mutations.contains(mutation)) {
                mutations.add(mutation);
                LogHelper.info(" - " + mutation.getFormula());
            }
        }
    }

    @SideOnly(Side.CLIENT)
    public void syncFromServer(IMutation mutation, boolean finished) {
        if(!isSyncing) {
            LogHelper.info("Receiving mutations from server");
            mutations = new ArrayList<IMutation>();
            isSyncing = true;
        }
        mutations.add(mutation);
        if(finished) {
            isSyncing = false;
            LogHelper.info("Successfully received mutations from server");
        }
    }

	private Mutation readMutation(String input) { //Removed some string concatenation, and de-nested the if statements.
		
		Mutation mutation = null;
		String[] data = IOHelper.getData(input);

        if (data.length == 0) {
            LogHelper.info("Error when reading mutation: invalid number of arguments. (line: " + input + ")");
            return null;
        }

		String mutationData = data[0];
		int indexEquals = mutationData.indexOf('=');
		int indexPlus = mutationData.indexOf('+');

		if (!(indexEquals > 0 && indexPlus > indexEquals)) {
			LogHelper.info("Error when reading mutation: mutation is not defined correctly. (line: " + input + ")");
			return null;
		}

		// read the stacks
		ItemStack resultStack = IOHelper.getStack(mutationData.substring(0,indexEquals));
		ItemStack parentStack1 = IOHelper.getStack(mutationData.substring(indexEquals + 1, indexPlus));
		ItemStack parentStack2 = IOHelper.getStack(mutationData.substring(indexPlus + 1));

		if (!CropPlantHandler.isValidSeed(resultStack)) {
			LogHelper.info("Error when reading mutation: resulting stack is not correct. (line: " + input + ")");
			return null;
		} else if (!CropPlantHandler.isValidSeed(parentStack1)) {
			LogHelper .info("Error when reading mutation: first parent stack is not correct. (line: " + input + ")");
			return null;
		} else if (!CropPlantHandler.isValidSeed(parentStack2)) {
			LogHelper.info("Error when reading mutation: second parent stack is not correct. (line: " + input + ")");
			return null;
		}
		try {
			mutation = new Mutation(resultStack, parentStack1, parentStack2);
		} catch (Exception e) {
			LogHelper.debug("Caught exception when trying to add mutation: "
					+ resultStack.getUnlocalizedName() + "="
					+ parentStack1.getUnlocalizedName() + "+"
					+ parentStack2.getUnlocalizedName()
					+ ", this seed is not registered");
		}

		return mutation;
	}

    //gets all the possible crossovers
    @Override
    public IMutation[] getCrossOvers(List<? extends ICrop> crops) {
        ICrop[] parents = filterParents(crops);
        ArrayList<IMutation> list = new ArrayList<IMutation>();
        switch (parents.length) {
            case 2:
                list.addAll(getMutationsFromParent(parents[0], parents[1]));
                break;
            case 3:
                list.addAll(getMutationsFromParent(parents[0], parents[1]));
                list.addAll(getMutationsFromParent(parents[0], parents[2]));
                list.addAll(getMutationsFromParent(parents[1], parents[2]));
                break;
            case 4:
                list.addAll(getMutationsFromParent(parents[0], parents[1]));
                list.addAll(getMutationsFromParent(parents[0], parents[2]));
                list.addAll(getMutationsFromParent(parents[0], parents[3]));
                list.addAll(getMutationsFromParent(parents[1], parents[2]));
                list.addAll(getMutationsFromParent(parents[1], parents[3]));
                list.addAll(getMutationsFromParent(parents[2], parents[3]));
                break;
        }
        return cleanMutationArray(list.toArray(new IMutation[list.size()]));
    }

    //gets an array of all the possible parents from the array containing all the neighbouring crops
    private ICrop[] filterParents(List<? extends ICrop> input) {
        ArrayList<ICrop> list = new ArrayList<ICrop>();
        for(ICrop crop:input) {
            if (crop != null && crop.isMature()) {
                list.add(crop);
            }
        }
        return list.toArray(new ICrop[list.size()]);
    }

    //finds the product of two parents
    private ArrayList<IMutation> getMutationsFromParent(ICrop parent1, ICrop parent2) {
        Item seed1 = parent1.getSeedStack().getItem();
        Item seed2 = parent2.getSeedStack().getItem();
        int meta1 = parent1.getSeedStack().getItemDamage();
        int meta2 = parent2.getSeedStack().getItemDamage();
        ArrayList<IMutation> list = new ArrayList<IMutation>();
        for (IMutation mutation:mutations) {
            ItemStack parent1Stack = mutation.getParents()[0];
            ItemStack parent2Stack = mutation.getParents()[1];
            if ((seed1==parent1Stack.getItem() && seed2==parent2Stack.getItem()) && (meta1==parent1Stack.getItemDamage() && meta2==parent2Stack.getItemDamage())) {
                list.add(mutation);
            }
            if ((seed1==parent2Stack.getItem() && seed2==parent1Stack.getItem()) && (meta1==parent2Stack.getItemDamage() && meta2==parent1Stack.getItemDamage())) {
                list.add(mutation);
            }
        }
        return list;
    }

    //removes null instance from a mutations array
    private IMutation[] cleanMutationArray(IMutation[] input) {
        ArrayList<IMutation> list = new ArrayList<IMutation>();
        for(IMutation mutation:input) {
            if (mutation.getResult() != null) {
                list.add(mutation);
            }
        }
        return list.toArray(new IMutation[list.size()]);
    }


    //public methods to read data from the mutations and parents arrays

    //gets all the mutations
    @Override
    public IMutation[] getMutations() {
        return mutations.toArray(new IMutation[mutations.size()]);
    }

    //gets all the mutations this crop can mutate to
    @Override
    public IMutation[] getMutationsFromParent(ItemStack stack) {
        ArrayList<IMutation> list = new ArrayList<IMutation>();
        for (IMutation mutation : mutations) {
            ItemStack parent1Stack = mutation.getParents()[0];
            ItemStack parent2Stack = mutation.getParents()[1];
            if (parent1Stack.getItem() == stack.getItem() && parent1Stack.getItemDamage() == stack.getItemDamage()) {
                list.add(new Mutation(mutation));
            }
            if (parent2Stack.getItem() == stack.getItem() && parent2Stack.getItemDamage() == stack.getItemDamage()) {
                if(parent2Stack.getItem() == parent1Stack.getItem() && parent2Stack.getItemDamage() == parent1Stack.getItemDamage()) {
                    continue;
                }
                list.add(new Mutation(mutation));
            }
        }

        return list.toArray(new IMutation[list.size()]);
    }

    public IMutation[] getMutationsFromChild(Item seed, int meta) {
        return getMutationsFromChild(new ItemStack(seed, 1, meta));
    }

    //gets the parents this crop mutates from
    @Override
    public IMutation[] getMutationsFromChild(ItemStack stack) {
        ArrayList<IMutation> list = new ArrayList<IMutation>();
        if(CropPlantHandler.isValidSeed(stack)) {
            for (IMutation mutation:mutations) {
                if (mutation.getResult().getItem() == stack.getItem() && mutation.getResult().getItemDamage() == stack.getItemDamage()) {
                    list.add(new Mutation(mutation));
                }
            }
        }
        return list.toArray(new IMutation[list.size()]);
    }

    /**
     * Removes all mutations where the given parameter is the result of a mutation
     * @return Removed mutations
     */
    @Override
    public List<IMutation> removeMutationsByResult(ItemStack result) {
        List<IMutation> removedMutations = new ArrayList<IMutation>();
        for (Iterator<IMutation> iter = mutations.iterator(); iter.hasNext();) {
           IMutation mutation = iter.next();
            if (mutation.getResult().isItemEqual(result)) {
                iter.remove();
                removedMutations.add(mutation);
            }
        }
        return removedMutations;
    }

    /** Adds the given mutation to the mutations list */
    @Override
    public void add(IMutation mutation) {
        mutations.add(mutation);
    }

    /** Removes the given mutation from the mutations list */
    @Override
    public void remove(IMutation mutation) {
        mutations.remove(mutation);
    }

    /**
     * Adds all mutations back into the mutation list. Does not perform and validation.
     */
    @Override
    public void addAll(Collection<? extends IMutation> mutationsToAdd) {
        mutations.addAll(mutationsToAdd);
    }

    @Override
    public Mutation createNewMutation(ItemStack result, ItemStack parent1, ItemStack parent2) {
        return new Mutation(result, parent1, parent2);
    }

    @Override
    public Mutation createNewMutation(ItemStack result, ItemStack parent1, ItemStack parent2, double chance) {
        return new Mutation(result, parent1, parent2, chance);
    }

    @Override
    public void setMutationLogic(IMutationLogic logic) {
        this.mutationLogic = logic;
    }

    @Override
    public IMutationLogic getMutationLogic() {
        return this.mutationLogic;
    }
}
