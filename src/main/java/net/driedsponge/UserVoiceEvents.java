package net.driedsponge;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import net.driedsponge.commands.Play;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.DisconnectEvent;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class UserVoiceEvents extends ListenerAdapter {
    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event){
        // Automatically disconnect if no one else is in the call
        if(event.getGuild().getAudioManager().isConnected()){
            if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1){
                Play.PLAYERS.get(event.getGuild()).leave();
            }
        }
    }
    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event){
        // Deafen self when joining call.
        if(event.getMember().getUser() == event.getJDA().getSelfUser()){
           if(!event.getMember().getVoiceState().isGuildDeafened()){
               selfDeafen(event);
           }
        }
    }

    @Override
    public void onGuildVoiceGuildDeafen(GuildVoiceGuildDeafenEvent event){
        if(event.getMember().getUser() == event.getJDA().getSelfUser()){
            if(!event.isGuildDeafened()){
                selfDeafen(event);
            }
        }
    }

    /**
     * Trys to make the bot deafen itself
     * @param event Event that provides context
     */
    private void selfDeafen(GenericGuildVoiceEvent event){
        if(event.getMember().hasPermission(Permission.VOICE_DEAF_OTHERS)){
            event.getMember().deafen(true).queue();
        }else{
            VoiceController vc = Play.PLAYERS.get(event.getGuild());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Please give me permission to deafen!");
            embed.setDescription("Please give me permission to deafen so I can defean myself." +
                    " This will help save my resources. **You can also manually sever deafen me.**");
            vc.getMsgChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
