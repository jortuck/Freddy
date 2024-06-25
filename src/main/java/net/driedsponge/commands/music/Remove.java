package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.QueuedSong;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Remove implements SlashCommand {

    public static final Remove INSTANCE = new Remove();

    private Remove(){}

    /**
     * Skips the current song.
     *
     * @param member   Who is removing the song?
     * @param guild    Where the song is being removed?
     * @param position The current position in the queue of the song to be removed.
     * @return The song removed from the queue.
     * @throws IllegalStateException If the person is not in a valid
     */
    public static QueuedSong remove(Member member, Guild guild, int position) {
        Player player = Player.get(guild.getId());
        if (player != null) {
            if (player.getVoiceChannel().asVoiceChannel() == member.getVoiceState().getChannel()) {
                return player.remove(position - 1); // -1 because a user will probably ask to remove index 1, not 0.
            } else {
                throw new IllegalStateException("You must be in the same channel as me to shuffle the queue!");
            }
        } else {
            throw new IllegalStateException("There is nothing playing right now.");
        }
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        try {
            int skipAmount = 1;
            try {
                skipAmount = Math.abs(event.getInteraction().getOption("position").getAsInt());
            } catch (Exception e) {
                throw new IllegalArgumentException("That is an invalid number!");
            }
            QueuedSong remove = remove(event.getMember(), event.getGuild(), skipAmount);
            event.replyEmbeds(
                    Embeds.basic(":wastebasket: Removed " + remove.getInfo().title + " from the queue!").build()
            ).queue();
        } catch (IllegalStateException | IllegalArgumentException e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("remove", "Removes a selected song from the queue.")
                .addOptions(new OptionData(OptionType.INTEGER, "position", "The current queue position of the song you want to remove.").setMinValue(1).setRequired(true))
                .setGuildOnly(true)};
    }
}
