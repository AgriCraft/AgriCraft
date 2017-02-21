package com.infinityraider.agricraft.blocks.tiles.storage;

import com.infinityraider.agricraft.api.plant.IAgriPlant;
import com.infinityraider.agricraft.apiimpl.SeedRegistry;
import com.infinityraider.agricraft.blocks.tiles.TileEntityCustomWood;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TileEntitySeedStorageController extends TileEntityCustomWood implements ISeedStorageController {

    private final List<ISeedStorageControllable> controllables = new ArrayList<>();
    public boolean isControlling;

    public TileEntitySeedStorageController() {
        super();
    }

    @Override
    public boolean addStackToInventory(ItemStack stack) {
        return getControllable(stack)
                .map(c -> c.addStackToInventory(stack))
                .orElse(false);
    }

    @Override
    public List<ItemStack> getControlledSeeds() {
        return controllables
                .stream()
                .map(c -> c.getLockedSeed())
                .filter(s -> s.isPresent())
                .map(c -> c.get().toStack())
                .collect(Collectors.toList());
    }

    @Override
    public List<SeedStorageSlot> getSlots(Item seed, int meta) {
        return this.getControllable(new ItemStack(seed, 1, meta))
                .map(c -> c.getSlots())
                .orElse(Collections.EMPTY_LIST);
    }

    @Override
    public void addControllable(ISeedStorageControllable controllable) {
        if (!controllable.hasController()) {
            this.controllables.add(controllable);
        }
    }

    @Override
    public boolean isControlling() {
        return this.isControlling;
    }

    @Override
    public ArrayList<int[]> getControlledCoordinates() {
        ArrayList<int[]> coords = new ArrayList<>();
        for (ISeedStorageControllable controllable : this.controllables) {
            coords.add(controllable.getCoords());
        }
        return coords;
    }

    @Override
    public int[] getCoordinates() {
        return new int[]{this.xCoord(), this.yCoord(), this.zCoord()};
    }

    @Override
    public int getControllableID(ISeedStorageControllable controllable) {
        int id = -1;
        for (int i = 0; i < this.controllables.size() && id < 0; i++) {
            ISeedStorageControllable currentControllable = this.controllables.get(i);
            if (currentControllable == controllable) {
                id = i;
            }
        }
        return id;
    }

    @Override
    public Optional<ISeedStorageControllable> getControllable(ItemStack stack) {
        final IAgriPlant plant = SeedRegistry.getInstance().valueOf(stack).map(s -> s.getPlant()).orElse(null);
        if (plant == null) {
            return Optional.empty();
        }
        return controllables
                .stream()
                .filter(c -> c.getLockedSeed().filter(s -> s.getPlant() == plant).isPresent())
                .findAny();
    }
}
