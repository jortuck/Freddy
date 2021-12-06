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
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class TrackScheduler extends AudioEventAdapter {
    private final BlockingQueue<YouTubeSong> queue;
    public VoiceController vc;
    public static final int QUEUE_LIMIT = 500;

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
        if (endReason.mayStartNext) {
            startNewTrack();
        }
    }

    @Override
    public void onTrackException(AudioPlayer player, AudioTrack track, FriendlyException exception) {

    }

    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {

    }

    public BlockingQueue<YouTubeSong> getQueue() {
        return queue;
    }

    public boolean shuffle() {
        if (this.getQueue().size() > 0) {
            List<Object> songs = Arrays.asList(this.queue.toArray());
            Collections.shuffle(songs);
            this.queue.clear();
            this.queue.addAll((Collection) songs);
            return true;
        } else {
            return false;
        }
    }

    public void queue(YouTubeSong song) {
        if (!vc.getPlayer().startTrack(song.getTrack(), true)) {
            queue.offer(song);
            song.getEvent().getHook().sendMessageEmbeds(songCard("Song Added to Queue", song).build()).queue();
        }
    }

    public void queue(AudioPlaylist playlist, SlashCommandEvent event) {
        int playListSize = playlist.getTracks().size();

        int loopLimit = Math.min(playListSize, QUEUE_LIMIT);

        for (int i = 0; i < loopLimit; i++) {
            AudioTrack track = playlist.getTracks().get(i);
            YouTubeSong song = new YouTubeSong(track, event);
            if (!vc.getPlayer().startTrack(song.getTrack(), true)) {
                queue.offer(song);
            } else {
                vc.setNowPlaying(song);
                vc.getMsgChannel().sendMessageEmbeds(songCard("Now Playing", song).build()).queue();
            }
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Added " + playlist.getTracks().size() + " songs to the Queue from " + playlist.getName() + "!");
        embedBuilder.setDescription("");
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setFooter("Requested by " + event.getUser().getAsTag(), event.getUser().getEffectiveAvatarUrl());

        event.getHook().sendMessageEmbeds(embedBuilder.build())
                .addActionRow(Button.link(event.getOptions().get(0).getAsString(), "Playlist"))
                .queue();
    }

    public static EmbedBuilder songCard(String title, YouTubeSong song) {
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
            YouTubeSong song = queue.poll();
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
