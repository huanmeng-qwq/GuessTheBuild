package me.huanmeng.guessthebuild.listener;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.*;
import me.huanmeng.guessthebuild.inventory.InventoryManager;
import me.huanmeng.guessthebuild.inventory.SelectTheme;
import me.huanmeng.guessthebuild.utils.LuckPerms;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleBlockCollisionEvent;
import org.bukkit.potion.PotionEffect;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class PlayerListener implements Listener {
    @EventHandler
    public void onLogin(PlayerLoginEvent e) {
        if (Game.getInstance() != null && Game.getInstance().getStatus() != GameStatus.WAIT && !Game.getInstance().getRejoin().contains(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().hasPermission("guessthebuild.forcejoin")) {
                return;
            }
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, "§cGame Already Start.");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        e.setJoinMessage(null);
        GamePlayer gamePlayer = GamePlayer.get(e.getPlayer().getUniqueId());
        if (gamePlayer == null) {
            gamePlayer = new GamePlayer(e.getPlayer().getUniqueId());
            gamePlayer.getPlayer().setPlayerListName(gamePlayer.getPrefix() + " " + (gamePlayer.getPlayer().getName().equalsIgnoreCase(gamePlayer.getName()) ? LuckPerms.getRank(gamePlayer.getPlayer()).replace("&", "§") : "§7") + gamePlayer.getName());
        }
        if (Game.getInstance() != null && Game.getInstance().getStatus() == GameStatus.WAIT) {
            Game.getInstance().addPlayer(gamePlayer);
            return;
        }
        if (Game.getInstance() != null && Game.getInstance().getStatus() != GameStatus.WAIT && !Game.getInstance().getRejoin().contains(e.getPlayer().getUniqueId())) {
            if (!e.getPlayer().hasPermission("guessthebuild.forcejoin")) {
                e.getPlayer().kickPlayer("§cGame Already Start.");
                return;
            }
            Game.getInstance().getRejoin().add(e.getPlayer().getUniqueId());
        }
        if (Game.getInstance() != null && Game.getInstance().getStatus() == GameStatus.GAMING && Game.getInstance().getRejoin().contains(e.getPlayer().getUniqueId())) {
            gamePlayer.setStatus(PlayerStatus.GUESS);
            gamePlayer.setPlayer(e.getPlayer());
            gamePlayer.setTheme(true);
            gamePlayer.setName(e.getPlayer().getName());
            gamePlayer.getPlayer().setPlayerListName(gamePlayer.getPrefix() + " §7" + gamePlayer.getName());
            Game.getInstance().refresh();
            Game.getInstance().getPlayers().add(gamePlayer);
            e.getPlayer().teleport(RoundManager.getRound().getRegion().getMiddle());
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            for (PotionEffect activePotionEffect : e.getPlayer().getActivePotionEffects()) {
                e.getPlayer().removePotionEffect(activePotionEffect.getType());
            }
            Game.getInstance().getRejoin().remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        e.setQuitMessage(null);
        if (Game.getInstance() == null) return;
        if (Game.getInstance().getStatus() == GameStatus.WAIT) {
            Game.getInstance().removePlayer(GamePlayer.get(e.getPlayer().getUniqueId()));
        }
        if (Game.getInstance().getStatus() == GameStatus.GAMING) {
            Game.getInstance().getRejoin().add(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        GamePlayer gamePlayer = GamePlayer.get(e.getPlayer().getUniqueId());
        if (gamePlayer != null) {
            e.setFormat(gamePlayer.getPrefix() + " " + LuckPerms.getPrefix(e.getPlayer()) + e.getPlayer().getDisplayName() + ": " + e.getMessage());
        }
        if (gamePlayer.isTheme()) {
            e.setCancelled(true);
            return;
        }
        if (RoundManager.getRound() != null && RoundManager.getRound().getSelectTheme() != null) {
            if (e.getMessage().equalsIgnoreCase(RoundManager.getRound().getSelectTheme().name)) {
                if (gamePlayer != null) {
                    if (gamePlayer.isTheme()) {
                        e.setCancelled(true);
                        return;
                    }
                    gamePlayer.setTheme(true);
                    e.setCancelled(true);
                    Game.getInstance().all(String.format(GuesstheBuild.config.getString("message.round.correct"), e.getPlayer().getDisplayName()));
                    RoundManager.getRound().addGuess();
                    switch (RoundManager.getRound().getGuess()) {
                        case 1:
                            gamePlayer.giveScore(3);
                            gamePlayer.sendMessage("&e+ &c3&eScore");
                            switch (RoundManager.getRound().getSelectTheme().getDif()) {
                                case EASY:
                                    RoundManager.getRound().getBuilder().giveScore(1);
                                    break;
                                case HARD:
                                    RoundManager.getRound().getBuilder().giveScore(2);
                                    break;
                                case MEDIUM:
                                    RoundManager.getRound().getBuilder().giveScore(3);
                                    break;
                            }
                            break;
                        case 2:
                        case 3:
                            gamePlayer.giveScore(2);
                            gamePlayer.sendMessage("&e+ &a2&eScore");
                            break;
                        default:
                            gamePlayer.giveScore(1);
                            gamePlayer.sendMessage("&e+ 1Score");
                            break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getItem() != null && e.getAction().name().contains("CLICK") && Game.getInstance().getStatus() == GameStatus.WAIT) {
            switch (e.getItem().getType()) {
                case BED:
                    GuesstheBuild.getInstance().tpToLobby(e.getPlayer());
                    e.setCancelled(true);
                    break;
                case DIAMOND:
                    if (e.getPlayer().hasPermission("guessthebuild.forcestart")) {
                        if (Game.getInstance().getOnlinePlayers().size() >= 4) {
                            Game.getInstance().start();
                        } else {
                            e.getPlayer().sendMessage("§cMust >=4 Players");
                        }
                        e.setCancelled(true);
                    }
                default:
                    return;
            }
        }
        if (e.getItem() != null && e.getAction().name().contains("CLICK") && Game.getInstance().getStatus() == GameStatus.GAMING) {
            switch (e.getItem().getType()) {
                case NETHER_STAR:
                    InventoryManager.FE.open(e.getPlayer());
                    break;
            }
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onInvClickDrop(InventoryClickEvent e) {
        if (e.getAction().name().contains("DROP") || e.getClick().name().contains("DROP")) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (Game.getInstance() != null && RoundManager.getRound() != null && Game.getInstance().getStatus() == GameStatus.GAMING && Game.getInstance().getBuilder().getUuid() == e.getPlayer().getUniqueId() && e.getView().getTitle() == GuesstheBuild.config.getString("inventory.select.title")) {
            if (RoundManager.getRound() != null && RoundManager.getRound().getSelectTheme() == null) {
                Bukkit.getScheduler().runTaskLater(GuesstheBuild.getInstance(), () -> InventoryManager.SELECTTHEME.open((Player) e.getPlayer()), 5);
            }
        }
    }

    @EventHandler
    public void onRightNPC(NPCRightClickEvent e) {
        if (!e.getNPC().getName().equalsIgnoreCase(GuesstheBuild.config.getString("message.npc.name"))) return;
        if (e.getClicker().getItemInHand() != null && e.getClicker().getItemInHand().getType() != Material.AIR && (e.getClicker().getItemInHand().getType().isBlock() || e.getClicker().getItemInHand().getType().name().endsWith("BUCKET"))) {
            RoundManager.getRound().setType(e.getClicker().getItemInHand());
            sendMessage(e.getClicker(), GuesstheBuild.config.getString("message.npc.message"));
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        e.setCancelled(true);
    }

    public static void sendMessage(Player player, String... message) {
        for (String s : message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDamageNPC(EntityDamageByEntityEvent e) {
        if (CitizensAPI.getNPCRegistry().isNPC(e.getEntity())) {
            e.setCancelled(true);
        }
    }
}
