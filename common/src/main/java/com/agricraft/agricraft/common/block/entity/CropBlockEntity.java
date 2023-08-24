package com.agricraft.agricraft.common.block.entity;

import com.agricraft.agricraft.api.IHaveMagnifyingInformation;
import com.agricraft.agricraft.api.codecs.AgriPlant;
import com.agricraft.agricraft.api.codecs.AgriProduct;
import com.agricraft.agricraft.api.genetic.AgriGenome;
import com.agricraft.agricraft.api.stat.AgriStatRegistry;
import com.agricraft.agricraft.common.config.CoreConfig;
import com.agricraft.agricraft.common.registry.ModBlockEntityTypes;
import com.agricraft.agricraft.common.util.LangUtils;
import com.agricraft.agricraft.common.util.PlatformUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CropBlockEntity extends BlockEntity implements IHaveMagnifyingInformation {

	private AgriGenome genome;
	private String plantId = "";
	private AgriPlant plant;
	private int growthStage = 0;
	private final Map<Integer, VoxelShape> shapeByAge = new HashMap<>();

	public CropBlockEntity(BlockPos blockPos, BlockState blockState) {
		super(ModBlockEntityTypes.CROP.get(), blockPos, blockState);
	}

	/**
	 * Change the genome of the plant.
	 * It will also change the plant to match the new genome.
	 * @param genome the new genome of the crop
	 */
	public void setGenome(AgriGenome genome) {
		this.genome = genome;
		this.plantId = genome.getSpeciesGene().getDominant().trait();
		this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
		this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
	}

	public String getPlantId() {
		return this.plantId;
	}

	public AgriPlant getPlant() {
		return this.plant;
	}

	public AgriGenome getGenome() {
		return genome;
	}

	public int getGrowthStage() {
		return this.growthStage;
	}

	public int getGrowthPercent() {
		return (this.growthStage + 1) * 100 / this.plant.stages().size();
	}

	public boolean isMaxStage() {
		return this.growthStage == this.plant.stages().size() - 1;
	}

	public int getPlantHeight() {
		return this.plant.stages().get(this.growthStage);
	}

	/**
	 * @return the block shape of the crop
	 */
	public VoxelShape getShape() {
		if (this.plant == null) {
			return Shapes.empty();
		}
		return shapeByAge.computeIfAbsent(this.growthStage, stage -> Block.box(0, 0, 0, 16, this.plant.stages().get(stage), 16));
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
			this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
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
			this.plant = level.registryAccess().registry(PlatformUtils.getPlantRegistryKey()).get().get(new ResourceLocation(this.plantId));
			level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	/**
	 * Right click interaction
	 * @see com.agricraft.agricraft.common.block.CropBlock#use
	 */
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
		// do nothing from off hand
		if (hand == InteractionHand.OFF_HAND) {
			return InteractionResult.PASS;
		}
		// harvesting
		if (this.isMaxStage()) {
			this.getHarvestProducts(this::spawnItem, level.random);
			this.growthStage = this.plant.harvestStage();
			return InteractionResult.SUCCESS;
		}
		return InteractionResult.FAIL;
	}

	private void spawnItem(ItemStack stack) {
		this.level.addFreshEntity(new ItemEntity(this.level, this.getBlockPos().getX() + 0.5, this.getBlockPos().getY() + 0.5, this.getBlockPos().getZ() + 0.5, stack));
	}

	/**
	 * Random tick interaction
	 * @see com.agricraft.agricraft.common.block.CropBlock#randomTick
	 */
	public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		System.out.println("boop");
		this.applyGrowthTick();
	}

	/**
	 * Compute the harvest products for the crop
	 * @param addToHarvest consumer to add the products to
	 * @param random a random generator
	 */
	public void getHarvestProducts(Consumer<ItemStack> addToHarvest, RandomSource random) {
		if (!this.isMaxStage()) {
			return;
		}
		for (int trials = (this.genome.getStatGene(AgriStatRegistry.getInstance().gainStat()).getTrait() + 3) / 3; trials > 0; --trials) {
			for (AgriProduct product : this.plant.products()) {
				if (product.chance() > random.nextDouble()) {
					int amount = random.nextInt(product.min(), product.max() + 1);
					ItemStack stack;
					if (product.item().tag()) {
						// product is a tag, get a random item from it
						ItemStack[] items = Ingredient.of(TagKey.create(Registries.ITEM, product.item().id())).getItems();
						for (int i = 0; i < amount; ++i) {
							addToHarvest.accept(items[random.nextInt(items.length)].copy());
						}
					} else {
						// product is an item
						Item item = BuiltInRegistries.ITEM.get(product.item().id());
						stack = item.getDefaultInstance();
						stack.setCount(amount);
						addToHarvest.accept(stack);
					}
				}
			}
		}
	}

	/**
	 * Apply a growth tick to the crop (plant+weed)
	 */
	public void applyGrowthTick() {
		if (this.level == null || this.level.isClientSide()) {
			return;
		}
		this.executePlantGrowthTick();

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
		System.out.printf("%.2f | %.2f%n", a, b);
		if (a > b) {
			this.growthStage++;
			this.setChanged();
			this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), Block.UPDATE_ALL);
		}
	}

	protected double calculateGrowthRate() {
		int growth = this.genome.getStatGene(AgriStatRegistry.getInstance().growthStat()).getTrait();
		double soilFactor = 1.0;
		return soilFactor * (this.plant.growthChance() + growth * this.plant.growthBonus() * CoreConfig.growthMultiplier);
	}

	/**
	 *  Right click with a bonemeal interaction
	 * 	@see com.agricraft.agricraft.common.block.CropBlock#performBonemeal
	 */
	public void performBonemeal() {
		this.applyGrowthTick();
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
			tooltip.add(Component.literal("  ").plainCopy().append(Component.translatable("agricraft.tooltip.magnifying.growth", this.growthStage + 1, this.plant.stages().size())));
		}
		return true;
	}

}
