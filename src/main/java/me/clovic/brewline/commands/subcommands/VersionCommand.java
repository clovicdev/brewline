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

package me.clovic.brewline.commands.subcommands;

import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.api.addons.AddonManager;
import me.clovic.brewline.api.addons.BreweryAddon;
import me.clovic.brewline.commands.SubCommand;
import me.clovic.brewline.configuration.files.Lang;
import me.clovic.brewline.utility.Logging;
import me.clovic.brewline.utility.releases.ReleaseChecker;
import org.bukkit.command.CommandSender;

import java.util.List;

public class VersionCommand implements SubCommand {
    @Override
    public void execute(BrewlinePlugin breweryPlugin, Lang lang, CommandSender sender, String label, String[] args) {
        StringBuilder addonString = new StringBuilder();


        List<BreweryAddon> addons = List.copyOf(AddonManager.LOADED_ADDONS);
        for (BreweryAddon addon : addons) {
            addonString.append(addon.getClass().getSimpleName());
            if (addons.indexOf(addon) < addons.size() - 1) {
                addonString.append("&f, &a");
            }
        }

        ReleaseChecker rc = ReleaseChecker.getInstance();

        Logging.msg(sender, "&6Brewline&7: &av" + rc.localVersion() + " &7(Latest: v" + rc.getResolvedLatestVersion() + ")");
        Logging.msg(sender, "&6Made by&7: &aClovic");
        Logging.msg(sender, "&6Baseline&7: &aBreweryX");
        Logging.msg(sender, "&6Note&7: &aClovic made Brewline using BreweryX as the baseline.");
        Logging.msg(sender, "&6Loaded addons&7: &a" + (addonString.isEmpty() ? "&7None" : addonString));
    }

    @Override
    public List<String> tabComplete(BrewlinePlugin breweryPlugin, CommandSender sender, String label, String[] args) {
        return null;
    }

    @Override
    public String permission() {
        return "brewline.command.about";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }
}
