package net.driedsponge;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class UserVoiceEvents extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        if(event.getChannelLeft() !=null && event.getMember().getUser() == event.getJDA().getSelfUser() && PlayerStore.get(event.getGuild()) != null){
            PlayerStore.get(event.getGuild()).leave();
        } else if (event.getGuild().getAudioManager().isConnected() && event.getGuild().getAudioManager().getConnectedChannel().getMembers().size() == 1){
            PlayerStore.get(event.getGuild()).leave();
        }
    }
}
