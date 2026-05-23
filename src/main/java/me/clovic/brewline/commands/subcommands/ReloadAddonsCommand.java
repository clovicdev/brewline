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
import me.clovic.brewline.commands.SubCommand;
import me.clovic.brewline.configuration.files.Lang;
import me.clovic.brewline.utility.Logging;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadAddonsCommand implements SubCommand {
    @Override
    public void execute(BrewlinePlugin breweryPlugin, Lang lang, CommandSender sender, String label, String[] args) {
        if (args.length > 1 && args[1].equalsIgnoreCase("confirm")) {
            AddonManager addonManager = BrewlinePlugin.getAddonManager();
            addonManager.unloadAddons();
            addonManager.loadAddons();
            addonManager.enableAddons();
            Logging.msg(sender, "Finished loading " + addonManager.getAddons().size() + " addon(s)");
            Logging.msg(sender, "&eUsing this command should be avoided as it can cause unpredictable behavior within addons!");
        } else {
            Logging.msg(sender, "&rThis command should be avoided as it can cause unpredictable behavior within addons, use &6/brewline addons refresh confirm &r to confirm.");
            Logging.msg(sender, "&aMost addons support reloading without using this command! Try using &6/brewline refresh &ainstead.");
        }
    }

    @Override
    public List<String> tabComplete(BrewlinePlugin breweryPlugin, CommandSender sender, String label, String[] args) {
        return List.of("confirm");
    }

    @Override
    public String permission() {
        return "brewline.command.addons.refresh";
    }

    @Override
    public boolean playerOnly() {
        return false;
    }
}
