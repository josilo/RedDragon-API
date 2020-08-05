package reddragon.api.configs;

import java.util.Locale;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import reddragon.api.content.BlockHolder;

public class ModBlockConfig implements BlockHolder {

	private Block block = null;

	public ModBlockConfig(final AbstractBlock.Settings settings) {
		block = new Block(settings);
	}

	public void register(final String namespace, final String name) {
		final Identifier identifier = new Identifier(namespace, name.toLowerCase(Locale.ROOT));

		Registry.register(Registry.BLOCK, identifier, block);

	}

	@Override
	public Block getBlock() {
		return block;
	}

}
