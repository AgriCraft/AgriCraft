package com.InfinityRaider.AgriCraft.tileentity.storage;


import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class TileEntitySeedStorageTest {

    @Before
    public void setup() {
        TileEntity.addMapping(TileEntitySeedStorage.class, "agricraft:tilenetity_seedStorage");
    }

    /**
     * Writes NBT and reads it back into another object and checks if the values are the same
     */
    @Test
    public void testReadWriteNBT() {
        // Set all the parameters here
        TileEntitySeedStorage baseSeedStorage = new TileEntitySeedStorage();
        baseSeedStorage.direction = ForgeDirection.NORTH;

        NBTTagCompound compoundTag = new NBTTagCompound();
        baseSeedStorage.writeToNBT(compoundTag);

        TileEntitySeedStorage readSeedStorage = new TileEntitySeedStorage();
        readSeedStorage.readFromNBT(compoundTag);

        // Check that all the parameters are correct
        assertThat(readSeedStorage.direction, is(ForgeDirection.NORTH));
    }
}
