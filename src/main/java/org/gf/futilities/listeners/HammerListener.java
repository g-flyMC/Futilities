package org.gf.futilities.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.HashSet;
import java.util.Set;

public class HammerListener implements Listener {

    private final FUtilities plugin;
    private final Set<Material> MINEABLE_BLOCKS = new HashSet<>();

    public HammerListener(FUtilities plugin) {
        this.plugin = plugin;

        // Blocs minables avec une pioche
        MINEABLE_BLOCKS.add(Material.STONE);
        MINEABLE_BLOCKS.add(Material.COBBLESTONE);
        MINEABLE_BLOCKS.add(Material.COAL_ORE);
        MINEABLE_BLOCKS.add(Material.IRON_ORE);
        MINEABLE_BLOCKS.add(Material.GOLD_ORE);
        MINEABLE_BLOCKS.add(Material.DIAMOND_ORE);
        MINEABLE_BLOCKS.add(Material.EMERALD_ORE);
        MINEABLE_BLOCKS.add(Material.LAPIS_ORE);
        MINEABLE_BLOCKS.add(Material.REDSTONE_ORE);
        MINEABLE_BLOCKS.add(Material.QUARTZ_ORE);
        MINEABLE_BLOCKS.add(Material.OBSIDIAN);
        MINEABLE_BLOCKS.add(Material.NETHERRACK);
        MINEABLE_BLOCKS.add(Material.ENDER_STONE);
        MINEABLE_BLOCKS.add(Material.SANDSTONE);
        MINEABLE_BLOCKS.add(Material.BRICK);
        MINEABLE_BLOCKS.add(Material.NETHER_BRICK);
        MINEABLE_BLOCKS.add(Material.STONE_PLATE);
        MINEABLE_BLOCKS.add(Material.IRON_PLATE);
        MINEABLE_BLOCKS.add(Material.GOLD_PLATE);
        MINEABLE_BLOCKS.add(Material.GLOWSTONE);
        MINEABLE_BLOCKS.add(Material.SMOOTH_BRICK);
        MINEABLE_BLOCKS.add(Material.MOSSY_COBBLESTONE);
        MINEABLE_BLOCKS.add(Material.FURNACE);
        MINEABLE_BLOCKS.add(Material.DISPENSER);
        MINEABLE_BLOCKS.add(Material.DROPPER);
        MINEABLE_BLOCKS.add(Material.HOPPER);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getItemInHand();

        if (item == null || !ItemUtils.hasCustomTag(item, "futilities_hammer")) {
            return;
        }

        Block centerBlock = event.getBlock();

        if (!MINEABLE_BLOCKS.contains(centerBlock.getType())) {
            return;
        }

        // Déterminer la face regardée par le joueur
        BlockFace face = getTargetBlockFace(player, centerBlock);

        // Miner en 3x3 selon la face
        mine3x3(player, centerBlock, face);
    }

    private BlockFace getTargetBlockFace(Player player, Block block) {
        // Obtenir la direction du regard du joueur
        double playerYaw = player.getLocation().getYaw();
        double playerPitch = player.getLocation().getPitch();

        // Si le joueur regarde fortement vers le haut ou vers le bas
        if (Math.abs(playerPitch) > 50) {
            if (playerPitch < 0) {
                return BlockFace.UP; // Regarde vers le haut
            } else {
                return BlockFace.DOWN; // Regarde vers le bas
            }
        }

        // Sinon, déterminer la direction horizontale
        // Normaliser le yaw entre 0 et 360
        playerYaw = (playerYaw + 360) % 360;

        if (playerYaw >= 315 || playerYaw < 45) {
            return BlockFace.SOUTH;
        } else if (playerYaw >= 45 && playerYaw < 135) {
            return BlockFace.WEST;
        } else if (playerYaw >= 135 && playerYaw < 225) {
            return BlockFace.NORTH;
        } else {
            return BlockFace.EAST;
        }
    }

    private void mine3x3(Player player, Block center, BlockFace face) {
        Set<Block> blocksToBreak = new HashSet<>();

        switch (face) {
            case UP:
            case DOWN:
                // Mine en 3x3 horizontal (X et Z)
                for (int x = -1; x <= 1; x++) {
                    for (int z = -1; z <= 1; z++) {
                        Block b = center.getRelative(x, 0, z);
                        if (MINEABLE_BLOCKS.contains(b.getType())) {
                            blocksToBreak.add(b);
                        }
                    }
                }
                break;

            case NORTH:
            case SOUTH:
                // Mine en 3x3 vertical (X et Y)
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        Block b = center.getRelative(x, y, 0);
                        if (MINEABLE_BLOCKS.contains(b.getType())) {
                            blocksToBreak.add(b);
                        }
                    }
                }
                break;

            case EAST:
            case WEST:
                // Mine en 3x3 vertical (Z et Y)
                for (int z = -1; z <= 1; z++) {
                    for (int y = -1; y <= 1; y++) {
                        Block b = center.getRelative(0, y, z);
                        if (MINEABLE_BLOCKS.contains(b.getType())) {
                            blocksToBreak.add(b);
                        }
                    }
                }
                break;
        }

        // Casser tous les blocs et dropper les items
        for (Block block : blocksToBreak) {
            if (block.equals(center)) {
                continue; // Le bloc central est déjà géré par l'event
            }

            // Dropper les items
            block.breakNaturally(player.getItemInHand());
        }
    }
}