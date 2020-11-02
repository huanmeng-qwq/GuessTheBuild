package me.huanmeng.guessthebuild;

import com.comphenix.protocol.ProtocolLibrary;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import fr.minuskube.inv.InventoryManager;
import lombok.Getter;
import lombok.Setter;
import me.huanmeng.guessthebuild.command.CommandHandler;
import me.huanmeng.guessthebuild.command.ForceStartCommand;
import me.huanmeng.guessthebuild.command.SetupCommand;
import me.huanmeng.guessthebuild.config.Config;
import me.huanmeng.guessthebuild.database.DataBase;
import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.listener.*;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GuesstheBuild extends JavaPlugin {
    @Getter
    private static GuesstheBuild instance;
    @Getter
    private File gamefile;
    List<String> lobbys;
    private static Config gameconfig;
    @Getter
    @Setter
    private Game game;
    @Getter
    private InventoryManager inventoryManager;
    @Getter
    private static DataBase dataBase;
    //    @Getter
//    private static DataBase serverDatabase;
    @Getter
    private static String server_name;
    public static Config config;

    @Override
    public void onEnable() {
        instance = this;
        if (Locale.getDefault().getLanguage().equals("zh")) {
            File zh = new File(getDataFolder(), "chinese_config.yml");
            if (!zh.exists()) {
                saveResource("chinese_config.yml", true);
                System.out.println("请手动将"+zh.getAbsolutePath()+"文件转码");
            }
            config = new Config(zh);

        } else {
            saveDefaultConfig();
            config = new Config(new File(getDataFolder(), "config.yml"));
        }
        lobbys = new ArrayList<>();
        for (Object lobby : getConfig().getList("lobby")) {
            lobbys.add(String.valueOf(lobby));
        }
        gamefile = new File(getDataFolder(), "game.yml");
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        dataBase = DataBase.create(this, getConfig().getConfigurationSection("database"));
        dataBase.createTables(SQL.TABLE_DATA, SQL.KV_DATA, null);
        if (gamefile.exists()) {
            gameconfig = new Config(gamefile);
            game = new Game(gameconfig);
            for (World world : getServer().getWorlds()) {
                world.setPVP(false);
            }
            inventoryManager = new InventoryManager(this);
            inventoryManager.init();
            new me.huanmeng.guessthebuild.inventory.InventoryManager();
            new CommandHandler();
            getServer().getPluginManager().registerEvents(new PlayerListener(), this);
            getServer().getPluginManager().registerEvents(new BlockListener(), this);
            getServer().getPluginManager().registerEvents(new MoveListener(), this);
            getServer().getPluginManager().registerEvents(new ServerListener(), this);
            getCommand("forcestart").setExecutor(new ForceStartCommand());
        } else {
            getCommand("gb").setExecutor(new SetupCommand());
            getServer().getPluginManager().registerEvents(new SetupListener(), this);
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new WeatherPacket());
        Metrics metrics = new Metrics(this, 9290);
    }

    public void tpToLobby(Player p) {
        if (lobbys.isEmpty()) return;
        try {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF("Connect");
            buf.writeUTF(lobbys.get(new Random().nextInt(lobbys.size())));
            p.sendPluginMessage(this, "BungeeCord", buf.toByteArray());
        } catch (Exception ignored) {
        }
    }

    public static void tpServer(Player player, String srv) {
        String server = srv;
        try {
            ByteArrayDataOutput buf = ByteStreams.newDataOutput();
            buf.writeUTF("Connect");
            buf.writeUTF(server);
            player.sendPluginMessage(getInstance(), "BungeeCord", buf.toByteArray());
        } catch (Exception var3) {
        }
    }

    public static void setGameconfig(Config gameconfig) {
        GuesstheBuild.gameconfig = gameconfig;
    }

    public static Config getGameConfig() {
        return gameconfig;
    }

    public void sendMessage(String... s) {
        int i = 0;
        for (String s1 : s) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', (++i) + "." + s1));
        }
    }

    @Override
    public void onDisable() {
        if (dataBase != null) {
            dataBase.close();
        }
        for (NPC npc : Game.getNpcs()) {
            npc.despawn();
            npc.destroy();
        }
    }
}
