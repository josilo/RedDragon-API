package reddragon.api.configs;

import static net.minecraft.item.ItemGroup.FOOD;

import java.util.Locale;

import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import reddragon.api.content.ItemHolder;

public class ModItemConfig implements ItemHolder {

	private Item item;

	public ModItemConfig(final Item.Settings settings) {
		item = new Item(settings);
	}

	public ModItemConfig(final FoodComponent foodComponent, int maxCount) {
		this(new Settings().group(FOOD).food(foodComponent).maxCount(maxCount));
	}

	public ModItemConfig(final FoodComponent foodComponent, int maxCount, Item recipeRemainder) {
		this(new Settings().group(FOOD).food(foodComponent).maxCount(maxCount).recipeRemainder(recipeRemainder));
	}

	public void register(final String namespace, final String name) {
		final Identifier identifier = new Identifier(namespace, name.toLowerCase(Locale.ROOT));

		Registry.register(Registry.ITEM, identifier, item);
	}

	@Override
	public Item getItem() {
		return item;
	}
}
