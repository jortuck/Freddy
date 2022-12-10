package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;import net.dv8tion.jda.api.managers.AudioManager;

public class Pause extends SlashCommand {
    public Pause() {
        super(new String[]{"pause", "resume"});
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        AudioManager manager = event.getGuild().getAudioManager();
        Member member = event.getMember();
        GuildVoiceState state = member.getVoiceState();
        if (manager.isConnected()) {
            if (state.inAudioChannel() && state.getChannel() == manager.getConnectedChannel()) {
                VoiceController vc = PlayerStore.get(event.getGuild().getIdLong());
                AudioPlayer player = vc.getPlayer();
                if (event.getName().equals("resume")) {
                    player.setPaused(false);
                    event.reply("Resuming...").queue();
                } else {
                    event.reply("Pausing...").queue();
                    player.setPaused(true);
                }
            } else {
                event.reply("You must be in the same channel as me to pause/resume.").setEphemeral(true).queue();
            }
        } else {
            event.reply("I am not connected to any voice channel").setEphemeral(true).queue();
        }
    }
}
