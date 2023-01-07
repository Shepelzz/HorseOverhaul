package club.tesseract.horseoverhaul.config.type;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.config.impl.ItemConfig;
import club.tesseract.horseoverhaul.item.Item;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CraftableConfig implements ConfigurationSerializable {


    private final NamespacedKey key;
    private final String recipeType;
    private final SimpleItemConfig result;
    private final Map<String, Material> materials;
    private String[] shape;

    public CraftableConfig(NamespacedKey key, SimpleItemConfig result, Map<String, Material> materials, String[] shape) {
        this.key = key;
        this.recipeType = "shaped";
        this.result = result;
        this.materials = materials;
        this.shape = shape;
    }
    public CraftableConfig(NamespacedKey key, SimpleItemConfig result, Map<String, Material> materials) {
        this.key = key;
        this.recipeType = "shapeless";
        this.result = result;
        this.materials = materials;
    }


    public NamespacedKey getKey() {
        return key;
    }

    @Nullable
    public Recipe getRecipe(Item itemType){
        ItemStack item = getItemStack(itemType);
        if(recipeType.equalsIgnoreCase("shaped")){
            ShapedRecipe recipe = new ShapedRecipe(key, item);
            recipe.shape(shape);
            for(Map.Entry<String, Material> entry : materials.entrySet()){
                recipe.setIngredient(entry.getKey().charAt(0), entry.getValue());
            }
            return recipe;
        }
        if(recipeType.equalsIgnoreCase("shapeless")){
            ShapelessRecipe recipe = new ShapelessRecipe(key, item);
            for(Map.Entry<String, Material> entry : materials.entrySet()){
                recipe.addIngredient(entry.getValue());
            }
            return recipe;
        }
        return null;
    }

    @NotNull
    public ItemStack getItemStack(Item itemType){
        ItemStack item = this.result.getItemStack(itemType);
        if(item.getType() == Material.AIR){
            HorseOverhaul.getInstance().getLogger().warning("Item " + itemType.name() + " has no result item set!");
            return item;
        }
        ItemMeta meta = item.getItemMeta();
        assert meta != null;
        meta.getPersistentDataContainer().set(ItemConfig.customItemKey, PersistentDataType.STRING, itemType.getUniqueId());
        item.setItemMeta(meta);
        return item;
    }


    @NotNull
    public Material getMaterial(){
        return result.getMaterial();
    }



    @NotNull
    @SuppressWarnings("unused")
    public static CraftableConfig deserialize(Map<String, Object> serialized){
        NamespacedKey key = new NamespacedKey(HorseOverhaul.getInstance(),(String) serialized.get("key"));
        String recipeType = (String) serialized.getOrDefault("recipe-type", "shaped");
        SimpleItemConfig result = (SimpleItemConfig) serialized.getOrDefault("result", new SimpleItemConfig(Material.AIR, "air", List.of("air")));
        Map<String, String> materials = (Map<String, String>) serialized.getOrDefault("materials", new HashMap<>());
        Map<String, Material> materialMap = new HashMap<>();
        for(Map.Entry<String, String> entry : materials.entrySet()){
            final Material mat =  Material.matchMaterial(entry.getValue());
            if(mat == null)continue;
            materialMap.put(entry.getKey(), mat);
        }
        String[] shape = (String[]) serialized.getOrDefault("shape", new String[]{"AAA", "AAA", "AAA"});
        if(recipeType.equalsIgnoreCase("shaped")){
            return new CraftableConfig(key, result, materialMap, shape);
        }else{
            return new CraftableConfig(key, result, materialMap);
        }
    }
    @Override
    public @NotNull Map<String, Object> serialize() {
        HashMap<String, Object> serialized = new HashMap<>();
        serialized.put("key", key.getKey());
        serialized.put("recipe-type", recipeType);
        serialized.put("result", result);
        Map<String, String> materials = new HashMap<>();
        this.materials.forEach((String, material) -> materials.put(String, material.name()));
        serialized.put("materials", materials);
        serialized.put("shape", shape);
        return serialized;
    }
}
