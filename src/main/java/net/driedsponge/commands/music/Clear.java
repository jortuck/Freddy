package net.driedsponge.commands.music;

import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.managers.AudioManager;

public class Clear extends SlashCommand {
    public Clear(){
        super("clear");
    }
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AudioManager manager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        if (manager.isConnected()) {
            if (state.inAudioChannel() && state.getChannel() == manager.getConnectedChannel()) {
                VoiceController vc = PlayerStore.get(event.getGuild().getIdLong());
                vc.getTrackScheduler().getQueue().clear();
                event.reply("The queue has been cleared!").queue();
            } else {
                event.reply("You must be in the same channel as me to clear the queue.").setEphemeral(true).queue();
            }
        } else {
            event.reply("I am not connected to any voice channel").setEphemeral(true).queue();
        }
    }
}
