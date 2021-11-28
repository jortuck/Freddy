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
import net.driedsponge.VoiceController;
import net.driedsponge.TrackScheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.HashMap;

public class Play extends ListenerAdapter {

    public AudioTrack current = null;
    public static HashMap<Guild, VoiceController> PLAYERS = new HashMap<Guild,VoiceController>();

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
                    if(PLAYERS.get(event.getGuild()) == null){
                        PLAYERS.put(event.getGuild(),new VoiceController(event.getGuild(),event.getMember().getVoiceState().getChannel()));
                    }
                    VoiceController vc = PLAYERS.get(event.getGuild());
                    vc.join();
                }
            }
            VoiceController vc = PLAYERS.get(event.getGuild());
            vc.play(event.getOptions().get(0).getAsString(),event);


        }
    }
}
