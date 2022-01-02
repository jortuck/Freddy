package net.driedsponge.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
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
        event.replyEmbeds(helpEmbed(event.getJDA()).build()).queue();
    }

    /**
     * Returns an embed builder for a nice help embed.
     * @param jda The JDA object necessary for titles.
     * @return The embed builder.
     */
    public static EmbedBuilder helpEmbed(JDA jda) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.ORANGE);
        embedBuilder.setTitle(jda.getSelfUser().getName()+" Help");
        StringBuilder builder = new StringBuilder();
        List<Command> response = jda.retrieveCommands().complete();

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

        return embedBuilder;
    }
}
