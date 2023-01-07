package club.tesseract.horseoverhaul.item;

import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public enum Item {
    NULL(Material.AIR, ""),
    OWNED_DEED_ITEM(Material.WRITTEN_BOOK, "owned_deed_item"),
    BLANK_DEED(Material.PAPER, "blank_dead_item"),
    WHISTLE(Material.IRON_NUGGET, "whistle_item");

    private final Material material;
    private final String uniqueId;

    Item(Material material, String uniqueId) {
        this.material = material;
        this.uniqueId = uniqueId;
    }

    public Material getMaterial() {
        return material;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public boolean isEqual(@Nullable ItemStack itemStack){
        if(itemStack == null) return false;
        Item otherItem = PersistentAttribute.CUSTOM_ITEM.getData(itemStack);
        return otherItem == this;
    }

    @Nullable
    public static Item fromName(String name) {
        for (Item item : Item.values()) {
            if (item.name().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
    }

    @Nullable
    public static Item fromUniqueId(String uniqueId) {
        for (Item item : Item.values()) {
            if (item.getUniqueId().equalsIgnoreCase(uniqueId)) {
                return item;
            }
        }
        return null;
    }

}
