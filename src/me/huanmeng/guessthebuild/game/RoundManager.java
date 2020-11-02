package me.huanmeng.guessthebuild.game;

import me.huanmeng.guessthebuild.GuesstheBuild;
import org.bukkit.GameMode;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class RoundManager {
    private static List<GamePlayer> alreadyBuilder=new ArrayList<>();
    private static List<Region> alreadyRegion=new ArrayList<>();
    private static List<Theme> alreadyTheme=new ArrayList<>();
    private static Round round;
    private static Game game=Game.getInstance();
    private static int roundAmount;
    public static void nextRound(){
        if(roundAmount>=game.getOnlinePlayers().size()){
            Game.getInstance().end();
            return;
        }
//        if(getFreeRegion().isEmpty()||getFreeRegion().size()==0||getFreeTheme().isEmpty()||getFreeTheme().size()<3||getFreePlayers().size()==0|| getFreePlayers().isEmpty()){
//            return;
//        }
        if(round!=null){
            alreadyTheme.add(round.getSelectTheme());
            alreadyBuilder.add(round.getBuilder());
            alreadyRegion.add(round.getRegion());
            round.setRun(false);
            round.cancel();
        }
        for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
            onlinePlayer.getPlayer().getInventory().clear();
            for (PotionEffect activePotionEffect : onlinePlayer.getPlayer().getActivePotionEffects()) {
                onlinePlayer.getPlayer().removePotionEffect(activePotionEffect.getType());
            }
            onlinePlayer.getPlayer().getInventory().setArmorContents(null);
            onlinePlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
            onlinePlayer.getPlayer().setAllowFlight(true);
            onlinePlayer.getPlayer().setFlying(true);
            onlinePlayer.setTheme(false);
        }
        ++roundAmount;
        round=new Round(getFreePlayers().get(0),
                new ArrayList<Theme>(){{
                    add(getFreeTheme().get(0));
                    add(getFreeTheme().get(1));
                    add(getFreeTheme().get(2));}},
                getFreeRegion().get(0));
        round.runTaskTimer(GuesstheBuild.getInstance(),0,20);
    }
    public static List<GamePlayer> getFreePlayers(){
        List<GamePlayer> a=new ArrayList<>();
        for (GamePlayer gamePlayer : game.getOnlinePlayers()) {
            if(!alreadyBuilder.contains(gamePlayer)){
                a.add(gamePlayer);
            }
        }
        Collections.shuffle(a);
        return a;
    }
    public static List<Region> getFreeRegion(){
        List<Region> a=new ArrayList<>();
        for (Region region : game.getRegions()) {
            if(!alreadyRegion.contains(region)){
                a.add(region);
            }
        }
        Collections.shuffle(a);
        return a;
    }
    public static List<Theme> getFreeTheme(){
        List<Theme> a=new ArrayList<>();
        for (Theme theme : game.getThemes()) {
            if(!alreadyTheme.contains(theme)){
                a.add(theme);
            }
        }
        Collections.shuffle(a);
        return a;
    }

    public static Round getRound() {
        return round;
    }

    public static int getRoundAmount() {
        return roundAmount;
    }
}
