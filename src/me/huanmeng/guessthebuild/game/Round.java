package me.huanmeng.guessthebuild.game;

import lombok.Getter;
import lombok.Setter;
import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.inventory.InventoryManager;
import me.huanmeng.guessthebuild.inventory.SelectTheme;
import me.huanmeng.guessthebuild.utils.ItemBuilder;
import me.huanmeng.guessthebuild.utils.LuckPerms;
import me.huanmeng.guessthebuild.utils.PacketWorldChunk;
import me.huanmeng.guessthebuild.utils.PlayerUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
@Getter
@Setter
public class Round extends BukkitRunnable {
    private GamePlayer builder;
    private List<Theme> theme;
    private Region region;
    private RoundStatus roundStatus;
    private boolean run = true;
    private int second = 120;
    private Theme selectTheme;
    private Game game;
    private int selectTime = 10;
    private int endTime = 3;
    private int Guess = 0;
    private String prompt;
    private Random random = new Random();
    private WeatherType weatherType;
    private Biome biome;
    private long time;
    private ItemStack type = new ItemStack(Material.STAINED_CLAY);
    private List<Location> locs;

    public Round(GamePlayer builder, List<Theme> theme, Region region) {
        this.builder = builder;
        this.theme = theme;
        this.region = region;
        roundStatus = RoundStatus.SELECT;
        game = Game.getInstance();
        for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
            onlinePlayer.getPlayer().setAllowFlight(true);
            onlinePlayer.getPlayer().setFlying(true);
            onlinePlayer.getPlayer().teleport(region.getMiddle());
            for (PotionEffect activePotionEffect : onlinePlayer.getPlayer().getActivePotionEffects()) {
                onlinePlayer.getPlayer().removePotionEffect(activePotionEffect.getType());
            }
        }
        game.all(String.format(GuesstheBuild.config.getString("message.round.builder"),builder.getName()));
        locs=new ArrayList<>();
        builder.getPlayer().setGameMode(GameMode.CREATIVE);
        Cube cube = new Cube(region.getMin(), region.getMax());
        for (Block block : cube.getBlocks()) {
            if (!locs.contains(block.getLocation())) {
                locs.add(block.getLocation());
            }
        }
        biome = Biome.PLAINS;
        time = 6000;
        builder.setTheme(true);
        World world = region.getMiddle().getWorld();
        world.setThunderDuration(0);
        world.setWeatherDuration(0);
        world.setThundering(false);
        world.setStorm(false);
    }

    @Override
    public void run() {
        if (!run) {
            return;
        }
        builder.setTheme(true);
        if (weatherType == WeatherType.SNOW)
            biome = Biome.ICE_PLAINS;
        World world = region.getMiddle().getWorld();
        world.setTime(time);
        for (Location loc : locs) {
            if (loc.getBlock().getBiome() != biome) {
                loc.getBlock().setBiome(biome);
                for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                    new PacketWorldChunk(loc.getChunk()).send(onlinePlayer);
                }
            }
        }
        if (weatherType != null) {
            switch (weatherType) {
                case SNOW:
                    for (Location loc : locs) {
                        if (loc.getBlock().getBiome() != biome) {
                            loc.getBlock().setBiome(biome);
                            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                                new PacketWorldChunk(loc.getChunk()).send(onlinePlayer);
                            }
                        }
                    }
                    world.setWeatherDuration(20 * second);
                    world.setThunderDuration(0);
                    world.setStorm(true);
                    world.setThundering(false);
                    if (biome != Biome.ICE_PLAINS) {
                        biome = Biome.ICE_PLAINS;
                    }
                    break;
                case THUNDER:
                    world.setWeatherDuration(20 * second);
                    world.setThunderDuration(20 * second);
                    world.setStorm(true);
                    world.setThundering(true);
                    break;
                case CLEAR:
                    world.setWeatherDuration(0);
                    world.setThunderDuration(0);
                    world.setStorm(false);
                    world.setThundering(false);
                    break;
                case DOWNFALL:
                    world.setWeatherDuration(20 * second);
                    world.setThunderDuration(0);
                    world.setStorm(true);
                    world.setThundering(false);
                    break;
            }
        }
        if (roundStatus == RoundStatus.SELECT) {
            if (selectTime == 10) {
                InventoryManager.SELECTTHEME.open(getBuilder().getPlayer());
            }
            if (selectTime == 0) {
                setSelectTheme(theme.get(new Random().nextInt(3)));
                builder.getPlayer().closeInventory();
                return;
            }
            for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
                PlayerUtil.sendTitle(onlinePlayer.getPlayer(), " ", GuesstheBuild.config.getString("message.round.title.Selecting"), 0, 19, 1);
            }
            --selectTime;
        }
        if (roundStatus == RoundStatus.BUILD) {
            if (second == 0) {
                PlayerUtil.sendAllTitle(GuesstheBuild.config.getString("message.round.title.show"), GuesstheBuild.config.getString("message.round.title.show_sub"), 0, 19, 1);
                roundStatus = RoundStatus.FINISH;
            }
            switch (second) {
                case 45:
                    newPrompt();
                case 30:
                case 15:
                    newPrompt();
                    PlayerUtil.sendAllTitle("", "&a"+String.format(GuesstheBuild.config.getString("message.round.title.surplus"),second), 0, 19, 1);
                    break;
                case 10:
                case 9:
                case 8:
                case 7:
                case 6:
                    PlayerUtil.sendAllTitle("", "&e"+String.format(GuesstheBuild.config.getString("message.round.title.surplus"),second), 0, 19, 1);
                    break;
                case 5:
                case 4:
                case 3:
                case 2:
                case 1:
                    PlayerUtil.sendAllTitle("", "&c"+String.format(GuesstheBuild.config.getString("message.round.title.surplus"),second), 0, 19, 1);
                    break;
                default:
                    break;
            }
            for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
                onlinePlayer.getPlayer().setExp(onlinePlayer.getPlayer().getTotalExperience());
                onlinePlayer.getPlayer().setLevel(second);
            }
            if (!builder.getPlayer().isOnline()) {
                roundStatus = RoundStatus.FINISH;
                game.all(GuesstheBuild.config.getString("message.round.title.offline"));
                second = 0;
                return;
            }
            for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
                if (onlinePlayer.isTheme()) {
                    PlayerUtil.sendActionbar(onlinePlayer.getPlayer(), String.format(GuesstheBuild.config.getString("message.round.title.actionBar"),selectTheme.name));
                } else {
                    if (second >= 90) {
                        PlayerUtil.sendActionbar(onlinePlayer.getPlayer(), String.format(GuesstheBuild.config.getString("message.round.title.actionBar"),"???"));
                    } else {
                        PlayerUtil.sendActionbar(onlinePlayer.getPlayer(), String.format(GuesstheBuild.config.getString("message.round.title.actionBar"),prompt));
                    }
                }
            }
            --second;
        }
        if (roundStatus == RoundStatus.FINISH) {
            if (endTime == 0) {
                cancel();
                RoundManager.nextRound();
                return;
            }
            if (endTime == 3) {
                AllThemeInfo();
            }
            --endTime;
        }

    }

    public void addGuess() {
        ++Guess;
        if (Guess == getGame().getOnlinePlayers().size() - 5 && second >= 18) {
            second = 15;
        }
        if (Guess == getGame().getOnlinePlayers().size() - 4 && second >= 15) {
            second = 10;
        }
        if (Guess == getGame().getOnlinePlayers().size() - 3 && second >= 8) {
            second = 8;
        }
        if (Guess == getGame().getOnlinePlayers().size() - 1 && second >= 1) {
            second = 1;
        }
    }

    public void setSelectTheme(Theme selectTheme) {
        this.selectTheme = selectTheme;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < selectTheme.name.length(); i++) {
            sb.append("_");
        }
        prompt = sb.toString();
        roundStatus = RoundStatus.BUILD;
        for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
            PlayerUtil.sendTitle(onlinePlayer.getPlayer(), " ", GuesstheBuild.config.getString("message.round.title.start"), 0, 19, 1);
            onlinePlayer.getPlayer().playSound(onlinePlayer.getPlayer().getLocation(), Sound.NOTE_PLING, 1, 1);
        }
        game.all(String.format(GuesstheBuild.config.getString("message.round.rounds"),RoundManager.getRoundAmount(),game.getMax()));
        Bukkit.getScheduler().runTaskLater(GuesstheBuild.getInstance(), () -> game.all(String.format(GuesstheBuild.config.getString("message.round.title.theme_length"),getSelectTheme().name.length())), 20 * 30);
        builder.getPlayer().getInventory().setItem(8, new ItemBuilder(Material.NETHER_STAR).setDisplayName(GuesstheBuild.config.getString("message.item.name")).build());
    }

    public void AllThemeInfo() {
        game.all(String.format(GuesstheBuild.config.getString("message.round.info"),getSelectTheme().getName()));
        for (GamePlayer onlinePlayer : game.getOnlinePlayers()) {
            PlayerUtil.sendTitle(onlinePlayer.getPlayer(), "", String.format(GuesstheBuild.config.getString("message.round.title.theme"),getSelectTheme().getName()));
        }
    }

    public String getPrompt() {
        return prompt;
    }

    public void newPrompt() {
        StringBuilder sb = new StringBuilder(prompt);
        List<Integer> emptySolt = new ArrayList<>();
        for (int i = 0; i < sb.length(); i++) {
            if (sb.charAt(i) == '_') {
                emptySolt.add(i);
            }
        }
        if (emptySolt.isEmpty() || emptySolt.size() == 1) return;
        int r = random.nextInt(emptySolt.size());
        sb.setCharAt(r, selectTheme.name.charAt(r));
        prompt = sb.toString();
    }

    public void setType(ItemStack type) {
        this.type = type;
        switch (type.getType()) {
            case WATER_BUCKET:
                this.type.setType(Material.WATER);
                break;
            case LAVA_BUCKET:
                this.type.setType(Material.LAVA);
                 break;
            default:
                break;
        }
        Location loc1=RoundManager.getRound().getRegion().getMax().clone();
        Location loc2=RoundManager.getRound().getRegion().getMin().clone();
        loc1.setY(Game.getInstance().getFlatY());
        loc2.setY(Game.getInstance().getFlatY());
        for (Block block : new Cube(loc1, loc2).getBlocks()) {
            block.setTypeIdAndData(RoundManager.getRound().getType().getTypeId(),RoundManager.getRound().getType().getData().getData(),true);
        }
    }
}
