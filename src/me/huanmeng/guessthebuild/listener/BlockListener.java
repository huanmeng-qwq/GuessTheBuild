package me.huanmeng.guessthebuild.listener;

import me.huanmeng.guessthebuild.game.Game;
import me.huanmeng.guessthebuild.game.GameStatus;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class BlockListener implements Listener {
    List<Location> locs = new ArrayList<>();

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e) {
        Game game = Game.getInstance();
        e.setBuild(false);
        e.setCancelled(true);
        if (game.getBuilder() != null && game.getBuilder().getPlayer().getUniqueId() == e.getPlayer().getUniqueId()) {
            e.setBuild(true);
            e.setCancelled(false);
            locs.add(e.getBlock().getLocation());
        }
        if(e.getBlock().getType().name().contains("SIGN")){
            e.setBuild(false);
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void onuseAnvil(PlayerInteractEvent e){
        if(e.getClickedBlock()!=null&&(e.getClickedBlock().getType().name().contains("SIGN")||e.getClickedBlock().getType().name().contains("ANVIL"))){
            e.setCancelled(true);
            e.getClickedBlock().setType(Material.AIR);
        }
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onChechCanBuild(BlockCanBuildEvent e) {
            e.setBuildable(true);
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        e.setCancelled(true);
        Game game = Game.getInstance();
        if (game.getStatus()== GameStatus.GAMING&&game.getBuilder() != null && game.getBuilder().getPlayer().getUniqueId() == e.getPlayer().getUniqueId()) {
            if (locs.contains(e.getBlock().getLocation())) {
                locs.remove(e.getBlock().getLocation());
                e.setCancelled(false);
            }
            return;
        }
    }
}
