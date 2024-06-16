package net.driedsponge;

import net.driedsponge.actions.VoiceChannelActions;
import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class UserVoiceEvents extends ListenerAdapter {
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event){
        if(event.getEntity().getJDA().getSelfUser().getId().equals(event.getEntity().getId())){
            if(!(event.getChannelLeft() != null && event.getChannelJoined() !=null)){
                if (event.getChannelJoined()!=null){
                    System.out.println("Joined");
                }else{
                    System.out.println("Left");
                    VoiceChannelActions.leave(event.getGuild().getAudioManager());
                }
            }else{
                System.out.println("moved");
                if(event.getChannelJoined().getMembers().size()  == 1){
                    VoiceChannelActions.leave(event.getGuild().getAudioManager());
                }
            }
        }
    }
}
