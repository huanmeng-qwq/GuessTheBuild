package me.huanmeng.guessthebuild.utils;

import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
@Author huanmeng_qwq
*/
@Getter
public class TextBuilder implements Cloneable {
    private String text;
    private TextComponent builder;
    public TextBuilder(String text){
        this.text=text;
        builder=new TextComponent(ts(text));
    }
    private String ts(String s){
        return ChatColor.translateAlternateColorCodes('&',s);
    }
    public TextBuilder addHover(String... info){
        StringBuilder sb=new StringBuilder();
        for (String s : info) {
            sb.append(s+",");
        }
        String h= sb.substring(0,sb.length()-1).replace(",","\n");
        builder.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,new BaseComponent[]{new TextBuilder(h).build()}));
        return this;
    }
    public TextBuilder addClick(ClickEvent.Action action,String s){
        builder.setClickEvent(new ClickEvent(action,s));
        return this;
    }
    public TextBuilder setText(String text){
        builder.setText(text);
        this.text=text;
        return this;
    }
    public TextBuilder addExtra(String s){
        builder.addExtra(s);
        return this;
    }
    public TextBuilder addExtra(TextBuilder textBuilder){
        builder.addExtra(textBuilder.build());
        return this;
    }
    public TextComponent build(){
        return builder;
    }
    public TextBuilder send(CommandSender sender){
        if(sender instanceof Player) {
            ((Player) sender).spigot().sendMessage(build());
        }else{
            sendMessage(sender, build().getText());
        }
        return this;
    }
    @Override
    public TextBuilder clone() throws CloneNotSupportedException {
        return (TextBuilder) super.clone();
    }
    public static void sendMessage(CommandSender sender,String... message){
        for (String s : message) {
            sender.sendMessage(ChatColor.translateAlternateColorCodes('&',s));
        }
    }
}
