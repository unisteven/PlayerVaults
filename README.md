# PlayerVaults Plugin

## Description
PlayerVaults is a Minecraft plugin that allows players to have personal, secure vaults that they can access anywhere in the game. This plugin is perfect for servers looking for a way to provide players with private storage solutions.

## Features
- **Personal Vaults**: Players can store items in their own private vaults.
- **Admin Commands**: Server administrators can manage player vaults with a set of powerful commands.
- **Flexible Storage**: Vaults are saved securely either in flat files or in a MySQL database.
- **Customizable**: Easy configuration for setting up vault sizes, permissions, and more.

## Installation
1. Download the PlayerVaults.jar file.
2. Place the jar file in your server's `plugins` directory.
3. Restart the server.

## Commands
- `/playervault [page]` - Opens the specified page of your vault.
- `/playervault admin open <player>` - Opens a specified player's vault (Admin command).
- `/playervault admin clear <player> <page>` - Clears a specific page of a player's vault (Admin command).
- `/playervault admin reload` - Reloads the plugin configuration (Admin command).

## Permissions
- `playervaults.limit.[number]` - Allows a player to use their vault.
- `playervaults.admin.*` - Grants all admin permissions for PlayerVaults.

## Configuration
You can configure the plugin by editing the `config.yml` file in the `PlayerVaults` folder inside your `plugins` directory.

## Dependencies
- **Minecraft Server**: Compatible with Spigot, Paper, and Bukkit servers.
- **Java Version**: Requires Java 8 or newer.

## Building from Source
1. Clone the repository.
2. Navigate to the root directory and run `mvn clean package`.
3. The built jar file will be in the `target` directory.

## Contributing
Contributions to PlayerVaults are welcome! Feel free to fork the repository, make changes, and submit a pull request.

## License
This plugin is released under the [MIT License](LICENSE).

## Support
If you encounter any issues or have questions, please open an issue on the GitHub repository.

---

Thank you for using PlayerVaults!

