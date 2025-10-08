package org.gf.futilities.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.gf.futilities.FUtilities;
import org.gf.futilities.commands.FarmCommand;
import org.gf.futilities.managers.FarmProgressManager;
import org.gf.futilities.utils.ItemUtils;
import org.gf.futilities.utils.CropUtils;

import java.util.*;

public class FarmListener implements Listener {

    private final FUtilities plugin;

    public FarmListener(FUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !ItemUtils.hasCustomTag(item, "futilities_farmhoe")) {
            return;
        }

        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) {
            return;
        }

        int level = ItemUtils.getIntTag(item, "farm_level", 1);
        List<String> abilities = getAbilities(level);

        // Labourage
        if (clickedBlock.getType() == Material.GRASS || clickedBlock.getType() == Material.DIRT) {
            if (abilities.contains("TILL_3x3")) {
                till3x3(clickedBlock, player);
            } else {
                clickedBlock.setType(Material.SOIL);
            }
            event.setCancelled(true);
        }

        // Plantation
        else if (clickedBlock.getType() == Material.SOIL) {
            if (abilities.contains("PLANT_3x3")) {
                plant3x3(clickedBlock, player);
            } else {
                plantSingle(clickedBlock, player);
            }
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !ItemUtils.hasCustomTag(item, "futilities_farmhoe")) {
            return;
        }

        Block block = event.getBlock();
        if (!CropUtils.isCropBlock(block)) {
            return;
        }

        // Vérifier si la culture est mature
        if (!CropUtils.isCropMature(block)) {
            event.setCancelled(true);
            return;
        }

        int level = ItemUtils.getIntTag(item, "farm_level", 1);
        List<String> abilities = getAbilities(level);

        // Récolte et expérience
        if (abilities.contains("HARVEST_3x3")) {
            harvest3x3(block, player, item, abilities);
        } else {
            harvestSingle(block, player, item, abilities);
        }

        event.setCancelled(true);
    }

    private boolean isMature(Block block) {
        return CropUtils.isCropMature(block);
    }

    private void harvestSingle(Block block, Player player, ItemStack hoe, List<String> abilities) {
        Collection<ItemStack> drops = block.getDrops();

        // Appliquer multiplicateur
        double multiplier = getMultiplier(abilities);

        for (ItemStack drop : drops) {
            int amount = (int) (drop.getAmount() * multiplier);
            drop.setAmount(amount);
            player.getInventory().addItem(drop);
        }

        // Replanter
        CropUtils.replantCrop(block);

        // Ajouter expérience pour cette culture spécifique
        addCropExperience(player, hoe, block.getType(), 1);
    }

    private void harvest3x3(Block center, Player player, ItemStack hoe, List<String> abilities) {
        Map<Material, Integer> harvestedByType = new HashMap<>();
        double multiplier = getMultiplier(abilities);

        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = center.getRelative(x, 0, z);
                if (CropUtils.isCropBlock(block) && CropUtils.isCropMature(block)) {
                    Collection<ItemStack> drops = block.getDrops();

                    for (ItemStack drop : drops) {
                        int amount = (int) (drop.getAmount() * multiplier);
                        drop.setAmount(amount);
                        player.getInventory().addItem(drop);
                    }

                    // Replanter
                    CropUtils.replantCrop(block);

                    // Compter par type de culture
                    Material cropType = block.getType();
                    harvestedByType.put(cropType, harvestedByType.getOrDefault(cropType, 0) + 1);
                }
            }
        }

        // Ajouter l'expérience pour chaque type de culture récolté
        for (Map.Entry<Material, Integer> entry : harvestedByType.entrySet()) {
            FarmProgressManager.addCropExperience(hoe, entry.getKey(), entry.getValue());
        }

        if (!harvestedByType.isEmpty()) {
            // Mettre à jour le lore et vérifier le level up
            int currentLevel = ItemUtils.getIntTag(hoe, "farm_level", 1);
            FarmProgressManager.updateHoeLore(hoe, currentLevel);

            if (FarmProgressManager.canLevelUp(hoe, currentLevel)) {
                int nextLevel = currentLevel + 1;
                FarmProgressManager.levelUp(hoe, nextLevel);

                ItemStack newHoe = FarmCommand.createFarmHoe(nextLevel);
                player.setItemInHand(newHoe);

                player.sendMessage("§a✦ Votre houe est passée au niveau " + nextLevel + "!");
                player.sendMessage("§7Toutes les progressions ont été remises à zéro.");
            } else {
                player.setItemInHand(hoe);
            }
        }
    }

    private void till3x3(Block center, Player player) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = center.getRelative(x, 0, z);
                if (block.getType() == Material.GRASS || block.getType() == Material.DIRT) {
                    block.setType(Material.SOIL);
                }
            }
        }
    }

    private void plant3x3(Block center, Player player) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                Block block = center.getRelative(x, 0, z);
                if (block.getType() == Material.SOIL) {
                    plantSingle(block, player);
                }
            }
        }
    }

    private void plantSingle(Block soilBlock, Player player) {
        Block above = soilBlock.getRelative(0, 1, 0);
        if (above.getType() != Material.AIR) {
            return;
        }

        // Chercher des graines dans l'inventaire
        for (ItemStack item : player.getInventory().getContents()) {
            if (item == null) continue;

            if (CropUtils.isSeed(item.getType())) {
                Material cropType = CropUtils.getCropFromSeed(item.getType());
                if (cropType != null) {
                    above.setType(cropType);
                    above.setData((byte) 0);

                    // Consommer une graine
                    if (item.getAmount() > 1) {
                        item.setAmount(item.getAmount() - 1);
                    } else {
                        player.getInventory().remove(item);
                    }
                    break;
                }
            }
        }
    }

    private double getMultiplier(List<String> abilities) {
        if (abilities.contains("MULTIPLIER_2")) {
            return 2.0;
        } else if (abilities.contains("MULTIPLIER_1_5")) {
            return 1.5;
        }
        return 1.0;
    }

    private List<String> getAbilities(int level) {
        FileConfiguration config = plugin.getConfigManager().getConfig();
        return config.getStringList("farmhoe.levels." + level + ".abilities");
    }

    private void addCropExperience(Player player, ItemStack hoe, Material cropType, int amount) {
        int currentLevel = ItemUtils.getIntTag(hoe, "farm_level", 1);

        // Ajouter l'expérience pour ce type de culture
        FarmProgressManager.addCropExperience(hoe, cropType, amount);

        // Mettre à jour le lore avec les nouveaux compteurs
        FarmProgressManager.updateHoeLore(hoe, currentLevel);

        // Vérifier si on peut passer au niveau suivant
        if (FarmProgressManager.canLevelUp(hoe, currentLevel)) {
            int nextLevel = currentLevel + 1;

            // Level up!
            FarmProgressManager.levelUp(hoe, nextLevel);

            // Recréer la houe avec le nouveau niveau
            ItemStack newHoe = FarmCommand.createFarmHoe(nextLevel);
            player.setItemInHand(newHoe);

            player.sendMessage("§a✦ Votre houe est passée au niveau " + nextLevel + "!");
            player.sendMessage("§7Toutes les progressions ont été remises à zéro.");
        } else {
            // Mettre à jour l'item dans la main du joueur
            player.setItemInHand(hoe);
        }
    }
}