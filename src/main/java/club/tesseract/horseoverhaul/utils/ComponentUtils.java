package club.tesseract.horseoverhaul.utils;

import club.tesseract.horseoverhaul.HorseOverhaul;
import club.tesseract.horseoverhaul.config.impl.MessageConfig;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ComponentUtils {

    public static void sendMessage(CommandSender player, Component component) {
        HorseOverhaul.getInstance().adventure().sender(player).sendMessage(component);
    }

    public static void sendMessage(CommandSender player, String message, TagResolver... resolvers) {
       sendMessage(player, MiniMessage.miniMessage().deserialize(message, resolvers));
    }

    public static void sendConfigMessage(CommandSender player, String path, TagResolver... resolvers) {
        sendMessage(player, MessageConfig.getMessage(path), resolvers);
    }

    public static void sendActionBar(CommandSender player, Component component) {
        HorseOverhaul.getInstance().adventure().sender(player).sendActionBar(component);
    }

    public static void sendActionBar(CommandSender player, String message, TagResolver... resolvers) {
        sendActionBar(player, MiniMessage.miniMessage().deserialize(message, resolvers));
    }

    public static void sendConfigActionBar(CommandSender player, String path, TagResolver... resolvers) {
        sendActionBar(player, MessageConfig.getMessage(path), resolvers);
    }

    public static Component parse(String message, TagResolver... resolvers) {
        return MiniMessage.miniMessage().deserialize(message, resolvers);
    }

    public static Component parseConfig(String path, TagResolver... resolvers) {
        return parse(MessageConfig.getMessage(path), resolvers);
    }

    public static String componentFormattedConfigString(String path, TagResolver... resolvers) {
        return ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(parseConfig(path, resolvers)));
    }

    public static String componentFormattedString(String message, TagResolver... resolvers) {
        return ChatColor.translateAlternateColorCodes('&', LegacyComponentSerializer.legacyAmpersand().serialize(parse(message, resolvers)));
    }

}
