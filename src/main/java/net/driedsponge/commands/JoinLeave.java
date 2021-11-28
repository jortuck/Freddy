package net.driedsponge.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinLeave extends ListenerAdapter {
    @Override
    public void onSlashCommand(SlashCommandEvent event) {
        if (event.getName().equals("join")) {
            event.deferReply().queue();
            if(event.getMember().getVoiceState().inVoiceChannel()){
                VoiceChannel channel = event.getMember().getVoiceState().getChannel();
                event.getHook().sendMessage("Connecting to "+channel.getName()+"...").queue();
                AudioManager audioManager = event.getGuild().getAudioManager();
                audioManager.openAudioConnection(channel);
            }else{
                event.getHook().sendMessage("You need to be in a voice channel!").queue();
            }
        }else if(event.getName().equals("leave")){
            leave(event);
        }
    }

    private void leave(SlashCommandEvent event){
        AudioManager audioManager = event.getGuild().getAudioManager();
        if(audioManager.isConnected()){
            VoiceChannel channel = audioManager.getConnectedChannel();
            Member member = event.getMember();
            if(member.getVoiceState().inVoiceChannel() && member.getVoiceState().getChannel() == channel || member.hasPermission(Permission.MANAGE_CHANNEL)) {
                Play.PLAYERS.get(event.getGuild()).leave();
                event.reply("Leaving "+channel.getName()+". Goodbye :wave:").queue();
            }else{
                event.reply("You must have the **MANAGE_CHANNEL** permission to use this command or you must be currently connect to "+channel.getName()+".");
            }
        }

    }
}
