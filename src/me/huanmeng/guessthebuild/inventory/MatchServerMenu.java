//package me.huanmeng.guessthebuild.inventory;
///*
// * @Author huanmeng_qwq
// * @Date 2020/9/1 20:37
// * Bedwars
// */
//
//import fr.minuskube.inv.ClickableItem;
//import fr.minuskube.inv.SmartInventory;
//import fr.minuskube.inv.content.InventoryContents;
//import fr.minuskube.inv.content.InventoryProvider;
//import me.huanmeng.guessthebuild.GuesstheBuild;
//import me.huanmeng.guessthebuild.SQL;
//import me.huanmeng.guessthebuild.database.KeyValue;
//import me.huanmeng.guessthebuild.game.GameStatus;
//import me.huanmeng.guessthebuild.game.Server;
//import me.huanmeng.guessthebuild.utils.ItemBuilder;
//import org.bukkit.ChatColor;
//import org.bukkit.Material;
//import org.bukkit.entity.Player;
//import org.bukkit.event.inventory.InventoryType;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SuppressWarnings("all")
//public class MatchServerMenu {
//    public static SmartInventory build(){
//        final SmartInventory.Builder inv= SmartInventory.builder();
//        inv.size(6,9);
//        inv.type(InventoryType.CHEST);
//        inv.title("建筑猜猜乐");
//        inv.provider(new InventoryProvider() {
//            @Override
//            public void init(Player player, InventoryContents contents) {
//                int row = 1;
//                int column = 1;
//                for (Server s : getWaitServer()) {
//                    contents.set(row,column, ClickableItem.of(new ItemBuilder(Material.PAPER).setDisplayName("&a"+s.getMapName()).setLore("&a"+s.getOnline()+"&b/"+"&e16","   ","&a等待中..").build(), e->{
//                        if(player.hasPermission("guessthebuild.mapseletor")) {
//                            e.setCancelled(true);
//                            GuesstheBuild.tpServer(player, GuesstheBuild.translateServerByGame(s));
//                        }else{
//                            player.closeInventory();
//                            player.sendMessage(ChatColor.translateAlternateColorCodes('&',"&c你没有权限使用"));
//                        }
//                    }));
//                    if (column + 1 > 7) {
//                        ++row;
//                        column = 1;
//                    } else {
//                        ++column;
//                    }
//                    if(row==4)break;
//                }
//                contents.set(4,4, ClickableItem.of(new ItemBuilder(Material.FIREWORK).setDisplayName("&a随机地图").setLore("   ","&a▶点击进行游戏").build(), e->{
//                    e.setCancelled(true);
//                    GuesstheBuild.tpServer(player,GuesstheBuild.translateServerByGame(CommandFastJoin.fastJoin()));
//                }));
//
//                contents.set(5,4, ClickableItem.of(new ItemBuilder(Material.ARROW).setDisplayName("&7返回到建筑猜猜乐").build(), e->{
//                    e.setCancelled(true);
//                    buildMain().open(player);
//                }));
//
//            }
//
//            @Override
//            public void update(Player var1, InventoryContents contents) {
//            }
//        });
//        return inv.build();
//    }
//    public static SmartInventory buildMain(){
//        SmartInventory.Builder builder= SmartInventory.builder();
//        builder.size(4,9);
//        builder.type(InventoryType.CHEST);
//        builder.title("游玩建筑猜猜乐");
//        builder.provider(new InventoryProvider() {
//            @Override
//            public void init(Player player, InventoryContents contents) {
//                contents.set(1,3, ClickableItem.of(new ItemBuilder(Material.BRICK,1).setDisplayName("&e建筑猜猜乐").setLore("&7游玩建筑猜猜乐"," ","&e点击开始游戏").build(), e->{
//                    e.setCancelled(true);
//                    GuesstheBuild.tpServer(player,GuesstheBuild.translateServerByGame(CommandFastJoin.fastJoin()));
//                }));
//
//                contents.set(1,5, ClickableItem.of(new ItemBuilder(Material.SIGN,1).setDisplayName("&a地图选择器").setLore("&7从可用服务器列表中","&7选择你想玩的服务器"," ","&e点击浏览").build(), e->{
//                    e.setCancelled(true);
//                    build().open(player);
//                }));
//
//                contents.set(3,4, ClickableItem.of(new ItemBuilder(Material.BARRIER,1).setDisplayName("&c关闭").build(), e->{
//                    e.setCancelled(true);
//                    player.closeInventory();
//                }));
////                contents.set(3,8, ClickableItem.of(new ItemBuilder(Material.ENDER_PEARL,1).setDisplayName("&c点击这里重新加入！").setLore("&7如果你掉线了,点击这里可以重新回到游戏.").build(), e->{
////                    e.setCancelled(true);
////                    player.closeInventory();
////                    Bukkit.dispatchCommand(player,"rejoin");
////                }));
//            }
//
//            @Override
//            public void update(Player player, InventoryContents contents) {
//
//            }
//        });
//        return builder.build();
//    }
//    public static List<Server> getWaitServer(){
//        List<Server> servers=new ArrayList<>();
//        for (KeyValue keyValue : GuesstheBuild.getServerDatabase().dbSelectA(SQL.TABLE_STATUS, new KeyValue("type", "type").add("mapname","mapname").add("servername","servername"), new KeyValue("status", GameStatus.WAIT.name().toLowerCase()).add("type","BuildTheGuess"))) {
//            String s=GuesstheBuild.getDataBase().dbSelectFirst(SQL.TABLE_STATUS,"online",new KeyValue("servername",keyValue.getString("servername")));
//            servers.add(new Server().setName(keyValue.getString("servername")).setMapName(keyValue.getString("mapname")).setOnline(s!=null?Integer.parseInt(s):0));
//        }
//        return servers;
//    }
//}
