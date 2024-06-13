package net.driedsponge.actions;

import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceChannelActions {
    public static void join(AudioChannel channel, AudioManager manager){
        manager.openAudioConnection(channel);
        manager.setSelfDeafened(true);
    };
    public static void leave(AudioManager manager){
        manager.closeAudioConnection();
    };
}
