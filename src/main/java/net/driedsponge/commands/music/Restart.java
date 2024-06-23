package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Restart implements SlashCommand {

    public static final Restart INSTANCE = new Restart();

    private Restart() {}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        Player player = Player.get(event.getGuild().getId());
        if (player != null) {
            if (player.getVoiceChannel().asVoiceChannel() == event.getMember().getVoiceState().getChannel()) {
                player.seek(0L);
                event.replyEmbeds(Embeds.basic(
                        ":arrows_counterclockwise: Now playing " + player.getNowPlaying().getInfo().title + " from the beginning!"
                ).build()).queue();
            } else {
                event.reply("You must be in the same channel as me to restart the song!").setEphemeral(true).queue();
            }
        } else {
            event.reply("There is nothing playing right now.").setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("restart", "Restarts the song that is currently playing.")
                .setGuildOnly(true)};
    }
}
