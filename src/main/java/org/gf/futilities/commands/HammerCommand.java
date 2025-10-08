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

public class HammerCommand {

    public boolean execute(Player player, String[] args) {
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();

        ItemStack hammer = createHammer(config);
        if (hammer != null) {
            player.getInventory().addItem(hammer);
            player.sendMessage("§aVous avez reçu le " + config.getString("hammer.name", "Marteau").replace("&", "§"));
        } else {
            player.sendMessage("§cErreur lors de la création du marteau!");
        }

        return true;
    }

    private ItemStack createHammer(FileConfiguration config) {
        try {
            String materialName = config.getString("hammer.material", "DIAMOND_PICKAXE");
            Material material = Material.getMaterial(materialName);

            if (material == null) {
                return null;
            }

            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();

            // Nom
            String name = config.getString("hammer.name");
            if (name != null) {
                meta.setDisplayName(name.replace("&", "§"));
            }

            // Lore
            List<String> lore = config.getStringList("hammer.lore");
            if (!lore.isEmpty()) {
                List<String> coloredLore = new ArrayList<>();
                for (String line : lore) {
                    coloredLore.add(line.replace("&", "§"));
                }
                meta.setLore(coloredLore);
            }

            // Incassable
            meta.spigot().setUnbreakable(true);

            // Appliquer le meta
            item.setItemMeta(meta);

            // Custom durability pour texture
            int customDurability = config.getInt("hammer.custom-durability", 0);
            if (customDurability > 0) {
                item.setDurability((short) customDurability);
            }

            // Enchantements
            List<String> enchantments = config.getStringList("hammer.enchantments");
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

            // Tag NBT
            ItemUtils.setCustomTag(item, "futilities_hammer", "true");

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
