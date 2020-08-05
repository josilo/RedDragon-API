package reddragon.api.configs;

import java.util.Locale;

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
	private FlowableFluid stillFluid = null;
	private FlowableFluid flowingFluid = null;
	private VaporizingFluidBlock fluidBlock = null;
	private BucketItem bucketItem = null;
	private final int color;

	public ModFluidConfig(final int color, final boolean ticksRandomly, final int levelDecreasePerBlock, final int tickRate, final ItemGroup itemGroup) {
		this.color = color;

		stillFluid = new AbstractFluid.Still(
				() -> stillFluid,
				() -> flowingFluid,
				() -> fluidBlock,
				() -> bucketItem,
				levelDecreasePerBlock,
				tickRate);

		flowingFluid = new AbstractFluid.Flowing(
				() -> stillFluid,
				() -> flowingFluid,
				() -> fluidBlock,
				() -> bucketItem,
				levelDecreasePerBlock,
				tickRate);

		final Settings blockSettings = FabricBlockSettings.copy(Blocks.WATER);
		if (ticksRandomly) {
			blockSettings.ticksRandomly();
		}
		fluidBlock = new VaporizingFluidBlock(stillFluid, blockSettings);

		bucketItem = new BucketItem(stillFluid, new Item.Settings().group(itemGroup).recipeRemainder(Items.BUCKET).maxCount(1));
	}

	public void register(final String namespace, final String name) {
		final Identifier identifier = new Identifier(namespace, name.toLowerCase(Locale.ROOT));

		Registry.register(Registry.FLUID, identifier, stillFluid);
		Registry.register(Registry.FLUID, new Identifier(namespace, identifier.getPath() + "_flowing"), flowingFluid);

		Registry.register(Registry.BLOCK, identifier, fluidBlock);
		Registry.register(Registry.ITEM,
				new Identifier(namespace, identifier.getPath() + "_bucket"), bucketItem);

		FluidUtils.setupFluidRendering(stillFluid, flowingFluid, identifier, color);
	}

	public void addVaporizedResultChance(final Block block, final float weight) {
		fluidBlock.addVaporizedResultChance(block, weight);
	}

	public void addVaporizedResultChance(final BlockHolder blockHolder, final float weight) {
		fluidBlock.addVaporizedResultChance(blockHolder.getBlock(), weight);
	}

	@Override
	public VaporizingFluidBlock getBlock() {
		return fluidBlock;
	}
}
