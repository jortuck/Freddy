package net.driedsponge.commands.util;

import net.driedsponge.Main;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.awt.*;

public class Bug extends SlashCommand {

    public Bug() {
        super("bug");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("New Bug Report");
        embedBuilder.addField("Sender",event.getUser().getName()+" ("+event.getUser().getId()+")", true);
        embedBuilder.addField("Guild",event.getGuild().getName()+" ("+event.getGuild().getId()+")", true);
        embedBuilder.setDescription(event.getOptions().get(0).getAsString());
        embedBuilder.setAuthor(event.getUser().getName(),event.getUser().getAvatarUrl());

        event.getJDA().retrieveUserById(Main.OWNER_ID).queue(owner ->{
            owner.openPrivateChannel().queue(response ->{
                response.sendMessageEmbeds(embedBuilder.build()).queue();
            });
        });


        event.reply("Your bug report has been sent, thank you!").setEphemeral(true).queue();
    }
}
