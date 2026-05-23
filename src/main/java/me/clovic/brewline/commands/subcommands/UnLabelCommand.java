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

import me.clovic.brewline.Brew;
import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.api.events.brew.BrewModifyEvent;
import me.clovic.brewline.commands.SubCommand;
import me.clovic.brewline.configuration.files.Lang;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class UnLabelCommand implements SubCommand {
    @Override
    public void execute(BrewlinePlugin breweryPlugin, Lang lang, CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        ItemStack hand = player.getInventory().getItemInMainHand();
        if (hand.getType() != Material.AIR) {
            Brew brew = Brew.get(hand);
            if (brew != null) {
                if (!brew.isUnlabeled()) {
                    ItemMeta origMeta = hand.getItemMeta();
                    brew.unLabel(hand);
                    brew.touch();
                    ItemMeta meta = hand.getItemMeta();
                    assert meta != null;
                    BrewModifyEvent modifyEvent = new BrewModifyEvent(brew, meta, BrewModifyEvent.Type.UNLABEL);
                    BrewlinePlugin.getInstance().getServer().getPluginManager().callEvent(modifyEvent);
                    if (modifyEvent.isCancelled()) {
                        hand.setItemMeta(origMeta);
                        return;
                    }
                    brew.save(meta);
                    hand.setItemMeta(meta);
                    lang.sendEntry(sender, "CMD_UnLabel");
                } else {
                    lang.sendEntry(sender, "Error_AlreadyUnlabeled");
                }
                return;
            }
        }
        lang.sendEntry(sender, "Error_ItemNotPotion");
    }

    @Override
    public List<String> tabComplete(BrewlinePlugin breweryPlugin, CommandSender sender, String label, String[] args) {
        return null;
    }

    @Override
    public String permission() {
        return "brewline.command.strip";
    }

    @Override
    public boolean playerOnly() {
        return true;
    }
}
