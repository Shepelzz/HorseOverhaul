package club.tesseract.horseoverhaul.config.impl;


import club.tesseract.horseoverhaul.config.AbstractConfig;
import club.tesseract.horseoverhaul.config.ConfigManager;

public class MessageConfig extends AbstractConfig {

    public MessageConfig() {
        super();
    }



    public static String getMessage(String location){
        return ConfigManager.get().messageConfig.customFile.getString(location, "Message not found");
    }

    @Override
    public String configName() {
        return "messages.yml";
    }

    @Override
    protected void addDefaults() {

        customFile.addDefault("deed.original-required", "<red>You need the original copy!");
        customFile.addDefault("horse.interaction.not-owner",  "<red><owner> owns this horse!");
        customFile.addDefault("horse.interaction.already-neutered",  "<red>You've already neutered <horse>!");
        customFile.addDefault("horse.interaction.neutered",  "<green>You have successfully neutered <horse>. He/She will never bread.");
        customFile.addDefault("horse.rename.not-holding-deed", "<red>You must be holding this horse's deed in your off hand in order to rename it!");
        customFile.addDefault("horse.rename.not-owner", "<red>you can only rename a horse that you own!");
        customFile.addDefault("horse.name.too-long", "<red>Name too long! Must be at most 16 characters!");
        customFile.addDefault("horse.name.question", "<blue>What would you like to name your new steed?<newline><gold><click:open_url:'https://www.spigotmc.org/attachments/example2-png.188806'><hover:show_text:'Click to open picture of all colour codes'>Click here to see a list of available colour codes</hover></click></gold>");
        customFile.addDefault("horse.name.question-no-colour", "<blue>What would you like to name your new steed? (No colour codes)");
        customFile.addDefault("horse.name.prompt", "<green>Enter a new name for your horse: (Type your name in chat)");
        customFile.addDefault("horse.claim.already-owned", "<red>You don't have permission to claim other people's horses");
        customFile.addDefault("horse.claim.is-conversing", "<red>You must finish naming the current horse before claiming another!");
        customFile.addDefault("horse.claim.wild-horse", "<red>You do not have permission to claim wild horses");
        customFile.addDefault("horse.claim.not-tamed", "<red>You must tame this horse before claiming it!");
        customFile.addDefault("horse.claim.success", "<green>You are now the proud owner of <horse>!");
        customFile.addDefault("horse.status.private", "<red>This horse is private!");
        customFile.addDefault("horse.status.public", "<green>This horse is public!");
        customFile.addDefault("whistle.no-name-item", "<yellow><horse_colour> Horses' Whistle");
        customFile.addDefault("whistle.name-item", "<yellow><horse>'s Whistle");
        customFile.addDefault("whistle.linked", "<yellow>Whistle Carved!");
        customFile.addDefault("whistle.not-horse", "<yellow>Only horses can hear the sound of your whistle.");
        customFile.addDefault("whistle.locate-successful", "<green>Horse Located!");
        customFile.addDefault("whistle.locate-unsuccessful", "<red>No response...");
        customFile.addDefault("whistle.on-cooldown", "<yellow>You must wait <time> more seconds");
        customFile.addDefault("command.no-permission", "<red>You do not have permission to use this command!");
        customFile.addDefault("command.help.breed", "<light_purple>HORSE OVERHAUL HELP: Breeding</light_purple><newline><newline><green>The Breeding algorithm has been reworked in a way such that there's more of a focus on generational improvements rather than random luck.<newline><yellow>You can get better results from your breeding by using better foods: golden apples will prevent the foal from having stats less than the lesser of the two parents; when using enchanted golden apples they will give you a foal with max stats, however, it will be sterile and unable to breed in the future.");
        customFile.addDefault("command.help.whistle","<light_purple>HORSE OVERHAUL HELP: Whistle</light_purple><newline><newline><green>You can craft a blank whistle by combining a golden carrot and iron ingot<newline><yellow>Once you've obtained a blank whistle, all you need to do is right-click the horse you want to link it to, and you'll have yourself a functioning whistle!<newline><red><obf>ImTheKazooKidYouWillObayMyCommand</obf><newline>Admins will need to remove one of these from message.yml command.help.whistle <newline><obf>ImTheKazooKidYouWillObayMyCommand</obf><newline><green>Right click while holding a whistle to search in a 100x30x100 radius for your horse. If found, it will be teleported to your location!<newline><newline>Right click while holding a whistle to search in a 100x30x100 radius for your horse. If found, you'll be able to see a highlight of the it (even through walls for 10 seconds)<newline><yellow>Note:</yellow><gray> You can only link your whistle to a horse that is not owned by another player. It's recommended that if you intend on claiming a horse, you should do so before carving the whistle.</gray>");
        customFile.addDefault("command.help.stats", "<light_purple>HORSE OVERHAUL HELP: Checking Stats<newline><green>Right click a horse while holding a carrot on a stick to see its stats!<newline><yellow>After viewing a horses' stats, you can display them on a sign by right clicking the sign with your carrot on a stick.<newline>You can only check the stats of tamed horses that are not owned by the other players");
        customFile.addDefault("command.help.ownership", "<light_purple>HORSE OVERHAUL HELP: Ownership<newline><green>Craft a blank deed by combining a golden carrot with a book and quill.<newline><yellow>Claim a horse as yours by right-clicking it with a blank deed, or if it's already claimed, the original copy of its deed!<newline><green>Other players cannot interact with horses that you own, and equipping your owned horses with armor prevents you from accidentally damaging them :)<newline><yellow>If you're in the business of selling horses, you can right-click a baby foal that you own with shears to neuter it and prevent it from ever breeding.<newline><green>If trying to use a nametag on one of your horses, you must be holding its deed in your off-hand.<newline>If colour codes are enabled, you can use them to name your horse! See <click:open_url:'https://www.spigotmc.org/attachments/example2-png.188806/'><hover:show_text:'Click to open image'><gold>[link]</gold></hover></click> for the format.");


        customFile.options().copyDefaults(true);
        customFile.options().header("HorseOverhaul Message Configuration\n\nMessages are formatted using MiniMessage more information can be found here: https://docs.adventure.kyori.net/minimessage/format.html\nA handy website for MiniMessage formatting: https://webui.adventure.kyori.net/\nIf you need any more help with the config, Contact me on discord: RealName_123#2570");

        save();
    }

}
