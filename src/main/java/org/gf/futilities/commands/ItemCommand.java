package org.gf.futilities.commands;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class ItemCommand {

    public boolean execute(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage("§c/fu item <nom_item>");
            return true;
        }

        String itemName = args[1];
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();

        if (!config.isSet("items." + itemName)) {
            player.sendMessage("§cItem '" + itemName + "' non trouvé dans la configuration!");
            return true;
        }

        ItemStack item = createCustomItem(itemName, config);
        if (item != null) {
            player.getInventory().addItem(item);
            player.sendMessage("§aVous avez reçu l'item: " + config.getString("items." + itemName + ".name", itemName));
        } else {
            player.sendMessage("§cErreur lors de la création de l'item!");
        }

        return true;
    }

    private ItemStack createCustomItem(String itemKey, FileConfiguration config) {
        try {
            String materialName = config.getString("items." + itemKey + ".material");
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                return null;
            }

            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();

            // Nom
            String name = config.getString("items." + itemKey + ".name");
            if (name != null) {
                meta.setDisplayName(name.replace("&", "§"));
            }

            // Lore avec placeholder respawn
            List<String> lore = config.getStringList("items." + itemKey + ".lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                int respawn = config.getInt("items." + itemKey + ".respawn", 0);
                String respawnText = respawn == -1 ? "∞" : String.valueOf(respawn);
                for (String line : lore) {
                    coloredLore.add(line.replace("&", "§").replace("%respawn%", respawnText));
                }
                meta.setLore(coloredLore);
            }

            // Incassable (TOUJOURS activé pour les items custom)
            meta.spigot().setUnbreakable(true);

            // Appliquer la durability AVANT de set le meta
            // C'est crucial pour que la texture custom fonctionne
            int customDurability = config.getInt("items." + itemKey + ".custom-durability", 0);
            if (customDurability > 0) {
                item.setDurability((short) customDurability);
            }

            item.setItemMeta(meta);

            // Enchantements
            List<String> enchantments = config.getStringList("items." + itemKey + ".enchantments");
            for (String enchant : enchantments) {
                String[] parts = enchant.split(":");
                if (parts.length == 2) {
                    try {
                        Enchantment enchantment = Enchantment.getByName(parts[0]);
                        int level = Integer.parseInt(parts[1]);
                        if (enchantment != null) {
                            item.addUnsafeEnchantment(enchantment, level);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Ajouter les données NBT pour identifier l'item
            ItemUtils.setCustomTag(item, "futilities_item", itemKey);
            ItemUtils.setCustomTag(item, "respawn_count", String.valueOf(config.getInt("items." + itemKey + ".respawn", 0)));
            ItemUtils.setCustomTag(item, "custom_durability", String.valueOf(customDurability));

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}