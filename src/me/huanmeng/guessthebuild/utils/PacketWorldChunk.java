package me.huanmeng.guessthebuild.utils;

import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunk;
import net.minecraft.server.v1_8_R3.PacketPlayOutMapChunkBulk;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Chunk;
import org.bukkit.craftbukkit.v1_8_R3.CraftChunk;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PacketWorldChunk {

    private PacketPlayOutMapChunk packet;

    public PacketWorldChunk(final Chunk chunk) {
        packet = new PacketPlayOutMapChunk(((CraftChunk) chunk).getHandle(),true, 65535);
    }

    public void send(Player player) {
        PlayerConnection conn = ((CraftPlayer) player).getHandle().playerConnection;
        conn.sendPacket(packet);
    }

}