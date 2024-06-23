package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Skip implements SlashCommand {

    public static final Skip INSTANCE = new Skip();

    private Skip() {}

    /**
     * Skips the current song.
     *
     * @param member The person who is skipping the song.
     * @param guild  The guild where the song is being skipped
     * @param amount The amount of songs to skip.
     * @return A message indicating what was skipped.
     * @throws IllegalStateException If the user is not listening to music with the bot.
     */
    public static String skip(Member member, Guild guild, int amount) {
        Player player = Player.get(guild.getId());
        if (player != null && player.getNowPlaying() != null) {
            if (player.getVoiceChannel().asVoiceChannel() == member.getVoiceState().getChannel()) {
                player.skip(amount);
                return "Skipping " + amount + " song(s)...";
            } else {
                throw new IllegalStateException("You must be in the same channel as me to skip!");
            }
        } else {
            throw new IllegalStateException("Nothing to skip.");
        }
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            int skipAmount = 1;
            try {
                skipAmount = Math.abs(event.getInteraction().getOption("position").getAsInt());
            } catch (Exception ignored) {
            }
            String skip = skip(event.getMember(), event.getGuild(), skipAmount);
            event.replyEmbeds(
                    Embeds.basic(skip).build()
            ).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("skip", "Skips the current song.")
                .addOptions(new OptionData(OptionType.INTEGER, "position", "If you wish to skip to a specific number in the queue, enter it here.").setMinValue(1).setRequired(false))
                .setGuildOnly(true)};
    }
}
