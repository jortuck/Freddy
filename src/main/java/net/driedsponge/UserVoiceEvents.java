package net.driedsponge;

import net.dv8tion.jda.api.events.guild.voice.*;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class UserVoiceEvents extends ListenerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(UserVoiceEvents.class);

    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent event) {
        if (event.getEntity().getJDA().getSelfUser().getId().equals(event.getEntity().getId())) {
            if (!(event.getChannelLeft() != null && event.getChannelJoined() != null)) {
                if (event.getChannelJoined() != null) {
                    logger.info("Connected to {} in {} ({})",
                            event.getChannelJoined().getName(),
                            event.getGuild().getName(),
                            event.getGuild().getId());
                } else {
                    logger.info("Disconnected from {} in {} ({})",
                            event.getChannelLeft().getName(),
                            event.getGuild().getName(),
                            event.getGuild().getId());
                    Player.destroy(event.getGuild().getId());
                }
            } else {
                logger.info("Moving from from {} to {} in {} ({})",
                        event.getChannelLeft().getName(),
                        event.getChannelJoined().getName(),
                        event.getGuild().getName(),
                        event.getGuild().getId());
                if (event.getChannelJoined().getMembers().size() == 1) {
                    event.getGuild().getAudioManager().closeAudioConnection();
                }else{
                    Player.get(event.getGuild().getId()).updateChannel(event.getChannelJoined());
                }
            }
        }else if(event.getGuild().getAudioManager().isConnected()){
            AudioManager manager = event.getGuild().getAudioManager();
            if(event.getChannelLeft() == manager.getConnectedChannel()){
                if(event.getChannelLeft().getMembers().size() == 1){
                   manager.closeAudioConnection();
                }
            }
        }
    }
}
