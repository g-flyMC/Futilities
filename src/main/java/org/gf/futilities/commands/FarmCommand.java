package org.gf.futilities.commands;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gf.futilities.FUtilities;
import org.gf.futilities.managers.FarmProgressManager;
import org.gf.futilities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class FarmCommand {

    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c/fu farm <level>");
            return true;
        }

        try {
            int level = Integer.parseInt(args[1]);
            FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();

            if (!config.isSet("farmhoe.levels." + level)) {
                player.sendMessage("§cNiveau " + level + " non trouvé dans la configuration!");
                return true;
            }

            ItemStack hoe = createFarmHoe(level);
            if (hoe != null) {
                player.getInventory().addItem(hoe);
                player.sendMessage("§aVous avez reçu une houe de farm niveau " + level + "!");
            } else {
                player.sendMessage("§cErreur lors de la création de la houe!");
            }
        } catch (NumberFormatException e) {
            player.sendMessage("§cVeuillez entrer un niveau valide!");
        }

        return true;
    }

    public static ItemStack createFarmHoe(int level) {
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();

        ItemStack hoe = new ItemStack(Material.WOOD_HOE, 1);
        ItemMeta meta = hoe.getItemMeta();

        // Nom
        meta.setDisplayName("§6Houe de Farm §e(Niveau " + level + ")");

        // Lore personnalisé par niveau avec placeholders
        List<String> lore = config.getStringList("farmhoe.levels." + level + ".lore");
        if (!lore.isEmpty()) {
            List<String> coloredLore = new ArrayList<>();

            for (String line : lore) {
                coloredLore.add(line.replace("&", "§"));
            }

            // Ajouter les capacités
            List<String> abilities = config.getStringList("farmhoe.levels." + level + ".abilities");
            if (!abilities.isEmpty()) {
                coloredLore.add("§7Capacités:");
                for (String ability : abilities) {
                    String abilityName = getAbilityDisplayName(ability);
                    coloredLore.add("§8- " + abilityName);
                }
            }

            meta.setLore(coloredLore);
        }

        // Incassable
        meta.spigot().setUnbreakable(true);
        hoe.setItemMeta(meta);

        // Tags NBT
        ItemUtils.setCustomTag(hoe, "futilities_farmhoe", "true");
        ItemUtils.setCustomTag(hoe, "farm_level", String.valueOf(level));

        // Initialiser la progression des cultures à zéro
        FarmProgressManager.CropProgress progress = new FarmProgressManager.CropProgress();
        FarmProgressManager.saveProgress(hoe, progress);

        // Mettre à jour le lore avec les placeholders
        FarmProgressManager.updateHoeLore(hoe, level);

        return hoe;
    }

    private static String getAbilityDisplayName(String ability) {
        switch (ability) {
            case "TILL_3x3":
                return "§aTill 3x3";
            case "PLANT_3x3":
                return "§bPlant 3x3";
            case "HARVEST_3x3":
                return "§eHarvest 3x3";
            case "MULTIPLIER_1_5":
                return "§6Multiplicateur x1.5";
            case "MULTIPLIER_2":
                return "§cMultiplicateur x2";
            default:
                return ability;
        }
    }
}