package net.driedsponge.actions;

import net.driedsponge.Player;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.util.logging.Level;

public class VoiceChannelActions {

    private VoiceChannelActions(){
        throw new AssertionError();
    };

    public static void leave(AudioManager manager){
        if(manager.isConnected()){
            manager.closeAudioConnection();
        }else{
            System.out.println("remove player from store");
            Player.PLAYERS.remove(manager.getGuild().getId());
        }
    };
}
