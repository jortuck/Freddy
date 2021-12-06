package net.driedsponge.commands;

import com.sedmelluq.discord.lavaplayer.track.AudioTrackInfo;
import net.driedsponge.YouTubeSong;
import net.driedsponge.VoiceController;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;

import java.awt.*;
import java.util.concurrent.BlockingQueue;

public class Queue extends GuildCommand {


    public Queue() {
        super("queue");
    }

    @Override
    public void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        if (event.getGuild().getAudioManager().isConnected() && Play.PLAYERS.get(event.getGuild()) != null) {
            VoiceController vc = Play.PLAYERS.get(event.getGuild());
            AudioTrackInfo np = vc.getNowPlaying().getInfo();
            BlockingQueue<YouTubeSong> songs = vc.getTrackScheduler().getQueue();
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
                    YouTubeSong song = (YouTubeSong) songs.toArray()[i];
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
            MessageEmbed embed = embedBuilder.build();
            event.getHook().sendMessageEmbeds(embed)
                    .queue();
        } else {
            embedBuilder.setTitle("Nothing is playing.");
            event.getHook().sendMessageEmbeds(embedBuilder.build()).queue();
        }
    }
}
