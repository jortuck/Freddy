package net.driedsponge.commands.music;

import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public final class Skip extends SlashCommand {
    public Skip() {
        super("skip");
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
            event.reply(skip).setEphemeral(true).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    /**
     * Skips the current song.
     *
     * @param member The person who is skipping the song.
     * @param guild  The guild where the song is being skipped
     * @param amount The amount of songs to skip.
     * @return
     * @throws Exception
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
}
