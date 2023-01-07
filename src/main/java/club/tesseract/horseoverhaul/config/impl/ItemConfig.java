package club.tesseract.horseoverhaul.config.impl;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.config.AbstractConfig;
import club.tesseract.horseoverhaul.config.type.CraftableConfig;
import club.tesseract.horseoverhaul.config.type.SimpleItemConfig;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class ItemConfig extends AbstractConfig {

    public ItemConfig() {
        super();
    }

    public static final NamespacedKey customItemKey = new NamespacedKey(HorseOverhaul.getInstance(), "custom_item");
    private CraftableConfig blankDeedItem;
    private CraftableConfig whistleItem;
    private SimpleItemConfig deedItem;


    @Override
    public String configName() {
        return "items.yml";
    }

    @Override
    protected void addDefaults() {
        this.customFile.addDefault("blank-deed-item.enabled", true);
        this.customFile.addDefault("blank-deed-item.crafting-recipe", new CraftableConfig(
                new NamespacedKey(HorseOverhaul.getInstance(), "blank_deed_item_recipe"),
                getDefaultBlankDeed(),
                Map.of("c", Material.GOLDEN_CARROT,"b",Material.WRITABLE_BOOK)
        ));
        this.customFile.addDefault("whistle-item.enabled", true);
        this.customFile.addDefault("whistle-item.crafting-recipe", new CraftableConfig(
                new NamespacedKey(HorseOverhaul.getInstance(), "whistle_horse_item_recipe"),
                getDefaultWhistle(),
                Map.of("c", Material.GOLDEN_CARROT,"b",Material.IRON_INGOT)
        ));

        this.customFile.addDefault("deed-item", getDefaultDeed());

        customFile.options().copyDefaults(true);
        customFile.options().header("HorseOverhaul Item Configuration\n\n");

        save();
    }

    @Override
    public void postLoad(){
        this.blankDeedItem = Objects.requireNonNull(this.customFile.getConfigurationSection("blank-deed-item")).getObject("crafting-recipe", CraftableConfig.class);
        this.whistleItem = Objects.requireNonNull(this.customFile.getConfigurationSection("whistle-item")).getObject("crafting-recipe", CraftableConfig.class);
        this.deedItem = this.customFile.getObject("deed-item", SimpleItemConfig.class);
        if(getDeedItemConfig().getMaterial() != Material.WRITTEN_BOOK){
            HorseOverhaul.getInstance().getLogger().warning("The deed item is not a written book. This will cause issues with the plugin. change this in items.yml");
        }
    }

    @NotNull
    public CraftableConfig getBlankDeedItem() {
        return blankDeedItem;
    }

    @NotNull
    public CraftableConfig getWhistleItem() {
        return whistleItem;
    }

    public boolean isBlankDeedItemEnabled() {
        return this.customFile.getBoolean("blank-deed-item.enabled");
    }

    public boolean isWhistleItemEnabled() {
        return this.customFile.getBoolean("whistle-item.enabled");
    }

    @NotNull
    public SimpleItemConfig getDeedItemConfig() {
        return deedItem;
    }

    @NotNull
    private static SimpleItemConfig getDefaultDeed(){
        return new SimpleItemConfig(Material.WRITTEN_BOOK, "<green>Deed to <horse>", Arrays.asList("<blue>Property of", "<blue><player>"));
    }

    @NotNull
    private static SimpleItemConfig getDefaultBlankDeed(){
        return new SimpleItemConfig(Material.PAPER, "<white>Blank Deed", Arrays.asList("<blue>Right click a horse to", "<blue>create a deed for it"));
    }

    @NotNull
    private static SimpleItemConfig getDefaultWhistle(){
        return new SimpleItemConfig(Material.IRON_NUGGET, "<white>Horse Whistle", Arrays.asList("<blue>Right click a horse to", "<blue>to link your whistle"));
    }



}
