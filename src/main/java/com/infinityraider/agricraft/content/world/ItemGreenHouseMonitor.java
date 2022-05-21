package com.infinityraider.agricraft.content.world;

import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemGreenHouseMonitor extends BlockItemBase {
    public ItemGreenHouseMonitor() {
        super(AgriBlockRegistry.getInstance().greenhouse_monitor.get(), new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag advanced) {
        tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L1);
        tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L2);
        tooltip.add(AgriToolTips.GREENHOUSE_MONITOR_L3);
    }
}
