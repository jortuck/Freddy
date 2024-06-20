package net.driedsponge;

import net.driedsponge.commands.music.Queue;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.sql.Timestamp;
import java.time.Duration;

public final class Embeds {
    private Embeds(){
        throw new AssertionError();
    }

    public static EmbedBuilder error(String title, String description){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("An Error Occurred Performing That Operation");
        embedBuilder.setDescription("### "+title);
        embedBuilder.appendDescription("\n *Below is the error message. If the message below does" +
                "not make any sense, please report it using the `/bug` command!*");
        embedBuilder.appendDescription("\n```\n"+description+"\n```");
        embedBuilder.setColor(Color.RED);
        embedBuilder.setFooter("Time Stamp: "+new Timestamp(System.currentTimeMillis()).toString());
        return embedBuilder;
    }

    /**
     * Generates an embed perfect for sharing songs.
     * @param title The title of the card.
     * @param song The song.
     * @return A build for the song embed.
     */
    public static EmbedBuilder songCard(String title, QueuedSong song) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setAuthor(title);
        embedBuilder.setTitle(song.getInfo().title, song.getInfo().uri);
        embedBuilder.setThumbnail(song.getInfo().artworkUrl);
        embedBuilder.addField("Artist", song.getInfo().author, true);
        embedBuilder.addField("Length", duration(song.getTrack().getDuration()), true);
        embedBuilder.setColor(Main.PRIMARY_COLOR);
        embedBuilder.setFooter("Requested by " + song.getRequester().getUser().getName(),
                song.getRequester().getEffectiveAvatarUrl());
        if(song.getInfo().artworkUrl != null){
            embedBuilder.setThumbnail(song.getInfo().artworkUrl);
        }
        return embedBuilder;
    }

    /**
     * Convert milliseconds to MM:SS/np
     * @param milliseconds
     * @return
     */
    public static String duration(long milliseconds){
        // Define the number of milliseconds
        Duration duration = Duration.ofMillis(milliseconds);
        long minutes = duration.toMinutes();
        long seconds = duration.getSeconds() % 60;
        String timeString = String.format("%02d:%02d", minutes, seconds);
        return timeString;
    }

    public static EmbedBuilder playlistEmbed(String name, int totalTracks, String image, String url, User user){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Added " + totalTracks + " songs to the Queue from " + name + "!");
        embedBuilder.setColor(Main.PRIMARY_COLOR);
        embedBuilder.setFooter("Requested by " + user.getName(), user.getEffectiveAvatarUrl());
        embedBuilder.setThumbnail(image);
        return embedBuilder;
    }
}
