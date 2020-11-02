package me.huanmeng.guessthebuild.task;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.game.GamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class PlayerTask extends BukkitRunnable {
    private Game game;

    public PlayerTask(Game game) {
        this.game = game;
    }

    @Override
    public void run() {
        for (GamePlayer gamePlayer /*自己*/: game.getOnlinePlayers()) {
            for (Player player/*别人*/ : Bukkit.getOnlinePlayers()) {
//                gamePlayer.getPlayer().showPlayer(player);
                if(gamePlayer.getUuid()==player.getUniqueId())continue;//是自己
                GamePlayer target=GamePlayer.get(player.getUniqueId());
                if(target==null)continue;
                if(target.getUuid()!=game.getBuilder().getUuid()){
                    gamePlayer.getPlayer().hidePlayer(player);
                }else{
                    gamePlayer.getPlayer().showPlayer(player);
                }
            }
        }
    }
}
