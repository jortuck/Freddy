package net.driedsponge.commands.music;

import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.actions.VoiceChannelActions;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public class Leave extends SlashCommand {

    public Leave() {
        super("leave");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
            leave(event);
    }

    private void leave(SlashCommandInteractionEvent event){
        AudioManager audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
        if(audioManager.isConnected()){
            if(audioManager.getConnectedChannel() == event.getMember().getVoiceState().getChannel()){
                event.reply("Leaving!").queue();
                VoiceChannelActions.leave(audioManager);
            }else{
                event.reply("You need to be in the same voice call as me!")
                        .setEphemeral(true).queue();
            }
        }else{
            event.reply("I am not in any call.").setEphemeral(true).queue();
        }

    }
}
