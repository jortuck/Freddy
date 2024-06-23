package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.Main;
import net.driedsponge.Player;
import net.driedsponge.QueueResponse;
import net.driedsponge.QueuedSong;
import net.driedsponge.buttons.NextPageButton;
import net.driedsponge.buttons.PreviousPageButton;
import net.driedsponge.buttons.ShuffleButton;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;

import java.util.List;

public final class Queue implements SlashCommand {

    public static final Queue INSTANCE = new Queue();

    private Queue(){}

    public static QueueResponse qResponse(Guild guild, int page) {
        if (!Player.contains(guild.getId())) {
            throw new IllegalStateException("The bot is not playing anything.");
        }
        int songsPerPage = 10;
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Queue - Page " + page);
        embedBuilder.setColor(Main.PRIMARY_COLOR);

        Player player = Player.get(guild.getId());
        if (player.getNowPlaying() == null) {
            throw new IllegalStateException("The bot is not playing anything.");
        }
        AudioTrackInfo np = player.getNowPlaying().getInfo();
        List<QueuedSong> songs = player.getQueue();
        int songCount = songs.size();
        int pages = (songCount + songsPerPage - 1) / songsPerPage;
        if ((page > pages && pages > 0) || page == 0) {
            throw new IndexOutOfBoundsException("Invalid queue page! Please enter a page between 1 and " + pages + "!");
        }

        StringBuilder queue = new StringBuilder();

        queue.append("**Now Playing - ").append(np.title).append("**");
        queue.append("\n");
        queue.append("\n**Up Next:**");

        int loopStart = ((page * songsPerPage) - songsPerPage);
        int loopEnd = Math.min(page * songsPerPage, songCount);
        if (songs.isEmpty()) {
            embedBuilder.setTitle("Queue");
            queue.append(" No songs in the queue!");
        } else {
            for (int i = loopStart; i < loopEnd; i++) {
                QueuedSong song = (QueuedSong) songs.toArray()[i];
                queue.append("\n").append(i + 1)
                        .append(" - ")
                        .append("[")
                        .append(song.getInfo().title)
                        .append("](" + song.getInfo().uri + ")")
                        .append(" `(Requested by: " + song.getRequester().getUser().getName() + ")`");
            }
            embedBuilder.setFooter("Page " + page + " of " + pages + " | Showing song(s) " + (loopStart + 1) + "-" + (loopEnd) + " of " + (songCount) + ".");
        }
        embedBuilder.setDescription(queue);
        return new QueueResponse(embedBuilder.build(), player.getQueue().isEmpty(), 1, pages, page);
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.deferReply().queue();


        if (Player.contains(event.getGuild().getId())) {
            int page = 1;

            try {
                page = Math.abs(event.getInteraction().getOption("page").getAsInt());
            } catch (NullPointerException ignored) {
            }
            try {
                QueueResponse qresponse = qResponse(event.getGuild(), page);
                WebhookMessageCreateAction<Message> response = event.getHook().sendMessageEmbeds(qresponse.embed());
                if (!qresponse.empty()) {
                    // Add once discord decides to support number inputs in modals.
                    //response.addActionRow(ShuffleButton.SHUFFLE_BUTTON,RemoveSongButton.REMOVE_BUTTON);
                    response.addActionRow(ShuffleButton.SHUFFLE_BUTTON,
                            PreviousPageButton.PREVIOUS_PAGE_BUTTON.withId("PP" + page)
                                    .withDisabled(page == qresponse.firstPage())
                            ,
                            NextPageButton.NEXT_PAGE_BUTTON.withId("NP" + page)
                                    .withDisabled(page == qresponse.lastPage() || qresponse.lastPage() == 0)
                    );
                }
                response.queue();
            } catch (Exception e) {
                event.getHook().sendMessage(e.getMessage()).setEphemeral(true).queue();
            }
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Main.PRIMARY_COLOR);
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }

    @Override
    public SlashCommandData[] getCommand() {
        return new SlashCommandData[]{Commands.slash("queue", "Returns the songs in the queue.")
                .addOptions(new OptionData(OptionType.INTEGER, "page", "View other pages of the queue (if there are more than 10 songs).").setMinValue(1).setRequired(false))
                .setGuildOnly(true)};
    }
}
