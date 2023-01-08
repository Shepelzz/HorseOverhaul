package club.tesseract.horseoverhaul.listener;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.NamePrompt;
import club.tesseract.horseoverhaul.attributes.PersistentAttribute;
import club.tesseract.horseoverhaul.config.ConfigManager;
import club.tesseract.horseoverhaul.config.type.SimpleItemConfig;
import club.tesseract.horseoverhaul.item.Item;
import club.tesseract.horseoverhaul.utils.ComponentUtils;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.conversations.Conversation;
import org.bukkit.conversations.ConversationFactory;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class OwnershipListener implements Listener {

    public static boolean OWNERSHIP_ENABLED, COLOURED_NAMES, craftDeed;


    private static ItemStack getDeed(UUID horseID, String horsey, UUID pID, String pname) {

        SimpleItemConfig deedItemConfig = ConfigManager.get().itemConfig.getDeedItemConfig();
        ItemStack deed = deedItemConfig.getItemStack(Item.OWNED_DEED_ITEM,
                Placeholder.parsed("horse_uuid", horseID.toString()),
                Placeholder.parsed("horse", horsey),
                Placeholder.parsed("player_uuid", pID.toString()),
                Placeholder.parsed("player", pname)
        );

        ItemMeta meta = deed.getItemMeta();
        if (meta == null) return deed;

        PersistentAttribute.DEED_OWNER.setData(meta, pID);
        PersistentAttribute.DEED_HORSE_ID.setData(meta, horseID);

        if (!(meta instanceof BookMeta)) {
            deed.setItemMeta(meta);
            return deed;
        }
        ((BookMeta) meta).setAuthor(null);
        ((BookMeta) meta).setGeneration(Generation.ORIGINAL);

        deed.setItemMeta(meta);

        return deed;
    }

    /**
     * Handles the claiming of a horse
     *
     * @param abHorse   the horse to claim
     * @param player    the player claiming the horse
     * @param horseName the name of the horse
     */
    public static void claimHorse(AbstractHorse abHorse, Player player, String horseName) {

        OfflinePlayer previousOwnerPlayer;
        String previousOwner = "nature";

        UUID currentOwner = PersistentAttribute.OWNER.getData(abHorse);

        if (player.getUniqueId().equals(currentOwner)) return;
        if (abHorse.getOwner() != null && currentOwner != null) {
            previousOwnerPlayer = Bukkit.getOfflinePlayer(currentOwner);
            previousOwner = previousOwnerPlayer.getName() != null ? previousOwnerPlayer.getName() : "Unknown";
        }

        abHorse.setOwner(player);
        PersistentAttribute.OWNER.setData(abHorse, player.getUniqueId());
        abHorse.setCustomName(horseName);

        abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ARMOR, 0.9f, 2.0f);
        ComponentUtils.sendConfigMessage(player, "horse.claim.success",
                Placeholder.parsed("horse", horseName),
                Placeholder.parsed("previous_owner", previousOwner)
        );

        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 0.5f);

        String consoleInformation = player.getName() + " claims horse " + horseName + " from " +
                previousOwner + ", in world: " + abHorse.getWorld().getName() +
                ", at coords: x=" + (int) abHorse.getLocation().getX() +
                " y=" + (int) abHorse.getLocation().getY() +
                " z=" + (int) abHorse.getLocation().getZ();

        HorseOverhaul.getInstance().getLogger().info(consoleInformation);

        new BukkitRunnable() {
            @Override
            public void run() {

                player.getInventory().addItem(getDeed(abHorse.getUniqueId(), abHorse.getCustomName(), player.getUniqueId(), player.getName()));

            }
        }.runTaskLater(HorseOverhaul.getInstance(), 4);


    }

    /**
     * Handles the horses inventory click events
     * prevent the player from taking the horse out of the inventory
     * if they are not the owner
     */
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getClickedInventory() instanceof HorseInventory)) return;
        HorseInventory inventory = (HorseInventory) event.getClickedInventory();
        if(!(inventory.getHolder() instanceof AbstractHorse))return;
        AbstractHorse horse = (AbstractHorse) event.getClickedInventory().getHolder();
        if(horse == null) return;
        Player player = (Player) event.getWhoClicked();
        if (horse.getOwner() != null && !horse.getOwner().equals(player)) {
            event.setCancelled(true);
        }
    }

    /**
     * Handles friendly fire on horses
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof AbstractHorse)) return;
        Player player = null;
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player) {
                player = (Player) arrow.getShooter();
            }
        } else if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        }
        if (player == null) return;
        AbstractHorse abHorse = (AbstractHorse) event.getEntity();
        if (abHorse.getOwner() != null && player.getUniqueId().equals(PersistentAttribute.OWNER.getData(abHorse))) {
            if (event.getEntity() instanceof Horse && ((Horse) event.getEntity()).getInventory().getArmor() != null) {
                if (abHorse.getOwner().getUniqueId().equals(player.getUniqueId())) {
                    event.setCancelled(true);
                    abHorse.getWorld().playSound(abHorse.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.5F, 1.3F);
                }
            }
        }
    }

    /**
     * Handles the neutering of the horse
     */
    @EventHandler
    public void onNeuter(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof AbstractHorse)) return;
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        AbstractHorse abHorse = (AbstractHorse) event.getRightClicked();
        boolean isOwner = abHorse.getOwner() != null && player.getUniqueId().equals(PersistentAttribute.OWNER.getData(abHorse));

        if (!isOwner) return;

        if (!abHorse.isAdult() && Material.SHEARS.equals(main.getType())) {

            boolean isNeutered = PersistentAttribute.NEUTERED.getData(abHorse, (byte) 0) == (byte) 1;
            if (isNeutered) {
                ComponentUtils.sendConfigMessage(player, "horse.interaction.already-neutered");
                return;
            }

            PersistentAttribute.NEUTERED.setData(abHorse, (byte) 1);

            abHorse.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 20, 10));
            abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_DEATH, 1.2f, 1.5f);

            new BukkitRunnable() {
                @Override
                public void run() {
                    abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_CHICKEN_EGG, 0.9f, 1.3f);
                    ComponentUtils.sendConfigMessage(player, "horse.interaction.neutered");
                }
            }.runTaskLater(HorseOverhaul.getInstance(), 20L);


        }

    }

    /**
     * Handles the ownership of horses
     */
    @EventHandler
    public void onClaimHorse(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof AbstractHorse)) return;
        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        AbstractHorse abHorse = (AbstractHorse) event.getRightClicked();
        boolean hasOwner = abHorse.getOwner() != null && PersistentAttribute.OWNER.getData(abHorse) != null;

        if (hasOwner) return;

        if (!Item.BLANK_DEED.isEqual(main)) return;

        if (abHorse.isTamed()) {

            event.setCancelled(true);

            if (player.isConversing()) {
                ComponentUtils.sendConfigMessage(player, "horse.claim.is-conversing");
                return;
            }

            if (!player.hasPermission("horseo.claim-wild")) {
                ComponentUtils.sendConfigMessage(player, "horse.claim.no-permission");
                return;
            }

            player.getInventory().getItemInMainHand().setAmount(player.getInventory().getItemInMainHand().getAmount() - 1);

            ConversationFactory cf = new ConversationFactory(HorseOverhaul.getInstance());
            Conversation conv = cf.withFirstPrompt(new NamePrompt(player, abHorse)).withLocalEcho(true).buildConversation(player);
            conv.begin();

        } else {
            ComponentUtils.sendConfigMessage(player, "horse.claim.not-tamed");
        }

    }

    /**
     * Handles the renaming of a horse
     */
    @EventHandler
    public void onRenameHorse(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof AbstractHorse)) return;

        Player player = event.getPlayer();
        ItemStack main = player.getInventory().getItemInMainHand();
        ItemStack off = player.getInventory().getItemInOffHand();
        AbstractHorse abHorse = (AbstractHorse) event.getRightClicked();
        boolean isOwner = abHorse.getOwner() != null && player.getUniqueId().equals(PersistentAttribute.OWNER.getData(abHorse));

        if (Material.NAME_TAG.equals(main.getType()) && main.getItemMeta().hasDisplayName()) {
            event.setCancelled(true);
            if (isOwner) {
                boolean isOwnedDeed = Item.OWNED_DEED_ITEM.isEqual(off);
                UUID horseID = PersistentAttribute.DEED_HORSE_ID.getData(off);
                UUID ownerID = PersistentAttribute.DEED_OWNER.getData(off);
                if (isOwnedDeed) {
                    if (horseID == abHorse.getUniqueId() && ownerID == player.getUniqueId()) {
                        player.getInventory().setItemInOffHand(getDeed(abHorse.getUniqueId(), main.getItemMeta().getDisplayName(), player.getUniqueId(), player.getName()));
                        return;
                    }
                }
                ComponentUtils.sendConfigMessage(player, "horse.rename.not-holding-deed");

            } else {
                ComponentUtils.sendConfigMessage(player, "horse.rename.not-owned");
            }
        }
    }

    /**
     * Handles changing status of a horse, Publicly accessible or not
     */
    @EventHandler
    public void onToggleStatus(PlayerInteractEntityEvent event){
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!(event.getRightClicked() instanceof AbstractHorse)) return;

        Player player = event.getPlayer();
        AbstractHorse abHorse = (AbstractHorse) event.getRightClicked();
        boolean isOwner = abHorse.getOwner() != null && player.getUniqueId().equals(PersistentAttribute.OWNER.getData(abHorse));
        boolean hasOwner = abHorse.getOwner() != null && PersistentAttribute.OWNER.getData(abHorse) != null;
        ItemStack main = player.getInventory().getItemInMainHand();

        if (!abHorse.isTamed()) return;
        if (!isOwner || !hasOwner) return;
        if(!Item.OWNED_DEED_ITEM.isEqual(main)) return;

        if(PersistentAttribute.PUBLIC_RIDEABLE.getData(abHorse, (byte) 0) == (byte) 1){
            PersistentAttribute.PUBLIC_RIDEABLE.setData(abHorse, (byte) 0);
            ComponentUtils.sendConfigMessage(player, "horse.status.private");
        } else {
            PersistentAttribute.PUBLIC_RIDEABLE.setData(abHorse, (byte) 1);
            ComponentUtils.sendConfigMessage(player, "horse.status.public");
        }
    }

    /**
     * Handles transferring of deed ownership
     * and prevent non owners interacting with horse
     */
    @EventHandler
    public void onClickHorse(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;

        if (!(event.getRightClicked() instanceof AbstractHorse)) return;

        Player player = event.getPlayer();
        AbstractHorse abHorse = (AbstractHorse) event.getRightClicked();
        boolean isOwner = abHorse.getOwner() != null && player.getUniqueId().equals(PersistentAttribute.OWNER.getData(abHorse));
        boolean hasOwner = abHorse.getOwner() != null && PersistentAttribute.OWNER.getData(abHorse) != null;
        ItemStack main = player.getInventory().getItemInMainHand();

        if (!abHorse.isTamed()) return;
        if (isOwner || !hasOwner) return;

        if (Item.OWNED_DEED_ITEM.isEqual(main)) {
            event.setCancelled(true);

            BookMeta met = (BookMeta) main.getItemMeta();

            UUID horseId = PersistentAttribute.DEED_HORSE_ID.getData(main);

            if (abHorse.getUniqueId().equals(horseId)) {

                if (Generation.ORIGINAL.equals(met.getGeneration())) {

                    if (!player.hasPermission("horseo.claim-owned")) {
                        ComponentUtils.sendConfigMessage(player, "horse.claim.not-allowed");
                        return;
                    }
                    player.getInventory().setItemInMainHand(null);
                    claimHorse(abHorse, player, abHorse.getCustomName());

                } else {

                    ComponentUtils.sendConfigActionBar(player, "deed.original-required");
                    abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);

                }

                return;
            }
        }

        if (player.hasPermission("horseo.interact-all")) return;
        if(PersistentAttribute.PUBLIC_RIDEABLE.getData(abHorse, (byte)0) == (byte)1) return;
        event.setCancelled(true);

        abHorse.getWorld().playSound(abHorse.getLocation(), Sound.ENTITY_HORSE_ANGRY, 0.8F, 1.0F);

        OfflinePlayer owner = Bukkit.getOfflinePlayer(abHorse.getOwner().getUniqueId());
        String username = owner.getName() != null ? owner.getName() : "Unknown";
        String horseName = abHorse.getCustomName() != null ? abHorse.getCustomName() : "Horse";

        ComponentUtils.sendConfigActionBar(player, "horse.interaction.not-owner",
                Placeholder.parsed("owner", username),
                Placeholder.parsed("owner_uuid", owner.getUniqueId().toString()),
                Placeholder.parsed("horse", horseName)
        );
    }

    /**
     * Prevent opening of deed book.
     */
    @EventHandler
    public void onReadableBookInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (!Item.OWNED_DEED_ITEM.isEqual(item)) return;
        event.setUseItemInHand(Result.DENY);
    }
}