package org.gf.futilities.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.*;

public class WeaponListener implements Listener {

    private final FUtilities plugin;
    private final Random random = new Random();
    private final Map<UUID, Map<String, Integer>> playerRespawnCounts = new HashMap<>();

    public WeaponListener(FUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player)) {
            return;
        }

        Player attacker = (Player) event.getDamager();
        ItemStack weapon = attacker.getItemInHand();

        if (weapon == null || !ItemUtils.hasCustomTag(weapon, "futilities_item")) {
            return;
        }

        String itemKey = ItemUtils.getCustomTag(weapon, "futilities_item");
        FileConfiguration config = plugin.getConfigManager().getConfig();

        if (!config.isSet("items." + itemKey + ".potion-effect")) {
            return;
        }

        // Vérifier la chance d'activation
        int chance = config.getInt("items." + itemKey + ".potion-effect.chance", 100);
        if (random.nextInt(100) >= chance) {
            return;
        }

        // Récupérer les paramètres de l'effet
        String effectType = config.getString("items." + itemKey + ".potion-effect.type");
        String target = config.getString("items." + itemKey + ".potion-effect.target", "ENEMY");
        int duration = config.getInt("items." + itemKey + ".potion-effect.duration", 100);
        int amplifier = config.getInt("items." + itemKey + ".potion-effect.amplifier", 1);

        PotionEffectType potionType = PotionEffectType.getByName(effectType);
        if (potionType == null) {
            plugin.getLogger().warning("Type d'effet inconnu: " + effectType);
            return;
        }

        // Mode debug depuis la config
        boolean debugMode = config.getBoolean("settings.debug", false);

        // Appliquer l'effet de manière synchrone avec un délai d'1 tick
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PotionEffect effect = new PotionEffect(potionType, duration, amplifier - 1);

            // Appliquer l'effet selon la cible
            if (target.equalsIgnoreCase("SELF")) {
                attacker.addPotionEffect(effect);
                if (debugMode) {
                    attacker.sendMessage("§7[Debug] Effet activé sur vous-même: §e" + effectType);
                }
            } else if (target.equalsIgnoreCase("ENEMY") && event.getEntity() instanceof LivingEntity) {
                LivingEntity enemy = (LivingEntity) event.getEntity();
                enemy.addPotionEffect(effect);
                if (debugMode) {
                    attacker.sendMessage("§7[Debug] Effet appliqué à l'ennemi: §e" + effectType);
                }
            }
        }, 1L);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        List<ItemStack> toRemove = new ArrayList<>();

        // Chercher les items FUtilities dans les drops
        for (ItemStack item : event.getDrops()) {
            if (item != null && ItemUtils.hasCustomTag(item, "futilities_item")) {
                String itemKey = ItemUtils.getCustomTag(item, "futilities_item");
                int currentRespawns = ItemUtils.getIntTag(item, "respawn_count", 0);

                // Empêcher le drop des épées FUtilities
                toRemove.add(item);

                if (currentRespawns != 0) { // -1 = infini, >0 = limité
                    // Sauvegarder pour le respawn
                    Map<String, Integer> playerItems = playerRespawnCounts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
                    int newCount = currentRespawns == -1 ? -1 : currentRespawns - 1;
                    playerItems.put(itemKey, newCount);

                    player.sendMessage("§6Votre " + itemKey + " sera restituée au respawn (" +
                            (newCount == -1 ? "∞" : newCount) + " respawns restants)");
                }
            }
        }

        // Retirer les items FUtilities des drops
        event.getDrops().removeAll(toRemove);

        // Aussi vérifier l'inventaire pour éviter la duplication
        for (ItemStack item : player.getInventory().getContents()) {
            if (item != null && ItemUtils.hasCustomTag(item, "futilities_item")) {
                String itemKey = ItemUtils.getCustomTag(item, "futilities_item");
                int currentRespawns = ItemUtils.getIntTag(item, "respawn_count", 0);

                if (currentRespawns != 0) {
                    Map<String, Integer> playerItems = playerRespawnCounts.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>());
                    int newCount = currentRespawns == -1 ? -1 : currentRespawns - 1;
                    playerItems.put(itemKey, newCount);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();

        if (!playerRespawnCounts.containsKey(playerId)) {
            return;
        }

        // Programmer la restitution des items après le respawn
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            Map<String, Integer> items = playerRespawnCounts.get(playerId);
            if (items != null) {
                FileConfiguration config = plugin.getConfigManager().getConfig();

                for (Map.Entry<String, Integer> entry : items.entrySet()) {
                    String itemKey = entry.getKey();
                    int remainingRespawns = entry.getValue();

                    if (remainingRespawns != 0) { // -1 = infini, >0 = limité
                        ItemStack restoredItem = createCustomItemWithRespawns(itemKey, remainingRespawns, config);
                        if (restoredItem != null) {
                            player.getInventory().addItem(restoredItem);
                            if (remainingRespawns == 0) {
                                player.sendMessage("§6Attention: Votre " + config.getString("items." + itemKey + ".name", itemKey).replace("&", "§") + " §6n'a plus de respawns!");
                            } else if (remainingRespawns > 0) {
                                player.sendMessage("§aVotre " + config.getString("items." + itemKey + ".name", itemKey).replace("&", "§") + " §aa été restituée! (" + remainingRespawns + " respawns restants)");
                            } else {
                                player.sendMessage("§aVotre " + config.getString("items." + itemKey + ".name", itemKey).replace("&", "§") + " §aa été restituée! (respawns infinis)");
                            }
                        }
                    }
                }

                playerRespawnCounts.remove(playerId);
            }
        }, 5L);
    }

    private ItemStack createCustomItemWithRespawns(String itemKey, int respawns, FileConfiguration config) {
        try {
            String materialName = config.getString("items." + itemKey + ".material");
            org.bukkit.Material material = org.bukkit.Material.getMaterial(materialName);

            if (material == null) {
                return null;
            }

            ItemStack item = new ItemStack(material, 1);
            org.bukkit.inventory.meta.ItemMeta meta = item.getItemMeta();

            // Nom
            String name = config.getString("items." + itemKey + ".name");
            if (name != null) {
                meta.setDisplayName(name.replace("&", "§"));
            }

            // Lore avec respawns mis à jour
            java.util.List<String> lore = config.getStringList("items." + itemKey + ".lore");
            if (!lore.isEmpty()) {
                java.util.List<String> coloredLore = new java.util.ArrayList<>();
                for (String line : lore) {
                    String respawnText = respawns == -1 ? "∞" : String.valueOf(respawns);
                    coloredLore.add(line.replace("&", "§").replace("%respawn%", respawnText));
                }
                meta.setLore(coloredLore);
            }

            // Incassable
            boolean unbreakable = config.getBoolean("items." + itemKey + ".unbreakable", false);
            if (unbreakable) {
                meta.spigot().setUnbreakable(true);
            }

            item.setItemMeta(meta);

            // Enchantements
            java.util.List<String> enchantments = config.getStringList("items." + itemKey + ".enchantments");
            for (String enchant : enchantments) {
                String[] parts = enchant.split(":");
                if (parts.length == 2) {
                    try {
                        org.bukkit.enchantments.Enchantment enchantment = org.bukkit.enchantments.Enchantment.getByName(parts[0]);
                        int level = Integer.parseInt(parts[1]);
                        if (enchantment != null) {
                            item.addUnsafeEnchantment(enchantment, level);
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }

            // Tags NBT
            ItemUtils.setCustomTag(item, "futilities_item", itemKey);
            ItemUtils.setCustomTag(item, "respawn_count", String.valueOf(respawns));

            return item;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}