package org.gf.futilities.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * Utilitaire pour gérer les tags NBT personnalisés sur les items
 * Compatible avec Spigot 1.12
 */
public class ItemUtils {

    private static final String NBT_PREFIX = "FU_";

    public static void setCustomTag(ItemStack item, String key, String value) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Supprimer l'ancien tag s'il existe
        lore.removeIf(line -> line.startsWith("§0§k" + NBT_PREFIX + key + ":"));

        // Ajouter le nouveau tag (invisible)
        lore.add("§0§k" + NBT_PREFIX + key + ":" + value);

        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static String getCustomTag(ItemStack item, String key) {
        if (item == null || !item.hasItemMeta()) {
            return null;
        }

        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();
        if (lore == null) {
            return null;
        }

        String tagPrefix = "§0§k" + NBT_PREFIX + key + ":";
        for (String line : lore) {
            if (line.startsWith(tagPrefix)) {
                return line.substring(tagPrefix.length());
            }
        }

        return null;
    }

    public static boolean hasCustomTag(ItemStack item, String key) {
        return getCustomTag(item, key) != null;
    }

    public static boolean isFUtilitiesItem(ItemStack item, String type) {
        return hasCustomTag(item, type);
    }

    public static int getIntTag(ItemStack item, String key, int defaultValue) {
        String value = getCustomTag(item, key);
        if (value == null) {
            return defaultValue;
        }

        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static void setIntTag(ItemStack item, String key, int value) {
        setCustomTag(item, key, String.valueOf(value));
    }

    public static void updateLorePlaceholder(ItemStack item, String placeholder, String value) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                if (!line.startsWith("§0§k" + NBT_PREFIX)) { // Ne pas modifier les tags NBT
                    newLore.add(line.replace(placeholder, value));
                } else {
                    newLore.add(line);
                }
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
    }
}