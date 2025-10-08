package org.gf.futilities.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.ArrayList;
import java.util.List;

public class BuilderListener implements Listener {

    private final FUtilities plugin;

    public BuilderListener(FUtilities plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.RIGHT_CLICK_AIR) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !ItemUtils.hasCustomTag(item, "futilities_builder")) {
            return;
        }

        event.setCancelled(true);

        // Récupérer les données de l'item
        int remainingUses = ItemUtils.getIntTag(item, "builder_uses", 0);
        String blockType = ItemUtils.getCustomTag(item, "builder_block");

        if (remainingUses <= 0) {
            player.sendMessage("§cCette batisseuse n'a plus d'utilisations!");
            return;
        }

        Material blockMaterial = Material.getMaterial(blockType);
        if (blockMaterial == null) {
            player.sendMessage("§cType de bloc invalide: " + blockType);
            return;
        }

        // Déterminer la position de construction
        Location buildLocation;
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            buildLocation = event.getClickedBlock().getLocation();
        } else {
            buildLocation = player.getLocation();
        }

        // Construire la colonne
        boolean success = buildColumn(buildLocation, blockMaterial, player);

        if (success) {
            // Décrémenter les utilisations
            remainingUses--;

            if (remainingUses > 0) {
                // Mettre à jour l'item
                ItemUtils.setIntTag(item, "builder_uses", remainingUses);
                updateBuilderLore(item, remainingUses);
                player.sendMessage("§aColonne construite! Utilisations restantes: §e" + remainingUses);
            } else {
                // Supprimer l'item
                player.setItemInHand(null);
                player.sendMessage("§6Votre batisseuse est épuisée et disparaît!");
            }
        }
    }

    private boolean buildColumn(Location location, Material material, Player player) {
        int blocksPlaced = 0;
        int playerY = location.getBlockY();

        // Construire de Y=0 à Y=256
        for (int y = 0; y <= 256; y++) {
            Block block = location.getWorld().getBlockAt(location.getBlockX(), y, location.getBlockZ());

            // Ne pas remplacer les blocs solides sauf si c'est de l'air
            if (block.getType() == Material.AIR) {
                block.setType(material);
                blocksPlaced++;
            }
            // Si c'est le même matériau, continuer
            else if (block.getType() == material) {
                continue;
            }
            // Sinon, ne pas remplacer les autres blocs solides
        }

        if (blocksPlaced == 0) {
            player.sendMessage("§cAucun bloc n'a pu être placé à cette position!");
            return false;
        }

        return true;
    }

    private void updateBuilderLore(ItemStack item, int remainingUses) {
        ItemMeta meta = item.getItemMeta();
        List<String> lore = meta.getLore();

        if (lore != null) {
            List<String> newLore = new ArrayList<>();
            for (String line : lore) {
                // Ne pas modifier les tags NBT cachés
                if (!line.startsWith("§0§k")) {
                    // Remplacer le placeholder %uses%
                    newLore.add(line.replaceAll("§eUtilisations restantes: §e\\d+", "§7Utilisations restantes: §e" + remainingUses)
                            .replace("%uses%", String.valueOf(remainingUses)));
                } else {
                    newLore.add(line);
                }
            }
            meta.setLore(newLore);
            item.setItemMeta(meta);
        }
    }
}
