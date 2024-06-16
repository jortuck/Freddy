package net.driedsponge.actions;

import net.driedsponge.Player;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;

public class VoiceChannelActions {

    private VoiceChannelActions(){
        throw new AssertionError();
    };

    /**
     *
     * @param channel The voice channel to join.
     * @param manager The AudioManager associated with the guild.
     * @throws IllegalArgumentException If channel or manager is null, or if the channel is not a
     * part of the guild
     * @throws UnsupportedOperationException  Due to JDA error.
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException Bad permissions or
     * channel is full.
     */
    public static Player join(AudioChannel channel, AudioManager manager) throws IllegalArgumentException  {
        if (channel==null || manager==null){
            throw new IllegalArgumentException();
        }
        manager.openAudioConnection(channel);
        manager.setSelfDeafened(true);
        return new Player(channel.getGuild());
    };

    public static void leave(AudioManager manager){
        if(manager.isConnected()){
            manager.closeAudioConnection();
        }else{
            System.out.println("remove player from store");
        }
    };
}
