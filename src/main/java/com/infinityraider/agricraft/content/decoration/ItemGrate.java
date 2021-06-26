package com.infinityraider.agricraft.content.decoration;

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

public class ItemGrate extends ItemCustomWood {
    public ItemGrate() {
        super(AgriCraft.instance.getModBlockRegistry().grate, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
        );
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void addInformation (@Nonnull ItemStack stack, @Nullable World world, @Nonnull Consumer<ITextComponent> tooltip, @Nonnull ITooltipFlag advanced) {
        tooltip.accept(AgriToolTips.GRATE_L1);
        tooltip.accept(AgriToolTips.GRATE_L2);
    }
}
