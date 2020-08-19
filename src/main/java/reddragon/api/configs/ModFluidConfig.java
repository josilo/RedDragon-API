package reddragon.api.configs;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandler;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractBlock.Settings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.BlockRenderView;
import reddragon.api.content.BlockHolder;
import reddragon.api.content.fluids.AbstractFluid;
import reddragon.api.content.fluids.VaporizingFluidBlock;
import reddragon.api.utils.EnvironmentUtils;
import reddragon.api.utils.FluidUtils;

public final class ModFluidConfig implements BlockHolder {
	private VaporizingFluidBlock fluidBlock = null;

	// Builder parameters:

	private int tintColor = 0xFFFFFF;
	private int levelDecreasePerBlock = 1;
	private int flowSpeed = 5;
	private boolean ticksRandomly = false;
	private final Map<Supplier<Block>, Float> vaporizedResultChances = new HashMap<>();

	// Set during registration:

	private BucketItem bucketItem;
	private FlowableFluid flowingFluid;
	private FlowableFluid stillFluid;

	public ModFluidConfig() {
		// Use default values.
	}

	/**
	 * Defines the tint color of this fluid.
	 * <p>
	 * The sprite texture for the fluid is multiplied with the tint color to get the
	 * final appearance.
	 * <p>
	 * Defaults to white (0xFFFFFF) if unspecified.
	 *
	 * @see {@link FluidRenderHandler#getFluidColor(BlockRenderView, BlockPos, FluidState)}.
	 */
	public ModFluidConfig color(final int tintColor) {
		this.tintColor = tintColor;
		return this;
	}

	/**
	 * Defines the decrease of the fluid level per block in horizontal direction.
	 * <p>
	 * Defaults to 1 if unspecified.
	 */
	public ModFluidConfig levelDecreasePerBlock(final int levelDecreasePerBlock) {
		this.levelDecreasePerBlock = levelDecreasePerBlock;
		return this;
	}

	/**
	 * Allows this fluid to receive random ticks.
	 * <p>
	 * When one or more vaporization results are specified this method will be
	 * called implicitly.
	 */
	public ModFluidConfig ticksRandomly() {
		ticksRandomly = true;
		return this;
	}

	/**
	 * Defines the flow speed, that is the speed at which the fluid will spread
	 * horizontally and vertically.
	 * <p>
	 * Defaults to 5 if unspecified.
	 */
	public ModFluidConfig flowSpeed(final int flowSpeed) {
		this.flowSpeed = flowSpeed;
		return this;
	}

	/**
	 * Adds a possibility to the list of vaporization result blocks.
	 * <p>
	 * When the fluid vaporizes due to a a random tick, one of the given block
	 * possibilities is picked. The weight of each possibility determines the chance
	 * of this block to be picked.
	 *
	 * @param blockHolder The result block of this vaporization possibility.
	 * @param weight      The chance of this possibility in relation to all other
	 *                    registered possibilities. Higher values in comparison will
	 *                    make this block more likely to be picked.
	 */
	public ModFluidConfig vaporizesTo(final BlockHolder blockHolder, final float weight) {
		vaporizedResultChances.put(() -> blockHolder.getBlock(), weight);
		return ticksRandomly();
	}

	/**
	 * Adds a possibility to the list of vaporization result blocks.
	 * <p>
	 * When the fluid vaporizes due to a a random tick, one of the given block
	 * possibilities is picked. The weight of each possibility determines the chance
	 * of this block to be picked.
	 *
	 * @param blockHolder The result block of this vaporization possibility.
	 * @param weight      The chance of this possibility in relation to all other
	 *                    registered possibilities. Higher values in comparison will
	 *                    make this block more likely to be picked.
	 */
	public ModFluidConfig vaporizesTo(final Block block, final float weight) {
		vaporizedResultChances.put(() -> block, weight);
		return ticksRandomly();
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

		EnvironmentUtils.clientOnly(() -> () -> {
			FluidUtils.setupFluidRendering(stillFluid, flowingFluid, identifier, tintColor);
		});
	}

	@Override
	public VaporizingFluidBlock getBlock() {
		return fluidBlock;
	}

	public FlowableFluid getStillFluid() {
		return stillFluid;
	}
}
