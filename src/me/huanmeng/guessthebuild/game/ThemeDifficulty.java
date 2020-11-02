package me.huanmeng.guessthebuild.game;

import me.huanmeng.guessthebuild.GuesstheBuild;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/24<br>
 * GuesstheBuild
 */
public enum ThemeDifficulty {
    EASY("§a"),MEDIUM("§e"),HARD("§5");

    String color;
    ThemeDifficulty(String s) {
        this.color=s;
    }

    public String getColor() {
        return color;
    }
    public String getName(){
        switch (color.toLowerCase()) {
            case "§a":
                return color+ GuesstheBuild.config.getString("theme.Difficulty.EASY");
            case "§e":
                return color+ GuesstheBuild.config.getString("theme.Difficulty.MEDIUM");
            case "§5":
                return color+ GuesstheBuild.config.getString("theme.Difficulty.HARD");
        }
        return "";
    }
}
