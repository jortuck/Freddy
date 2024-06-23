package net.driedsponge.commands.util;

import net.driedsponge.Main;
import net.driedsponge.commands.SlashCommand;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;

public final class Bug extends SlashCommand {
    private static final Logger logger = LoggerFactory.getLogger(Bug.class);

    public Bug() {
        super("bug");
    }

    @Override
    public void execute(SlashCommandInteractionEvent event){
        EmbedBuilder embedBuilder = getEmbedBuilder(event);
        logger.warn("Bug Report From {} ({}) in {} ({}): {}",
                event.getUser().getName(),
                event.getUser().getId(),
                event.getGuild().getName(),
                event.getGuild().getId(),
                event.getOptions().get(0).getAsString());
        event.getJDA().retrieveUserById(Main.OWNER_ID).queue(owner ->{
            owner.openPrivateChannel().queue(response ->{
                response.sendMessageEmbeds(embedBuilder.build()).queue();
            });
        });


        event.reply("Your bug report has been sent, thank you! " +
                "**If you would like to provide more information, please submit an " +
                        "[issue on GitHub]" +
                        "(<https://github.com/jortuck/Freddy/issues/new/choose>).**")
                .setEphemeral(true).queue();

    }

    private static @NotNull EmbedBuilder getEmbedBuilder(SlashCommandInteractionEvent event) {
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("New Bug Report");
        embedBuilder.addField("Sender", event.getUser().getName()+" ("+ event.getUser().getId()+")", true);
        embedBuilder.addField("Guild", event.getGuild().getName()+" ("+ event.getGuild().getId()+")", true);
        embedBuilder.setDescription(event.getOptions().get(0).getAsString());
        embedBuilder.setAuthor(event.getUser().getName(), event.getUser().getAvatarUrl());
        return embedBuilder;
    }
}
