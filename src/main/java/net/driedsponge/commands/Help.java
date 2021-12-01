package net.driedsponge.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.requests.RestAction;

import java.awt.*;
import java.util.List;

public class Help extends GuildCommand {
    public Help() {
        super("help");
    }

    @Override
    public void execute(SlashCommandEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setTitle(event.getJDA().getSelfUser().getName()+" Help");

        StringBuilder builder = new StringBuilder();

        event.getJDA().retrieveCommands().queue(response ->{
            response.forEach(command -> {
                  builder.append("\n").append("`/").append(command.getName());
                  command.getOptions().forEach(o ->{
                      builder.append(" ").append("["+o.getName()+"]");
                  });
                  builder.append("`");
                  builder.append(" - ");
                  builder.append("**").append(command.getDescription()).append("**");
                builder.append("\n");
            });
            embedBuilder.setDescription(builder.toString());
            event.replyEmbeds(embedBuilder.build()).queue();
        });

    }

}
