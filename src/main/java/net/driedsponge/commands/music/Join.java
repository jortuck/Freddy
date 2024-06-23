package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Join implements SlashCommand {

    public static final Join INSTANCE = new Join();

    private Join(){}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        join(event);
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("join", "Tells the bot to join the current voice channel.").setGuildOnly(true)};
    }

    private void join(SlashCommandInteractionEvent event) {
        if (event.getMember().getVoiceState().inAudioChannel()) {
            try {
                Player.createPlayer(event.getMember().getVoiceState().getChannel());
                event.replyEmbeds(
                        Embeds.basic("Joining " + event.getMember().getVoiceState().getChannel().getName()).build()
                ).queue();
            } catch (Exception e) {
                event.reply(e.getMessage()).queue();
            }
        } else {
            event.reply("You must be in a call for me to join you!").queue();
        }
    }
}
