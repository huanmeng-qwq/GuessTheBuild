package me.huanmeng.guessthebuild.utils;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.node.NodeType;
import net.luckperms.api.node.types.InheritanceNode;
import net.luckperms.api.query.QueryOptions;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Objects;

public class LuckPerms {
    public static String getPrefix(Player p) {
        if (LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()) == null||LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix() == null) {
            return "§7";
        } else {
            return ChatColor.translateAlternateColorCodes('&',LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getPrefix()+" ");
        }

    }

    public static String getSuffix(Player p) {
        if (LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix() == null) {
            return "";
        } else {
            return ChatColor.translateAlternateColorCodes('&'," "+LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getCachedData().getMetaData(QueryOptions.defaultContextualOptions()).getSuffix());
        }
    }
    public static String getRank(Player p) {
        if(Bukkit.getPlayer(p.getUniqueId())==null){
            return "§7";
        }
        return LuckPermsProvider.get().getUserManager().getUser(p.getUniqueId()).getNodes().stream()
                .filter(NodeType.INHERITANCE::matches)
                .map(NodeType.INHERITANCE::cast)
                .filter(n -> n.getContexts().isSatisfiedBy(QueryOptions.defaultContextualOptions().context()))
                .map(InheritanceNode::getGroupName)
                .map(n -> LuckPermsProvider.get().getGroupManager().getGroup(n))
                .filter(Objects::nonNull)
                .min((o1, o2) -> {
                    int ret = Integer.compare(o1.getWeight().orElse(0), o2.getWeight().orElse(0));
                    return ret == 1 ? -1 : 1;
                })
                .map(Group::getName)
                .map(groupName -> convertGroupDisplayName(groupName))
                .orElse("");
    }
    private static String convertGroupDisplayName(String groupName) {
        Group group = LuckPermsProvider.get().getGroupManager().getGroup(groupName);
        if (group != null) {
            groupName = group.getFriendlyName();
        }
        return groupName;
    }
    public static String formatTime(int i){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yy/MM/dd hh:mm:ss");
        return simpleDateFormat.format(System.currentTimeMillis() + i*1000);
    }

}

