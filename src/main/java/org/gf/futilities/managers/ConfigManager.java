package org.gf.futilities.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

public class ConfigManager {

    private final JavaPlugin plugin;

    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        // Créer un backup avant de charger la config
        createBackup();

        plugin.saveDefaultConfig();
        FileConfiguration config = plugin.getConfig();

        // Configuration par défaut si elle n'existe pas
        if (!config.isSet("items")) {
            createDefaultConfig();
            plugin.saveConfig();
        }
    }

    private void createBackup() {
        File configFile = new File(plugin.getDataFolder(), "config.yml");

        // Si le fichier de config n'existe pas encore, pas besoin de backup
        if (!configFile.exists()) {
            return;
        }

        try {
            // Créer le dossier backups s'il n'existe pas
            File backupFolder = new File(plugin.getDataFolder(), "backups");
            if (!backupFolder.exists()) {
                backupFolder.mkdirs();
            }

            // Générer un nom de backup avec timestamp
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
            String timestamp = dateFormat.format(new Date());
            String backupName = "backup_" + timestamp + ".yml";

            File backupFile = new File(backupFolder, backupName);

            // Copier le fichier
            Files.copy(configFile.toPath(), backupFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            plugin.getLogger().info("Backup de configuration cree: " + backupName);

            // Nettoyer les vieux backups (garder seulement les 10 derniers)
            cleanOldBackups(backupFolder);

        } catch (IOException e) {
            plugin.getLogger().warning("Impossible de creer un backup de la config: " + e.getMessage());
        }
    }

    private void cleanOldBackups(File backupFolder) {
        File[] backups = backupFolder.listFiles((dir, name) -> name.startsWith("backup_") && name.endsWith(".yml"));

        if (backups != null && backups.length > 10) {
            // Trier par date de modification (plus ancien en premier)
            Arrays.sort(backups, (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));

            // Supprimer les backups en trop (garder les 10 plus récents)
            int toDelete = backups.length - 10;
            for (int i = 0; i < toDelete; i++) {
                if (backups[i].delete()) {
                    plugin.getLogger().info("Ancien backup supprime: " + backups[i].getName());
                }
            }
        }
    }

    private void createDefaultConfig() {
        FileConfiguration config = plugin.getConfig();

        // Items par défaut avec custom durability pour textures custom
        config.set("items.fire_sword.name", "&cÉpée de Feu");
        config.set("items.fire_sword.material", "DIAMOND_SWORD");
        config.set("items.fire_sword.custom-durability", 1); // Pour texture custom
        config.set("items.fire_sword.lore", Arrays.asList("&7Une épée enflammée qui brûle vos ennemis", "&7Respawns restants: &e%respawn%"));
        config.set("items.fire_sword.enchantments", Arrays.asList("DAMAGE_ALL:10", "FIRE_ASPECT:2"));
        config.set("items.fire_sword.unbreakable", true);
        config.set("items.fire_sword.potion-effect.type", "FIRE_RESISTANCE");
        config.set("items.fire_sword.potion-effect.target", "SELF");
        config.set("items.fire_sword.potion-effect.duration", 100);
        config.set("items.fire_sword.potion-effect.amplifier", 1);
        config.set("items.fire_sword.potion-effect.chance", 50);
        config.set("items.fire_sword.respawn", 3);

        // Épée custom avec durabilité custom (non-unbreakable)
        config.set("items.custom_blade.name", "&dLame Mortelle");
        config.set("items.custom_blade.material", "IRON_SWORD");
        config.set("items.custom_blade.custom-durability", 5); // Pour texture custom
        config.set("items.custom_blade.lore", Arrays.asList("&7Une lame puissante mais fragile", "&7Respawns restants: &e%respawn%"));
        config.set("items.custom_blade.enchantments", Arrays.asList("DAMAGE_ALL:8", "DURABILITY:3"));
        config.set("items.custom_blade.unbreakable", false); // Durabilité custom active
        config.set("items.custom_blade.max-durability", 150); // Durabilité personnalisée
        config.set("items.custom_blade.potion-effect.type", "SPEED");
        config.set("items.custom_blade.potion-effect.target", "SELF");
        config.set("items.custom_blade.potion-effect.duration", 40);
        config.set("items.custom_blade.potion-effect.amplifier", 1);
        config.set("items.custom_blade.potion-effect.chance", 30);
        config.set("items.custom_blade.respawn", 2);

        config.set("items.ice_sword.name", "&bÉpée de Glace");
        config.set("items.ice_sword.material", "IRON_SWORD");
        config.set("items.ice_sword.custom-durability", 2); // Pour texture custom
        config.set("items.ice_sword.lore", Arrays.asList("&7Une épée glaciale qui ralentit vos adversaires", "&7Respawns restants: &e%respawn%"));
        config.set("items.ice_sword.enchantments", Arrays.asList("DAMAGE_ALL:5", "KNOCKBACK:3"));
        config.set("items.ice_sword.unbreakable", true);
        config.set("items.ice_sword.potion-effect.type", "SLOW");
        config.set("items.ice_sword.potion-effect.target", "ENEMY");
        config.set("items.ice_sword.potion-effect.duration", 60);
        config.set("items.ice_sword.potion-effect.amplifier", 2);
        config.set("items.ice_sword.potion-effect.chance", 75);
        config.set("items.ice_sword.respawn", 5);

        config.set("items.thunder_sword.name", "&eÉpée de la Foudre");
        config.set("items.thunder_sword.material", "GOLD_SWORD");
        config.set("items.thunder_sword.custom-durability", 3); // Pour texture custom
        config.set("items.thunder_sword.lore", Arrays.asList("&7L'épée des dieux du tonnerre", "&7Respawns restants: &e%respawn%"));
        config.set("items.thunder_sword.enchantments", Arrays.asList("DAMAGE_ALL:15", "LOOTING:3"));
        config.set("items.thunder_sword.unbreakable", true);
        config.set("items.thunder_sword.potion-effect.type", "SPEED");
        config.set("items.thunder_sword.potion-effect.target", "SELF");
        config.set("items.thunder_sword.potion-effect.duration", 200);
        config.set("items.thunder_sword.potion-effect.amplifier", 2);
        config.set("items.thunder_sword.potion-effect.chance", 100);
        config.set("items.thunder_sword.respawn", -1);

        // Arcs custom avec effets et textures custom
        config.set("items.poison_bow.name", "&2Arc Empoisonne");
        config.set("items.poison_bow.material", "BOW");
        config.set("items.poison_bow.custom-durability", 1); // Pour texture custom
        config.set("items.poison_bow.lore", Arrays.asList("&7Un arc qui empoisonne vos ennemis", "&7Respawns restants: &e%respawn%"));
        config.set("items.poison_bow.enchantments", Arrays.asList("ARROW_DAMAGE:5", "ARROW_INFINITE:1", "DURABILITY:10"));
        config.set("items.poison_bow.unbreakable", true);
        config.set("items.poison_bow.potion-effect.type", "POISON");
        config.set("items.poison_bow.potion-effect.target", "ENEMY");
        config.set("items.poison_bow.potion-effect.duration", 100);
        config.set("items.poison_bow.potion-effect.amplifier", 2);
        config.set("items.poison_bow.potion-effect.chance", 100);
        config.set("items.poison_bow.respawn", 3);

        config.set("items.speed_bow.name", "&bArc de Vitesse");
        config.set("items.speed_bow.material", "BOW");
        config.set("items.speed_bow.custom-durability", 2); // Pour texture custom
        config.set("items.speed_bow.lore", Arrays.asList("&7Chaque fleche vous rend plus rapide", "&7Respawns restants: &e%respawn%"));
        config.set("items.speed_bow.enchantments", Arrays.asList("ARROW_DAMAGE:8", "ARROW_KNOCKBACK:2", "DURABILITY:10"));
        config.set("items.speed_bow.unbreakable", true);
        config.set("items.speed_bow.potion-effect.type", "SPEED");
        config.set("items.speed_bow.potion-effect.target", "SELF");
        config.set("items.speed_bow.potion-effect.duration", 60);
        config.set("items.speed_bow.potion-effect.amplifier", 2);
        config.set("items.speed_bow.potion-effect.chance", 100);
        config.set("items.speed_bow.respawn", 5);

        config.set("items.wither_bow.name", "&8Arc du Wither");
        config.set("items.wither_bow.material", "BOW");
        config.set("items.wither_bow.custom-durability", 3); // Pour texture custom
        config.set("items.wither_bow.lore", Arrays.asList("&7Arc maudit qui inflige le Wither", "&7Respawns restants: &e%respawn%"));
        config.set("items.wither_bow.enchantments", Arrays.asList("ARROW_DAMAGE:10", "ARROW_FIRE:1", "DURABILITY:10"));
        config.set("items.wither_bow.unbreakable", true);
        config.set("items.wither_bow.potion-effect.type", "WITHER");
        config.set("items.wither_bow.potion-effect.target", "ENEMY");
        config.set("items.wither_bow.potion-effect.duration", 200);
        config.set("items.wither_bow.potion-effect.amplifier", 1);
        config.set("items.wither_bow.potion-effect.chance", 75);
        config.set("items.wither_bow.respawn", -1);

        // Hammer (Marteau 3x3)
        config.set("hammer.name", "&6&lMarteau de Thor");
        config.set("hammer.material", "DIAMOND_PICKAXE");
        config.set("hammer.custom-durability", 4); // Pour texture custom
        config.set("hammer.lore", Arrays.asList(
                "&7Mine en 3x3 selon la direction",
                "&7Regarde en haut/bas: 3x3 horizontal",
                "&7Regarde devant: 3x3 vertical",
                "&7Pioche ultime des dieux"
        ));
        config.set("hammer.enchantments", Arrays.asList("DIG_SPEED:10", "DURABILITY:10", "LOOT_BONUS_BLOCKS:3"));
        config.set("hammer.unbreakable", true);

        // Farm hoe par défaut avec objectifs par culture (sans emojis)
        config.set("farmhoe.levels.1.lore", Arrays.asList(
                "&7Niveau 1: Simple fermier",
                "&7Progression vers niveau 2:",
                "&7  Ble: &e%wheat%&7/&c%req_wheat%",
                "&7  Carottes: &e%carrot%&7/&c%req_carrot%",
                "&7  Pommes de terre: &e%potato%&7/&c%req_potato%",
                "&7  Nether Wart: &e%nether_wart%&7/&c%req_nether_wart%"
        ));
        config.set("farmhoe.levels.1.required.wheat", 50);
        config.set("farmhoe.levels.1.required.carrot", 20);
        config.set("farmhoe.levels.1.required.potato", 30);
        config.set("farmhoe.levels.1.required.nether_wart", 10);
        config.set("farmhoe.levels.1.abilities", Arrays.asList());

        config.set("farmhoe.levels.2.lore", Arrays.asList(
                "&7Niveau 2: Fermier aguerri",
                "&7Progression vers niveau 3:",
                "&7  Ble: &e%wheat%&7/&c%req_wheat%",
                "&7  Carottes: &e%carrot%&7/&c%req_carrot%",
                "&7  Pommes de terre: &e%potato%&7/&c%req_potato%",
                "&7  Nether Wart: &e%nether_wart%&7/&c%req_nether_wart%",
                "&aNouveaux pouvoirs debloques!"
        ));
        config.set("farmhoe.levels.2.required.wheat", 150);
        config.set("farmhoe.levels.2.required.carrot", 80);
        config.set("farmhoe.levels.2.required.potato", 100);
        config.set("farmhoe.levels.2.required.nether_wart", 50);
        config.set("farmhoe.levels.2.abilities", Arrays.asList("TILL_3x3"));

        config.set("farmhoe.levels.3.lore", Arrays.asList(
                "&7Niveau 3: Maitre cultivateur",
                "&7Progression vers niveau 4:",
                "&7  Ble: &e%wheat%&7/&c%req_wheat%",
                "&7  Carottes: &e%carrot%&7/&c%req_carrot%",
                "&7  Pommes de terre: &e%potato%&7/&c%req_potato%",
                "&7  Nether Wart: &e%nether_wart%&7/&c%req_nether_wart%",
                "&6Multiplicateur de recoltes active!"
        ));
        config.set("farmhoe.levels.3.required.wheat", 300);
        config.set("farmhoe.levels.3.required.carrot", 200);
        config.set("farmhoe.levels.3.required.potato", 250);
        config.set("farmhoe.levels.3.required.nether_wart", 100);
        config.set("farmhoe.levels.3.abilities", Arrays.asList("TILL_3x3", "PLANT_3x3", "MULTIPLIER_1_5"));

        config.set("farmhoe.levels.4.lore", Arrays.asList(
                "&6Niveau 4: Grand maitre fermier",
                "&6&lNiveau Maximum Atteint!",
                "&7Statistiques totales:",
                "&7  Ble recolte: &e%wheat%",
                "&7  Carottes recoltees: &e%carrot%",
                "&7  Pommes de terre recoltees: &e%potato%",
                "&7  Nether Wart recolte: &e%nether_wart%",
                "&7Toutes les capacites debloquees"
        ));
        config.set("farmhoe.levels.4.required.wheat", -1); // -1 = niveau maximum
        config.set("farmhoe.levels.4.required.carrot", -1);
        config.set("farmhoe.levels.4.required.potato", -1);
        config.set("farmhoe.levels.4.required.nether_wart", -1);
        config.set("farmhoe.levels.4.abilities", Arrays.asList("TILL_3x3", "PLANT_3x3", "HARVEST_3x3", "MULTIPLIER_2"));

        // Builders par défaut
        config.set("builders.1.name", "&bBatisseuse de pierre");
        config.set("builders.1.material", "STICK");
        config.set("builders.1.lore", Arrays.asList("&7Construit des colonnes de pierre", "&7de la couche 0 à la couche 256", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.1.block", "STONE");
        config.set("builders.1.uses", 10);

        config.set("builders.2.name", "&aBatisseuse de verre");
        config.set("builders.2.material", "BLAZE_ROD");
        config.set("builders.2.lore", Arrays.asList("&7Place des piliers de verre transparents", "&7Parfait pour les constructions modernes", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.2.block", "GLASS");
        config.set("builders.2.uses", 5);

        config.set("builders.3.name", "&6Batisseuse d'or");
        config.set("builders.3.material", "GOLD_INGOT");
        config.set("builders.3.lore", Arrays.asList("&7Colonnes dorées pour les palais", "&7Matériau de luxe et de prestige", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.3.block", "GOLD_BLOCK");
        config.set("builders.3.uses", 3);

        config.set("builders.4.name", "&4Batisseuse d'obsidienne");
        config.set("builders.4.material", "OBSIDIAN");
        config.set("builders.4.lore", Arrays.asList("&7Piliers indestructibles d'obsidienne", "&7Pour les forteresses les plus solides", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.4.block", "OBSIDIAN");
        config.set("builders.4.uses", 1);

        config.set("builders.5.name", "&dBatisseuse de quartz");
        config.set("builders.5.material", "QUARTZ");
        config.set("builders.5.lore", Arrays.asList("&7Élégantes colonnes de quartz", "&7Style architectural raffiné", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.5.block", "QUARTZ_BLOCK");
        config.set("builders.5.uses", 7);

        config.set("builders.6.name", "&2Batisseuse d'émeraude");
        config.set("builders.6.material", "EMERALD");
        config.set("builders.6.lore", Arrays.asList("&7Colonnes d'émeraude étincelantes", "&7Le summum du luxe architectural", "&7Utilisations restantes: &e%uses%"));
        config.set("builders.6.block", "EMERALD_BLOCK");
        config.set("builders.6.uses", 2);

        // Messages
        config.set("messages.prefix", "&8[&6FUtilities&8]&r ");
        config.set("messages.no_permission", "&cVous n'avez pas la permission!");
        config.set("messages.player_only", "&cSeuls les joueurs peuvent utiliser cette commande!");
        config.set("messages.invalid_args", "&cUtilisation: /fu <item|farm|bat> [args]");

        config.set("messages.item.not_found", "&cItem '%item%' non trouvé dans la configuration!");
        config.set("messages.item.given", "&aVous avez reçu l'item: %name%");
        config.set("messages.item.error", "&cErreur lors de la création de l'item!");

        config.set("messages.farm.invalid_level", "&cNiveau %level% non trouvé dans la configuration!");
        config.set("messages.farm.given", "&aVous avez reçu une houe de farm niveau %level%!");
        config.set("messages.farm.levelup", "&a✦ Votre houe est passée au niveau %level%!");
        config.set("messages.farm.error", "&cErreur lors de la création de la houe!");

        config.set("messages.builder.not_found", "&cBatisseuse '%id%' non trouvée dans la configuration!");
        config.set("messages.builder.given", "&aVous avez reçu: %name%");
        config.set("messages.builder.column_built", "&aColonne construite! Utilisations restantes: &e%uses%");
        config.set("messages.builder.exhausted", "&6Votre batisseuse est épuisée et disparaît!");
        config.set("messages.builder.no_uses", "&cCette batisseuse n'a plus d'utilisations!");
        config.set("messages.builder.no_blocks_placed", "&cAucun bloc n'a pu être placé à cette position!");
        config.set("messages.builder.invalid_block", "&cType de bloc invalide: %block%");
        config.set("messages.builder.error", "&cErreur lors de la création de la batisseuse!");

        // Paramètres avancés
        config.set("settings.respawn_delay", 5);
        config.set("settings.debug", false);
        config.set("settings.farm_exp_per_harvest", 1);
        config.set("settings.max_column_height", 256);
        config.set("settings.min_column_height", 0);
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}