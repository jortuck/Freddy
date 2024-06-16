package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;

import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.api.audio.AudioSendHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

import java.net.URI;
import java.nio.ByteBuffer;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public final class Player {
    public static final HashMap<String, Player> PLAYERS = new HashMap<>();

    private final Guild guild;
    private TrackSchedule trackScheduler;
    private AudioPlayer player;
    private AudioChannelUnion channel;
    private TrackSchedule schedule;
    private final BlockingQueue<AudioTrack> queue;

    /**
     * @param channel
     * @throws IllegalArgumentException –
     * If the provided channel was null.
     * If the provided channel is not part of the Guild that the current audio connection is connected to.
     * @throws UnsupportedOperationException – If audio is disabled due to an internal JDA error
     * @throws net.dv8tion.jda.api.exceptions.InsufficientPermissionException –
     * If the currently logged in account does not have the Permission VOICE_CONNECT
     * If the currently logged in account does not have the Permission VOICE_MOVE_OTHERS and the user limit has been exceeded!
     */
    public Player(AudioChannelUnion channel){
        this.guild = channel.getGuild();
        this.channel = channel;
        this.player = Main.PLAYER_MANAGER.createPlayer();
        this.schedule = new TrackSchedule();
        player.addListener(schedule);
        guild.getAudioManager().openAudioConnection(channel);
        guild.getAudioManager().setSendingHandler(new MusicHandler(player));
        this.queue = new LinkedBlockingQueue<>();
        channel.asGuildMessageChannel().sendMessage("test").queue();;

    }

    public void play(String song){
        System.out.println(song);
        Main.PLAYER_MANAGER.loadItem(song,new StandardResultLoader(queue, player));
    }

    public void play(URI song){
        if(Main.getAllowedHosts().contains(song.getHost())){

        }else{
            throw new BadHostException();
        }
    }

    public AudioChannelUnion getChannel(){
        return channel;
    }

    public void updateChannel(AudioChannelUnion channel){
        this.guild.getAudioManager().openAudioConnection(channel);
        this.channel = channel;
    }

    public void playNow(String song){

    }

    private class TrackSchedule extends AudioEventAdapter {
        @Override
        public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
            player.playTrack(queue.poll());
        }

    }

}
