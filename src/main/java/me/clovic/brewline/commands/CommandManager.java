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

package me.clovic.brewline.commands;

import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.commands.subcommands.AgeCommand;
import me.clovic.brewline.commands.subcommands.CopyCommand;
import me.clovic.brewline.commands.subcommands.CreateCommand;
import me.clovic.brewline.commands.subcommands.DataManagerCommand;
import me.clovic.brewline.commands.subcommands.DebugInfoCommand;
import me.clovic.brewline.commands.subcommands.DeleteCommand;
import me.clovic.brewline.commands.subcommands.DistillCommand;
import me.clovic.brewline.commands.subcommands.DrinkCommand;
import me.clovic.brewline.commands.subcommands.HelpCommand;
import me.clovic.brewline.commands.subcommands.InfoCommand;
import me.clovic.brewline.commands.subcommands.ItemName;
import me.clovic.brewline.commands.subcommands.PukeCommand;
import me.clovic.brewline.commands.subcommands.ReloadAddonsCommand;
import me.clovic.brewline.commands.subcommands.ReloadCommand;
import me.clovic.brewline.commands.subcommands.SealCommand;
import me.clovic.brewline.commands.subcommands.SetCommand;
import me.clovic.brewline.commands.subcommands.ShowStatsCommand;
import me.clovic.brewline.commands.subcommands.SimulateCommand;
import me.clovic.brewline.commands.subcommands.StaticCommand;
import me.clovic.brewline.commands.subcommands.UnLabelCommand;
import me.clovic.brewline.commands.subcommands.VersionCommand;
import me.clovic.brewline.commands.subcommands.WakeupCommand;
import me.clovic.brewline.configuration.ConfigManager;
import me.clovic.brewline.configuration.files.Lang;
import me.clovic.brewline.gui.BrewlineMenu;
import me.clovic.brewline.utility.Logging;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CommandManager implements TabExecutor {

    private static final BrewlinePlugin plugin = BrewlinePlugin.getInstance();
    private static final Lang lang = ConfigManager.getConfig(Lang.class);
    private static final List<CommandSpec> commandSpecs = new ArrayList<>();
    private static final Map<String, SubCommand> legacyCommands = new LinkedHashMap<>();

    public CommandManager() {
        if (!commandSpecs.isEmpty()) {
            return;
        }

        register(List.of("guide"), new HelpCommand(), "help");
        register(List.of("status"), new InfoCommand(), "info");
        register(List.of("identify"), new ItemName(), "itemName");
        register(List.of("cork"), new SealCommand(), "seal");
        register(List.of("duplicate"), new CopyCommand(), "copy");
        register(List.of("discard"), new DeleteCommand(), "delete");
        register(List.of("preserve"), new StaticCommand(), "static");
        register(List.of("strip"), new UnLabelCommand(), "unLabel");
        register(List.of("craft"), new CreateCommand(), "create");
        register(List.of("pour"), new DrinkCommand(), "drink");
        register(List.of("refine"), new DistillCommand(), "distill");
        register(List.of("mature"), new AgeCommand(), "age");
        register(List.of("lab"), new SimulateCommand(), "simulate");
        register(List.of("purge"), new PukeCommand(), "puke");
        register(List.of("calibrate"), new SetCommand(), "set");
        register(List.of("refresh"), new ReloadCommand(), "reload");
        register(List.of("diagnostics"), new DebugInfoCommand(), "debuginfo");
        register(List.of("metrics"), new ShowStatsCommand(), "showstats");
        register(List.of("about"), new VersionCommand(), "version");
        register(List.of("addons", "refresh"), new ReloadAddonsCommand(), "reloadaddons");
        register(List.of("storage", "refresh"), new DataManagerCommand(), "data", "reload");
        register(List.of("storage", "flush"), new DataManagerCommand(), "data", "save");
        register(List.of("havens", "mark"), new WakeupCommand(), "wakeup", "add");
        register(List.of("havens", "browse"), new WakeupCommand(), "wakeup", "list");
        register(List.of("havens", "tour"), new WakeupCommand(), "wakeup", "check");
        register(List.of("havens", "erase"), new WakeupCommand(), "wakeup", "remove");
        register(List.of("havens", "stop"), new WakeupCommand(), "wakeup", "cancel");

        commandSpecs.sort(Comparator.comparingInt(CommandSpec::pathLength).reversed());
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                BrewlineMenu.openHub(player);
            } else {
                CommandUtil.cmdHelp(sender, new String[] { "guide" });
            }
            return true;
        }

        if (args[0].equalsIgnoreCase("hub")) {
            if (!(sender instanceof Player player)) {
                lang.sendEntry(sender, "Error_PlayerCommand");
                return true;
            }
            if (!sender.hasPermission("brewline.command.hub")) {
                lang.sendEntry(sender, "Error_NoPermissions");
                return true;
            }
            BrewlineMenu.openHub(player);
            return true;
        }

        CommandSpec spec = find(args);
        if (spec == null) {
            lang.sendEntry(sender, "Error_UnknownCommand");
            Logging.msg(sender, "&7Use &b/" + label + " guide &7or &b/" + label + " hub&7.");
            return true;
        }

        SubCommand subCommand = spec.subCommand();
        String permission = subCommand.permission();
        if (subCommand.playerOnly() && !(sender instanceof Player)) {
            lang.sendEntry(sender, "Error_PlayerCommand");
            return true;
        } else if (permission != null && !sender.hasPermission(permission)) {
            lang.sendEntry(sender, "Error_NoPermissions");
            return true;
        }

        subCommand.execute(plugin, lang, sender, label, spec.forwardArgs(args));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            return List.of();
        }

        List<String> branchSuggestions = branchSuggestions(sender, args);
        if (!branchSuggestions.isEmpty()) {
            return branchSuggestions;
        }

        CommandSpec spec = find(args);
        if (spec != null) {
            return spec.subCommand().tabComplete(plugin, sender, label, spec.forwardArgs(args));
        }
        return List.of();
    }

    public static void execute(Class<? extends SubCommand> clazz, CommandSender sender, String label, String[] args) {
        legacyCommands.values().stream()
            .filter(subCommand -> subCommand.getClass().equals(clazz))
            .findFirst()
            .ifPresent(subCommand -> subCommand.execute(plugin, lang, sender, label, args));
    }

    public static void addSubCommand(String name, SubCommand subCommand) {
        String normalized = name.toLowerCase(Locale.ROOT);
        removeSubCommand(normalized);
        commandSpecs.add(new CommandSpec(List.of(normalized), subCommand, List.of(normalized)));
        commandSpecs.sort(Comparator.comparingInt(CommandSpec::pathLength).reversed());
        legacyCommands.put(normalized, subCommand);
    }

    public static void addSubCommand(SubCommand subCommand, String... names) {
        for (String name : names) {
            addSubCommand(name, subCommand);
        }
    }

    public static void removeSubCommand(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        commandSpecs.removeIf(spec -> spec.path().size() == 1 && spec.path().get(0).equals(normalized));
        legacyCommands.remove(normalized);
    }

    public static void removeSubCommand(String... names) {
        for (String name : names) {
            removeSubCommand(name);
        }
    }

    public static void removeSubCommand(SubCommand subCommand) {
        commandSpecs.removeIf(spec -> spec.subCommand() == subCommand);
        legacyCommands.values().removeIf(registered -> registered == subCommand);
    }

    private static void register(List<String> path, SubCommand subCommand, String... forwardedPrefix) {
        commandSpecs.add(new CommandSpec(path, subCommand, List.of(forwardedPrefix)));
        legacyCommands.putIfAbsent(forwardedPrefix[0].toLowerCase(Locale.ROOT), subCommand);
    }

    private static CommandSpec find(String[] args) {
        return commandSpecs.stream()
            .filter(spec -> spec.matches(args))
            .findFirst()
            .orElse(null);
    }

    private static List<String> branchSuggestions(CommandSender sender, String[] args) {
        String typed = args[args.length - 1].toLowerCase(Locale.ROOT);
        List<String> suggestions = new ArrayList<>();

        if (args.length == 1) {
            addSuggestion(suggestions, "hub", typed);
        }

        for (CommandSpec spec : commandSpecs) {
            if (!spec.canUse(sender)) {
                continue;
            }
            List<String> path = spec.path();
            if (args.length > path.size()) {
                continue;
            }

            boolean prefixMatches = true;
            for (int i = 0; i < args.length - 1; i++) {
                if (!path.get(i).equalsIgnoreCase(args[i])) {
                    prefixMatches = false;
                    break;
                }
            }
            if (prefixMatches) {
                addSuggestion(suggestions, path.get(args.length - 1), typed);
            }
        }
        return suggestions.stream().distinct().sorted().toList();
    }

    private static void addSuggestion(List<String> suggestions, String candidate, String typed) {
        if (candidate.toLowerCase(Locale.ROOT).startsWith(typed)) {
            suggestions.add(candidate);
        }
    }

    private record CommandSpec(List<String> path, SubCommand subCommand, List<String> forwardedPrefix) {
        boolean matches(String[] args) {
            if (args.length < path.size()) {
                return false;
            }
            for (int i = 0; i < path.size(); i++) {
                if (!path.get(i).equalsIgnoreCase(args[i])) {
                    return false;
                }
            }
            return true;
        }

        boolean canUse(CommandSender sender) {
            String permission = subCommand.permission();
            return permission == null || sender.hasPermission(permission);
        }

        int pathLength() {
            return path.size();
        }

        String[] forwardArgs(String[] args) {
            List<String> forwarded = new ArrayList<>(forwardedPrefix);
            if (args.length > path.size()) {
                forwarded.addAll(Arrays.asList(args).subList(path.size(), args.length));
            }
            return forwarded.toArray(String[]::new);
        }
    }
}
