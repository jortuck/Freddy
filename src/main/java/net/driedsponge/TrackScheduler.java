package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import net.driedsponge.commands.Play;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import javax.sound.midi.Track;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    public ArrayList<Song> queue2 = new ArrayList<Song>();
    private final BlockingQueue<Song> queue;
    public VoiceController vc;

    public TrackScheduler(VoiceController vc) {
        this.vc = vc;
        this.queue = new LinkedBlockingQueue<>();
    }

    @Override
    public void onPlayerPause(AudioPlayer player) {
        // Player was paused
    }

    @Override
    public void onPlayerResume(AudioPlayer player) {
        // Player was resumed
    }

    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
    }

    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        if(endReason.mayStartNext){
            startNewTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

    }

    public BlockingQueue<Song> getQueue() {
        return queue;
    }

    public void queue(Song song) {
        if (!vc.getPlayer().startTrack(song.getTrack(), true)) {
            queue.offer(song);
            song.getEvent().getHook().sendMessageEmbeds(songCard("Song Added to Queue", song).build()).queue();
        }
    }

    public void queue(AudioPlaylist playlist, SlashCommandEvent event){
        StringBuilder builder = new StringBuilder();
        for (AudioTrack track : playlist.getTracks()) {
            Song song = new Song(track, event);
            if (!vc.getPlayer().startTrack(song.getTrack(), true)) {
                queue.offer(song);
                builder.append("\n")
                        .append("[")
                        .append(song.getInfo().title)
                        .append("]("+song.getInfo().uri+")")
                        .append("\n");
            }else{
                vc.setNowPlaying(song);
            }
        }
        if(!builder.isEmpty()){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("The Following Songs Have Been Added to the Queue.");
            embedBuilder.setDescription(builder.toString());
            embedBuilder.setColor(Color.CYAN);
            embedBuilder.setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }

    public static EmbedBuilder songCard(String title, Song song) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(title);
        embedBuilder.setTitle(song.getInfo().title, song.getInfo().uri);
        embedBuilder.addField("Artist", song.getInfo().author, true);
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setFooter("Requested by " + song.getRequester().getUser().getAsTag(), song.getRequester().getEffectiveAvatarUrl());
        return embedBuilder;
    }

    public void startNewTrack() {
        if (!queue.isEmpty()) {
            Song song = queue.poll();
            this.vc.setNowPlaying(song);
            vc.getPlayer().playTrack(song.getTrack());
            vc.getMsgChannel().sendMessageEmbeds(songCard("Now Playing", song).build()).queue();
        } else {
            Guild guild = vc.getGuild();
            vc.getMsgChannel().sendMessage("No more songs to play. Leaving now!").queue();
            vc.leave();
            Play.PLAYERS.remove(guild);
        }
    }
}
