package me.huanmeng.guessthebuild.event;

import lombok.Getter;
import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.game.GamePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@Getter
public class PlayerJoinGameEvent extends Event {
    private Player player;
    private GamePlayer gamePlayer;
    private Game game;

    public PlayerJoinGameEvent(Player player, GamePlayer gamePlayer, Game game) {
        this.player = player;
        this.gamePlayer = gamePlayer;
        this.game = game;
    }

    private static HandlerList handlerList=new HandlerList();
    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }
}
