package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.AgriApi;
import com.infinityraider.agricraft.api.v1.requirement.IAgriSoil;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebugModeCheckSoil extends DebugMode {

    @Override
    public String debugName() {
        return "check soil";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, UseOnContext context) {
        IAgriSoil soil = AgriApi.getSoilRegistry().stream()
                .filter(s -> s.isVariant(context.getLevel().getBlockState(context.getClickedPos())))
                .findFirst()
                .orElse(null);
        MessageUtil.messagePlayer(context.getPlayer(), "{0} Soil Info:", AgriCraft.instance.proxy().getLogicalSide());
        if(soil == null) {
            MessageUtil.messagePlayer(context.getPlayer(), " - Type: \"{0}\"", "Unknown Soil");
        } else {
            MessageUtil.messagePlayer(context.getPlayer(), " - Type: \"{0}\"", soil.getId());
            MessageUtil.messagePlayer(context.getPlayer(), " - Humidity: \"{0}\"", soil.getHumidity());
            MessageUtil.messagePlayer(context.getPlayer(), " - Acidity: \"{0}\"", soil.getAcidity());
            MessageUtil.messagePlayer(context.getPlayer(), " - Nutrients: \"{0}\"", soil.getNutrients());
        }
    }

    @Override
    public void debugActionClicked(ItemStack stack, Level world, Player player, InteractionHand hand) {
        // NOP
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        // NOP
    }
}
