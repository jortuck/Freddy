package net.driedsponge.commands;

import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class JoinLeave extends GuildCommand {

    public JoinLeave() {
        super(new String[]{"join", "leave"});
    }

    @Override
    public void execute(SlashCommandEvent event) {
        if (event.getName().equals("join")) {
            event.reply("This command is no longer supported. Please use `/play [song]`.").setEphemeral(true).queue();
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
                Play.PLAYERS.remove(event.getGuild());
                event.reply("Leaving "+channel.getName()+". Goodbye :wave:").queue();
            }else{
                event.reply("You must have the **MANAGE_CHANNEL** permission to use this command or you must be currently connected to "+channel.getName()+".").queue();
            }
        }

    }
}
