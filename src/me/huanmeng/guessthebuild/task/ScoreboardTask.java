package me.huanmeng.guessthebuild.task;


import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.*;
import me.huanmeng.guessthebuild.utils.LuckPerms;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

public class ScoreboardTask extends BukkitRunnable {
    public static final SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
    @Override
    public void run() {
        Game game = Game.getInstance();

        if (game != null) {
            if (game.getStatus() == GameStatus.WAIT) {
                for (GamePlayer gamePlayer : game.getOnlinePlayers()) {
                    _displayWaiting(gamePlayer, game);
                }
            } else if (game.getStatus() == GameStatus.GAMING) {
                for (GamePlayer gamePlayer : game.getOnlinePlayers()) {
                    _displayGaming(gamePlayer, game);
                }
            }else if(game.getStatus()==GameStatus.FINISH){
                for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
                    displayFinishing(onlinePlayer,game);
                }
            }
        }
    }
    private void displayFinishing(GamePlayer gamePlayer,Game game){
        List<String> strings = new ArrayList<String>(){
            @Override
            public boolean add(String s) {
                return super.add(ChatColor.translateAlternateColorCodes('&',s));
            }
        };
        List<GamePlayer> sorted=new ArrayList<>(game.getPlayers());
        sorted.sort(new Comparator<GamePlayer>() {
            @Override
            public int compare(GamePlayer o1, GamePlayer o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });
        strings.add(GuesstheBuild.config.getString("scoreboard.title"));
        strings.add("&7"+dateFormat.format(new Date()));
        strings.add("&a");
        for (int i = 0; i < sorted.size(); i++) {
            strings.add("&e"+(i+1)+". "+ (Bukkit.getPlayer(sorted.get(i).getUuid())!=null?LuckPerms.getRank(sorted.get(i).getPlayer())+sorted.get(i).getName()+"&f: &a"+sorted.get(i).getScore():"&7"+sorted.get(i).getName()+"&f: &a"+sorted.get(i).getScore()));
        }
        strings.add("&c");
        strings.add(GuesstheBuild.config.getString("scoreboard.endLine"));
        ScoreboardUtil.unrankedSidebarDisplay(gamePlayer.getPlayer(), strings.toArray(new String[strings.size()]));
    }

    private void _displayWaiting(GamePlayer gamePlayer, Game game) {
        List<String> strings = new ArrayList<String>(){
            @Override
            public boolean add(String s) {
                return super.add(ChatColor.translateAlternateColorCodes('&',s));
            }
        };
        strings.addAll(Arrays.asList(
                GuesstheBuild.config.getString("scoreboard.title"),
                "§7"+dateFormat.format(new Date()),
                ChatColor.BLACK + "§1",
                String.format(GuesstheBuild.config.getString("scoreboard.wait.player"),game.getOnlinePlayers().size(),game.getMax()),
                ChatColor.DARK_GRAY + " "
        ));
        if (game.getPlayers().size() >= 3) {
            String s1="";
            String s2="";
            String s3="";
            s1=String.format(GuesstheBuild.config.getString("scoreboard.wait.start"),getFormattedTime(game.getWaitTime()));
            s2=GuesstheBuild.config.getString("scoreboard.wait.start1");
            s3=GuesstheBuild.config.getString("scoreboard.wait.start2");
            if(!s1.isEmpty()){
                strings.add(s1);
            }
            if(!s2.isEmpty()){
                strings.add(s2);
            }
            if(!s3.isEmpty()){
                strings.add(s3);
            }
        } else {
            strings.add(String.format(GuesstheBuild.config.getString("scoreboard.wait.countdown"),getFormattedTime(game.getWaitTime())));
            strings.add(String.format(GuesstheBuild.config.getString("scoreboard.wait.mustPlayer"),(4 - game.getPlayers().size())));
        }
        strings.add("§f§2");
        strings.add(GuesstheBuild.config.getString("scoreboard.endLine"));

        ScoreboardUtil.unrankedSidebarDisplay(gamePlayer.getPlayer(), strings.toArray(new String[strings.size()]));
    }

    private void _displayGaming(GamePlayer gamePlayer, Game game) {
        List<String> strings = new ArrayList<String>(){
            @Override
            public boolean add(String s) {
                return super.add(ChatColor.translateAlternateColorCodes('&',s));
            }
        };
        strings.addAll(Arrays.asList(
                GuesstheBuild.config.getString("scoreboard.title"),
                "§7"+dateFormat.format(new Date())
        ));
        strings.add(GuesstheBuild.config.getString("scoreboard.builder"));
        strings.add(" " + (game.getBuilder().getPlayer().getName().equalsIgnoreCase(game.getBuilder().getName()) ? LuckPerms.getPrefix(game.getBuilder().getPlayer()) + game.getBuilder().getName() : game.getBuilder().getName()));
        strings.add("§a");
//        strings.add(game.getScoreTop().get(0));
        strings.add((game.getScoreTop().get(0).getPlayer().getName().equalsIgnoreCase(game.getScoreTop().get(0).getName()) ? LuckPerms.getPrefix(game.getScoreTop().get(0).getPlayer()) + game.getScoreTop().get(0).getName() : game.getScoreTop().get(0).getName()) + "&f: &a" + game.getScoreTop().get(0).getScore());
        strings.add((game.getScoreTop().get(1).getPlayer().getName().equalsIgnoreCase(game.getScoreTop().get(1).getName()) ? LuckPerms.getPrefix(game.getScoreTop().get(1).getPlayer()) + game.getScoreTop().get(1).getName() : game.getScoreTop().get(1).getName()) + "&f: &a" + game.getScoreTop().get(1).getScore());
        strings.add((game.getScoreTop().get(2).getPlayer().getName().equalsIgnoreCase(game.getScoreTop().get(2).getName()) ? LuckPerms.getPrefix(game.getScoreTop().get(2).getPlayer()) + game.getScoreTop().get(2).getName() : game.getScoreTop().get(2).getName()) + "&f: &a" + game.getScoreTop().get(2).getScore());
        if (game.getPlayers().size() > 3) {
            strings.add("§f...");
            strings.add((gamePlayer.getPlayer().getName().equalsIgnoreCase(gamePlayer.getName()) ? LuckPerms.getPrefix(gamePlayer.getPlayer()) + gamePlayer.getName() : gamePlayer.getName()));
        }
        strings.add((RoundManager.getRound().getRoundStatus()== RoundStatus.SELECT ? String.format(GuesstheBuild.config.getString("scoreboard.startIn"), getFormattedTime(RoundManager.getRound().getSelectTime())) : String.format(GuesstheBuild.config.getString("scoreboard.time"),getFormattedTime(RoundManager.getRound().getSecond()))));
        strings.add("§f§f");
        strings.add(GuesstheBuild.config.getString("scoreboard.theme"));
        if (gamePlayer.isTheme()) {
            strings.add(" "+(RoundManager.getRound().getSelectTheme()==null?"&a"+GuesstheBuild.config.getString("scoreboard.select"):"&b"+(gamePlayer.getPlayer().hasPermission("gtb.hide")?"&kasasa":RoundManager.getRound().getSelectTheme().name)));
        } else {
//            strings.add(" " + (!gamePlayer.isTheme() || RoundManager.getRound().getSelectTheme() == null ? "§d???" : game.getRoundTask().getTheme().getName()));
            if(RoundManager.getRound().getSecond()>=90){
                strings.add(" §c???");
            }
            if(RoundManager.getRound().getSecond()<90){
                strings.add(" §a"+RoundManager.getRound().getPrompt());
            }
        }
        strings.add(GuesstheBuild.config.getString("scoreboard.endLine"));
        ScoreboardUtil.unrankedSidebarDisplay(gamePlayer.getPlayer(), strings.toArray(new String[strings.size()]));
    }

    public static String getFormattedTime(int time) {
        int min = (int) Math.floor(time / 60);
        int sec = time % 60;
        String minStr = min < 10 ? "0" + min : String.valueOf(min);
        String secStr = sec < 10 ? "0" + sec : String.valueOf(sec);
        return minStr + ":" + secStr;
    }
}
