package me.huanmeng.guessthebuild.listener;

import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @Author huanmeng_qwq
 * 2020/9/23
 * GuesstheBuild
 */
public class SetupListener implements Listener {
    public static Location left;
    public static Location right;
    private ItemStack axe = null;

    public SetupListener() {
        this.axe = (new ItemBuilder(Material.DIAMOND_AXE)).setDisplayName("§a§lRegion Tool").setUnbreakable(true).build();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        if(e.getPlayer().hasPermission("guessthebuild.setup")){
            Bukkit.getScheduler().runTaskLater(GuesstheBuild.getInstance(),()->{
                e.getPlayer().getInventory().addItem(axe);
            },30);
        }
    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        if (e.getPlayer().getItemInHand() != null) {
            if (e.getPlayer().getItemInHand().equals(this.axe) && (e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
                if (e.getAction() == Action.LEFT_CLICK_BLOCK) {
                    left = e.getClickedBlock().getLocation();
                    e.getPlayer().sendMessage("§eSelect First:" + left.getBlockX() + " " + left.getBlockY() + " " + left.getBlockZ());
                } else if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    right = e.getClickedBlock().getLocation();
                    e.getPlayer().sendMessage("§eSelect Second:" + right.getBlockX() + " " + right.getBlockY() + " " + right.getBlockZ());
                }

                e.setCancelled(true);
            }

        }
    }
}
