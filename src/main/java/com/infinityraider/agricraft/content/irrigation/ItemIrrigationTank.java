package com.infinityraider.agricraft.content.irrigation;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.content.core.ItemCustomWood;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.infinitylib.item.BlockItemDynamicTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Consumer;

public class ItemIrrigationTank extends ItemCustomWood {
    public ItemIrrigationTank() {
        super(AgriCraft.instance.getModBlockRegistry().tank, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void addInformation(@Nonnull ItemStack stack, @Nullable World world, @Nonnull Consumer<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        tooltip.accept(AgriToolTips.TANK_L1);
    }
}
