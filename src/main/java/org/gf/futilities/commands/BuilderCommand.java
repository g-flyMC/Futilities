package org.gf.futilities.commands;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class BuilderCommand {

    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c/fu bat <id>");
            return true;
        }

        String builderId = args[1];
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();

        if (!config.isSet("builders." + builderId)) {
            player.sendMessage("§cBatisseuse '" + builderId + "' non trouvée dans la configuration!");
            return true;
        }

        ItemStack builder = createBuilder(builderId, config);
        if (builder != null) {
            player.getInventory().addItem(builder);
            String name = config.getString("builders." + builderId + ".name", "Batisseuse");
            player.sendMessage("§aVous avez reçu: " + name.replace("&", "§"));
        } else {
            player.sendMessage("§cErreur lors de la création de la batisseuse!");
        }

        return true;
    }

    private ItemStack createBuilder(String builderId, FileConfiguration config) {
        try {
            String materialName = config.getString("builders." + builderId + ".material");
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                return null;
            }

            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();

            // Nom
            String name = config.getString("builders." + builderId + ".name");
            if (name != null) {
                meta.setDisplayName(name.replace("&", "§"));
            }

            // Lore avec placeholder uses
            List<String> lore = config.getStringList("builders." + builderId + ".lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                int uses = config.getInt("builders." + builderId + ".uses", 1);
                for (String line : lore) {
                    coloredLore.add(line.replace("&", "§").replace("%uses%", String.valueOf(uses)));
                }
                meta.setLore(coloredLore);
            }

            item.setItemMeta(meta);

            // Tags NBT
            ItemUtils.setCustomTag(item, "futilities_builder", builderId);
            ItemUtils.setCustomTag(item, "builder_uses", String.valueOf(config.getInt("builders." + builderId + ".uses", 1)));
            ItemUtils.setCustomTag(item, "builder_block", config.getString("builders." + builderId + ".block", "STONE"));

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateBuilderLore(ItemStack item, int remainingUses) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                // Ne pas modifier les tags NBT cachés
                if (!line.startsWith("§0§k")) {
                    newLore.add(line.replaceAll("Utilisations restantes: §e\\d+", "Utilisations restantes: §e" + remainingUses)
                            .replace("%uses%", String.valueOf(remainingUses)));
                } else {
                    newLore.add(line);
                }
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }

        ItemUtils.setCustomTag(item, "builder_uses", String.valueOf(remainingUses));
    }
}