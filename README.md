# Hyssentials

Essential server commands for Hytale.

## Commands

### Teleport Requests

*   `/tpa <player>` - Request to teleport to a player
*   `/tpahere <player>` - Request a player to teleport to you
*   `/tpaccept` - Accept a teleport request
*   `/tpdeny` - Deny a teleport request
*   `/tpcancel` - Cancel your outgoing request

### Homes

*   `/sethome <name>` - Set a home
*   `/home <name>` - Teleport to a home
*   `/delhome <name>` - Delete a home
*   `/homes` - List your homes

### Warps

*   `/setwarp <name>` - Create a server warp (admin)
*   `/warp <name>` - Teleport to a warp
*   `/delwarp <name>` - Delete a warp (admin)
*   `/warps` - List all warps

### Spawn

*   `/spawn` - Teleport to spawn
*   `/setspawn` - Set custom spawn point (admin)

### Utilities

*   `/back` - Return to your previous location (works after death)
*   `/htp <player>` - Teleport to a player (admin)
*   `/htphere <player>` - Teleport a player to you (admin)

## Configuration

Config file: `config.json`

```
{
  "ConfigVersion": 3,
  "MaxHomes": 5,
  "TpaTimeoutSeconds": 60,
  "TpaCooldownSeconds": 30,
  "TeleportDelaySeconds": 3,
  "BackHistorySize": 5,
  "HomeCooldownSeconds": 60,
  "WarpCooldownSeconds": 60,
  "SpawnCooldownSeconds": 60,
  "BackCooldownSeconds": 60,
  "VipMaxHomes": 10,
  "VipHomeCooldownSeconds": 0,
  "VipWarpCooldownSeconds": 0,
  "VipSpawnCooldownSeconds": 0,
  "VipBackCooldownSeconds": 0
}
```

| Option                  | Description                          | Default |
| ----------------------- | ------------------------------------ | ------- |
| MaxHomes                | Maximum homes per player             | 5       |
| TpaTimeoutSeconds       | Seconds before TPA request expires   | 60      |
| TpaCooldownSeconds      | Seconds between TPA requests         | 30      |
| TeleportDelaySeconds    | Delay before teleport (unused)       | 3       |
| BackHistorySize         | Number of locations to remember      | 5       |
| HomeCooldownSeconds     | Seconds between /home uses           | 60      |
| WarpCooldownSeconds     | Seconds between /warp uses           | 60      |
| SpawnCooldownSeconds    | Seconds between /spawn uses          | 60      |
| BackCooldownSeconds     | Seconds between /back uses           | 60      |
| VipMaxHomes             | Maximum homes for VIP players        | 10      |
| VipHomeCooldownSeconds  | /home cooldown for VIP (0=none)      | 0       |
| VipWarpCooldownSeconds  | /warp cooldown for VIP (0=none)      | 0       |
| VipSpawnCooldownSeconds | /spawn cooldown for VIP (0=none)     | 0       |
| VipBackCooldownSeconds  | /back cooldown for VIP (0=none)      | 0       |

## Data Storage

All data is stored in JSON files in the plugin data folder:

*   `homes.json` - Player homes
*   `warps.json` - Server warps
*   `spawn.json` - Custom spawn point

## Permissions

### Admin Permissions

Admin commands require operator status or the following permissions:

*   `hyssentials.setspawn` - Set server spawn
*   `hyssentials.setwarp` - Create warps
*   `hyssentials.delwarp` - Delete warps
*   `hyssentials.htp` - Teleport to players
*   `hyssentials.htphere` - Teleport players to you

### VIP Permissions

Grant players VIP benefits like more homes and reduced cooldowns:

*   `hyssentials.vip` - Full VIP benefits (homes + cooldowns)
*   `hyssentials.vip.homes` - Extended homes limit only
*   `hyssentials.vip.cooldown` - Reduced cooldowns only
*   `hyssentials.cooldown.bypass` - Completely bypass all cooldowns

Support on [Discord](https://discord.gg/m4EHeRjfZ9)

If you'd like to support me and the development of my mods, I recommend trying out BisectHosting. Use code "project8gbderam" to get 25% off your first month of a gaming server for new customers. They offer 24/7 support and lightning-fast response times, ensuring you're in great hands and providing exceptional assistance for all your gaming needs. [https://www.bisecthosting.com/project8gbderam](https://www.curseforge.com/linkout?remoteUrl=https://www.bisecthosting.com/project8gbderam)

![Bisect](https://www.bisecthosting.com/partners/custom-banners/54bb107c-f9fc-4f32-8515-fb4e3d56c124.png)