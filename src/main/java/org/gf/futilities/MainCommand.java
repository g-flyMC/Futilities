package org.gf.futilities;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.gf.futilities.commands.HammerCommand;
import org.gf.futilities.commands.ItemCommand;
import org.gf.futilities.commands.FarmCommand;
import org.gf.futilities.commands.BuilderCommand;

public class MainCommand implements CommandExecutor {

    private final ItemCommand itemCommand = new ItemCommand();
    private final FarmCommand farmCommand = new FarmCommand();
    private final BuilderCommand builderCommand = new BuilderCommand();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cSeuls les joueurs peuvent utiliser cette commande!");
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage("§c/fu <item|farm|bat> [args]");
            return true;
        }

        Player player = (Player) sender;
        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "item":
                if (!player.hasPermission("futilities.item")) {
                    player.sendMessage("§cVous n'avez pas la permission!");
                    return true;
                }
                return itemCommand.execute(player, args);

            case "farm":
                if (!player.hasPermission("futilities.farm")) {
                    player.sendMessage("§cVous n'avez pas la permission!");
                    return true;
                }
                return farmCommand.execute(player, args);

            case "bat":
                if (!player.hasPermission("futilities.bat")) {
                    player.sendMessage("§cVous n'avez pas la permission!");
                    return true;
                }
                return builderCommand.execute(player, args);

            case "hammer":
                if (!player.hasPermission("futilities.hammer")) {
                    player.sendMessage("§cVous n'avez pas la permission!");
                    return true;
                }
                return new HammerCommand().execute(player, args);

            default:
                player.sendMessage("§c/fu <item|farm|bat|hammer> [args]");
                return true;
        }
    }
}