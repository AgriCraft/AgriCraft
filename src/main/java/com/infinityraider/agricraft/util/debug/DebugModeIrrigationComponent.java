package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.infinitylib.utility.MessageUtil;
import com.infinityraider.infinitylib.utility.debug.DebugMode;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DebugModeIrrigationComponent extends DebugMode {
    @Override
    public String debugName() {
        return "irrigation network data";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        CapabilityIrrigationComponent.getInstance().getIrrigationComponent(context.getWorld().getTileEntity(context.getPos())).ifPresent(component -> {
            // Log side:
            MessageUtil.messagePlayer(context.getPlayer(), "{0} Irrigation Component Data", AgriCraft.instance.proxy().getLogicalSide());
            // Log component class name
            MessageUtil.messagePlayer(context.getPlayer(), "Found Irrigation component: {0}", component.getClass().getName());
            // Log nodes
            Arrays.stream(Direction.values()).forEach(dir -> this.logNode(component, dir, context.getPlayer()));
            this.logNode(component, null, context.getPlayer());
            // Fetch networks
            Set<IAgriIrrigationNetwork> networks = Stream.concat(
                    Arrays.stream(Direction.values()).map(component::getNetwork),
                    Stream.of(component.getNetwork(null))
            ).filter(IAgriIrrigationNetwork::isValid).collect(Collectors.toSet());
            MessageUtil.messagePlayer(context.getPlayer(), "Connected to {0} networks", "" + networks.size());
        });
    }

    protected void logNode(IAgriIrrigationComponent component, @Nullable Direction dir, PlayerEntity player) {
        MessageUtil.messagePlayer(player, "{0} node: {1}",
                dir == null ? "null" : dir.name(),
                component.getNode(dir).map(node -> node.getClass().getName()).orElse("null")
        );
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {
    }

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {
    }
}
