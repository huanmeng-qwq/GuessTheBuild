package me.huanmeng.guessthebuild.listener;

import me.huanmeng.guessthebuild.game.Game;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/10/1<br>
 * GuesstheBuild
 */
public class ServerListener implements Listener {
    @EventHandler
    public void onPing(ServerListPingEvent e){
        switch (Game.getInstance().getStatus()) {

            case WAIT:
                e.setMotd("Waiting");
                break;
            case GAMING:
                e.setMotd("Gaming");
                break;
            case FINISH:
                e.setMotd("Stoping");
                break;
        }
    }
}
