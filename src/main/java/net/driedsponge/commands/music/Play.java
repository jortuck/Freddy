package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import net.driedsponge.Player;
import net.driedsponge.PlayerStore;
import net.driedsponge.SpotifyLookup;
import net.driedsponge.VoiceController;
import net.driedsponge.actions.VoiceChannelActions;
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
            if(event.getMember().getVoiceState().inAudioChannel()){
                String arg = event.getOptions().getFirst().getAsString();
                if(!Player.PLAYERS.containsKey(event.getGuild().getId())){
                    Player.PLAYERS.put(event.getGuild().getId(),new Player(event.getMember()
                            .getVoiceState().getChannel()));
                }
                Player player = Player.PLAYERS.get(event.getGuild().getId());
                if(!player.getChannel().asVoiceChannel().getId()
                        .equals(event.getMember().getVoiceState().getChannel().asVoiceChannel().getId())){
                    player.updateChannel(event.getMember().getVoiceState().getChannel());
                }
                player.play("ytsearch:"+arg);
                event.reply("playing song").queue();
            }else{
                event.reply("You must be a voice channel for me to play music for you!")
                        .setEphemeral(true).queue();
            }
        }
    }
}
