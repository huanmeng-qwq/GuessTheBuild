package me.huanmeng.guessthebuild.game;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.config.Config;
import me.huanmeng.guessthebuild.event.PlayerJoinGameEvent;
import me.huanmeng.guessthebuild.event.PlayerQuitGameEvent;
import me.huanmeng.guessthebuild.task.PlayerTask;
import me.huanmeng.guessthebuild.task.ScoreboardTask;
import me.huanmeng.guessthebuild.task.WaitTask;
import me.huanmeng.guessthebuild.utils.ItemBuilder;
import me.huanmeng.guessthebuild.utils.LuckPerms;
import me.huanmeng.guessthebuild.utils.TextBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.md_5.bungee.api.chat.ClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.scoreboard.Score;

import java.util.*;

@Getter
@Setter
public class Game {
    private List<GamePlayer> players;
    private Location loc;
    private final int max=10;
    private Config config;
    private List<Theme> themes;
    private WaitTask waitTask;
    private GameStatus status;
    @Getter
    private static Game instance;
    private List<Region> regions;
    private List<UUID> rejoin;
    private PlayerTask playerTask;
    private int flatY=1;
    @Getter
    private static List<NPC> npcs=new ArrayList<>();
    //3分钟等待
    public Game(Config config){
        players=new ArrayList<GamePlayer>(){
            @Override
            public boolean add(GamePlayer gamePlayer) {
                removeIf(ga->ga.getUuid()==gamePlayer.getUuid());
                return super.add(gamePlayer);
            }
        };
        this.config=config;
        this.loc=config.getLocation("lobby");
        instance=this;
        waitTask=new WaitTask(this);
        waitTask.runTaskTimer(GuesstheBuild.getInstance(),0,20);
        regions=new ArrayList<>();
        themes=new ArrayList<>();
        rejoin=new ArrayList<>();
        if(config.getConfig().contains("floor")) {
            flatY = config.getInt("floor");
        }
        status=GameStatus.WAIT;
        for (String regions : config.getConfig().getConfigurationSection("regions").getKeys(false)) {
            this.regions.add(new Region(config.getLocation("regions."+regions+".min"),config.getLocation("regions."+regions+".max"),config.getLocation("regions."+regions+".middle")));
        }
        for (String themes : config.getConfig().getConfigurationSection("themes").getKeys(false)) {
            this.themes.add(new Theme(ThemeDifficulty.valueOf(config.getString("themes."+themes+".difficulty").toUpperCase()),config.getString("themes."+themes+".name")));
        }
        regions.get(0).getMiddle().getWorld().setStorm(false);
        regions.get(0).getMiddle().getWorld().setThundering(false);
        for (Region region : regions) {
            if(!region.getMiddle().getChunk().isLoaded()){
                region.getMiddle().getChunk().load(true);
            }
        }
        for (String npcs : config.getConfig().getConfigurationSection("npcs").getKeys(false)) {
            NPC npc=CitizensAPI.getNPCRegistry().createNPC(EntityType.valueOf(GuesstheBuild.config.getString("message.npc.type").toUpperCase()),GuesstheBuild.config.getString("message.npc.name"));
            npc.spawn(config.getLocation("npcs."+npcs+".loc"));
            Game.npcs.add(npc);
        }
        new ScoreboardTask().runTaskTimer(GuesstheBuild.getInstance(),0,20);
    }
    @SneakyThrows
    public void addPlayer(GamePlayer gamePlayer) {
        if(players.size()>=getMax()){
            throw new BuildTheGuessException("Can't add more player, Max Player: "+getMax());
        }
        players.add(gamePlayer);
        Bukkit.getPluginManager().callEvent(new PlayerJoinGameEvent(gamePlayer.getPlayer(),gamePlayer,this));
        gamePlayer.getPlayer().teleport(loc);
        all(String.format(GuesstheBuild.config.getString("message.join"),(gamePlayer.getPlayer().getName().equalsIgnoreCase(gamePlayer.getName())?LuckPerms.getPrefix(gamePlayer.getPlayer())+gamePlayer.getName():gamePlayer.getName()),getPlayers().size(),getMax()));
        gamePlayer.getPlayer().getInventory().clear();
        gamePlayer.getPlayer().getInventory().setArmorContents(null);
        gamePlayer.getPlayer().getInventory().setItem(8,new ItemBuilder(Material.BED).setDisplayName(GuesstheBuild.config.getString("message.leave_bed.name")).build());
        if(gamePlayer.getPlayer().hasPermission("guessthebuild.forcestart")){
            gamePlayer.getPlayer().getInventory().setItem(7,new ItemBuilder(Material.DIAMOND).setDisplayName(GuesstheBuild.config.getString("message.forcestart.name")).build());
        }
        gamePlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
    }
    public void removePlayer(GamePlayer gamePlayer){
        players.remove(gamePlayer);
        Bukkit.getPluginManager().callEvent(new PlayerQuitGameEvent(gamePlayer.getPlayer(),gamePlayer,this));
        all(String.format(GuesstheBuild.config.getString("message.quit"),(gamePlayer.getPlayer().getName().equalsIgnoreCase(gamePlayer.getName())?LuckPerms.getPrefix(gamePlayer.getPlayer())+gamePlayer.getName():gamePlayer.getName())));
    }
    public void all(String... message){
        players.forEach(p->p.sendMessage(message));
    }
    public int getWaitTime(){
        return waitTask.getSecond();
    }
    public List<GamePlayer> getOnlinePlayers(){
        List<GamePlayer> g=new ArrayList<>();
        for (GamePlayer player : players) {
            if(Bukkit.getPlayer(player.getUuid())!=null&&player.getPlayer().isOnline())
                g.add(player);
        }
        return g;
    }
    public List<GamePlayer> getScoreTop(){
        List<GamePlayer> g=new ArrayList<>(players);
        Collections.sort(g, new Comparator<GamePlayer>() {
            @Override
            public int compare(GamePlayer o1, GamePlayer o2) {
                return o2.getScore()-o1.getScore();
            }
        });
        return g;
    }
    public void start(){
        waitTask.cancel();
        RoundManager.nextRound();
        status=GameStatus.GAMING;
        playerTask=new PlayerTask(this);
        playerTask.runTaskTimer(GuesstheBuild.getInstance(),0,60);
    }
    public void end(){
        status=GameStatus.FINISH;
        List<GamePlayer> sorted=new ArrayList<>(players);
        sorted.sort(new Comparator<GamePlayer>() {
            @Override
            public int compare(GamePlayer o1, GamePlayer o2) {
                return Integer.compare(o2.getScore(), o1.getScore());
            }
        });
        all("&a&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
        all(" ");
        all("                              "+GuesstheBuild.config.getString("message.name"));
        all(" ");
        all("                                 "+GuesstheBuild.config.getString("message.end.winner"));
        sorted.get(0).giveWin(1);
        all("                            "+String.format(GuesstheBuild.config.getString("message.end.one"),sorted.get(0).getScore()));
        all("                                "+sorted.get(0).getName());
        all("                            "+String.format(GuesstheBuild.config.getString("message.end.two"),sorted.get(1).getScore()));
        all("                                "+sorted.get(1).getName());
        all("                            "+String.format(GuesstheBuild.config.getString("message.end.three"),sorted.get(2).getScore()));
        all("                                "+sorted.get(2).getName());
        all("          ");
        for (GamePlayer onlinePlayer : getOnlinePlayers()) {
            onlinePlayer.sendMessage("                             "+String.format(GuesstheBuild.config.getString("message.end.you_score"),onlinePlayer.getScore()));
        }
        all(" ");
        all("&a&m▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
//        for (GamePlayer onlinePlayer : getOnlinePlayers()) {
//            onlinePlayer.getPlayer().spigot().sendMessage(new TextBuilder("&e想再来一局吗？ &b点击这里！").addClick(ClickEvent.Action.RUN_COMMAND,"/fastjoin").build());
//        }
        for (NPC npc : npcs) {
            npc.despawn();
            npc.destroy();
        }
        Bukkit.getScheduler().runTaskTimer(GuesstheBuild.getInstance(), ()-> {
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                GuesstheBuild.getInstance().tpToLobby(onlinePlayer);
            }
        },20*8,20);
        Bukkit.getScheduler().runTaskLater(GuesstheBuild.getInstance(), Bukkit::shutdown,20*10);
    }
    public void refresh(){
        playerTask.run();
    }

    public GamePlayer getBuilder() {
        return RoundManager.getRound().getBuilder()==null?null:RoundManager.getRound().getBuilder();
    }
}
