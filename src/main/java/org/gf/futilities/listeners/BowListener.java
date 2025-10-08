package org.gf.futilities.listeners;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

public class BowListener implements Listener {

    private final FUtilities plugin;
    private final Random random = new Random();

    // Map pour tracker quelle flèche vient de quel arc custom
    private final Map<UUID, String> arrowToItemKey = new HashMap<>();

    public BowListener(FUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player shooter = (Player) event.getEntity();
        ItemStack bow = event.getBow();

        if (bow == null || !ItemUtils.hasCustomTag(bow, "futilities_item")) {
            return;
        }

        // Sauvegarder l'itemKey pour cette flèche
        if (event.getProjectile() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getProjectile();
            String itemKey = ItemUtils.getCustomTag(bow, "futilities_item");
            arrowToItemKey.put(arrow.getUniqueId(), itemKey);
        }
    }

    @EventHandler
    public void onArrowHit(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Arrow)) {
            return;
        }

        Arrow arrow = (Arrow) event.getDamager();

        // Vérifier si cette flèche vient d'un arc custom
        if (!arrowToItemKey.containsKey(arrow.getUniqueId())) {
            return;
        }

        String itemKey = arrowToItemKey.get(arrow.getUniqueId());
        FileConfiguration config = plugin.getConfigManager().getConfig();

        if (!config.isSet("items." + itemKey + ".potion-effect")) {
            arrowToItemKey.remove(arrow.getUniqueId());
            return;
        }

        // Vérifier la chance d'activation
        int chance = config.getInt("items." + itemKey + ".potion-effect.chance", 100);
        if (random.nextInt(100) >= chance) {
            arrowToItemKey.remove(arrow.getUniqueId());
            return;
        }

        // Récupérer les paramètres de l'effet
        String effectType = config.getString("items." + itemKey + ".potion-effect.type");
        String target = config.getString("items." + itemKey + ".potion-effect.target", "ENEMY");
        int duration = config.getInt("items." + itemKey + ".potion-effect.duration", 100);
        int amplifier = config.getInt("items." + itemKey + ".potion-effect.amplifier", 1);

        PotionEffectType potionType = PotionEffectType.getByName(effectType);
        if (potionType == null) {
            plugin.getLogger().warning("Type d'effet inconnu pour l'arc: " + effectType);
            arrowToItemKey.remove(arrow.getUniqueId());
            return;
        }

        boolean debugMode = config.getBoolean("settings.debug", false);

        // Récupérer le tireur (shooter)
        Player shooter = null;
        if (arrow.getShooter() instanceof Player) {
            shooter = (Player) arrow.getShooter();
        }

        final Player finalShooter = shooter;

        // Appliquer l'effet de manière synchrone
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            PotionEffect effect = new PotionEffect(potionType, duration, amplifier - 1);

            // Appliquer l'effet selon la cible
            if (target.equalsIgnoreCase("SELF") && finalShooter != null) {
                finalShooter.addPotionEffect(effect);
                if (debugMode) {
                    finalShooter.sendMessage("§7[Debug] Effet d'arc active sur vous-meme: §e" + effectType);
                }
            } else if (target.equalsIgnoreCase("ENEMY") && event.getEntity() instanceof LivingEntity) {
                LivingEntity enemy = (LivingEntity) event.getEntity();
                enemy.addPotionEffect(effect);
                if (debugMode && finalShooter != null) {
                    finalShooter.sendMessage("§7[Debug] Effet d'arc applique a l'ennemi: §e" + effectType);
                }
            }
        }, 1L);

        // Nettoyer la map
        arrowToItemKey.remove(arrow.getUniqueId());
    }
}