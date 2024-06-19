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
            manager.closeAudioConnection();
    };
}
