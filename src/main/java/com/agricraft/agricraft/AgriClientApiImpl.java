package com.agricraft.agricraft;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.AgriClientApi;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.tools.journal.JournalPage;
import com.agricraft.agricraft.api.tools.journal.JournalPageDrawer;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspector;
import com.agricraft.agricraft.client.tools.journal.drawers.EmptyPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.FrontPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.GeneticsPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.GrowthReqsPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.IntroductionPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.MutationPageDrawer;
import com.agricraft.agricraft.client.tools.journal.drawers.PlantPageDrawer;
import com.agricraft.agricraft.common.item.journal.FrontPage;
import com.agricraft.agricraft.common.item.journal.GeneticsPage;
import com.agricraft.agricraft.common.item.journal.GrowthReqsPage;
import com.agricraft.agricraft.common.item.journal.IntroductionPage;
import com.agricraft.agricraft.common.item.journal.MutationsPage;
import com.agricraft.agricraft.common.item.journal.PlantPage;
import com.agricraft.agricraft.common.registry.AgriDataComponents;
import com.agricraft.agricraft.common.registry.AgriItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class AgriClientApiImpl implements AgriClientApi {

	private static final Map<ResourceLocation, JournalPageDrawer<?>> DRAWERS = new HashMap<>();
	private static final List<Predicate<Player>> ALLOWING_PREDICATE = new ArrayList<>();
	private static final Set<MagnifyingInspector> INSPECTORS = new HashSet<>();

	public AgriClientApiImpl() {
		this.registerJournalPageDrawer(FrontPage.ID, new FrontPageDrawer());
		this.registerJournalPageDrawer(IntroductionPage.ID, new IntroductionPageDrawer());
		this.registerJournalPageDrawer(GrowthReqsPage.ID, new GrowthReqsPageDrawer());
		this.registerJournalPageDrawer(GeneticsPage.ID, new GeneticsPageDrawer());
		this.registerJournalPageDrawer(PlantPage.ID, new PlantPageDrawer());
		this.registerJournalPageDrawer(MutationsPage.ID, new MutationPageDrawer());
		this.registerMagnifyingAllowingPredicate(player -> player.getMainHandItem().is(AgriItems.MAGNIFYING_GLASS.get()));
		this.registerMagnifyingAllowingPredicate(player -> player.getOffhandItem().is(AgriItems.MAGNIFYING_GLASS.get()));
		this.registerMagnifyingAllowingPredicate(player -> player.getItemBySlot(EquipmentSlot.HEAD).has(AgriDataComponents.MAGNIFYING));
		this.registerMagnifyingInspector((level, player, hitResult) -> {
			if (hitResult instanceof BlockHitResult result) {
				if (level.getBlockEntity(result.getBlockPos()) instanceof MagnifyingInspectable inspectable) {
					return Optional.of(inspectable);
				}
				Optional<AgriSoil> soil = AgriApi.get().getSoil(level, result.getBlockPos());
				if (soil.isPresent()) {
					return Optional.of(soil.get());
				}
			}
			return Optional.empty();
		});
		this.registerMagnifyingInspector((level, player, hitResult) -> {
			Vec3 lookAngle = player.getLookAngle();
			if (-0.1 <= lookAngle.z && lookAngle.z <= 0.1) {
				HitResult pick = Minecraft.getInstance().getCameraEntity().pick(100, 0, false);
				double sunOrientation = level.getTimeOfDay(0);  // angle in circle in [0,1]
				double playerOrientation = Math.atan2(lookAngle.x, lookAngle.y) / Math.PI / 2.0;  // angle in a circle in [0,1]
				// modify the orientation of the player to be in the same system as the sun's
				if (-0.5 < playerOrientation && playerOrientation < 0) {
					playerOrientation = Math.abs(playerOrientation);
				} else if (0 < playerOrientation && playerOrientation < 0.5) {
					playerOrientation = 1 - playerOrientation;
				}
				// when level time is around noon/midnight and the player look at the sun it will bug
				// the difference will be around 1 instead of 0
				// this happens because the if-else above does nothing on 0
				// I could handle it and check when player tod is 0-1 and level tod too, but I do not want to :)

				double diff = sunOrientation - playerOrientation;

				if (-0.013 <= diff && diff <= 0.013 && pick.getType() == HitResult.Type.MISS) {
					return Optional.of((tooltip, isPlayerSneaking) -> tooltip.add(Component.translatable("agricraft.tooltip.magnifying.sun")));
				}
			}
			return Optional.empty();
		});
	}

	@Override
	public <T extends JournalPage> void registerJournalPageDrawer(ResourceLocation id, JournalPageDrawer<T> drawer) {
		DRAWERS.put(id, drawer);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T extends JournalPage> JournalPageDrawer<T> getJournalPageDrawer(T page) {
		return (JournalPageDrawer<T>) DRAWERS.getOrDefault(page.getDrawerId(), EmptyPageDrawer.INSTANCE);
	}

	@Override
	public void registerMagnifyingAllowingPredicate(Predicate<Player> predicate) {
		ALLOWING_PREDICATE.add(predicate);
	}

	@Override
	public Collection<Predicate<Player>> getMagnifyingAllowingPredicates() {
		return ALLOWING_PREDICATE;
	}

	@Override
	public void registerMagnifyingInspector(MagnifyingInspector inspector) {
		INSPECTORS.add(inspector);
	}

	@Override
	public Stream<MagnifyingInspector> getMagnifyingInspectors() {
		return INSPECTORS.stream();
	}

}
