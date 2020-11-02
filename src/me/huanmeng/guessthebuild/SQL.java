package me.huanmeng.guessthebuild;

import me.huanmeng.guessthebuild.database.KeyValue;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/25<br>
 * GuesstheBuild
 */
public class SQL {
    public static final String TABLE_DATA="build_data";
    public static final String TABLE_SERVERS = "servers";
    public static final String TABLE_STATUS="status";
    public static final String TABLE_REJOIN="rejoin";
    public static final KeyValue KV_DATA=new KeyValue("uuid", "VARCHAR(36) PRIMARY KEY").add("win","BIGINT").add("score","BIGINT");
    public static final KeyValue KV_SERVER = new KeyValue("server", "VARCHAR(20) PRIMARY KEY").add("bc", "VARCHAR(25)");
    public static final KeyValue KV_STATUS=new KeyValue("type","VARCHAR(40) PRIMARY KEY").add("status","VARCHAR(20)").add("online","VARCHAR(50)").add("mapname","VARCHAR(20)").add("servername","VARCHAR(20)");
    public static final KeyValue KV_REJOIN=new KeyValue("uuid", "VARCHAR(36) PRIMARY KEY").add("servername","VARCHAR(20)").add("time","BIGINT");

}
