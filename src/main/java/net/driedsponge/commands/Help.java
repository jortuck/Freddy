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

        event.getJDA().retrieveCommands().queue(response ->{
            response.forEach(command -> {

                System.out.println(command.getName());
            });
        });
        event.reply("Help will work soon").queue();
    }

}
