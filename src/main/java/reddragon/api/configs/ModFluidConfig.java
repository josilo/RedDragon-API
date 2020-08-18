package reddragon.api.configs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import reddragon.api.content.BlockHolder;
import reddragon.api.content.fluids.AbstractFluid;
import reddragon.api.content.fluids.VaporizingFluidBlock;
import reddragon.api.utils.FluidUtils;

public final class ModFluidConfig implements BlockHolder {
	private int color;
	private int levelDecreasePerBlock = 1;
	private int flowSpeed = 5;
	private boolean ticksRandomly = false;

	private VaporizingFluidBlock fluidBlock = null;

	// Set during registration:

	private BucketItem bucketItem;
	private FlowableFluid flowingFluid;
	private FlowableFluid stillFluid;

	private final Map<Supplier<Block>, Float> vaporizedResultChances = new HashMap<>();

	public ModFluidConfig() {
		// Use default values.
	}

	public ModFluidConfig color(final int color) {
		this.color = color;
		return this;
	}

	public ModFluidConfig levelDecreasePerBlock(final int levelDecreasePerBlock) {
		this.levelDecreasePerBlock = levelDecreasePerBlock;
		return this;
	}

	public ModFluidConfig ticksRandomly() {
		ticksRandomly = true;
		return this;
	}

	public ModFluidConfig flowSpeed(final int flowSpeed) {
		this.flowSpeed = flowSpeed;
		return this;
	}

	public void register(final String namespace, final ItemGroup itemGroup, final String name) {
		// Once we have all data, we can create required objects.

		stillFluid = new AbstractFluid.Still(
				() -> stillFluid,
				() -> flowingFluid,
				() -> fluidBlock,
				() -> bucketItem,
				levelDecreasePerBlock,
				flowSpeed);

		flowingFluid = new AbstractFluid.Flowing(
				() -> stillFluid,
				() -> flowingFluid,
				() -> fluidBlock,
				() -> bucketItem,
				levelDecreasePerBlock,
				flowSpeed);

		final Settings blockSettings = FabricBlockSettings.copy(Blocks.WATER);
		if (ticksRandomly) {
			blockSettings.ticksRandomly();
		}

		fluidBlock = new VaporizingFluidBlock(stillFluid, blockSettings);

		for (final Entry<Supplier<Block>, Float> chance : vaporizedResultChances.entrySet()) {
			fluidBlock.addVaporizedResultChance(chance.getKey(), chance.getValue());
		}

		bucketItem = new BucketItem(stillFluid, new Item.Settings().group(itemGroup).recipeRemainder(Items.BUCKET).maxCount(1));

		// Begin registering all data.

		final Identifier identifier = new Identifier(namespace, name.toLowerCase(Locale.ROOT));

		Registry.register(Registry.FLUID, identifier, stillFluid);
		Registry.register(Registry.FLUID, new Identifier(namespace, identifier.getPath() + "_flowing"), flowingFluid);

		Registry.register(Registry.BLOCK, identifier, fluidBlock);
		Registry.register(Registry.ITEM,
				new Identifier(namespace, identifier.getPath() + "_bucket"), bucketItem);

		FluidUtils.setupFluidRendering(stillFluid, flowingFluid, identifier, color);
	}

	public void addVaporizedResultChance(final Supplier<Block> block, final float weight) {
		vaporizedResultChances.put(block, weight);
	}

	@Override
	public VaporizingFluidBlock getBlock() {
		return fluidBlock;
	}

	public FlowableFluid getStillFluid() {
		return stillFluid;
	}
}
