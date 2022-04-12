package com.infinityraider.agricraft.content.decoration;

import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemCustomWood;
import com.infinityraider.agricraft.reference.AgriToolTips;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemGrate extends ItemCustomWood {
    public ItemGrate() {
        super(AgriBlockRegistry.grate, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull Consumer<Component> tooltip, @Nonnull TooltipFlag advanced) {
        tooltip.accept(AgriToolTips.GRATE_L1);
        tooltip.accept(AgriToolTips.GRATE_L2);
    }
}
