package net.driedsponge.commands.util;

import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;

import java.awt.*;
import java.util.List;

public final class Help extends SlashCommand {
    public Help() {
        super("help");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        event.replyEmbeds(helpEmbed(event.getJDA()).build()).setEphemeral(true).queue();
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
        builder.append("Fun fact: If you want to restrict commands to certain roles or channels, go to `Server Settings` > `Integrations` > `").append(jda.getSelfUser().getName()).append("` > `Manage`!")
                .append("" + "\n");
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
