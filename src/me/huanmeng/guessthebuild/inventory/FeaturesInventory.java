package me.huanmeng.guessthebuild.inventory;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import me.huanmeng.guessthebuild.GuesstheBuild;
import me.huanmeng.guessthebuild.game.*;
import me.huanmeng.guessthebuild.game.WeatherType;
import me.huanmeng.guessthebuild.utils.ItemBuilder;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/25<br>
 * GuesstheBuild
 */
public class FeaturesInventory {
    public static SmartInventory Features(){
        SmartInventory.Builder builder=SmartInventory.builder();
        builder.size(4,9);
        builder.title(GuesstheBuild.config.getString("inventory.title"));
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                contents.set(1,2, ClickableItem.of(new ItemBuilder(Material.YELLOW_FLOWER).setDisplayName(GuesstheBuild.config.getString("inventory.weather.name")).build(),e->{
                    InventoryManager.FE_WEATHER.open(player);
                }));
                contents.set(1,3, ClickableItem.of(new ItemBuilder(Material.WATCH).setDisplayName(GuesstheBuild.config.getString("inventory.time.name")).build(),e->{
                    InventoryManager.FE_TIME.open(player);
                }));
                contents.set(1,5, ClickableItem.of(new ItemBuilder(Material.EMPTY_MAP).setDisplayName(GuesstheBuild.config.getString("inventory.biome.name")).build(),e->{
                    InventoryManager.FE_BIOME.open(player);
                }));
                contents.set(1,6, ClickableItem.of(new ItemBuilder(RoundManager.getRound().getType().getType()).setDisplayName(GuesstheBuild.config.getString("inventory.block")).build(),e->{
                    if(e.getWhoClicked().getItemOnCursor()!=null&&e.getWhoClicked().getItemOnCursor().getType()!=Material.AIR&&(e.getWhoClicked().getItemOnCursor().getType().isBlock()||e.getWhoClicked().getItemOnCursor().getType().name().endsWith("BUCKET"))){
                        RoundManager.getRound().setType(e.getWhoClicked().getItemOnCursor());
                        e.getWhoClicked().setItemOnCursor(null);
                        sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                        player.closeInventory();
                    }
                }));
                contents.set(2,4,ClickableItem.of(new ItemBuilder(Material.BANNER).setDisplayName(GuesstheBuild.config.getString("inventory.banner")).build(),e->{
                    buildBannerColor(new ItemStack(Material.BANNER,1, (short) 15)).open(player);
                }));

            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.build();
    }
    public static SmartInventory Weather(){
        SmartInventory.Builder builder=SmartInventory.builder();
        builder.title(GuesstheBuild.config.getString("inventory.weather.name")).size(4,9).parent(InventoryManager.FE);
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                contents.set(1,2,ClickableItem.of(new ItemBuilder(Material.BLAZE_ROD).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.weather.weather.thunder")).build(),e->{
                    RoundManager.getRound().setWeatherType(WeatherType.THUNDER);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                }));
                contents.set(1,3,ClickableItem.of(new ItemBuilder(Material.WATER_BUCKET).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.weather.weather.downfall")).build(),e->{
                    RoundManager.getRound().setWeatherType(WeatherType.DOWNFALL);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                }));
                contents.set(1,4,ClickableItem.of(new ItemBuilder(Material.SNOW_BALL).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.weather.weather.snow")).build(),e->{
                    RoundManager.getRound().setWeatherType(WeatherType.SNOW);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                }));
                contents.set(1,5,ClickableItem.of(new ItemBuilder(Material.YELLOW_FLOWER).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.weather.weather.clear")).build(),e->{
                    RoundManager.getRound().setWeatherType(WeatherType.CLEAR);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                }));
                contents.set(3,4,ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&a"+GuesstheBuild.config.getString("inventory.back")).addLore("&7"+GuesstheBuild.config.getString("inventory.back1")).build(), e->{
                    builder.getParent().open(player);
                }));
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.title(GuesstheBuild.config.getString("inventory.weather.title")).build();
    }
    public static SmartInventory Time(){
        SmartInventory.Builder builder=SmartInventory.builder();
        builder.size(4,9).parent(InventoryManager.FE);
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                contents.set(1,2,ClickableItem.of(new ItemBuilder(Material.STAINED_CLAY,1, (byte) 14).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.time.time.before_dawn")).build(),e->{
                    RoundManager.getRound().setTime(500);
                }));
                contents.set(1,3,ClickableItem.of(new ItemBuilder(Material.STAINED_CLAY,1, (byte) 14).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.time.time.morning")).build(),e->{
                    RoundManager.getRound().setTime(900);
                }));
                contents.set(1,4,ClickableItem.of(new ItemBuilder(Material.STAINED_CLAY,1, (byte) 1).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.time.time.noon")).build(),e->{
                    RoundManager.getRound().setTime(6000);
                }));
                contents.set(1,5,ClickableItem.of(new ItemBuilder(Material.STAINED_CLAY,1, (byte) 13).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.time.time.evening")).build(),e->{
                    RoundManager.getRound().setTime(13500);
                }));
                contents.set(1,6,ClickableItem.of(new ItemBuilder(Material.STAINED_CLAY,1, (byte) 13).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.time.time.night")).build(),e->{
                    RoundManager.getRound().setTime(18000);
                }));
                contents.set(3,4,ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&a"+GuesstheBuild.config.getString("inventory.back")).addLore("&7"+GuesstheBuild.config.getString("inventory.back1")).build(), e->{
                    builder.getParent().open(player);
                }));

            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.title(GuesstheBuild.config.getString("inventory.time.title")).build();
    }
    public static SmartInventory Biome(){
        SmartInventory.Builder builder= SmartInventory.builder();
        builder.parent(Features()).size(4,9).parent(InventoryManager.FE);
        Biome biome=RoundManager.getRound()==null||RoundManager.getRound().getBiome()==null?Biome.PLAINS:RoundManager.getRound().getBiome();
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                contents.set(0,0,ClickableItem.of(new ItemBuilder(Material.GRASS).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.plains")).addLore((biome!=Biome.PLAINS?"&e点击选择！":"&a已选择")).build(),e->{
                    RoundManager.getRound().setBiome(Biome.PLAINS);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,1,ClickableItem.of(new ItemBuilder(Material.SANDSTONE,1, (byte) 1).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.mesa")).addLore((biome!=Biome.MESA?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.MESA);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,2,ClickableItem.of(new ItemBuilder(Material.WATER_BUCKET).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.ocean")).addLore((biome!=Biome.OCEAN?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.OCEAN);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,3,ClickableItem.of(new ItemBuilder(Material.SAND).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.desert")).addLore((biome!=Biome.DESERT?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.DESERT);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,4,ClickableItem.of(new ItemBuilder(Material.LOG).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.forest")).addLore((biome!=Biome.FOREST?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.FOREST);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,5,ClickableItem.of(new ItemBuilder(Material.VINE).setDisplayName("&a"+GuesstheBuild.config.getString("inventory.biome.biome.jungle")).addLore((biome!=Biome.JUNGLE?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.JUNGLE);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,6,ClickableItem.of(new ItemBuilder(Material.ICE).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.ice_plains")).addLore((biome!=Biome.ICE_PLAINS?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.ICE_PLAINS);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,7,ClickableItem.of(new ItemBuilder(Material.WATER_LILY).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.swampland")).addLore((biome!=Biome.SWAMPLAND?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.SWAMPLAND);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(0,8,ClickableItem.of(new ItemBuilder(Material.LOG_2).setDisplayName("&e"+GuesstheBuild.config.getString("inventory.biome.biome.savanna")).addLore((biome!=Biome.SAVANNA?"&e点击选择！":"&a已选择")).build(), e->{
                    RoundManager.getRound().setBiome(Biome.SAVANNA);
                    sendMessage(player,GuesstheBuild.config.getString("inventory.success"));
                    player.closeInventory();
                }));
                contents.set(1,4,ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName(GuesstheBuild.config.getString("inventory.back")).addLore(GuesstheBuild.config.getString("inventory.back1")).build(), e->{
                    builder.getParent().open(player);
                }));
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.title(GuesstheBuild.config.getString("inventory.biome.title")).build();
    }
    public static SmartInventory buildBannerColor(ItemStack itemStack){
        SmartInventory.Builder builder=SmartInventory.builder();
        if(itemStack.getType()!=Material.BANNER)return builder.build();
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                int y=0;
                int x=1;
                for (DyeColor value : DyeColor.values()) {
                    if(x==8){
                        x=1;
                        ++y;
                    }
                    contents.set(y,x,ClickableItem.of(new ItemBuilder(Material.INK_SACK).setDyeColor(value).build(),e->{
                        buildBannerPattern(itemStack,value).open(player);
                    }));
                    ++x;
                }
                contents.set(5,3,ClickableItem.of(itemStack,e->{
                    player.closeInventory();
                    player.getInventory().addItem(itemStack);
                }));
                contents.set(5,5,ClickableItem.of(new ItemBuilder(Material.BARRIER).setDisplayName(GuesstheBuild.config.getString("inventory.close")).build(),e->{
                    player.closeInventory();
                }));
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.build();
    }

    public static SmartInventory buildBannerPattern(ItemStack stack,DyeColor dyeColor){
        SmartInventory.Builder builder=SmartInventory.builder();
        if(stack.getType()!=Material.BANNER)return builder.build();
        builder.provider(new InventoryProvider() {
            @Override
            public void init(Player player, InventoryContents contents) {
                int y=0;
                int x=0;
                for (PatternType value : PatternType.values()) {
                    if(x==9){
                        x=0;
                        ++y;
                    }
                    contents.set(y,x,ClickableItem.of(new ItemBuilder(Material.BANNER,1, (byte) 0).addPattern(new Pattern(DyeColor.WHITE,value)).addLore("&7点击以添加到旗帜").build(), e->{
                        ItemStack stack1=new ItemBuilder(stack).addPattern(new Pattern(dyeColor,value)).build();
                        buildBannerColor(stack1).open(player);
                    }));
                    ++x;
                }
                contents.set(5,3,ClickableItem.of(stack,e->{
                    player.closeInventory();
                    player.getInventory().addItem(stack);
                }));
                contents.set(5,5,ClickableItem.of(new ItemBuilder(Material.BARRIER).setDisplayName(GuesstheBuild.config.getString("inventory.close")).build(), e->{
                    player.closeInventory();
                }));
            }

            @Override
            public void update(Player player, InventoryContents contents) {

            }
        });
        return builder.build();
    }
    public static void sendMessage(Player player,String... message){
        for (String s : message) {
            player.sendMessage(ChatColor.translateAlternateColorCodes('&',s));
        }
    }
}
