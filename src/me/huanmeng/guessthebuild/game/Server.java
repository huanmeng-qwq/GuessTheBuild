package me.huanmeng.guessthebuild.game;

public class Server {
    String name;
    String mapName;
    int online;
    public Server(){
    }

    public Server setName(String name) {
        this.name = name;
        return this;
    }

    public Server setMapName(String mapName) {
        this.mapName = mapName;
        return this;
    }

    public String getName() {
        return name;
    }

    public String getMapName() {
        return mapName;
    }

    public Server setOnline(int online) {
        this.online = online;
        return this;
    }

    public int getOnline() {
        return online;
    }
}
