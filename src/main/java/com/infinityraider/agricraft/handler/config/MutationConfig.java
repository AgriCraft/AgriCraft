package com.InfinityRaider.AgriCraft.handler.config;

import com.InfinityRaider.AgriCraft.api.v1.IMutation;
import com.InfinityRaider.AgriCraft.farming.mutation.Mutation;
import com.InfinityRaider.AgriCraft.utility.LogHelper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class MutationConfig {
    private static final MutationConfig INSTANCE = new MutationConfig();

    public static MutationConfig getInstance() {
        return INSTANCE;
    }

    private final Gson gson;
    private List<Mutation> defaultMutations;

    private MutationConfig() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public List<Mutation> readMutations(String jsonPath) {
        try {
            return readMutations(new InputStreamReader(new FileInputStream(jsonPath)));
        } catch (FileNotFoundException e) {
            LogHelper.printStackTrace(e);
            return new ArrayList<>();
        }
    }

    public List<Mutation> readMutations(Reader reader) {
        List<Mutation> list = new ArrayList<>();
        try {
            SerializableMutation[] parsed = gson.fromJson(reader, SerializableMutation[].class);
            reader.close();
            for(SerializableMutation serializableMutation : parsed) {
                Mutation mutation = serializableMutation.getMutation();
                if(mutation != null) {
                    list.add(mutation);
                }
            }
        } catch(IOException e) {
            LogHelper.info("Parsing mutations failed");
            LogHelper.printStackTrace(e);
        }
        return list;

    }

    public void writeMutations(List<Mutation> mutations, String jsonPath) {
        SerializableMutation[] array = new SerializableMutation[mutations.size()];
        for(int i = 0 ; i < array.length; i++) {
            array[i] = new SerializableMutation(mutations.get(i));
        }
        try {
            Writer writer = new OutputStreamWriter(new FileOutputStream(jsonPath));
            gson.toJson(array, writer);
            writer.close();
        } catch(IOException e) {
            LogHelper.info("Writing default mutations failed");
            LogHelper.printStackTrace(e);
        }
    }

    public Mutation getDefaultMutation(ItemStack seed) {
        if(seed == null || seed.getItem() == null) {
            return null;
        }
        if(defaultMutations == null) {
            initDefaultMutations();
        }
        for(Mutation mutation : defaultMutations) {
            ItemStack result = mutation.getResult();
            if(result.getItem() == seed.getItem() && result.getItemDamage() == seed.getItemDamage()) {
                return mutation;
            }
        }
        return null;
    }

    private void initDefaultMutations() {
        defaultMutations = new ArrayList<>();
        try {
            defaultMutations.addAll(readMutations(
                    new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/agricraft/vanilla.json"), "UTF-8"))));
            if(ConfigurationHandler.resourcePlants) {
                defaultMutations.addAll(readMutations(
                        new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("assets/agricraft/agricraft.json"), "UTF-8"))));
            }
        } catch (UnsupportedEncodingException e) {
            LogHelper.printStackTrace(e);
        }
    }

    public static class SerializableMutation {
        public String result;
        public int resultMeta;
        public String parent1;
        public int parent1Meta;
        public String parent2;
        public int parent2Meta;
        public double chance;

        public SerializableMutation(IMutation mutation) {
            ItemStack resultStack = mutation.getResult();
            this.result = Item.itemRegistry.getNameForObject(resultStack.getItem()).toString();
            this.resultMeta = resultStack.getItemDamage();
            ItemStack parent1Stack = mutation.getParents()[0];
            this.parent1 = Item.itemRegistry.getNameForObject(parent1Stack.getItem()).toString();
            this.parent1Meta = parent1Stack.getItemDamage();
            ItemStack parent2Stack = mutation.getParents()[1];
            this.parent2 = Item.itemRegistry.getNameForObject(parent2Stack.getItem()).toString();
            this.parent2Meta = parent2Stack.getItemDamage();
            this.chance = mutation.getChance();
        }

        public Mutation getMutation() {
            Item resultItem = Item.itemRegistry.getObject(new ResourceLocation(result));
            if(resultItem == null) {
                return null;
            }
            Item parent1item = Item.itemRegistry.getObject(new ResourceLocation(parent1));
            if(parent1item == null) {
                return null;
            }
            Item parent2item = Item.itemRegistry.getObject(new ResourceLocation(parent2));
            if(parent2item == null) {
                return null;
            }
            return new Mutation(new ItemStack(resultItem, 1, resultMeta), new ItemStack(parent1item, 1, parent1Meta), new ItemStack(parent2item, 1, parent2Meta), chance);
        }
    }
}
