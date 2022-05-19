package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ItemMagnifyingGlass extends ItemBase {
    private static final Map<UUID, Boolean> observers = Maps.newIdentityHashMap();

    public static boolean isObserving(Player player) {
        return player != null && (observers.computeIfAbsent(player.getUUID(), uuid -> false) || CapabilityGeneInspector.getInstance().canInspect(player));
    }

    public static void setObserving(Player player, boolean status) {
        if(player != null) {
            observers.put(player.getUUID(), status);
            if (!player.getLevel().isClientSide()) {
                MessageMagnifyingGlassObserving.sendToClient(player, status);
            }
        }
    }

    public ItemMagnifyingGlass() {
        super(Names.Items.MAGNIFYING_GLASS, new Properties()
                .tab(AgriTabs.TAB_AGRICRAFT)
                .stacksTo(1)
        );
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        // Only do this on the client
        if(context.getLevel().isClientSide()) {
            AgriCraft.instance.proxy().toggleMagnifyingGlassObserving(context.getHand());
        }
        // Return fail to prevent swing animation
        return InteractionResult.FAIL;
    }

    @Nonnull
    @Override
    public InteractionResultHolder<ItemStack> use(@Nonnull Level world, @Nonnull Player player, @Nonnull InteractionHand hand) {
        // Only do this on the client
        if(world.isClientSide()) {
            AgriCraft.instance.proxy().toggleMagnifyingGlassObserving(hand);
        }
        // Return fail to prevent swing animation
        return InteractionResultHolder.fail(player.getItemInHand(hand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@Nonnull ItemStack stack, @Nullable Level world, @Nonnull List<Component> tooltip, @Nonnull TooltipFlag flagIn) {
        tooltip.add(AgriToolTips.MAGNIFYING_GLASS);
    }
}
