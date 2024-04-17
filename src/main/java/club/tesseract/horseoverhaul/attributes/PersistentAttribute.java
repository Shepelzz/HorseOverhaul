package club.tesseract.horseoverhaul.attributes;

import club.tesseract.horseoverhaul.HorseOverhaul;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public enum PersistentAttribute {
    PUBLIC_RIDEABLE("public_rideable", PersistentDataType.BYTE, (byte) 0),
    NERFED("nerfed", PersistentDataType.BYTE, (byte) 1),
    NEUTERED("neutered", PersistentDataType.BYTE, (byte) 0),
    OWNER("owner", new UUIDDataType(), null),
    CUSTOM_ITEM("custom_item", new CustomItemType(), null),
    DEED_HORSE_ID("deed_horse_id", new UUIDDataType(), null),
    DEED_OWNER("deed_owner", new UUIDDataType(), null),
    WHISTLE_HORSE_ID("whistle_horse_id", new UUIDDataType(), null),
    OWNER_COUNT("owner_count", PersistentDataType.INTEGER, 0)

    ;


    private final String key;
    private final PersistentDataType<?,?> type;
    private final Object defaultValue;

    PersistentAttribute(String key, PersistentDataType<?,?> type, Object defaultValue) {
        this.key = key;
        this.type = type;
        this.defaultValue = defaultValue;
    }


    @NotNull
    public <T> T getData(@NotNull PersistentDataHolder holder, T defaultValue){
        T data = (T) holder.getPersistentDataContainer().get(getKey(), type);
        if(data == null) return defaultValue;
        return data;
    }

    @Nullable
    public <T> T getData(@NotNull PersistentDataHolder holder) {
        if(!holder.getPersistentDataContainer().has(getKey(), getType()))return null;
        return (T) holder.getPersistentDataContainer().get(getKey(), getType());
    }

    @Nullable
    public <T> T getData(@NotNull ItemStack itemStack){
        if(!itemStack.hasItemMeta())return null;
        return getData(itemStack.getItemMeta());
    }

    public void setData(@NotNull PersistentDataHolder holder, Object value) {
        holder.getPersistentDataContainer().set(getKey(), getType(), value);
    }

    public void addOwnerCounter() {

    }

    public void setData(@NotNull ItemStack itemStack, Object value) {
        if(!itemStack.hasItemMeta())itemStack.setItemMeta(HorseOverhaul.getInstance().getServer().getItemFactory().getItemMeta(itemStack.getType()));
        setData(itemStack.getItemMeta(), value);
    }

    @NotNull
    public NamespacedKey getKey(){
        return new NamespacedKey(HorseOverhaul.getInstance(), key);
    }

    @NotNull
    public PersistentDataType getType(){
        return type;
    }

    @NotNull
    public Object getDefaultValue(){
        return defaultValue;
    }

}
