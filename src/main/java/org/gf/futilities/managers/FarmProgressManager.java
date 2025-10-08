package org.gf.futilities.managers;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.gf.futilities.FUtilities;
import org.gf.futilities.utils.ItemUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Gestionnaire de progression des houes par type de culture
 */
public class FarmProgressManager {

    public static class CropProgress {
        private int wheat = 0;
        private int carrot = 0;
        private int potato = 0;
        private int netherWart = 0;

        public CropProgress() {}

        public CropProgress(int wheat, int carrot, int potato, int netherWart) {
            this.wheat = wheat;
            this.carrot = carrot;
            this.potato = potato;
            this.netherWart = netherWart;
        }

        // Getters
        public int getWheat() { return wheat; }
        public int getCarrot() { return carrot; }
        public int getPotato() { return potato; }
        public int getNetherWart() { return netherWart; }

        // Setters
        public void setWheat(int wheat) { this.wheat = wheat; }
        public void setCarrot(int carrot) { this.carrot = carrot; }
        public void setPotato(int potato) { this.potato = potato; }
        public void setNetherWart(int netherWart) { this.netherWart = netherWart; }

        // Ajouter de l'expérience
        public void addWheat(int amount) { this.wheat += amount; }
        public void addCarrot(int amount) { this.carrot += amount; }
        public void addPotato(int amount) { this.potato += amount; }
        public void addNetherWart(int amount) { this.netherWart += amount; }

        // Remise à zéro
        public void reset() {
            this.wheat = 0;
            this.carrot = 0;
            this.potato = 0;
            this.netherWart = 0;
        }

        // Sérialisation pour NBT
        public String serialize() {
            return wheat + "," + carrot + "," + potato + "," + netherWart;
        }

        // Désérialisation depuis NBT
        public static CropProgress deserialize(String data) {
            try {
                String[] parts = data.split(",");
                if (parts.length == 4) {
                    return new CropProgress(
                            Integer.parseInt(parts[0]),
                            Integer.parseInt(parts[1]),
                            Integer.parseInt(parts[2]),
                            Integer.parseInt(parts[3])
                    );
                }
            } catch (NumberFormatException ignored) {}
            return new CropProgress();
        }
    }

    /**
     * Récupère la progression des cultures d'une houe
     */
    public static CropProgress getProgress(ItemStack hoe) {
        String data = ItemUtils.getCustomTag(hoe, "crop_progress");
        if (data != null) {
            return CropProgress.deserialize(data);
        }
        return new CropProgress();
    }

    /**
     * Sauvegarde la progression des cultures d'une houe
     */
    public static void saveProgress(ItemStack hoe, CropProgress progress) {
        ItemUtils.setCustomTag(hoe, "crop_progress", progress.serialize());
    }

    /**
     * Ajoute de l'expérience pour un type de culture
     */
    public static void addCropExperience(ItemStack hoe, Material cropType, int amount) {
        CropProgress progress = getProgress(hoe);

        switch (cropType) {
            case CROPS:
                progress.addWheat(amount);
                break;
            case CARROT:
                progress.addCarrot(amount);
                break;
            case POTATO:
                progress.addPotato(amount);
                break;
            case NETHER_WARTS:
                progress.addNetherWart(amount);
                break;
        }

        saveProgress(hoe, progress);
    }

    /**
     * Vérifie si une houe peut passer au niveau suivant
     */
    public static boolean canLevelUp(ItemStack hoe, int currentLevel) {
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();
        int nextLevel = currentLevel + 1;

        if (!config.isSet("farmhoe.levels." + nextLevel)) {
            return false; // Pas de niveau suivant
        }

        CropProgress progress = getProgress(hoe);

        // Vérifier les prérequis pour chaque culture
        int requiredWheat = config.getInt("farmhoe.levels." + nextLevel + ".required.wheat", 0);
        int requiredCarrot = config.getInt("farmhoe.levels." + nextLevel + ".required.carrot", 0);
        int requiredPotato = config.getInt("farmhoe.levels." + nextLevel + ".required.potato", 0);
        int requiredNetherWart = config.getInt("farmhoe.levels." + nextLevel + ".required.nether_wart", 0);

        return progress.getWheat() >= requiredWheat &&
                progress.getCarrot() >= requiredCarrot &&
                progress.getPotato() >= requiredPotato &&
                progress.getNetherWart() >= requiredNetherWart;
    }

    /**
     * Passe une houe au niveau suivant et remet les compteurs à zéro
     */
    public static void levelUp(ItemStack hoe, int newLevel) {
        CropProgress progress = getProgress(hoe);
        progress.reset();
        saveProgress(hoe, progress);

        ItemUtils.setIntTag(hoe, "farm_level", newLevel);
    }

    /**
     * Obtient les prérequis pour un niveau donné
     */
    public static Map<String, Integer> getRequirements(int level) {
        FileConfiguration config = FUtilities.getInstance().getConfigManager().getConfig();
        Map<String, Integer> requirements = new HashMap<>();

        requirements.put("wheat", config.getInt("farmhoe.levels." + level + ".required.wheat", 0));
        requirements.put("carrot", config.getInt("farmhoe.levels." + level + ".required.carrot", 0));
        requirements.put("potato", config.getInt("farmhoe.levels." + level + ".required.potato", 0));
        requirements.put("nether_wart", config.getInt("farmhoe.levels." + level + ".required.nether_wart", 0));

        return requirements;
    }

    /**
     * Met à jour le lore d'une houe avec les placeholders de progression
     */
    public static void updateHoeLore(ItemStack hoe, int level) {
        CropProgress progress = getProgress(hoe);
        Map<String, Integer> requirements = getRequirements(level + 1); // Prérequis pour le niveau suivant

        // Remplacer les placeholders
        ItemUtils.updateLorePlaceholder(hoe, "%wheat%", String.valueOf(progress.getWheat()));
        ItemUtils.updateLorePlaceholder(hoe, "%carrot%", String.valueOf(progress.getCarrot()));
        ItemUtils.updateLorePlaceholder(hoe, "%potato%", String.valueOf(progress.getPotato()));
        ItemUtils.updateLorePlaceholder(hoe, "%nether_wart%", String.valueOf(progress.getNetherWart()));

        ItemUtils.updateLorePlaceholder(hoe, "%req_wheat%", String.valueOf(requirements.get("wheat")));
        ItemUtils.updateLorePlaceholder(hoe, "%req_carrot%", String.valueOf(requirements.get("carrot")));
        ItemUtils.updateLorePlaceholder(hoe, "%req_potato%", String.valueOf(requirements.get("potato")));
        ItemUtils.updateLorePlaceholder(hoe, "%req_nether_wart%", String.valueOf(requirements.get("nether_wart")));
    }
}