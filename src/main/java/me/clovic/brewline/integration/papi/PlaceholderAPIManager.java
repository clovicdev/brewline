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

package me.clovic.brewline.integration.papi;

import me.clovic.brewline.BPlayer;
import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.integration.papi.placeholders.DrunkennessBarsPlaceholder;
import me.clovic.brewline.integration.papi.placeholders.DrunkennessPlaceholder;
import me.clovic.brewline.integration.papi.placeholders.QualityPlaceholder;
import me.clovic.brewline.integration.papi.placeholders.QualityStarsPlaceholder;
import me.clovic.brewline.utility.BUtil;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderAPIManager extends PlaceholderExpansion {

    private static final BrewlinePlugin plugin = BrewlinePlugin.getInstance();
    private static final Map<String, Placeholder> placeholders = new HashMap<>();

    public PlaceholderAPIManager() {
        placeholders.put("drunkenness", new DrunkennessPlaceholder());
        placeholders.put("drunkennessbars", new DrunkennessBarsPlaceholder());
        placeholders.put("quality", new QualityPlaceholder());
        placeholders.put("qualitystars", new QualityStarsPlaceholder());
    }

    @Override
    public @NotNull String getIdentifier() {
        return "brewline";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Mitality & Jsinco";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        BPlayer bPlayer = BPlayer.get(player);
        if (bPlayer == null) bPlayer = new BPlayer(player.getUniqueId());

        String[] args = params.split("_");

        Placeholder placeholder = placeholders.get(args[0].toLowerCase());
        if (placeholder != null) {
            return placeholder.onReceivedRequest(plugin, player, bPlayer, args);
        }
        return null;
    }
}
