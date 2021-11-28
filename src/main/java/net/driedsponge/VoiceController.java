package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class VoiceController {
    private Guild guild;
    private VoiceChannel channel;
    private AudioManager audioManager;
    private AudioPlayerManager playerManager;
    private TrackScheduler trackScheduler;
    private AudioPlayer player;
    private AudioTrack nowPlaying;

    public VoiceController(Guild guild, VoiceChannel channel){
        this.guild = guild;
        this.channel = channel;
        this.audioManager = guild.getAudioManager();

        AudioPlayerManager playerManager = new DefaultAudioPlayerManager();

        this.playerManager = playerManager;

        AudioSourceManagers.registerRemoteSources(playerManager);

        AudioPlayer player = playerManager.createPlayer();
        this.player = player;
        audioManager.setSendingHandler(new MusicHandler(player));
        TrackScheduler trackScheduler = new TrackScheduler();
        this.trackScheduler = trackScheduler;
        player.addListener(trackScheduler);

    }

    public void join(){
        guild.getAudioManager().openAudioConnection(channel);
    }

    public void play(String song, SlashCommandEvent event){
        playerManager.loadItem(song, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {
                event.getHook().sendMessage("Now playing **"+track.getInfo().title+"**").queue();
                player.playTrack(track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                for (AudioTrack track : playlist.getTracks()) {
                    trackScheduler.queue(track,event);
                }
            }

            @Override
            public void noMatches() {
                event.getHook().sendMessage("We could not find that song!").queue();
            }

            @Override
            public void loadFailed(FriendlyException throwable) {
                event.getHook().sendMessage("That song failed to load. I don't know why.").queue();
            }
        });
    }

    public AudioPlayer getPlayer() {
        return player;
    }
}
