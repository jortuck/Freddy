package net.driedsponge;

import net.driedsponge.buttons.Entertaining;
import net.driedsponge.commands.util.Help;
import net.driedsponge.commands.util.Owner;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onMessageReceived(MessageReceivedEvent event){
        if(event.getAuthor().isBot()) return;
        if(event.getMessage().getMentions().isMentioned(event.getJDA().getSelfUser(), Message.MentionType.USER)){
            event.getMessage().replyEmbeds(Help.helpEmbed(event.getJDA()).build()).queue();
        }else{
            if(!event.getAuthor().getId().equals(Main.OWNER_ID)) return;
            if(event.getMessage().getContentRaw().startsWith("!statistics")){
                Owner.statistics(event);
            }else if(event.getMessage().getContentRaw().startsWith("!guildlist")){
                EmbedBuilder embedBuilder = Owner.guildList(event.getJDA());
                event.getMessage().replyEmbeds(embedBuilder.build())
                        .setActionRow(Entertaining.ENTERTAINING_BUTTON)
                        .queue();
            }else if(event.getMessage().getContentRaw().startsWith("!entertaining")){
                EmbedBuilder embedBuilder = Owner.callList(event.getJDA());
                event.getMessage().replyEmbeds(embedBuilder.build()).queue();
            }
        }

    }
}
