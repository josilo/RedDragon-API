package reddragon.api.configs;

import java.util.Locale;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import reddragon.api.content.BlockHolder;

public class ModBlockConfig implements BlockHolder {

	private Block block = null;

	public ModBlockConfig(final Block block) {
		this.block = block;
	}

	public ModBlockConfig(final AbstractBlock.Settings settings) {
		this(new Block(settings));
	}

	public void register(final String namespace, ItemGroup itemGroup, final String name) {
		final Identifier identifier = new Identifier(namespace, name.toLowerCase(Locale.ROOT));

		Registry.register(Registry.BLOCK, identifier, block);
		Registry.register(Registry.ITEM, identifier, new BlockItem(block, new Item.Settings().group(itemGroup)));
	}

	@Override
	public Block getBlock() {
		return block;
	}

}
