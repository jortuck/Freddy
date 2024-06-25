package net.driedsponge.commands.music;

import net.driedsponge.Embeds;
import net.driedsponge.Player;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;

public final class Shuffle implements SlashCommand {

    public static final Shuffle INSTANCE = new Shuffle();

    private Shuffle() {}

    /**
     * Shuffles the queue from the perspective of a user interaction.
     *
     * @param member The person who shuffled the queue.
     * @param guild  The guild where to shuffle the queue.
     * @throws IllegalStateException If the queue is empty or the user is not in the call.
     */
    public static void shuffle(Member member, Guild guild) throws IllegalStateException {
        Player player = Player.get(guild.getId());
        if (player != null) {
            if (player.getVoiceChannel().asVoiceChannel() == member.getVoiceState().getChannel()) {
                player.shuffle();
            } else {
                throw new IllegalStateException("You must be in the same channel as me to shuffle the queue!");
            }
        } else {
            throw new IllegalStateException("There is nothing playing right now.");
        }
    }

    public static String replyMessage(Member member) {
        return member.getAsMention() + " shuffled the queue!";
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        try {
            shuffle(event.getMember(), event.getGuild());
            event.replyEmbeds(Embeds.basic("The queue has been shuffled!").setFooter(
                    "Shuffled by " + event.getMember().getEffectiveName(),
                    event.getMember().getEffectiveAvatarUrl()
            ).build()).queue();
        } catch (Exception e) {
            event.reply(e.getMessage()).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("shuffle", "Shuffles the songs in the queue.")
                .setGuildOnly(true)};
    }
}
