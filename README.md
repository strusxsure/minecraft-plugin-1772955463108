# SoulBoundSMP

A Minecraft Paper 1.21 plugin where players collect **Souls** instead of hearts. Gain special abilities, trade souls, or be revived via a ritual.

## Features
- **Soul Collection** – Kill a player → absorb a Soul.
- **Physical Soul Items** – Dropped on death, can be picked up for an extra Soul.
- **Abilities** – Spend a Soul to temporarily gain Strength, Speed, or an Extra Heart.
- **Weakened State** – When a player has 0 Souls their max health drops to 10 until revived.
- **Ritual Revival** – Use `/ritual` (admin command, placeholder for future expansion) to restore health.
- **GUI Menu** – `/souls` opens a custom inventory to view and activate abilities.
- **YAML Storage** – Soul counts are saved in `souls.yml`.
- **Configurable Cool‑down** – Prevents rapid farming.
- **Permissions** – Built‑in permission system for admins and custom command usage.
- **Modular Structure** – Easy to expand with new abilities or features.

## Installation
1. Build the plugin using Maven (`mvn clean package`).
2. Drop the generated JAR into your server's `plugins/` folder.
3. Restart the server.
4. (Optional) Edit `plugin.yml` or add custom permissions in your permissions plugin.

## Commands
| Command | Description |
|---------|-------------|
| `/souls` | Open the Soul GUI to view and use abilities. |
| `/souls add <num>` | Add souls (requires `souls.add` permission). |
| `/souls info` | Show current soul count. |
| `/souls use <ability> [duration]` | Activate an ability (e.g., `strength 30`). |
| `/ritual` | (Placeholder) Revive weakened players. |

## Permissions
| Permission | Description |
|------------|-------------|
| `soulboundsmp.*` | Full admin access. |
| `soulboundsmp.add` | Ability to add souls via command. |
| `soulboundsmp.use` | Use abilities via `/souls use`. |

## Configuration
The plugin creates a `souls.yml` file in the plugin directory automatically.
It stores each player’s soul count under their UUID key.