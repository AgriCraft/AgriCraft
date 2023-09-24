package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.api.AgriApi;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.codecs.AgriSoil;
import com.agricraft.agricraft.api.crop.AgriCrop;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.requirement.AgriGrowthConditionRegistry;
import com.agricraft.agricraft.api.requirement.AgriGrowthResponse;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.api.tools.magnifying.MagnifyingInspectable;
import com.agricraft.agricraft.common.config.CoreConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.registry.ModItems;
import com.agricraft.agricraft.common.util.LangUtils;
import mezz.jei.api.constants.ModIds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

public class CropBlockEntity extends BlockEntity implements AgriCrop, MagnifyingInspectable {

	private AgriGenome genome;
	private String plantId = "";
	private AgriPlant plant;
	private int growthStage = 0;
	private final Map<Integer, VoxelShape> shapeByAge = new HashMap<>();

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(ModBlockEntityTypes.CROP.get(), blockPos, blockState);
	}

	@Override
	public void load(CompoundTag tag) {
		super.load(tag);
		this.genome = AgriGenome.fromNBT(tag);
		if (this.genome == null) {
			this.plantId = "agricraft:unknown";
		} else {
			this.plantId = this.genome.getSpeciesGene().getDominant().trait();
		}
		this.growthStage = tag.getInt("growth");
		if (plant == null && level != null) {
			this.plant = AgriApi.getPlant(this.plantId, this.level.registryAccess()).orElse(null);
		}
	}

	@Override
	protected void saveAdditional(CompoundTag tag) {
		super.saveAdditional(tag);
		genome.writeToNBT(tag);
		tag.putInt("growth", this.growthStage);
	}

	@NotNull
	@Override
	public CompoundTag getUpdateTag() {
		return this.saveWithoutMetadata();
	}

	@Nullable
	@Override
	public Packet<ClientGamePacketListener> getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public void setLevel(Level level) {
		super.setLevel(level);
		// when the block id deserialized the level is null so we can't load the plant from the registry yet
		// thus, we're doing it now, as soon as the level is present
		if (level != null && !level.isClientSide) {
			this.plant = AgriApi.getPlant(this.plantId, level.registryAccess()).orElse(null);
			level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	public void setGenome(AgriGenome genome) {
		this.genome = genome;
		this.plantId = genome.getSpeciesGene().getDominant().trait();
		this.plant = AgriApi.getPlant(this.plantId, this.level.registryAccess()).orElse(null);
		this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
	}

	public AgriGenome getGenome() {
		return genome;
	}

	public String getPlantId() {
		return this.plantId;
	}

	public AgriPlant getPlant() {
		return this.plant;
	}

	public int getGrowthStage() {
		return this.growthStage;
	}

	@Override
	public void setGrowthStage(int stage) {
		this.growthStage = stage;
		this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
	}

	public int getGrowthPercent() {
		return (this.growthStage + 1) * 100 / this.plant.stages().size();
	}

	public boolean isMaxStage() {
		return this.growthStage == this.plant.stages().size() - 1;
	}

	@Override
	public boolean isFertile() {
		return this.getFertilityResponse().isFertile();
	}

	@Override
	public AgriGrowthResponse getFertilityResponse() {
		// TODO: should we cache the fertility response ?
		// check growth space
		int height = this.plant.stages().get(this.growthStage);
		while (height > 16) {
			if (!level.getBlockState(this.getBlockPos().above(height / 16)).isAir()) {
				return AgriGrowthResponse.INFERTILE;
			}
			height -= 16;
		}
		// if there is no condition registered
		if (AgriGrowthConditionRegistry.getInstance().isEmpty()) {
			return AgriGrowthResponse.FERTILE;
		}
		// check every growth condition registered and take the one with the highest priority
		int strength = this.genome.getStatGene(AgriStatRegistry.getInstance().strengthStat()).getTrait();
		Optional<AgriGrowthResponse> optional = AgriGrowthConditionRegistry.getInstance().stream()
				.map(condition -> condition.check(this, this.level, this.getBlockPos(), strength))
				.reduce((result, element) -> result.priority() >= element.priority() ? result : element);
		return optional.orElse(AgriGrowthResponse.FERTILE);
	}

	@Override
	public Optional<AgriSoil> getSoil() {
		return AgriApi.getSoil(this.level, getBlockPos().below(), this.level.registryAccess());
	}

	/**
	 * @return the block shape of the crop
	 */
	public VoxelShape getShape() {
		if (this.plant == null) {
			return Shapes.empty();
		}
		double yoffset = 0;
		if (level.getBlockState(this.getBlockPos().below()).is(Blocks.FARMLAND)) {
			// TODO: should we change this to allow other soil to offsets the plant shape ?
			yoffset = -1.0D / 16.0D;
		}
		return shapeByAge.computeIfAbsent(this.growthStage, stage -> Block.box(0, 0, 0, 16, this.plant.stages().get(stage), 16)).move(0, yoffset, 0);
	}

	/**
	 * Right click interaction
	 *
	 * @see com.agricraft.agricraft.common.block.CropBlock#use
	 */
	public InteractionResult use(Level level, BlockPos pos, BlockState state, Player player, InteractionHand hand, BlockHitResult hit) {
		// do nothing from off hand
		if (hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}
		ItemStack heldItem = player.getItemInHand(hand);
		// harvesting if empty-handed
		if (heldItem.isEmpty()) {
			if (this.canBeHarvested()) {
				this.getHarvestProducts(this::spawnItem);
				this.growthStage = this.plant.harvestStage();
				return InteractionResult.SUCCESS;
			}
		}
		if (heldItem.is(ModItems.CLIPPER.get()) || heldItem.is(ModItems.RAKE_IRON.get()) || heldItem.is(ModItems.RAKE_WOOD.get())) {
			return InteractionResult.PASS;
		}
		return InteractionResult.FAIL;
	}

	private void spawnItem(ItemStack stack) {
		this.level.addFreshEntity(new ItemEntity(this.level, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, stack));
	}

	@Override
	public void onRandomTick(AgriCrop crop, RandomSource random) {
		this.onApplyGrowthTick(crop, random);
	}

	/**
	 * Compute the harvest products for the crop
	 *
	 * @param addToHarvest consumer to add the products to
	 */
	public void getHarvestProducts(Consumer<ItemStack> addToHarvest) {
		if (!this.isMaxStage()) {
			return;
		}
		for (int trials = (this.genome.getStatGene(AgriStatRegistry.getInstance().gainStat()).getTrait() + 3) / 3; trials > 0; --trials) {
			for (AgriProduct product : this.plant.products()) {
				if (product.chance() > this.level.random.nextDouble()) {
					int amount = this.level.random.nextInt(product.min(), product.max() + 1);
					if (product.item().tag()) {
						// product is a tag, get a random item from it
						ItemStack[] items = Ingredient.of(TagKey.create(Registries.ITEM, product.item().id())).getItems();
						for (int i = 0; i < amount; ++i) {
							addToHarvest.accept(items[this.level.random.nextInt(items.length)].copy());
						}
					} else {
						// product is an item
						Item item = BuiltInRegistries.ITEM.get(product.item().id());
						ItemStack stack = item.getDefaultInstance();
						stack.setCount(amount);
						addToHarvest.accept(stack);
					}
				}
			}
		}
	}

	@Override
	public void getClippingProducts(Consumer<ItemStack> addToClipping) {
		if (!this.isMaxStage()) {
			return;
		}
		for (AgriProduct product : this.plant.clipProducts()) {
			if (product.chance() > this.level.random.nextDouble()) {
				if (product.item().tag()) {
					// product is a tag, get a random item from it
					ItemStack[] items = Ingredient.of(TagKey.create(Registries.ITEM, product.item().id())).getItems();
					addToClipping.accept(items[this.level.random.nextInt(items.length)].copy());
				} else {
					// product is an item
					Item item = BuiltInRegistries.ITEM.get(product.item().id());
					addToClipping.accept(item.getDefaultInstance());
				}
			}
		}
	}

	/**
	 * Apply a growth tick to the crop (plant+weed)
	 */
	@Override
	public void onApplyGrowthTick(AgriCrop crop, RandomSource random) {
		if (this.level == null || this.level.isClientSide()) {
			return;
		}
		AgriGrowthResponse fertility = this.getFertilityResponse();
		System.out.println(fertility);
		if (fertility.isInstantKill()) {
			// kill plant
			fertility.onPlantKilled(this);
			this.removeBlock();
		} else if (fertility.isLethal()) {
			// reverse growth stage
			this.growthStage--;
			if (this.growthStage < 0) {
				this.removeBlock();
			} else {
				this.setChanged();
				this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
			}
		} else if (fertility.isFertile()) {
			// plant growth tick
			this.executePlantGrowthTick();
		}
	}

	private void removeBlock() {
		if (this.getBlockState().getValue(BlockStateProperties.WATERLOGGED)) {
			level.setBlockAndUpdate(this.getBlockPos(), Fluids.WATER.defaultFluidState().createLegacyBlock());
		} else {
			level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
		}
	}

	/**
	 * Apply a growth tick to the plant
	 */
	protected void executePlantGrowthTick() {
		if (this.isMaxStage()) {
			return;
		}
		double a = this.calculateGrowthRate();
		double b = this.level.random.nextDouble();
		if (a > b) {
			this.growthStage++;
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	protected double calculateGrowthRate() {
		int growth = this.genome.getStatGene(AgriStatRegistry.getInstance().growthStat()).getTrait();
		double soilFactor = this.getSoil().map(AgriSoil::growthModifier).orElse(1.0D);
		return soilFactor * (this.plant.growthChance() + growth * this.plant.growthBonus() * CoreConfig.growthMultiplier);
	}

	/**
	 * Right click with a bonemeal interaction
	 *
	 * @see com.agricraft.agricraft.common.block.CropBlock#performBonemeal
	 */
	@Override
	public void onPerformBonemeal(AgriCrop crop, RandomSource random) {
		this.onApplyGrowthTick(crop, random);
	}

	@Override
	public void addMagnifyingTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		tooltip.add(Component.translatable("agricraft.tooltip.magnifying.crop"));
		tooltip.add(Component.literal("  ").plainCopy().append(Component.translatable("agricraft.tooltip.magnifying.species"))
				.append(LangUtils.plantName(genome.getSpeciesGene().getDominant().trait()))
				.append(Component.literal(" - ").plainCopy())
				.append(LangUtils.plantName(genome.getSpeciesGene().getRecessive().trait()))
		);
		AgriStatRegistry.getInstance().stream()
				.filter(stat -> !stat.isHidden())
				.map(stat -> this.genome.getStatGene(stat))
				.sorted(Comparator.comparing(p -> p.getGene().getId()))
				.map(genePair -> Component.translatable("agricraft.tooltip.magnifying.stat." + genePair.getGene().getId(),
						genePair.getDominant().trait(), genePair.getRecessive().trait()))
				.map(component -> Component.literal("  ").append(component))
				.forEach(tooltip::add);
		if (isPlayerSneaking) {
			tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.growth", this.growthStage + 1, this.plant.stages().size())));
		}
		AgriGrowthResponse response = this.getFertilityResponse();
		tooltip.add(Component.literal("  ").append(Component.translatable("agricraft.tooltip.magnifying.requirement." + (response.isLethal() ? "lethal" : response.isFertile() ? "fertile" : "not_fertile"))));
	}

}
