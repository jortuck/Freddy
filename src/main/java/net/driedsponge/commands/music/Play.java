package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.SpotifyLookup;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.apache.hc.core5.http.ParseException;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class Play extends SlashCommand {

    public Play() {
        super(new String[]{"playskip", "play"});
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("play") || event.getName().equals("playskip")) {

            // Join if not connected, check for perms
            if (!event.getMember().getVoiceState().inAudioChannel()) {
                event.reply("You must be in a voice channel to play a song!").setEphemeral(true).queue();
                return;
            }

            VoiceChannel voiceChannel = event.getMember().getVoiceState().getChannel().asVoiceChannel();
            String arg = event.getOptions().getFirst().getAsString();

            // Check for valid YouTube links, if they did not send an url, search the term on YouTube.
            try {
                VoiceController vc = getOrCreateVc(event.getGuild(), voiceChannel, event.getChannel().asTextChannel());
                if (isURL(arg)) {
                    URI u = new URI(arg);
                    try {
                        if (u.getHost().equals("youtube.com") || u.getHost().equals("www.youtube.com") || u.getHost().equals("youtu.be") || u.getHost().equals("music.youtube.com")) {
                            event.deferReply().queue();
                            vc.play(u.toString(), event, event.getName().equals("playskip"));
                        } else if (u.getHost().equals("open.spotify.com")) {
                            String[] paths = u.getPath().split("/", 3);
                            if (paths[1].equals("playlist")) {
                                if (paths[2] != null) {
                                    event.deferReply().queue();
                                    SpotifyLookup.loadPlayList(paths[2], event, vc);
                                } else {
                                    event.reply("Invalid Spotify playlist!").setEphemeral(true).queue();
                                }
                            } else {
                                event.reply("Invalid Spotify link!").setEphemeral(true).queue();
                            }
                        } else {
                            event.reply("The URL you send must be a valid YouTube or Spotify link. **Tip: You can also just search the name of your song!**").setEphemeral(true).queue();
                        }
                    } catch (IOException | ParseException e) {
                        event.reply("Sorry, there was an error playing your song. Please try again later.").setEphemeral(true).queue();
                    } catch (SpotifyWebApiException e) {
                        event.reply("That spotify playlist could not be found. Make sure it's a valid **public** playlist.").setEphemeral(true).queue();
                    }
                } else {
                    event.deferReply().queue();
                    event.getHook().sendMessage(":mag: Searching for **" + arg + "**...").queue();
                    vc.play("ytsearch:" + arg, event, event.getName().equals("playskip"));
                }
            } catch (Exception e) {
                event.replyEmbeds(badPermissions(e.getMessage()).build()).queue();
            }
        }
    }

    private boolean isURL(String url){
        try {
            URI u = new URI(url);
            if(u.isAbsolute()){
                return true;
            }
        } catch (URISyntaxException e){
            return false;
        }
        return  false;
    }

    private EmbedBuilder badPermissions(String msg) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Error: Insufficient Permissions");
        embed.setDescription(msg);
        embed.setColor(Color.RED);
        return embed;
    }


    /**
     * Check for existing voice controller, if none then create
     */
    private VoiceController getOrCreateVc(Guild guild, VoiceChannel voiceChannel, TextChannel textChannel) throws Exception {
        if (guild.getSelfMember().hasPermission(voiceChannel, Permission.VOICE_CONNECT, Permission.VOICE_SPEAK, Permission.VIEW_CHANNEL)) {
            if (PlayerStore.hasController(guild.getIdLong())) {
                return PlayerStore.get(guild.getIdLong());
            }
            VoiceController vc = new VoiceController(guild, voiceChannel, textChannel);
            PlayerStore.store(guild, vc);
            return vc;
        } else {
            throw new Exception("It looks like I don't have enough permissions to enter the call. I would love to play music for you, but please make sure I can join! I am missing the `VOICE_CONNECT` permission.");
        }

    }

}
