package me.huanmeng.guessthebuild.command;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.config.Config;
import me.huanmeng.guessthebuild.game.Region;
import me.huanmeng.guessthebuild.game.Theme;
import me.huanmeng.guessthebuild.game.ThemeDifficulty;
import me.huanmeng.guessthebuild.listener.SetupListener;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author huanmeng_qwq
 * 2020/9/23
 * GuesstheBuild
 */
public class SetupCommand implements CommandExecutor {
    private static List<Region> regions = new ArrayList<>();
    private static List<Theme> themes = new ArrayList<>();
    private static List<Location> locs = new ArrayList<>();

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!GuesstheBuild.getInstance().getGamefile().exists()) {
            try {
                GuesstheBuild.getInstance().getGamefile().createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            GuesstheBuild.setGameconfig(new Config(GuesstheBuild.getInstance().getGamefile()));
        }
        if(!sender.hasPermission("guessthebuild.setup")){
            m(sender,GuesstheBuild.config.getString("message.no_permission"));
            return true;
        }
        Config config = GuesstheBuild.getGameConfig();
        if (sender instanceof Player) {
            if (args.length == 0) {
                for (Object o : GuesstheBuild.config.getList("message.command.setup.help")) {
                    m(sender, String.format(o.toString(), label));
                }
                return true;
            }
            if (args.length == 1) {
                switch (args[0].toLowerCase()) {
                    case "setlobby":
                        config.setLocation("lobby", ((Player) sender).getLocation());
                        m(sender, GuesstheBuild.config.getString("message.command.setup.setlobby.success"));
                        break;
                    case "setmaxplayer":
                        config.set("max", 10);
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.setmaxplayer.success"), 10));
                        break;
                    case "addregion":
                        if (SetupListener.left == null || SetupListener.right == null) {
                            m(sender, GuesstheBuild.config.getString("message.command.setup.addregion.unknow"));
                            return true;
                        }
                        config.setLocation("regions." + (regions.size() + 1) + ".min", SetupListener.left);
                        config.setLocation("regions." + (regions.size() + 1) + ".max", SetupListener.right);
                        config.setLocation("regions." + (regions.size() + 1) + ".middle", ((Player) sender).getLocation());
                        regions.add(new Region(SetupListener.left, SetupListener.right, ((Player) sender).getLocation()));
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.addregion.success"), regions.size()));
                        break;
                    case "info":
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.info.region"), regions.size()));
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.info.theme"), themes.size()));
                        themes.forEach(t -> m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.info.themes"), t.getName())));
                    case "floor":
                        config.set("floor", ((Player) sender).getLocation().getBlockY());
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.floor.success"), ((Player) sender).getLocation().getBlockY()));
                    case "addnpc":
                        config.setLocation("npcs." + (locs.size() + 1) + ".loc", ((Player) sender).getLocation());
                        locs.add(((Player) sender).getLocation());
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.addnpc.success"), locs.size()));
                    default:
                        return true;
                }
            }
            if (args.length == 2) {
                switch (args[0].toLowerCase()) {
                    case "setmaxplayer":
                        try {
                            Integer.parseInt(args[1]);
                        } catch (NumberFormatException e) {
                            m(sender, "&cUnkNow Number!");
                            return true;
                        }
                        config.set("max", Integer.parseInt(args[1]));
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.setmaxplayer"), args[1]));
                        break;
                    case "floor":
                        try {
                            if (Integer.parseInt(args[1]) > 256) {
                                sendmsg(sender, "&a", "&cError: >256");
                                return true;
                            }
                            config.set("floor", Integer.parseInt(args[1]));
                        } catch (NumberFormatException e) {
                            sendmsg(sender, "&a", "&cUnknow Number");
                            return true;
                        }
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.floor.success"), args[1]));
                    default:
                        return true;
                }
            }
            if (args.length == 3) {
                switch (args[0].toLowerCase()) {
                    case "addtheme":
                        try {
                            config.set("themes." + (themes.size() + 1) + ".difficulty", args[1].toUpperCase());
                            config.set("themes." + (themes.size() + 1) + ".name", args[2]);
                            themes.add(new Theme(ThemeDifficulty.valueOf(args[1].toUpperCase()), args[2]));
                        } catch (IllegalArgumentException e) {
                            m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.addtheme.unknow"), args[1]));
                            return true;
                        }
                        m(sender, String.format(GuesstheBuild.config.getString("message.command.setup.addtheme.success"), args[1]));
                        break;
                }
            }
        }
        return true;
    }

    public void sendmsg(CommandSender sender, String prefix, String... message) {
        for (String s : message) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + s).replace("(prefix)",GuesstheBuild.config.getString("prefix")));
        }
    }

    public void m(CommandSender sender, String... m) {
        sendmsg(sender, "", m);
    }
}
