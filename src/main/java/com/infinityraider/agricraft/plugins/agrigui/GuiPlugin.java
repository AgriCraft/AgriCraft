package com.infinityraider.agricraft.plugins.agrigui;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.content.AgriBlockRegistry;
import com.infinityraider.agricraft.content.AgriItemRegistry;
import com.infinityraider.agricraft.plugins.agrigui.analyzer.SeedAnalyzerContainer;
import com.infinityraider.agricraft.plugins.agrigui.analyzer.SeedAnalyzerScreen;
import com.infinityraider.agricraft.plugins.agrigui.journal.GuiCompatClient;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@OnlyIn(Dist.CLIENT)
@AgriPlugin(modId = Names.Mods.MINECRAFT, alwaysLoad = true)
public class GuiPlugin implements IAgriPlugin {
	public static final Component TITLE_JOURNAL = new TranslatableComponent("screen.agricraft.journal");
	public static final Component TITLE_SEED_ANALYZER = new TranslatableComponent("screen.agricraft.seed_analyzer");

	public GuiPlugin() {
		CONTAINERS.register(FMLJavaModLoadingContext.get().getModEventBus());
	}

	@Override
	public boolean isEnabled() {
		return AgriCraft.instance.getConfig().useGUIsInsteadOfAnimation();
	}

	@Override
	public String getId() {
		return "agrigui";
	}

	@Override
	public String getDescription() {
		return "Add guis to agricraft (journal & analyzer)";
	}

	@Override
	public void onClientSetupEvent(FMLClientSetupEvent event) {
		MenuScreens.register(SEED_ANALYZER_CONTAINER.get(), SeedAnalyzerScreen::new);
	}

	public static final DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "agricraft");
	public static final RegistryObject<MenuType<SeedAnalyzerContainer>> SEED_ANALYZER_CONTAINER = CONTAINERS.register("seed_analyzer_container",
			() -> IForgeMenuType.create((id, inv, data) -> new SeedAnalyzerContainer(id, inv.player.getLevel(), inv, data.readBlockPos())));

	@Override
	public void onCommonSetupEvent(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onSeedAnalyzerRightClick);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onBookRightClick);
	}

	public void onBookRightClick(PlayerInteractEvent.RightClickItem event) {
		if (!event.getPlayer().getLevel().isClientSide() || event.getPlayer().isDiscrete()) {
			return;
		}
		if (event.getItemStack().getItem() != AgriItemRegistry.journal) {
			return;
		}
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);

		GuiCompatClient.openScreen(event.getPlayer(), event.getHand());
	}

	public void onSeedAnalyzerRightClick(PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos =event.getPos();
		BlockState state = event.getWorld().getBlockState(pos);

		if (event.getPlayer().isDiscrete()) {
			return;
		}
		if (state.getBlock() != AgriBlockRegistry.seed_analyzer) {
			return;
		}
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
		if (event.getPlayer().getLevel().isClientSide()) {
			return;
		}
		MenuProvider containerProvider = new MenuProvider() {
			@Nonnull
			@Override
			public Component getDisplayName() {
				return TITLE_SEED_ANALYZER;
			}

			@Override
			public AbstractContainerMenu createMenu(int id, @Nonnull Inventory inventory, @Nonnull Player player) {
				return new SeedAnalyzerContainer(id, event.getWorld(), inventory, pos);
			}

		};
		NetworkHooks.openGui((ServerPlayer) event.getPlayer(), containerProvider, pos);
	}
}
