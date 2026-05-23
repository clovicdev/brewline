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

package me.clovic.brewline.integration;

import me.clovic.brewline.integration.barrel.WGBarrel;
import me.clovic.brewline.integration.barrel.WGBarrel5;
import me.clovic.brewline.integration.barrel.WGBarrel6;
import me.clovic.brewline.integration.barrel.WGBarrel7;
import me.clovic.brewline.utility.Logging;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

@Getter
@Setter
public class WorldGuarkHook extends Hook {

    public static final WorldGuarkHook WORLDGUARD = new WorldGuarkHook("WorldGuard", config.isUseWorldGuard());

    private WGBarrel wgBarrel;


    public WorldGuarkHook(String name, boolean enabled) {
        super(name, enabled);

        if (!isEnabled()) {
            return;
        }

        Plugin plugin = this.getPlugin();

        if (plugin == null) {
            Logging.errorLog("Failed loading WorldGuard Integration! Opening Barrels will NOT work!");
            Logging.errorLog("Brewery was tested with version 5.8, 6.1 and 7.0 of WorldGuard!");
            Logging.errorLog("Disable the WorldGuard support in the config and do /brewline refresh");
            return;
        }

        String wgv = plugin.getDescription().getVersion();
        if (wgv.startsWith("6.")) {
            wgBarrel = new WGBarrel6();
        } else if (wgv.startsWith("5.")) {
            wgBarrel = new WGBarrel5();
        } else {
            wgBarrel = new WGBarrel7();
        }
    }
}
