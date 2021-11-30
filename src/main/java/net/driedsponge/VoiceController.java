package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.managers.AudioManager;

import javax.annotation.Nullable;
import java.awt.*;

public class VoiceController {
    private Guild guild;
    private VoiceChannel channel;
    private AudioManager audioManager;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private Song nowPlaying;
    private MessageChannel msgChannel;

    public VoiceController(Guild guild, VoiceChannel channel, MessageChannel message){
        this.guild = guild;
        this.channel = channel;
        this.msgChannel = message;
        this.audioManager = guild.getAudioManager();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        this.playerManager = playerManager;

        AudioSourceManagers.registerRemoteSources(playerManager);

        AudioPlayer player = playerManager.createPlayer();
        this.player = player;
        audioManager.setSendingHandler(new MusicHandler(player));
        TrackScheduler trackScheduler = new TrackScheduler(this);
        this.trackScheduler = trackScheduler;
        player.addListener(trackScheduler);

    }

    public void setNowPlaying(Song nowPlaying) {
        this.nowPlaying = nowPlaying;
    }

    public void join(){
        try {
            guild.getAudioManager().openAudioConnection(channel);
        } catch (PermissionException e){
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("An error occured when trying to join the call!");
            embed.setDescription("**Missing permission: `"+e.getPermission().getName()+"`**");
            embed.setColor(Color.RED);
            this.getMsgChannel().sendMessageEmbeds(embed.build()).queue();
        }

    }

    public void play(String song, SlashCommandEvent event, boolean now){
        playerManager.loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                Song song = new Song(track, event);
                if(nowPlaying == null){
                    event.getHook().sendMessageEmbeds(TrackScheduler.songCard("Now Playing",song).build()).queue();
                    trackScheduler.queue(song);
                    nowPlaying=song;
                }else{
                    trackScheduler.queue(song);
                }

            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    Song song = new Song(track, event);
                    trackScheduler.queue(song);
                }
            }

            @Override
            public void noMatches() {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("We could not find that song!");
                embed.setColor(Color.RED);
                embed.setDescription("**At this moment, the bot does not support YouTube searching. If you want to play a song from YouTube, you must manually paste the link.**");
                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.getHook().sendMessage("That song failed to load. I don't know why.").queue();
            }
        });
    }

    public void skip(){

        this.trackScheduler.startNewTrack();
    }

    public Song getNowPlaying() {
        return nowPlaying;
    }

    public VoiceChannel getChannel() {
        return channel;
    }

    public MessageChannel getMsgChannel() {
        return msgChannel;
    }

    public void leave(){
        guild.getAudioManager().closeAudioConnection();
        this.nowPlaying = null;
        player.destroy();
    }

    public TrackScheduler getTrackScheduler() {
        return trackScheduler;
    }

    public Guild getGuild() {
        return guild;
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
