package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.managers.AudioManager;

import java.awt.*;

public class JoinLeave extends SlashCommand {

    public JoinLeave() {
        super(new String[]{"join", "leave"});
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        if (event.getName().equals("join")) {
            event.reply("This command is no longer supported. Please use `/play [song]`.").setEphemeral(true).queue();
        }else if(event.getName().equals("leave")){
            leave(event);
        }
    }

    private void leave(SlashCommandInteractionEvent event){
        AudioManager audioManager = event.getGuild().getAudioManager();
        if(audioManager.isConnected()){
            VoiceChannel channel = audioManager.getConnectedChannel().asVoiceChannel();
            Member member = event.getMember();
            if(member.getVoiceState().inAudioChannel() && member.getVoiceState().getChannel() == channel || member.hasPermission(Permission.MANAGE_CHANNEL)) {
                VoiceController vc = PlayerStore.get(event.getGuild());
                EmbedBuilder embedBuilder = new EmbedBuilder()
                        .setColor(Color.CYAN)
                        .setTitle(String.format(":wave: Leaving %s! Goodbye!",vc.getVoiceChannel().getName()));
                vc.leave();
                PlayerStore.remove(event.getGuild());
                event.replyEmbeds(embedBuilder.build()).queue();
            }else{
                event.reply("You must have the **MANAGE_CHANNEL** permission to use this command or you must be currently connected to "+channel.getName()+".").queue();
            }
        }

    }
}
