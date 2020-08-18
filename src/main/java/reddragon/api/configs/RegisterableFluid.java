package reddragon.api.configs;

import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import reddragon.api.content.BlockHolder;

public interface RegisterableFluid {
	/**
	 * Requests the identifier name of this fluid. Usually you should not need to
	 * implement this when this interface is implemented by an enum.
	 */
	String name();

	/**
	 * Requests the item group this fluid should be added to.
	 */
	ItemGroup getItemGroup();

	/**
	 * Requests the identifier namespace of this fluid. This is usually the mod ID.
	 */
	String getNamespace();

	/**
	 * Requests the configuration of this fluid. The configuration is used
	 * internally when registering the fluid to the game.
	 */
	ModFluidConfig getConfig();

	default void register() {
		getConfig().register(getNamespace(), getItemGroup(), name());
	}

	default void addVaporizedResultChance(final Block block, final float weight) {
		getConfig().addVaporizedResultChance(() -> block, weight);

	}

	default void addVaporizedResultChance(final BlockHolder blockHolder, final float weight) {
		getConfig().addVaporizedResultChance(() -> blockHolder.getBlock(), weight);
	}
}