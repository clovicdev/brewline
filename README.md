# Brewline ⚗️

Brewline is a brewing plugin for Minecraft servers that want drinks to feel like a real server feature instead of a small side mechanic. It handles cauldron brewing, distilling, barrel aging, configurable recipes, drunkenness, wakeup havens, storage, and clean inventory menus.

The plugin is built around one command, `/brewline`, with `/brl` as the only short alias.

◈ **Minecraft:** 1.20 and newer  
◈ **Server platforms:** Spigot, Paper, Purpur, and Folia-style scheduling  
⌁ **Java:** 17 or newer  
☄︎ **Jar:** `Brewline-1.0.0.jar`  
⚗️ **Main package:** `me.clovic.brewline`

## Install

1. Put `Brewline-1.0.0.jar` in your server's `plugins` folder.
2. Start the server once so Brewline can create its files.
3. Open `plugins/Brewline` and edit the config files you need.
4. Run `/brewline refresh` after recipe or message changes.

Paper is the best target for most servers. Folia-style scheduling metadata is included.

## First Run

Players brew by cooking ingredients in cauldrons, bottling the result, distilling it when a recipe needs refinement, and aging it in barrels. The final quality depends on recipe timing, ingredients, wood type, aging time, and distillation.

Staff can use `/brewline hub` for the main menu, then jump into recipe browsing, player status, brew inspection, barrel checks, cauldron monitoring, and admin tools.

## Commands

| Command | Purpose |
|---|---|
| `/brewline` | Opens the player hub, or prints guide text from console |
| `/brewline guide [page]` | Shows help pages |
| `/brewline hub` | Opens the main GUI |
| `/brewline status [player]` | Shows drunkenness and quality |
| `/brewline identify` | Shows the config id for the held item |
| `/brewline cork` | Opens the brew sealer |
| `/brewline duplicate [amount]` | Duplicates the held brew |
| `/brewline discard` | Deletes the held brew |
| `/brewline preserve` | Makes the held brew static |
| `/brewline strip` | Removes detailed label text from a brew |
| `/brewline craft <recipe> [quality] [player]` | Creates a brew item |
| `/brewline pour <recipe> [quality] [player]` | Simulates drinking a brew |
| `/brewline refine [runs]` | Distills the held brew |
| `/brewline mature <wood> <years>` | Ages the held brew |
| `/brewline lab <options...> [ingredients...]` | Simulates a custom brewing run |
| `/brewline purge [player] [amount]` | Forces purge behavior |
| `/brewline calibrate <player> <drunkenness> [quality]` | Sets player alcohol values |
| `/brewline refresh` | Reloads config, language, recipes, and runtime settings |
| `/brewline addons refresh` | Reloads addons |
| `/brewline storage refresh` | Reinitializes storage |
| `/brewline storage flush` | Saves storage immediately |
| `/brewline diagnostics` | Prints debug information |
| `/brewline metrics` | Shows plugin stats |
| `/brewline about` | Shows version and build info |
| `/brewline havens mark` | Adds a wakeup point |
| `/brewline havens browse [page] [world]` | Lists wakeup points |
| `/brewline havens tour [id]` | Tests wakeup points |
| `/brewline havens erase <id>` | Removes a wakeup point |
| `/brewline havens stop` | Cancels wakeup-point testing |

Old command names and old aliases are not registered by default. Brewline uses `/brewline` and `/brl`.

## Menus

Brewline includes native Bukkit inventory menus with dark titles, copper/cyan accents, and short hover text:

- Main hub
- Player status
- Recipe book
- Brew inspector
- Barrel inspector
- Cauldron monitor
- Recipe lab
- Admin dashboard
- Brew sealer

The menus are meant to be quick. They do not try to explain the whole plugin on one screen.

## Permissions

| Permission | Use |
|---|---|
| `brewline.user` | Normal player access |
| `brewline.mod` | Moderation tools and wakeup haven controls |
| `brewline.admin` | Admin commands, dashboards, reloads, and item tools |
| `brewline.*` | Full access |

Command permissions use `brewline.command.*`.

Common examples:

- `brewline.command.hub`
- `brewline.command.status`
- `brewline.command.craft`
- `brewline.command.refresh`
- `brewline.command.storage`
- `brewline.command.havens`

## Config Files

Brewline writes its files under `plugins/Brewline`.

| File | What it controls |
|---|---|
| `config.yml` | Storage, integrations, drunkenness, behavior, and runtime settings |
| `recipes.yml` | Finished brews and their recipe rules |
| `cauldron.yml` | Cauldron bases and accepted ingredient timing |
| `custom-items.yml` | Custom ingredient matching |
| `languages/en.yml` | Player and staff messages |

Use `/brewline refresh` after normal recipe, message, or behavior edits. Restart the server after storage changes.

## Build From Source

```bash
./gradlew clean test shadowJar
```

The main jar is written to:

```text
build/libs/Brewline-1.0.0.jar
```

To copy the jar into `dist/`:

```bash
./gradlew copyDistJar
```

## Notice

Brewline is a modified GPLv3 derivative of BreweryX.

Upstream project:

- BreweryX by The Brewery Team
- Repository: https://github.com/BreweryTeam/BreweryX
- Source baseline used for this fork: `5f92508e6401e0ff25535af9bc7490c4ba2af76a`

BreweryX is itself based on the original Brewery plugin by DieReicheErethons and contributors.
