package me.huanmeng.guessthebuild;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.injector.GamePhase;

/**
 * 作者 huanmeng_qwq<br>
 * 2020/9/25<br>
 * GuesstheBuild
 */
public class WeatherPacket extends PacketAdapter {
    public WeatherPacket() {
        super(PacketAdapter.params().listenerPriority(ListenerPriority.NORMAL).serverSide().clientSide().optionAsync().gamePhase(GamePhase.PLAYING).types(new PacketType[]{PacketType.Play.Server.SPAWN_ENTITY_WEATHER}).plugin(GuesstheBuild.getInstance()));
    }

    @Override
    public void onPacketSending(PacketEvent e) {
        if(e.getPacketType()== PacketType.Play.Server.SPAWN_ENTITY_WEATHER){
            System.out.println(e.getPacket());
        }
    }
}
