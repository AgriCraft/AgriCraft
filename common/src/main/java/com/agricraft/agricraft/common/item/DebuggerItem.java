package com.agricraft.agricraft.common.item;

import com.agricraft.agricraft.common.block.entity.CropBlockEntity;
import com.agricraft.agricraft.common.block.entity.SeedAnalyzerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class DebuggerItem extends Item {

	public DebuggerItem(Properties properties) {
		super(properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		System.out.println("side: " + (level.isClientSide?"client":"server"));
		if (level.getBlockEntity(pos) instanceof SeedAnalyzerBlockEntity analyzer) {
			System.out.println("  tag: " + analyzer.saveWithoutMetadata());
			System.out.println("  hasSeed: " + analyzer.hasSeed());
			if (analyzer.hasSeed()) {
				System.out.println("    seed: " + analyzer.getSeed() + " " + analyzer.getSeed().getTag());
			}
			System.out.println("  hasJournal: " + analyzer.hasJournal());
			if (analyzer.hasJournal()) {
				System.out.println("    journal: " + analyzer.getJournal() + " " + analyzer.getJournal().getTag());
			}
		}
		if (level.getBlockEntity(pos) instanceof CropBlockEntity crop) {
			System.out.println("  tag: " + crop.saveWithoutMetadata());
			System.out.println("  plant id: " + crop.getPlantId());
			System.out.println("  plant: " + crop.getPlant());
		}
		return super.useOn(context);
	}

	//	private static final List<DebugMode> MODES = ImmutableList.of(
//			new DebugModeCheckSoil(),
//			new DebugModeCoreInfo(),
//			new DebugModeCheckIrrigationComponent(),
//			new DebugModeFillIrrigationComponent(),
//			new DebugModeDrainIrrigationComponent(),
//			new DebugModeBonemeal(),
//			new DebugModeDiffLight(),
//			new DebugModeGreenHouse()
//	);


//	@Override
//	protected List<DebugMode> getDebugModes() {
//		return MODES;
//	}
}
