package net.driedsponge;

import net.driedsponge.commands.Help;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        if(event.getMessage().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)){
            event.getMessage().replyEmbeds(Help.helpEmbed(event.getJDA()).build()).queue();
        }
    }
}
