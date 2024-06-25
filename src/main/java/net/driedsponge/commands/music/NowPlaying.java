package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Main;
import net.driedsponge.Player;
import net.driedsponge.QueuedSong;
import net.driedsponge.buttons.SkipButton;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

import java.util.Objects;

public final class NowPlaying implements SlashCommand {

    public static final NowPlaying INSTANCE = new NowPlaying();

    private NowPlaying(){}

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Main.PRIMARY_COLOR);
        if (Player.contains(Objects.requireNonNull(event.getGuild()).getId()) && Player.get(event.getGuild().getId()).getNowPlaying() != null) {
            Player player = Player.get(event.getGuild().getId());
            QueuedSong np = player.getNowPlaying();
            String title = String.format("Now Playing in %s", player.getVoiceChannel().getName());
            event.replyEmbeds(Embeds.songCard(title, np)
                            .addField("Time Left", Embeds.duration(np.getTrack().getDuration() - np.getTrack().getPosition()), true)
                            .build())
                    .addActionRow(SkipButton.SKIP_BUTTON)
                    .queue();
        } else {
            embedBuilder.setTitle("Nothing is playing.");
            event.replyEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("np", "Shows the song that is currently playing")
                .setGuildOnly(true)};
    }
}
