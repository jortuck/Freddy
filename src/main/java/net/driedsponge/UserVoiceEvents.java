package net.driedsponge;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class UserVoiceEvents extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        if(event.getChannelLeft() !=null && event.getMember().getUser() == event.getJDA().getSelfUser()){
            PlayerStore.get(event.getGuild()).leave();
            return;
        }
        // Automatically disconnect if no one else is in the call
        if(event.getGuild().getAudioManager().isConnected()){
            if(event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1){
                PlayerStore.get(event.getGuild()).leave();
                return;
            }
        }


        // Self deafen
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
            VoiceController vc = PlayerStore.get(event.getGuild());
            EmbedBuilder embed = new EmbedBuilder();
            embed.setColor(Color.RED);
            embed.setTitle("Please give me permission to deafen!");
            embed.setDescription("Please give me permission to deafen so I can deafean myself." +
                    " This will help save my resources. **You can also manually sever deafen me.**");
            vc.getTextChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }
}
