package me.huanmeng.guessthebuild.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.*;
import me.huanmeng.guessthebuild.utils.ItemBuilder;
import me.huanmeng.guessthebuild.utils.PlayerUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @Author huanmeng_qwq
 * 2020/9/23
 * GuesstheBuild
 */
public class SelectTheme {
    public static SmartInventory build(){
        Game game=Game.getInstance();
        SmartInventory.Builder builder=SmartInventory.builder();
        builder.size(3,9);
        builder.title(GuesstheBuild.config.getString("inventory.select.title"));
        builder.type(InventoryType.CHEST);
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                List<Theme> t=new ArrayList<>(RoundManager.getRound().getTheme());
                contents.set(1,2, ClickableItem.of(new ItemBuilder(Material.PAPER).setDisplayName("&a"+t.get(0).name).addLore("&e"+ GuesstheBuild.config.getString("inventory.select.Difficulty") +": "+t.get(2).getDif().getName()).build(), e->{
                    if(RoundManager.getRound().getSelectTheme()==null){
                        RoundManager.getRound().setSelectTheme(t.get(0));
                    }
                    player.closeInventory();
                    Bukkit.getPluginManager().callEvent(new InventoryCloseEvent(player.getOpenInventory()));
                }));
                contents.set(1,4, ClickableItem.of(new ItemBuilder(Material.PAPER).setDisplayName("&a"+t.get(1).name).addLore("&e"+ GuesstheBuild.config.getString("inventory.select.Difficulty")+": "+t.get(2).getDif().getName()).build(),e->{
                    if(RoundManager.getRound().getSelectTheme()==null){
                        RoundManager.getRound().setSelectTheme(t.get(1));
                    }
                    player.closeInventory();
                    Bukkit.getPluginManager().callEvent(new InventoryCloseEvent(player.getOpenInventory()));
                }));
                contents.set(1,6, ClickableItem.of(new ItemBuilder(Material.PAPER).setDisplayName("&a"+t.get(2).name).addLore("&e"+ GuesstheBuild.config.getString("inventory.select.Difficulty")+": "+t.get(2).getDif().getName()).build(),e->{
                    if(RoundManager.getRound().getSelectTheme()==null){
                        RoundManager.getRound().setSelectTheme(t.get(2));
                    }
                    player.closeInventory();
                    Bukkit.getPluginManager().callEvent(new InventoryCloseEvent(player.getOpenInventory()));
                }));
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.build();
    }
}
