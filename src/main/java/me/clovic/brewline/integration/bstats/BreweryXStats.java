/*
 * BreweryX Bukkit-Plugin for an alternate brewing process
 * Copyright (C) 2024 The Brewery Team
 *
 * This file is part of BreweryX.
 *
 * BreweryX is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * BreweryX is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with BreweryX. If not, see <http://www.gnu.org/licenses/gpl-3.0.html>.
 */

package me.clovic.brewline.integration.bstats;

import me.clovic.brewline.BCauldron;
import me.clovic.brewline.BPlayer;
import me.clovic.brewline.Barrel;
import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.configuration.ConfigManager;
import me.clovic.brewline.configuration.files.Config;
import me.clovic.brewline.integration.bstats.Metrics.AdvancedPie;
import me.clovic.brewline.integration.bstats.Metrics.DrilldownPie;
import me.clovic.brewline.integration.bstats.Metrics.SimplePie;
import me.clovic.brewline.integration.bstats.Metrics.SingleLineChart;
import me.clovic.brewline.recipe.BRecipe;
import me.clovic.brewline.utility.Logging;

import java.util.HashMap;
import java.util.Map;

/**
 * Stats which are exclusive to BreweryX.
 */
public class BreweryXStats {

    private static final int BSTATS_ID = 24059;

    private final Config config = ConfigManager.getConfig(Config.class);

    private String getBranch() {
        String versionString = BrewlinePlugin.getInstance().getDescription().getVersion();
        if (versionString.contains(";")) {
            return versionString.split(";")[1];
        }
        return "unknown";
    }


    public void setupBStats() {
        try {
            Metrics metrics = new Metrics(BrewlinePlugin.getInstance(), BSTATS_ID);

            metrics.addCustomChart(new DrilldownPie("storage_type", () -> {
                Map<String, Map<String, Integer>> map = new HashMap<>();
                String storageType = BrewlinePlugin.getDataManager().getType().getFormattedName();
                Map<String, Integer> entry = new HashMap<>();
                entry.put(storageType, 1);
                map.put(storageType, entry);
                return map;
            }));
            metrics.addCustomChart(new AdvancedPie("recipe_count", () -> {
                Map<String, Integer> valueMap = new HashMap<>();
                int recipeCount = BRecipe.getAllRecipes().size();
                if (recipeCount >= 150) {
                    valueMap.put("150+", recipeCount);
                } else if (recipeCount >= 100) {
                    valueMap.put("100-149", recipeCount);
                } else if (recipeCount >= 50) {
                    valueMap.put("50-99", recipeCount);
                } else if (recipeCount >= 25) {
                    valueMap.put("25-49", recipeCount);
                } else if (recipeCount >= 10) {
                    valueMap.put("10-24", recipeCount);
                } else if (recipeCount >= 5) {
                    valueMap.put("5-9", recipeCount);
                } else {
                    valueMap.put("1-4", recipeCount);
                }
                return valueMap;
            }));

            metrics.addCustomChart(new AdvancedPie("addons", () -> {
                Map<String, Integer> valueMap = new HashMap<>();
                int addonsAmount = BrewlinePlugin.getAddonManager().getAddons().size();
                if (addonsAmount >= 5) {
                    valueMap.put("5+", addonsAmount);
                } else if (addonsAmount >= 3) {
                    valueMap.put("3-4", addonsAmount);
                } else if (addonsAmount == 2) {
                    valueMap.put("2", addonsAmount);
                } else if (addonsAmount == 1) {
                    valueMap.put("1", addonsAmount);
                } else {
                    valueMap.put("0", addonsAmount);
                }
                return valueMap;
            }));

            metrics.addCustomChart(new SimplePie("language", config::getLanguage));
            metrics.addCustomChart(new SimplePie("branch", this::getBranch));

            metrics.addCustomChart(new SingleLineChart("drunk_players", BPlayer::numDrunkPlayers));
            metrics.addCustomChart(new SingleLineChart("barrels_built", Barrel.getAllBarrels()::size));
            metrics.addCustomChart(new SingleLineChart("cauldrons_boiling", BCauldron.bcauldrons::size));

        } catch (Exception | LinkageError e) {
            Logging.errorLog("Failed to submit stats data to bStats.org (BreweryXStats)", e);
        }
    }
}
