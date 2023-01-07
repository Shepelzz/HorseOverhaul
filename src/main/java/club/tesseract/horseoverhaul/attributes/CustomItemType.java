package club.tesseract.horseoverhaul.attributes;

import club.tesseract.horseoverhaul.item.Item;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class CustomItemType implements PersistentDataType<String, Item> {


    @NotNull
    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @NotNull
    @Override
    public Class<Item> getComplexType() {
        return Item.class;
    }

    @NotNull
    @Override
    public String toPrimitive(@NotNull Item complex, @NotNull PersistentDataAdapterContext context) {
        return complex.getUniqueId();
    }


    @NotNull
    @Override
    public Item fromPrimitive(@NotNull String primitive, @NotNull PersistentDataAdapterContext context) {
        Item item = Item.fromUniqueId(primitive);
        if(item == null)return Item.NULL;
        return item;
    }
}
