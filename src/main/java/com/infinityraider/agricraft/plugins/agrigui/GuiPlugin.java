package com.infinityraider.agricraft.plugins.agrigui;

import com.infinityraider.agricraft.AgriCraft;
import com.infinityraider.agricraft.api.v1.plugin.AgriPlugin;
import com.infinityraider.agricraft.api.v1.plugin.IAgriPlugin;
import com.infinityraider.agricraft.handler.JournalViewPointHandler;
import com.infinityraider.agricraft.plugins.agrigui.analyzer.SeedAnalyzerContainer;
import com.infinityraider.agricraft.plugins.agrigui.analyzer.SeedAnalyzerScreen;
import com.infinityraider.agricraft.plugins.agrigui.journal.GuiCompatClient;
import com.infinityraider.agricraft.reference.Names;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

@SuppressWarnings("unused")
@AgriPlugin(modId = Names.Mods.MINECRAFT, alwaysLoad = true)
public class GuiPlugin implements IAgriPlugin {

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
		ScreenManager.registerFactory(SEED_ANALYZER_CONTAINER.get(), SeedAnalyzerScreen::new);
	}

	public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, "agricraft");
	public static final RegistryObject<ContainerType<SeedAnalyzerContainer>> SEED_ANALYZER_CONTAINER = CONTAINERS.register("seed_analyzer_container",
			() -> IForgeContainerType.create((id, inv, data) -> new SeedAnalyzerContainer(id, inv.player.getEntityWorld(), inv, data.readBlockPos())));

	@Override
	public void onCommonSetupEvent(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onSeedAnalyzerRightClick);
		MinecraftForge.EVENT_BUS.addListener(EventPriority.HIGHEST, this::onBookRightClick);
	}

	public void onBookRightClick(PlayerInteractEvent.RightClickItem event) {
		if (!event.getPlayer().world.isRemote || event.getPlayer().isSneaking()) {
			return;
		}
		if (event.getItemStack().getItem() != AgriCraft.instance.getModItemRegistry().journal.toItem()) {
			return;
		}
		event.setCancellationResult(ActionResultType.SUCCESS);
		event.setCanceled(true);

		GuiCompatClient.openScreen(event.getPlayer(), event.getHand());
	}

	public void onSeedAnalyzerRightClick(PlayerInteractEvent.RightClickBlock event) {
		BlockPos pos =event.getPos();
		BlockState state = event.getWorld().getBlockState(pos);

		if (event.getPlayer().isSneaking()) {
			return;
		}
		if (state.getBlock() != AgriCraft.instance.getModBlockRegistry().seed_analyzer.getBlock()) {
			return;
		}
		event.setCancellationResult(ActionResultType.SUCCESS);
		event.setCanceled(true);
		if (event.getPlayer().world.isRemote) {
			return;
		}
		INamedContainerProvider containerProvider = new INamedContainerProvider() {
			@Nonnull
			@Override
			public ITextComponent getDisplayName() {
				return new TranslationTextComponent("screen.agricraft.seed_analyzer");
			}

			@Override
			public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
				return new SeedAnalyzerContainer(id, event.getWorld(), playerInventory, pos);
			}

		};
		NetworkHooks.openGui((ServerPlayerEntity) event.getPlayer(), containerProvider, pos);
	}
}
