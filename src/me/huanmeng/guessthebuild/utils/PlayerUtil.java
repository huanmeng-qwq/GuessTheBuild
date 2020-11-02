package me.huanmeng.guessthebuild.utils;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.minecraft.server.v1_8_R3.EntityLightning;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityWeather;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class PlayerUtil {
    private static ProtocolManager api = ProtocolLibrary.getProtocolManager();

    public static void sendActionbar(Player player, String text) {
        PacketContainer pc = new PacketContainer(PacketType.Play.Server.CHAT);
        pc.getBytes().write(0, EnumWrappers.ChatType.GAME_INFO.getId());
        pc.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', text)));
        try {
            api.sendServerPacket(player, pc);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void sendAllTitle(String title, String subtitle) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendTitle(onlinePlayer, title, subtitle);
        }
    }
    public static void sendAllTitle(String title, String subtitle,int in,int stay,int out) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            sendTitle(onlinePlayer, title, subtitle,in,stay,out);
        }
    }

    public static void sendTitle(Player p, String title, String subtitle) {
        sendTitle(p, title, subtitle, 10, 20, 10);
    }

    public static void sendTitle(Player player, String title, String subtitle, int in, int stay, int out) {
        PacketContainer pc1 = new PacketContainer(PacketType.Play.Server.TITLE);
        PacketContainer pc2 = new PacketContainer(PacketType.Play.Server.TITLE);
        pc1.getTitleActions().write(0, EnumWrappers.TitleAction.TITLE);
        pc2.getTitleActions().write(0, EnumWrappers.TitleAction.SUBTITLE);
        pc1.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', title)));
        pc2.getChatComponents().write(0, WrappedChatComponent.fromText(ChatColor.translateAlternateColorCodes('&', subtitle)));
        pc1.getIntegers().write(0, in);
        pc2.getIntegers().write(0, in);
        pc1.getIntegers().write(1, stay);
        pc2.getIntegers().write(1, stay);
        pc1.getIntegers().write(2, out);
        pc2.getIntegers().write(2, out);
        try {
            api.sendServerPacket(player, pc1);
            api.sendServerPacket(player, pc2);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void playFakeLightning(Player sender, boolean silent) {
//        sender.getLocation().getWorld().spigot().strikeLightning(sender.getLocation(), silent);
        World world = ((CraftWorld) sender.getLocation().getWorld()).getHandle();
        Packet packet = new PacketPlayOutSpawnEntityWeather(new EntityLightning(world, sender.getLocation().getX(), sender.getLocation().getY(), sender.getLocation().getZ()));

        ((CraftPlayer) sender).getHandle().playerConnection.sendPacket(packet);

        if (!silent)
            sender.getLocation().getWorld().playSound(sender.getLocation(), Sound.AMBIENCE_THUNDER, 100, 0);
    }

    public static void playFakeLightning(Player sender) {
        playFakeLightning(sender, true);
    }
}
