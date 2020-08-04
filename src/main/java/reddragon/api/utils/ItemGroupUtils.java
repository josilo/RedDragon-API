package reddragon.api.utils;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ItemGroupUtils {
	private static final String ITEM_GROUP_IDENTIFIER_PATH = "item_group";

	public static ItemGroup createItemGroup(final String namespace, final ItemConvertible icon) {
		return FabricItemGroupBuilder.create(
				new Identifier(namespace, ITEM_GROUP_IDENTIFIER_PATH))
				.icon(() -> new ItemStack(icon))
				.build();
	}
}
