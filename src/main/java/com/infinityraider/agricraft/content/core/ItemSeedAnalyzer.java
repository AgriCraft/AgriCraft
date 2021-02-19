package com.infinityraider.agricraft.content.core;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.item.BlockItemBase;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ItemSeedAnalyzer extends BlockItemBase {
    public ItemSeedAnalyzer() {
        super(AgriCraft.instance.getModBlockRegistry().seed_analyzer, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        tooltip.add(AgriToolTips.SEED_ANALYZER_L1);
        tooltip.add(AgriToolTips.SEED_ANALYZER_L2);
    }
}
