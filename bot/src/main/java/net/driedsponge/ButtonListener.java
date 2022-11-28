package net.driedsponge;

import net.driedsponge.commands.Owner;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.annotation.Nonnull;

public class ButtonListener extends ListenerAdapter {
    @Override
    public void onButtonClick(@Nonnull ButtonClickEvent event) {
        if(event.getButton().getId().equals("guildlist")) {
            event.replyEmbeds(Owner.guildList(event.getJDA()).build())
                    .addActionRow(Owner.CALL_LIST_BUTTON)
                    .queue();
        }else if(event.getButton().getId().equals("entertaining")){
            event.replyEmbeds(Owner.callList(event.getJDA()).build()).queue();
        }
    }
}
