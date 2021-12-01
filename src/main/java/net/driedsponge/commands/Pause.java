package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;

public class Pause extends GuildCommand {
    public Pause() {
        super(new String[]{"pause", "resume"});
    }

    @Override
    public void execute(SlashCommandEvent event) {
        event.deferReply().queue();
        AudioManager manager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        if (manager.isConnected()) {
            if (state.inVoiceChannel() && state.getChannel() == manager.getConnectedChannel()) {
                VoiceController vc = Play.PLAYERS.get(event.getGuild());
                AudioPlayer player = vc.getPlayer();
                if (event.getName().equals("resume")) {
                    player.setPaused(false);
                    event.getHook().sendMessage("Resuming...").queue();
                } else {
                    event.getHook().sendMessage("Pausing...").queue();
                    player.setPaused(true);
                }
            } else {
                event.getHook().sendMessage("You must be in the same channel as me to pause/resume.").queue();
            }
        } else {
            event.getHook().sendMessage("I am not connected to any voice channel").queue();
        }
    }
}
