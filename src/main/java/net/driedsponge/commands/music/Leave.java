package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.managers.AudioManager;

import java.util.Objects;

public final class Leave implements SlashCommand {
    public static final Leave INSTANCE = new Leave();

    private Leave(){}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        leave(event);
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("leave", "Tells the bot to leave the current voice channel.").setGuildOnly(true)};
    }

    private void leave(SlashCommandInteractionEvent event) {
        AudioManager audioManager = Objects.requireNonNull(event.getGuild()).getAudioManager();
        if (audioManager.isConnected()) {
            if (audioManager.getConnectedChannel() == event.getMember().getVoiceState().getChannel()) {
                event.replyEmbeds(Embeds.basic("Leaving!").build()).queue();
                audioManager.closeAudioConnection();
            } else {
                event.reply("You need to be in the same voice call as me!")
                        .setEphemeral(true).queue();
            }
        } else {
            event.reply("I am not in any call.").setEphemeral(true).queue();
        }

    }
}
