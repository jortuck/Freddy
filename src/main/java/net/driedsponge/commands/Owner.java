package net.driedsponge.commands;

import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.driedsponge.buttons.Entertaining;
import net.driedsponge.buttons.GuildList;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;

public class Owner {


    public static EmbedBuilder callList(JDA jda){
        if(PlayerStore.size() == 0){
            return new EmbedBuilder().setColor(Color.RED).setTitle("Not playing any music anywhere!");
        }
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Listeners");
        StringBuilder builder = new StringBuilder();
        builder.append("```");
        PlayerStore.getControllers().values().forEach(vc -> {
            builder.append("\n ").append(vc.getGuild().getName()).append(" (").append(vc.getGuild().getId()).append(")")
                    .append(" - ").append(vc.getNowPlaying().getInfo().title)
            ;
        });
        builder.append("\n```");
        embedBuilder.setDescription(builder.toString());
        return embedBuilder;
    }

    public static EmbedBuilder guildList(JDA jda){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Guilds");
        StringBuilder builder = new StringBuilder();
        builder.append("```");
        jda.getGuilds().forEach(guild -> {
            builder.append("\n ").append(guild.getName()).append(" (").append(guild.getId()).append(")");
        });
        builder.append("\n```");
        embedBuilder.setDescription(builder.toString());
        return embedBuilder;
    }

    public static EmbedBuilder statistics(MessageReceivedEvent event){
        JDA jda = event.getJDA();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Statistics");
        embedBuilder.addField("Guilds",String.valueOf(jda.getGuilds().size()),true);
        embedBuilder.addField("Currently Entertaining",String.valueOf(PlayerStore.size()),true);
        event.getMessage().replyEmbeds(embedBuilder.build())
                .setActionRow(Entertaining.ENTERTAINING_BUTTON,GuildList.GUILD_LIST_BUTTON)
                .queue();
        return embedBuilder;
    }


}
