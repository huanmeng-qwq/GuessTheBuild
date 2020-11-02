package me.huanmeng.guessthebuild.inventory;

import fr.minuskube.inv.SmartInventory;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public class InventoryManager {
//    public static SmartInventory MATCHSERVERMENU;
    public static SmartInventory SELECTTHEME;
    public static SmartInventory FE;
    public static SmartInventory FE_WEATHER;
    public static SmartInventory FE_TIME;
    public static SmartInventory FE_BIOME;
    public InventoryManager(){
        SELECTTHEME=SelectTheme.build();
//        MATCHSERVERMENU=MatchServerMenu.buildMain();
        FE=FeaturesInventory.Features();
        FE_WEATHER=FeaturesInventory.Weather();
        FE_TIME=FeaturesInventory.Time();
        FE_BIOME=FeaturesInventory.Biome();
    }
}
