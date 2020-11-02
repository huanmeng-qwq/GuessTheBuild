package me.huanmeng.guessthebuild.task;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.game.GamePlayer;
import me.huanmeng.guessthebuild.utils.PlayerUtil;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author huanmeng_qwq
 * 2020/9/22
 * GuesstheBuild
 */
public class WaitTask extends BukkitRunnable {
    private boolean d=false;
    private static final List<Integer> titletime = new ArrayList<>();
    private int second=-1;
    private Game game;
    public WaitTask(Game game){
        this.game=game;
        titletime.add(30);
        titletime.add(10);
        titletime.add(5);
        titletime.add(4);
        titletime.add(3);
        titletime.add(2);
        titletime.add(1);
    }

    @Override
    public void run() {
        int m=game.getPlayers().size();
        if(!d){
            if(!game.getPlayers().isEmpty()&&second==-1) {
                second = 60 * 3;
            }
        }
        d= m >= 4;
        if(d&&second==0){
            cancel();
            game.start();
        }
        if(m>=game.getMax()&&second>=10){
            second=10;
        }
        if (titletime.contains(second)) {
            PlayerUtil.sendAllTitle("&c" + second, "");
            for (GamePlayer player : game.getOnlinePlayers()) {
                player.getPlayer().playSound(player.getPlayer().getLocation(), Sound.CLICK, 1, 1);
            }
            game.all(String.format(GuesstheBuild.config.getString("message.lobby.startgamein"),second));
        }
        if(d){
            --second;
        }
    }

    public int getSecond() {
        return second;
    }
}
