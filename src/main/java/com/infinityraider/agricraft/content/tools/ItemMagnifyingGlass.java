package com.infinityraider.agricraft.content.tools;

import com.google.common.collect.Maps;
import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.capability.CapabilityGeneInspector;
import com.infinityraider.agricraft.content.AgriTabs;
import com.infinityraider.agricraft.network.MessageMagnifyingGlassObserving;
import com.infinityraider.agricraft.reference.AgriToolTips;
import com.infinityraider.agricraft.reference.Names;
import com.infinityraider.infinitylib.item.ItemBase;
import net.minecraft.world.entity.player.Player;
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
        return player != null && (observers.computeIfAbsent(player.getUniqueID(), uuid -> false) || CapabilityGeneInspector.getInstance().canInspect(player));
    }

    public static void setObserving(Player player, boolean status) {
        if(player != null) {
            observers.put(player.getUniqueID(), status);
            if (!player.getEntityWorld().isRemote()) {
                MessageMagnifyingGlassObserving.sendToClient(player, status);
            }
        }
    }

    public ItemMagnifyingGlass() {
        super(Names.Items.MAGNIFYING_GLASS, new Properties()
                .group(AgriTabs.TAB_AGRICRAFT)
                .maxStackSize(1)
        );
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        // Only do this on the client
        if(context.getWorld().isRemote()) {
            AgriCraft.instance.proxy().toggleMagnifyingGlassObserving(context.getHand());
        }
        // Return fail to prevent swing animation
        return ActionResultType.FAIL;
    }

    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(@Nonnull World world, @Nonnull PlayerEntity player, @Nonnull Hand hand) {
        // Only do this on the client
        if(world.isRemote()) {
            AgriCraft.instance.proxy().toggleMagnifyingGlassObserving(hand);
        }
        // Return fail to prevent swing animation
        return ActionResult.resultFail(player.getHeldItem(hand));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void addInformation(@Nonnull ItemStack stack, @Nullable World worldIn, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag flagIn) {
        tooltip.add(AgriToolTips.MAGNIFYING_GLASS);
    }
}
