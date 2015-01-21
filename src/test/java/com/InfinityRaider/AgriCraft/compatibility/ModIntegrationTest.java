package com.InfinityRaider.AgriCraft.compatibility;


import com.InfinityRaider.AgriCraft.blocks.BlockCrop;
import com.InfinityRaider.AgriCraft.blocks.BlockCropBotania;
import com.InfinityRaider.AgriCraft.init.Blocks;
import com.InfinityRaider.AgriCraft.utility.RegisterHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({RegisterHelper.class})
@PowerMockIgnore("javax.management.*")
public class ModIntegrationTest {

    @Before
    public void setup() {
        mockStatic(RegisterHelper.class);
        PowerMockito.doNothing().when(RegisterHelper.class);
    }

    /** Tests that if Botania is present, BlockCropBotania is used instead of BlockCrop */
    @Test
    public void testBlockCropBotania() {
        ModIntegration.LoadedMods.botania = true;

        Blocks.init();

        assertThat(Blocks.blockCrop, instanceOf(BlockCropBotania.class));
    }

    /** Tests that BlockCrop is used when Botania is NOT present */
    @Test
    public void testBlockCrop() {
        ModIntegration.LoadedMods.botania = false;

        Blocks.init();
        assertThat(Blocks.blockCrop, instanceOf(BlockCrop.class));
        assertThat(Blocks.blockCrop, not(instanceOf(BlockCropBotania.class)));
    }
}
