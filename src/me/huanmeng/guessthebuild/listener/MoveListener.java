package me.huanmeng.guessthebuild.listener;

import me.huanmeng.guessthebuild.game.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class MoveListener implements Listener {
    private long last=0;
    @EventHandler
    public void onMove(PlayerMoveEvent e){
        Game game= Game.getInstance();
        if(game.getStatus()== GameStatus.GAMING){
            GamePlayer gamePlayer=GamePlayer.get(e.getPlayer().getUniqueId());
            if(gamePlayer!=null){
                Cube cube=new Cube(RoundManager.getRound().getRegion().getMin().clone().add(2,0,2),RoundManager.getRound().getRegion().getMax().clone().add(2,0,2));
                if(!cube.isLocationInCube(e.getTo())&&System.currentTimeMillis()>=last+200){
                    e.getPlayer().sendMessage("§c你不能到领地外面去！");
                    e.getPlayer().teleport(RoundManager.getRound().getRegion().getMiddle());
                    last=System.currentTimeMillis();
                }
            }
        }
    }
}
