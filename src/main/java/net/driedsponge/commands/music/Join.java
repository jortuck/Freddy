package net.driedsponge.commands.music;

import net.driedsponge.Main;
import net.driedsponge.Player;
import net.driedsponge.PlayerStore;
import net.driedsponge.VoiceController;
import net.driedsponge.actions.VoiceChannelActions;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.managers.AudioManager;

public class Join extends SlashCommand {

    public Join() {
        super("Join");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        join(event);
    }

    private void join(SlashCommandInteractionEvent event) {
        AudioManager audioManager = event.getGuild().getAudioManager();
        if (event.getMember().getVoiceState().inAudioChannel()) {
            try {
                Player.PLAYERS.put(event.getGuild().getId(),
                        new Player(event.getMember().getVoiceState().getChannel()));
                event.reply("Joining your channel").queue();
            }catch (Exception e){
                event.reply(e.getMessage()).queue();
            }
        } else {
            event.reply("You must be in a call for me to join you!").queue();
        }
    }
}
