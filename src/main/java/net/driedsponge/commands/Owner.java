package net.driedsponge.commands;

import net.driedsponge.Main;
import net.driedsponge.PlayerStore;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class Owner extends ListenerAdapter {
    public static final Button CALL_LIST_BUTTON = Button.primary("entertaining","Current Calls");

    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(!event.getAuthor().getId().equals(Main.OWNER_ID)) return;
        if(event.getMessage().getContentRaw().startsWith("!statistics")){
            statistics(event);
        }else if(event.getMessage().getContentRaw().startsWith("!guildlist")){
            EmbedBuilder embedBuilder = guildList(event.getJDA());
            event.getMessage().replyEmbeds(embedBuilder.build())
                    .setActionRow(CALL_LIST_BUTTON)
                    .queue();
        }else if(event.getMessage().getContentRaw().startsWith("!entertaining")){
            EmbedBuilder embedBuilder = callList(event.getJDA());
            event.getMessage().replyEmbeds(embedBuilder.build()).queue();
        }
    }

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

    private void statistics(MessageReceivedEvent event){
        JDA jda = event.getJDA();
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.CYAN);
        embedBuilder.setTitle(jda.getSelfUser().getName() + " Statistics");
        embedBuilder.addField("Guilds",String.valueOf(jda.getGuilds().size()),true);
        embedBuilder.addField("Currently Entertaining",String.valueOf(PlayerStore.size()),true);
        event.getMessage().replyEmbeds(embedBuilder.build())
                .setActionRow(Button.primary("guildlist","Guild List"),CALL_LIST_BUTTON)
                .queue();
    }


}
