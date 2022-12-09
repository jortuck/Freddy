package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.driedsponge.PlayerStore;
import net.driedsponge.SpotifyLookup;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.managers.AudioManager;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class Play extends SlashCommand {

    public AudioTrack current = null;

    public Play(){
        super(new String[]{"playskip","play"});
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("play") || event.getName().equals("playskip")) {

            AudioManager audioManager = event.getGuild().getAudioManager();

            // Join if not connected, check for perms
                if (!event.getMember().getVoiceState().inAudioChannel()) {
                    event.reply("You must be in a voice channel to play a song!").setEphemeral(true).queue();
                    return;
                }



            VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
            String arg = event.getOptions().get(0).getAsString();
            String url;

            // Check for valid YouTube links, if they did not send a url, search the term on YouTube.
            try {
                URL u = new URL(arg);
                if (u.getHost().equals("youtube.com") || u.getHost().equals("www.youtube.com") || u.getHost().equals("youtu.be")|| u.getHost().equals("music.youtube.com")) {
                    event.deferReply().queue();
                    url = u.toString();
                    try {
                        VoiceController vc = getOrCreateVc(event.getGuild(),voiceChannel,event.getChannel().asTextChannel());
                        vc.play(url, event, event.getName().equals("playskip"));
                    }catch (Exception e){
                        EmbedBuilder embed = new EmbedBuilder();
                        embed.setTitle("Error: Insufficient Permissions");
                        embed.setDescription(e.getMessage());
                        embed.setColor(Color.RED);
                        event.getHook().sendMessageEmbeds(embed.build()).queue();
                    }
                } else if(u.getHost().equals("open.spotify.com")){
                    String[] paths = u.getPath().split("/",3);
                    if(paths[1].equals("playlist")){
                        if(paths[2] != null){
                            SpotifyLookup.loadPlayList(paths[2],event);
                        }else{
                            event.reply("Invalid Spotify playlist!").setEphemeral(true).queue();
                        }
                    }else {
                        event.reply("Invalid Spotify link!").setEphemeral(true).queue();
                    }
                } else {
                    event.reply("The URL you send must be a valid YouTube or Spotify link. **Tip: You can also just search the name of your song!**").setEphemeral(true).queue();
                }
            } catch (MalformedURLException exception) {
                event.deferReply().queue();
                event.getHook().sendMessage(":mag: Searching for **"+arg+"**...").queue();
                try {
                    VoiceController vc = getOrCreateVc(event.getGuild(),voiceChannel,event.getChannel().asTextChannel());
                    vc.play("ytsearch:"+arg, event,  event.getName().equals("playskip"));
                }catch (Exception e){
                    EmbedBuilder embed = new EmbedBuilder();
                    embed.setTitle("Error: Insufficient Permissions");
                    embed.setDescription(e.getMessage());
                    embed.setColor(Color.RED);
                    event.getHook().sendMessageEmbeds(embed.build()).queue();
                }
            } catch (IOException | ParseException e) {
                event.reply("Sorry, there was an error playing your song. Please try again later.").setEphemeral(true).queue();
            } catch (SpotifyWebApiException e){
                event.reply("That spotify playlist could not be found. Make sure it's a valid **public** playlist.").setEphemeral(true).queue();
            }


        }
    }

    /**
     * Check for existing voice controller, if none then create
     * @param guild
     * @param voiceChannel
     * @param textChannel
     * @return
     * @throws Exception
     */
    private VoiceController getOrCreateVc(Guild guild, VoiceChannel voiceChannel, TextChannel textChannel) throws Exception{
        // If already exist
        if (PlayerStore.get(guild.getIdLong()) != null) {
            return PlayerStore.get(guild.getIdLong());
        }
        try {
            VoiceController vc = new VoiceController(guild, voiceChannel, textChannel);
            vc.join();
            PlayerStore.store(guild, vc);
            return vc;
        }   catch (PermissionException e) {
            throw new Exception("It looks like I don't have enough permissions to enter the call. I would love to play music for you, but please make sure I can join! I am missing the `"+e.getPermission().getName()+"` permission.");
        }
    }
}
