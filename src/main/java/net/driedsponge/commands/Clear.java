package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Clear extends GuildCommand{
    public Clear(){
        super("clear");
    }
    @Override
    public void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        AudioManager manager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        if (manager.isConnected()) {
            if (state.inVoiceChannel() && state.getChannel() == manager.getConnectedChannel()) {
                VoiceController vc = PlayerStore.get(event.getGuild().getIdLong());
                vc.getTrackScheduler().getQueue().clear();
                event.getHook().sendMessage("The queue has been cleared!").queue();
            } else {
                event.getHook().sendMessage("You must be in the same channel as me to clear the queue.").queue();
            }
        } else {
            event.getHook().sendMessage("I am not connected to any voice channel").queue();
        }
    }
}
