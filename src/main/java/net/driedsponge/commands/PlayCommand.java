package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.driedsponge.MusicHandler;
import net.driedsponge.TrackScheduler;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class PlayCommand extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event){
        if(event.getName().equals("play")){
            event.deferReply().queue();
            AudioManager audioManager = event.getGuild().getAudioManager();

            // Join if not connected
            if(!audioManager.isConnected()){
                if(!event.getMember().getVoiceState().inVoiceChannel()){
                    event.getHook().sendMessage("You must be in a voice channel to play a song!").queue();
                    return;
                }else{
                    audioManager.openAudioConnection(event.getMember().getVoiceState().getChannel());
                }
            }

            AudioPlayerManager playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(playerManager);

            AudioPlayer player = playerManager.createPlayer();

            audioManager.setSendingHandler(new MusicHandler(player));
            TrackScheduler trackScheduler = new TrackScheduler();
            player.addListener(trackScheduler);

            playerManager.loadItem(event.getOptions().get(0).getAsString(), new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    player.playTrack(track);
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    for (AudioTrack track : playlist.getTracks()) {
                       trackScheduler.queue(track);
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

            event.getHook().sendMessage("Playing a song!").queue();
        }
    }
}
