package reddragon.api.content.fluids;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.fluid.FlowableFluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;

public class DryingFluidBlock extends FluidBlock {

	public static final int MIN_LIGHT_LEVEL_FOR_DRYING = 13;

	public DryingFluidBlock(final FlowableFluid fluid, final Settings properties) {
		super(fluid, properties);
	}

	private class DriedResult {
		public Supplier<Block> block;
		public float accumulatedWeight;
	}

	private final List<DriedResult> driedResults = new ArrayList<>();
	private float accumulatedWeight;

	public void addDriedResult(final Supplier<Block> block, final float weight) {
		accumulatedWeight += weight;

		final DriedResult driedResult = new DriedResult();
		driedResult.accumulatedWeight = accumulatedWeight;
		driedResult.block = block;

		driedResults.add(driedResult);
	}

	public Block getDriedResult(final World world) {
		final float randomValue = world.random.nextFloat() * accumulatedWeight;

		for (final DriedResult driedResult : driedResults) {
			if (driedResult.accumulatedWeight >= randomValue) {
				return driedResult.block.get();
			}
		}

		return Blocks.WATER; // should only happen when there are no entries
	}

	@Override
	public void randomTick(final BlockState state, final ServerWorld world, final BlockPos pos, final Random random) {
		if (canDry(state, world, pos)) {
			dry(state, world, pos);
		}
	}

	private boolean canDry(final BlockState state, final ServerWorld world, final BlockPos pos) {
		if (!isSourceBlock(state)) {
			return false;
		}

		if (world.getLightLevel(pos.up(), 0) < MIN_LIGHT_LEVEL_FOR_DRYING) {
			return false;
		}

		if (isWaterNearby(world, pos, 1, 7)) {
			return false;
		}

		if (world.hasRain(pos.up())) {
			return false;
		}

		return true;
	}

	private void dry(final BlockState state, final World world, final BlockPos pos) {

		final Block resultBlock = getDriedResult(world);

		world.setBlockState(pos, resultBlock.getDefaultState(), 3);
		world.syncWorldEvent(1501, pos, 0); // ExtingushEvent
	}

	private boolean isSourceBlock(final BlockState state) {
		final FluidState fluidState = getFluidState(state);
		return fluidState.isStill();
	}

	private boolean isWaterNearby(final WorldView world, final BlockPos pos, final int radius, final int sources) {
		int count = 0;
		final Iterator<BlockPos> var2 = BlockPos.iterate(pos.add(-radius, -1, -radius), pos.add(radius, 0, radius))
				.iterator();

		BlockPos blockPos;
		do {
			if (!var2.hasNext()) {
				return false;
			}
			blockPos = var2.next();
			final FluidState fluidState = world.getFluidState(blockPos);
			if (fluidState.isIn(FluidTags.WATER) && fluidState.isStill()) {
				count++;
			}
		} while (count <= sources);

		return true;
	}

	@Override
	public boolean hasRandomTicks(final BlockState state) {
		return true;
	}

}
