package net.driedsponge.commands;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Pause extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event){
        if(event.getName().equals("pause")){
            event.deferReply().queue();
            AudioManager manager = event.getGuild().getAudioManager();
            Member member = event.getMember();
            GuildVoiceState state = member.getVoiceState();
            if(manager.isConnected()){
                if(state.inVoiceChannel() && state.getChannel() == manager.getConnectedChannel()){
                    Play.PLAYERS.get(event.getGuild()).setPaused(true);
                }else{
                    event.getHook().sendMessage("You must be in the same channel as me to pause.").queue();
                }
            }else{
                event.getHook().sendMessage("I am not connected to any voice channel").queue();
            }
        }
    }
}
