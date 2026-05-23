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

package me.clovic.brewline.configuration.files;

import me.clovic.brewline.configuration.AbstractOkaeriConfigFile;
import me.clovic.brewline.configuration.ConfigManager;
import me.clovic.brewline.configuration.annotation.LocalizedComment;
import me.clovic.brewline.configuration.annotation.OkaeriConfigFileOptions;
import me.clovic.brewline.configuration.sector.CustomItemsSector;
import me.clovic.brewline.configuration.sector.capsule.ConfigCustomItem;
import me.clovic.brewline.recipe.RecipeItem;
import eu.okaeri.configs.annotation.Header;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Header("!!! IMPORTANT: BreweryX configuration files do NOT support external comments! If you add any comments, they will be overwritten !!!")
@OkaeriConfigFileOptions("custom-items.yml")
@Setter
public class CustomItemsFile extends AbstractOkaeriConfigFile {

    @LocalizedComment("customItemsFile.header")
    private Map<String, ConfigCustomItem> customItems = new CustomItemsSector().getCapsules();

    // Backwards compatibility, merges custom items from the default config
    public Map<String, ConfigCustomItem> getCustomItems() {
        Map<String, ConfigCustomItem> map = new HashMap<>(this.customItems);
        map.putAll(ConfigManager.getConfig(Config.class).getCustomItems());
        return map;
    }

    public List<RecipeItem> getRecipeItems() {
        List<RecipeItem> recipeItems = new ArrayList<>();

        for (var entry : getCustomItems().entrySet()) {
            ConfigCustomItem customItem = entry.getValue();
            recipeItems.add(RecipeItem.fromConfigCustom(entry.getKey(), customItem));
        }
        return recipeItems;
    }
}
