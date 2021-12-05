package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.driedsponge.VoiceController;
import net.driedsponge.YouTubeLookup;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.util.HashMap;

public class Play extends GuildCommand {

    public AudioTrack current = null;
    public static HashMap<Guild, VoiceController> PLAYERS = new HashMap<Guild, VoiceController>();

    public Play(){
        super("play");
    }

    @Override
    public void execute(SlashCommandEvent event) {
        if (event.getName().equals("play")) {
            event.deferReply().queue();
            AudioManager audioManager = event.getGuild().getAudioManager();

            // Join if not connected
            if (!audioManager.isConnected()) {
                if (!event.getMember().getVoiceState().inVoiceChannel()) {
                    event.getHook().sendMessage("You must be in a voice channel to play a song!").setEphemeral(true).queue();
                    return;
                } else {
                    VoiceController vc = new VoiceController(event.getGuild(), event.getMember().getVoiceState().getChannel(), event.getChannel());
                    PLAYERS.putIfAbsent(event.getGuild(), vc);
                    vc.join();
                }
            }
            if (PLAYERS.get(event.getGuild()) == null) {
                VoiceController vc = new VoiceController(event.getGuild(), event.getMember().getVoiceState().getChannel(), event.getChannel());
                PLAYERS.put(vc.getGuild(), vc);
            }

            VoiceController vc = PLAYERS.get(event.getGuild());
            String arg = event.getOptions().get(0).getAsString();
            String url = "";

            // Check for valid YouTube links, if they did not send a url, search the term on YouTube.
            try {
                URL u = new URL(arg);
                if (u.getHost().equals("youtube.com") || u.getHost().equals("www.youtube.com") || u.getHost().equals("youtu.be")) {
                    url = u.toString();
                    vc.play(url, event, false);
                } else if(u.getHost().equals("open.spotify.com")){
                    String[] paths = u.getPath().split("/",3);
                    if(paths[1].equals("playlist")){
                        if(paths[2] != null){
                            event.getHook().sendMessage("Good spotify playlist").queue();
                        }else{
                            event.getHook().sendMessage("Invalid Spotify playlist!").queue();
                        }
                    }else {
                        event.getHook().sendMessage("Invalid Spotify link!").queue();
                    }
                } else {
                    event.getHook().sendMessage("The URL you send must be a valid YouTube link. **Tip: You can also just search the name of your song!**").setEphemeral(true).queue();
                }
            } catch (MalformedURLException exception) {
                try {
                    url = YouTubeLookup.getUrl(arg);
                    vc.play(url, event, false);
                } catch (IOException | GeneralSecurityException | NoSuchFieldException e) {
                    event.getHook().sendMessage("We could not find that video on YouTube.").setEphemeral(true).queue();
                }
            }



        }
    }

}
