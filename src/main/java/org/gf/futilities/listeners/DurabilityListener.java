package org.gf.futilities.listeners;

import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DurabilityListener implements Listener {

    private final FUtilities plugin;
    private final Random random = new Random();

    public DurabilityListener(FUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemDamage(PlayerItemDamageEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !ItemUtils.hasCustomTag(item, "futilities_item")) {
            return;
        }

        String itemKey = ItemUtils.getCustomTag(item, "futilities_item");
        FileConfiguration config = plugin.getConfigManager().getConfig();

        boolean isUnbreakable = config.getBoolean("items." + itemKey + ".unbreakable", false);

        if (isUnbreakable) {
            // Item unbreakable → annuler complètement les dégâts
            event.setCancelled(true);
        } else {
            // Item avec durabilité custom
            event.setCancelled(true); // On gère nous-même la durabilité

            // Récupérer la durabilité custom actuelle
            int currentDurability = ItemUtils.getIntTag(item, "custom_current_durability", -1);
            int maxDurability = config.getInt("items." + itemKey + ".max-durability", 100);

            // Si c'est la première utilisation, initialiser
            if (currentDurability == -1) {
                currentDurability = maxDurability;
            }

            // Vérifier Unbreaking
            int unbreakingLevel = item.getEnchantmentLevel(org.bukkit.enchantments.Enchantment.DURABILITY);
            boolean shouldDamage = true;

            if (unbreakingLevel > 0) {
                // Formule Unbreaking : 100 / (level + 1) % de chance de perdre de la durabilité
                int chance = 100 / (unbreakingLevel + 1);
                if (random.nextInt(100) >= chance) {
                    shouldDamage = false; // Unbreaking a protégé l'item
                }
            }

            if (shouldDamage) {
                currentDurability--;

                if (currentDurability <= 0) {
                    // Item cassé
                    player.getInventory().remove(item);
                    player.sendMessage("§cVotre " + config.getString("items." + itemKey + ".name", itemKey).replace("&", "§") + " §cs'est brisé!");
                    // Son de cassure
                    player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
                } else {
                    // Mettre à jour la durabilité
                    ItemUtils.setIntTag(item, "custom_current_durability", currentDurability);
                    updateDurabilityLore(item, currentDurability, maxDurability);
                }
            }
        }
    }

    private void updateDurabilityLore(ItemStack item, int current, int max) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore == null) {
            lore = new ArrayList<>();
        }

        // Retirer l'ancienne ligne de durabilité si elle existe
        lore.removeIf(line -> line.contains("Durabilite:") || line.contains("§8"));

        // Calculer la couleur selon le pourcentage
        double percentage = (double) current / max * 100;
        String color;
        if (percentage > 66) {
            color = "§a"; // Vert
        } else if (percentage > 33) {
            color = "§e"; // Jaune
        } else {
            color = "§c"; // Rouge
        }

        // Ajouter la nouvelle ligne de durabilité en bas
        // Retirer les tags NBT cachés temporairement
        List<String> visibleLore = new ArrayList<>();
        List<String> hiddenTags = new ArrayList<>();

        for (String line : lore) {
            if (line.startsWith("§0§k")) {
                hiddenTags.add(line);
            } else {
                visibleLore.add(line);
            }
        }

        // Ajouter la durabilité
        visibleLore.add("§8▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        visibleLore.add("§7Durabilite: " + color + current + "§7/" + max);

        // Remettre les tags cachés à la fin
        visibleLore.addAll(hiddenTags);

        meta.setLore(visibleLore);
        item.setItemMeta(meta);
    }
}