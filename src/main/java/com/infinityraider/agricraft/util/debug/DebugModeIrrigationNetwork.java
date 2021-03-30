package com.infinityraider.agricraft.util.debug;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationComponent;
import com.infinityraider.agricraft.api.v1.irrigation.IAgriIrrigationNetwork;
import com.infinityraider.agricraft.capability.CapabilityIrrigationComponent;
import com.infinityraider.agricraft.impl.v1.irrigation.IrrigationNetwork;
import com.infinityraider.infinitylib.utility.InfinityLogger;
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

public class DebugModeIrrigationNetwork extends DebugMode {
    @Override
    public String debugName() {
        return "irrigation network data";
    }

    @Override
    public void debugActionBlockClicked(ItemStack stack, ItemUseContext context) {
        CapabilityIrrigationComponent.getInstance().getIrrigationComponent(context.getWorld().getTileEntity(context.getPos())).ifPresent(component -> {
            InfinityLogger logger = AgriCraft.instance.getLogger();
            // Log component class name
            logger.info("Found Irrigation component: " + component.getClass().getName());
            // Log nodes
            Arrays.stream(Direction.values()).forEach(dir -> this.logNode(component, dir, logger));
            this.logNode(component, null, logger);
            // Fetch networks
            Set<IAgriIrrigationNetwork> networks = Stream.concat(
                    Arrays.stream(Direction.values()).map(component::getNetwork),
                    Stream.of(component.getNetwork(null))
            ).filter(IAgriIrrigationNetwork::isValid).collect(Collectors.toSet());
            logger.info("Connected to " + networks.size() + " networks");
            // Log networks
            networks.forEach(network -> {
                if(network instanceof IrrigationNetwork) {
                    logger.info("Network: " + ((IrrigationNetwork) network).getId());
                } else {
                    logger.info("Network: " + network.getClass().getName());
                }
                this.logNetwork(network, logger);
            });
        });
    }

    protected void logNode(IAgriIrrigationComponent component, @Nullable Direction dir, InfinityLogger logger) {
        logger.info("  " + (dir == null ? "null" : dir.name()) + " node: "
                + component.getNode(dir).map(node -> node.getClass().getName()).orElse("null"));
    }

    protected void logNetwork(IAgriIrrigationNetwork network, InfinityLogger logger) {
        logger.info(" - " + network.nodes().size() + " nodes");
        logger.info(" - " + network.connections().size() + " nodes");
    }

    @Override
    public void debugActionClicked(ItemStack stack, World world, PlayerEntity player, Hand hand) {}

    @Override
    public void debugActionEntityClicked(ItemStack stack, PlayerEntity player, LivingEntity target, Hand hand) {}
}
