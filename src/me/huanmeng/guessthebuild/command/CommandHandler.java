package me.huanmeng.guessthebuild.command;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class CommandHandler implements CommandExecutor, TabCompleter {
    private List<BaseCommand> commands = new ArrayList();
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd mm:ss");
    public static final Config config=GuesstheBuild.config;

    public CommandHandler() {
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Iterator var5 = this.commands.iterator();

        while (true) {
            BaseCommand command;
            do {
                if (!var5.hasNext()) {
                    return false;
                }

                command = (BaseCommand) var5.next();
            } while (!command.isValidTrigger(cmd.getName()));

            if (!command.hasPermission(sender)) {
                sender.sendMessage(config.getString("message.no_permission"));
                return true;
            }

            if (command.isOnlyPlayerExecutable() && !(sender instanceof Player)) {
                sender.sendMessage(config.getString("message.only_player"));
                return true;
            }
            if (args.length >= command.getMinimumArguments()) {
                try {
                    command.execute(sender, label, args);
                    return true;
                } catch (CommandException var8) {
                }
            } else {
                sender.sendMessage(String.format(config.getString("message.Bad_parameter"),command.getName(),command.getPossibleArguments()));
            }
        }

    }

    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return null;
    }

    public void registerCommand(BaseCommand command) {
        this.commands.add(command);
        GuesstheBuild.getInstance().getCommand(command.getName()).setExecutor(this);
        GuesstheBuild.getInstance().getCommand(command.getName()).setTabCompleter(this);
    }
}
