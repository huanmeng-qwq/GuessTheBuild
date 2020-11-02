package me.huanmeng.guessthebuild.game;

import lombok.Getter;

@Getter
public class Theme implements Cloneable{
    public String name;
    private ThemeDifficulty dif;
    public Theme(ThemeDifficulty dif,String name) {
        this.name = name;
        this.dif=dif;
    }

    public String getName() {
        return dif.color+name;
    }

    @Override
    public Theme clone() throws CloneNotSupportedException {
        return (Theme) super.clone();
    }
}
