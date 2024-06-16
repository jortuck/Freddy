package net.driedsponge;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

import java.awt.*;
import java.sql.Timestamp;

public final class Embeds {
    private Embeds(){
        throw new AssertionError();
    }

    public static EmbedBuilder error(String title, String description){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle(title);
        embedBuilder.setDescription(description);
        embedBuilder.setColor(Color.RED);
        embedBuilder.setFooter("Time Stamp: "+new Timestamp(System.currentTimeMillis()).toString());
        return embedBuilder;
    }
}
