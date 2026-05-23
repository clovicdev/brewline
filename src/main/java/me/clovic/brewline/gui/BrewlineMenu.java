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

package me.clovic.brewline.gui;

import me.clovic.brewline.BCauldron;
import me.clovic.brewline.BPlayer;
import me.clovic.brewline.BSealer;
import me.clovic.brewline.Barrel;
import me.clovic.brewline.Brew;
import me.clovic.brewline.BrewlinePlugin;
import me.clovic.brewline.Wakeup;
import me.clovic.brewline.configuration.ConfigManager;
import me.clovic.brewline.configuration.files.Config;
import me.clovic.brewline.recipe.BRecipe;
import me.clovic.brewline.utility.BUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class BrewlineMenu {

    private static final String TITLE_PREFIX = "&#36d7d0&lBREWLINE &8| &r";
    private static final String COPPER = "&#c9874a";
    private static final String CYAN = "&#36d7d0";
    private static final String LIME = "&#a6d96a";
    private static final String MUTED = "&7";

    private BrewlineMenu() {
    }

    public static void openHub(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.HUB);
        Inventory inventory = create(holder, 54, "Hub");
        frame(inventory);

        inventory.setItem(10, button(mat("PLAYER_HEAD", "SKULL_ITEM"), CYAN + "&lStatus", "status",
            "&7View drunkenness, quality, and recovery.",
            "&8/" + command(player) + " status"));
        inventory.setItem(11, button(mat("WRITABLE_BOOK", "BOOK_AND_QUILL"), COPPER + "&lRecipe Book", "recipes",
            "&7Browse configured brews.",
            "&7Admin details stay compact."));
        inventory.setItem(12, button(Material.POTION, CYAN + "&lBrew Inspector", "inspect_brew",
            "&7Read the brew in your hand.",
            "&7Quality, alcohol, age, and state."));
        inventory.setItem(13, button(Material.CAULDRON, COPPER + "&lCauldron Monitor", "cauldron",
            "&7Look at a cauldron within reach.",
            "&7Shows cook state and controls."));
        inventory.setItem(14, button(mat("BARREL", "CHEST"), CYAN + "&lBarrel Inspector", "barrel",
            "&7Look at a barrel within reach.",
            "&7Shows size, capacity, and age."));
        inventory.setItem(15, button(mat("BREWING_STAND", "BREWING_STAND_ITEM"), COPPER + "&lRecipe Lab", "lab",
            "&7Open simulation shortcuts.",
            "&7Use command input for exact tests."));
        inventory.setItem(16, button(mat("COMPARATOR", "REDSTONE_COMPARATOR"), CYAN + "&lAdmin Dashboard", "admin",
            "&7Storage, integrations, and reload tools.",
            "&7Requires admin permissions."));
        inventory.setItem(22, button(mat("SMOKER", "FURNACE"), LIME + "&lCorking Station", "cork",
            "&7Seal brews for shops and trading.",
            "&8/" + command(player) + " cork"));

        player.openInventory(inventory);
    }

    public static void openStatus(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.STATUS);
        Inventory inventory = create(holder, 36, "Status");
        frame(inventory);

        BPlayer bPlayer = BPlayer.getPlayers().get(player.getUniqueId().toString());
        int drunkenness = bPlayer == null ? 0 : bPlayer.getDrunkeness();
        int quality = bPlayer == null ? 0 : bPlayer.getQuality();

        inventory.setItem(13, item(mat("HONEY_BOTTLE", "GLASS_BOTTLE"), CYAN + "&lCurrent State",
            "&7Drunkenness: " + COPPER + drunkenness + "%",
            "&7Quality: " + COPPER + quality + "/10",
            "&7Recovery: " + COPPER + "natural server tickdown",
            "",
            "&8Concise status for staff checks."));
        inventory.setItem(31, backButton());
        player.openInventory(inventory);
    }

    public static void openRecipes(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.RECIPES);
        Inventory inventory = create(holder, 54, "Recipe Book");
        frame(inventory);

        List<BRecipe> recipes = BRecipe.getAllRecipes();
        int[] slots = contentSlots();
        for (int i = 0; i < Math.min(slots.length, recipes.size()); i++) {
            BRecipe recipe = recipes.get(i);
            String id = recipe.getId() == null ? "unlisted" : recipe.getId();
            inventory.setItem(slots[i], item(Material.POTION, COPPER + recipe.getName(10),
                "&7Id: " + CYAN + id,
                "&7Cook: " + CYAN + recipe.getCookingTime() + " min",
                "&7Distill: " + CYAN + recipe.getDistillruns() + " run(s)",
                "&7Age: " + CYAN + recipe.getAge() + " year(s)",
                "&8Use /" + command(player) + " craft " + id));
        }

        inventory.setItem(49, backButton());
        player.openInventory(inventory);
    }

    public static void openBrewInspector(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.BREW);
        Inventory inventory = create(holder, 45, "Brew Inspector");
        frame(inventory);

        ItemStack hand = player.getInventory().getItemInMainHand();
        Brew brew = Brew.get(hand);
        if (brew == null) {
            inventory.setItem(22, item(Material.GLASS_BOTTLE, COPPER + "&lNo Brew Found",
                "&7Hold a Brewline potion in your main hand.",
                "&7Then open this panel again."));
        } else {
            String recipe = brew.getCurrentRecipe() == null ? "Unknown" : brew.getCurrentRecipe().getName(brew.getQuality());
            inventory.setItem(13, item(Material.POTION, CYAN + "&l" + recipe,
                "&7Quality: " + COPPER + brew.getQuality() + "/10",
                "&7Alcohol: " + COPPER + brew.getOrCalcAlc() + " ml",
                "&7Distill runs: " + COPPER + brew.getDistillRuns(),
                "&7Age: " + COPPER + String.format(Locale.US, "%.1f", brew.getAgeTime()),
                "&7Static: " + COPPER + yesNo(brew.isStatic()),
                "&7Sealed: " + COPPER + yesNo(brew.isSealed())));
            inventory.setItem(29, button(Material.CLOCK, COPPER + "&lPreserve", "preserve",
                "&7Make this brew static.",
                "&8/" + command(player) + " preserve"));
            inventory.setItem(31, button(Material.NAME_TAG, CYAN + "&lStrip Label", "strip",
                "&7Remove detailed brew data.",
                "&8/" + command(player) + " strip"));
            inventory.setItem(33, button(Material.PAPER, COPPER + "&lDuplicate", "duplicate",
                "&7Copy the held brew once.",
                "&8/" + command(player) + " duplicate 1"));
        }

        inventory.setItem(40, backButton());
        player.openInventory(inventory);
    }

    public static void openCauldron(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.CAULDRON);
        Inventory inventory = create(holder, 36, "Cauldron");
        frame(inventory);

        BCauldron cauldron = targetCauldron(player);
        if (cauldron == null) {
            inventory.setItem(13, item(Material.CAULDRON, COPPER + "&lNo Cauldron Targeted",
                "&7Look at a Brewline cauldron within 6 blocks.",
                "&7Then open the monitor again."));
        } else {
            inventory.setItem(13, item(Material.CAULDRON, CYAN + "&lActive Cauldron",
                "&7Cook time: " + COPPER + cauldron.getState() + " min",
                "&7World: " + COPPER + cauldron.getBlock().getWorld().getName(),
                "&7Location: " + COPPER + compactLocation(cauldron.getBlock()),
                "&7Particles: " + COPPER + yesNo(ConfigManager.getConfig(Config.class).isEnableCauldronParticles())));
        }

        inventory.setItem(31, backButton());
        player.openInventory(inventory);
    }

    public static void openBarrel(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.BARREL);
        Inventory inventory = create(holder, 36, "Barrel");
        frame(inventory);

        Barrel barrel = targetBarrel(player);
        if (barrel == null) {
            inventory.setItem(13, item(mat("BARREL", "CHEST"), COPPER + "&lNo Barrel Targeted",
                "&7Look at a Brewline barrel within 6 blocks.",
                "&7Then open the inspector again."));
        } else {
            inventory.setItem(13, item(mat("BARREL", "CHEST"), CYAN + "&lBarrel Status",
                "&7Type: " + COPPER + (barrel.isLarge() ? "Large" : "Small"),
                "&7Slots: " + COPPER + barrel.getInventory().getSize(),
                "&7Stored items: " + COPPER + storedItems(barrel),
                "&7Age: " + COPPER + String.format(Locale.US, "%.1f", barrel.getTime()),
                "&7Location: " + COPPER + compactLocation(barrel.getSpigot())));
        }

        inventory.setItem(31, backButton());
        player.openInventory(inventory);
    }

    public static void openLab(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.LAB);
        Inventory inventory = create(holder, 45, "Recipe Lab");
        frame(inventory);

        inventory.setItem(11, item(mat("BREWING_STAND", "BREWING_STAND_ITEM"), CYAN + "&lSimulation Command",
            "&7Use the command for exact input.",
            "&8/" + command(player) + " lab --recipe <id>",
            "&8/" + command(player) + " lab --cook 10 wheat/3"));
        inventory.setItem(15, button(mat("WRITABLE_BOOK", "BOOK_AND_QUILL"), COPPER + "&lOpen Recipe Book", "recipes",
            "&7Pick a recipe id before testing.",
            "&7Keeps trial runs tidy."));
        inventory.setItem(31, backButton());
        player.openInventory(inventory);
    }

    public static void openAdmin(Player player) {
        MenuHolder holder = new MenuHolder(MenuType.ADMIN);
        Inventory inventory = create(holder, 54, "Admin");
        frame(inventory);

        Config config = ConfigManager.getConfig(Config.class);
        inventory.setItem(10, item(Material.ENDER_CHEST, CYAN + "&lStorage",
            "&7Type: " + COPPER + config.getStorage().getType(),
            "&7Barrels: " + COPPER + Barrel.getAllBarrels().size(),
            "&7Cauldrons: " + COPPER + BCauldron.getBcauldrons().size(),
            "&7Players: " + COPPER + BPlayer.getPlayers().size(),
            "&7Havens: " + COPPER + Wakeup.getWakeups().size()));
        inventory.setItem(12, button(mat("REDSTONE_TORCH", "REDSTONE_TORCH_ON"), COPPER + "&lRefresh Config", "refresh",
            "&7Reload configs, recipes, and runtime settings.",
            "&8/" + command(player) + " refresh"));
        inventory.setItem(14, button(Material.HOPPER, CYAN + "&lFlush Storage", "storage_flush",
            "&7Save all storage immediately.",
            "&8/" + command(player) + " storage flush"));
        inventory.setItem(16, button(mat("COMPARATOR", "REDSTONE_COMPARATOR"), COPPER + "&lDiagnostics", "diagnostics",
            "&7Print debug details to chat.",
            "&8/" + command(player) + " diagnostics"));
        inventory.setItem(49, backButton());
        player.openInventory(inventory);
    }

    static void handle(Player player, String action) {
        switch (action) {
            case "status" -> openStatus(player);
            case "recipes" -> openRecipes(player);
            case "inspect_brew" -> openBrewInspector(player);
            case "cauldron" -> openCauldron(player);
            case "barrel" -> openBarrel(player);
            case "lab" -> openLab(player);
            case "admin" -> openAdmin(player);
            case "cork" -> player.openInventory(new BSealer(player).getInventory());
            case "back" -> openHub(player);
            case "preserve" -> run(player, "preserve");
            case "strip" -> run(player, "strip");
            case "duplicate" -> run(player, "duplicate 1");
            case "refresh" -> run(player, "refresh");
            case "storage_flush" -> run(player, "storage flush");
            case "diagnostics" -> run(player, "diagnostics");
            default -> {
            }
        }
    }

    private static Inventory create(MenuHolder holder, int size, String title) {
        Inventory inventory = Bukkit.createInventory(holder, size, BUtil.color(TITLE_PREFIX + COPPER + title));
        holder.inventory = inventory;
        return inventory;
    }

    private static void frame(Inventory inventory) {
        ItemStack edge = item(mat("BLACK_STAINED_GLASS_PANE", "STAINED_GLASS_PANE"), "&8", List.of());
        for (int slot = 0; slot < inventory.getSize(); slot++) {
            int row = slot / 9;
            int col = slot % 9;
            if (row == 0 || row == (inventory.getSize() / 9) - 1 || col == 0 || col == 8) {
                inventory.setItem(slot, edge);
            }
        }
    }

    private static ItemStack backButton() {
        return button(Material.ARROW, "&f&lBack", "back", "&7Return to the Brewline hub.");
    }

    private static ItemStack button(Material material, String name, String action, String... lore) {
        ItemStack item = item(material, name, lore);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.getPersistentDataContainer().set(BrewlineMenuListener.ACTION_KEY, BrewlineMenuListener.ACTION_TYPE, action);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static ItemStack item(Material material, String name, String... lore) {
        return item(material, name, List.of(lore));
    }

    private static ItemStack item(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(BUtil.color(name));
            meta.setLore(BUtil.colorArrayList(lore));
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Material mat(String modern, String legacy) {
        Material material = Material.matchMaterial(modern);
        if (material == null) {
            material = Material.matchMaterial(legacy);
        }
        return material == null ? Material.STONE : material;
    }

    private static int[] contentSlots() {
        return new int[] {
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34,
            37, 38, 39, 40, 41, 42, 43
        };
    }

    private static void run(Player player, String command) {
        player.closeInventory();
        player.performCommand("brewline " + command);
    }

    private static String command(Player player) {
        return player.hasPermission("brewline.command.hub") ? "brewline" : "brl";
    }

    private static String yesNo(boolean value) {
        return value ? "yes" : "no";
    }

    private static int storedItems(Barrel barrel) {
        int count = 0;
        for (ItemStack item : barrel.getInventory().getContents()) {
            if (item != null && !item.getType().isAir()) {
                count += item.getAmount();
            }
        }
        return count;
    }

    @SuppressWarnings("deprecation")
    private static Block targetBlock(Player player) {
        return player.getTargetBlock((java.util.Set<Material>) null, 6);
    }

    private static Barrel targetBarrel(Player player) {
        Block block = targetBlock(player);
        return block == null || block.getType().isAir() ? null : Barrel.get(block);
    }

    private static BCauldron targetCauldron(Player player) {
        Block block = targetBlock(player);
        return block == null || block.getType().isAir() ? null : BCauldron.getBcauldrons().get(block);
    }

    private static String compactLocation(Block block) {
        return block.getX() + ", " + block.getY() + ", " + block.getZ();
    }

    enum MenuType {
        HUB,
        STATUS,
        RECIPES,
        BREW,
        CAULDRON,
        BARREL,
        LAB,
        ADMIN
    }

    static final class MenuHolder implements InventoryHolder {
        private final MenuType type;
        private Inventory inventory;

        MenuHolder(MenuType type) {
            this.type = type;
        }

        MenuType type() {
            return type;
        }

        @Override
        public @NotNull Inventory getInventory() {
            return inventory;
        }
    }
}
