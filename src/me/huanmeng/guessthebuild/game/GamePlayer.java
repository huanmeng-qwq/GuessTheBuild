package me.huanmeng.guessthebuild.game;

import lombok.Getter;
import lombok.Setter;
import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.SQL;
import me.huanmeng.guessthebuild.database.DataBase;
import me.huanmeng.guessthebuild.database.KeyValue;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Getter
@Setter
public class GamePlayer {
    private static List<GamePlayer> players=new ArrayList<>();
    private PlayerStatus status;
    private Player player;
    private UUID uuid;
    private String name;
    private String prefix;
    private int score;
    private int sqlScore;
    private boolean theme=false;
    private int win;
    private DataBase database= GuesstheBuild.getDataBase();
    public GamePlayer(UUID uuid){
        this.uuid=uuid;
        this.player= Bukkit.getPlayer(uuid);
        this.status=PlayerStatus.GUESS;
        this.name=player.getName();
        prefix=GuesstheBuild.config.getString("rank.0");
        score=0;
        players.add(this);
        init();
    }

    public void init(){
        if (database.isValueExists(SQL.TABLE_DATA, SQL.KV_DATA, new KeyValue("uuid", getUuid().toString()))) {
            this.sqlScore = Integer.parseInt(database.dbSelectFirst(SQL.TABLE_DATA, "score", new KeyValue("uuid", uuid.toString())));
            this.win = Integer.parseInt(database.dbSelectFirst(SQL.TABLE_DATA, "win", new KeyValue("uuid", uuid.toString())));
        } else {
            win=0;
            score = 0;
            database.dbInsert(SQL.TABLE_DATA, new KeyValue("uuid", uuid.toString()).add("win", 0).add("score", 0));
        }
        if(sqlScore>=0){
            prefix=GuesstheBuild.config.getString("rank.0");
        }
        if(sqlScore>=100){
            prefix=GuesstheBuild.config.getString("rank.1");
        }
        if(sqlScore>=250){
            prefix=GuesstheBuild.config.getString("rank.2");
        }
        if(sqlScore>=500){
            prefix=GuesstheBuild.config.getString("rank.3");
        }
        if(sqlScore>=1000){
            prefix=GuesstheBuild.config.getString("rank.4");
        }
        if(sqlScore>=2000){
            prefix=GuesstheBuild.config.getString("rank.5");
        }
        if(sqlScore>=3500){
            prefix=GuesstheBuild.config.getString("rank.6");
        }
        if(sqlScore>=5000){
            prefix=GuesstheBuild.config.getString("rank.7");
        }
        if(sqlScore>=7500){
            prefix=GuesstheBuild.config.getString("rank.8");
        }
        if(sqlScore>=15000){
            prefix=GuesstheBuild.config.getString("rank.9");
        }
        if(sqlScore>=20000){
            prefix=GuesstheBuild.config.getString("rank.10");
        }

    }

    public void sendMessage(String... message){
        if(Bukkit.getPlayer(uuid)==null)return;
        setPlayer(Bukkit.getPlayer(uuid));
        for (String s : message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',s));
        }
    }
    public static GamePlayer get(UUID uuid){
//        return players.stream().filter(g->g.getUuid()==uuid).collect(Collectors.toList()).get(0);
        for (GamePlayer player : players) {
            if(player.getUuid()==uuid){
                return player;
            }
        }
        return null;
    }
    public void giveScore(int amount){
        score+=amount;
        sqlScore+=amount+10;
        database.dbUpdate(SQL.TABLE_DATA, new KeyValue("score", this.sqlScore), new KeyValue("uuid", this.getUuid().toString()));
    }
    public void giveWin(int amount){
        win+=amount;
        database.dbUpdate(SQL.TABLE_DATA, new KeyValue("win", this.win), new KeyValue("uuid", this.getUuid().toString()));
    }
}
