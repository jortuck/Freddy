package net.driedsponge.commands.music;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.driedsponge.Song;
import net.driedsponge.VoiceController;
import net.driedsponge.buttons.ShuffleButton;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class Queue extends SlashCommand {


    public Queue() {
        super("queue");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event) {

        event.deferReply().queue();


        if (event.getGuild().getAudioManager().isConnected() && PlayerStore.get(event.getGuild()) != null) {
            MessageEmbed embed = qEmbed(event.getGuild());
            event.getHook().sendMessageEmbeds(embed)
                    .addActionRow(ShuffleButton.SHUFFLE_BUTTON)
                    .queue();
        } else {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(Main.PRIMARY_COLOR);
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).setEphemeral(true).queue();
        }
    }

    public static MessageEmbed qEmbed(Guild guild) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Queue");
        embedBuilder.setColor(Main.PRIMARY_COLOR);

        VoiceController vc = PlayerStore.get(guild);
        AudioTrackInfo np = vc.getNowPlaying().getInfo();
        BlockingQueue<Song> songs = vc.getTrackScheduler().getQueue();
        embedBuilder.setTitle("Queue");
        StringBuilder queue = new StringBuilder();

        queue.append("**Now Playing - ").append(np.title).append("**");
        queue.append("\n");
        queue.append("\n**Up Next:**");
        int loopLimit = Math.min(songs.size(), 10);
        if (songs.size() < 1) {
            queue.append(" No songs in the queue!");
        } else {
            for (int i = 0; i < loopLimit; i++) {
                Song song = (Song) songs.toArray()[i];
                queue.append("\n").append(i + 1)
                        .append(" - ")
                        .append("[")
                        .append(song.getInfo().title)
                        .append("](" + song.getInfo().uri + ")")
                        .append(" `(Requested by: " + song.getRequester().getUser().getAsTag() + ")`");
            }
            if (songs.size() > 10) {
                queue.append("\n");
                queue.append("\n**+ " + (songs.size() - 10) + " more songs!**");
            }


        }

        embedBuilder.setDescription(queue);
        return embedBuilder.build();
    }
}
