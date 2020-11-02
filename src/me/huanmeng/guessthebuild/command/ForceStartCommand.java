package me.huanmeng.guessthebuild.command;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.game.GameStatus;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class ForceStartCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(sender.hasPermission("guessthebuild.forcestart")){
            if(Game.getInstance()==null){
                m(sender,GuesstheBuild.config.getString("message.command.forcestart.unknow"));
                return true;
            }
            if(Game.getInstance().getStatus()== GameStatus.WAIT){
                if(Game.getInstance().getOnlinePlayers().size()>=4) {
                    Game.getInstance().start();
                }else{
                    sender.sendMessage("§cMust >=4 Players");
                }
            }else{
                m(sender,GuesstheBuild.config.getString("message.command.forcestart.already"));
            }
        }
        return true;
    }
    public void sendmsg(CommandSender sender, String prefix, String... message) {
        for (String s : message) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix + s).replace("(prefix)", GuesstheBuild.config.getString("prefix")));
        }
    }

    public void m(CommandSender sender, String... m) {
        sendmsg(sender, "", m);
    }
}
