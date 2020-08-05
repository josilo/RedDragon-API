package reddragon.api.content;

import net.minecraft.item.Item;

/**
 * Indicates that each class instance has a {@link Item} instance that can be
 * access via {@link #getItem()}.
 */
public interface ItemHolder {
	Item getItem();
}
