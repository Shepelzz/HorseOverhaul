package club.tesseract.horseoverhaul.config.type;

import club.tesseract.horseoverhaul.config.impl.ItemConfig;
import club.tesseract.horseoverhaul.item.Item;
import club.tesseract.horseoverhaul.utils.ComponentUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.Material;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static club.tesseract.horseoverhaul.utils.ComponentUtils.componentFormattedString;

public class SimpleItemConfig implements ConfigurationSerializable {

    private final Material material;
    private final String displayName;
    private final List<String> lore;

    public SimpleItemConfig(Material material, String displayName, List<String> lore) {
        this.material = material;
        this.displayName = displayName;
        this.lore = lore;
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public List<String> getLore() {
        return lore;
    }

    public ItemStack getItemStack(Item itemType, TagResolver... resolvers) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if(meta == null)return item;
        meta.setDisplayName(componentFormattedString(displayName, resolvers));
        meta.setLore(lore.stream().map( line -> ComponentUtils.componentFormattedString(line, resolvers)).collect(Collectors.toList()));
        meta.getPersistentDataContainer().set(ItemConfig.customItemKey, PersistentDataType.STRING, itemType.getUniqueId());
        item.setItemMeta(meta);
        return item;
    }

    @NotNull
    @SuppressWarnings("unused")
    public static SimpleItemConfig deserialize(Map<String, Object> map) {
        String material = (String) map.getOrDefault("material", "AIR");
        String displayName = (String) map.getOrDefault("displayName", "Invalid Display Name");
        List<String> lore = (List<String>) map.getOrDefault("lore", new ArrayList<String>());
        return new SimpleItemConfig(Material.matchMaterial(material), displayName, lore);
    }

    @NotNull
    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> result = new HashMap<>();
        result.put("material", material.name());
        result.put("displayName", displayName);
        result.put("lore", lore);
        return result;
    }
}
