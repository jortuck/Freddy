package net.driedsponge.commands;

import io.sentry.Sentry;
import io.sentry.UserFeedback;
import net.driedsponge.Main;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.time.temporal.TemporalAccessor;

public class Bug extends GuildCommand {

    public Bug() {
        super("bug");
    }

    @Override
    public void execute(SlashCommandEvent event){
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle("New Bug Report");
        embedBuilder.addField("Sender",event.getUser().getAsTag()+" ("+event.getUser().getId()+")", true);
        embedBuilder.addField("Guild",event.getGuild().getName()+" ("+event.getGuild().getId()+")", true);
        embedBuilder.setDescription(event.getOptions().get(0).getAsString());
        embedBuilder.setAuthor(event.getUser().getAsTag(),event.getUser().getAvatarUrl());

        event.getJDA().retrieveUserById(Main.OWNER_ID).queue(owner ->{
            owner.openPrivateChannel().queue(response ->{
                response.sendMessageEmbeds(embedBuilder.build()).queue();
            });
        });


        event.reply("Your bug report has been sent, thank you!").setEphemeral(true).queue();
    }
}
