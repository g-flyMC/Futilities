package org.gf.futilities.utils;

import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Utilitaires pour la gestion des cultures
 * Compatible Spigot 1.12 / Minecraft 1.8+
 */
public class CropUtils {

    /**
     * Vérifie si une culture est mature en utilisant les data values
     * Compatible avec toutes les versions de Minecraft
     *
     * @param block Le bloc de culture à vérifier
     * @return true si la culture est mature
     */
    public static boolean isCropMature(Block block) {
        Material type = block.getType();
        byte data = block.getData();

        switch (type) {
            case CROPS:         // Blé
            case CARROT:        // Carottes
            case POTATO:        // Pommes de terre
                return data == 7; // Data value 7 = mature pour ces cultures

            case NETHER_WARTS:  // Nether Wart
                return data == 3; // Data value 3 = mature pour les nether warts

            default:
                return false;
        }
    }

    /**
     * Remet une culture au stade de croissance initial (data = 0)
     *
     * @param block Le bloc de culture à replanter
     */
    public static void replantCrop(Block block) {
        if (isCropBlock(block)) {
            block.setData((byte) 0);
        }
    }

    /**
     * Vérifie si un bloc est une culture valide
     *
     * @param block Le bloc à vérifier
     * @return true si c'est une culture
     */
    public static boolean isCropBlock(Block block) {
        Material type = block.getType();
        return type == Material.CROPS ||
                type == Material.CARROT ||
                type == Material.POTATO ||
                type == Material.NETHER_WARTS;
    }

    /**
     * Obtient le matériau de graine correspondant à une culture
     *
     * @param cropType Le type de culture
     * @return Le matériau de la graine correspondante
     */
    public static Material getSeedFromCrop(Material cropType) {
        switch (cropType) {
            case CROPS:
                return Material.SEEDS;
            case CARROT:
                return Material.CARROT_ITEM;
            case POTATO:
                return Material.POTATO_ITEM;
            case NETHER_WARTS:
                return Material.NETHER_STALK;
            default:
                return null;
        }
    }

    /**
     * Obtient le type de culture correspondant à une graine
     *
     * @param seedType Le type de graine
     * @return Le matériau de la culture correspondante
     */
    public static Material getCropFromSeed(Material seedType) {
        switch (seedType) {
            case SEEDS:
                return Material.CROPS;
            case CARROT_ITEM:
                return Material.CARROT;
            case POTATO_ITEM:
                return Material.POTATO;
            case NETHER_STALK:
                return Material.NETHER_WARTS;
            default:
                return null;
        }
    }

    /**
     * Vérifie si un matériau est une graine plantable
     *
     * @param material Le matériau à vérifier
     * @return true si c'est une graine
     */
    public static boolean isSeed(Material material) {
        return material == Material.SEEDS ||
                material == Material.CARROT_ITEM ||
                material == Material.POTATO_ITEM ||
                material == Material.NETHER_STALK;
    }

    /**
     * Obtient le pourcentage de croissance d'une culture
     *
     * @param block Le bloc de culture
     * @return Pourcentage de croissance (0-100)
     */
    public static int getGrowthPercentage(Block block) {
        if (!isCropBlock(block)) {
            return 0;
        }

        byte data = block.getData();
        Material type = block.getType();

        if (type == Material.NETHER_WARTS) {
            // Nether Warts: 0-3 stades
            return (int) ((data / 3.0) * 100);
        } else {
            // Autres cultures: 0-7 stades
            return (int) ((data / 7.0) * 100);
        }
    }
}